package com.vendingMachine.vendingMachine.service;


import com.vendingMachine.vendingMachine.entity.EPI;
import com.vendingMachine.vendingMachine.repository.EPIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EPIService {

    @Autowired
    private EPIRepository epiRepository;

    public EPI insert(EPI epi){
        return epiRepository.save(epi);
    }

    public void atualizarDados(){};

    public List<EPI> listarTodos(){
        return epiRepository.findAll();
    }

    public List<EPI> listarPorTipo(String tipo){
        return epiRepository.listarPorTipo(tipo);
    }

    public void deletarPorId(Long id) {
        epiRepository.deleteById(id);
        System.out.println("EPI com ID " + id + " deletada.");
    }
}
