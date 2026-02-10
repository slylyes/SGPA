# 🧪 Guide de Test - Système de Soft Delete

## Prérequis
✅ Migration de la base de données effectuée (colonne `actif` ajoutée)
✅ Compilation réussie : `mvn clean compile`
✅ Tous les médicaments existants sont marqués comme actifs

## 🎯 Scénario de test

### 1️⃣ Lancer l'application
```bash
mvn javafx:run
```

**Login :** admin / admin123

---

### 2️⃣ Tester l'archivage d'un médicament

1. Aller dans **"Gestion des Médicaments"**
2. Sélectionner un médicament (ex: Doliprane)
3. Cliquer sur **"Supprimer"**
4. Confirmer la suppression

**✅ Résultat attendu :**
- Message : "Médicament supprimé avec succès"
- Le médicament disparaît de la liste
- **MAIS** il reste dans la base de données avec `actif = 0`

---

### 3️⃣ Vérifier que l'historique est préservé

#### A. Vérifier les ventes passées
```bash
echo "SELECT v.id, m.nom_commercial, lv.quantite 
FROM vente v 
JOIN ligne_vente lv ON v.id = lv.id_vente 
JOIN medicament m ON lv.id_medicament = m.id 
WHERE m.actif = 0;" | mysql -u root -p pharmacie_db
```

#### B. Vérifier les commandes passées
```bash
echo "SELECT c.id, m.nom_commercial, lc.quantite 
FROM commande c 
JOIN ligne_commande lc ON c.id = lc.id_commande 
JOIN medicament m ON lc.id_medicament = m.id 
WHERE m.actif = 0;" | mysql -u root -p pharmacie_db
```

**✅ Résultat attendu :**
- Les ventes et commandes contenant le médicament archivé sont toujours accessibles
- Aucune erreur de contrainte de clé étrangère

---

### 4️⃣ Vérifier dans la base de données

```bash
# Lister les médicaments actifs
echo "SELECT id, nom_commercial, actif FROM medicament WHERE actif = 1;" \
| mysql -u root -p pharmacie_db

# Lister les médicaments archivés
echo "SELECT id, nom_commercial, actif FROM medicament WHERE actif = 0;" \
| mysql -u root -p pharmacie_db
```

**✅ Résultat attendu :**
- Le médicament "supprimé" apparaît dans la liste des archivés avec `actif = 0`
- Les autres médicaments ont `actif = 1`

---

### 5️⃣ Tester la réactivation (via code)

Pour réactiver un médicament archivé, il faut actuellement utiliser le contrôleur :

```java
MedicamentController controller = new MedicamentController();
boolean success = controller.reactiverMedicament(1); // ID du médicament
```

Ou via SQL directement :
```bash
echo "UPDATE medicament SET actif = 1 WHERE id = 1;" | mysql -u root -p pharmacie_db
```

**✅ Résultat attendu :**
- Le médicament réapparaît dans la liste des médicaments actifs

---

### 6️⃣ Tester les autres fonctionnalités

#### Recherche
1. Dans "Gestion des Médicaments"
2. Utiliser la barre de recherche
3. Vérifier que seuls les médicaments actifs sont retournés

#### Alertes de stock
1. Aller dans "Alertes"
2. Vérifier que les médicaments archivés n'apparaissent pas dans les alertes

#### Ventes
1. Créer une nouvelle vente
2. Vérifier que seuls les médicaments actifs sont disponibles dans la liste

---

## 🔍 Vérifications SQL avancées

### Compter les médicaments par statut
```sql
SELECT 
    SUM(CASE WHEN actif = 1 THEN 1 ELSE 0 END) AS actifs,
    SUM(CASE WHEN actif = 0 THEN 1 ELSE 0 END) AS archives,
    COUNT(*) AS total
FROM medicament;
```

### Trouver les médicaments archivés avec des ventes
```sql
SELECT 
    m.id,
    m.nom_commercial,
    COUNT(DISTINCT v.id) AS nombre_ventes,
    SUM(lv.quantite) AS quantite_totale_vendue
FROM medicament m
JOIN ligne_vente lv ON m.id = lv.id_medicament
JOIN vente v ON lv.id_vente = v.id
WHERE m.actif = 0
GROUP BY m.id, m.nom_commercial;
```

### Médicaments archivés avec commandes
```sql
SELECT 
    m.id,
    m.nom_commercial,
    COUNT(DISTINCT c.id) AS nombre_commandes,
    SUM(lc.quantite) AS quantite_totale_commandee
FROM medicament m
JOIN ligne_commande lc ON m.id = lc.id_medicament
JOIN commande c ON lc.id_commande = c.id
WHERE m.actif = 0
GROUP BY m.id, m.nom_commercial;
```

---

## ✅ Checklist de validation

- [ ] Migration SQL exécutée avec succès
- [ ] Compilation Maven réussie
- [ ] Application se lance sans erreur
- [ ] Archivage d'un médicament fonctionne
- [ ] Le médicament archivé disparaît de la liste
- [ ] L'historique des ventes est préservé
- [ ] L'historique des commandes est préservé
- [ ] La recherche n'affiche que les médicaments actifs
- [ ] Les alertes n'affichent que les médicaments actifs
- [ ] Les ventes ne proposent que les médicaments actifs

---

## 🐛 Problèmes potentiels et solutions

### Problème : Erreur de compilation
**Solution :** 
```bash
mvn clean compile
```

### Problème : Médicament archivé toujours visible
**Vérifier :**
```sql
SELECT actif FROM medicament WHERE id = X;
```
Doit retourner `0` pour un médicament archivé.

### Problème : Erreur SQL lors de la migration
**Solution :** Le script est idempotent, vous pouvez le relancer :
```bash
cat database/migration_add_actif.sql | mysql -u root -p pharmacie_db
```

---

## 📊 Résultat attendu final

| Action | Avant (DELETE) | Après (Soft Delete) |
|--------|----------------|---------------------|
| Supprimer médicament | ❌ Erreur FK si utilisé | ✅ Archive (actif=0) |
| Liste médicaments | Tous sauf supprimés | Seulement actifs |
| Historique ventes | ❌ Cassé si supprimé | ✅ Toujours intact |
| Historique commandes | ❌ Cassé si supprimé | ✅ Toujours intact |
| Réactivation | ❌ Impossible | ✅ Possible |

---

## 🎓 Conclusion

Le système de soft delete permet de :
- ✅ Supprimer un médicament sans casser l'historique
- ✅ Conserver l'intégrité des données
- ✅ Réactiver un médicament si nécessaire
- ✅ Maintenir les statistiques et rapports cohérents
