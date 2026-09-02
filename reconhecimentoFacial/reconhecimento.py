from deepface import DeepFace
from pathlib import Path
import numpy as np


def calcular_distancia_cosseno(vetor1, vetor2):
    a = np.array(vetor1)
    b = np.array(vetor2)
    return 1 - (np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b)))

caminho_pasta = Path("rostosCadastrados")
extensoes_validas = {".jpg", ".jpeg", ".png"}

# --- 1. PROCESSAR FOTO ATUAL (Apenas uma vez) ---
print("Extraindo vetor da foto da webcam...")
embedding_atual = DeepFace.represent(
    img_path="foto_webcam.jpg", 
    model_name="Facenet512",
    detector_backend="skip",
    enforce_detection=False
)[0]["embedding"]

acesso_liberado = False
limiar_tolerancia = 0.30  # Limite para o modelo Facenet512 (menor = mais rigoroso)

# --- 2. PERCORRER A PASTA DE CADASTRADOS ---
for arquivo in caminho_pasta.iterdir():
    if arquivo.suffix.lower() in extensoes_validas:
        try:
            # Passamos o caminho do arquivo direto como String para o DeepFace
            embedding_cadastrado = DeepFace.represent(
                img_path=str(arquivo), 
                model_name="Facenet512",
                detector_backend="skip",
                enforce_detection=False
            )[0]["embedding"]

            # Compara os dois vetores numericamente
            distancia = calcular_distancia_cosseno(embedding_atual, embedding_cadastrado)

            # Verifica se está dentro da margem de aceitação
            if distancia <= limiar_tolerancia:
                nome_pessoa = arquivo.stem  # Pega o nome do arquivo sem a extensão .jpg
                print(f"✅ Acesso LIBERADO! Rosto identificado: {nome_pessoa} (Distância: {distancia:.2f})")
                acesso_liberado = True
                break  # Encerra a busca assim que encontrar uma correspondência
            else:
                print(f"Diferente de {arquivo.stem} (Distância: {distancia:.2f})")

        except Exception as e:
            print(f"Erro ao processar {arquivo.name}: {e}")

if not acesso_liberado:
    print("❌ Acesso NEGADO: Nenhuma correspondência encontrada na pasta.")