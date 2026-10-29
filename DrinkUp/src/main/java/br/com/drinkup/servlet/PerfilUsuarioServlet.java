package br.com.drinkup.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.drinkup.dao.UsuarioDao;
import br.com.drinkup.model.Usuario;
import br.com.drinkup.utilidades.UtilidadesJSON;
import br.com.drinkup.utilidades.UtilidadesSessao;


@WebServlet ("/perfilUsuario")
public class PerfilUsuarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Usuario logado = UtilidadesSessao.getUsuarioLogado(request);
        if (logado == null) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_UNAUTHORIZED, "Usuário não logado");
            return;
        }

        Usuario usuario = new UsuarioDao().buscarPorId(logado.getId());
        if (usuario == null) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado");
            return;
        }
        UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK, UtilidadesJSON.usuarioJson(usuario));

    }
}
