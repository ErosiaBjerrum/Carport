package app.persistence;

import app.config.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static boolean userExists(int userID) throws SQLException {

        String sql = "SELECT 1 FROM user_account WHERE user_id = ?";

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

 // CREATE USER METHOD

    public static int createUser (String name, String password, String email, String phone, String address, int zipCode, String role) throws SQLException {

        String sql = """
            INSERT INTO user_account (name, password, email, phone, address, zip_code, role)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING user_id
            """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setInt(6, zipCode);
            ps.setString(7, role);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            } else {
                throw new SQLException("Bruger blev ikke oprettet.");
            }
        }
    }

    public static Integer login(String email, String password) throws SQLException {

        String sql = """
            SELECT user_id
            FROM user_account
            WHERE email = ? AND password = ?
            """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                } else {
                    return null;
                }
            }
        }
    }

    public static boolean emailExists(String email) throws SQLException {

        String sql = "SELECT 1 FROM user_account WHERE email = ?";

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

}