package br.com.drinkup.servlet;

import br.com.drinkup.dao.ProdutoDao;
import br.com.drinkup.model.Produto;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/produtos")
public class ListaProdutosJsonServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Produto> produtos = new ProdutoDao().listarProdutos();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < produtos.size(); i++) {
                Produto produto = produtos.get(i);
                if (i > 0) {
                    json.append(',');
                }
                json.append("{\"id\":").append(produto.getIdProduto())
                        .append(",\"idCategoria\":").append(produto.getIdCategoria())
                        .append(",\"nome\":\"").append(escapeJson(produto.getNomeDoProduto())).append('"')
                        .append(",\"codigoDeBarras\":\"").append(escapeJson(produto.getCodigoDeBarras())).append('"')
                        .append(",\"descricao\":\"").append(escapeJson(produto.getDescricao())).append('"')
                        .append(",\"preco\":").append(produto.getPreco())
                        .append(",\"quantidadeEstoque\":").append(produto.getQuantidadeEstoque())
                        .append(",\"volumeML\":").append(produto.getVolumeML())
                        .append('}');
            }
            response.getWriter().write(json.append(']').toString());
        } catch (Exception e) {
            throw new ServletException("Erro ao listar produtos.", e);
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
