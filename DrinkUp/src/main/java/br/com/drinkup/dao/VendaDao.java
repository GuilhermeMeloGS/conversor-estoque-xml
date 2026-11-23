package br.com.drinkup.dao;

import br.com.drinkup.model.ItemVenda;
import br.com.drinkup.model.StatusVenda;
import br.com.drinkup.model.Venda;
import br.com.drinkup.servlet.config.ConnectionPoolConfig;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VendaDao {

    private static final BigDecimal FRETE = new BigDecimal("8.90");
    private static final BigDecimal FRETE_GRATIS_ACIMA_DE = new BigDecimal("50.00");

    private static final String SQL_VENDAS = """
            SELECT v.idVenda, v.idUsuario, u.nome, u.sobrenome, v.dataVenda, v.valorTotal, v.status
            FROM venda v
            JOIN usuario u ON u.idUsuario = v.idUsuario
            """;

    /**
     * Registra a venda em uma única transação: valida o estoque, baixa o estoque,
     * grava a venda e seus itens usando o preço atual do banco.
     *
     * @param itens mapa idProduto -> quantidade
     * @return id da venda criada
     * @throws IllegalArgumentException regra de negócio violada (carrinho vazio, estoque insuficiente...)
     */
    public int criarVenda(int idUsuario, Map<Integer, Integer> itens) throws SQLException {

        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Carrinho vazio.");
        }

        try (Connection connection = ConnectionPoolConfig.getConnection()) {
            connection.setAutoCommit(false);

            try {
                BigDecimal subtotal = BigDecimal.ZERO;
                List<Object[]> linhas = new ArrayList<>(); // {idProduto, quantidade, preco}

                try (PreparedStatement buscar = connection.prepareStatement(
                        "SELECT nome, preco, quantidadeEstoque FROM produto WHERE idProduto = ? FOR UPDATE");
                     PreparedStatement baixar = connection.prepareStatement(
                             "UPDATE produto SET quantidadeEstoque = quantidadeEstoque - ? WHERE idProduto = ?")) {

                    for (Map.Entry<Integer, Integer> item : itens.entrySet()) {
                        int idProduto = item.getKey();
                        int quantidade = item.getValue();

                        if (quantidade < 1) {
                            throw new IllegalArgumentException("Quantidade inválida.");
                        }

                        buscar.setInt(1, idProduto);
                        try (ResultSet rs = buscar.executeQuery()) {
                            if (!rs.next()) {
                                throw new IllegalArgumentException("Produto não encontrado: " + idProduto);
                            }
                            if (rs.getInt("quantidadeEstoque") < quantidade) {
                                throw new IllegalArgumentException("Estoque insuficiente para: " + rs.getString("nome"));
                            }
                            BigDecimal preco = rs.getBigDecimal("preco");
                            subtotal = subtotal.add(preco.multiply(BigDecimal.valueOf(quantidade)));
                            linhas.add(new Object[]{idProduto, quantidade, preco});
                        }

                        baixar.setInt(1, quantidade);
                        baixar.setInt(2, idProduto);
                        baixar.executeUpdate();
                    }
                }

                BigDecimal frete = subtotal.compareTo(FRETE_GRATIS_ACIMA_DE) < 0 ? FRETE : BigDecimal.ZERO;
                BigDecimal total = subtotal.add(frete);

                int idVenda;
                try (PreparedStatement inserirVenda = connection.prepareStatement(
                        "INSERT INTO venda (idUsuario, valorTotal, status) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    inserirVenda.setInt(1, idUsuario);
                    inserirVenda.setBigDecimal(2, total);
                    inserirVenda.setString(3, StatusVenda.Pendente.name());
                    inserirVenda.executeUpdate();

                    try (ResultSet chaves = inserirVenda.getGeneratedKeys()) {
                        chaves.next();
                        idVenda = chaves.getInt(1);
                    }
                }

                try (PreparedStatement inserirItem = connection.prepareStatement(
                        "INSERT INTO itemVenda (idVenda, idProduto, quantidade, precoUnitario) VALUES (?, ?, ?, ?)")) {
                    for (Object[] linha : linhas) {
                        inserirItem.setInt(1, idVenda);
                        inserirItem.setInt(2, (Integer) linha[0]);
                        inserirItem.setInt(3, (Integer) linha[1]);
                        inserirItem.setBigDecimal(4, (BigDecimal) linha[2]);
                        inserirItem.addBatch();
                    }
                    inserirItem.executeBatch();
                }

                connection.commit();
                return idVenda;

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public List<Venda> listarTodas() throws SQLException {
        return listar(SQL_VENDAS + " ORDER BY v.idVenda DESC", null);
    }

    public List<Venda> listarPorUsuario(int idUsuario) throws SQLException {
        return listar(SQL_VENDAS + " WHERE v.idUsuario = ? ORDER BY v.idVenda DESC", idUsuario);
    }

    private List<Venda> listar(String sql, Integer idUsuario) throws SQLException {
        Map<Integer, Venda> vendas = new LinkedHashMap<>();

        try (Connection connection = ConnectionPoolConfig.getConnection()) {

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                if (idUsuario != null) {
                    ps.setInt(1, idUsuario);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String sobrenome = rs.getString("sobrenome");
                        String cliente = rs.getString("nome") + (sobrenome == null ? "" : " " + sobrenome);
                        Venda venda = new Venda(
                                rs.getInt("idVenda"),
                                rs.getInt("idUsuario"),
                                cliente,
                                rs.getObject("dataVenda", LocalDateTime.class),
                                rs.getBigDecimal("valorTotal"),
                                StatusVenda.valueOf(rs.getString("status")));
                        vendas.put(venda.getIdVenda(), venda);
                    }
                }
            }

            if (vendas.isEmpty()) {
                return new ArrayList<>();
            }

            String sqlItens = """
                    SELECT i.idVenda, i.idProduto, p.nome, i.quantidade, i.precoUnitario
                    FROM itemVenda i
                    JOIN produto p ON p.idProduto = i.idProduto
                    ORDER BY i.idItemVenda
                    """;
            try (PreparedStatement ps = connection.prepareStatement(sqlItens);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Venda venda = vendas.get(rs.getInt("idVenda"));
                    if (venda != null) {
                        venda.getItens().add(new ItemVenda(
                                rs.getInt("idProduto"),
                                rs.getString("nome"),
                                rs.getInt("quantidade"),
                                rs.getBigDecimal("precoUnitario")));
                    }
                }
            }
        }

        return new ArrayList<>(vendas.values());
    }

    /**
     * Atualiza o status. Venda cancelada é definitiva e devolve os itens ao estoque.
     *
     * @return false se a venda não existe ou já estava cancelada
     */
    public boolean atualizarStatus(int idVenda, StatusVenda novoStatus) throws SQLException {

        try (Connection connection = ConnectionPoolConfig.getConnection()) {
            connection.setAutoCommit(false);

            try {
                StatusVenda atual;
                try (PreparedStatement ps = connection.prepareStatement(
                        "SELECT status FROM venda WHERE idVenda = ? FOR UPDATE")) {
                    ps.setInt(1, idVenda);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            connection.rollback();
                            return false;
                        }
                        atual = StatusVenda.valueOf(rs.getString("status"));
                    }
                }

                if (atual == StatusVenda.Cancelado) {
                    connection.rollback();
                    return false;
                }

                if (novoStatus == StatusVenda.Cancelado) {
                    try (PreparedStatement ps = connection.prepareStatement("""
                            UPDATE produto p
                            SET quantidadeEstoque = quantidadeEstoque +
                                (SELECT SUM(i.quantidade) FROM itemVenda i
                                 WHERE i.idVenda = ? AND i.idProduto = p.idProduto)
                            WHERE p.idProduto IN (SELECT idProduto FROM itemVenda WHERE idVenda = ?)
                            """)) {
                        ps.setInt(1, idVenda);
                        ps.setInt(2, idVenda);
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = connection.prepareStatement(
                        "UPDATE venda SET status = ? WHERE idVenda = ?")) {
                    ps.setString(1, novoStatus.name());
                    ps.setInt(2, idVenda);
                    ps.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
}
