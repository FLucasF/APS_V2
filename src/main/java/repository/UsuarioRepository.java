package repository;

import config.DatabaseConnection;
import model.Usuario;

import java.sql.*;
import java.util.Optional;

public class UsuarioRepository {

    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT login, nome, senha FROM usuarios WHERE login = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Usuario usuario = new Usuario();
                usuario.setLogin(resultSet.getString("login"));
                usuario.setNome(resultSet.getString("nome"));
                usuario.setSenha(resultSet.getString("senha"));
                return Optional.of(usuario);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário.", e);
        }
        return Optional.empty();
    }


    public void adicionarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (login, nome, senha) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, usuario.getLogin());
            statement.setString(2, usuario.getNome());
            statement.setString(3, usuario.getSenha());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar usuário.", e);
        }
    }

}
