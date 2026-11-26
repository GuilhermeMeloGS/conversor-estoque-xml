package br.com.drinkup.servlet;

import br.com.drinkup.servlet.config.ConnectionPoolConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebListener
public class IniciarBancoServlet implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        List<String> scriptsCriacao = List.of(
                """
                CREATE TABLE IF NOT EXISTS usuario (
                    idUsuario INT PRIMARY KEY AUTO_INCREMENT,
                    nome VARCHAR(100),
                    sobrenome VARCHAR(100),
                    email VARCHAR(150) UNIQUE,
                    senha VARCHAR(255),
                    tipo ENUM('Admin', 'Cliente'),
                    criadoEm TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS categoria (
                    idCategoria INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                    nome VARCHAR(100),
                    icone VARCHAR(20)
                )
                """,
                "ALTER TABLE categoria ADD COLUMN IF NOT EXISTS icone VARCHAR(20)",
                """
                CREATE TABLE IF NOT EXISTS produto (
                    idProduto INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                    idCategoria INT NOT NULL,
                    nome VARCHAR(150),
                    codigoDeBarras VARCHAR(50) UNIQUE,
                    descricao CLOB,
                    preco DECIMAL(10,2),
                    quantidadeEstoque INT,
                    volumeML INT,
                    imagem BLOB,
                    CONSTRAINT fk_produto_categoria
                        FOREIGN KEY (idCategoria) REFERENCES categoria(idCategoria)
                )
                """,
                "ALTER TABLE produto ADD COLUMN IF NOT EXISTS imagem BLOB",
                """
                CREATE TABLE IF NOT EXISTS venda (
                    idVenda INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                    idUsuario INT NOT NULL,
                    dataVenda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    valorTotal DECIMAL(10,2),
                    status ENUM('Pendente', 'Cancelado', 'Concluido'),
                    CONSTRAINT fk_venda_usuario
                        FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
                )
                """,
                "ALTER TABLE venda ADD COLUMN IF NOT EXISTS enderecoCep VARCHAR(9)",
                "ALTER TABLE venda ADD COLUMN IF NOT EXISTS enderecoRua VARCHAR(150)",
                "ALTER TABLE venda ADD COLUMN IF NOT EXISTS enderecoNumero VARCHAR(20)",
                "ALTER TABLE venda ADD COLUMN IF NOT EXISTS enderecoComplemento VARCHAR(100)",
                "ALTER TABLE venda ADD COLUMN IF NOT EXISTS enderecoBairro VARCHAR(100)",
                "ALTER TABLE venda ADD COLUMN IF NOT EXISTS enderecoCidade VARCHAR(100)",
                "ALTER TABLE venda ADD COLUMN IF NOT EXISTS enderecoEstado VARCHAR(2)",
                """
                CREATE TABLE IF NOT EXISTS itemVenda (
                    idItemVenda INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                    idVenda INT NOT NULL,
                    idProduto INT NOT NULL,
                    quantidade INT,
                    precoUnitario DECIMAL(10,2),
                    CONSTRAINT fk_itemVenda_venda
                        FOREIGN KEY (idVenda) REFERENCES venda(idVenda),
                    CONSTRAINT fk_itemVenda_produto
                        FOREIGN KEY (idProduto) REFERENCES produto(idProduto)
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS importacaoXML (
                    idImportacao INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                    idUsuarioAdmin INT NOT NULL,
                    nomeArquivo VARCHAR(255),
                    dataImportacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_importacaoXML_usuario
                        FOREIGN KEY (idUsuarioAdmin) REFERENCES usuario(idUsuario)
                )
                """,

                // Cadastro dos estoques importados.
                """
                CREATE TABLE IF NOT EXISTS estoqueXML (
                    idEstoque INT PRIMARY KEY AUTO_INCREMENT,
                    idImportacao INT NOT NULL,
                    nome VARCHAR(150) NOT NULL,
                    dataCriacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_estoqueXML_importacao
                        FOREIGN KEY (idImportacao)
                        REFERENCES importacaoXML(idImportacao)
                )
                """,

                // Produtos pertencentes a cada estoque importado.
                """
                CREATE TABLE IF NOT EXISTS produtoEstoqueXML (
                    idProdutoEstoque INT PRIMARY KEY AUTO_INCREMENT,
                    idEstoque INT NOT NULL,
                    idProduto INT,
                    idCategoria INT,
                    nome VARCHAR(150),
                    codigoDeBarras VARCHAR(50),
                    descricao CLOB,
                    preco DECIMAL(10,2),
                    quantidadeEstoque INT,
                    volumeML INT,

                    CONSTRAINT fk_produtoEstoqueXML_estoque
                        FOREIGN KEY (idEstoque)
                        REFERENCES estoqueXML(idEstoque),

                    CONSTRAINT fk_produtoEstoqueXML_categoria
                        FOREIGN KEY (idCategoria)
                        REFERENCES categoria(idCategoria),

                    CONSTRAINT fk_produtoEstoqueXML_produto
                        FOREIGN KEY (idProduto)
                        REFERENCES produto(idProduto)
                )
                """,
                "ALTER TABLE produtoEstoqueXML ADD COLUMN IF NOT EXISTS idProduto INT",
                "ALTER TABLE produtoEstoqueXML ADD CONSTRAINT IF NOT EXISTS fk_produtoEstoqueXML_produto FOREIGN KEY (idProduto) REFERENCES produto(idProduto)"
        );

        String sqlInserirUsuario = """
            MERGE INTO usuario (nome, sobrenome, email, senha, tipo)
            KEY (email)
            VALUES (?, ?, ?, ?, ?)
            """;

        String inserirCategoriasBase = """
            MERGE INTO categoria (idCategoria, nome, icone)
            KEY (idCategoria)
            VALUES (?, ?, ?)
            """;

        String inserirProdutosBase = """
            INSERT INTO produto
            (idCategoria, nome, codigoDeBarras, descricao, preco, quantidadeEstoque, volumeML)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        Object[][] categoriasBase = {
                {1, "Cervejas", "\uD83C\uDF7A"},
                {2, "Refrigerantes", "\uD83E\uDD64"},
                {3, "Combos", "\uD83C\uDF79"},
                {4, "Sucos", "\uD83E\uDDC3"},
                {5, "Energéticos", "\u26A1"},
                {6, "Águas", "\uD83D\uDCA7"},
                {7, "Vinhos", "\uD83C\uDF77"},
                {8, "Destilados", "\uD83E\uDD43"}
        };

        Object[][] produtosBase = {
                {1, "Cerveja Heineken Long Neck 330ml", "7891000000011", "Cerveja Premium Puro Malte, origem holandesa, sabor suave e refrescante.", 9.90, 120, 330},
                {1, "Cerveja Brahma Chopp Lata 350ml", "7891000000028", "Cerveja Pilsen leve e refrescante, ideal para qualquer ocasião.", 3.80, 300, 350},
                {1, "Cerveja Stella Artois Long Neck 330ml", "7891000000035", "Cerveja lager premium belga, com amargor equilibrado.", 8.90, 150, 330},
                {1, "Cerveja Skol Lata 350ml", "7891000000042", "Cerveja Pilsen leve, a que desce redondo.", 3.50, 400, 350},
                {2, "Coca-Cola Original Lata 350ml", "7891000000059", "Refrigerante de cola sabor original, gelado fica ainda melhor.", 4.50, 250, 350},
                {2, "Guaraná Antarctica 2L", "7891000000066", "Refrigerante de guaraná com sabor marcante e refrescante.", 9.50, 90, 2000},
                {2, "Sprite Limão Lata 350ml", "7891000000073", "Refrigerante de limão transparente e sem corantes.", 4.30, 180, 350},
                {3, "Combo Churrasco - 12 Cervejas + 2 Refrigerantes", "7891000000080", "Kit com 12 cervejas Pilsen lata 350ml e 2 refrigerantes de 2L.", 69.90, 25, 6200},
                {3, "Combo Festa - 6 Long Necks + 1 Whisky", "7891000000097", "Kit com 6 cervejas Heineken Long Neck e 1 whisky 1L.", 199.90, 15, 3000},
                {4, "Suco Del Valle Uva 1L", "7891000000103", "Néctar de uva pronto para beber, sabor intenso.", 7.90, 100, 1000},
                {4, "Suco Del Valle Laranja 1L", "7891000000110", "Néctar de laranja pronto para beber.", 7.50, 110, 1000},
                {5, "Red Bull Energy Drink 250ml", "7891000000127", "Bebida energética que revitaliza corpo e mente.", 11.90, 200, 250},
                {5, "Monster Energy Lata 473ml", "7891000000134", "Energético com sabor marcante e alta cafeína.", 9.90, 140, 473},
                {6, "Água Mineral Crystal sem Gás 500ml", "7891000000141", "Água mineral natural sem gás, leve e pura.", 2.50, 500, 500},
                {6, "Água Mineral Crystal com Gás 500ml", "7891000000158", "Água mineral com gás, refrescante.", 3.00, 320, 500},
                {7, "Vinho Tinto Seco Reserva 750ml", "7891000000165", "Vinho tinto seco, corpo médio, notas de frutas vermelhas.", 45.90, 60, 750},
                {7, "Vinho Branco Suave 750ml", "7891000000172", "Vinho branco suave, aromático e leve.", 39.90, 45, 750},
                {8, "Whisky Escocês Ballantines 12 Anos 1L", "7891000000189", "Whisky escocês envelhecido por 12 anos, sabor encorpado.", 149.90, 30, 1000},
                {8, "Vodka Smirnoff 998ml", "7891000000196", "Vodka tripla destilação, ideal para drinks.", 39.90, 80, 998},
                {8, "Gin Tanqueray London Dry 750ml", "7891000000202", "Gin London Dry clássico, com notas de zimbro e cítricos.", 119.90, 35, 750}
        };

        Object[][] vendasBase = {
                {40, "Concluido", new Object[]{"7891000000011", 2}, new Object[]{"7891000000059", 3}},
                {20, "Concluido", new Object[]{"7891000000127", 4}},
                {5, "Pendente", new Object[]{"7891000000189", 1}},
                {2, "Cancelado", new Object[]{"7891000000165", 2}, new Object[]{"7891000000172", 1}}
        };

        try (Connection connection = ConnectionPoolConfig.getConnection();
             Statement stmt = connection.createStatement()) {

            // Migração: bancos criados antes da correção tinham a PK de importacaoXML
            // escrita "idImportacoa". Renomeia antes de criar estoqueXML (que referencia idImportacao).
            try {
                stmt.execute("ALTER TABLE IF EXISTS importacaoXML "
                        + "ALTER COLUMN IF EXISTS idImportacoa RENAME TO idImportacao");
            } catch (Exception e) {
                System.out.println("Migração de importacaoXML ignorada: " + e.getMessage());
            }

            for (String sql : scriptsCriacao) {
                try {
                    stmt.execute(sql);
                } catch (Exception e) {
                    // Não interrompe o restante da inicialização; mostra qual comando falhou.
                    System.out.println("Erro ao executar script de criação: " + e.getMessage());
                    System.out.println("SQL: " + sql);
                }
            }

            try (PreparedStatement psUsuario = connection.prepareStatement(sqlInserirUsuario)) {
                inserirUsuario(psUsuario, "admin", "admin", "admin@admin.com", "admin123", "Admin");
                inserirUsuario(psUsuario, "João", "Silva", "cliente@cliente.com", "cliente123", "Cliente");
            }

            try (PreparedStatement psCategoria = connection.prepareStatement(inserirCategoriasBase)) {
                for (Object[] c : categoriasBase) {
                    psCategoria.setInt(1, (Integer) c[0]);
                    psCategoria.setString(2, (String) c[1]);
                    psCategoria.setString(3, (String) c[2]);
                    psCategoria.addBatch();
                }
                psCategoria.executeBatch();
            }

            if (tabelaVazia(stmt, "produto")) {
                try (PreparedStatement psProduto = connection.prepareStatement(inserirProdutosBase)) {
                    for (Object[] p : produtosBase) {
                        psProduto.setInt(1, (Integer) p[0]);
                        psProduto.setString(2, (String) p[1]);
                        psProduto.setString(3, (String) p[2]);
                        psProduto.setString(4, (String) p[3]);
                        psProduto.setDouble(5, (Double) p[4]);
                        psProduto.setInt(6, (Integer) p[5]);
                        psProduto.setInt(7, (Integer) p[6]);
                        psProduto.addBatch();
                    }
                    psProduto.executeBatch();
                }
            }

            try (PreparedStatement psRenomear = connection.prepareStatement(
                    "UPDATE produto SET nome = ? WHERE codigoDeBarras = ? AND nome = ?")) {
                psRenomear.setString(1, "Whisky Escocês Ballantines 12 Anos 1L");
                psRenomear.setString(2, "7891000000189");
                psRenomear.setString(3, "Whisky Escocês 12 Anos 1L");
                psRenomear.executeUpdate();
            }

            Map<String, byte[]> imagemAtual = new HashMap<>();

            try (ResultSet rs = stmt.executeQuery("SELECT codigoDeBarras, imagem FROM produto")) {
                while (rs.next()) {
                    imagemAtual.put(rs.getString(1), rs.getBytes(2));
                }
            }

            try (PreparedStatement psImagem = connection.prepareStatement(
                    "UPDATE produto SET imagem = ? WHERE codigoDeBarras = ?")) {

                for (Object[] p : produtosBase) {
                    String codigo = (String) p[2];

                    if (!imagemAtual.containsKey(codigo)) continue;

                    byte[] atual = imagemAtual.get(codigo);
                    byte[] gerada = ImagensBase.gerar();
                    List<byte[]> fotos = ImagensBase.arquivos((String) p[1]);

                    boolean gerenciadaPeloSeed = atual == null
                            || Arrays.equals(atual, gerada)
                            || atual.length == 0
                            || isPlaceholderAntigo(atual)
                            || fotos.stream().anyMatch(f -> Arrays.equals(f, atual));

                    if (!gerenciadaPeloSeed) continue;

                    byte[] nova = fotos.isEmpty() ? gerada : fotos.get(0);

                    if (nova == null || Arrays.equals(nova, atual)) continue;

                    psImagem.setBytes(1, nova);
                    psImagem.setString(2, codigo);
                    psImagem.executeUpdate();
                }
            }

            if (tabelaVazia(stmt, "venda")) {
                inserirVendasBase(connection, vendasBase, "cliente@cliente.com");
            }

        } catch (Exception e) {
            System.out.println("Erro ao iniciar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inserirUsuario(PreparedStatement ps, String nome, String sobrenome,
                                       String email, String senha, String tipo) throws Exception {
        ps.setString(1, nome);
        ps.setString(2, sobrenome);
        ps.setString(3, email);
        ps.setString(4, senha);
        ps.setString(5, tipo);
        ps.executeUpdate();
    }

    private static boolean tabelaVazia(Statement stmt, String tabela) throws Exception {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tabela)) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private static void inserirVendasBase(Connection connection, Object[][] vendasBase, String emailCliente)
            throws Exception {

        int idCliente;

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT idUsuario FROM usuario WHERE email = ?")) {
            ps.setString(1, emailCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;
                idCliente = rs.getInt(1);
            }
        }

        String sqlVenda = "INSERT INTO venda (idUsuario, dataVenda, valorTotal, status) VALUES (?, ?, 0, ?)";

        String sqlItem = """
                INSERT INTO itemVenda (idVenda, idProduto, quantidade, precoUnitario)
                SELECT ?, idProduto, ?, preco FROM produto WHERE codigoDeBarras = ?
                """;

        String sqlTotal = """
                UPDATE venda SET valorTotal =
                    (SELECT COALESCE(SUM(quantidade * precoUnitario), 0) FROM itemVenda WHERE idVenda = ?)
                    + CASE WHEN (SELECT COALESCE(SUM(quantidade * precoUnitario), 0) FROM itemVenda WHERE idVenda = ?) < 50
                           THEN 8.90 ELSE 0 END
                WHERE idVenda = ?
                """;

        try (PreparedStatement psVenda = connection.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psItem = connection.prepareStatement(sqlItem);
             PreparedStatement psTotal = connection.prepareStatement(sqlTotal)) {

            for (Object[] venda : vendasBase) {
                psVenda.setInt(1, idCliente);
                psVenda.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().minusDays((Integer) venda[0])));
                psVenda.setString(3, (String) venda[1]);
                psVenda.executeUpdate();

                int idVenda;

                try (ResultSet chaves = psVenda.getGeneratedKeys()) {
                    chaves.next();
                    idVenda = chaves.getInt(1);
                }

                for (int i = 2; i < venda.length; i++) {
                    Object[] item = (Object[]) venda[i];

                    psItem.setInt(1, idVenda);
                    psItem.setInt(2, (Integer) item[1]);
                    psItem.setString(3, (String) item[0]);
                    psItem.executeUpdate();
                }

                psTotal.setInt(1, idVenda);
                psTotal.setInt(2, idVenda);
                psTotal.setInt(3, idVenda);
                psTotal.executeUpdate();
            }
        }
    }

    /**
     * Verifica imagens antigas geradas pelo seed.
     */
    private static boolean isPlaceholderAntigo(byte[] atual) {
        try {
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(
                    new java.io.ByteArrayInputStream(atual));

            return img != null
                    && img.getWidth() == 400
                    && img.getHeight() == 400
                    && !Arrays.equals(atual, ImagensBase.gerar())
                    && atual.length < 20_000
                    && (img.getRGB(5, 5) & 0xFFFFFF) != 0xFFFFFF
                    && "image/png".equals(
                    br.com.drinkup.utilidades.UtilidadesImagem.tipoPorAssinatura(atual));

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}