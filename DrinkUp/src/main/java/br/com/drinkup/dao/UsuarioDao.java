package br.com.drinkup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.drinkup.model.Usuario;
import br.com.drinkup.servlet.config.ConnectionPoolConfig;

public class UsuarioDao {
    
    public void criarUsuario(Usuario usuario) throws SQLException {
        // Implementação para criar um usuário no banco de dados
        String SQL = "INSERT INTO USUARIO (NOME, SOBRENOME, EMAIL, SENHA, TIPO) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection connection = ConnectionPoolConfig.getConnection();
            
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, usuario.getNome());
            preparedStatement.setString(2, usuario.getSobrenome());
            preparedStatement.setString(3, usuario.getEmail());
            preparedStatement.setString(4, usuario.getSenha());
            preparedStatement.setString(5, usuario.getTipo().name());
            preparedStatement.execute();

            System.out.println("Usuário criado com sucesso!");
            connection.close();

        } catch (Exception e) {
            System.out.println("Erro ao criar usuário: " + e.getMessage());
        }
    }
}
