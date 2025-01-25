package repository;


import model.Participante;
import model.Projeto;
import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjetoRepository {

    public void adicionar(Projeto projeto) {
        String sql = "INSERT INTO projetos (nome, descricao, participante_id, data_inicio, data_encerramento) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, projeto.getNome());
            statement.setString(2, projeto.getDescricao());
            statement.setLong(3, projeto.getParticipante().getId());
            statement.setDate(4, Date.valueOf(projeto.getDataInicio()));
            statement.setDate(5, Date.valueOf(projeto.getDataEncerramento()));
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                projeto.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir projeto.", e);
        }
    }

    public List<Projeto> listarTodos() {
        String sql = "SELECT p.*, c.id as participante_id, c.nome as participante_nome " +
                "FROM projetos p LEFT JOIN participantes c ON p.participante_id = c.id";
        List<Projeto> projetos = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Projeto projeto = new Projeto();
                projeto.setId(resultSet.getLong("id"));
                projeto.setNome(resultSet.getString("nome"));
                projeto.setDescricao(resultSet.getString("descricao"));
                projeto.setDataInicio(resultSet.getDate("data_inicio").toLocalDate());
                projeto.setDataEncerramento(resultSet.getDate("data_encerramento").toLocalDate());

                Participante participante = new Participante();
                participante.setId(resultSet.getLong("participante_id"));
                participante.setNome(resultSet.getString("participante_nome"));

                projeto.setParticipante(participante);
                projetos.add(projeto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar projetos: " + e.getMessage(), e);
        }

        return projetos;
    }

    public Optional<Projeto> buscarPorId(Long id) {
        String sql = "SELECT * FROM projetos WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Projeto projeto = new Projeto();
                projeto.setId(resultSet.getLong("id"));
                projeto.setNome(resultSet.getString("nome"));
                projeto.setDescricao(resultSet.getString("descricao"));
                projeto.setDataInicio(resultSet.getDate("data_inicio").toLocalDate());
                projeto.setDataEncerramento(resultSet.getDate("data_encerramento").toLocalDate());
                return Optional.of(projeto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar projeto.", e);
        }

        return Optional.empty();
    }

    public void atualizar(Projeto projeto) {
        String sqlAtualizarProjeto = "UPDATE projetos SET nome = ?, descricao = ?, data_inicio = ?, data_encerramento = ?, participante_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlAtualizarProjeto)) {

            statement.setString(1, projeto.getNome());
            statement.setString(2, projeto.getDescricao());
            statement.setDate(3, Date.valueOf(projeto.getDataInicio()));
            statement.setDate(4, Date.valueOf(projeto.getDataEncerramento()));
            statement.setLong(5, projeto.getParticipante().getId());
            statement.setLong(6, projeto.getId());

            statement.executeUpdate();

            atualizarAssociacaoParticipanteProjeto(connection, projeto);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar projeto", e);
        }
    }

    private void atualizarAssociacaoParticipanteProjeto(Connection connection, Projeto projeto) {
        String sqlDeleteAssociacaoAntiga = "DELETE FROM participantes_projetos WHERE projeto_id = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(sqlDeleteAssociacaoAntiga)) {
            deleteStatement.setLong(1, projeto.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover associação antiga", e);
        }

        String sqlInsertAssociacao = "INSERT INTO participantes_projetos (participante_id, projeto_id) VALUES (?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sqlInsertAssociacao)) {
            insertStatement.setLong(1, projeto.getParticipante().getId());
            insertStatement.setLong(2, projeto.getId());
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir nova associação", e);
        }
    }


    public void remover(Long id) {
        String sql = "DELETE FROM projetos WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhum projeto foi removido. Verifique se o ID está correto.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover projeto: " + e.getMessage(), e);
        }
    }

    public List<Projeto> listarProjetosPorParticipante(Long participanteId) {
        String sql = "SELECT p.* FROM projetos p " +
                "JOIN participantes_projetos pp ON p.id = pp.projeto_id " +
                "WHERE pp.participante_id = ?";
        List<Projeto> projetos = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, participanteId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Projeto projeto = new Projeto();
                projeto.setId(resultSet.getLong("id"));
                projeto.setNome(resultSet.getString("nome"));
                projeto.setDescricao(resultSet.getString("descricao"));
                projeto.setDataInicio(resultSet.getDate("data_inicio").toLocalDate());
                projeto.setDataEncerramento(resultSet.getDate("data_encerramento").toLocalDate());
                projetos.add(projeto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar projetos do participante.", e);
        }

        return projetos;
    }

    public void adicionarParticipanteAoProjeto(Long participanteId, Long projetoId) {
        String sql = "INSERT INTO participantes_projetos (participante_id, projeto_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, participanteId);
            statement.setLong(2, projetoId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao associar participante ao projeto.", e);
        }
    }
}