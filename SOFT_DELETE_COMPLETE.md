# ✅ Soft Delete Complet - Fournisseurs et Utilisateurs

## 🎯 Problèmes résolus

### Problème 1 : Fournisseur
```
Cannot delete or update a parent row: a foreign key constraint fails 
(`pharmacie_db`.`commande`, CONSTRAINT `commande_ibfk_1` 
FOREIGN KEY (`id_fournisseur`) REFERENCES `fournisseur` (`id`))
```
**Cause :** Suppression impossible si le fournisseur a des commandes en historique

### Problème 2 : Utilisateur
```
Cannot delete or update a parent row: a foreign key constraint fails 
(`pharmacie_db`.`vente`, CONSTRAINT `vente_ibfk_1` 
FOREIGN KEY (`id_utilisateur`) REFERENCES `utilisateur` (`id`))
```
**Cause :** Suppression impossible si l'utilisateur a effectué des ventes

---

## ✅ Solution : Soft Delete

Au lieu de supprimer physiquement (`DELETE`), on archive logiquement en mettant `actif = FALSE`.

---

## 📝 Modifications effectuées

### 1. Base de données

#### Schema ([schema.sql](database/schema.sql))
```sql
-- Table fournisseur
ALTER TABLE fournisseur 
ADD COLUMN actif BOOLEAN DEFAULT TRUE;
ADD INDEX idx_actif (actif);

-- Table utilisateur  
ALTER TABLE utilisateur 
ADD COLUMN actif BOOLEAN DEFAULT TRUE;
ADD INDEX idx_actif (actif);
```

#### Migration ([migration_add_actif_complete.sql](database/migration_add_actif_complete.sql))
- ✅ Ajout colonne `actif` à `fournisseur`
- ✅ Ajout colonne `actif` à `utilisateur`
- ✅ Création des index sur `actif`
- ✅ Mise à jour des données existantes (actif = TRUE)

**Résultat migration :**
```
FOURNISSEUR : 3 actifs, 0 archivés
UTILISATEUR : 2 actifs, 0 archivés
```

---

### 2. Modèles Java

#### [Fournisseur.java](src/main/java/com/pharmacie/model/Fournisseur.java)
```java
public class Fournisseur {
    private boolean actif;  // ✅ Nouveau
    
    // Constructeur complet mis à jour
    public Fournisseur(int id, String nom, String contact, 
                       String adresse, boolean actif)
    
    // Constructeur sans ID : actif = true par défaut
    public Fournisseur(String nom, String contact, String adresse)
    
    // Getters/Setters
    public boolean isActif()
    public void setActif(boolean actif)
}
```

#### [Utilisateur.java](src/main/java/com/pharmacie/model/Utilisateur.java)
```java
public class Utilisateur {
    private boolean actif;  // ✅ Nouveau
    
    // Constructeur complet mis à jour
    public Utilisateur(int id, String login, String motDePasse,
                      String nom, String prenom, Role role, boolean actif)
    
    // Constructeur sans ID : actif = true par défaut
    public Utilisateur(String login, String motDePasse, String nom,
                      String prenom, Role role)
    
    // Getters/Setters
    public boolean isActif()
    public void setActif(boolean actif)
}
```

---

### 3. DAOs

#### [FournisseurDAO.java](src/main/java/com/pharmacie/dao/FournisseurDAO.java)

**Méthodes modifiées :**
```java
// Création : inclut actif
creer(Fournisseur) : actif = true

// Lecture : filtre les actifs
lireTous() : WHERE actif = TRUE

// Suppression : archivage logique
supprimer(int id) : UPDATE actif = FALSE
```

**Nouvelles méthodes :**
```java
// Réactive un fournisseur archivé
boolean reactiver(int id) : UPDATE actif = TRUE

// Liste les fournisseurs archivés
List<Fournisseur> lireTousArchives() : WHERE actif = FALSE

// Extraction : inclut le champ actif
extraireFournisseur(ResultSet) : rs.getBoolean("actif")
```

#### [UtilisateurDAO.java](src/main/java/com/pharmacie/dao/UtilisateurDAO.java)

**Méthodes modifiées :**
```java
// Authentification : vérifie actif
authentifier(login, mdp) : WHERE actif = TRUE

// Création : inclut actif
creer(Utilisateur) : actif = true

// Lecture : filtre les actifs
lireTous() : WHERE actif = TRUE

// Suppression : archivage logique
supprimer(int id) : UPDATE actif = FALSE
```

**Nouvelles méthodes :**
```java
// Réactive un utilisateur archivé
boolean reactiver(int id) : UPDATE actif = TRUE

// Liste les utilisateurs archivés
List<Utilisateur> lireTousArchives() : WHERE actif = FALSE

// Extraction : inclut le champ actif
extraireUtilisateur(ResultSet) : rs.getBoolean("actif")
```

