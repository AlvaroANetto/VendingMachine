package com.vendingMachine.vendingMachine.repository;


import com.vendingMachine.vendingMachine.entity.EPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EPIRepository extends JpaRepository<EPI, Long> {

}
