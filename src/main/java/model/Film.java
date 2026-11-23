package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Film {

    private int idF;
    private String titre;
    private int annee;
    private List<Acteur> acteurs;

    public Film() {
        this.acteurs = new ArrayList<>();
    }

    public Film(int idF, String titre, int annee) {
        this();
        this.idF = idF;
        this.titre = titre;
        this.annee = annee;
    }

    public int getIdF() {
        return idF;
    }

    public void setIdF(int idF) {
        this.idF = idF;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public List<Acteur> getActeurs() {
        return acteurs;
    }

    public void setActeurs(List<Acteur> acteurs) {
        this.acteurs = acteurs;
    }

    public void addActeur(Acteur acteur) {
        this.acteurs.add(acteur);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Film : ").append(titre).append(" (").append(annee).append(") - Acteurs: ").append(acteurs.size());
        if (!acteurs.isEmpty()) {
            sb.append(" [");
            for (int i = 0; i < acteurs.size(); i++) {
                sb.append(acteurs.get(i).getPrenom()).append(" ").append(acteurs.get(i).getNom());
                if (i < acteurs.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
