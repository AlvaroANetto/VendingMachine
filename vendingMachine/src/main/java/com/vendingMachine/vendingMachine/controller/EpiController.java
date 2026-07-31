package com.vendingMachine.vendingMachine.controller;

import com.vendingMachine.vendingMachine.entity.EPI;
import com.vendingMachine.vendingMachine.service.EPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendingMachine/epi")
@CrossOrigin(origins = "*")
public class EpiController {

    @Autowired
    private EPIService epiService;

    // Se passar ?tipoEpi=Luva filtra por tipo, senão traz todos
    @GetMapping
    public ResponseEntity<List<EPI>> listar(@RequestParam(required = false) String tipoEpi) {
        if (tipoEpi != null) {
            return ResponseEntity.ok(epiService.listarPorTipo(tipoEpi));
        }
        return ResponseEntity.ok(epiService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<EPI> salvar(@RequestBody EPI epi) {
        EPI novoEpi = epiService.insert(epi);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoEpi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        epiService.deletarPorId(id);
        return ResponseEntity.status(204).body("Deletado");
    }
}