---

## 🚀 Tests effectués

### ✅ Migration SQL
```bash
cat database/migration_add_actif_complete.sql | mysql -u root -p pharmacie_db
```
**Résultat :** Migration réussie
- 3 fournisseurs actifs
- 2 utilisateurs actifs

### ✅ Structure des tables
```sql
DESC fournisseur;  -- actif tinyint(1) DEFAULT 1 ✅
DESC utilisateur;  -- actif tinyint(1) DEFAULT 1 ✅
```

### ✅ Compilation
```bash
mvn clean compile
```
**Résultat :** BUILD SUCCESS

---

## 📊 Comparaison Avant/Après

| Entité | Action | Avant (DELETE) | Après (Soft Delete) |
|--------|--------|----------------|---------------------|
| **Fournisseur** | Supprimer avec commandes | ❌ Erreur FK | ✅ Archive (actif=0) |
| | Liste fournisseurs | Tous | Seulement actifs |
| | Historique commandes | ❌ Cassé | ✅ Intact |
| **Utilisateur** | Supprimer avec ventes | ❌ Erreur FK | ✅ Archive (actif=0) |
| | Connexion archivé | ✅ Possible | ❌ Bloqué |
| | Liste utilisateurs | Tous | Seulement actifs |
| | Historique ventes | ❌ Cassé | ✅ Intact |

---

## 💡 Utilisation

### Archiver un fournisseur
```java
FournisseurDAO dao = new FournisseurDAO();
boolean success = dao.supprimer(fournisseurId);
// Le fournisseur est marqué actif = FALSE
```

### Réactiver un fournisseur
```java
boolean success = dao.reactiver(fournisseurId);
// Le fournisseur redevient visible
```

### Lister les archives
```java
List<Fournisseur> archives = dao.lireTousArchives();
```

### Archiver un utilisateur
```java
UtilisateurDAO dao = new UtilisateurDAO();
boolean success = dao.supprimer(utilisateurId);
// L'utilisateur ne peut plus se connecter
```

### Vérifier en base
```sql
-- Fournisseurs actifs
SELECT * FROM fournisseur WHERE actif = TRUE;

-- Fournisseurs archivés
SELECT * FROM fournisseur WHERE actif = FALSE;

-- Utilisateurs actifs
SELECT * FROM utilisateur WHERE actif = TRUE;

-- Utilisateurs archivés
SELECT * FROM utilisateur WHERE actif = FALSE;
```

---

## ⚠️ Important : Sécurité

### Utilisateur archivé ne peut plus se connecter
La méthode `authentifier()` vérifie maintenant `actif = TRUE` :
```java
String sql = "SELECT * FROM utilisateur 
              WHERE login = ? AND mot_de_passe = ? AND actif = TRUE";
```

✅ **Avantage :** Un utilisateur archivé ne peut plus accéder au système, mais son historique de ventes est préservé.

---

## 🎓 Résumé des entités avec soft delete

| Entité | Table | Colonne actif | Contrainte FK | Archivage |
|--------|-------|---------------|---------------|-----------|
| **Médicament** | `medicament` | ✅ | `ligne_vente`, `ligne_commande` | ✅ Opérationnel |
| **Fournisseur** | `fournisseur` | ✅ | `commande` | ✅ Opérationnel |
| **Utilisateur** | `utilisateur` | ✅ | `vente` | ✅ Opérationnel |

---

## 📁 Fichiers modifiés

### Base de données
- [schema.sql](database/schema.sql) - Tables mises à jour
- [migration_add_actif_complete.sql](database/migration_add_actif_complete.sql) - Script de migration

### Modèles
- [Fournisseur.java](src/main/java/com/pharmacie/model/Fournisseur.java) - Ajout actif
- [Utilisateur.java](src/main/java/com/pharmacie/model/Utilisateur.java) - Ajout actif

### DAOs
- [FournisseurDAO.java](src/main/java/com/pharmacie/dao/FournisseurDAO.java) - Soft delete
- [UtilisateurDAO.java](src/main/java/com/pharmacie/dao/UtilisateurDAO.java) - Soft delete

---

## ✨ Conclusion

Les trois entités principales de l'application utilisent maintenant le **soft delete** :

1. ✅ **Médicaments** : Archivage préserve ventes et commandes
2. ✅ **Fournisseurs** : Archivage préserve historique commandes
3. ✅ **Utilisateurs** : Archivage préserve historique ventes + bloque connexion

**Plus aucune erreur de contrainte de clé étrangère !** 🎉

L'intégrité référentielle est maintenue tout en permettant l'archivage des données obsolètes.
