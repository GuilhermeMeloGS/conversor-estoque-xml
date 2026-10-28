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

        Produto produto;
        try {
            produto = new Produto();
            produto.setIdProduto(Integer.parseInt(request.getParameter("id")));
            produto.setIdCategoria(Integer.parseInt(request.getParameter("idCategoria")));
            produto.setNomeDoProduto(request.getParameter("nome"));
            produto.setCodigoDeBarras(request.getParameter("codigoDeBarras"));
            produto.setDescricao(request.getParameter("descricao"));
            produto.setPreco(Double.parseDouble(request.getParameter("preco")));
            produto.setQuantidadeEstoque(Integer.parseInt(request.getParameter("quantidadeEstoque")));
            produto.setVolumeML(Integer.parseInt(request.getParameter("volumeML")));
            validarProduto(produto);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dados do produto inválidos.");
            return;
        }

        try {
            if (new ProdutoDao().atualizarProduto(produto) == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado.");
                return;
            }
        } catch (Exception e) {
            throw new ServletException("Erro ao atualizar produto.", e);
        }

        if ("application/json".equalsIgnoreCase(request.getHeader("Accept"))) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            response.sendRedirect("produtos");
        }
    }

    private void validarProduto(Produto produto) {
        if (produto.getIdProduto() <= 0 || produto.getIdCategoria() <= 0 || produto.getNomeDoProduto() == null
                || produto.getNomeDoProduto().trim().isEmpty() || produto.getCodigoDeBarras() == null
                || produto.getCodigoDeBarras().trim().isEmpty() || produto.getPreco() < 0
                || produto.getQuantidadeEstoque() < 0 || produto.getVolumeML() <= 0) {
            throw new IllegalArgumentException("Dados do produto inválidos.");
        }
    }
}