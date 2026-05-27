package com.vendingMachine.vendingMachine.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "tbl_EPI")
public class EPI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo_EPI;

    public EPI(){}

    public EPI(Long id, String tipo_EPI) {
        this.id = id;
        this.tipo_EPI = tipo_EPI;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo_EPI() {
        return tipo_EPI;
    }

    public void setTipo_EPI(String tipo_EPI) {
        this.tipo_EPI = tipo_EPI;
    }
}
