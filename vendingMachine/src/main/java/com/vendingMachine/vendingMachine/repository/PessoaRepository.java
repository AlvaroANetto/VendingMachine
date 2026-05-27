package com.vendingMachine.vendingMachine.repository;


import com.vendingMachine.vendingMachine.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, String> {

    Pessoa findByqrCode(String qrcode);

}
