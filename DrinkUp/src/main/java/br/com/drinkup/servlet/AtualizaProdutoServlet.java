package br.com.drinkup.servlet;

import br.com.drinkup.dao.ProdutoDao;
import br.com.drinkup.model.Produto;
import br.com.drinkup.utilidades.UtilidadesImagem;
import br.com.drinkup.utilidades.UtilidadesJSON;
import br.com.drinkup.utilidades.UtilidadesSessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/produtos/atualizar")
@MultipartConfig(maxFileSize = UtilidadesImagem.TAMANHO_MAXIMO, maxRequestSize = UtilidadesImagem.TAMANHO_MAXIMO + 512 * 1024)
public class AtualizaProdutoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (UtilidadesSessao.exigirLogin(request, response, true) == null) return;

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

        // null = nenhum arquivo novo enviado: a imagem atual do banco é mantida.
        try {
            produto.setImagem(UtilidadesImagem.lerUpload(request, "imagem"));
        } catch (IllegalArgumentException e) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        try {
            new ProdutoDao().atualizarProduto(produto);
        } catch (Exception e) {
            throw new ServletException(e);
        }

        response.sendRedirect(request.getContextPath() + "/produtos");
    }
}