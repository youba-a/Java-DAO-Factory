package dao.mongo;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.*;

final class MongoIds {
    private MongoIds() {
    }

    static int inserer(MongoCollection<Document> collection, Document document, int idDemande) {
        if (idDemande != 0) {
            collection.insertOne(document.append("_id", idDemande));
            return idDemande;
        }

        var compteurs = MongoConnection.getInstance().getCollection("Compteurs");
        String nom = collection.getNamespace().getCollectionName();
        var compteur = eq("_id", nom);

        while (true) {
            Document dernier = collection.find().sort(descending("_id")).projection(include("_id")).first();
            int minimum = dernier == null ? 0 : Math.max(0, dernier.getInteger("_id"));
            try {
                compteurs.updateOne(compteur, max("seq", minimum), new UpdateOptions().upsert(true));
            } catch (MongoWriteException e) {
                // Une autre instance peut créer le compteur au même moment.
                if (e.getError().getCategory() != ErrorCategory.DUPLICATE_KEY) {
                    throw e;
                }
                continue;
            }

            Document reserve = compteurs.findOneAndUpdate(
                    and(compteur, lt("seq", Integer.MAX_VALUE)), inc("seq", 1),
                    new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
            if (reserve == null) {
                throw new IllegalStateException("Identifiants MongoDB épuisés pour " + nom);
            }
            int id = reserve.getInteger("seq");
            try {
                collection.insertOne(document.append("_id", id));
                return id;
            } catch (MongoWriteException e) {
                // Un import peut avoir utilisé cet identifiant après sa réservation.
                if (e.getError().getCategory() != ErrorCategory.DUPLICATE_KEY
                        || collection.find(eq("_id", id)).first() == null) {
                    throw e;
                }
            }
        }
    }
}
