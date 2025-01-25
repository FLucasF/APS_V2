package model;

import enums.Tipo;

import java.util.ArrayList;
import java.util.List;


public class Participante {
    private Long id;
    private Tipo tipo;
    private String nome;
    private String email;
    private String bio;
    private List<Projeto> projetosList;

    public Participante() {
        this.projetosList = new ArrayList<>();
    }

    public Participante(Long id, String nome, String email, String bio, Tipo tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.email = email;
        this.bio = bio;
        this.projetosList = new ArrayList<>();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<Projeto> getProjetosList() {
        return projetosList;
    }

    public void setProjetosList(List<Projeto> projetosList) {
        this.projetosList = projetosList;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}