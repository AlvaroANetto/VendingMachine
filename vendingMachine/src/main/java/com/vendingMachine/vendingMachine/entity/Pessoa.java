package com.vendingMachine.vendingMachine.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Pessoa {

    @Id
    @Column(name = "QrCode")
    private String qrCode;

    private String cpf;
    private String nome;
    private String curso;

    private Pessoa(){}

    public Pessoa(String qrcode, String cpf, String nome, String curso) {
        this.qrCode = qrcode;
        this.cpf = cpf;
        this.nome = nome;
        this.curso = curso;
    }

    public String getQrcode() {
        return qrCode;
    }

    public void setQrcode(String qrcode) {
        this.qrCode = qrcode;
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

    @Override
    public String toString(){
        return "NOME: " + getNome() + " CÓDIGO: " + getQrcode() + " CURSO: " + getCurso() + " CPF: " + getCpf();
    }
}
