package dao.mongo;

import dao.DaoFactory;
import dao.DaoActeur;
import dao.DaoFilm;

public class MongoFactory extends DaoFactory {

    @Override
    public DaoActeur getDaoActeur() {
        return new DaoActeurMongo();
    }

    @Override
    public DaoFilm getDaoFilm() {
        return new DaoFilmMongo();
    }
}
