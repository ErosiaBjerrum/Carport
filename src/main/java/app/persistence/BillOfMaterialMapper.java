package app.persistence;

import app.config.ConnectionPool;
import app.entities.BOMLine;
import app.entities.BillOfMaterial;
import app.entities.MaterialItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillOfMaterialMapper {

    public static int createBillOfMaterial(int offerId) throws SQLException {

        String sql = """
                INSERT INTO bill_of_material (offer_id)
                VALUES (?)
                RETURNING bom_id
                """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, offerId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("bom_id");
                } else {
                    throw new SQLException("Stykliste blev ikke oprettet.");
                }
            }
        }
    }

    public static void createBOMLine(int bomId, BOMLine bomLine) throws SQLException {

        String sql = """
            INSERT INTO bom_line (bom_id, quantity, material_id, usage)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, bomId);
            ps.setInt(2, bomLine.getQuantity());
            ps.setInt(3, bomLine.getMaterialItem().getMaterialId());
            ps.setString(4, "Konstruktion");

            ps.executeUpdate();
        }
    }

    public static void saveBillOfMaterial(int offerId, BillOfMaterial billOfMaterial) throws SQLException {

        int bomId = createBillOfMaterial(offerId);

        for (BOMLine bomLine : billOfMaterial.getBomLines()) {
            createBOMLine(bomId, bomLine);
        }
    }

    public static List<BOMLine> getBOMLinesByOfferId(int offerId) throws SQLException {

        String sql = """
            SELECT 
                mi.material_item_id,
                mt.name,
                lsv.length_cm,
                lsv.price,
                bl.quantity
            FROM bill_of_material bom
            JOIN bom_line bl 
                ON bom.bom_id = bl.bom_id
            JOIN material_item mi 
                ON bl.material_id = mi.material_item_id
            JOIN material_type mt 
                ON mi.material_type_id = mt.material_type_id
            JOIN length_set_value lsv 
                ON mi.length_set_value_id = lsv.length_set_value_id
            WHERE bom.offer_id = ?
            ORDER BY bl.bom_line_id
            """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, offerId);

            ResultSet rs = ps.executeQuery();

            List<BOMLine> bomLines = new ArrayList<>();

            while (rs.next()) {
                MaterialItem materialItem = new MaterialItem(
                        rs.getInt("material_item_id"),
                        rs.getString("name"),
                        rs.getInt("length_cm"),
                        rs.getDouble("price")
                );

                bomLines.add(new BOMLine(
                        materialItem,
                        rs.getInt("quantity")
                ));
            }

            return bomLines;
        }
    }

}