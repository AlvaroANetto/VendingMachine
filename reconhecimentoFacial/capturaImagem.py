import cv2
from pathlib import Path

# Garante que a pasta de cadastro exista
pasta_cadastro = Path("rostosCadastrados")
pasta_cadastro.mkdir(exist_ok=True)

print("=== TIPO DE CAPTURA ===")
print("1 - Foto para CADASTRO (Salva na pasta 'rostosCadastrados')")
print("2 - Foto da WEBCAM (Salva na raiz como 'foto_webcam.jpg' para validação)")
opcao = input("Escolha a opção (1 ou 2): ").strip()

if opcao == "1":
    nome_pessoa = input("Digite o nome ou ID do crachá da pessoa: ").strip()
    
    # Se não digitar nada, gera um número automático para o nome
    if not nome_pessoa:
        qtd_arquivos = len(list(pasta_cadastro.glob("*.jpg")))
        caminho_salvar = pasta_cadastro / f"pessoa_{qtd_arquivos + 1}.jpg"
    else:
        caminho_salvar = pasta_cadastro / f"{nome_pessoa}.jpg"
        
    titulo_janela = f"Cadastro: {caminho_salvar.name}"

elif opcao == "2":
    caminho_salvar = Path("foto_webcam.jpg")
    titulo_janela = "Foto da Webcam (Validacao)"

else:
    print("Opção inválida! Encerrando...")
    exit()

# Inicializa a câmera
camera = cv2.VideoCapture(0)

print("\nInstruções:")
print(" - Pressione 'ESPAÇO' ou 's' para salvar a foto.")
print(" - Pressione 'ESC' ou 'q' para cancelar.\n")

while True:
    ret, frame = camera.read()
    if not ret:
        print("Erro ao acessar a câmera.")
        break

    # Mostra a imagem com o título correspondente à escolha
    cv2.imshow(titulo_janela, frame)

    key = cv2.waitKey(1) & 0xFF

    # Tecla ESPAÇO (32) ou 's' para salvar
    if key == 32 or key == ord('s'):
        cv2.imwrite(str(caminho_salvar), frame, [cv2.IMWRITE_JPEG_QUALITY, 95])
        print(f"✅ Foto salva com sucesso em: {caminho_salvar}")
        break

    # Tecla ESC (27) ou 'q' para cancelar
    elif key == 27 or key == ord('q'):
        print("Captura cancelada.")
        break

camera.release()
cv2.destroyAllWindows()