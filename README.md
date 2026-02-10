# Système de Gestion Pharmacie Avancé (SGPA)

## Description
Application Java de gestion de pharmacie développée dans le cadre d'un projet universitaire M1 MIAGE.

## Technologies
- **Langage** : Java 11
- **Interface** : JavaFX 17
- **Base de données** : MySQL 8
- **Build** : Maven

## Architecture
Le projet suit l'architecture MVC (Modèle-Vue-Contrôleur) :
- **Modèle** : Classes entités dans `com.pharmacie.model`
- **Vue** : Interfaces JavaFX dans `com.pharmacie.view`
- **Contrôleur** : Logique métier dans `com.pharmacie.controller`
- **DAO** : Accès aux données dans `com.pharmacie.dao`

## Fonctionnalités
1. **Gestion des Médicaments** : CRUD complet
2. **Gestion des Stocks** : Alertes automatiques (stock minimum, péremption)
3. **Gestion des Ventes** : Enregistrement et historique
4. **Gestion des Fournisseurs** : Commandes et réceptions
5. **Authentification** : Système de connexion avec 2 rôles (Pharmacien, Préparateur)

## Installation

### Prérequis
- Java JDK 11 ou supérieur
- Maven 3.6+
- MySQL 8.0+

### Configuration de la base de données
1. Créer une base de données MySQL :
```sql
CREATE DATABASE pharmacie_db;
```

2. Exécuter le script SQL fourni : `database/schema.sql`

3. Modifier les paramètres de connexion dans : `src/main/java/com/pharmacie/dao/DatabaseConnection.java`

### Compilation et exécution
```bash
# Compiler le projet
mvn clean compile

# Exécuter l'application
mvn javafx:run
```

## Utilisateurs par défaut
- **Pharmacien (Admin)** : `admin` / `admin123`
- **Préparateur** : `vendeur` / `vendeur123`

## Structure du projet
```
src/
├── main/
│   ├── java/com/pharmacie/
│   │   ├── Main.java
│   │   ├── model/           # Entités (Medicament, Vente, etc.)
│   │   ├── dao/             # Accès aux données
│   │   ├── controller/      # Logique métier
│   │   ├── view/            # Interfaces JavaFX
│   │   └── util/            # Classes utilitaires
│   └── resources/
│       └── fxml/            # Fichiers FXML
├── database/                # Scripts SQL
└── docs/                    # Documentation UML
```

## Auteur
Projet M1 MIAGE - POO
