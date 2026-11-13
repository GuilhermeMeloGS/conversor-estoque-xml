package br.com.drinkup.model;

public class Produto {

    private int idProduto;
    private int idCategoria;
    private String nomeDoProduto;
    private String codigoDeBarras;
    private String descricao;
    private double preco;
    private int quantidadeEstoque;
    private int volumeML;

    public Produto() {
    }

    public Produto(int idProduto, int idCategoria, String nomeDoProduto, String codigoDeBarras, String descricao, double preco, int quantidadeEstoque, int volumeML) {
        this.idProduto = idProduto;
        this.idCategoria = idCategoria;
        this.nomeDoProduto = nomeDoProduto;
        this.codigoDeBarras = codigoDeBarras;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.volumeML = volumeML;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNomeDoProduto() {
        return nomeDoProduto;
    }

    public void setNomeDoProduto(String nomeDoProduto) {
        this.nomeDoProduto = nomeDoProduto;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getVolumeML() {
        return volumeML;
    }

    public void setVolumeML(int volumeML) {
        this.volumeML = volumeML;
    }
}
