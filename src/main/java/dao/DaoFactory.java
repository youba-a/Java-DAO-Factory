package dao;


public abstract class DaoFactory {

    public enum TypeBase {
        MYSQL,
        MONGODB
    }

    public abstract DaoActeur getDaoActeur();

    public abstract DaoFilm getDaoFilm();

    // Factory method
    public static DaoFactory getDaoFactory(TypeBase type) {
        switch (type) {
            case MYSQL:
                return new dao.mysql.MySQLFactory();
            case MONGODB:
                return new dao.mongo.MongoFactory();
            default:
                throw new IllegalArgumentException("Type inconnu");
        }
    }
}
