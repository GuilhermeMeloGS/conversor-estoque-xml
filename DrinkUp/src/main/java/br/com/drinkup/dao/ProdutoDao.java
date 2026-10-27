package br.com.drinkup.dao;

import br.com.drinkup.model.Produto;
import br.com.drinkup.servlet.config.ConnectionPoolConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDao {

    public void criarProduto(Produto produto) throws SQLException {
        String SQL = "INSERT INTO PRODUTO " +
                "(IDCATEGORIA, NOME, CODIGODEBARRAS, DESCRICAO, PRECO, QUANTIDADEESTOQUE, VOLUMEML) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = ConnectionPoolConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setInt(1, produto.getIdCategoria());
            preparedStatement.setString(2, produto.getNomeDoProduto());
            preparedStatement.setString(3, produto.getCodigoDeBarras());
            preparedStatement.setString(4, produto.getDescricao());
            preparedStatement.setDouble(5, produto.getPreco());
            preparedStatement.setInt(6, produto.getQuantidadeEstoque());
            preparedStatement.setInt(7, produto.getVolumeML());

            preparedStatement.execute(); // Ou executeUpdate()

            System.out.println("Produto criado com sucesso!");
            connection.close();

        } catch (Exception e) {
            System.out.println("Erro ao criar produto: " + e.getMessage());
        }
    }

    public List<Produto> listarProdutos() throws SQLException {
        // Usamos IDPRODUTO aqui para coincidir com o que você já usou no delete/update
        String SQL = "SELECT IDPRODUTO, IDCATEGORIA, NOME, CODIGODEBARRAS, DESCRICAO, PRECO, QUANTIDADEESTOQUE, VOLUMEML FROM PRODUTO";
        List<Produto> produtos = new ArrayList<>();

        try {
            Connection connection = ConnectionPoolConfig.getConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Produto p = new Produto();
                p.setIdProduto(rs.getInt("IDPRODUTO")); // E buscamos IDPRODUTO aqui também
                p.setIdCategoria(rs.getInt("IDCATEGORIA"));
                p.setNomeDoProduto(rs.getString("NOME"));
                p.setCodigoDeBarras(rs.getString("CODIGODEBARRAS"));
                p.setDescricao(rs.getString("DESCRICAO"));
                p.setPreco(rs.getDouble("PRECO"));
                p.setQuantidadeEstoque(rs.getInt("QUANTIDADEESTOQUE"));
                p.setVolumeML(rs.getInt("VOLUMEML"));
                produtos.add(p);
            }

            connection.close();

        } catch (Exception e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }

        return produtos;
    }

    public void deletarProduto(int id) throws SQLException {
        String SQL = "DELETE FROM PRODUTO WHERE IDPRODUTO = ?";

        try {
            Connection connection = ConnectionPoolConfig.getConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);
            ps.setInt(1, id);
            ps.execute();
            System.out.println("Produto deletado com sucesso!");
            connection.close();
        } catch (Exception e) {
            System.out.println("Erro ao deletar produto: " + e.getMessage());
        }
    }

    public void atualizarProduto(Produto produto) throws SQLException {
        String SQL = "UPDATE PRODUTO SET IDCATEGORIA = ?, NOME = ?, CODIGODEBARRAS = ?, DESCRICAO = ?, PRECO = ?, QUANTIDADEESTOQUE = ?, VOLUMEML = ? WHERE IDPRODUTO = ?";

        try {
            Connection connection = ConnectionPoolConfig.getConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);

            ps.setInt(1, produto.getIdCategoria());
            ps.setString(2, produto.getNomeDoProduto());
            ps.setString(3, produto.getCodigoDeBarras());
            ps.setString(4, produto.getDescricao());
            ps.setDouble(5, produto.getPreco());
            ps.setInt(6, produto.getQuantidadeEstoque());
            ps.setInt(7, produto.getVolumeML());
            ps.setInt(8, produto.getIdProduto()); // ID que vai no WHERE

            ps.execute();
            System.out.println("Produto atualizado com sucesso!");
            connection.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}