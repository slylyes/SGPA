package com.pharmacie.model;

/**
 * Énumération des rôles utilisateur
 */
public enum Role {
    PHARMACIEN,    // Administrateur - accès complet
    PREPARATEUR    // Vendeur - accès limité (ventes et consultation stocks)
}
