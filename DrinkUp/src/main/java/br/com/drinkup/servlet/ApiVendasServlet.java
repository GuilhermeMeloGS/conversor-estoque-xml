package br.com.drinkup.servlet;

import br.com.drinkup.dao.VendaDao;
import br.com.drinkup.model.StatusVenda;
import br.com.drinkup.model.Usuario;
import br.com.drinkup.model.Venda;
import br.com.drinkup.utilidades.UtilidadesJSON;
import br.com.drinkup.utilidades.UtilidadesSessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *   GET  /api/vendas            (admin)   todas as vendas com seus itens
 *   GET  /api/minhasVendas      (logado)  vendas do usuário logado
 *   POST /api/vendas/finalizar  (logado)  itens=idProduto:qtd,idProduto:qtd
 *   POST /api/vendas/status     (admin)   id=N&status=Pendente|Concluido|Cancelado
 */
@WebServlet({"/api/vendas", "/api/minhasVendas", "/api/vendas/finalizar", "/api/vendas/status"})
public class ApiVendasServlet extends HttpServlet {

    private static final int MAX_QUANTIDADE_POR_ITEM = 1000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean todas = "/api/vendas".equals(request.getServletPath());
        Usuario logado = UtilidadesSessao.exigirLogin(request, response, todas);
        if (logado == null) return;

        try {
            VendaDao dao = new VendaDao();
            List<Venda> vendas = todas ? dao.listarTodas() : dao.listarPorUsuario(logado.getId());

            UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK,
                    vendas.stream().map(UtilidadesJSON::vendaJson)
                            .collect(Collectors.joining(",", "[", "]")));

        } catch (Exception e) {
            log("Erro ao listar vendas", e);
            UtilidadesJSON.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao buscar pedidos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if ("/api/vendas/finalizar".equals(request.getServletPath())) {
            finalizar(request, response);
        } else if ("/api/vendas/status".equals(request.getServletPath())) {
            alterarStatus(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void finalizar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Usuario logado = UtilidadesSessao.exigirLogin(request, response, false);
        if (logado == null) return;

        Map<Integer, Integer> itens = lerItens(request.getParameter("itens"));
        if (itens == null || itens.isEmpty()) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, "Carrinho inválido");
            return;
        }

        try {
            int idVenda = new VendaDao().criarVenda(logado.getId(), itens);
            UtilidadesJSON.enviar(response, HttpServletResponse.SC_CREATED, "{\"idVenda\":" + idVenda + "}");

        } catch (IllegalArgumentException e) {
            // Regra de negócio (ex.: estoque insuficiente): a mensagem é segura para o cliente.
            UtilidadesJSON.erro(response, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            log("Erro ao finalizar compra", e);
            UtilidadesJSON.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao registrar o pedido");
        }
    }

    private void alterarStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (UtilidadesSessao.exigirLogin(request, response, true) == null) return;

        int id;
        StatusVenda status;
        try {
            id = Integer.parseInt(request.getParameter("id"));
            status = StatusVenda.valueOf(request.getParameter("status"));
        } catch (IllegalArgumentException | NullPointerException e) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, "ID ou status inválido");
            return;
        }

        try {
            if (!new VendaDao().atualizarStatus(id, status)) {
                UtilidadesJSON.erro(response, HttpServletResponse.SC_CONFLICT,
                        "Pedido não encontrado ou já cancelado");
                return;
            }
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (Exception e) {
            log("Erro ao atualizar status da venda", e);
            UtilidadesJSON.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao atualizar o pedido");
        }
    }

    /** Converte "1:2,5:1" em {1=2, 5=1}; devolve null se o formato for inválido. */
    private Map<Integer, Integer> lerItens(String texto) {
        if (texto == null || texto.isBlank()) return null;

        Map<Integer, Integer> itens = new TreeMap<>();
        try {
            for (String par : texto.split(",")) {
                String[] partes = par.trim().split(":");
                if (partes.length != 2) return null;

                int idProduto = Integer.parseInt(partes[0].trim());
                int quantidade = Integer.parseInt(partes[1].trim());
                if (quantidade < 1 || quantidade > MAX_QUANTIDADE_POR_ITEM) return null;

                itens.merge(idProduto, quantidade, Integer::sum);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return itens;
    }
}
