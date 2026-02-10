#!/bin/bash
# Script de lancement du Système de Gestion Pharmacie Avancé (SGPA)
# Pour Linux/macOS

echo "======================================================"
echo "  SGPA - Système de Gestion Pharmacie Avancé"
echo "======================================================"
echo ""

# Vérifier Java
echo "🔍 Vérification de Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé. Veuillez installer Java 11 ou supérieur."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F. '{print $1}')
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "❌ Java 11 ou supérieur est requis. Version actuelle: $JAVA_VERSION"
    exit 1
fi
echo "✅ Java version OK"

# Vérifier Maven
echo "🔍 Vérification de Maven..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé. Veuillez installer Maven 3.6 ou supérieur."
    exit 1
fi
echo "✅ Maven OK"

# Vérifier MySQL
echo "🔍 Vérification de MySQL..."
if ! command -v mysql &> /dev/null; then
    echo "⚠️ MySQL n'est pas détecté. Assurez-vous qu'il est installé et démarré."
fi

echo ""
echo "======================================================"
echo "  Compilation du projet..."
echo "======================================================"
mvn clean compile

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Erreur lors de la compilation"
    exit 1
fi

echo ""
echo "======================================================"
echo "  Lancement de l'application..."
echo "======================================================"
echo ""
echo "Utilisateurs par défaut:"
echo "  - Admin     : admin / admin123"
echo "  - Préparateur : vendeur / vendeur123"
echo ""

mvn javafx:run
