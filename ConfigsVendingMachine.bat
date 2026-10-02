@echo off
echo ==========================================
echo Configurando Vending Machine API
echo ==========================================

REM 1. Verifica onde estamos e atualiza/clona o repositorio
IF EXIST "reconhecimentoFacial\" (
    echo [INFO] O script ja esta dentro da pasta do repositorio. Atualizando...
    git pull
) ELSE IF EXIST "VendingMachine\" (
    echo [INFO] Repositorio ja existe nesta pasta. Entrando e atualizando...
    cd VendingMachine
    git pull
) ELSE (
    echo [INFO] Clonando o repositorio pela primeira vez...
    git clone https://github.com/AlvaroANetto/VendingMachine
    cd VendingMachine
)

REM 2. Garante que vai entrar na pasta correta da API
cd reconhecimentoFacial

echo [INFO] Instalando bibliotecas necessarias...
py -3.12 -m pip install fastapi sqlalchemy deepface tf-keras pymysql python-multipart uvicorn alembic

echo [INFO] Atualizando a estrutura do Banco de Dados...
py -3.12 -m alembic upgrade head

echo [INFO] Iniciando a API...
py -3.12 ApiReconhecimento.py

pause
