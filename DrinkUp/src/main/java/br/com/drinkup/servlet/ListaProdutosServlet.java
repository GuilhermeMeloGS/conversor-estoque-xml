package br.com.drinkup.servlet;

import br.com.drinkup.dao.ProdutoDao;
import br.com.drinkup.model.Produto;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/produtos")
public class ListaProdutosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");

        try {
            List<Produto> produtos = new ProdutoDao().listarProdutos();
            PrintWriter out = response.getWriter();
            out.println("<h1>Bebidas cadastradas</h1>");
            out.println("<a href='cadastroBebida.html'>Cadastrar novo produto</a><br><br>");
            out.println("<table border='1' cellpadding='6'>");
            out.println("<tr><th>ID</th><th>Nome</th><th>Código</th><th>Categoria</th><th>Preço</th><th>Estoque</th><th>Volume (ml)</th><th>Ações</th></tr>");

            if (produtos != null) {
                for (Produto p : produtos) {
                    out.println("<tr>");
                    out.println("<td>" + p.getIdProduto() + "</td>");
                    out.println("<td>" + p.getNomeDoProduto() + "</td>");
                    out.println("<td>" + p.getCodigoDeBarras() + "</td>");
                    out.println("<td>" + p.getIdCategoria() + "</td>");
                    out.println("<td>" + p.getPreco() + "</td>");
                    out.println("<td>" + p.getQuantidadeEstoque() + "</td>");
                    out.println("<td>" + p.getVolumeML() + "</td>");

                    out.println("<td>");

                    out.println("<form action='delete-produto' method='post' style='display:inline;'>");
                    out.println("<input type='hidden' name='id' value='" + p.getIdProduto() + "'>");
                    out.println("<button type='submit'>Delete</button>");
                    out.println("</form>");

                    out.println("<span> | </span>");

                    out.println("<a href='cadastroBebida.html?id=" + p.getIdProduto()
                            + "&idCategoria=" + p.getIdCategoria()
                            + "&nome=" + p.getNomeDoProduto()
                            + "&codigo=" + p.getCodigoDeBarras()
                            + "&descricao=" + p.getDescricao()
                            + "&preco=" + p.getPreco()
                            + "&estoque=" + p.getQuantidadeEstoque()
                            + "&volume=" + p.getVolumeML() + "'>Update</a>");

                    out.println("</td>");
                    out.println("</tr>");
                }
            }
            out.println("</table>");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}