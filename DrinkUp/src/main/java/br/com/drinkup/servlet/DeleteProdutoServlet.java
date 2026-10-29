package br.com.drinkup.servlet;

import br.com.drinkup.dao.ProdutoDao;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/delete-produto")
public class DeleteProdutoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");

        try {
            if (idStr != null && !idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                new ProdutoDao().deletarProduto(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect("produtos");
    }
}