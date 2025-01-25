package model;
import java.time.LocalDate;

public class Projeto {
    private Long id;
    private String nome;
    private String descricao;
    private Participante participante;
    private LocalDate dataInicio;
    private LocalDate dataEncerramento;
    public Projeto() {
    }

    public Projeto(Long id, String nome, String descricao, Participante participante, LocalDate dataInicio, LocalDate dataEncerramento) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.participante = participante;
        this.dataInicio = dataInicio;
        this.dataEncerramento = dataEncerramento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Participante getParticipante() {
        return participante;
    }

    public void setParticipante(Participante participante) {
        this.participante = participante;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDate dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }
}