package br.com.drinkup.servlet;

import br.com.drinkup.servlet.config.ConnectionPoolConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

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
                    nome VARCHAR(100)
                )
                """,
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
                    CONSTRAINT fk_produto_categoria FOREIGN KEY (idCategoria) REFERENCES categoria(idCategoria)
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS venda (
                    idVenda INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                    idUsuario INT NOT NULL,
                    dataVenda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    valorTotal DECIMAL(10,2),
                    status ENUM('Pendente', 'Cancelado', 'Concluido'),
                    CONSTRAINT fk_venda_usuario FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS itemVenda (
                    idItemVenda INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                    idVenda INT NOT NULL,
                    idProduto INT NOT NULL,
                    quantidade INT,
                    precoUnitario DECIMAL(10,2),
                    CONSTRAINT fk_itemVenda_venda FOREIGN KEY (idVenda) REFERENCES venda(idVenda),
                    CONSTRAINT fk_itemVenda_produto FOREIGN KEY (idProduto) REFERENCES produto(idProduto)
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS importacaoXML (
                    idImportacao INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                    idUsuarioAdmin INT NOT NULL,
                    nomeArquivo VARCHAR(255),
                    dataImportacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_importacaoXML_usuario FOREIGN KEY (idUsuarioAdmin) REFERENCES usuario(idUsuario)
                )
                """
        );

        String sqlInserirAdmin = """
                MERGE INTO usuario (nome, sobrenome, email, senha, tipo)
                KEY (email)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConnectionPoolConfig.getConnection();
             Statement stmt = connection.createStatement();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlInserirAdmin)) {

            for (String sql : scriptsCriacao) {
                stmt.execute(sql);
            }

            preparedStatement.setString(1, "admin");
            preparedStatement.setString(2, "admin");
            preparedStatement.setString(3, "admin@admin.com");
            preparedStatement.setString(4, "admin123");
            preparedStatement.setString(5, "Admin");
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}