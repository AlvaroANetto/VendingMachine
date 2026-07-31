package com.vendingMachine.vendingMachine.controller;

import com.vendingMachine.vendingMachine.entity.Pessoa;
import com.vendingMachine.vendingMachine.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendingMachine/pessoa")
@CrossOrigin(origins = "*")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public ResponseEntity<List<Pessoa>> listarTodos() {
        return ResponseEntity.ok(pessoaService.listaTodos());
    }

    @GetMapping("/{qrCode}")
    public ResponseEntity<Pessoa> buscarPorQrCode(@PathVariable String qrCode) {
        Pessoa pessoa = pessoaService.encontrarQrcode(qrCode);
        return ResponseEntity.ok(pessoa);
    }

    @PostMapping
    public ResponseEntity<Pessoa> salvar(@RequestBody Pessoa pessoa) {
        Pessoa novaPessoa = pessoaService.insert(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPessoa);
    }

    @PutMapping("/{qrCode}")
    public ResponseEntity<Pessoa> atualizar(@PathVariable String qrCode, @RequestBody Pessoa pessoaAtualizada) {
        Pessoa pessoa = pessoaService.atualizarDados(qrCode, pessoaAtualizada);
        return ResponseEntity.ok(pessoa);
    }

    @DeleteMapping("/{qrCode}")
    public ResponseEntity<Void> deletar(@PathVariable String qrCode) {
        pessoaService.deletar(qrCode);
        return ResponseEntity.noContent().build();
    }
}