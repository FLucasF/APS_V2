package service;

import model.Projeto;
import repository.ParticipanteRepository;
import repository.ProjetoRepository;

import java.util.List;
import java.util.Optional;

public class ProjetoService {

    private final ProjetoRepository projetoRepository = new ProjetoRepository();
    private final ParticipanteRepository participanteRepository = new ParticipanteRepository();


    public List<Projeto> listarProjetos() {
        return projetoRepository.listarTodos();
    }

    public Optional<Projeto> buscarProjetoPorId(Long id) {
        return projetoRepository.buscarPorId(id);
    }

    public void atualizarProjeto(Projeto projeto) {
        projetoRepository.atualizar(projeto);
    }

    public void removerProjeto(Long id) {
        projetoRepository.remover(id);
    }

    public void adicionarProjeto(Projeto projeto) {
        projetoRepository.adicionar(projeto);

        if (projeto.getParticipante() != null) {
            Long participanteId = projeto.getParticipante().getId();
            Long projetoId = projeto.getId();

            projetoRepository.adicionarParticipanteAoProjeto(participanteId, projetoId);
        }
    }

    public List<Projeto> listarProjetosDoParticipante(Long participanteId) {
        return projetoRepository.listarProjetosPorParticipante(participanteId);
    }
}