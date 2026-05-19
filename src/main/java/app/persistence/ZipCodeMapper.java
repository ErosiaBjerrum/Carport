package app.persistence;

import app.config.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ZipCodeMapper {

    public static boolean zipCodeExists(int zipCode) throws SQLException {

        String sql = "SELECT 1 FROM zip_code WHERE zip_code = ?";

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, zipCode);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}