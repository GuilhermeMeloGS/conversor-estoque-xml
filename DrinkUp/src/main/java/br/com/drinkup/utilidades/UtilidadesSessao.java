package br.com.drinkup.utilidades;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.drinkup.model.Usuario;

public class UtilidadesSessao {
    private UtilidadesSessao(){}

    public static Usuario getUsuarioLogado(HttpServletRequest request) {
        
        HttpSession sessao = request.getSession(false);
        if (sessao == null)return null;
        return(Usuario)sessao.getAttribute("usuario");
    }
}
