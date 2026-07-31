package com.vendingMachine.vendingMachine.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Pessoa {

    @Id
    @Column(name = "QrCode")
    private String qrCode;

    private String cpf;
    private String nome;
    private String curso;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL)
    private List<EpiRetirado> epiRetirados;


    public Pessoa() {}

    public Pessoa(String qrCode, String cpf, String nome, String curso) {
        this.qrCode = qrCode;
        this.cpf = cpf;
        this.nome = nome;
        this.curso = curso;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public List<EpiRetirado> getEpiRetirados() {
        return epiRetirados;
    }

    public void setEpiRetirados(List<EpiRetirado> epiRetirados) {
        this.epiRetirados = epiRetirados;
    }

    @Override
    public String toString() {
        return "NOME: " + getNome() + " CÓDIGO: " + getQrCode() + " CURSO: " + getCurso() + " CPF: " + getCpf();
    }
}