package com.vendingMachine.vendingMachine.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_EPI")
public class EPI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "tipo_EPI")
    private String tipoEpi;

    @OneToMany(mappedBy = "epi", cascade = CascadeType.ALL)
    private List<EpiRetirado> historicoRetirados;

    public EPI() {}

    public EPI(Long id, String tipoEpi) {
        this.id = id;
        this.tipoEpi = tipoEpi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoEpi() {
        return tipoEpi;
    }

    public void setTipoEpi(String tipoEpi) {
        this.tipoEpi = tipoEpi;
    }

    public List<EpiRetirado> getHistoricoRetirados() {
        return historicoRetirados;
    }

    public void setHistoricoRetirados(List<EpiRetirado> historicoRetirados) {
        this.historicoRetirados = historicoRetirados;
    }
}