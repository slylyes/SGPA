# Système de Gestion Pharmacie Avancé (SGPA)

## Description
Application Java de gestion de pharmacie développée dans le cadre d'un projet universitaire M1 MIAGE à Université Paris Dauphine - PSL

## Technologies
- **Langage** : Java 
- **Interface** : JavaFX 
- **Base de données** : MySQL 
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

# Guide d'installation

## Prérequis

| Outil | Version minimum | Vérification |
|-------|----------------|--------------|
| **Java JDK** | 11+ | `java -version` |
| **Maven** | 3.6+ | `mvn -version` |
| **MySQL** | 8.0+ | `mysql --version` |

## 1. Base de données

Ouvrir un terminal MySQL et exécuter le script de création :

```bash
mysql -u root -p < database/schema.sql
```

Cela crée la base `pharmacie_db` avec toutes les tables, vues et données de test.

## 2. Configuration de la connexion

Ouvrir le fichier `src/main/java/com/pharmacie/dao/DatabaseConnection.java` et adapter les identifiants MySQL (ligne 16-18) :

```java
private static final String URL = "jdbc:mysql://localhost:3306/pharmacie_db";
private static final String USER = "root";           // ← votre utilisateur MySQL
private static final String PASSWORD = "Root@1234";  // ← votre mot de passe MySQL
```

## 3. Lancer l'application

```bash
# Compiler le projet
mvn clean compile

# Exécuter l'application
mvn javafx:run
```


## 4. Connexion par défaut

Le script `schema.sql` crée un compte administrateur et préparateur :

| Login | Mot de passe | Rôle |
|-------|-------------|------|
| `admin` | `admin123` | Pharmacien |
| `vendeur` | `vendeur123` | Préparateur |


## Structure du projet
```
src/
├── main/
│   ├── java/com/pharmacie/
│       ├── Main.java
│       ├── model/           # Entités (Medicament, Vente, etc.)
│       ├── dao/             # Accès aux données
│       ├── controller/      # Logique métier
│       ├── view/            # Interfaces JavaFX
│       └── service/            # Classes pour l'export PDF et EXCEL
│   
│       
├── database/                # Scripts SQL
└── docs/                    # Documentation UML
```

## Auteur
Lyes SID ALI 
