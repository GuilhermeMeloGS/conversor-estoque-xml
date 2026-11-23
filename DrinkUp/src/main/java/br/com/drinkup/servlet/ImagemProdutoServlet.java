package br.com.drinkup.servlet;

import br.com.drinkup.dao.ProdutoDao;
import br.com.drinkup.model.Produto;
import br.com.drinkup.utilidades.UtilidadesImagem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * GET /imagem-produto?id=N  -> bytes da imagem gravada no banco.
 * Produto sem foto recebe um placeholder "SEM FOTO" (fundo branco); produto inexistente retorna 404.
 */
@WebServlet("/imagem-produto")
public class ImagemProdutoServlet extends HttpServlet {

    private static final byte[] SEM_FOTO_SVG = (
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 400 400\" width=\"400\" height=\"400\">"
            + "<rect width=\"400\" height=\"400\" fill=\"#ffffff\"/>"
            + "<text x=\"200\" y=\"200\" text-anchor=\"middle\" dominant-baseline=\"middle\" "
            + "font-family=\"Arial, Helvetica, sans-serif\" font-size=\"44\" font-weight=\"700\" "
            + "fill=\"#9ca3af\">SEM FOTO</text>"
            + "</svg>").getBytes(StandardCharsets.UTF_8);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            ProdutoDao dao = new ProdutoDao();
            Produto produto = dao.buscarImagem(id);
            String tipo = produto == null ? null : UtilidadesImagem.tipoPorAssinatura(produto.getImagem());
            byte[] bytes;

            if (tipo != null) {
                bytes = produto.getImagem();
            } else if (dao.buscarPorId(id) != null) {
                tipo = "image/svg+xml";
                bytes = SEM_FOTO_SVG;
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String etag = "\"" + Integer.toHexString(Arrays.hashCode(bytes)) + "-" + bytes.length + "\"";

            // no-cache + ETag: o navegador sempre revalida e só baixa de novo se a imagem foi trocada.
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("ETag", etag);
            response.setHeader("X-Content-Type-Options", "nosniff");

            if (etag.equals(request.getHeader("If-None-Match"))) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            response.setContentType(tipo);
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);

        } catch (Exception e) {
            log("Erro ao buscar a imagem do produto", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
