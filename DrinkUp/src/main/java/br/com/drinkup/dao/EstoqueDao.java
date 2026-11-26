package br.com.drinkup.dao;

import br.com.drinkup.model.Produto;
import br.com.drinkup.servlet.config.ConnectionPoolConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    /** Lista os produtos de um estoque importado. O idProduto do Produto recebe o idProdutoEstoque. */
    public List<Produto> listarProdutosDoEstoque(int idEstoque) throws SQLException {
        String sql = """
                SELECT p.idProdutoEstoque, p.idCategoria, p.nome, p.codigoDeBarras, p.descricao,
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
                    p.setIdProduto(r.getInt("idProdutoEstoque"));
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

    // ------------------------------------------------------------------
    // EDIÇÃO DE ESTOQUE IMPORTADO
    // ------------------------------------------------------------------

    /** Renomeia um estoque importado. */
    public void renomearEstoque(int idEstoque, String novoNome) throws SQLException {
        if (novoNome == null || novoNome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe um nome para o estoque.");
        }

        novoNome = novoNome.trim();

        if (novoNome.length() > 150) {
            throw new IllegalArgumentException("O nome pode ter no máximo 150 caracteres.");
        }

        String sql = "UPDATE estoqueXML SET nome = ? WHERE idEstoque = ?";

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, novoNome);
            statement.setInt(2, idEstoque);

            if (statement.executeUpdate() == 0) {
                throw new IllegalArgumentException("Estoque não encontrado.");
            }
        }
    }

    /** Adiciona um produto a um estoque importado. */
    public void adicionarProduto(int idEstoque, Produto p) throws SQLException {

        if (p.getNomeDoProduto() == null || p.getNomeDoProduto().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome do produto.");
        }

        if (p.getCodigoDeBarras() == null || p.getCodigoDeBarras().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o código de barras.");
        }

        if (!Double.isFinite(p.getPreco()) || p.getPreco() < 0
                || p.getQuantidadeEstoque() < 0 || p.getVolumeML() < 0) {
            throw new IllegalArgumentException("Preço, estoque e volume não podem ser negativos.");
        }

        try (Connection connection = ConnectionPoolConfig.getConnection()) {

            if (!existe(connection, "SELECT COUNT(*) FROM estoqueXML WHERE idEstoque = ?", idEstoque)) {
                throw new IllegalArgumentException("Estoque não encontrado.");
            }

            if (!existe(connection, "SELECT COUNT(*) FROM categoria WHERE idCategoria = ?",
                    p.getIdCategoria())) {
                throw new IllegalArgumentException("Categoria não encontrada: " + p.getIdCategoria());
            }

            String sqlDuplicado = """
                    SELECT COUNT(*) FROM produtoEstoqueXML
                    WHERE idEstoque = ? AND codigoDeBarras = ?
                    """;

            try (PreparedStatement st = connection.prepareStatement(sqlDuplicado)) {
                st.setInt(1, idEstoque);
                st.setString(2, p.getCodigoDeBarras().trim());

                try (ResultSet r = st.executeQuery()) {
                    r.next();
                    if (r.getInt(1) > 0) {
                        throw new IllegalArgumentException(
                                "Já existe um produto com esse código de barras neste estoque.");
                    }
                }
            }

            String sqlInserir = """
                    INSERT INTO produtoEstoqueXML
                    (idEstoque, idCategoria, nome, codigoDeBarras, descricao,
                     preco, quantidadeEstoque, volumeML)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement st = connection.prepareStatement(sqlInserir)) {
                st.setInt(1, idEstoque);
                st.setInt(2, p.getIdCategoria());
                st.setString(3, p.getNomeDoProduto().trim());
                st.setString(4, p.getCodigoDeBarras().trim());
                st.setString(5, p.getDescricao());
                st.setDouble(6, p.getPreco());
                st.setInt(7, p.getQuantidadeEstoque());
                st.setInt(8, p.getVolumeML());
                st.executeUpdate();
            }
        }
    }

    /**
     * Associa um produto já cadastrado no catálogo principal a um estoque importado.
     * A quantidade informada é gravada somente em produtoEstoqueXML; não altera
     * produto.quantidadeEstoque.
     */
    public void associarProdutoAoEstoque(int idEstoque, int idProduto,
                                         int quantidadeEstoqueImportado) throws SQLException {
        if (quantidadeEstoqueImportado < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }

        String sqlProduto = """
                SELECT idCategoria, nome, codigoDeBarras, descricao, preco, volumeML
                FROM produto
                WHERE idProduto = ?
                """;
        String sqlDuplicado = """
                SELECT COUNT(*) FROM produtoEstoqueXML
                WHERE idEstoque = ? AND (idProduto = ? OR codigoDeBarras = ?)
                """;
        String sqlInserir = """
                INSERT INTO produtoEstoqueXML
                (idEstoque, idProduto, idCategoria, nome, codigoDeBarras, descricao,
                 preco, quantidadeEstoque, volumeML)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConnectionPoolConfig.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!existe(connection, "SELECT COUNT(*) FROM estoqueXML WHERE idEstoque = ?", idEstoque)) {
                    throw new IllegalArgumentException("Estoque não encontrado.");
                }

                int idCategoria;
                String nome;
                String codigoDeBarras;
                String descricao;
                double preco;
                int volumeML;

                try (PreparedStatement st = connection.prepareStatement(sqlProduto)) {
                    st.setInt(1, idProduto);
                    try (ResultSet r = st.executeQuery()) {
                        if (!r.next()) {
                            throw new IllegalArgumentException("Produto não encontrado no catálogo.");
                        }
                        idCategoria = r.getInt("idCategoria");
                        nome = r.getString("nome");
                        codigoDeBarras = r.getString("codigoDeBarras");
                        descricao = r.getString("descricao");
                        preco = r.getDouble("preco");
                        volumeML = r.getInt("volumeML");
                    }
                }

                try (PreparedStatement st = connection.prepareStatement(sqlDuplicado)) {
                    st.setInt(1, idEstoque);
                    st.setInt(2, idProduto);
                    st.setString(3, codigoDeBarras);
                    try (ResultSet r = st.executeQuery()) {
                        r.next();
                        if (r.getInt(1) > 0) {
                            throw new IllegalArgumentException(
                                    "Este produto já está associado a esse estoque.");
                        }
                    }
                }

                try (PreparedStatement st = connection.prepareStatement(sqlInserir)) {
                    st.setInt(1, idEstoque);
                    st.setInt(2, idProduto);
                    st.setInt(3, idCategoria);
                    st.setString(4, nome);
                    st.setString(5, codigoDeBarras);
                    st.setString(6, descricao);
                    st.setDouble(7, preco);
                    st.setInt(8, quantidadeEstoqueImportado);
                    st.setInt(9, volumeML);
                    st.executeUpdate();
                }

                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    /**
     * Envia os produtos de um estoque importado para a loja (tabela produto).
     * Produtos cujo código de barras ainda não existe na loja são criados, usando a
     * quantidade do estoque importado como estoque inicial. Produtos que já existem
     * na loja não são alterados. Em ambos os casos o produto do estoque importado
     * passa a ficar ligado ao produto da loja (idProduto).
     *
     * @return {quantidade de produtos criados na loja, quantidade que já existiam}
     */
    public int[] enviarEstoqueParaLoja(int idEstoque) throws SQLException {

        String sqlProdutos = """
                SELECT idProdutoEstoque, idCategoria, nome, codigoDeBarras, descricao,
                       preco, quantidadeEstoque, volumeML
                FROM produtoEstoqueXML
                WHERE idEstoque = ?
                ORDER BY idProdutoEstoque
                """;
        String sqlBuscar = "SELECT idProduto FROM produto WHERE codigoDeBarras = ?";
        String sqlCriar = """
                INSERT INTO produto
                (idCategoria, nome, codigoDeBarras, descricao, preco, quantidadeEstoque, volumeML)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        String sqlVincular = "UPDATE produtoEstoqueXML SET idProduto = ? WHERE idProdutoEstoque = ?";

        try (Connection connection = ConnectionPoolConfig.getConnection()) {
            connection.setAutoCommit(false);

            try {
                if (!existe(connection, "SELECT COUNT(*) FROM estoqueXML WHERE idEstoque = ?", idEstoque)) {
                    throw new IllegalArgumentException("Estoque não encontrado.");
                }

                // Lê todos os produtos do estoque antes de gravar.
                List<Produto> itens = new ArrayList<>();
                try (PreparedStatement st = connection.prepareStatement(sqlProdutos)) {
                    st.setInt(1, idEstoque);
                    try (ResultSet r = st.executeQuery()) {
                        while (r.next()) {
                            Produto p = new Produto();
                            p.setIdProduto(r.getInt("idProdutoEstoque")); // id na tabela do estoque
                            p.setIdCategoria(r.getInt("idCategoria"));
                            p.setNomeDoProduto(r.getString("nome"));
                            p.setCodigoDeBarras(r.getString("codigoDeBarras"));
                            p.setDescricao(r.getString("descricao"));
                            p.setPreco(r.getDouble("preco"));
                            p.setQuantidadeEstoque(r.getInt("quantidadeEstoque"));
                            p.setVolumeML(r.getInt("volumeML"));
                            itens.add(p);
                        }
                    }
                }

                if (itens.isEmpty()) {
                    throw new IllegalArgumentException("Este estoque não possui produtos.");
                }

                int criados = 0;
                int jaExistiam = 0;

                try (PreparedStatement buscar = connection.prepareStatement(sqlBuscar);
                     PreparedStatement criar = connection.prepareStatement(
                             sqlCriar, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement vincular = connection.prepareStatement(sqlVincular)) {

                    for (Produto p : itens) {

                        if (p.getIdCategoria() <= 0) {
                            throw new IllegalArgumentException(
                                    "Produto sem categoria: " + p.getNomeDoProduto());
                        }

                        Integer idProduto = null;

                        buscar.setString(1, p.getCodigoDeBarras());
                        try (ResultSet r = buscar.executeQuery()) {
                            if (r.next()) {
                                idProduto = r.getInt(1);
                            }
                        }

                        if (idProduto == null) {
                            criar.setInt(1, p.getIdCategoria());
                            criar.setString(2, p.getNomeDoProduto());
                            criar.setString(3, p.getCodigoDeBarras());
                            criar.setString(4, p.getDescricao());
                            criar.setDouble(5, p.getPreco());
                            criar.setInt(6, p.getQuantidadeEstoque());
                            criar.setInt(7, p.getVolumeML());
                            criar.executeUpdate();

                            try (ResultSet keys = criar.getGeneratedKeys()) {
                                if (!keys.next()) {
                                    throw new SQLException("Não foi possível obter o ID do produto criado.");
                                }
                                idProduto = keys.getInt(1);
                            }
                            criados++;
                        } else {
                            jaExistiam++;
                        }

                        vincular.setInt(1, idProduto);
                        vincular.setInt(2, p.getIdProduto());
                        vincular.executeUpdate();
                    }
                }

                connection.commit();
                return new int[]{criados, jaExistiam};

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    /** Remove um produto de um estoque importado. */
    public void removerProduto(int idEstoque, int idProdutoEstoque) throws SQLException {
        String sql = "DELETE FROM produtoEstoqueXML WHERE idProdutoEstoque = ? AND idEstoque = ?";

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idProdutoEstoque);
            statement.setInt(2, idEstoque);

            if (statement.executeUpdate() == 0) {
                throw new IllegalArgumentException("Produto não encontrado neste estoque.");
            }
        }
    }

    private boolean existe(Connection connection, String sql, int id) throws SQLException {
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);

            try (ResultSet r = st.executeQuery()) {
                r.next();
                return r.getInt(1) > 0;
            }
        }
    }

    // ------------------------------------------------------------------
    // IMPORTAÇÃO
    // ------------------------------------------------------------------

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
                        sqlImportacao, Statement.RETURN_GENERATED_KEYS)) {
                    st.setInt(1, idUsuarioAdmin);
                    st.setString(2, nomeArquivo);
                    st.executeUpdate();
                    try (ResultSet keys = st.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Não foi possível obter o ID da importação.");
                        }
                        idImportacao = keys.getInt(1);
                    }
                }

                int idEstoque;
                try (PreparedStatement st = connection.prepareStatement(
                        sqlEstoque, Statement.RETURN_GENERATED_KEYS)) {
                    st.setInt(1, idImportacao);
                    st.setString(2, nomeEstoque);
                    st.executeUpdate();
                    try (ResultSet keys = st.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Não foi possível obter o ID do estoque.");
                        }
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