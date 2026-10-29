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

@WebServlet("/atualiza-produto")
@MultipartConfig
public class AtualizaProdutoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id");

        Produto produto = new Produto();
        produto.setIdProduto(Integer.parseInt(idStr));
        produto.setIdCategoria(Integer.parseInt(request.getParameter("idCategoria")));
        produto.setNomeDoProduto(request.getParameter("nome"));
        produto.setCodigoDeBarras(request.getParameter("codigoDeBarras"));
        produto.setDescricao(request.getParameter("descricao"));
        produto.setPreco(Double.parseDouble(request.getParameter("preco")));
        produto.setQuantidadeEstoque(Integer.parseInt(request.getParameter("quantidadeEstoque")));
        produto.setVolumeML(Integer.parseInt(request.getParameter("volumeML")));

        try {
            new ProdutoDao().atualizarProduto(produto);
        } catch (Exception e) {
            throw new ServletException(e);
        }

        response.sendRedirect("produtos");
    }
}