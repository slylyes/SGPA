# Documentation UML - Système de Gestion Pharmacie Avancé

## 1. Diagramme de Classes UML (notation textuelle)

```
┌─────────────────────────────┐
│        Medicament           │
├─────────────────────────────┤
│ - id: int                   │
│ - nomCommercial: String     │
│ - principeActif: String     │
│ - formeGalenique: String    │
│ - dosage: String            │
│ - prixPublic: double        │
│ - necessiteOrdonnance: bool │
│ - datePeremption: LocalDate │
│ - stockActuel: int          │
│ - seuilMinimum: int         │
├─────────────────────────────┤
│ + estEnAlerteStock(): bool  │
│ + estProcheDeLaPeremption() │
└─────────────────────────────┘

┌─────────────────────────────┐
│         Vente               │
├─────────────────────────────┤
│ - id: int                   │
│ - dateHeure: LocalDateTime  │
│ - lignesVente: List         │
│ - montantTotal: double      │
│ - avecOrdonnance: boolean   │
│ - idUtilisateur: int        │
├─────────────────────────────┤
│ + ajouterLigne()            │
│ + calculerMontantTotal()    │
└─────────────────────────────┘
           │ 1
           │
           │ *
┌─────────────────────────────┐
│       LigneVente            │
├─────────────────────────────┤
│ - id: int                   │
│ - idVente: int              │
│ - idMedicament: int         │
│ - nomMedicament: String     │
│ - quantite: int             │
│ - prixUnitaire: double      │
│ - sousTotal: double         │
└─────────────────────────────┘

┌─────────────────────────────┐
│      Fournisseur            │
├─────────────────────────────┤
│ - id: int                   │
│ - nom: String               │
│ - contact: String           │
│ - adresse: String           │
└─────────────────────────────┘
           │ 1
           │
           │ *
┌─────────────────────────────┐
│        Commande             │
├─────────────────────────────┤
│ - id: int                   │
│ - idFournisseur: int        │
│ - nomFournisseur: String    │
│ - dateCommande: LocalDate   │
│ - dateReception: LocalDate  │
│ - statut: String            │
│ - lignesCommande: List      │
├─────────────────────────────┤
│ + ajouterLigne()            │
└─────────────────────────────┘
           │ 1
           │
           │ *
┌─────────────────────────────┐
│     LigneCommande           │
├─────────────────────────────┤
│ - id: int                   │
│ - idCommande: int           │
│ - idMedicament: int         │
│ - nomMedicament: String     │
│ - quantite: int             │
└─────────────────────────────┘

┌─────────────────────────────┐
│      <<enumeration>>        │
│          Role               │
├─────────────────────────────┤
│ PHARMACIEN                  │
│ PREPARATEUR                 │
└─────────────────────────────┘
                 │
                 │
┌─────────────────────────────┐
│       Utilisateur           │
├─────────────────────────────┤
│ - id: int                   │
│ - login: String             │
│ - motDePasse: String        │
│ - nom: String               │
│ - prenom: String            │
│ - role: Role                │
├─────────────────────────────┤
│ + estPharmacien(): boolean  │
│ + estPreparateur(): boolean │
└─────────────────────────────┘

┌─────────────────────────────┐
│   <<singleton>>             │
│   SessionManager            │
├─────────────────────────────┤
│ - instance: SessionManager  │
│ - utilisateurConnecte       │
├─────────────────────────────┤
│ + getInstance()             │
│ + estConnecte(): boolean    │
│ + estPharmacien(): boolean  │
│ + deconnecter()             │
└─────────────────────────────┘

┌─────────────────────────────┐
│   <<singleton>>             │
│   DatabaseConnection        │
├─────────────────────────────┤
│ - instance                  │
│ - connection: Connection    │
├─────────────────────────────┤
│ + getInstance()             │
│ + getConnection()           │
│ + closeConnection()         │
└─────────────────────────────┘
```

## 2. Diagramme de Cas d'Utilisation

### Acteurs:
- **Pharmacien (Administrateur)**: Accès complet au système
- **Préparateur (Vendeur)**: Accès limité aux ventes et consultation stocks

### Cas d'utilisation:

**Pharmacien peut:**
1. Se connecter
2. Gérer les médicaments (CRUD)
   - Créer un médicament
   - Modifier un médicament
   - Supprimer un médicament
   - Consulter la liste des médicaments
3. Effectuer des ventes
4. Consulter les alertes
   - Alertes de stock minimum
   - Alertes de péremption
5. Gérer les fournisseurs (CRUD)
6. Gérer les commandes fournisseurs
   - Créer une commande
   - Recevoir une commande (mise à jour stock)
   - Commande automatique pour articles en alerte
7. Gérer les utilisateurs (CRUD)
8. Consulter l'historique des ventes
9. Voir le chiffre d'affaires

**Préparateur peut:**
1. Se connecter
2. Effectuer des ventes
3. Consulter les médicaments
4. Consulter les alertes de stock
5. Consulter l'historique des ventes

