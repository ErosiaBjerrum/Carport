package app.persistence;

import app.config.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarportRequestMapper {

    public static int createRequest(int userId, String length, String width) throws SQLException {

        String sql = """
        INSERT INTO carport_request (user_id, length, width)
        VALUES (?, ?, ?)
        RETURNING request_id
        """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, Integer.parseInt(length));
            ps.setInt(3, Integer.parseInt(width));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("request_id");
            } else {
                throw new SQLException("Carport request blev ikke oprettet.");
            }
        }
    }
}