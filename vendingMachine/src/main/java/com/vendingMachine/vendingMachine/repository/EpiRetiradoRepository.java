package com.vendingMachine.vendingMachine.repository;

import com.vendingMachine.vendingMachine.entity.EpiRetirado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EpiRetiradoRepository extends JpaRepository<EpiRetirado, Long> {

    List<EpiRetirado> findByPessoaQrCode(String qrCode);

    List<EpiRetirado> findByEpiId(Long epiId);

    List<EpiRetirado> findByDataHoraRetiradaBetween(LocalDateTime inicio, LocalDateTime fim);
}