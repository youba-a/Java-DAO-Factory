// Initialisation des collections sans modifier les documents existants.
const filmographie = db.getSiblingDB(process.env.FILM_MONGO_DATABASE || "Filmographie");
for (const nom of ["Acteurs", "Films", "Compteurs"]) {
    if (!filmographie.getCollectionNames().includes(nom)) {
        filmographie.createCollection(nom);
    }
}
