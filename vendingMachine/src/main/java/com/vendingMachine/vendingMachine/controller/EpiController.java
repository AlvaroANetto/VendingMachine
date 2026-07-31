package com.vendingMachine.vendingMachine.controller;


import com.vendingMachine.vendingMachine.entity.EPI;
import com.vendingMachine.vendingMachine.service.EPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendingMachine/epi")
@CrossOrigin(origins = "*")
public class EpiController {
    @Autowired
    private EPIService epiService;

    @GetMapping
    public ResponseEntity<List<EPI>> listar(@RequestParam String tipo_EPI) {
        if (tipo_EPI != ""){
            return ResponseEntity.status(200).body(epiService.listarTodos());
        } else {
            return ResponseEntity.status(200).body(epiService.listarPorTipo(tipo_EPI));
        }
    }
}
