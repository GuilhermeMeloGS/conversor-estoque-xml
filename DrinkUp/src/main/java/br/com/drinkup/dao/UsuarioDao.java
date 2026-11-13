package br.com.drinkup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.drinkup.model.TipoUsuario;
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

    public List<Usuario> mostrarUsuarios(){
        String SQL = "SELECT * FROM USUARIO";

        try{
            Connection connection = ConnectionPoolConfig.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Usuario> usuarios = new ArrayList<>();
            while(resultSet.next()){
                Usuario usuario = new Usuario(
                    resultSet.getInt("IDUSUARIO"),
                    resultSet.getString("NOME"),
                    resultSet.getString("SOBRENOME"),
                    resultSet.getString("EMAIL"),
                    resultSet.getString("SENHA"),
                    TipoUsuario.valueOf(resultSet.getString("TIPO")),
                    resultSet.getObject("CRIADOEM", LocalDateTime.class)
                );
                usuarios.add(usuario);
            }

            connection.close();
            return usuarios;

        } catch (Exception e) {
            System.out.println("Erro ao buscar usuários: " + e.getMessage());
            return null;
        }

    }

    public Usuario logarUsuario(String email, String senha){
        String SQL = "SELECT * FROM USUARIO WHERE EMAIL = ? AND SENHA = ?";

        try{
            Connection connection = ConnectionPoolConfig.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, senha);

            try(ResultSet resultSet = preparedStatement.executeQuery()){;
                if(resultSet.next()){
                    return new Usuario(

                        resultSet.getInt("IDUSUARIO"),
                        resultSet.getString("NOME"),
                        resultSet.getString("SOBRENOME"),
                        resultSet.getString("EMAIL"),
                        resultSet.getString("SENHA"),
                        TipoUsuario.valueOf(resultSet.getString("TIPO")),
                        resultSet.getObject("CRIADOEM", LocalDateTime.class)
                    );
                }
                return null;
        }

        } catch (Exception e) {
            System.out.println("Erro ao logar usuário: " + e.getMessage());
            return null;
        }
    }

    public Usuario buscarPorId(int id){
        String SQL = "SELECT * FROM USUARIO WHERE IDUSUARIO = ?";

        try(Connection connection = ConnectionPoolConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL)){

            preparedStatement.setInt(1, id);

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    return new Usuario(
                        resultSet.getInt("IDUSUARIO"),
                        resultSet.getString("NOME"),
                        resultSet.getString("SOBRENOME"),
                        resultSet.getString("EMAIL"),
                        resultSet.getString("SENHA"),
                        TipoUsuario.valueOf(resultSet.getString("TIPO")),
                        resultSet.getObject("CRIADOEM", LocalDateTime.class)
                    );
                }
                return null;
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar usuário por ID: " + e.getMessage());
            return null;
        }
    }

    public boolean atualizarUsuario(int id, String nome, String sobrenome, String email, String senha){
        String SQL = "UPDATE USUARIO SET NOME = ?, SOBRENOME = ?, EMAIL = ?, SENHA = ? WHERE IDUSUARIO = ?";

        try(Connection connection = ConnectionPoolConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL)){

            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, sobrenome);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, senha);
            preparedStatement.setInt(5, id);

            return preparedStatement.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean deletarUsuario(int id){
        String SQL = "DELETE FROM USUARIO WHERE IDUSUARIO = ?";

        try(Connection connection = ConnectionPoolConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL)){

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Erro ao deletar usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarTipo(int id, TipoUsuario tipo) {
    String SQL = "UPDATE USUARIO SET TIPO = ? WHERE IDUSUARIO = ?";

    try (Connection connection = ConnectionPoolConfig.getConnection();
    PreparedStatement ps = connection.prepareStatement(SQL)) {

        ps.setString(1, tipo.name());
        ps.setInt(2, id);

        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        System.out.println("Erro ao atualizar tipo do usuário: " + e.getMessage());
        return false;
    }
}
}
