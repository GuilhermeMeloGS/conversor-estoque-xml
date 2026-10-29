package br.com.drinkup.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.drinkup.dao.UsuarioDao;
import br.com.drinkup.model.TipoUsuario;
import br.com.drinkup.model.Usuario;
import br.com.drinkup.utilidades.UtilidadesJSON;
import br.com.drinkup.utilidades.UtilidadesSessao;

@WebServlet("/alterarTipoUsuario")
public class AlterarTipoUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Usuario logado = UtilidadesSessao.getUsuarioLogado(request);
        if (logado == null) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_UNAUTHORIZED, "Usuário não logado");
            return;
        }
        if (logado.getTipo() != TipoUsuario.Admin) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
            return;
        }

        int id;
        TipoUsuario tipo;

        try {
            id = Integer.parseInt(request.getParameter("id"));
            tipo = TipoUsuario.valueOf(request.getParameter("tipo"));
        } catch (IllegalAccessError | NullPointerException e ) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, "ID ou tipo inválido");
            return;
        }

        if(id == logado.getId()) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_FORBIDDEN, "Não é possível alterar o seu próprio perfil");
            return;
        }

        if(!new UsuarioDao().atualizarTipo(id, tipo)){
            UtilidadesJSON.erro(response, HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado");
            return;
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
