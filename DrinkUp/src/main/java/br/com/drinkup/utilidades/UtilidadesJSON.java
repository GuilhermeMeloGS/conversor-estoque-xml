package br.com.drinkup.utilidades;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.drinkup.model.Usuario;

public class UtilidadesJSON {
    private UtilidadesJSON() {
    }

    public static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    public static void enviar(HttpServletResponse response, int statusCode, String json) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    public static void erro(HttpServletResponse response, int statusCode, String mensagem) throws IOException {
        enviar(response, statusCode, "{\"erro\":\"" + escapeJson(mensagem) + "\"}");
    }

    public static String usuarioJson(Usuario usuario){
        return "{\"id\":" + usuario.getId()
            + ",\"nome\":\"" + escapeJson(usuario.getNome()) + "\""
            + ",\"sobrenome\":\"" + escapeJson(usuario.getSobrenome()) + "\""
            + ",\"email\":\"" + escapeJson(usuario.getEmail()) + "\""
            + ",\"role\":\"" + usuario.getTipo().name().toLowerCase() + "\"}";
        }
}
