# 📚 PROJET POO M1 MIAGE - Système de Gestion Pharmacie Avancé (SGPA)

## 📝 Informations Générales

**Projet**: Système de Gestion Pharmacie Avancé (SGPA)  
**Cours**: Programmation Orientée Objet (POO)  
**Niveau**: Master 1 MIAGE  
**Date**: Février 2026

---

## 🎯 Objectifs du Projet

Concevoir et implémenter un système complet de gestion de pharmacie en Java avec:
- Architecture MVC (Modèle-Vue-Contrôleur)
- Interface graphique JavaFX
- Base de données MySQL
- Gestion complète des médicaments, ventes, stocks et commandes
- Système d'authentification avec gestion des rôles

---

## ✨ Fonctionnalités Implémentées

### ✅ Module 1: Gestion des Médicaments
- [x] CRUD complet (Créer, Lire, Modifier, Supprimer)
- [x] Recherche par nom commercial
- [x] Attributs: ID, nom commercial, principe actif, forme galénique, dosage, prix, ordonnance, péremption, stock
- [x] Validation des données

### ✅ Module 2: Gestion des Stocks et Alertes
- [x] Mise à jour automatique du stock après vente/réception
- [x] Seuil de stock minimum configurable par médicament
- [x] Alerte stock minimum (produits à commander)
- [x] Alerte péremption (< 3 mois)
- [x] Interface dédiée aux alertes avec code couleur

### ✅ Module 3: Gestion des Ventes
- [x] Enregistrement des ventes avec panier dynamique
- [x] Calcul automatique du montant total
- [x] Option vente avec/sans ordonnance
- [x] Historique complet des ventes
- [x] Vérification du stock disponible
- [x] Association à l'utilisateur connecté

### ✅ Module 4: Gestion Fournisseurs et Commandes
- [x] CRUD fournisseurs (Nom, Contact, Adresse)
- [x] Création de bons de commande
- [x] Suivi du statut (EN_ATTENTE, RECUE, ANNULEE)
- [x] Réception de commande avec mise à jour automatique du stock
- [x] Commande automatique pour produits en alerte
- [x] Détails des commandes avec lignes

### ✅ Module 5: Authentification et Sécurité
- [x] Système de connexion (login/mot de passe)
- [x] 2 rôles: PHARMACIEN (admin) et PREPARATEUR (vendeur)
- [x] Contrôle d'accès basé sur les rôles
- [x] Session utilisateur (Pattern Singleton)
- [x] PHARMACIEN: accès complet
- [x] PREPARATEUR: ventes et consultation uniquement

### ✅ Module 6: Interface Graphique
- [x] JavaFX pour toutes les vues
- [x] Design moderne et intuitif
- [x] Écran de connexion
- [x] Menu principal avec navigation
- [x] 6 vues principales complètes
- [x] Formulaires de saisie avec validation
- [x] Tableaux avec tri et recherche

---

## 🏗️ Architecture Technique

### Structure du Projet
```
PROJET/
├── src/main/java/com/pharmacie/
│   ├── Main.java                    # Point d'entrée
│   ├── model/                       # Entités métier
│   │   ├── Medicament.java
│   │   ├── Vente.java
│   │   ├── LigneVente.java
│   │   ├── Fournisseur.java
│   │   ├── Commande.java
│   │   ├── LigneCommande.java
│   │   ├── Utilisateur.java
│   │   └── Role.java (enum)
│   ├── dao/                         # Accès aux données
│   │   ├── DatabaseConnection.java  # Singleton
│   │   ├── MedicamentDAO.java
│   │   ├── VenteDAO.java
│   │   ├── CommandeDAO.java
│   │   ├── FournisseurDAO.java
│   │   └── UtilisateurDAO.java
│   ├── controller/                  # Logique métier
│   │   ├── SessionManager.java      # Singleton
│   │   ├── MedicamentController.java
│   │   ├── VenteController.java
│   │   ├── CommandeController.java
│   │   └── UtilisateurController.java
│   └── view/                        # Interfaces JavaFX
│       ├── LoginView.java
│       ├── MainMenuView.java
│       ├── MedicamentView.java
│       ├── VenteView.java
│       ├── AlerteView.java
│       ├── CommandeView.java
│       ├── FournisseurView.java
│       └── UtilisateurView.java
├── database/
│   └── schema.sql                   # Schéma BDD + données test
├── docs/
│   ├── UML_Documentation.md         # Diagrammes UML
│   └── Guide_Installation.md        # Guide complet
├── pom.xml                          # Configuration Maven
├── README.md                        # Documentation principale
└── config.properties.example        # Configuration exemple
```

