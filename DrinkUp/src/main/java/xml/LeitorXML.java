
package xml;

import br.com.drinkup.model.Produto;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LeitorXML {

    public List<Produto> ler(InputStream arquivo) throws Exception {

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();


        factory.setFeature(
                XMLConstants.FEATURE_SECURE_PROCESSING, true);

        factory.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl",
                true);

        factory.setFeature(
                "http://xml.org/sax/features/external-general-entities",
                false);

        factory.setFeature(
                "http://xml.org/sax/features/external-parameter-entities",
                false);

        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);


        DocumentBuilder builder = factory.newDocumentBuilder();

        Document documento = builder.parse(arquivo);


        Element raiz = documento.getDocumentElement();

        if (!"estoque".equals(raiz.getTagName())) {
            throw new Exception("O XML não possui a estrutura de estoque.");
        }

        NodeList listaProdutos = raiz.getElementsByTagName("produto");

        List<Produto> produtos = new ArrayList<>();


        for (int i = 0; i < listaProdutos.getLength(); i++) {

            Node node = listaProdutos.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element elemento = (Element) node;


            try {
                int idCategoria = Integer.parseInt(
                        obterValor(elemento, "idCategoria"));

                String nome = obterValor(elemento, "nome");
                String codigoDeBarras =
                        obterValor(elemento, "codigoDeBarras");
                String descricao = obterValor(elemento, "descricao");
                double preco = Double.parseDouble(
                        obterValor(elemento, "preco"));
                int quantidadeEstoque = Integer.parseInt(
                        obterValor(elemento, "quantidadeEstoque"));
                int volumeML = Integer.parseInt(
                        obterValor(elemento, "volumeML"));

                Produto produto = new Produto();
                produto.setIdCategoria(idCategoria);
                produto.setNomeDoProduto(nome);
                produto.setCodigoDeBarras(codigoDeBarras);
                produto.setDescricao(descricao);
                produto.setPreco(preco);
                produto.setQuantidadeEstoque(quantidadeEstoque);
                produto.setVolumeML(volumeML);
                produtos.add(produto);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "O XML possui um valor numérico inválido no produto "
                                + (i + 1) + ".", e
                );
            }
        }

        return produtos;
    }


    private String obterValor(Element elemento, String nomeTag)
            throws Exception {

        NodeList lista = elemento.getElementsByTagName(nomeTag);

        if (lista.getLength() == 0) {
            throw new Exception("Tag obrigatória ausente: " + nomeTag);
        }


        Element tag = null;

        for (int i = 0; i < lista.getLength(); i++) {
            Node node = lista.item(i);

            if (node.getParentNode() == elemento) {
                tag = (Element) node;
                break;
            }
        }

        if (tag == null) {
            throw new Exception("Tag inválida: " + nomeTag);
        }

        return tag.getTextContent().trim();
    }
}
