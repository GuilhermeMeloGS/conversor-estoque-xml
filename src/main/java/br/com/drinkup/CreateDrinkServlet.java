package br.com.drinkup;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/cadastrar-nova-bebida")
public class CreateDrinkServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String nome = request.getParameter("nome-produto");
        String codBarrar = request.getParameter("cod-barras");
        String descricao = request.getParameter("descricao-prod");

        request.getRequestDispatcher("index.jsp").forward(request,response);
    }
}
