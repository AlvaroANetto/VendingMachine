package com.vendingMachine.vendingMachine.repository;

import com.vendingMachine.vendingMachine.entity.EPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EPIRepository extends JpaRepository<EPI, Long> {

    List<EPI> findByTipoEpi(String tipoEpi);
}