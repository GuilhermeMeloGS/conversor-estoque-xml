package br.com.drinkup.servlet;

import br.com.drinkup.dao.ProdutoDao;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/produtos/excluir")
public class DeleteProdutoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");

        try {
            if (idStr != null && !idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                new ProdutoDao().deletarProduto(id);
            }
        } catch (Exception e) {
            throw new ServletException("Erro ao excluir produto", e);
        }

        resp.sendRedirect(req.getContextPath() + "/produtos");
    }
}