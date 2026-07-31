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

    public Pessoa insert(Pessoa p) {
        return repository.save(p);
    }

    public Pessoa encontrarQrcode(String qrcode) {
        return repository.findByQrCode(qrcode);
    }

    public Pessoa atualizarDados(String qrcode, Pessoa pessoaNova) {
        Pessoa pessoaTemp = repository.findByQrCode(qrcode);
        if (pessoaTemp != null) {
            pessoaTemp.setCpf(pessoaNova.getCpf());
            pessoaTemp.setNome(pessoaNova.getNome());
            pessoaTemp.setCurso(pessoaNova.getCurso());
            return repository.save(pessoaTemp);
        }
        throw new RuntimeException("Pessoa não encontrada para o QR Code: " + qrcode);
    }

    public List<Pessoa> listaTodos() {
        return repository.findAll();
    }

    public void deletar(String qrcode) {
        Pessoa pessoa = encontrarQrcode(qrcode);
        if (pessoa != null) {
            repository.delete(pessoa);
        }
    }
}