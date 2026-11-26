package br.com.drinkup.servlet;

import br.com.drinkup.dao.EstoqueDao;
import br.com.drinkup.model.Produto;
import br.com.drinkup.utilidades.UtilidadesJSON;
import br.com.drinkup.utilidades.UtilidadesSessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Edição de estoques importados por XML.
 * POST /estoques/editar  com acao = renomear | adicionar | remover | enviarLoja
 */
@WebServlet("/estoques/editar")
public class EditarEstoqueServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (UtilidadesSessao.exigirLogin(request, response, true) == null) {
            return;
        }

        String acao = request.getParameter("acao");
        EstoqueDao dao = new EstoqueDao();

        try {
            int idEstoque = Integer.parseInt(request.getParameter("idEstoque"));

            if ("renomear".equals(acao)) {
                dao.renomearEstoque(idEstoque, request.getParameter("nome"));

            } else if ("adicionar".equals(acao)) {
                Produto p = new Produto();
                p.setIdCategoria(Integer.parseInt(request.getParameter("idCategoria")));
                p.setNomeDoProduto(request.getParameter("nome"));
                p.setCodigoDeBarras(request.getParameter("codigoDeBarras"));
                p.setDescricao(request.getParameter("descricao"));
                p.setPreco(Double.parseDouble(
                        request.getParameter("preco").trim().replace(",", ".")));
                p.setQuantidadeEstoque(Integer.parseInt(request.getParameter("quantidadeEstoque").trim()));
                p.setVolumeML(Integer.parseInt(request.getParameter("volumeML").trim()));

                dao.adicionarProduto(idEstoque, p);

            } else if ("remover".equals(acao)) {
                dao.removerProduto(idEstoque,
                        Integer.parseInt(request.getParameter("idProdutoEstoque")));

            } else if ("enviarLoja".equals(acao)) {
                int[] resultado = dao.enviarEstoqueParaLoja(idEstoque);

                UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK,
                        "{\"ok\":true,\"criados\":" + resultado[0]
                                + ",\"existentes\":" + resultado[1] + "}");
                return;

            } else {
                UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, "Ação inválida.");
                return;
            }

            UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK, "{\"ok\":true}");

        } catch (NumberFormatException | NullPointerException e) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Preencha todos os campos com valores numéricos válidos.");

        } catch (IllegalArgumentException e) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesJSON.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro ao atualizar o estoque.");
        }
    }
}