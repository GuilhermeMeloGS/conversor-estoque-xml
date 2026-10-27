package br.com.drinkup.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.drinkup.dao.UsuarioDao;
import br.com.drinkup.model.Usuario;

@WebServlet ("/criarUsuario")
public class CreateUsuarioServlet extends HttpServlet {
    
    @Override 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String nome = request.getParameter("nome");
        String sobrenome = request.getParameter("sobrenome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        Usuario usuario = new Usuario(email, nome, senha, sobrenome);
        
        UsuarioDao usuarioDao = new UsuarioDao();

        try {
            usuarioDao.criarUsuario(usuario);
        } catch (SQLException ex) {
            throw new ServletException("Erro ao criar usuário", ex);
        }

        request.getRequestDispatcher("cadastroPessoa.html").forward(request, response);
    }
}
