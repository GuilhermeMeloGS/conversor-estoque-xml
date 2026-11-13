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

@WebServlet ("/atualizarUsuario")
public class AtualizarUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Usuario logado = UtilidadesSessao.getUsuarioLogado(request);
        if (logado == null){
            UtilidadesJSON.erro(response, HttpServletResponse.SC_UNAUTHORIZED, "Usuário não logado");
            return;
        }

        String nome = request.getParameter("nome");
        String sobrenome = request.getParameter("sobrenome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if(nome == null || sobrenome == null || email == null || senha == null ||
            nome.trim().isEmpty() || sobrenome.trim().isEmpty() || email.trim().isEmpty() || senha.trim().isEmpty() ||
            nome.length() > 100 || sobrenome.length() > 100 || email.length() > 100 || senha.length() > 50){
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, "Dados inválidos");
            return;
        }

        UsuarioDao usuarioDao = new UsuarioDao();
        boolean ok = usuarioDao.atualizarUsuario(logado.getId(), nome.trim(), sobrenome.trim(), email.trim(), senha.trim());
        Usuario usuarioAtualizado = ok? usuarioDao.buscarPorId(logado.getId()) : null;
        
        if(usuarioAtualizado == null){
            UtilidadesJSON.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao atualizar usuário");
            return;
        }

        request.getSession().setAttribute("usuario", usuarioAtualizado);

        UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK, UtilidadesJSON.usuarioJson(usuarioAtualizado));
    }
}
