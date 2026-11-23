package br.com.drinkup.servlet;

import br.com.drinkup.dao.ProdutoDao;
import br.com.drinkup.utilidades.UtilidadesSessao;
import br.com.drinkup.utilidades.UtilidadesJSON;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/produtos/excluir")
public class DeleteProdutoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (UtilidadesSessao.exigirLogin(req, resp, true) == null) return;

        String idStr = req.getParameter("id");

        try {
            if (idStr != null && !idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                new ProdutoDao().deletarProduto(id);
            }
        } catch (NumberFormatException e) {
            UtilidadesJSON.erro(resp, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
            return;
        } catch (SQLException e) {
            // Violação de chave estrangeira: o produto já aparece em itemVenda.
            UtilidadesJSON.erro(resp, HttpServletResponse.SC_CONFLICT,
                    "Produto já vendido, não pode ser excluído");
            return;
        } catch (Exception e) {
            throw new ServletException("Erro ao excluir produto", e);
        }

        resp.sendRedirect(req.getContextPath() + "/produtos");
    }
}