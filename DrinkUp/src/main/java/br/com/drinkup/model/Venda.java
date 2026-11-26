package br.com.drinkup.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {

    private final int idVenda;
    private final int idUsuario;
    private final String nomeCliente;
    private final LocalDateTime dataVenda;
    private final BigDecimal valorTotal;
    private final StatusVenda status;
    private final Endereco endereco;
    private final List<ItemVenda> itens = new ArrayList<>();

    public Venda(int idVenda, int idUsuario, String nomeCliente, LocalDateTime dataVenda,
                 BigDecimal valorTotal, StatusVenda status, Endereco endereco) {
        this.idVenda = idVenda;
        this.idUsuario = idUsuario;
        this.nomeCliente = nomeCliente;
        this.dataVenda = dataVenda;
        this.valorTotal = valorTotal;
        this.status = status;
        this.endereco = endereco;
    }

    public int getIdVenda() {
        return idVenda;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public StatusVenda getStatus() {
        return status;
    }

    /** Null em pedidos antigos, criados antes de existir o endereço de entrega. */
    public Endereco getEndereco() {
        return endereco;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }
}
