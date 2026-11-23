package dao.mysql;

import dao.DaoActeur;
import model.Acteur;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DaoActeurMySQL implements DaoActeur {

    private final Connection connexion = MySQLConnection.getInstance();

    @Override
    public void create(Acteur obj) {
        String req = "INSERT INTO Acteur (nom, prenom, dnaiss) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connexion.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, obj.getNom());
            pstmt.setString(2, obj.getPrenom());
            pstmt.setDate(3, obj.getDnaiss() != null ? Date.valueOf(obj.getDnaiss()) : null);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                obj.setIdA(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
    }

    @Override
    public Acteur read(int id) {
        String req = "SELECT * FROM Acteur WHERE idA = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(req)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Acteur(
                        rs.getInt("idA"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("dnaiss") != null ? rs.getDate("dnaiss").toLocalDate() : null);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
        return null;
    }

    @Override
    public void update(Acteur obj) {
        String req = "UPDATE Acteur SET nom = ?, prenom = ?, dnaiss = ? WHERE idA = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(req)) {
            pstmt.setString(1, obj.getNom());
            pstmt.setString(2, obj.getPrenom());
            pstmt.setDate(3, obj.getDnaiss() != null ? Date.valueOf(obj.getDnaiss()) : null);
            pstmt.setInt(4, obj.getIdA());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
    }

    @Override
    public void delete(int id) {
        String req = "DELETE FROM Acteur WHERE idA = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
    }

    @Override
    public List<Acteur> findAll() {
        List<Acteur> list = new ArrayList<>();
        String req = "SELECT * FROM Acteur";
        try (Statement stmt = connexion.createStatement();
                ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) {
                list.add(new Acteur(
                        rs.getInt("idA"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("dnaiss") != null ? rs.getDate("dnaiss").toLocalDate() : null));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Opération JDBC impossible.", e);
        }
        return list;
    }
}