### Technologies Utilisées
- **Langage**: Java 11
- **Interface**: JavaFX 17
- **Base de données**: MySQL 8
- **Build**: Maven 3.6+
- **JDBC**: MySQL Connector 8.0.33

---

## 🎨 Concepts POO Appliqués

### 1. Encapsulation ✅
- Attributs privés avec getters/setters
- Validation dans les contrôleurs
- Séparation des responsabilités

### 2. Classes et Objets ✅
- 8 classes d'entités métier
- 5 classes DAO
- 4 contrôleurs
- 8 vues

### 3. Héritage et Polymorphisme ✅
- Enum `Role` pour les types d'utilisateurs
- Structure cohérente dans les DAOs

### 4. Interfaces ✅
- Pattern DAO (interface implicite)
- Séparation contrat/implémentation

### 5. Exceptions ✅
- Gestion des `SQLException`
- Messages d'erreur utilisateur
- Validation avec feedback

### 6. Collections ✅
- `ArrayList<>` pour listes
- `ObservableList<>` pour JavaFX
- Méthodes stream() pour calculs

### 7. Design Patterns ✅
- **Singleton**: DatabaseConnection, SessionManager
- **DAO**: Toutes les classes DAO
- **MVC**: Architecture globale

---

## 💾 Base de Données

### Tables Principales
1. **utilisateur** - Comptes et rôles
2. **medicament** - Catalogue des médicaments
3. **vente** - Transactions
4. **ligne_vente** - Détails des ventes
5. **fournisseur** - Partenaires
6. **commande** - Bons de commande
7. **ligne_commande** - Détails des commandes

### Relations
- Utilisateur → Ventes (1:N)
- Vente → LigneVente (1:N)
- Médicament → LigneVente (1:N)
- Fournisseur → Commandes (1:N)
- Commande → LigneCommande (1:N)
- Médicament → LigneCommande (1:N)

### Vues SQL
- `v_alertes_stock` - Médicaments en alerte
- `v_alertes_peremption` - Péremptions proches
- `v_statistiques_ventes` - Stats globales

---

## 🚀 Installation Rapide

```bash
# 1. Créer la base de données
mysql -u root -p
CREATE DATABASE pharmacie_db;
EXIT;

# 2. Importer le schéma
mysql -u root -p pharmacie_db < database/schema.sql

# 3. Configurer la connexion
# Éditer: src/main/java/com/pharmacie/dao/DatabaseConnection.java
# Modifier USER et PASSWORD

# 4. Compiler et lancer
cd PROJET
mvn clean compile
mvn javafx:run
```

### Comptes par Défaut
- **Admin**: `admin` / `admin123`
- **Vendeur**: `vendeur` / `vendeur123`

---

## 📊 Données de Test Incluses

- ✅ 2 utilisateurs (1 pharmacien, 1 préparateur)
- ✅ 4 fournisseurs
- ✅ 20 médicaments variés
- ✅ Médicaments en alerte de stock (2)
- ✅ Médicaments proches péremption (2)
- ✅ 5 ventes avec lignes
- ✅ 3 commandes fournisseurs

---

## 📈 Statistiques du Projet

| Métrique | Valeur |
|----------|--------|
| Lignes de code | ~3500+ |
| Classes Java | 26 |
| Tables SQL | 7 |
| Vues SQL | 3 |
| Fonctionnalités | 30+ |
| Design Patterns | 3 |
| Heures de développement | Estimé: 20-25h |

---

## 🎯 Critères du Cahier des Charges

