package com.vendingMachine.vendingMachine.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "epi_Retirado")
public class EpiRetirado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    @ManyToOne
    @JoinColumn(name = "epi_id")
    private EPI epi;

    @CreationTimestamp
    @Column(name = "dia_hora", updatable = false)
    private LocalDateTime dataHoraRetirada;

    private Integer quantidade;

    public EpiRetirado() {}

    public EpiRetirado(Pessoa pessoa, EPI epi, Integer quantidade) {
        this.pessoa = pessoa;
        this.epi = epi;
        this.quantidade = quantidade;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public EPI getEpi() {
        return epi;
    }

    public void setEpi(EPI epi) {
        this.epi = epi;
    }

    public LocalDateTime getDataHoraRetirada() {
        return dataHoraRetirada;
    }

    public void setDataHoraRetirada(LocalDateTime dataHoraRetirada) {
        this.dataHoraRetirada = dataHoraRetirada;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}