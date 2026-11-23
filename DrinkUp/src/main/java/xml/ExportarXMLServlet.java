
package xml;

import br.com.drinkup.model.TipoUsuario;
import br.com.drinkup.model.Usuario;
import br.com.drinkup.utilidades.UtilidadesSessao;
import xml.service.EstoqueXMLService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/estoque/xml/exportar")
public class ExportarXMLServlet extends HttpServlet {

    private EstoqueXMLService estoqueXMLService;

    @Override
    public void init() throws ServletException {
        estoqueXMLService = new EstoqueXMLService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuarioLogado = UtilidadesSessao.getUsuarioLogado(request);
        if (usuarioLogado == null || usuarioLogado.getTipo() != TipoUsuario.Admin) {
            response.sendRedirect(request.getContextPath() + "/paginaLogin.html");
            return;
        }

        try {


            byte[] arquivoXML =
                    estoqueXMLService.exportarEstoque();


            response.setContentType("application/xml");
            response.setCharacterEncoding("UTF-8");

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"estoque.xml\""
            );

            response.setContentLength(arquivoXML.length);


            response.getOutputStream().write(arquivoXML);
            response.getOutputStream().flush();

        } catch (Exception e) {

            throw new ServletException(
                    "Erro ao exportar o estoque para XML.", e
            );
        }
    }
}
