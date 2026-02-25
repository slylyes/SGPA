package com.pharmacie.model;


public class RapportVente {
    private int annee;
    private int mois;
    private String moisLibelle;
    private int nombreVentes;
    private double chiffreAffaires;
    private int quantiteTotale;

    public RapportVente(int annee, int mois, String moisLibelle, int nombreVentes, 
                        double chiffreAffaires, int quantiteTotale) {
        this.annee = annee;
        this.mois = mois;
        this.moisLibelle = moisLibelle;
        this.nombreVentes = nombreVentes;
        this.chiffreAffaires = chiffreAffaires;
        this.quantiteTotale = quantiteTotale;
    }

    public int getAnnee() { return annee; }
    public int getMois() { return mois; }
    public String getMoisLibelle() { return moisLibelle; }
    public int getNombreVentes() { return nombreVentes; }
    public double getChiffreAffaires() { return chiffreAffaires; }
    public int getQuantiteTotale() { return quantiteTotale; }

    public String getPeriode() {
        return moisLibelle + " " + annee;
    }

    @Override
    public String toString() {
        return getPeriode() + " - CA: " + String.format("%.2f", chiffreAffaires) + "€";
    }
}
