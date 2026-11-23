import dao.DaoFactory;
import model.Acteur;
import model.Film;

import java.time.LocalDate;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        DaoFactory.TypeBase type = args.length == 0 ? DaoFactory.TypeBase.MONGODB
                : DaoFactory.TypeBase.valueOf(args[0].toUpperCase(Locale.ROOT));
        testerBaseDeDonnees(type);
        System.out.println("CRUD et associations vérifiés sur " + type);
    }

    public static void testerBaseDeDonnees(DaoFactory.TypeBase type) {
        DaoFactory factory = DaoFactory.getDaoFactory(type);
        var acteurs = factory.getDaoActeur();
        var films = factory.getDaoFilm();
        Acteur acteur = new Acteur(0, "Démo", "Camille", null);
        Film film = new Film(0, "Film de démonstration", 2024);

        try {
            acteurs.create(acteur);
            int idActeur = acteur.getIdA();
            verifier(idActeur > 0 && acteurs.read(idActeur) != null, "création de l'acteur");
            verifier(acteurs.read(idActeur).getDnaiss() == null, "lecture d'une date absente");
            verifier(acteurs.findAll().stream().anyMatch(a -> a.getIdA() == idActeur), "liste des acteurs");

            acteur.setDnaiss(LocalDate.of(2000, 1, 15));
            acteurs.update(acteur);
            verifier(acteur.getDnaiss().equals(acteurs.read(idActeur).getDnaiss()), "modification de la date");

            films.create(film);
            int idFilm = film.getIdF();
            verifier(idFilm > 0 && films.read(idFilm) != null, "création du film");
            verifier(films.read(idFilm).getActeurs().isEmpty(), "lecture d'un film sans acteur");
            films.addActeurToFilm(idFilm, idActeur);
            films.addActeurToFilm(idFilm, idActeur);
            verifier(films.read(idFilm).getActeurs().size() == 1
                    && films.read(idFilm).getActeurs().get(0).getIdA() == idActeur, "association unique");
            verifier(films.findAll().stream().anyMatch(f -> f.getIdF() == idFilm
                    && f.getActeurs().stream().anyMatch(a -> a.getIdA() == idActeur)), "liste des films et acteurs");

            film.setAnnee(2025);
            films.update(film);
            verifier(films.read(idFilm).getAnnee() == 2025
                    && films.read(idFilm).getActeurs().size() == 1, "modification du film et conservation du lien");
            films.removeActeurFromFilm(idFilm, idActeur);
            verifier(films.read(idFilm).getActeurs().isEmpty(), "retrait de l'association");
            films.addActeurToFilm(idFilm, idActeur);
            acteurs.delete(idActeur);
            acteur.setIdA(0);
            verifier(acteurs.read(idActeur) == null && films.read(idFilm).getActeurs().isEmpty(),
                    "suppression de l'acteur et du lien");

            films.delete(idFilm);
            film.setIdF(0);
            verifier(films.read(idFilm) == null, "suppression du film");
        } finally {
            try {
                if (film.getIdF() != 0) {
                    films.delete(film.getIdF());
                }
            } finally {
                if (acteur.getIdA() != 0) {
                    acteurs.delete(acteur.getIdA());
                }
            }
        }
    }

    private static void verifier(boolean condition, String operation) {
        if (!condition) {
            throw new IllegalStateException("Échec du scénario CRUD : " + operation);
        }
    }
}
