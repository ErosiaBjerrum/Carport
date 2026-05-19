package app.persistence;

import app.config.ConnectionPool;
import app.entities.MaterialItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MaterialMapper {

    public static MaterialItem getMaterialItem(String materialName, int lengthCm) throws SQLException {

        String sql = """
            SELECT 
                mi.material_item_id,
                mt.name,
                lsv.length_cm,
                lsv.price
            FROM material_item mi
            JOIN material_type mt 
                ON mi.material_type_id = mt.material_type_id
            JOIN length_set_value lsv 
                ON mi.length_set_value_id = lsv.length_set_value_id
            WHERE mt.name = ?
            AND lsv.length_cm = ?
            """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, materialName);
            ps.setInt(2, lengthCm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MaterialItem(
                            rs.getInt("material_item_id"),
                            rs.getString("name"),
                            rs.getInt("length_cm"),
                            rs.getDouble("price")
                    );
                }
            }
        }

        return null;
    }
}