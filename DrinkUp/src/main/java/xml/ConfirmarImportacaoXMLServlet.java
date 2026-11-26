package xml;

import br.com.drinkup.dao.EstoqueDao;
import br.com.drinkup.model.Produto;
import br.com.drinkup.model.TipoUsuario;
import br.com.drinkup.model.Usuario;
import br.com.drinkup.utilidades.UtilidadesSessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/estoque/xml/confirmar")
public class ConfirmarImportacaoXMLServlet extends HttpServlet {

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = UtilidadesSessao.getUsuarioLogado(request);
        if (usuario == null || usuario.getTipo() != TipoUsuario.Admin) {
            response.sendRedirect(request.getContextPath() + "/paginaLogin.html");
            return;
        }

        HttpSession sessao = request.getSession();
        List<Produto> produtos = (List<Produto>) sessao.getAttribute("produtosImportacaoXML");
        String nomeArquivo = (String) sessao.getAttribute("nomeArquivoImportacaoXML");

        if (produtos == null || produtos.isEmpty()) {
            sessao.setAttribute("erroImportacao", "Nenhum produto encontrado para importar.");
            response.sendRedirect(request.getContextPath() + "/xml/conversorXML.html");
            return;
        }

        try {
            EstoqueDao dao = new EstoqueDao();

            // Grava o estoque importado e envia seus produtos para a loja (tabela produto).
            int idEstoque = dao.salvarImportacao(usuario.getId(), nomeArquivo, produtos);
            int[] resultado = dao.enviarEstoqueParaLoja(idEstoque);

            sessao.removeAttribute("produtosImportacaoXML");
            sessao.removeAttribute("nomeArquivoImportacaoXML");
            sessao.setAttribute("sucessoImportacao", "Estoque importado com sucesso! "
                    + resultado[0] + " produto(s) novo(s) enviado(s) para a loja, "
                    + resultado[1] + " já existia(m).");

            response.sendRedirect(request.getContextPath() + "/estoques.html");

        } catch (SQLException e) {
            sessao.setAttribute("erroImportacao", "Erro ao importar produtos: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/xml/conversorXML.html");
        }
    }
}