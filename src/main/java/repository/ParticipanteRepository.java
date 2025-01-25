package repository;
import config.DatabaseConnection;
import enums.Tipo;
import model.Participante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipanteRepository {

    public void adicionar(Participante participante) {
        String sql = "INSERT INTO participantes (nome, email, tipo, bio) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, participante.getNome());
            statement.setString(2, participante.getEmail());
            statement.setString(3, participante.getTipo().name());
            statement.setString(4, participante.getBio());
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                participante.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir participante.", e);
        }
    }

    public List<Participante> listarTodos() {
        String sql = "SELECT id, nome, email, tipo, bio FROM participantes";
        List<Participante> participantes = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Participante participante = new Participante();
                participante.setId(resultSet.getLong("id"));
                participante.setNome(resultSet.getString("nome"));
                participante.setEmail(resultSet.getString("email"));
                participante.setBio(resultSet.getString("bio"));

                // Conversão da String para Tipo
                String tipoString = resultSet.getString("tipo");
                if (tipoString != null) {
                    participante.setTipo(Tipo.valueOf(tipoString));
                }

                participantes.add(participante);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar participantes.", e);
        }
        return participantes;
    }


    public Optional<Participante> buscarPorId(Long id) {
        String sql = "SELECT id, nome, email, tipo, bio FROM participantes WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Participante participante = new Participante();
                participante.setId(resultSet.getLong("id"));
                participante.setNome(resultSet.getString("nome"));
                participante.setEmail(resultSet.getString("email"));
                participante.setBio(resultSet.getString("bio"));

                // Conversão da String para Tipo
                String tipoString = resultSet.getString("tipo");
                if (tipoString != null) {
                    participante.setTipo(Tipo.valueOf(tipoString));
                }

                return Optional.of(participante);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar participante.", e);
        }
        return Optional.empty();
    }


    public void atualizar(Participante participante) {
        String sql = "UPDATE participantes SET nome = ?, email = ?, tipo = ?, bio = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, participante.getNome());
            statement.setString(2, participante.getEmail());
            statement.setString(3, participante.getTipo().name());
            statement.setString(4, participante.getBio());
            statement.setLong(5, participante.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar participante.", e);
        }
    }

    public void remover(Long id) {
        String sql = "DELETE FROM participantes WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover participante.", e);
        }
    }
}