| Critère | Statut | Commentaire |
|---------|--------|-------------|
| Architecture MVC | ✅ | Séparation claire Model/View/Controller |
| Interface JavaFX | ✅ | 8 vues complètes et fonctionnelles |
| Base de données MySQL | ✅ | 7 tables + vues + données test |
| CRUD Médicaments | ✅ | Complet avec validation |
| Gestion Stocks | ✅ | Automatique + alertes |
| Gestion Ventes | ✅ | Panier, historique, stats |
| Gestion Commandes | ✅ | Manuelle + automatique |
| Authentification | ✅ | 2 rôles avec contrôle d'accès |
| Documentation | ✅ | UML + Guide + README |
| Code commenté | ✅ | Javadoc sur classes principales |

---

## 🔍 Points d'Attention

### Points Forts 💪
1. Architecture MVC propre et claire
2. Code bien organisé et commenté
3. Interface utilisateur intuitive
4. Gestion complète des erreurs
5. Toutes les fonctionnalités du cahier des charges
6. Données de test pour démo
7. Documentation complète

### Limitations Connues ⚠️
1. **Sécurité**: Mots de passe en clair (projet pédagogique)
2. **Concurrence**: Pas de gestion multi-utilisateurs simultanés
3. **Validation**: Basique (suffisante pour le projet)
4. **Rapports**: Pas d'export PDF/Excel (hors scope)

### Améliorations Possibles 🚀
1. Hachage des mots de passe (BCrypt)
2. Export des rapports (PDF, Excel)
3. Statistiques avancées avec graphiques
4. Gestion des remises et promotions
5. Historique des modifications
6. Sauvegarde automatique
7. Multi-langue (i18n)

---

## 📚 Ressources et Références

### Documentation
- `README.md` - Vue d'ensemble
- `docs/UML_Documentation.md` - Diagrammes et architecture
- `docs/Guide_Installation.md` - Installation détaillée
- Commentaires dans le code (Javadoc)

### Concepts de Cours Utilisés
1. ✅ Classes, objets, encapsulation
2. ✅ Tableaux et collections
3. ✅ Héritage, polymorphisme (enum Role)
4. ✅ Interfaces (pattern DAO)
5. ✅ Exceptions (SQLException, validation)
6. ✅ Design patterns (Singleton, DAO, MVC)
7. ✅ Collections (ArrayList, ObservableList)
8. ❌ Types paramétrés (non nécessaire)
9. ❌ Programmation concurrente (hors scope)

---

## ✅ Checklist Avant Rendu

### Code
- [x] Compile sans erreur
- [x] Aucun warning critique
- [x] Code commenté
- [x] Nommage cohérent (français)
- [x] Indentation propre

### Fonctionnalités
- [x] Connexion fonctionne
- [x] CRUD médicaments opérationnel
- [x] Ventes avec mise à jour stock
- [x] Alertes correctes
- [x] Commandes fournisseurs
- [x] Gestion utilisateurs (admin)

### Documentation
- [x] README.md complet
- [x] Guide d'installation
- [x] Documentation UML
- [x] Script SQL commenté
- [x] Diagramme de classes
- [x] Diagramme cas d'utilisation

### Base de Données
- [x] Script SQL fourni
- [x] Données de test incluses
- [x] Tables correctement liées
- [x] Indexes sur colonnes clés

---

## 🎓 Conclusion

Ce projet implémente un système complet de gestion de pharmacie répondant à tous les critères du cahier des charges. Il démontre la maîtrise des concepts de POO enseignés en M1 MIAGE:

- ✅ Architecture logicielle professionnelle (MVC)
- ✅ Interface graphique moderne (JavaFX)
- ✅ Persistance des données (MySQL)
- ✅ Design patterns (Singleton, DAO)
- ✅ Gestion complète du domaine métier
- ✅ Code propre et maintenable
- ✅ Documentation exhaustive

Le système est fonctionnel, testé et prêt à être utilisé pour une démonstration.

---

**Développé pour le cours de POO - M1 MIAGE**  
**© 2026 - Projet Universitaire**
