@echo off
REM Script de lancement du Système de Gestion Pharmacie Avancé (SGPA)
REM Pour Windows

echo ======================================================
echo   SGPA - Système de Gestion Pharmacie Avancé
echo ======================================================
echo.

REM Vérifier Java
echo Vérification de Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo Erreur: Java n'est pas installé ou n'est pas dans le PATH
    echo Veuillez installer Java 11 ou supérieur
    pause
    exit /b 1
)
echo Java OK

REM Vérifier Maven
echo Vérification de Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo Erreur: Maven n'est pas installé ou n'est pas dans le PATH
    echo Veuillez installer Maven 3.6 ou supérieur
    pause
    exit /b 1
)
echo Maven OK

echo.
echo ======================================================
echo   Compilation du projet...
echo ======================================================
call mvn clean compile

if errorlevel 1 (
    echo.
    echo Erreur lors de la compilation
    pause
    exit /b 1
)

echo.
echo ======================================================
echo   Lancement de l'application...
echo ======================================================
echo.
echo Utilisateurs par défaut:
echo   - Admin     : admin / admin123
echo   - Préparateur : vendeur / vendeur123
echo.

call mvn javafx:run

pause
