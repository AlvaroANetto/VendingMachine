import cv2
from pathlib import Path
from deepface import DeepFace
import pandas as pd
import os

# ==========================================
# FUNÇÕES DO SISTEMA
# ==========================================

def limpar_cache_deepface(pasta_db):
    """Apaga os arquivos de cache do DeepFace para forçar a leitura de novos cadastros."""
    pasta = Path(pasta_db)
    for arquivo in pasta.glob("*.pkl"):
        try:
            arquivo.unlink()
            print(f" Cache atualizado: {arquivo.name} removido.")
        except Exception as e:
            print(f"Erro ao remover cache: {e}")

def validar_acesso(img_path, db_path):
    """Verifica se a foto atual corresponde a alguém no banco de dados."""
    try:
        print("\n Analisando rosto e buscando correspondências no banco de dados...")
        
        resultados = DeepFace.find(
            img_path=str(img_path),
            db_path=str(db_path),
            model_name="Facenet512",
            detector_backend="mtcnn", # mtcnn ou retinaface são ótimos
            distance_metric="cosine",
            enforce_detection=True,
            silent=True
        )
        
        df_resultado = resultados[0]
        
        if not df_resultado.empty:
            melhor_match = df_resultado.iloc[0]
            caminho_arquivo = melhor_match["identity"]
            distancia = melhor_match["distance"]
            
            # Pega o nome do arquivo de forma segura, independente de ser Windows ou Linux/Mac
            nome_pessoa = Path(caminho_arquivo).stem 
            
            print(f"\n ACESSO LIBERADO! \n Bem-vindo(a), {nome_pessoa}! \n(Distância/Confiança: {distancia:.2f})")
        else:
            print("\n ACESSO NEGADO: Rosto não reconhecido.")
            
    except ValueError:
        print("\n ACESSO NEGADO: Nenhum rosto humano detectado na foto. Tente novamente em um local mais iluminado.")
    except Exception as e:
        print(f"\n Erro inesperado na validação: {e}")


# ==========================================
# FLUXO PRINCIPAL (MENU E CÂMERA)
# ==========================================

pasta_cadastro = Path("rostosCadastrados")
pasta_cadastro.mkdir(exist_ok=True)

print("=== SISTEMA DE CONTROLE DE ACESSO ===")
print("1 - Cadastrar novo rosto")
print("2 - Validar acesso (Webcam)")
opcao = input("Escolha a opção (1 ou 2): ").strip()

# --- Configuração das Opções ---
if opcao == "1":
    nome_pessoa = input("Digite o nome ou ID do crachá da pessoa: ").strip()
    if not nome_pessoa:
        qtd_arquivos = len(list(pasta_cadastro.glob("*.jpg")))
        caminho_salvar = pasta_cadastro / f"pessoa_{qtd_arquivos + 1}.jpg"
    else:
        caminho_salvar = pasta_cadastro / f"{nome_pessoa}.jpg"
    
    titulo_janela = f"Cadastro: {caminho_salvar.name}"

elif opcao == "2":
    caminho_salvar = Path("foto_webcam.jpg")
    titulo_janela = "Validacao de Acesso (ESC para cancelar, ESPACO para capturar)"

else:
    print("Opção inválida! Encerrando...")
    exit()

# --- Captura da Câmera ---
camera = cv2.VideoCapture(0)

print("\nInstruções da Câmera:")
print(" - Olhe para a câmera.")
print(" - Pressione 'ESPAÇO' ou 's' para tirar a foto.")
print(" - Pressione 'ESC' ou 'q' para cancelar.\n")

foto_tirada = False

while True:
    ret, frame = camera.read()
    if not ret:
        print("Erro ao acessar a câmera.")
        break

    cv2.imshow(titulo_janela, frame)
    key = cv2.waitKey(1) & 0xFF

    if key == 32 or key == ord('s'): # ESPAÇO ou 'S'
        cv2.imwrite(str(caminho_salvar), frame, [cv2.IMWRITE_JPEG_QUALITY, 100])
        print(f"Foto salva com sucesso: {caminho_salvar}")
        foto_tirada = True
        break

    elif key == 27 or key == ord('q'): # ESC ou 'Q'
        print("Operação cancelada pelo usuário.")
        break

camera.release()
cv2.destroyAllWindows()

# ==========================================
# AÇÕES PÓS-CAPTURA
# ==========================================

if foto_tirada:
    if opcao == "1":
        print("\n✅ Cadastro realizado com sucesso!")
        limpar_cache_deepface(pasta_cadastro) # Limpa o cache para reconhecer a nova pessoa
        
    elif opcao == "2":
        # Chama a função que faz a mágica do DeepFace
        validar_acesso(caminho_salvar, pasta_cadastro)