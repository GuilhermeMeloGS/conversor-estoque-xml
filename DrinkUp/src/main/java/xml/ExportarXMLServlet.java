package xml;

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
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (UtilidadesSessao.exigirLogin(request, response, true) == null) {
            return;
        }

        try {
            String idParam = request.getParameter("id");
            byte[] arquivoXML;
            String nomeArquivo;

            if (idParam == null || idParam.trim().isEmpty()) {
                // Estoque principal
                arquivoXML = estoqueXMLService.exportarEstoque();
                nomeArquivo = "estoque.xml";
            } else {
                // Estoque importado por XML
                int id;
                try {
                    id = Integer.parseInt(idParam.trim());
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id inválido.");
                    return;
                }
                arquivoXML = estoqueXMLService.exportarEstoqueImportado(id);
                nomeArquivo = "estoque-importado-" + id + ".xml";
            }

            response.setContentType("application/xml");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + nomeArquivo + "\"");
            response.setContentLength(arquivoXML.length);

            response.getOutputStream().write(arquivoXML);
            response.getOutputStream().flush();

        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (Exception e) {
            throw new ServletException("Erro ao exportar o estoque para XML.", e);
        }
    }
}