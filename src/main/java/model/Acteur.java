package model;

import java.time.LocalDate;

public class Acteur {

    private int idA;
    private String nom;
    private String prenom;
    private LocalDate dnaiss;

    public Acteur() {
    }

    public Acteur(int idA, String nom, String prenom, LocalDate dnaiss) {
        this.idA = idA;
        this.nom = nom;
        this.prenom = prenom;
        this.dnaiss = dnaiss;
    }

    public int getIdA() {
        return idA;
    }

    public void setIdA(int idA) {
        this.idA = idA;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDnaiss() {
        return dnaiss;
    }

    public void setDnaiss(LocalDate dnaiss) {
        this.dnaiss = dnaiss;
    }

    @Override
    public String toString() {
        return "Acteur [ID=" + idA + ", nom=" + nom + ", prenom=" + prenom + ", dNaiss=" + dnaiss + "]";
    }
}
