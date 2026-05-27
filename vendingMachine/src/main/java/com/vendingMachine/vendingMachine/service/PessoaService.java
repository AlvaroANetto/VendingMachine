package com.vendingMachine.vendingMachine.service;


import com.vendingMachine.vendingMachine.entity.Pessoa;
import com.vendingMachine.vendingMachine.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository repository;

    public Pessoa insert(Pessoa p){
        return repository.save(p);
    }

    public Pessoa encontrarQrcode(String qrcode){
        return repository.findByqrCode(qrcode);
    }

    public Pessoa atualizarDados(String qrcode, Pessoa pessoaNova){
        Pessoa pessoaTemp = repository.findByqrCode(qrcode);
        pessoaTemp.setCpf(pessoaNova.getCpf());
        pessoaTemp.setNome(pessoaNova.getNome());
        pessoaTemp.setCurso(pessoaNova.getCurso());
        return repository.save(pessoaTemp);
    }

    public List<Pessoa> listaTodos(){
        return repository.findAll();
    }

    public void deletar(String qrcode){
        repository.delete(encontrarQrcode(qrcode));
    }



}
