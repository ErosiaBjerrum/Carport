package app.persistence;

import app.config.ConnectionPool;
import app.entities.Offer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

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
                o.total_price,
                o.offer_date
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
                            rs.getInt("total_price"),
                            null,
                            rs.getTimestamp("offer_date")
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

    public static List<Offer> getAllOffers() throws SQLException {

        String sql = """
        
        SELECT
            o.offer_id,
            cr.length,
            cr.width,
            o.total_price,
            ua.name,
            o.offer_date
        FROM offer o
        JOIN carport_request cr\s
            ON o.request_id = cr.request_id
        JOIN user_account ua
            ON cr.user_id = ua.user_id
        LEFT JOIN customer_order co
            ON o.offer_id = co.offer_id
        WHERE co.order_id IS NULL
        ORDER BY o.offer_id ASC
        """;

        List<Offer> offers = new ArrayList<>();

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                offers.add(new Offer(
                        rs.getInt("offer_id"),
                        rs.getInt("length"),
                        rs.getInt("width"),
                        rs.getInt("total_price"),
                        rs.getString("name"),
                        rs.getTimestamp("offer_date")
                ));
            }
        }

        return offers;
    }

    public static boolean offerBelongsToUser(int offerId, int userId) throws SQLException {

        String sql = """
        SELECT 1
        FROM offer o
        JOIN carport_request cr
            ON o.request_id = cr.request_id
        WHERE o.offer_id = ?
        AND cr.user_id = ?
        """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, offerId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            return rs.next();
        }
    }

    public static Offer getOfferById(int offerId) throws SQLException {

        String sql = """
        SELECT
            o.offer_id,
            cr.length,
            cr.width,
            o.total_price,
            ua.name,
            o.offer_date
        FROM offer o
        JOIN carport_request cr
            ON o.request_id = cr.request_id
        JOIN user_account ua
            ON cr.user_id = ua.user_id
        WHERE o.offer_id = ?
        """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, offerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Offer(
                        rs.getInt("offer_id"),
                        rs.getInt("length"),
                        rs.getInt("width"),
                        rs.getInt("total_price"),
                        rs.getString("name"),
                        rs.getTimestamp("offer_date")
                );
            }

            return null;
        }
    }

}