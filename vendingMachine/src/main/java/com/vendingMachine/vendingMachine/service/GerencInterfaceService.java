package com.vendingMachine.vendingMachine.service;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.vendingMachine.vendingMachine.repository.EPIRepository;
import com.vendingMachine.vendingMachine.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.JFrame;


@Service
public class GerencInterfaceService {

    @Autowired
    private PessoaRepository pRepository;
    @Autowired
    private EPIRepository eRepository;
    @Autowired
    private GerenciadorService gService;

    public void criarJanela(){
        JFrame frame = new JFrame("Janela");
        frame.setSize(400,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void verCamera(){
        if (gService.getCamera() != null){
            gService.getCamera().open();
            JFrame capturaCamera = new JFrame("Captura de Camera");
            WebcamPanel panel = new WebcamPanel(gService.getCamera());
            capturaCamera.add(panel);
            capturaCamera.pack();
            capturaCamera.setVisible(true);
        }
    }

    public void menu(){
        System.out.println("------------------------------------------------");
        System.out.println("1-Ler Crachá");
        System.out.println("0-Sair");
    }
}
