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

@WebServlet("/deletarUsuario")
public class DeletarUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Usuario logado = UtilidadesSessao.getUsuarioLogado(request);
        if (logado == null) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_UNAUTHORIZED, "Usuário não logado");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
            return;
        }

        boolean usuarioAtualAdmin = logado.getTipo() == TipoUsuario.Admin;
        boolean usuarioAtualLogado = logado.getId() == id;

        if(!usuarioAtualAdmin && !usuarioAtualLogado) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
            return;
        }
        if(!new UsuarioDao().deletarUsuario(id)) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao deletar usuário");
            return;
        }

        if(usuarioAtualLogado) {
            request.getSession().invalidate();
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
