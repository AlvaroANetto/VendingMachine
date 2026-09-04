# Instalar: pip install fastapi uvicorn deepface requests python-multipart
from fastapi import FastAPI, UploadFile, File, Form
from deepface import DeepFace
import requests
import os
import shutil

app = FastAPI()

# URL da sua API Java (ajuste a porta se o Spring Boot rodar em outra)
JAVA_API_URL = "http://localhost:8080/vendingMachine/retiradas"

@app.post("/autenticar_e_retirar")
async def autenticar_rosto(
    qrCode: str = Form(...), 
    epiId: int = Form(...), 
    quantidade: int = Form(...), 
    foto: UploadFile = File(...)
):
    
    # 1. Salvar a foto temporária enviada pelo ESP32
    caminho_temp = f"temp_{qrCode}.jpg"
    with open(caminho_temp, "wb") as buffer:
        shutil.copyfileobj(foto.file, buffer)

    # 2. Localizar a foto oficial do cadastro usando o QR Code
    caminho_oficial = f"rostosCadastrados/{qrCode}.jpg"
    
    if not os.path.exists(caminho_oficial):
        os.remove(caminho_temp)
        return {"status": "erro", "mensagem": "Usuário não possui foto cadastrada."}

    # 3. VERIFICAÇÃO 1 PARA 1 (Muito mais leve)
    try:
        resultado = DeepFace.verify(
            img1_path=caminho_temp,
            img2_path=caminho_oficial,
            model_name="Facenet512",
            detector_backend="mtcnn",
            distance_metric="cosine",
            enforce_detection=True
        )
        
        os.remove(caminho_temp) # Limpa a foto temporária

        if resultado["verified"]:
            # 4. ROSTO RECONHECIDO! Avisar o Java para registrar a retirada
            dados_para_java = {
                "qrCode": qrCode,
                "epiId": epiId,
                "quantidade": quantidade
            }
            
            resposta_java = requests.post(JAVA_API_URL, json=dados_para_java)
            
            if resposta_java.status_code == 201:
                return {"status": "sucesso", "mensagem": "Acesso Liberado e EPI registrado!", "motor_esp32": "ON"}
            else:
                return {"status": "erro", "mensagem": "Rosto reconhecido, mas erro ao salvar no banco Java."}
        else:
            return {"status": "negado", "mensagem": "O rosto não bate com o dono do crachá."}
            
    except Exception as e:
        if os.path.exists(caminho_temp): os.remove(caminho_temp)
        return {"status": "erro", "mensagem": f"Falha no reconhecimento: {str(e)}"}

# Para rodar: uvicorn nome_do_arquivo:app --host 0.0.0.0 --port 5000