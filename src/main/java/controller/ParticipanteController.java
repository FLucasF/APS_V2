package controller;

import java.util.List;
import java.util.Map;

import enums.Tipo;
import io.javalin.Javalin;
import model.Participante;
import model.Projeto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ParticipanteService;
import io.javalin.http.Context;
import service.ProjetoService;

public class ParticipanteController {
    private static final Logger log = LoggerFactory.getLogger(ParticipanteController.class);
    private final ParticipanteService participanteService = new ParticipanteService();
    private final ProjetoService projetoService = new ProjetoService();

    public void registerRoutes(Javalin app) {
        app.post("/participantes", this::createParticipante);
        app.get("/participantes/get/{id}", this::getParticipante);
        app.get("/participantes/getAll", this::getAllParticipantes);
        app.post("/participantes/update/{id}", this::updateParticipante);
        app.get("/participantes/delete/{id}", this::deleteParticipante);
        app.get("/participantes/novo", this::formNovoParticipante);
        app.get("/participantes/{id}/editar", this::formEditarParticipante);
        app.get("/participantes/listar", this::listarParticipantes);
    }

    public void listarParticipantes(Context ctx) {
        List<Participante> participantes = participanteService.listarParticipantes();

        // Para cada participante, buscar os projetos associados
        for (Participante participante : participantes) {
            List<Projeto> projetos = projetoService.listarProjetosDoParticipante(participante.getId());
            participante.setProjetosList(projetos);  // Associando a lista de projetos ao participante
        }

        ctx.attribute("participantes", participantes);
        ctx.render("/participante/listar-participantes.html");  // Renderizando o template
    }

    private void formNovoParticipante(Context ctx) {
        try {
            ctx.render("/participante/adicionar-participante.html", Map.of(
                    "tipos", enums.Tipo.values()
            ));
        } catch (Exception e) {
            log.error("Erro ao carregar formulário de novo participante", e);
            ctx.status(500).result("Erro ao carregar formulário de novo participante: " + e.getMessage());
        }
    }

    private void formEditarParticipante(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Participante participante = participanteService.buscarParticipantePorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado"));

            // Disponibiliza o enum Tipo para o template
            ctx.render("/participante/editar-participante.html", Map.of(
                    "participante", participante,
                    "tipos", Tipo.values()
            ));
        } catch (Exception e) {
            log.error("Erro ao carregar formulário de edição", e);
            ctx.status(500).result("Erro ao carregar formulário de edição: " + e.getMessage());
        }
    }


    private void createParticipante(Context ctx) {
        try {
            String nome = ctx.formParam("nome");
            String email = ctx.formParam("email");
            String bio = ctx.formParam("bio");
            String tipoParam = ctx.formParam("tipo");

            if (nome == null || email == null || tipoParam == null) {
                throw new IllegalArgumentException("Nome, email e tipo são obrigatórios.");
            }

            Tipo tipo = Tipo.valueOf(tipoParam); // Converte a string para o enum

            Participante participante = new Participante();
            participante.setNome(nome);
            participante.setEmail(email);
            participante.setBio(bio);
            participante.setTipo(tipo);

            participanteService.adicionarParticipante(participante);

            ctx.redirect("/participantes/listar");
        } catch (Exception e) {
            log.error("Erro ao criar participante", e);
            ctx.status(500).result("Erro ao criar participante: " + e.getMessage());
        }
    }


    private void getParticipante(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Participante participante = participanteService.buscarParticipantePorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado"));

            ctx.render("/participante/detalhes-participante.html", Map.of("participante", participante));
        } catch (Exception e) {
            log.error("Erro ao buscar participante", e);
            ctx.status(500).result("Erro ao buscar participante: " + e.getMessage());
        }
    }

    private void getAllParticipantes(Context ctx) {
        try {
            List<Participante> participantes = participanteService.listarParticipantes();
            ctx.render("/participante/listar-participantes.html", Map.of("participantes", participantes));
        } catch (Exception e) {
            log.error("Erro ao listar participantes", e);
            ctx.status(500).result("Erro ao listar participantes: " + e.getMessage());
        }
    }

    private void updateParticipante(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            String nome = ctx.formParam("nome");
            String email = ctx.formParam("email");
            String bio = ctx.formParam("bio");
            String tipoParam = ctx.formParam("tipo");

            if (nome == null || email == null || tipoParam == null) {
                throw new IllegalArgumentException("Nome, email e tipo são obrigatórios.");
            }

            Tipo tipo = Tipo.valueOf(tipoParam);

            Participante participante = new Participante();
            participante.setId(id);
            participante.setNome(nome);
            participante.setEmail(email);
            participante.setBio(bio);
            participante.setTipo(tipo);

            participanteService.atualizarParticipante(participante);

            ctx.redirect("/participantes/listar");
        } catch (Exception e) {
            log.error("Erro ao atualizar participante", e);
            ctx.status(500).result("Erro ao atualizar participante: " + e.getMessage());
        }
    }


    private void deleteParticipante(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));

            // Removendo o participante
            participanteService.removerParticipante(id);

            // Redireciona para a página de listagem
            ctx.redirect("/participantes/listar");
        } catch (Exception e) {
            log.error("Erro ao remover participante", e);
            ctx.status(500).result("Erro ao remover participante: " + e.getMessage());
        }
    }
}
