package dao.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static Connection connexion;

    private MySQLConnection() {
    }

    public static synchronized Connection getInstance() {
        String utilisateur = System.getenv("FILM_MYSQL_USER");
        String motDePasse = System.getenv("FILM_MYSQL_PASSWORD");
        if (utilisateur == null || utilisateur.isBlank() || motDePasse == null) {
            throw new IllegalStateException("Définissez FILM_MYSQL_USER et FILM_MYSQL_PASSWORD.");
        }

        try {
            if (connexion == null || connexion.isClosed()) {
                String url = System.getenv().getOrDefault("FILM_MYSQL_URL",
                        "jdbc:mysql://127.0.0.1:3306/Filmographie");
                connexion = DriverManager.getConnection(url, utilisateur, motDePasse);
            }
            return connexion;
        } catch (SQLException e) {
            throw new IllegalStateException("Connexion MySQL impossible.", e);
        }
    }
}
