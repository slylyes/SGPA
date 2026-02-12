package com.pharmacie.model;

/**
 * Classe représentant un médicament dans le classement des plus vendus
 */
public class TopMedicament {
    private int idMedicament;
    private String nomCommercial;
    private int quantiteTotaleVendue;
    private double chiffreAffaires;
    private int nombreVentes;

    public TopMedicament(int idMedicament, String nomCommercial, int quantiteTotaleVendue,
                         double chiffreAffaires, int nombreVentes) {
        this.idMedicament = idMedicament;
        this.nomCommercial = nomCommercial;
        this.quantiteTotaleVendue = quantiteTotaleVendue;
        this.chiffreAffaires = chiffreAffaires;
        this.nombreVentes = nombreVentes;
    }

    public int getIdMedicament() { return idMedicament; }
    public String getNomCommercial() { return nomCommercial; }
    public int getQuantiteTotaleVendue() { return quantiteTotaleVendue; }
    public double getChiffreAffaires() { return chiffreAffaires; }
    public int getNombreVentes() { return nombreVentes; }

    @Override
    public String toString() {
        return nomCommercial + " - " + quantiteTotaleVendue + " vendus - " 
               + String.format("%.2f", chiffreAffaires) + "€";
    }
}
