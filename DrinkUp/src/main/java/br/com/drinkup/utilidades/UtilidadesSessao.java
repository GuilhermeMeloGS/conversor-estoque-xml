package br.com.drinkup.utilidades;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.com.drinkup.model.TipoUsuario;
import br.com.drinkup.model.Usuario;

public class UtilidadesSessao {
    private UtilidadesSessao(){}

    public static Usuario getUsuarioLogado(HttpServletRequest request) {
        
        HttpSession sessao = request.getSession(false);
        if (sessao == null)return null;
        return(Usuario)sessao.getAttribute("usuario");
    }

    /** Responde 401/403 e devolve null quando não há usuário logado (ou admin, se exigirAdmin). */
    public static Usuario exigirLogin(HttpServletRequest request, HttpServletResponse response,
                                      boolean exigirAdmin) throws IOException {
        Usuario logado = getUsuarioLogado(request);
        if (logado == null) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_UNAUTHORIZED, "Não autenticado");
            return null;
        }
        if (exigirAdmin && logado.getTipo() != TipoUsuario.Admin) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_FORBIDDEN, "Sem permissão");
            return null;
        }
        return logado;
    }
}
