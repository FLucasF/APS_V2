package controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ProjetoService;
import service.ParticipanteService;
import model.Projeto;
import model.Participante;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ProjetoController {
    private static final Logger log = LoggerFactory.getLogger(ProjetoController.class);
    private final ProjetoService projetoService = new ProjetoService();
    private final ParticipanteService participanteService = new ParticipanteService();

    public void registerRoutes(Javalin app) {
        app.get("/projetos/novo", this::formNovoProjeto);
        app.post("/projetos", this::createProjeto);

        app.get("/projetos/{id}/editar", this::formEditarProjeto);
        app.post("/projetos/update/{id}", this::updateProjeto);

        app.get("/projetos/getAll", this::getAllProjetos);
        app.get("/projetos/delete/{id}", this::deleteProjeto);
        app.get("/projetos/listar", this::listarProjetosHtml);
    }

    private void createProjeto(Context ctx) {
        try {
            String nome = ctx.formParam("nome");
            String descricao = ctx.formParam("descricao");
            String dataInicio = ctx.formParam("dataInicio");
            String dataEncerramento = ctx.formParam("dataEncerramento");
            Long participanteId = Long.parseLong(ctx.formParam("participanteId"));

            Participante participante = participanteService.buscarParticipantePorId(participanteId)
                    .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado com o ID: " + participanteId));

            Projeto projeto = new Projeto();
            projeto.setNome(nome);
            projeto.setDescricao(descricao);
            projeto.setDataInicio(LocalDate.parse(dataInicio));
            projeto.setDataEncerramento(LocalDate.parse(dataEncerramento));
            projeto.setParticipante(participante);

            projetoService.adicionarProjeto(projeto);

            ctx.redirect("/projetos/listar");
        } catch (Exception e) {
            log.error("Erro ao criar projeto", e);
            ctx.status(500).result("Erro ao criar projeto: " + e.getMessage());
        }
    }

    private void formNovoProjeto(Context ctx) {
        try {
            List<Participante> participantes = participanteService.listarParticipantes();
            ctx.render("/projeto/adicionar-projeto.html", Map.of("participantes", participantes));
        } catch (Exception e) {
            log.error("Erro ao carregar formulário de novo projeto", e);
            ctx.status(500).result("Erro ao carregar formulário de novo projeto: " + e.getMessage());
        }
    }

    private void getAllProjetos(Context ctx) {
        try {
            List<Projeto> projetos = projetoService.listarProjetos();
            ctx.render("/projeto/listar-projeto.html", Map.of("projetos", projetos));
        } catch (Exception e) {
            log.error("Erro ao listar projetos", e);
            ctx.status(500).result("Erro ao listar projetos: " + e.getMessage());
        }
    }

    private void listarProjetosHtml(Context ctx) {
        try {
            List<Projeto> projetos = projetoService.listarProjetos();
            ctx.render("/projeto/listar-projeto.html", Map.of("projetos", projetos));
        } catch (Exception e) {
            log.error("Erro ao carregar listagem de projetos", e);
            ctx.status(500).result("Erro ao carregar listagem de projetos: " + e.getMessage());
        }
    }

    private void updateProjeto(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            String nome = ctx.formParam("nome");
            String descricao = ctx.formParam("descricao");
            String dataInicio = ctx.formParam("dataInicio");
            String dataEncerramento = ctx.formParam("dataEncerramento");
            Long participanteId = Long.parseLong(ctx.formParam("participanteId"));

            if (nome == null || descricao == null || dataInicio == null || dataEncerramento == null || participanteId == null) {
                throw new IllegalArgumentException("Todos os campos são obrigatórios.");
            }

            Participante participante = participanteService.buscarParticipantePorId(participanteId)
                    .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado com o ID: " + participanteId));

            Projeto projeto = new Projeto();
            projeto.setId(id);
            projeto.setNome(nome);
            projeto.setDescricao(descricao);
            projeto.setDataInicio(LocalDate.parse(dataInicio));
            projeto.setDataEncerramento(LocalDate.parse(dataEncerramento));
            projeto.setParticipante(participante);

            projetoService.atualizarProjeto(projeto);

            ctx.redirect("/projetos/listar");
        } catch (Exception e) {
            log.error("Erro ao atualizar projeto", e);
            ctx.status(500).result("Erro ao atualizar projeto: " + e.getMessage());
        }
    }

    private void formEditarProjeto(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Projeto projeto = projetoService.buscarProjetoPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));

            List<Participante> participantes = participanteService.listarParticipantes();
            ctx.render("/projeto/editar-projeto.html", Map.of("projeto", projeto, "participantes", participantes));
        } catch (Exception e) {
            log.error("Erro ao carregar formulário de edição", e);
            ctx.status(500).result("Erro ao carregar formulário de edição: " + e.getMessage());
        }
    }

    private void deleteProjeto(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));

            projetoService.removerProjeto(id);

            ctx.redirect("/projetos/listar");
        } catch (Exception e) {
            log.error("Erro ao remover projeto", e);
            ctx.status(500).result("Erro ao remover projeto: " + e.getMessage());
        }
    }
}
