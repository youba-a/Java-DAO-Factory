package dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dao.DaoActeur;
import model.Acteur;
import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.pull;

public class DaoActeurMongo implements DaoActeur {
    private final MongoDatabase database = MongoConnection.getInstance();
    private final MongoCollection<Document> collection = database.getCollection("Acteurs");

    @Override
    public void create(Acteur acteur) {
        acteur.setIdA(MongoIds.inserer(collection, valeurs(acteur), acteur.getIdA()));
    }

    @Override
    public Acteur read(int id) {
        Document document = collection.find(eq("_id", id)).first();
        return document == null ? null : lireActeur(document);
    }

    @Override
    public void update(Acteur acteur) {
        collection.updateOne(eq("_id", acteur.getIdA()), new Document("$set", valeurs(acteur)));
    }

    @Override
    public void delete(int id) {
        collection.deleteOne(eq("_id", id));
        database.getCollection("Films").updateMany(eq("acteurs_ids", id), pull("acteurs_ids", id));
    }

    @Override
    public List<Acteur> findAll() {
        List<Acteur> acteurs = new ArrayList<>();
        for (Document document : collection.find()) {
            acteurs.add(lireActeur(document));
        }
        return acteurs;
    }

    private Document valeurs(Acteur acteur) {
        LocalDate naissance = acteur.getDnaiss();
        return new Document("nom", acteur.getNom())
                .append("prenom", acteur.getPrenom())
                .append("dnaiss", naissance == null ? null : Date.from(naissance.atStartOfDay().toInstant(ZoneOffset.UTC)));
    }

    private Acteur lireActeur(Document document) {
        Object naissance = document.get("dnaiss");
        LocalDate date = null;
        if (naissance instanceof String texte) {
            date = LocalDate.parse(texte);
        } else if (naissance instanceof Date ancienneDate) {
            var utc = ancienneDate.toInstant().atOffset(ZoneOffset.UTC);
            // Les anciennes dates de démonstration sont à minuit UTC.
            date = utc.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)
                    ? utc.toLocalDate()
                    : ancienneDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return new Acteur(document.getInteger("_id"), document.getString("nom"),
                document.getString("prenom"), date);
    }
}
