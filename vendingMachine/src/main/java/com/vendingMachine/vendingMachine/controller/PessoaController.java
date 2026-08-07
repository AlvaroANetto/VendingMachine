package com.vendingMachine.vendingMachine.controller;

import com.vendingMachine.vendingMachine.entity.Pessoa;
import com.vendingMachine.vendingMachine.service.GerenciadorService;
import com.vendingMachine.vendingMachine.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/vendingMachine/pessoa")
@CrossOrigin(origins = "")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private GerenciadorService gerenciadorService;

    @GetMapping
    public ResponseEntity<List<Pessoa>> listarTodos() {
        return ResponseEntity.ok(pessoaService.listaTodos());
    }

    @PostMapping(value = "/verificar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> verificarPessoa(
            @RequestParam(value = "qrCode", required = false) String qrCode,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        String qrCodeExtraido = null;

        //Processa a imagem se ela tiver sido enviada no multipart
        if (file != null && !file.isEmpty()) {
            try (InputStream inputStream = file.getInputStream()) {
                BufferedImage imagem = ImageIO.read(inputStream);

                if (imagem == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("O arquivo enviado não é uma imagem válida.");
                }

                // Extrai a String do QR Code
                qrCodeExtraido = gerenciadorService.lerQrCodePorImagem(imagem);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar imagem: " + e.getMessage());
            }
        }
        //Se não veio imagem, verifica se tem Stringdo qrCode
        else if (qrCode != null && !qrCode.trim().isEmpty()) {
            qrCodeExtraido = qrCode;
        }
        // Erro caso nenhum parâmetro tenha sido enviado
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Informe o parâmetro 'qrCode' ou envie uma imagem ");
        }

        // Caso a leitura da imagem não tenha conseguido decodificar nenhum texto
        if (qrCodeExtraido == null || qrCodeExtraido.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não foi possível ler um QR Code válido da imagem.");
        }

        // Busca o usuário no banco de dados usando a String obtida
        Pessoa pessoa = pessoaService.encontrarQrcode(qrCodeExtraido);

        if (pessoa == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pessoa não encontrada para o QR Code: " + qrCodeExtraido);
        }

        return ResponseEntity.ok(pessoa);
    }

    @PostMapping
    public ResponseEntity<Pessoa> salvar(@RequestBody Pessoa pessoa) {
        Pessoa novaPessoa = pessoaService.insert(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPessoa);
    }

    @PutMapping("/{qrCode}")
    public ResponseEntity<Pessoa> atualizar(@PathVariable String qrCode, @RequestBody Pessoa pessoaAtualizada) {
        Pessoa pessoa = pessoaService.atualizarDados(qrCode, pessoaAtualizada);
        return ResponseEntity.ok(pessoa);
    }

    @DeleteMapping("/{qrCode}")
    public ResponseEntity<Void> deletar(@PathVariable String qrCode) {
        pessoaService.deletar(qrCode);
        return ResponseEntity.noContent().build();
    }
}