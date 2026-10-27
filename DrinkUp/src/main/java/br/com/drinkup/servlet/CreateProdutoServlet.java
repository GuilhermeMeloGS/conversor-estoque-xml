package br.com.drinkup.servlet;

import br.com.drinkup.dao.ProdutoDao;
import br.com.drinkup.model.Produto;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/produtos")
@MultipartConfig
public class CreateProdutoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id"); 

        Produto produto = new Produto();
        produto.setIdCategoria(Integer.parseInt(request.getParameter("idCategoria")));
        produto.setNomeDoProduto(request.getParameter("nome"));
        produto.setCodigoDeBarras(request.getParameter("codigoDeBarras"));
        produto.setDescricao(request.getParameter("descricao"));
        produto.setPreco(Double.parseDouble(request.getParameter("preco")));
        produto.setQuantidadeEstoque(Integer.parseInt(request.getParameter("quantidadeEstoque")));
        produto.setVolumeML(Integer.parseInt(request.getParameter("volumeML")));

        try {
            ProdutoDao dao = new ProdutoDao();

            if (idStr == null || idStr.isBlank()) {
                // Se o ID estiver vazio, é um cadastro novo
                dao.criarProduto(produto);
            } else {
                // Se tem ID, é uma atualização
                produto.setIdProduto(Integer.parseInt(idStr));
                dao.atualizarProduto(produto);
            }

        } catch (Exception e) {
            throw new ServletException(e);
        }

        response.sendRedirect("produtos");
    }

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
            // Adicionamos as colunas de ID e Ações no cabeçalho
            out.println("<tr><th>ID</th><th>Nome</th><th>Código</th><th>Categoria</th><th>Preço</th><th>Estoque</th><th>Volume (ml)</th><th>Ações</th></tr>");

            for (Produto p : produtos) {
                out.println("<tr>");
                out.println("<td>" + p.getIdProduto() + "</td>");
                out.println("<td>" + escapar(p.getNomeDoProduto()) + "</td>");
                out.println("<td>" + escapar(p.getCodigoDeBarras()) + "</td>");
                out.println("<td>" + p.getIdCategoria() + "</td>");
                out.println("<td>" + p.getPreco() + "</td>");
                out.println("<td>" + p.getQuantidadeEstoque() + "</td>");
                out.println("<td>" + p.getVolumeML() + "</td>");

                // Coluna (Delete e Update)
                out.println("<td>");

                //  Delete
                out.println("<form action='delete-produto' method='post' style='display:inline;'>");
                out.println("<input type='hidden' name='id' value='" + p.getIdProduto() + "'>");
                out.println("<button type='submit'>Delete</button>");
                out.println("</form>");

                out.println("<span> | </span>");

                out.println("<a href='cadastroBebida.html?id=" + p.getIdProduto()
                        + "&idCategoria=" + p.getIdCategoria()
                        + "&nome=" + escapar(p.getNomeDoProduto())
                        + "&codigo=" + escapar(p.getCodigoDeBarras())
                        + "&descricao=" + escapar(p.getDescricao())
                        + "&preco=" + p.getPreco()
                        + "&estoque=" + p.getQuantidadeEstoque()
                        + "&volume=" + p.getVolumeML() + "'>Update</a>");

                out.println("</td>");

                out.println("</tr>");
            }
            out.println("</table>");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private String escapar(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
