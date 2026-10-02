from fastapi import FastAPI, UploadFile, File, Form, HTTPException, Depends
from sqlalchemy import create_engine, Column, Integer, String, ForeignKey, DateTime
from sqlalchemy.orm import declarative_base, sessionmaker, Session, relationship
from datetime import datetime
from deepface import DeepFace
from typing import Optional
import socket

import requests
import shutil
import os

hostname = socket.gethostname()

ipMaquina = socket.gethostbyname(hostname)

# ==========================================
# 1. CONFIGURAÇÃO DO BANCO DE DADOS (Substitui o JPA/Hibernate)
# ==========================================
SQLALCHEMY_DATABASE_URL = "mysql+pymysql://vending_api:123456@" + ipMaquina + ":3306/vendingMachine"
engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# Entidades (Substitui as classes em com.vendingMachine.vendingMachine.entity)
class Pessoa(Base):
    __tablename__ = "cliente"
    # Adicionamos "qr_code" como o nome real da coluna no MySQL
    qr_code = Column("qr_code", String(100), primary_key=True, index=True)
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
    pessoa_id = Column(String(100), ForeignKey("cliente.qr_code"))
    epi_id = Column(Integer, ForeignKey("tbl_EPI.id"))
    quantidade = Column(Integer)
    dia_hora = Column(DateTime, default=datetime.utcnow)

#Base.metadata.create_all(bind=engine)

# ==========================================
# 2. INICIALIZAÇÃO DA API
# ==========================================
app = FastAPI(title="Vending Machine API")
ESP32_URL = "http://10.110.22.34/girar_motor" # Mude para o IP do seu ESP32

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
def criar_pessoa(
    qr_code: str = Form(...), 
    nome: str = Form(...),
    cpf: Optional[str] = Form(None),   # Permite receber o CPF
    curso: Optional[str] = Form(None), # Permite receber o Curso
    db: Session = Depends(get_db)
):
    # Agora passamos todos os atributos para a Entidade do banco
    nova_pessoa = Pessoa(qr_code=qr_code, nome=nome, cpf=cpf, curso=curso)
    db.add(nova_pessoa)
    db.commit()
    
    return {"mensagem": f"Pessoa {nome} cadastrada com sucesso!"}

@app.post("/epi")
def criar_epi(tipo_epi: str = Form(...), db: Session = Depends(get_db)):
    novo_epi = EPI(tipo_epi=tipo_epi)
    db.add(novo_epi)
    db.commit()
    return {"mensagem": f"EPI {tipo_epi} cadastrado!"}

    # ---------------------------------------------------------
# Rota GET - Listar todas as pessoas cadastradas
# ---------------------------------------------------------
@app.get("/pessoa")
def listar_pessoas(db: Session = Depends(get_db)):
    # Busca todos os registros na tabela cliente (Pessoa)
    pessoas = db.query(Pessoa).all()
    return pessoas

@app.get("/epi")
def listar_epi(db: Session = Depends(get_db)):
    epis = db.query(EPI).all()
    return epis
# ---------------------------------------------------------
# Rota DELETE - Deletar uma pessoa pelo qrCode
# ---------------------------------------------------------
@app.delete("/pessoa/{qr_code}")
def deletar_pessoa(qr_code: str, db: Session = Depends(get_db)):
    # Busca a pessoa específica no banco de dados
    pessoa = db.query(Pessoa).filter(Pessoa.qr_code == qr_code).first()
    
    # Se a pessoa não existir, retorna um erro 404 (Não Encontrado)
    if not pessoa:
        raise HTTPException(status_code=404, detail="Pessoa não encontrada no banco de dados.")
    
    # Se encontrar, deleta do banco e confirma (commit)
    db.delete(pessoa)
    db.commit()
    
    return {"mensagem": f"Pessoa com qrCode '{qr_code}' deletada com sucesso!"}

# ==========================================
# 4. A ROTA PRINCIPAL: RECONHECIMENTO + RETIRADA + ESP32
# ==========================================
@app.post("/vendingMachine/retirar")
async def processar_retirada(
    qr_code: str = Form(...),
    epiId: int = Form(...),
    quantidade: int = Form(...),
    motor: int = Form(...), # Qual motor a máquina deve girar (1, 2 ou 3)
    passos: int = Form(200), # Quantos passos o NEMA 17 deve dar
    foto: UploadFile = File(...),
    db: Session = Depends(get_db)
):
    # 1. Verifica se a pessoa existe no banco
    pessoa = db.query(Pessoa).filter(Pessoa.qr_code == qr_code).first()
    if not pessoa:
        raise HTTPException(status_code=404, detail="QR Code não encontrado no sistema.")

    caminho_temp = f"temp_{qr_code}.jpg"
    caminho_oficial = f"rostosCadastrados/{qr_code}.jpg"

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
            nova_retirada = EpiRetirado(pessoa_id=qr_code, epi_id=epiId, quantidade=quantidade)
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