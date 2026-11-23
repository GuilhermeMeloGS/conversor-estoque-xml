package br.com.drinkup.servlet;

import br.com.drinkup.dao.CategoriaDao;
import br.com.drinkup.dao.ProdutoDao;
import br.com.drinkup.model.Categoria;
import br.com.drinkup.model.Produto;
import br.com.drinkup.utilidades.UtilidadesJSON;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Consultas públicas (somente leitura) ao catálogo, em JSON:
 *   GET /api/categorias        -> lista de categorias
 *   GET /api/produtos          -> lista de produtos com a categoria
 *   GET /api/produto?id=N      -> um produto
 */
@WebServlet({"/api/categorias", "/api/produtos", "/api/produto"})
public class ApiCatalogoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String caminho = request.getServletPath();

        try {
            if ("/api/categorias".equals(caminho)) {
                List<Categoria> categorias = new CategoriaDao().listarCategorias();
                UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK,
                        categorias.stream().map(UtilidadesJSON::categoriaJson)
                                .collect(Collectors.joining(",", "[", "]")));

            } else if ("/api/produtos".equals(caminho)) {
                List<Produto> produtos = new ProdutoDao().listarComCategoria();
                UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK,
                        produtos.stream().map(UtilidadesJSON::produtoJson)
                                .collect(Collectors.joining(",", "[", "]")));

            } else {
                int id;
                try {
                    id = Integer.parseInt(request.getParameter("id"));
                } catch (NumberFormatException e) {
                    UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
                    return;
                }

                Produto produto = new ProdutoDao().buscarPorId(id);
                if (produto == null) {
                    UtilidadesJSON.erro(response, HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado");
                    return;
                }
                UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK, UtilidadesJSON.produtoJson(produto));
            }

        } catch (Exception e) {
            log("Erro ao consultar o catálogo", e);
            UtilidadesJSON.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao consultar o catálogo");
        }
    }
}
