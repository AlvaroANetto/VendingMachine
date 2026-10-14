import cv2
import numpy as np
from pathlib import Path

# Garante que a pasta de cadastro exista
pasta_cadastro = Path("rostosCadastrados")
pasta_cadastro.mkdir(exist_ok=True)

# Inicializa o detector nativo de QR Code do OpenCV
qr_detector = cv2.QRCodeDetector()

# Inicializa a câmera
camera = cv2.VideoCapture(0)

# Variável de sessão para guardar o último QR Code detectado
qr_code_lido = ""

print("=== CADASTRO DE ROSTO VIA QR CODE ===")
print("1. Aproxime o QR Code do crachá da câmera.")
print("2. Quando o indicador VERDE aparecer, alinhe o rosto.")
print("3. Pressione 'ESPAÇO' ou 's' para salvar a foto.")
print("4. Pressione 'ESC' ou 'q' para cancelar.\n")

while True:
    ret, frame = camera.read()
    if not ret:
        print("Erro ao acessar a câmera.")
        break

    # Tenta detectar e decodificar o QR Code no frame atual
    dados, bbox, _ = qr_detector.detectAndDecode(frame)

    # Cria uma cópia do frame apenas para desenhar as marcações visuais
    frame_visual = frame.copy()

    # Se um QR Code for detectado no frame
    if bbox is not None and dados:
        qr_code_lido = dados.strip()
        
        # Converte os pontos da borda para inteiros
        pts = bbox.astype(int)
        
        # Desenha um retângulo verde em volta do QR Code
        cv2.polylines(frame_visual, [pts], isClosed=True, color=(0, 255, 0), thickness=3)
        
        # Mostra o texto do QR Code no topo da janela
        cv2.putText(
            frame_visual, 
            f"QR Detectado: {qr_code_lido}", 
            (10, 35), 
            cv2.FONT_HERSHEY_SIMPLEX, 
            0.7, 
            (0, 255, 0), 
            2
        )
    else:
        # Se nenhum QR Code estiver visível agora, mas já foi lido um na sessão
        if qr_code_lido:
            cv2.putText(
                frame_visual, 
                f"Pronto para salvar: {qr_code_lido}", 
                (10, 35), 
                cv2.FONT_HERSHEY_SIMPLEX, 
                0.7, 
                (0, 255, 255), 
                2
            )
        else:
            cv2.putText(
                frame_visual, 
                "Aproxime um QR Code da camera...", 
                (10, 35), 
                cv2.FONT_HERSHEY_SIMPLEX, 
                0.6, 
                (0, 0, 255), 
                2
            )

    # Exibe o preview com as informações na tela
    cv2.imshow("Cadastro - Pressione ESPACO para Salvar", frame_visual)

    key = cv2.waitKey(1) & 0xFF

    # Tecla ESPAÇO (32) ou 's' para salvar
    if key == 32 or key == ord('s'):
        if qr_code_lido:
            caminho_salvar = pasta_cadastro / f"{qr_code_lido}.jpg"
            
            # IMPORTANTE: Salva a imagem LIMPA (frame), sem o retângulo verde/texto
            cv2.imwrite(str(caminho_salvar), frame, [cv2.IMWRITE_JPEG_QUALITY, 95])
            
            print(f"✅ Foto salva com sucesso!")
            print(f"   Nome do arquivo: {caminho_salvar.name}")
            print(f"   Caminho: {caminho_salvar.absolute()}")
            break
        else:
            print("⚠️  Atenção: Nenhum QR Code foi detectado ainda. Aproxime o QR Code antes de apertar Espaço.")

    # Tecla ESC (27) ou 'q' para cancelar
    elif key == 27 or key == ord('q'):
        print("Captura cancelada.")
        break

camera.release()
cv2.destroyAllWindows()
