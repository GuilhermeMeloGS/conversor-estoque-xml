
package xml;

import br.com.drinkup.model.Produto;
import br.com.drinkup.model.TipoUsuario;
import br.com.drinkup.model.Usuario;
import br.com.drinkup.utilidades.UtilidadesSessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/estoque/xml/preview")
public class PreviewImportacaoXMLServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuarioLogado = UtilidadesSessao.getUsuarioLogado(request);
        if (usuarioLogado == null || usuarioLogado.getTipo() != TipoUsuario.Admin) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print("{\"erro\":\"Acesso permitido apenas para administradores.\"}");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Produto> produtos = null;

        if (request.getSession(false) != null) {
            produtos = (List<Produto>) request.getSession(false)
                    .getAttribute("produtosImportacaoXML");
        }

        if (produtos == null || produtos.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            try (PrintWriter out = response.getWriter()) {
                out.print("{\"erro\":\"Nenhum produto encontrado na importação.\"}");
            }

            return;
        }

        try (PrintWriter out = response.getWriter()) {

            out.print("[");

            for (int i = 0; i < produtos.size(); i++) {

                Produto produto = produtos.get(i);

                if (i > 0) {
                    out.print(",");
                }

                out.print("{");
                out.print("\"idCategoria\":" + produto.getIdCategoria() + ",");
                out.print("\"nomeDoProduto\":\"" + escaparJSON(produto.getNomeDoProduto()) + "\",");
                out.print("\"codigoDeBarras\":\"" + escaparJSON(produto.getCodigoDeBarras()) + "\",");
                out.print("\"preco\":" + produto.getPreco() + ",");
                out.print("\"quantidadeEstoque\":" + produto.getQuantidadeEstoque() + ",");
                out.print("\"volumeML\":" + produto.getVolumeML());
                out.print("}");
            }

            out.print("]");
        }
    }

    private String escaparJSON(String valor) {

        if (valor == null) {
            return "";
        }

        return valor
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
