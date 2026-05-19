package app.persistence;

import app.config.ConnectionPool;
import app.entities.Offer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfferMapper {

    public static int createOffer(int requestId, int price) throws SQLException {

        String sql = """
            INSERT INTO offer (request_id, total_price)
            VALUES (?, ?)
            RETURNING offer_id
            """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.setInt(2, price);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("offer_id");
                } else {
                    throw new SQLException("Tilbud blev ikke oprettet.");
                }
            }
        }
    }

    public static List<Offer> getOffersByUserId(int userId) throws SQLException {

        String sql = """
            SELECT 
                o.offer_id,
                cr.length,
                cr.width,
                o.total_price
            FROM offer o
            JOIN carport_request cr 
                ON o.request_id = cr.request_id
            LEFT JOIN customer_order co
                ON o.offer_id = co.offer_id
            WHERE cr.user_id = ?
            AND co.order_id IS NULL
            ORDER BY o.offer_id DESC
            """;

        List<Offer> offers = new ArrayList<>();

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    offers.add(new Offer(
                            rs.getInt("offer_id"),
                            rs.getInt("length"),
                            rs.getInt("width"),
                            rs.getInt("total_price")
                    ));
                }
            }
        }

        return offers;
    }

    public static void deleteOfferById(int offerId) throws SQLException {

        String sql = """
        DELETE FROM offer
        WHERE offer_id = ?
        """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, offerId);
            ps.executeUpdate();
        }
    }
}