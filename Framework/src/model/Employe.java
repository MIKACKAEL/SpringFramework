package model;

import Annotations.AttributAnnotation;

public class Employe {

    @AttributAnnotation(value = "nom")
    private String nom;

    @AttributAnnotation(value = "prenom")
    private String prenom;

    @AttributAnnotation(value = "poste")
    private String poste;

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

    public String getPoste() {
        return poste;
    }
    public void setPoste(String poste) {
        this.poste = poste;
    }
}