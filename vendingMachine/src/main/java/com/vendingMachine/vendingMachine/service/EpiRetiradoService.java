package com.vendingMachine.vendingMachine.service;

import com.vendingMachine.vendingMachine.entity.EPI;
import com.vendingMachine.vendingMachine.entity.EpiRetirado;
import com.vendingMachine.vendingMachine.entity.Pessoa;
import com.vendingMachine.vendingMachine.repository.EPIRepository;
import com.vendingMachine.vendingMachine.repository.EpiRetiradoRepository;
import com.vendingMachine.vendingMachine.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpiRetiradoService {

    @Autowired
    private EpiRetiradoRepository epiRetiradoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EPIRepository epiRepository;

    // Método principal para registrar a retirada do EPI pelo QR Code
    public EpiRetirado registrarRetirada(String qrCode, Long epiId, Integer quantidade) {
        Pessoa pessoa = pessoaRepository.findByQrCode(qrCode);
        if (pessoa == null) {
            throw new RuntimeException("Pessoa/Cliente não encontrado para o QR Code: " + qrCode);
        }

        EPI epi = epiRepository.findById(epiId)
                .orElseThrow(() -> new RuntimeException("EPI não encontrado para o ID: " + epiId));

        EpiRetirado retirada = new EpiRetirado(pessoa, epi, quantidade);

        // Salva a retirada. O @CreationTimestamp preenche a dataHoraRetirada automaticamente no DB
        return epiRetiradoRepository.save(retirada);
    }

    public List<EpiRetirado> listarTodasRetiradas() {
        return epiRetiradoRepository.findAll();
    }
}