package com.vendingMachine.vendingMachine;

import com.vendingMachine.vendingMachine.entity.Pessoa;
import com.vendingMachine.vendingMachine.service.EPIService;
import com.vendingMachine.vendingMachine.service.GerencInterfaceService;
import com.vendingMachine.vendingMachine.service.GerenciadorService;
import com.vendingMachine.vendingMachine.service.PessoaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

/*
		    pService.insert(new Pessoa("90000000001615847200", "12345632165", "Alvaro", "2IDS"));
			pService.insert(new Pessoa("90000000001613524430", "12345632166", "Jasmine", "2IDS"));
			pService.insert(new Pessoa("90000000001624111546", "12345632167", "Flavia", "2IDS"));
			pService.insert(new Pessoa("90000000001624357293", "12345632168", "Gustavo", "2IDS"));
			pService.insert(new Pessoa("90000000001624463270", "12345632169", "Emanuelli", "2IDS"));
			pService.insert(new Pessoa("90000000001624348116", "12345632170", "Emanuella", "2IDS"));
			pService.insert(new Pessoa("90000000001624242660", "12345632171", "Gabriel", "2IDS"));
			pService.insert(new Pessoa("90000000001610063610", "12345632172", "Alany", "2IDS"));
			pService.insert(new Pessoa("90000000001611183563", "12345632173", "Carlos", "2IDS"));
			pService.insert(new Pessoa("90000000001624151120", "12345632174", "Ester", "2IDS"));
			pService.insert(new Pessoa("90000000001618332780", "12345632175", "Gabrelle", "2IDS"));
			pService.insert(new Pessoa("90000000001611893700", "12345632176", "Ana", "2IDS"));
*/

@SpringBootApplication
public class VendingMachineApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(VendingMachineApplication.class)
                .headless(false) //(Swing)
                .run(args);
    }

/*
    @Bean
    CommandLineRunner run(PessoaService pService, EPIService eService, GerenciadorService gService, GerencInterfaceService gIService) {
        return args -> {
            Scanner scr = new Scanner(System.in);
            // pService.listaTodos().forEach(p -> System.out.println(p.toString()));
            // System.out.println(gService.encontrarPorQrCodeDireto("Crachas/3.jpg"));
            // gService.tirarFoto("Crahas/");
            int opcao;
            gIService.verCamera();
            while (true) {
                gIService.menu();
                opcao = scr.nextInt();
                System.out.println(opcao);
                if (opcao == 0) {
                    System.exit(0);
                } else {
                    System.out.println(gService.encontrarPorQrCodeImagem());
                }

            }
            //gIService.verCamera();
        };
    }*/
}
