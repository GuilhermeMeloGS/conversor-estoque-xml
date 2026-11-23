package br.com.drinkup.dao;

import br.com.drinkup.model.Categoria;
import br.com.drinkup.servlet.config.ConnectionPoolConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {

    public List<Categoria> listarCategorias() throws SQLException {
        String sql = "SELECT idCategoria, nome, icone FROM categoria ORDER BY idCategoria";
        List<Categoria> categorias = new ArrayList<>();

        try (Connection connection = ConnectionPoolConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                categorias.add(new Categoria(
                        resultado.getInt("idCategoria"),
                        resultado.getString("nome"),
                        resultado.getString("icone")));
            }
        }
        return categorias;
    }
}
