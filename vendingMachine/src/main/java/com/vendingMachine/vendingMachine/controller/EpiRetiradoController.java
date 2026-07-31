package com.vendingMachine.vendingMachine.controller;

import com.vendingMachine.vendingMachine.entity.EpiRetirado;
import com.vendingMachine.vendingMachine.service.EpiRetiradoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Classe auxiliar para receber o JSON do POST de retirada
record RetiradaDTO(String qrCode, Long epiId, Integer quantidade) {}

@RestController
@RequestMapping("/vendingMachine/retiradas")
@CrossOrigin(origins = "*")
public class EpiRetiradoController {

    @Autowired
    private EpiRetiradoService epiRetiradoService;


    // Exemplo de JSON no body: { "qrCode": "12345", "epiId": 1, "quantidade": 2 }
    @PostMapping
    public ResponseEntity<EpiRetirado> registrarRetirada(@RequestBody RetiradaDTO dto) {
        EpiRetirado retirada = epiRetiradoService.registrarRetirada(
                dto.qrCode(),
                dto.epiId(),
                dto.quantidade()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(retirada);
    }


    @GetMapping
    public ResponseEntity<List<EpiRetirado>> listarTodas() {
        return ResponseEntity.ok(epiRetiradoService.listarTodasRetiradas());
    }
}