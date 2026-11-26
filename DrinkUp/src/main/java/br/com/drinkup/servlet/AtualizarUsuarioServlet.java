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

        // sem "id" edita o próprio perfil; com "id" de outro usuário, só admin pode
        int idAlvo = logado.getId();
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isBlank()) {
            try {
                idAlvo = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
                return;
            }
        }
        boolean editandoOutro = idAlvo != logado.getId();
        if (editandoOutro && logado.getTipo() != TipoUsuario.Admin) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_FORBIDDEN, "Sem permissão");
            return;
        }

        String nome = request.getParameter("nome");
        String sobrenome = request.getParameter("sobrenome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // senha em branco mantém a atual (admin não precisa conhecer a senha de outro perfil)
        boolean manterSenha = senha == null || senha.trim().isEmpty();

        if(nome == null || sobrenome == null || email == null ||
            nome.trim().isEmpty() || sobrenome.trim().isEmpty() || email.trim().isEmpty() ||
            nome.length() > 100 || sobrenome.length() > 100 || email.length() > 100 ||
            (!manterSenha && senha.length() > 50)){
            UtilidadesJSON.erro(response, HttpServletResponse.SC_BAD_REQUEST, "Dados inválidos");
            return;
        }

        UsuarioDao usuarioDao = new UsuarioDao();
        Usuario atual = usuarioDao.buscarPorId(idAlvo);
        if (atual == null) {
            UtilidadesJSON.erro(response, HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado");
            return;
        }

        String senhaFinal = manterSenha ? atual.getSenha() : senha.trim();
        boolean ok = usuarioDao.atualizarUsuario(idAlvo, nome.trim(), sobrenome.trim(), email.trim(), senhaFinal);
        Usuario usuarioAtualizado = ok? usuarioDao.buscarPorId(idAlvo) : null;

        if(usuarioAtualizado == null){
            UtilidadesJSON.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao atualizar usuário");
            return;
        }

        // só troca a sessão quando o próprio usuário logado foi alterado
        if (!editandoOutro) {
            request.getSession().setAttribute("usuario", usuarioAtualizado);
        }

        UtilidadesJSON.enviar(response, HttpServletResponse.SC_OK, UtilidadesJSON.usuarioJson(usuarioAtualizado));
    }
}
