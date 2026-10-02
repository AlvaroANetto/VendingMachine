@echo off
echo ==========================================
echo Configurando Vending Machine API
echo ==========================================

REM Verifica se a pasta VendingMachine já existe no local onde o .bat foi executado
IF EXIST "VendingMachine\" (
    echo [INFO] Repositorio ja existe. Atualizando os arquivos...
    cd VendingMachine
    git pull
) ELSE (
    echo [INFO] Clonando o repositorio pela primeira vez...
    git clone https://github.com/AlvaroANetto/VendingMachine
    cd VendingMachine
)

REM Entra na pasta do reconhecimento facial
cd reconhecimentoFacial

echo [INFO] Instalando bibliotecas necessarias...
py -3.12 -m pip install fastapi sqlalchemy deepface tf-keras pymysql python-multipart uvicorn

echo [INFO] Iniciando a API...
py -3.12 ApiReconhecimento.py

pause
