# Guide d'Installation et d'Utilisation
## Système de Gestion Pharmacie Avancé (SGPA)

---

## 📋 Table des Matières
1. [Prérequis](#prérequis)
2. [Installation](#installation)
3. [Configuration](#configuration)
4. [Compilation et Exécution](#compilation-et-exécution)
5. [Guide d'Utilisation](#guide-dutilisation)
6. [Résolution des Problèmes](#résolution-des-problèmes)

---

## 🔧 Prérequis

### Logiciels Requis

1. **Java JDK 11 ou supérieur**
   ```bash
   # Vérifier la version
   java -version
   javac -version
   ```
   📥 Téléchargement: https://www.oracle.com/java/technologies/downloads/

2. **Apache Maven 3.6+**
   ```bash
   # Vérifier la version
   mvn -version
   ```
   📥 Téléchargement: https://maven.apache.org/download.cgi

3. **MySQL 8.0+**
   ```bash
   # Vérifier la version
   mysql --version
   ```
   📥 Téléchargement: https://dev.mysql.com/downloads/mysql/

4. **IDE Recommandé** (optionnel mais conseillé)
   - IntelliJ IDEA Community Edition
   - Eclipse IDE for Java Developers
   - Visual Studio Code avec extension Java

---

## 📥 Installation

### Étape 1: Cloner ou Extraire le Projet

Si vous avez reçu le projet en archive:
```bash
# Extraire l'archive
unzip sgpa-pharmacie.zip
cd PROJET
```

### Étape 2: Installer MySQL

1. **Windows**: Télécharger MySQL Installer
2. **Linux (Ubuntu/Debian)**:
   ```bash
   sudo apt update
   sudo apt install mysql-server
   sudo systemctl start mysql
   ```
3. **macOS**:
   ```bash
   brew install mysql
   brew services start mysql
   ```

### Étape 3: Configurer MySQL

```bash
# Se connecter à MySQL
mysql -u root -p

# Créer l'utilisateur et la base de données
CREATE DATABASE pharmacie_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'pharmacie_user'@'localhost' IDENTIFIED BY 'pharmacie123';
GRANT ALL PRIVILEGES ON pharmacie_db.* TO 'pharmacie_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Étape 4: Importer le Schéma de la Base de Données

```bash
# Depuis le répertoire du projet
mysql -u root -p pharmacie_db < database/schema.sql
```

Ou depuis MySQL:
```sql
SOURCE /chemin/vers/PROJET/database/schema.sql;
```

---

## ⚙️ Configuration

### Modifier les Paramètres de Connexion

Éditer le fichier: `src/main/java/com/pharmacie/dao/DatabaseConnection.java`

```java
// Modifier ces lignes selon votre configuration
private static final String URL = "jdbc:mysql://localhost:3306/pharmacie_db";
private static final String USER = "root";  // ou "pharmacie_user"
private static final String PASSWORD = "";  // votre mot de passe MySQL
```

**Options de configuration:**

| Configuration | Par défaut | À modifier |
|---------------|-----------|------------|
| Host | localhost | Si MySQL est sur un autre serveur |
| Port | 3306 | Si vous utilisez un port différent |
| Database | pharmacie_db | Si vous avez changé le nom |
| User | root | Recommandé: créer un utilisateur dédié |
| Password | "" | Toujours définir un mot de passe |

---

## 🚀 Compilation et Exécution

### Option 1: Ligne de Commande (Recommandé)

```bash
# Se placer dans le répertoire du projet
cd PROJET

# Compiler le projet
mvn clean compile

# Exécuter l'application
mvn javafx:run
```

### Option 2: Créer un JAR Exécutable

```bash
# Compiler et packager
mvn clean package

# Exécuter le JAR
java -jar target/sgpa-1.0-SNAPSHOT.jar
```

### Option 3: Depuis un IDE

**IntelliJ IDEA:**
1. File → Open → Sélectionner le dossier PROJET
2. Attendre le chargement Maven
3. Clic droit sur `Main.java` → Run 'Main.main()'

**Eclipse:**
1. File → Import → Maven → Existing Maven Projects
2. Sélectionner le dossier PROJET
3. Clic droit sur le projet → Run As → Java Application

**Visual Studio Code:**
1. File → Open Folder → Sélectionner PROJET
2. Installer l'extension "Extension Pack for Java"
3. Ouvrir `Main.java` et cliquer sur "Run"

---

## 📖 Guide d'Utilisation

### 1. Connexion

**Comptes par défaut:**

| Rôle | Login | Mot de passe | Accès |
|------|-------|--------------|-------|
| Pharmacien (Admin) | `admin` | `admin123` | Complet |
| Préparateur (Vendeur) | `vendeur` | `vendeur123` | Limité |

⚠️ **Important**: Changez ces mots de passe après la première connexion !

### 2. Menu Principal

Après connexion, vous accédez au menu principal avec 6 modules:

#### 📦 Gestion des Médicaments
- **Ajouter**: Créer une nouvelle fiche médicament
- **Modifier**: Mettre à jour les informations
- **Supprimer**: Retirer un médicament (attention: vérifier qu'il n'y a pas de ventes associées)
- **Rechercher**: Filtrer par nom commercial

**Champs obligatoires:**
- Nom commercial
- Principe actif
- Forme galénique (Comprimé, Sirop, Crème, etc.)
- Dosage
- Prix public
- Stock actuel
- Seuil minimum
- Date de péremption

#### 💰 Gestion des Ventes
1. Sélectionner un médicament (ou rechercher)
2. Définir la quantité
3. Ajouter au panier
4. Cocher "Vente avec ordonnance" si nécessaire
5. Valider la vente

**Le système vérifie automatiquement:**
- Stock disponible
- Calcul du montant total
- Mise à jour du stock après validation

#### ⚠️ Alertes Stock et Péremption

**Onglet Alertes Stock:**
- Liste des médicaments dont le stock ≤ seuil minimum
- 🔴 RUPTURE: stock = 0
- 🟠 ALERTE: stock ≤ seuil

**Onglet Alertes Péremption:**
- Médicaments périmant dans moins de 3 mois
- 🔴 Périmé: date dépassée
- 🟠 Urgent: < 30 jours
- 🟡 Attention: < 90 jours

#### 📋 Commandes Fournisseurs (Pharmacien uniquement)

**Créer une commande manuelle:**
1. Sélectionner un fournisseur
2. Ajouter des médicaments avec quantités
3. Créer la commande (statut: EN_ATTENTE)

**Commande automatique:**
1. Cliquer sur "Commande Automatique"
2. Choisir un fournisseur
3. Le système crée automatiquement une commande pour tous les médicaments en alerte

**Réceptionner une commande:**
1. Sélectionner une commande EN_ATTENTE
2. Cliquer sur "Marquer comme Reçue"
3. Les stocks sont automatiquement mis à jour

#### 🏢 Gestion des Fournisseurs (Pharmacien uniquement)
- Ajouter/Modifier/Supprimer des fournisseurs
- Informations: Nom, Contact, Adresse

#### 👥 Gestion des Utilisateurs (Pharmacien uniquement)
- Créer de nouveaux comptes
- 2 rôles disponibles: PHARMACIEN ou PREPARATEUR
- Modifier les informations
- Supprimer un utilisateur (sauf soi-même)

### 3. Déconnexion
Cliquer sur le bouton "Déconnexion" en haut à droite

---

## 🔍 Résolution des Problèmes

### Problème: "Connection refused" ou "Access denied"

**Solution:**
1. Vérifier que MySQL est démarré:
   ```bash
   # Linux/macOS
   sudo systemctl status mysql
   
   # Windows (dans Services)
   services.msc → Chercher MySQL
   ```

2. Vérifier les identifiants dans `DatabaseConnection.java`

3. Tester la connexion MySQL:
   ```bash
   mysql -u root -p
   ```

### Problème: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"

**Solution:**
```bash
# Réinstaller les dépendances Maven
mvn clean install
```

### Problème: "JavaFX runtime components are missing"

**Solution:**
Le projet utilise le plugin `javafx-maven-plugin`. Toujours lancer avec:
```bash
mvn javafx:run
```

Pas avec:
```bash
mvn exec:java  # ❌ N'utilisez pas cette commande
```

### Problème: L'interface n'affiche pas correctement

**Solution:**
Vérifier que JavaFX est correctement installé:
```bash
mvn dependency:tree | grep javafx
```

### Problème: "Table doesn't exist"

**Solution:**
Réimporter le schéma SQL:
```bash
mysql -u root -p pharmacie_db < database/schema.sql
```

### Problème: Erreur lors de la compilation Maven

**Solution:**
```bash
# Nettoyer et recompiler
mvn clean
mvn compile

# Si le problème persiste, supprimer le cache Maven
rm -rf ~/.m2/repository
mvn clean install
```

---

## 📊 Données de Test

Le script SQL inclut des données de test:
- 2 utilisateurs (admin, vendeur)
- 4 fournisseurs
- 20 médicaments (dont certains en alerte)
- 5 ventes d'exemple
- 3 commandes d'exemple

Pour réinitialiser les données:
```bash
mysql -u root -p pharmacie_db < database/schema.sql
```

---

## 🎓 Fonctionnalités Avancées

### Requêtes SQL Utiles

```sql
-- Voir les statistiques
SELECT * FROM v_statistiques_ventes;

-- Voir les alertes stock
SELECT * FROM v_alertes_stock;

-- Voir les alertes péremption
SELECT * FROM v_alertes_peremption;

-- Chiffre d'affaires du jour
SELECT SUM(montant_total) FROM vente 
WHERE DATE(date_heure) = CURDATE();

-- Top 10 médicaments vendus
SELECT m.nom_commercial, SUM(lv.quantite) as total_vendu
FROM ligne_vente lv
JOIN medicament m ON lv.id_medicament = m.id
GROUP BY m.id
ORDER BY total_vendu DESC
LIMIT 10;
```

---

## 📞 Support

Pour toute question ou problème:
1. Consulter la documentation UML dans `docs/UML_Documentation.md`
2. Vérifier les logs dans la console
3. Examiner les messages d'erreur dans l'interface

---

## ✅ Checklist de Vérification

Avant de soumettre le projet, vérifiez:

- [ ] MySQL installé et démarré
- [ ] Base de données créée et peuplée
- [ ] Paramètres de connexion configurés
- [ ] Projet compile sans erreur: `mvn clean compile`
- [ ] Application se lance: `mvn javafx:run`
- [ ] Connexion avec admin/admin123 fonctionne
- [ ] Création d'un médicament fonctionne
- [ ] Vente avec mise à jour du stock fonctionne
- [ ] Alertes affichent les bons médicaments
- [ ] Documentation présente et complète

---

## 📝 Notes Importantes

1. **Mot de passe en clair**: Pour simplifier le projet universitaire, les mots de passe sont stockés en clair. En production, utilisez toujours du hachage (BCrypt, etc.)

2. **Validation côté client**: La validation est basique. En production, ajoutez plus de contrôles.

3. **Concurrence**: Le système ne gère pas les accès concurrents multiples. Pour une utilisation réelle, ajoutez un système de verrous.

4. **Sauvegarde**: Pensez à sauvegarder régulièrement la base de données:
   ```bash
   mysqldump -u root -p pharmacie_db > backup.sql
   ```

---

**Projet développé dans le cadre du cours de POO - M1 MIAGE**
