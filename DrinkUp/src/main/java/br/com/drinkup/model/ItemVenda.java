package br.com.drinkup.model;

import java.math.BigDecimal;

public class ItemVenda {

    private final int idProduto;
    private final String nomeProduto;
    private final int quantidade;
    private final BigDecimal precoUnitario;

    public ItemVenda(int idProduto, String nomeProduto, int quantidade, BigDecimal precoUnitario) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
}
