
package br.com.drinkup.dao;

import br.com.drinkup.model.Produto;
import br.com.drinkup.servlet.config.ConnectionPoolConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProdutoDao {

    public void criarProduto(Produto produto) {
        String sql = """
                INSERT INTO produto
                (idCategoria, nome, codigoDeBarras, descricao,
                 preco, quantidadeEstoque, volumeML)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, produto.getIdCategoria());
            statement.setString(2, produto.getNomeDoProduto());
            statement.setString(3, produto.getCodigoDeBarras());
            statement.setString(4, produto.getDescricao());
            statement.setDouble(5, produto.getPreco());
            statement.setInt(6, produto.getQuantidadeEstoque());
            statement.setInt(7, produto.getVolumeML());

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Produto> listarProdutos() {
        List<Produto> produtos = new ArrayList<>();

        String sql = "SELECT * FROM produto";

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                Produto produto = new Produto();

                produto.setIdProduto(resultado.getInt("idProduto"));
                produto.setIdCategoria(resultado.getInt("idCategoria"));
                produto.setNomeDoProduto(resultado.getString("nome"));
                produto.setCodigoDeBarras(resultado.getString("codigoDeBarras"));
                produto.setDescricao(resultado.getString("descricao"));
                produto.setPreco(resultado.getDouble("preco"));
                produto.setQuantidadeEstoque(resultado.getInt("quantidadeEstoque"));
                produto.setVolumeML(resultado.getInt("volumeML"));

                produtos.add(produto);
            }

            return produtos;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void importarProdutos(List<Produto> produtos) throws SQLException {

        if (produtos == null || produtos.isEmpty()) {
            throw new SQLException("Nenhum produto foi informado para importação.");
        }

        String sql = """
                INSERT INTO produto
                (idCategoria, nome, codigoDeBarras, descricao,
                 preco, quantidadeEstoque, volumeML)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        String sqlCategoria =
                "SELECT COUNT(*) FROM categoria WHERE idCategoria = ?";

        Set<String> codigos = new HashSet<>();

        // Valida os produtos antes de acessar o banco.
        for (Produto produto : produtos) {

            if (produto.getNomeDoProduto() == null ||
                    produto.getNomeDoProduto().trim().isEmpty()) {
                throw new SQLException("Produto sem nome.");
            }

            String codigo = produto.getCodigoDeBarras();

            if (codigo == null || codigo.trim().isEmpty() ||
                    !codigos.add(codigo)) {
                throw new SQLException(
                        "Código de barras ausente ou duplicado: " + codigo
                );
            }

            if (!Double.isFinite(produto.getPreco()) ||
                    produto.getPreco() < 0 ||
                    produto.getQuantidadeEstoque() < 0 ||
                    produto.getVolumeML() < 0) {
                throw new SQLException("Existem valores inválidos no XML.");
            }
        }

        // Abre a conexão e inicia a transação.
        try (Connection connection = ConnectionPoolConfig.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement verificarCategoria =
                         connection.prepareStatement(sqlCategoria);
                 PreparedStatement inserir =
                         connection.prepareStatement(sql)) {

                for (Produto produto : produtos) {

                    // Verifica se a categoria existe.
                    verificarCategoria.setInt(1, produto.getIdCategoria());

                    try (ResultSet resultado =
                                 verificarCategoria.executeQuery()) {

                        resultado.next();

                        if (resultado.getInt(1) == 0) {
                            throw new SQLException(
                                    "Categoria não encontrada: "
                                            + produto.getIdCategoria()
                            );
                        }
                    }

                    // Prepara a inserção do produto.
                    inserir.setInt(1, produto.getIdCategoria());
                    inserir.setString(2, produto.getNomeDoProduto());
                    inserir.setString(3, produto.getCodigoDeBarras());
                    inserir.setString(4, produto.getDescricao());
                    inserir.setDouble(5, produto.getPreco());
                    inserir.setInt(6, produto.getQuantidadeEstoque());
                    inserir.setInt(7, produto.getVolumeML());

                    inserir.addBatch();
                }

                // Executa todas as inserções.
                inserir.executeBatch();

                // Confirma a transação.
                connection.commit();

            } catch (Exception e) {

                // Se alguma inserção falhar, desfaz todas.
                connection.rollback();

                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }

                throw new SQLException("Erro ao importar produtos.", e);
            }
        }
    }


    public void atualizarProduto(Produto produto) throws SQLException {
        String SQL = """
            UPDATE PRODUTO
            SET IDCATEGORIA = ?, NOME = ?, CODIGODEBARRAS = ?,
                DESCRICAO = ?, PRECO = ?, QUANTIDADEESTOQUE = ?,
                VOLUMEML = ?
            WHERE IDPRODUTO = ?
            """;

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {

            ps.setInt(1, produto.getIdCategoria());
            ps.setString(2, produto.getNomeDoProduto());
            ps.setString(3, produto.getCodigoDeBarras());
            ps.setString(4, produto.getDescricao());
            ps.setDouble(5, produto.getPreco());
            ps.setInt(6, produto.getQuantidadeEstoque());
            ps.setInt(7, produto.getVolumeML());
            ps.setInt(8, produto.getIdProduto());

            ps.executeUpdate();
        }
    }

    public void deletarProduto(int id) throws SQLException {
        String SQL = "DELETE FROM PRODUTO WHERE IDPRODUTO = ?";

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

}