```
┌──────────────────────────────────────────────────────────────┐
│                    SYSTÈME PHARMACIE                          │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │            Gestion des Médicaments                   │    │
│  │  • Créer médicament                                  │    │
│  │  • Modifier médicament                               │◄───┼─── Pharmacien
│  │  • Supprimer médicament                              │    │
│  │  • Consulter médicaments                             │◄───┼─── Préparateur
│  └─────────────────────────────────────────────────────┘    │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │            Gestion des Ventes                        │    │
│  │  • Enregistrer vente                                 │◄───┼─── Pharmacien
│  │  • Consulter historique                              │◄───┼─── Préparateur
│  └─────────────────────────────────────────────────────┘    │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │            Alertes Stock                             │    │
│  │  • Consulter alertes stock minimum                   │◄───┼─── Pharmacien
│  │  • Consulter alertes péremption                      │◄───┼─── Préparateur
│  └─────────────────────────────────────────────────────┘    │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │       Gestion Fournisseurs & Commandes              │    │
│  │  • Gérer fournisseurs                                │◄───┼─── Pharmacien
│  │  • Créer commande                                    │    │      (uniquement)
│  │  • Recevoir commande (MAJ stock)                     │    │
│  │  • Commande automatique                              │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │         Gestion des Utilisateurs                     │    │
│  │  • Créer utilisateur                                 │◄───┼─── Pharmacien
│  │  • Modifier utilisateur                              │    │      (uniquement)
│  │  • Supprimer utilisateur                             │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

## 3. Architecture MVC

```
┌─────────────────────────────────────────────────────────────┐
│                         VUE (View)                           │
│  LoginView, MainMenuView, MedicamentView, VenteView,        │
│  AlerteView, CommandeView, FournisseurView, UtilisateurView │
└────────────────────┬────────────────────────────────────────┘
                     │ affiche / événements utilisateur
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                   CONTRÔLEUR (Controller)                    │
│  MedicamentController, VenteController, CommandeController, │
│  UtilisateurController, SessionManager                       │
└────────────────────┬────────────────────────────────────────┘
                     │ logique métier / validations
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                      MODÈLE (Model)                          │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ Entités:                                              │  │
│  │  Medicament, Vente, LigneVente, Fournisseur,         │  │
│  │  Commande, LigneCommande, Utilisateur, Role          │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ DAO (Data Access Object):                            │  │
│  │  MedicamentDAO, VenteDAO, CommandeDAO,               │  │
│  │  FournisseurDAO, UtilisateurDAO                       │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ Connexion:                                            │  │
│  │  DatabaseConnection (Singleton)                       │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬─────────────────────────────────────────┘
                     │ requêtes SQL
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  BASE DE DONNÉES MySQL                       │
│  medicament, vente, ligne_vente, fournisseur, commande,     │
│  ligne_commande, utilisateur                                 │
└─────────────────────────────────────────────────────────────┘
```

## 4. Design Patterns Utilisés

### 4.1 Singleton Pattern
**Classe**: `DatabaseConnection`, `SessionManager`
**Objectif**: Garantir une seule instance de connexion à la base de données et une seule session utilisateur

### 4.2 DAO Pattern (Data Access Object)
**Classes**: `MedicamentDAO`, `VenteDAO`, `CommandeDAO`, `FournisseurDAO`, `UtilisateurDAO`
**Objectif**: Séparer la logique d'accès aux données de la logique métier

### 4.3 MVC Pattern (Model-View-Controller)
**Objectif**: Séparation des responsabilités
- **Model**: Entités et DAOs
- **View**: Interfaces JavaFX
- **Controller**: Logique métier

## 5. Concepts POO Appliqués

### 5.1 Encapsulation
- Attributs privés avec getters/setters
- Validation dans les contrôleurs

### 5.2 Héritage
- Pas d'héritage complexe (volontairement simple pour le contexte universitaire)

### 5.3 Polymorphisme
- Enum `Role` pour les types d'utilisateurs

### 5.4 Collections
- `ArrayList<>` pour les listes (lignes de vente, lignes de commande)
- `ObservableList<>` pour les tables JavaFX

### 5.5 Exceptions
- Gestion des `SQLException` dans les DAOs
- Messages d'erreur utilisateur dans les contrôleurs

### 5.6 Interfaces
- Pas d'interfaces explicites (simplicité pour projet universitaire)
- Mais structure DAO suit le pattern interface DAO

## 6. Flux de Données Principal

### Exemple: Enregistrement d'une Vente

```
1. Utilisateur remplit le panier (VenteView)
   ↓
2. Clic sur "Valider la vente"
   ↓
3. VenteView → VenteController.enregistrerVente()
   ↓
4. VenteController valide les données
   ↓
5. VenteController → VenteDAO.creer()
   ↓
6. VenteDAO exécute les requêtes SQL (transaction)
   - INSERT INTO vente
   - INSERT INTO ligne_vente (pour chaque ligne)
   ↓
7. VenteController → MedicamentController.diminuerStock()
   ↓
8. MedicamentDAO.mettreAJourStock()
   ↓
9. Retour de confirmation à VenteView
   ↓
10. Affichage message succès à l'utilisateur
```

## 7. Sécurité

- Authentification par login/mot de passe
- Gestion des rôles (PHARMACIEN vs PREPARATEUR)
- Contrôle d'accès dans les contrôleurs
- Transactions SQL pour garantir l'intégrité

## 8. Fonctionnalités Principales

1. **CRUD Complet** sur toutes les entités
2. **Système d'Alertes**
   - Stock minimum
   - Péremption < 3 mois
3. **Gestion des Ventes**
   - Panier dynamique
   - Mise à jour automatique du stock
4. **Gestion des Commandes**
   - Création manuelle
   - Création automatique pour articles en alerte
   - Réception avec mise à jour du stock
5. **Authentification et Autorisation**
6. **Interface Graphique Intuitive**

## 9. Technologies

- **Java 11**
- **JavaFX 17** (Interface graphique)
- **MySQL 8** (Base de données)
- **Maven** (Gestion de dépendances)
- **JDBC** (Connexion base de données)
