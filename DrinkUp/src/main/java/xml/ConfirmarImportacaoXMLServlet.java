package xml;

import br.com.drinkup.dao.ProdutoDao;
import br.com.drinkup.model.Produto;
import br.com.drinkup.model.Usuario;
import br.com.drinkup.utilidades.UtilidadesSessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/estoque/xml/confirmar")
public class ConfirmarImportacaoXMLServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Garante que há um usuário logado na sessão
        Usuario usuarioLogado = UtilidadesSessao.getUsuarioLogado(request);
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/paginaLogin.html");
            return;
        }

        List<Produto> produtos = (List<Produto>)
                request.getSession().getAttribute("produtosImportacaoXML");

        if (produtos == null || produtos.isEmpty()) {
            request.getSession().setAttribute(
                    "erroImportacao",
                    "Nenhum produto encontrado para importar."
            );
            response.sendRedirect(request.getContextPath() + "/produtos");
            return;
        }

        try {
            // 2. Salva os produtos importados no estoque
            new ProdutoDao().importarProdutos(produtos);

            request.getSession().removeAttribute("produtosImportacaoXML");
            request.getSession().setAttribute(
                    "sucessoImportacao",
                    "Produtos importados com sucesso!"
            );

            // 3. Redireciona para a página de estoque/produtos após confirmar
            response.sendRedirect(request.getContextPath() + "/produtos");

        } catch (SQLException e) {
            request.getSession().setAttribute(
                    "erroImportacao",
                    "Erro ao importar produtos: " + e.getMessage()
            );
            response.sendRedirect(request.getContextPath() + "/produtos");
        }
    }
}