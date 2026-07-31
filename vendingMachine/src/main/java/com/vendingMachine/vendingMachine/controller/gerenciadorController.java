package com.vendingMachine.vendingMachine.controller;

import com.vendingMachine.vendingMachine.entity.EPI;
import com.vendingMachine.vendingMachine.entity.Pessoa;
import com.vendingMachine.vendingMachine.repository.PessoaRepository;
import com.vendingMachine.vendingMachine.service.EPIService;
import com.vendingMachine.vendingMachine.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendingMachine")
@CrossOrigin(origins = "*")
public class gerenciadorController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public List<Pessoa> listarTodos(){
        return pessoaService.listaTodos();
    }

    @PostMapping
    public ResponseEntity<Pessoa> salvar(@RequestBody Pessoa pessoa){
        return ResponseEntity.status(200).body(pessoaService.insert(pessoa));
    }

    @DeleteMapping("/{qrCode}")
    public void deletar(@PathVariable String qrCode){
         pessoaService.deletar(qrCode);
    }

    @PutMapping("/{qrCode}")
    public Pessoa atualizar(@PathVariable String qrcode, @RequestBody Pessoa pessoaAtualizada){
        return pessoaService.atualizarDados(qrcode,pessoaAtualizada);
    }

}
