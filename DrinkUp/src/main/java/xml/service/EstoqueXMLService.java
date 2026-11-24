package xml.service;

import br.com.drinkup.dao.EstoqueDao;
import br.com.drinkup.dao.ProdutoDao;
import br.com.drinkup.model.Produto;

import xml.GeradorXML;
import xml.LeitorXML;

import java.io.InputStream;
import java.util.List;

public class EstoqueXMLService {

    private final ProdutoDao produtoDao;
    private final EstoqueDao estoqueDao;
    private final GeradorXML geradorXML;
    private final LeitorXML leitorXML;

    public EstoqueXMLService() {
        this.produtoDao = new ProdutoDao();
        this.estoqueDao = new EstoqueDao();
        this.geradorXML = new GeradorXML();
        this.leitorXML = new LeitorXML();
    }

    public List<Produto> listarEstoque() {
        List<Produto> produtos = produtoDao.listarProdutos();

        if (produtos == null) {
            throw new IllegalStateException("Não foi possível consultar o estoque.");
        }

        return produtos;
    }

    public byte[] exportarEstoque() throws Exception {
        return geradorXML.gerar(listarEstoque());
    }

    /** Exporta um estoque importado por XML. */
    public byte[] exportarEstoqueImportado(int idEstoque) throws Exception {
        if (estoqueDao.buscarNomeEstoqueImportado(idEstoque) == null) {
            throw new IllegalArgumentException("Estoque não encontrado.");
        }

        return geradorXML.gerar(estoqueDao.listarProdutosDoEstoque(idEstoque));
    }

    public List<Produto> importarEstoque(InputStream arquivo) throws Exception {
        if (arquivo == null) {
            throw new IllegalArgumentException("Nenhum arquivo XML foi enviado.");
        }

        return leitorXML.ler(arquivo);
    }
}