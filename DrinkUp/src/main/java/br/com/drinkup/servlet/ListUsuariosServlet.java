package br.com.drinkup.servlet;

import java.io.IOException;
import java.util.List;

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

@WebServlet ("/listarUsuarios")
public class ListUsuariosServlet extends HttpServlet {

@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        Usuario logado = UtilidadesSessao.getUsuarioLogado(request);
        if (logado == null) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_UNAUTHORIZED, "Não autenticado");
            return;
        }
        if (logado.getTipo() != TipoUsuario.Admin) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_FORBIDDEN, "Sem permissão");
            return;
        }
        try{
            List <Usuario> usuarios = new UsuarioDao().mostrarUsuarios();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < usuarios.size(); i++) {
                Usuario usuario = usuarios.get(i);
                if(i > 0){
                    json.append(",");
                }
                json.append(String.format(
                "{\"id\": %d, \"nome\": \"%s\", \"sobrenome\": \"%s\", \"email\": \"%s\", \"tipo\": \"%s\", \"dataCadastro\": \"%s\"}",
                usuario.getId(),
                UtilidadesJSON.escapeJson(usuario.getNome()),
                UtilidadesJSON.escapeJson(usuario.getSobrenome()),
                UtilidadesJSON.escapeJson(usuario.getEmail()),
                usuario.getTipo().name(),
                usuario.getDataCadastro()));
            }
            json.append("]");

            response.getWriter().write(json.toString());
            
        }catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"erro\":\"Falha ao buscar usuarios\"}");
        }
    }
}
