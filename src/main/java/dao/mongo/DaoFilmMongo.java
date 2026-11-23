package dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dao.DaoFilm;
import model.Film;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.pull;

public class DaoFilmMongo implements DaoFilm {
    private final MongoDatabase database = MongoConnection.getInstance();
    private final MongoCollection<Document> collection = database.getCollection("Films");
    private final DaoActeurMongo daoActeur = new DaoActeurMongo();

    @Override
    public void create(Film film) {
        Document document = new Document("titre", film.getTitre())
                .append("annee", film.getAnnee())
                .append("acteurs_ids", List.of());
        film.setIdF(MongoIds.inserer(collection, document, film.getIdF()));
    }

    @Override
    public Film read(int id) {
        Document document = collection.find(eq("_id", id)).first();
        return document == null ? null : lireFilm(document);
    }

    @Override
    public void update(Film film) {
        Document valeurs = new Document("titre", film.getTitre()).append("annee", film.getAnnee());
        collection.updateOne(eq("_id", film.getIdF()), new Document("$set", valeurs));
    }

    @Override
    public void delete(int id) {
        collection.deleteOne(eq("_id", id));
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        for (Document document : collection.find()) {
            films.add(lireFilm(document));
        }
        return films;
    }

    @Override
    public void addActeurToFilm(int idFilm, int idActeur) {
        var acteurs = database.getCollection("Acteurs");
        if (acteurs.find(eq("_id", idActeur)).first() == null) {
            throw new IllegalArgumentException("Acteur inexistant : " + idActeur);
        }
        var resultat = collection.updateOne(eq("_id", idFilm), addToSet("acteurs_ids", idActeur));
        if (resultat.getMatchedCount() == 0) {
            throw new IllegalArgumentException("Film inexistant : " + idFilm);
        }
        if (acteurs.find(eq("_id", idActeur)).first() == null) {
            removeActeurFromFilm(idFilm, idActeur);
            throw new IllegalStateException("Acteur supprimé pendant l'association : " + idActeur);
        }
    }

    @Override
    public void removeActeurFromFilm(int idFilm, int idActeur) {
        collection.updateOne(eq("_id", idFilm), pull("acteurs_ids", idActeur));
    }

    private Film lireFilm(Document document) {
        Film film = new Film(document.getInteger("_id"), document.getString("titre"),
                document.getInteger("annee", 0));
        for (int id : document.getList("acteurs_ids", Integer.class, List.of())) {
            var acteur = daoActeur.read(id);
            if (acteur != null) {
                film.addActeur(acteur);
            }
        }
        return film;
    }
}
