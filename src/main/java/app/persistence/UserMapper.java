package app.persistence;

import app.config.ConnectionPool;
import app.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public static User login(String email, String password) throws SQLException {

        String sql = """
            SELECT user_id, role
            FROM user_account
            WHERE email = ? AND password = ?
            """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("role"),
                            null,
                            null,
                            null,
                            null,
                            0,
                            null
                    );
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

    public static List<User> getAllCustomers() throws SQLException {

        String sql = """
            SELECT 
                ua.user_id,
                ua.role,
                ua.name,
                ua.email,
                ua.phone,
                ua.address,
                ua.zip_code,
                zc.city
            FROM user_account ua
            JOIN zip_code zc
                ON ua.zip_code = zc.zip_code
            WHERE role = 'customer'
            ORDER BY user_id ASC
            """;

        List<User> customers = new ArrayList<>();

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                customers.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("role"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getInt("zip_code"),
                        rs.getString("city")
                ));
            }
        }

        return customers;
    }

}