package xml;

import br.com.drinkup.utilidades.UtilidadesJSON;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/estoque/xml/mensagem")
public class MensagemImportacaoXMLServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String erro = "";
        String sucesso = "";

        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            if (sessao.getAttribute("erroImportacao") != null) {
                erro = (String) sessao.getAttribute("erroImportacao");
                sessao.removeAttribute("erroImportacao");
            }
            if (sessao.getAttribute("sucessoImportacao") != null) {
                sucesso = (String) sessao.getAttribute("sucessoImportacao");
                sessao.removeAttribute("sucessoImportacao");
            }
        }

        UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK,
                "{\"erro\":\"" + UtilidadesJSON.escapeJson(erro) + "\","
                        + "\"sucesso\":\"" + UtilidadesJSON.escapeJson(sucesso) + "\"}");
    }
}