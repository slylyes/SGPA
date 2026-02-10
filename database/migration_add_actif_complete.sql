-- ============================================================================
-- Script de migration : Ajout de la colonne 'actif' pour tous les soft deletes
-- ============================================================================
-- Ce script ajoute les colonnes 'actif' aux tables :
--   - fournisseur
--   - utilisateur
-- ============================================================================

USE pharmacie_db;

-- ============================================================================
-- FOURNISSEUR
-- ============================================================================

-- Vérifier si la colonne existe pour fournisseur
SET @col_exists_fournisseur = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'pharmacie_db' 
    AND TABLE_NAME = 'fournisseur' 
    AND COLUMN_NAME = 'actif'
);

-- Ajouter la colonne actif à fournisseur si elle n'existe pas
SET @query_fournisseur = IF(@col_exists_fournisseur = 0,
    'ALTER TABLE fournisseur ADD COLUMN actif BOOLEAN DEFAULT TRUE AFTER adresse',
    'SELECT "La colonne actif existe déjà dans fournisseur" AS Info'
);

PREPARE stmt_fournisseur FROM @query_fournisseur;
EXECUTE stmt_fournisseur;
DEALLOCATE PREPARE stmt_fournisseur;

-- Mettre à jour tous les fournisseurs existants
UPDATE fournisseur SET actif = TRUE WHERE actif IS NULL;

-- Créer un index sur actif pour fournisseur
SET @index_exists_fournisseur = (
    SELECT COUNT(*) 
    FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = 'pharmacie_db' 
    AND TABLE_NAME = 'fournisseur' 
    AND INDEX_NAME = 'idx_actif'
);

SET @query_index_fournisseur = IF(@index_exists_fournisseur = 0,
    'ALTER TABLE fournisseur ADD INDEX idx_actif (actif)',
    'SELECT "L\'index idx_actif existe déjà dans fournisseur" AS Info'
);

PREPARE stmt_index_fournisseur FROM @query_index_fournisseur;
EXECUTE stmt_index_fournisseur;
DEALLOCATE PREPARE stmt_index_fournisseur;

-- ============================================================================
-- UTILISATEUR
-- ============================================================================

-- Vérifier si la colonne existe pour utilisateur
SET @col_exists_utilisateur = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'pharmacie_db' 
    AND TABLE_NAME = 'utilisateur' 
    AND COLUMN_NAME = 'actif'
);

-- Ajouter la colonne actif à utilisateur si elle n'existe pas
SET @query_utilisateur = IF(@col_exists_utilisateur = 0,
    'ALTER TABLE utilisateur ADD COLUMN actif BOOLEAN DEFAULT TRUE AFTER role',
    'SELECT "La colonne actif existe déjà dans utilisateur" AS Info'
);

PREPARE stmt_utilisateur FROM @query_utilisateur;
EXECUTE stmt_utilisateur;
DEALLOCATE PREPARE stmt_utilisateur;

-- Mettre à jour tous les utilisateurs existants
UPDATE utilisateur SET actif = TRUE WHERE actif IS NULL;

-- Créer un index sur actif pour utilisateur
SET @index_exists_utilisateur = (
    SELECT COUNT(*) 
    FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = 'pharmacie_db' 
    AND TABLE_NAME = 'utilisateur' 
    AND INDEX_NAME = 'idx_actif'
);

SET @query_index_utilisateur = IF(@index_exists_utilisateur = 0,
    'ALTER TABLE utilisateur ADD INDEX idx_actif (actif)',
    'SELECT "L\'index idx_actif existe déjà dans utilisateur" AS Info'
);

PREPARE stmt_index_utilisateur FROM @query_index_utilisateur;
EXECUTE stmt_index_utilisateur;
DEALLOCATE PREPARE stmt_index_utilisateur;

-- ============================================================================
-- RAPPORT DE MIGRATION
-- ============================================================================

SELECT 'Migration terminée : colonnes actif ajoutées avec succès' AS Status;

-- Vérifier les résultats pour fournisseur
SELECT 
    'FOURNISSEUR' AS Table_Name,
    COUNT(*) as total,
    SUM(CASE WHEN actif = TRUE THEN 1 ELSE 0 END) as actifs,
    SUM(CASE WHEN actif = FALSE THEN 1 ELSE 0 END) as archives
FROM fournisseur

UNION ALL

-- Vérifier les résultats pour utilisateur
SELECT 
    'UTILISATEUR' AS Table_Name,
    COUNT(*) as total,
    SUM(CASE WHEN actif = TRUE THEN 1 ELSE 0 END) as actifs,
    SUM(CASE WHEN actif = FALSE THEN 1 ELSE 0 END) as archives
FROM utilisateur;
