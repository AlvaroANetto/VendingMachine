from fastapi import FastAPI, UploadFile, File, Form, HTTPException, Depends
from sqlalchemy import create_engine, Column, Integer, String, ForeignKey, DateTime
from sqlalchemy.orm import declarative_base, sessionmaker, Session, relationship
from datetime import datetime
from deepface import DeepFace
import requests
import shutil
import os

# ==========================================
# 1. CONFIGURAÇÃO DO BANCO DE DADOS (Substitui o JPA/Hibernate)
# ==========================================
SQLALCHEMY_DATABASE_URL = "mysql+pymysql://vending_api:654321@10.110.12.43:3306/vendingMachine"
engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# Entidades (Substitui as classes em com.vendingMachine.vendingMachine.entity)
class Pessoa(Base):
    __tablename__ = "cliente"
    qrCode = Column(String(100), primary_key=True, index=True)
    cpf = Column(String(14))
    nome = Column(String(255))
    curso = Column(String(100))

class EPI(Base):
    __tablename__ = "tbl_EPI"
    id = Column(Integer, primary_key=True, index=True)
    tipo_epi = Column(String(100))

class EpiRetirado(Base):
    __tablename__ = "epi_Retirado"
    id = Column(Integer, primary_key=True, index=True)
    pessoa_id = Column(String(100), ForeignKey("cliente.qrCode"))
    epi_id = Column(Integer, ForeignKey("tbl_EPI.id"))
    quantidade = Column(Integer)
    dia_hora = Column(DateTime, default=datetime.utcnow)

Base.metadata.create_all(bind=engine)

# ==========================================
# 2. INICIALIZAÇÃO DA API
# ==========================================
app = FastAPI(title="Vending Machine API")
ESP32_URL = "http://192.168.1.100/girar_motor" # Mude para o IP do seu ESP32

# Dependência para pegar o banco de dados
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# ==========================================
# 3. ROTAS DE CADASTRO BÁSICO (CRUD)
# ==========================================
@app.post("/pessoa")
def criar_pessoa(qrCode: str = Form(...), nome: str = Form(...), db: Session = Depends(get_db)):
    nova_pessoa = Pessoa(qrCode=qrCode, nome=nome)
    db.add(nova_pessoa)
    db.commit()
    return {"mensagem": f"Pessoa {nome} cadastrada com sucesso!"}

@app.post("/epi")
def criar_epi(tipo_epi: str = Form(...), db: Session = Depends(get_db)):
    novo_epi = EPI(tipo_epi=tipo_epi)
    db.add(novo_epi)
    db.commit()
    return {"mensagem": f"EPI {tipo_epi} cadastrado!"}

# ==========================================
# 4. A ROTA PRINCIPAL: RECONHECIMENTO + RETIRADA + ESP32
# ==========================================
@app.post("/vendingMachine/retirar")
async def processar_retirada(
    qrCode: str = Form(...),
    epiId: int = Form(...),
    quantidade: int = Form(...),
    motor: int = Form(...), # Qual motor a máquina deve girar (1, 2 ou 3)
    passos: int = Form(200), # Quantos passos o NEMA 17 deve dar
    foto: UploadFile = File(...),
    db: Session = Depends(get_db)
):
    # 1. Verifica se a pessoa existe no banco
    pessoa = db.query(Pessoa).filter(Pessoa.qrCode == qrCode).first()
    if not pessoa:
        raise HTTPException(status_code=404, detail="QR Code não encontrado no sistema.")

    caminho_temp = f"temp_{qrCode}.jpg"
    caminho_oficial = f"rostosCadastrados/{qrCode}.jpg"

    if not os.path.exists(caminho_oficial):
        raise HTTPException(status_code=404, detail="Foto de cadastro não encontrada.")

    # Salva a foto da webcam
    with open(caminho_temp, "wb") as buffer:
        shutil.copyfileobj(foto.file, buffer)

    try:
        # 2. IA compara as duas fotos
        resultado = DeepFace.verify(
            img1_path=caminho_temp,
            img2_path=caminho_oficial,
            model_name="Facenet512",
            detector_backend="mtcnn",
            distance_metric="cosine"
        )
        os.remove(caminho_temp)

        if resultado["verified"]:
            # 3. Rosto confirmado! Salva a retirada no banco de dados
            nova_retirada = EpiRetirado(pessoa_id=qrCode, epi_id=epiId, quantidade=quantidade)
            db.add(nova_retirada)
            db.commit()

            # 4. Envia o comando HTTP para o ESP32 girar o NEMA 17
            comando_esp = {"motor": motor, "passos": passos}
            resposta_esp = requests.post(ESP32_URL, json=comando_esp, timeout=5)

            if resposta_esp.status_code == 200:
                return {"status": "sucesso", "mensagem": f"Acesso liberado para {pessoa.nome}. Motor acionado!"}
            else:
                return {"status": "alerta", "mensagem": "Salvo no banco, mas falha ao acionar a máquina física."}
        else:
            raise HTTPException(status_code=401, detail="Rosto não corresponde ao dono do crachá.")

    except Exception as e:
        if os.path.exists(caminho_temp): os.remove(caminho_temp)
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)