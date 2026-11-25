
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
            int idEstoque = Integer.parseInt(
                    request.getParameter("idEstoque")
            );

            if ("renomear".equals(acao)) {

                dao.renomearEstoque(
                        idEstoque,
                        request.getParameter("nome")
                );

            } else if ("remover".equals(acao)) {

                int idProdutoEstoque = Integer.parseInt(
                        request.getParameter("idProdutoEstoque")
                );

                dao.removerProduto(idEstoque, idProdutoEstoque);

            } else {

                UtilidadesJSON.erro(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Ação inválida."
                );
                return;
            }

            UtilidadesJSON.enviar(
                    response,
                    HttpServletResponse.SC_OK,
                    "{\"ok\":true}"
            );

        } catch (NumberFormatException | NullPointerException e) {

            UtilidadesJSON.erro(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Dados inválidos."
            );

        } catch (IllegalArgumentException e) {

            UtilidadesJSON.erro(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (SQLException e) {

            e.printStackTrace();

            UtilidadesJSON.erro(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro ao atualizar o estoque."
            );
        }
    }
}
