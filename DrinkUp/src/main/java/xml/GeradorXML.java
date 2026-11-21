
package xml;

import br.com.drinkup.model.Produto;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class GeradorXML {

    public byte[] gerar(List<Produto> produtos) throws Exception {


        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document documento = builder.newDocument();

        // Elemento principal do XML
        Element estoque = documento.createElement("estoque");
        documento.appendChild(estoque);


        for (Produto produto : produtos) {

            Element elementoProduto =
                    documento.createElement("produto");

            estoque.appendChild(elementoProduto);

            adicionarElemento(documento, elementoProduto,
                    "idCategoria", produto.getIdCategoria());

            adicionarElemento(documento, elementoProduto,
                    "nome", produto.getNomeDoProduto());

            adicionarElemento(documento, elementoProduto,
                    "codigoDeBarras", produto.getCodigoDeBarras());

            adicionarElemento(documento, elementoProduto,
                    "descricao", produto.getDescricao());

            adicionarElemento(documento, elementoProduto,
                    "preco", produto.getPreco());

            adicionarElemento(documento, elementoProduto,
                    "quantidadeEstoque", produto.getQuantidadeEstoque());

            adicionarElemento(documento, elementoProduto,
                    "volumeML", produto.getVolumeML());
        }


        TransformerFactory transformerFactory =
                TransformerFactory.newInstance();

        Transformer transformer =
                transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        transformer.transform(
                new DOMSource(documento),
                new StreamResult(saida)
        );

        return saida.toByteArray();
    }

    // Método auxiliar para criar elementos XML
    private void adicionarElemento(
            Document documento,
            Element pai,
            String nome,
            Object valor) {

        Element elemento = documento.createElement(nome);

        if (valor != null) {
            elemento.setTextContent(valor.toString());
        }

        pai.appendChild(elemento);
    }
}
