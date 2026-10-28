package br.com.drinkup.servlet;

import br.com.drinkup.dao.ProdutoDao;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/delete-produto")
public class DeleteProdutoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        int id;

        try {
            id = Integer.parseInt(idStr);
            if (id <= 0) {
                throw new NumberFormatException("ID inválido");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do produto inválido.");
            return;
        }

        try {
            if (new ProdutoDao().deletarProduto(id) == 0) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado.");
                return;
            }
        } catch (Exception e) {
            throw new ServletException("Erro ao excluir produto.", e);
        }

        if ("application/json".equalsIgnoreCase(req.getHeader("Accept"))) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.sendRedirect("produtos");
        }
    }
}