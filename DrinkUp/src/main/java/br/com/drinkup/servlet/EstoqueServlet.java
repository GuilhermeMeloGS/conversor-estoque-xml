package br.com.drinkup.servlet;

import br.com.drinkup.dao.EstoqueDao;
import br.com.drinkup.utilidades.UtilidadesJSON;
import br.com.drinkup.utilidades.UtilidadesSessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/estoques/dados")
public class EstoqueServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (UtilidadesSessao.exigirLogin(request, response, true) == null) {
            return;
        }

        try {
            EstoqueDao dao = new EstoqueDao();

            StringBuilder json = new StringBuilder("[");

            // Estoque principal
            json.append("{\"tipo\":\"PRINCIPAL\"")
                    .append(",\"nome\":\"Estoque principal\"")
                    .append(",\"quantidadeProdutos\":").append(dao.contarProdutosEstoquePrincipal())
                    .append(",\"quantidadeTotal\":").append(dao.contarUnidadesEstoquePrincipal())
                    .append("}");

            // Estoques importados por XML
            List<Map<String, Object>> importados = dao.listarEstoquesImportados();
            for (Map<String, Object> e : importados) {
                json.append(",{\"tipo\":\"IMPORTADO\"")
                        .append(",\"id\":").append(e.get("id"))
                        .append(",\"nome\":\"")
                        .append(UtilidadesJSON.escapeJson((String) e.get("nome"))).append("\"")
                        .append(",\"quantidadeProdutos\":").append(e.get("quantidadeProdutos"))
                        .append(",\"quantidadeTotal\":").append(e.get("quantidadeTotal"))
                        .append("}");
            }

            json.append("]");

            UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK, json.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesJSON.erro(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro ao consultar os estoques.");
        }
    }
}