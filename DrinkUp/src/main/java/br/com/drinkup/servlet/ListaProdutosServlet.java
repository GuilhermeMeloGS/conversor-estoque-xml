package br.com.drinkup.servlet;

import br.com.drinkup.dao.ProdutoDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/produtos")
public class ListaProdutosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setAttribute("produtos", new ProdutoDao().listarProdutos());
            request.getRequestDispatcher("/listaProdutos.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}