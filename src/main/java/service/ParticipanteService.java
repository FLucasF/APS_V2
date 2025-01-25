package service;

import model.Participante;
import repository.ParticipanteRepository;
import repository.ProjetoRepository;

import java.util.List;
import java.util.Optional;

public class ParticipanteService {

    private final ParticipanteRepository participanteRepository = new ParticipanteRepository();

    public void adicionarParticipante(Participante participante) {
        participanteRepository.adicionar(participante);
    }

    public List<Participante> listarParticipantes() {
        return participanteRepository.listarTodos();
    }

    public Optional<Participante> buscarParticipantePorId(Long id) {
        return participanteRepository.buscarPorId(id);
    }

    public void atualizarParticipante(Participante participante) {
        participanteRepository.atualizar(participante);
    }

    public void removerParticipante(Long id) {
        participanteRepository.remover(id);
    }
}