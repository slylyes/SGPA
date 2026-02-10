-- ============================================================================
-- Script de migration : Ajout de la colonne 'actif' pour le soft delete
-- ============================================================================
-- Ce script permet de migrer une base de données existante en ajoutant
-- la colonne 'actif' à la table medicament.
-- ============================================================================

USE pharmacie_db;

-- Vérifier si la colonne existe, sinon l'ajouter
SET @col_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'pharmacie_db' 
    AND TABLE_NAME = 'medicament' 
    AND COLUMN_NAME = 'actif'
);

-- Ajouter la colonne actif si elle n'existe pas
SET @query = IF(@col_exists = 0,
    'ALTER TABLE medicament ADD COLUMN actif BOOLEAN DEFAULT TRUE AFTER seuil_minimum',
    'SELECT "La colonne actif existe déjà" AS Info'
);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Mettre à jour tous les médicaments existants pour les marquer comme actifs
UPDATE medicament SET actif = TRUE WHERE actif IS NULL;

-- Créer un index sur la colonne actif (si non existant)
SET @index_exists = (
    SELECT COUNT(*) 
    FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = 'pharmacie_db' 
    AND TABLE_NAME = 'medicament' 
    AND INDEX_NAME = 'idx_actif'
);

SET @query_index = IF(@index_exists = 0,
    'ALTER TABLE medicament ADD INDEX idx_actif (actif)',
    'SELECT "L\'index idx_actif existe déjà" AS Info'
);

PREPARE stmt_index FROM @query_index;
EXECUTE stmt_index;
DEALLOCATE PREPARE stmt_index;

-- Afficher un message de confirmation
SELECT 'Migration terminée : colonne actif ajoutée avec succès' AS Status;

-- Vérifier les résultats
SELECT 
    COUNT(*) as total_medicaments,
    SUM(CASE WHEN actif = TRUE THEN 1 ELSE 0 END) as medicaments_actifs,
    SUM(CASE WHEN actif = FALSE THEN 1 ELSE 0 END) as medicaments_archives
FROM medicament;
