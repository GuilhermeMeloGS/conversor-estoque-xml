package br.com.drinkup.utilidades;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import br.com.drinkup.model.Categoria;
import br.com.drinkup.model.ItemVenda;
import br.com.drinkup.model.Produto;
import br.com.drinkup.model.Usuario;
import br.com.drinkup.model.Venda;

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

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String categoriaJson(Categoria categoria) {
        return "{\"id\":" + categoria.getIdCategoria()
            + ",\"nome\":\"" + escapeJson(categoria.getNome()) + "\""
            + ",\"icone\":\"" + escapeJson(categoria.getIcone()) + "\"}";
    }

    public static String produtoJson(Produto produto) {
        return "{\"id\":" + produto.getIdProduto()
            + ",\"idCategoria\":" + produto.getIdCategoria()
            + ",\"categoria\":\"" + escapeJson(produto.getNomeCategoria()) + "\""
            + ",\"icone\":\"" + escapeJson(produto.getIconeCategoria()) + "\""
            // A URL existe sempre: sem foto, o servlet devolve o placeholder "SEM FOTO".
            + ",\"imagem\":\"imagem-produto?id=" + produto.getIdProduto() + "\""
            + ",\"temFoto\":" + produto.isTemImagem()
            + ",\"nome\":\"" + escapeJson(produto.getNomeDoProduto()) + "\""
            + ",\"codigoDeBarras\":\"" + escapeJson(produto.getCodigoDeBarras()) + "\""
            + ",\"descricao\":\"" + escapeJson(produto.getDescricao()) + "\""
            + ",\"preco\":" + BigDecimal.valueOf(produto.getPreco()).toPlainString()
            + ",\"estoque\":" + produto.getQuantidadeEstoque()
            + ",\"volumeML\":" + produto.getVolumeML() + "}";
    }

    public static String vendaJson(Venda venda) {
        StringBuilder itens = new StringBuilder("[");
        List<ItemVenda> lista = venda.getItens();
        for (int i = 0; i < lista.size(); i++) {
            ItemVenda item = lista.get(i);
            if (i > 0) itens.append(",");
            itens.append("{\"idProduto\":").append(item.getIdProduto())
                 .append(",\"nome\":\"").append(escapeJson(item.getNomeProduto())).append("\"")
                 .append(",\"quantidade\":").append(item.getQuantidade())
                 .append(",\"precoUnitario\":").append(item.getPrecoUnitario().toPlainString()).append("}");
        }
        itens.append("]");

        return "{\"id\":" + venda.getIdVenda()
            + ",\"idUsuario\":" + venda.getIdUsuario()
            + ",\"cliente\":\"" + escapeJson(venda.getNomeCliente()) + "\""
            + ",\"data\":\"" + (venda.getDataVenda() == null ? "" : venda.getDataVenda().format(FORMATO_DATA)) + "\""
            + ",\"total\":" + venda.getValorTotal().toPlainString()
            + ",\"status\":\"" + venda.getStatus().name() + "\""
            + ",\"itens\":" + itens + "}";
    }

    public static String usuarioJson(Usuario usuario){
        return "{\"id\":" + usuario.getId()
            + ",\"nome\":\"" + escapeJson(usuario.getNome()) + "\""
            + ",\"sobrenome\":\"" + escapeJson(usuario.getSobrenome()) + "\""
            + ",\"email\":\"" + escapeJson(usuario.getEmail()) + "\""
            + ",\"role\":\"" + usuario.getTipo().name().toLowerCase() + "\"}";
        }
}
