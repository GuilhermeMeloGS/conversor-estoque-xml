package br.com.drinkup.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.com.drinkup.dao.UsuarioDao;
import br.com.drinkup.model.Usuario;

@WebServlet ("/logarUsuario")
public class LogarUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");



        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if (email == null || senha == null || email.trim().isEmpty() || senha.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\\\"erro\\\":\\\"Dados inválidos\\\"}");
            return;
        }

        Usuario usuario = new UsuarioDao().logarUsuario(email, senha);
        
        if(usuario == null)
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"erro\":\"E-mail ou senha inválidos\"}");
            return;
        }

        HttpSession sessao = request.getSession(false);

        if(sessao != null)sessao.invalidate();
        request.getSession(true).setAttribute("usuario", usuario);
        String nome = usuario.getNome().replace("\\","\\\\").replace("\"", "\\\"");
        String tipo = usuario.getTipo().name().toLowerCase();

        response.getWriter().write("{\"id\":" + usuario.getId() + ",\"nome\":\"" + nome + "\",\"role\":\"" + tipo + "\"}");
    }
}
