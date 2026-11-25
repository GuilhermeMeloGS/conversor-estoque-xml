package br.com.drinkup.servlet;

import br.com.drinkup.dao.CategoriaDao;
import br.com.drinkup.dao.EstoqueDao;
import br.com.drinkup.dao.ProdutoDao;
import br.com.drinkup.model.Categoria;
import br.com.drinkup.model.Produto;
import br.com.drinkup.utilidades.UtilidadesJSON;
import br.com.drinkup.utilidades.UtilidadesSessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/estoques/produtos")
public class ProdutosEstoqueServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (UtilidadesSessao.exigirLogin(request, response, true) == null) {
            return;
        }

        try {
            boolean principal = "principal".equals(request.getParameter("tipo"));
            String nome;
            List<Produto> produtos;

            if (principal) {
                nome = "Estoque principal";
                produtos = new ProdutoDao().listarComCategoria();
            } else {
                int id;
                try {
                    id = Integer.parseInt(request.getParameter("id"));
                } catch (NumberFormatException e) {
                    UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, "Id inválido.");
                    return;
                }

                EstoqueDao dao = new EstoqueDao();
                nome = dao.buscarNomeEstoqueImportado(id);
                if (nome == null) {
                    UtilidadesJSON.erro(response, HttpServletResponse.SC_NOT_FOUND,
                            "Estoque não encontrado.");
                    return;
                }
                produtos = dao.listarProdutosDoEstoque(id);
            }

            StringBuilder json = new StringBuilder("{\"nome\":\"")
                    .append(UtilidadesJSON.escapeJson(nome))
                    .append("\",\"editavel\":").append(!principal)
                    .append(",\"produtos\":[");

            for (int i = 0; i < produtos.size(); i++) {
                Produto p = produtos.get(i);
                if (i > 0) json.append(",");
                json.append("{\"id\":").append(p.getIdProduto())
                        .append(",\"categoria\":\"")
                        .append(UtilidadesJSON.escapeJson(p.getNomeCategoria())).append("\"")
                        .append(",\"nome\":\"")
                        .append(UtilidadesJSON.escapeJson(p.getNomeDoProduto())).append("\"")
                        .append(",\"codigoDeBarras\":\"")
                        .append(UtilidadesJSON.escapeJson(p.getCodigoDeBarras())).append("\"")
                        .append(",\"preco\":").append(BigDecimal.valueOf(p.getPreco()).toPlainString())
                        .append(",\"quantidadeEstoque\":").append(p.getQuantidadeEstoque())
                        .append(",\"volumeML\":").append(p.getVolumeML())
                        .append("}");
            }

            json.append("],\"categorias\":[");

            List<Categoria> categorias = new CategoriaDao().listarCategorias();
            for (int i = 0; i < categorias.size(); i++) {
                if (i > 0) json.append(",");
                json.append(UtilidadesJSON.categoriaJson(categorias.get(i)));
            }

            json.append("]}");

            UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK, json.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesJSON.erro(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro ao consultar os produtos do estoque.");
        }
    }
}