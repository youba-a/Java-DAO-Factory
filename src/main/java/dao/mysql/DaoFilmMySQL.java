package dao.mysql;

import dao.DaoFilm;
import model.Film;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Acteur;

public class DaoFilmMySQL implements DaoFilm {

    private final Connection connexion = MySQLConnection.getInstance();

    @Override
    public void create(Film obj) {
        String req = "INSERT INTO Film (titre, annee) VALUES (?, ?)";
        try (PreparedStatement pstmt = connexion.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, obj.getTitre());
            pstmt.setInt(2, obj.getAnnee());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                obj.setIdF(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
    }

    @Override
    public Film read(int id) {
        Film film = null;
        String req = "SELECT f.idF, f.titre, f.annee, a.idA, a.nom, a.prenom, a.dnaiss " +
                "FROM Film f " +
                "LEFT JOIN Joue j ON f.idF = j.idF " +
                "LEFT JOIN Acteur a ON j.idA = a.idA " +
                "WHERE f.idF = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(req)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (film == null) {
                    film = new Film(rs.getInt("idF"), rs.getString("titre"), rs.getInt("annee"));
                }
                int idA = rs.getInt("idA");
                if (!rs.wasNull()) {
                    Acteur acteur = new Acteur(idA, rs.getString("nom"), rs.getString("prenom"),
                            rs.getDate("dnaiss") != null ? rs.getDate("dnaiss").toLocalDate() : null);
                    film.addActeur(acteur);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
        return film;
    }

    @Override
    public void update(Film obj) {
        String req = "UPDATE Film SET titre = ?, annee = ? WHERE idF = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(req)) {
            pstmt.setString(1, obj.getTitre());
            pstmt.setInt(2, obj.getAnnee());
            pstmt.setInt(3, obj.getIdF());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
    }

    @Override
    public void delete(int id) {
        String req = "DELETE FROM Film WHERE idF = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
    }

    @Override
    public List<Film> findAll() {
        Map<Integer, Film> filmMap = new LinkedHashMap<>();
        String req = "SELECT f.idF, f.titre, f.annee,\r\n" + //
                "       a.idA, a.nom, a.prenom, a.dnaiss\r\n" + //
                "FROM Film f\r\n" + //
                "LEFT JOIN Joue j ON f.idF = j.idF\r\n" + //
                "LEFT JOIN Acteur a ON j.idA = a.idA\r\n" + //
                "ORDER BY f.idF;";
        try (Statement stmt = connexion.createStatement();
                ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) {
                int idF = rs.getInt("idF");
                Film film = filmMap.get(idF);
                if (film == null) {
                    film = new Film(idF, rs.getString("titre"), rs.getInt("annee"));
                    filmMap.put(idF, film);
                }

                int idA = rs.getInt("idA");
                if (!rs.wasNull()) {
                    Acteur acteur = new Acteur(
                            idA,
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getDate("dnaiss") != null ? rs.getDate("dnaiss").toLocalDate() : null);
                    film.addActeur(acteur);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public void addActeurToFilm(int idFilm, int idActeur) {
        String req = "INSERT INTO Joue (idF, idA) VALUES (?, ?) ON DUPLICATE KEY UPDATE idF = Joue.idF";
        try (PreparedStatement pstmt = connexion.prepareStatement(req)) {
            pstmt.setInt(1, idFilm);
            pstmt.setInt(2, idActeur);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
    }

    @Override
    public void removeActeurFromFilm(int idFilm, int idActeur) {
        String req = "DELETE FROM Joue WHERE idF = ? AND idA = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(req)) {
            pstmt.setInt(1, idFilm);
            pstmt.setInt(2, idActeur);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
    }
}
