package br.com.drinkup.utilidades;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public final class UtilidadesImagem {

    public static final long TAMANHO_MAXIMO = 2L * 1024 * 1024;

    private UtilidadesImagem() {
    }

    /** Identifica PNG/JPEG/WebP pelos primeiros bytes (não confia no Content-Type nem no nome do arquivo). */
    public static String tipoPorAssinatura(byte[] b) {
        if (b == null) return null;

        if (b.length > 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return "image/png";
        }
        if (b.length > 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (b.length > 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "image/webp";
        }
        return null;
    }

    /**
     * Lê o campo de upload do formulário multipart.
     *
     * @return os bytes da imagem, ou null se nenhum arquivo foi enviado
     * @throws IllegalArgumentException arquivo grande demais ou que não é PNG/JPG
     */
    public static byte[] lerUpload(HttpServletRequest request, String campo) throws IOException, ServletException {
        Part part;
        try {
            part = request.getPart(campo);
        } catch (IllegalStateException e) {
            // Limite do @MultipartConfig excedido.
            throw new IllegalArgumentException("A imagem deve ter no máximo 2 MB.");
        }

        if (part == null || part.getSize() == 0) return null;

        if (part.getSize() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("A imagem deve ter no máximo 2 MB.");
        }

        byte[] bytes;
        try (InputStream in = part.getInputStream()) {
            bytes = in.readAllBytes();
        }

        if (tipoPorAssinatura(bytes) == null) {
            throw new IllegalArgumentException("Envie uma imagem PNG, JPG ou WebP válida.");
        }
        return bytes;
    }
}
