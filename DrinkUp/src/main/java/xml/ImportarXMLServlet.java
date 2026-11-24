
package xml;

import br.com.drinkup.model.Produto;
import br.com.drinkup.model.TipoUsuario;
import br.com.drinkup.model.Usuario;
import br.com.drinkup.utilidades.UtilidadesSessao;
import xml.service.EstoqueXMLService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/estoque/xml/importar")
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class ImportarXMLServlet extends HttpServlet {

    private EstoqueXMLService estoqueXMLService;

    @Override
    public void init() throws ServletException {
        estoqueXMLService = new EstoqueXMLService();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuarioLogado = UtilidadesSessao.getUsuarioLogado(request);
        if (usuarioLogado == null || usuarioLogado.getTipo() != TipoUsuario.Admin) {
            response.sendRedirect(request.getContextPath() + "/paginaLogin.html");
            return;
        }

        HttpSession sessao = request.getSession();

        // Remove uma importação anterior e mensagens antigas.
        sessao.removeAttribute("produtosImportacaoXML");
        sessao.removeAttribute("erroImportacao");

        try {

            Part arquivo = request.getPart("arquivoXML");

            if (arquivo == null || arquivo.getSize() == 0) {
                throw new IllegalArgumentException(
                        "Selecione um arquivo XML."
                );
            }

            String contentDisposition =
                    arquivo.getHeader("content-disposition");

            if (contentDisposition == null) {
                throw new IllegalArgumentException(
                        "Não foi possível identificar o arquivo."
                );
            }

            String nomeArquivo = contentDisposition.replaceAll(
                    ".*filename=\"([^\"]*)\".*", "$1"
            );

            nomeArquivo = nomeArquivo.replace("\\", "/");

            nomeArquivo = nomeArquivo.substring(
                    nomeArquivo.lastIndexOf("/") + 1
            );

            if (nomeArquivo.isEmpty() ||
                    !nomeArquivo.toLowerCase().endsWith(".xml")) {

                throw new IllegalArgumentException(
                        "O arquivo selecionado deve possuir extensão XML."
                );
            }

            List<Produto> produtos;

            try (InputStream entrada = arquivo.getInputStream()) {
                produtos = estoqueXMLService.importarEstoque(entrada);
            }

            if (produtos == null || produtos.isEmpty()) {
                throw new IllegalArgumentException(
                        "O arquivo XML não possui produtos."
                );
            }

            // Guarda os produtos para a pré-visualização e confirmação.
            sessao.setAttribute(
                    "produtosImportacaoXML",
                    produtos
            );

            // Redireciona para a página HTML.
            response.sendRedirect(
                    request.getContextPath() + "/xml/importados.html"
            );

        } catch (IllegalArgumentException e) {

            sessao.setAttribute(
                    "erroImportacao",
                    e.getMessage()
            );

            response.sendRedirect(
                    request.getContextPath() + "/xml/conversorXML.html?erro="
                            + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8)
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Erro ao importar o arquivo XML.", e
            );
        }
    }
}
