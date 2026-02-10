# 🚀 Commandes Rapides - SGPA

## Lancement de l'Application

### Linux/macOS
```bash
./run.sh
```

### Windows
```bash
run.bat
```

### Ligne de commande (tous OS)
```bash
mvn javafx:run
```

---

## Commandes Maven Utiles

### Compilation
```bash
# Nettoyer et compiler
mvn clean compile

# Compiler sans les tests
mvn compile -DskipTests
```

### Packaging
```bash
# Créer un JAR
mvn clean package

# Créer un JAR avec dépendances
mvn clean package assembly:single
```

### Tests et Vérifications
```bash
# Vérifier les dépendances
mvn dependency:tree

# Vérifier les mises à jour
mvn versions:display-dependency-updates

# Nettoyer complètement
mvn clean
```

---

## Gestion de la Base de Données

### Connexion MySQL
```bash
# Se connecter à MySQL
mysql -u root -p

# Se connecter directement à la base
mysql -u root -p pharmacie_db
```

### Import/Export
```bash
# Importer le schéma
mysql -u root -p pharmacie_db < database/schema.sql

# Exporter la base (sauvegarde)
mysqldump -u root -p pharmacie_db > backup.sql

# Restaurer depuis une sauvegarde
mysql -u root -p pharmacie_db < backup.sql
```

### Requêtes SQL Utiles
```sql
-- Voir les statistiques
USE pharmacie_db;
SELECT * FROM v_statistiques_ventes;

-- Voir les alertes stock
SELECT * FROM v_alertes_stock;

-- Voir les alertes péremption
SELECT * FROM v_alertes_peremption;

-- Compter les médicaments
SELECT COUNT(*) FROM medicament;

-- Voir le chiffre d'affaires
SELECT SUM(montant_total) FROM vente;

-- Réinitialiser les données de test
SOURCE database/schema.sql;
```

---

## Dépannage

### Problème de compilation
```bash
# Supprimer le cache Maven et recompiler
rm -rf ~/.m2/repository/com/pharmacie
mvn clean install
```

### Problème de connexion MySQL
```bash
# Vérifier le statut de MySQL
# Linux/macOS:
sudo systemctl status mysql

# Démarrer MySQL
sudo systemctl start mysql
```

### Problème JavaFX
```bash
# Vérifier les dépendances JavaFX
mvn dependency:tree | grep javafx
```

---

## Structure des Fichiers Clés

| Fichier | Description |
|---------|-------------|
| `pom.xml` | Configuration Maven |
| `database/schema.sql` | Schéma de la base de données |
| `src/main/java/com/pharmacie/Main.java` | Point d'entrée |
| `src/main/java/com/pharmacie/dao/DatabaseConnection.java` | Configuration BDD |
| `docs/` | Documentation complète |

---

## Comptes par Défaut

| Rôle | Login | Mot de passe |
|------|-------|--------------|
| Pharmacien (Admin) | `admin` | `admin123` |
| Préparateur | `vendeur` | `vendeur123` |

---

## URLs et Ports

| Service | URL | Port |
|---------|-----|------|
| MySQL | localhost | 3306 |
| Application | - | - |

---

## Logs et Débogage

### Voir les logs Maven
```bash
# Mode verbose
mvn -X javafx:run

# Mode debug
mvn -e javafx:run
```

### Logs de l'application
Les messages de log apparaissent dans la console où vous avez lancé l'application.

---

## IDE Shortcuts

### IntelliJ IDEA
- Ouvrir le projet : `File → Open → PROJET/`
- Exécuter : `Shift + F10`
- Déboguer : `Shift + F9`

### Eclipse
- Importer : `File → Import → Maven → Existing Maven Projects`
- Exécuter : `Ctrl + F11`
- Déboguer : `F11`

### VS Code
- Ouvrir : `File → Open Folder → PROJET/`
- Exécuter : Clic droit sur `Main.java` → `Run Java`

---

## Git (optionnel)

```bash
# Initialiser un dépôt
git init

# Ajouter tous les fichiers
git add .

# Premier commit
git commit -m "Initial commit - SGPA Project"

# Ignorer les fichiers compilés
# Déjà configuré dans .gitignore
```

---

## Checklist Avant Démonstration

- [ ] MySQL démarré
- [ ] Base de données créée et peuplée
- [ ] Application compile sans erreur
- [ ] Connexion avec admin/admin123 fonctionne
- [ ] Test d'une vente
- [ ] Vérification des alertes
- [ ] Test de création de commande

---

## Ressources

- **Documentation UML** : `docs/UML_Documentation.md`
- **Guide Installation** : `docs/Guide_Installation.md`
- **Résumé Projet** : `PROJET_RESUME.md`
- **README Principal** : `README.md`

---

**Tip** 💡 : Gardez ce fichier à portée de main pour référence rapide !
