package dao.mysql;

import dao.DaoFactory;
import dao.DaoActeur;
import dao.DaoFilm;

public class MySQLFactory extends DaoFactory {

    @Override
    public DaoActeur getDaoActeur() {
        return new DaoActeurMySQL();
    }

    @Override
    public DaoFilm getDaoFilm() {
        return new DaoFilmMySQL();
    }
}
