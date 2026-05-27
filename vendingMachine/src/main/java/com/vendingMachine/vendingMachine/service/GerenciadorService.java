package com.vendingMachine.vendingMachine.service;


import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.vendingMachine.vendingMachine.entity.Pessoa;
import com.vendingMachine.vendingMachine.repository.EPIRepository;
import com.vendingMachine.vendingMachine.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

@Service
public class GerenciadorService {

    @Autowired
    private PessoaRepository pRepository;
    @Autowired
    private EPIRepository eRepository;

    private final Webcam camera = Webcam.getDefault();

    public GerenciadorService(){
        //camera.setViewSize(new Dimension(640,480));
    }

    public Pessoa encontrarPorQrCodeString(String qrCode){
        return pRepository.findByqrCode(qrCode);
    }

    public Pessoa encontrarPorQrCodeDireto(String caminho){
        return pRepository.findByqrCode(lerQrCodePorCaminho(caminho));
    }

    public Pessoa encontrarPorQrCodeImagem(){
        return pRepository.findByqrCode(lerQrCodePorImagem(pegarFoto()));
    }

    public String lerQrCodePorCaminho(String caminho) {
        try {
            BufferedImage img = ImageIO.read(new FileInputStream(caminho));
            if (img == null) {
                return "Erro: Imagem não encontrada ou em formato inválido.";
            }

            // Converte a imagem na fonte de luminância base do ZXing
            BufferedImageLuminanceSource fonte = new BufferedImageLuminanceSource(img);

            // Mantemos a dica para tentar mais arduamente
            Map<DecodeHintType, Object> dicas = new EnumMap<>(DecodeHintType.class);
            dicas.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

            QRCodeReader leitor = new QRCodeReader();

            // TENTATIVA 1: HybridBinarizer (Padrão, lida bem com sombras e iluminação irregular)
            try {
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(fonte));
                Result resultado = leitor.decode(bitmap, dicas);
                return resultado.getText();
            } catch (ReaderException e) {
                // Falhou (NotFound, Checksum ou Format). Vamos engolir a exceção e tentar o próximo metodo.
            }

            // TENTATIVA 2: GlobalHistogramBinarizer (Costuma ser melhor para imagens com baixo contraste)
            try {
                BinaryBitmap bitmapGlobal = new BinaryBitmap(new GlobalHistogramBinarizer(fonte));
                Result resultado = leitor.decode(bitmapGlobal, dicas);
                return resultado.getText();
            } catch (ReaderException e) {
                // Falhou novamente.
            }

            // TENTATIVA 3: Inverter as cores (resolve se o QR Code for branco no fundo preto)
            try {
                LuminanceSource fonteInvertida = fonte.invert();
                BinaryBitmap bitmapInvertido = new BinaryBitmap(new HybridBinarizer(fonteInvertida));
                Result resultado = leitor.decode(bitmapInvertido, dicas);
                return resultado.getText();
            } catch (ReaderException e) {
                // Falhou em todas as tentativas possíveis.
            }

            // Se o código chegou até aqui, nenhuma das 3 tentativas deu certo.
            return "Falha na leitura: O QR Code está muito danificado, ilegível ou ausente.";

        } catch (IOException e) {
            throw new RuntimeException("Erro de IO ao tentar ler o arquivo: " + e.getMessage(), e);
        }
    }

    public void lerVarios(String caminhoPasta) {
        File pasta = new File(caminhoPasta);

        // Verifica se o caminho realmente existe e se é uma pasta
        if (!pasta.exists() || !pasta.isDirectory()) {
            System.out.println("Erro: O diretório informado não existe ou não é uma pasta válida.");
            return;
        }

        // Pega todos os arquivos da pasta que terminam com extensões de imagem
        File[] arquivos = pasta.listFiles((dir, nome) -> {
            String nomeMinusculo = nome.toLowerCase();
            return nomeMinusculo.endsWith(".png") || nomeMinusculo.endsWith(".jpg") || nomeMinusculo.endsWith(".jpeg");
        });

        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhuma imagem encontrada na pasta: " + caminhoPasta);
            return;
        }

        System.out.println("--- Iniciando leitura de " + arquivos.length + " imagens ---");

        // Ordena os arquivos pelo nome para que a leitura faça sentido lógico
        Arrays.sort(arquivos);

        for (File arquivo : arquivos) {
            System.out.print("Lendo " + arquivo.getName() + " -> ");
            String dadosQrCode = lerQrCodePorCaminho(arquivo.getAbsolutePath());
            System.out.println(dadosQrCode);
        }

        System.out.println("--- Leitura finalizada ---");
    }

    public String lerQrCodePorImagem(BufferedImage img) {
        if (img == null) {
            return "Erro: Imagem não encontrada ou em formato inválido.";
        }

        // Converte a imagem na fonte de luminância base do ZXing
        BufferedImageLuminanceSource fonte = new BufferedImageLuminanceSource(img);

        // Mantemos a dica para tentar mais arduamente
        Map<DecodeHintType, Object> dicas = new EnumMap<>(DecodeHintType.class);
        dicas.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        QRCodeReader leitor = new QRCodeReader();

        // TENTATIVA 1: HybridBinarizer (Padrão, lida bem com sombras e iluminação irregular)
        try {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(fonte));
            Result resultado = leitor.decode(bitmap, dicas);
            return resultado.getText();
        } catch (ReaderException e) {
            // Falhou (NotFound, Checksum ou Format). Vamos engolir a exceção e tentar o próximo metodo.
        }

        // TENTATIVA 2: GlobalHistogramBinarizer (Costuma ser melhor para imagens com baixo contraste)
        try {
            BinaryBitmap bitmapGlobal = new BinaryBitmap(new GlobalHistogramBinarizer(fonte));
            Result resultado = leitor.decode(bitmapGlobal, dicas);
            return resultado.getText();
        } catch (ReaderException e) {
            // Falhou novamente.
        }

        // TENTATIVA 3: Inverter as cores (resolve se o QR Code for branco no fundo preto)
        try {
            LuminanceSource fonteInvertida = fonte.invert();
            BinaryBitmap bitmapInvertido = new BinaryBitmap(new HybridBinarizer(fonteInvertida));
            Result resultado = leitor.decode(bitmapInvertido, dicas);
            return resultado.getText();
        } catch (ReaderException e) {
            // Falhou em todas as tentativas possíveis.
        }

        // Se o código chegou até aqui, nenhuma das 3 tentativas deu certo.
        return "Falha na leitura: O QR Code está muito danificado, ilegível ou ausente.";

    }

    public BufferedImage pegarFoto(){
        this.camera.open();
        return camera.getImage();
    }

    public void tirarFoto(String pasta){
        this.camera.open();
        try{
            ImageIO.write(camera.getImage(),"PNG", new File(pasta + "\\Imagem.png"));
        } catch (IOException e ){
            e.printStackTrace();
        }
        camera.close();
    }

    public Webcam getCamera() {
        return camera;
    }
}
