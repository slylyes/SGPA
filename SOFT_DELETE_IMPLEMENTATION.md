# Système de Soft Delete (Suppression Logique)

## 📋 Vue d'ensemble

Le système implémente maintenant une **suppression logique** (soft delete) pour les médicaments au lieu d'une suppression physique de la base de données.

## ✅ Avantages

1. **Préservation de l'historique** : Les ventes et commandes passées restent intactes
2. **Traçabilité** : Possibilité de consulter les médicaments archivés
3. **Réversibilité** : Un médicament archivé peut être réactivé
4. **Intégrité des données** : Plus de problèmes de contraintes de clés étrangères

## 🔧 Modifications apportées

### 1. Base de données (`schema.sql`)
- Ajout de la colonne `actif BOOLEAN DEFAULT TRUE`
- Ajout d'un index sur `actif` pour optimiser les performances

### 2. Modèle (`Medicament.java`)
- Ajout de l'attribut `private boolean actif`
- Mise à jour des constructeurs
- Ajout des getters/setters : `isActif()` et `setActif()`

### 3. DAO (`MedicamentDAO.java`)
**Nouvelles méthodes :**
- `supprimer(int id)` : Archive le médicament (met `actif = FALSE`)
- `reactiver(int id)` : Réactive un médicament archivé (met `actif = TRUE`)
- `lireTousArchives()` : Récupère tous les médicaments archivés

**Méthodes modifiées :**
- `lireTous()` : Filtre uniquement les médicaments actifs (`WHERE actif = TRUE`)
- `rechercherParNom()` : Recherche uniquement parmi les actifs
- `getMedicamentsEnAlerteStock()` : Uniquement les actifs
- `getMedicamentsProchesPeremption()` : Uniquement les actifs
- `extraireMedicament()` : Inclut le champ `actif` dans la construction

### 4. Contrôleur (`MedicamentController.java`)
**Nouvelles méthodes :**
- `reactiverMedicament(int id)` : Réactive un médicament
- `getMedicamentsArchives()` : Liste les médicaments archivés

**Méthode renommée :**
- `supprimerMedicament()` : Archive maintenant au lieu de supprimer

## 🚀 Migration des données existantes

Pour les bases de données existantes, exécuter :

```bash
mysql -u root -p < database/migration_add_actif.sql
```

Ce script :
1. Ajoute la colonne `actif` si elle n'existe pas
2. Met tous les médicaments existants à `actif = TRUE`
3. Crée un index sur la colonne
4. Affiche un rapport de migration

## 📊 Utilisation

### Archiver un médicament
```java
MedicamentController controller = new MedicamentController();
boolean success = controller.supprimerMedicament(medicamentId);
// Le médicament est maintenant marqué comme inactif
```

### Réactiver un médicament
```java
boolean success = controller.reactiverMedicament(medicamentId);
// Le médicament redevient visible dans la liste principale
```

### Consulter les archives
```java
List<Medicament> archives = controller.getMedicamentsArchives();
// Retourne tous les médicaments avec actif = false
```

### Lister les médicaments actifs
```java
List<Medicament> actifs = controller.getTousMedicaments();
// Ne retourne que les médicaments avec actif = true
```

## 🔍 Comportement

| Action | Ancien système | Nouveau système |
|--------|----------------|-----------------|
| Supprimer un médicament | `DELETE FROM medicament` | `UPDATE medicament SET actif = FALSE` |
| Lister médicaments | Tous | Uniquement `actif = TRUE` |
| Rechercher | Tous | Uniquement `actif = TRUE` |
| Historique ventes | ❌ Erreur FK | ✅ Préservé |
| Historique commandes | ❌ Erreur FK | ✅ Préservé |

## 💡 Évolutions futures possibles

1. **Vue d'administration** : Ajouter une interface pour consulter et gérer les archives
2. **Statistiques** : Rapports sur les médicaments archivés
3. **Restauration** : Interface graphique pour réactiver les médicaments
4. **Audit trail** : Ajouter `date_archivage` et `archive_par` pour traçabilité complète
5. **Auto-archivage** : Archiver automatiquement les médicaments périmés

## ⚠️ Notes importantes

- Les médicaments archivés **restent dans la base de données**
- Les ventes et commandes historiques **continuent de fonctionner**
- Pour supprimer définitivement (si nécessaire) : requête SQL manuelle avec précautions
- Aucun impact sur les fonctionnalités existantes : l'application continue de fonctionner normalement
