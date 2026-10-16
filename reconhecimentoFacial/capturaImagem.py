import cv2
import numpy as np
import requests
from pathlib import Path

# Configuração da sua API
API_URL = "http://127.0.0.1:8000/pessoa"  # Mude o IP se a API estiver em outro PC

pasta_cadastro = Path("rostosCadastrados")
pasta_cadastro.mkdir(exist_ok=True)

qr_detector = cv2.QRCodeDetector()
camera = cv2.VideoCapture(0)
qr_code_lido = ""

print("=== CADASTRO COMPLETO (FOTO + BANCO DE DADOS) ===")
print("1. Aproxime o QR Code do crachá da câmera.")
print("2. Quando o indicador VERDE aparecer, alinhe o rosto.")
print("3. Pressione 'ESPAÇO' ou 's' para salvar a foto.")
print("4. Pressione 'ESC' ou 'q' para cancelar.\n")

while True:
    ret, frame = camera.read()
    if not ret:
        print("Erro ao acessar a câmera.")
        break

    dados, bbox, _ = qr_detector.detectAndDecode(frame)
    frame_visual = frame.copy()

    if bbox is not None and dados:
        qr_code_lido = dados.strip()
        pts = bbox.astype(int)
        cv2.polylines(frame_visual, [pts], isClosed=True, color=(0, 255, 0), thickness=3)
        cv2.putText(frame_visual, f"QR: {qr_code_lido}", (10, 35), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)
    else:
        if qr_code_lido:
            cv2.putText(frame_visual, f"Pronto: {qr_code_lido}", (10, 35), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 255), 2)
        else:
            cv2.putText(frame_visual, "Aproxime o QR Code...", (10, 35), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 255), 2)

    cv2.imshow("Cadastro - Pressione ESPACO para Salvar", frame_visual)

    key = cv2.waitKey(1) & 0xFF

    if key == 32 or key == ord('s'):
        if qr_code_lido:
            # 1. Salva a foto fisicamente
            caminho_salvar = pasta_cadastro / f"{qr_code_lido}.jpg"
            cv2.imwrite(str(caminho_salvar), frame, [cv2.IMWRITE_JPEG_QUALITY, 95])
            print(f"\n✅ Foto salva em: {caminho_salvar.name}")
            
            # Fecha a janela da câmera para o usuário focar no terminal
            camera.release()
            cv2.destroyAllWindows()

            # 2. Coleta os dados no terminal
            print("\n=== DADOS DO USUÁRIO ===")
            nome = input("Digite o NOME da pessoa: ").strip()
            cpf = input("Digite o CPF (opcional, aperte Enter para pular): ").strip()
            curso = input("Digite o CURSO (opcional, aperte Enter para pular): ").strip()

            # 3. Envia para o Banco de Dados via API
            print("\nSalvando no Banco de Dados...")
            dados_api = {
                "qr_code": qr_code_lido,
                "nome": nome,
                "cpf": cpf if cpf else None,
                "curso": curso if curso else None
            }

            try:
                resposta = requests.post(API_URL, data=dados_api)
                
                if resposta.status_code == 200:
                    print("✅ Usuário cadastrado com sucesso no Banco de Dados!")
                else:
                    print(f"⚠️ Erro ao salvar no banco: {resposta.text}")
            except requests.exceptions.ConnectionError:
                print("❌ ERRO: A API (FastAPI) parece estar desligada. Ligue a API e cadastre o usuário manualmente depois.")
            
            break
        else:
            print("⚠️ Nenhum QR Code lido. Aproxime antes de apertar Espaço.")

    elif key == 27 or key == ord('q'):
        print("Captura cancelada.")
        break

# Garante que a câmera seja liberada caso o loop quebre por erro
if camera.isOpened():
    camera.release()
cv2.destroyAllWindows()