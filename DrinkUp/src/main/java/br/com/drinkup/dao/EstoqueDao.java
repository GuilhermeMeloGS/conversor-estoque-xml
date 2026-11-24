package br.com.drinkup.dao;

import br.com.drinkup.model.Produto;
import br.com.drinkup.servlet.config.ConnectionPoolConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EstoqueDao {

    public int contarProdutosEstoquePrincipal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM produto";

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public int contarUnidadesEstoquePrincipal() throws SQLException {
        String sql = "SELECT COALESCE(SUM(quantidadeEstoque), 0) FROM produto";

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public List<Map<String, Object>> listarEstoquesImportados() throws SQLException {
        String sql = """
                SELECT
                    e.idEstoque,
                    e.nome,
                    COUNT(p.idProdutoEstoque) AS quantidadeProdutos,
                    COALESCE(SUM(p.quantidadeEstoque), 0) AS quantidadeTotal
                FROM estoqueXML e
                LEFT JOIN produtoEstoqueXML p
                    ON p.idEstoque = e.idEstoque
                GROUP BY e.idEstoque, e.nome
                ORDER BY e.idEstoque DESC
                """;

        List<Map<String, Object>> estoques = new ArrayList<>();

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, Object> estoque = new HashMap<>();

                estoque.put("id", resultSet.getInt("idEstoque"));
                estoque.put("nome", resultSet.getString("nome"));
                estoque.put("quantidadeProdutos", resultSet.getInt("quantidadeProdutos"));
                estoque.put("quantidadeTotal", resultSet.getInt("quantidadeTotal"));

                estoques.add(estoque);
            }
        }

        return estoques;
    }

    /** Nome do estoque importado, ou null se não existir. */
    public String buscarNomeEstoqueImportado(int idEstoque) throws SQLException {
        String sql = "SELECT nome FROM estoqueXML WHERE idEstoque = ?";

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idEstoque);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("nome") : null;
            }
        }
    }

    public List<Produto> listarProdutosDoEstoque(int idEstoque) throws SQLException {
        String sql = """
                SELECT p.idCategoria, p.nome, p.codigoDeBarras, p.descricao,
                       p.preco, p.quantidadeEstoque, p.volumeML,
                       c.nome AS nomeCategoria
                FROM produtoEstoqueXML p
                LEFT JOIN categoria c ON c.idCategoria = p.idCategoria
                WHERE p.idEstoque = ?
                ORDER BY p.idProdutoEstoque
                """;

        List<Produto> produtos = new ArrayList<>();

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idEstoque);

            try (ResultSet r = statement.executeQuery()) {
                while (r.next()) {
                    Produto p = new Produto();
                    p.setIdCategoria(r.getInt("idCategoria"));
                    p.setNomeDoProduto(r.getString("nome"));
                    p.setCodigoDeBarras(r.getString("codigoDeBarras"));
                    p.setDescricao(r.getString("descricao"));
                    p.setPreco(r.getDouble("preco"));
                    p.setQuantidadeEstoque(r.getInt("quantidadeEstoque"));
                    p.setVolumeML(r.getInt("volumeML"));
                    p.setNomeCategoria(r.getString("nomeCategoria"));
                    produtos.add(p);
                }
            }
        }

        return produtos;
    }

    /**
     * Salva uma importação XML em estoque próprio (importacaoXML, estoqueXML,
     * produtoEstoqueXML). Não altera a tabela produto. Devolve o id do novo estoque.
     */
    public int salvarImportacao(int idUsuarioAdmin, String nomeArquivo,
                                List<Produto> produtos) throws SQLException {

        if (produtos == null || produtos.isEmpty()) {
            throw new SQLException("Nenhum produto foi informado para importação.");
        }

        // Valida tudo antes de abrir a transação.
        Set<String> codigos = new HashSet<>();
        Set<Integer> categorias = new HashSet<>();

        for (Produto p : produtos) {
            if (p.getNomeDoProduto() == null || p.getNomeDoProduto().trim().isEmpty()) {
                throw new SQLException("Produto sem nome.");
            }

            String codigo = p.getCodigoDeBarras();
            if (codigo == null || codigo.trim().isEmpty() || !codigos.add(codigo)) {
                throw new SQLException("Código de barras ausente ou duplicado: " + codigo);
            }

            if (!Double.isFinite(p.getPreco()) || p.getPreco() < 0
                    || p.getQuantidadeEstoque() < 0 || p.getVolumeML() < 0) {
                throw new SQLException("Existem valores inválidos no XML.");
            }

            categorias.add(p.getIdCategoria());
        }

        String base = (nomeArquivo == null || nomeArquivo.trim().isEmpty())
                ? "Importação XML"
                : nomeArquivo.replaceAll("(?i)\\.xml$", "");
        if (base.length() > 100) {
            base = base.substring(0, 100);
        }
        String nomeEstoque = base + " ("
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                + ")";

        String sqlCategoria = "SELECT COUNT(*) FROM categoria WHERE idCategoria = ?";
        String sqlImportacao = "INSERT INTO importacaoXML (idUsuarioAdmin, nomeArquivo) VALUES (?, ?)";
        String sqlEstoque = "INSERT INTO estoqueXML (idImportacao, nome) VALUES (?, ?)";
        String sqlProduto = """
                INSERT INTO produtoEstoqueXML
                (idEstoque, idCategoria, nome, codigoDeBarras, descricao,
                 preco, quantidadeEstoque, volumeML)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConnectionPoolConfig.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // Confere se todas as categorias existem.
                try (PreparedStatement st = connection.prepareStatement(sqlCategoria)) {
                    for (int idCategoria : categorias) {
                        st.setInt(1, idCategoria);
                        try (ResultSet r = st.executeQuery()) {
                            r.next();
                            if (r.getInt(1) == 0) {
                                throw new SQLException("Categoria não encontrada: " + idCategoria);
                            }
                        }
                    }
                }

                int idImportacao;
                try (PreparedStatement st = connection.prepareStatement(
                        sqlImportacao, new int[]{1})) {
                    st.setInt(1, idUsuarioAdmin);
                    st.setString(2, nomeArquivo);
                    st.executeUpdate();
                    try (ResultSet keys = st.getGeneratedKeys()) {
                        keys.next();
                        idImportacao = keys.getInt(1);
                    }
                }

                int idEstoque;
                try (PreparedStatement st = connection.prepareStatement(
                        sqlEstoque, new int[]{1})) {
                    st.setInt(1, idImportacao);
                    st.setString(2, nomeEstoque);
                    st.executeUpdate();
                    try (ResultSet keys = st.getGeneratedKeys()) {
                        keys.next();
                        idEstoque = keys.getInt(1);
                    }
                }

                try (PreparedStatement st = connection.prepareStatement(sqlProduto)) {
                    for (Produto p : produtos) {
                        st.setInt(1, idEstoque);
                        st.setInt(2, p.getIdCategoria());
                        st.setString(3, p.getNomeDoProduto());
                        st.setString(4, p.getCodigoDeBarras());
                        st.setString(5, p.getDescricao());
                        st.setDouble(6, p.getPreco());
                        st.setInt(7, p.getQuantidadeEstoque());
                        st.setInt(8, p.getVolumeML());
                        st.addBatch();
                    }
                    st.executeBatch();
                }

                connection.commit();
                return idEstoque;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
}