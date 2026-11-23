package dao;

import model.Film;

public interface DaoFilm extends IDao<Film> {
    void addActeurToFilm(int idFilm, int idActeur);

    void removeActeurFromFilm(int idFilm, int idActeur);
}
