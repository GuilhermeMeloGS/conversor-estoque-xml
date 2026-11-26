package br.com.drinkup.servlet;

import br.com.drinkup.dao.EstoqueDao;
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

@WebServlet("/produtos/cadastrar")
@MultipartConfig(maxFileSize = UtilidadesImagem.TAMANHO_MAXIMO,
        maxRequestSize = UtilidadesImagem.TAMANHO_MAXIMO + 512 * 1024)
public class CadastraProdutoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (UtilidadesSessao.exigirLogin(request, response, true) == null) return;

        request.setCharacterEncoding("UTF-8");

        Produto produto = new Produto();
        int quantidadeInformada;

        try {
            produto.setIdCategoria(Integer.parseInt(request.getParameter("idCategoria")));
            produto.setNomeDoProduto(request.getParameter("nome"));
            produto.setCodigoDeBarras(request.getParameter("codigoDeBarras"));
            produto.setDescricao(request.getParameter("descricao"));
            produto.setPreco(Double.parseDouble(request.getParameter("preco")));

            // A quantidade digitada pode pertencer ao estoque principal ou a um estoque importado.
            quantidadeInformada = Integer.parseInt(request.getParameter("quantidadeEstoque"));
            produto.setQuantidadeEstoque(quantidadeInformada);
            produto.setVolumeML(Integer.parseInt(request.getParameter("volumeML")));
        } catch (NumberFormatException | NullPointerException e) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Preencha preço, quantidade e volume com números válidos "
                            + "(quantidade e volume até 2.147.483.647).");
            return;
        }

        if (produto.getPreco() < 0 || produto.getPreco() >= 100_000_000
                || quantidadeInformada < 0 || produto.getVolumeML() < 0) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Preço, quantidade e volume devem ser positivos; o preço deve ser menor que R$ 100.000.000.");
            return;
        }

        try {
            produto.setImagem(UtilidadesImagem.lerUpload(request, "imagem"));
        } catch (IllegalArgumentException e) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        String idEstoqueDestinoParam = request.getParameter("idEstoqueDestino");
        boolean cadastroEmEstoqueImportado = idEstoqueDestinoParam != null
                && !idEstoqueDestinoParam.trim().isEmpty();

        try {
            ProdutoDao produtoDao = new ProdutoDao();

            if (cadastroEmEstoqueImportado) {
                int idEstoqueDestino = Integer.parseInt(idEstoqueDestinoParam);

                // O produto entra no catálogo principal com quantidade zero.
                // A quantidade informada fica exclusivamente no estoque importado.
                produto.setQuantidadeEstoque(0);
                int idProduto = produtoDao.criarProdutoEObterId(produto);

                new EstoqueDao().associarProdutoAoEstoque(
                        idEstoqueDestino, idProduto, quantidadeInformada);

                response.sendRedirect(request.getContextPath()
                        + "/detalheEstoque.html?id=" + idEstoqueDestino);
            } else {
                produtoDao.criarProduto(produto);
                response.sendRedirect(request.getContextPath() + "/produtos");
            }

        } catch (IllegalArgumentException e) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (java.sql.SQLException e) {
            log("Erro ao cadastrar produto", e);
            UtilidadesJSON.erro(response, HttpServletResponse.SC_CONFLICT,
                    "Não foi possível salvar o produto. Verifique se o código de barras já existe.");
        }
    }
}
