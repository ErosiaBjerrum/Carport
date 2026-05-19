package app.persistence;

import app.config.ConnectionPool;
import app.entities.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static void createOrder(int offerId, int userId) throws SQLException {

        String sql = """
                INSERT INTO customer_order (offer_id, user_id, order_date)
                VALUES (?, ?, NOW())
                """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, offerId);
            ps.setInt(2, userId);

            ps.executeUpdate();
        }
    }

    public static List<Order> getOrdersByUserId(int userId) throws SQLException {

        String sql = """
            SELECT 
                co.order_id,
                o.offer_id,
                cr.length,
                cr.width,
                o.total_price
            FROM customer_order co
            JOIN offer o 
                ON co.offer_id = o.offer_id
            JOIN carport_request cr 
                ON o.request_id = cr.request_id
            WHERE co.user_id = ?
            ORDER BY co.order_id DESC
            """;

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            List<Order> orders = new ArrayList<>();

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getInt("offer_id"),
                        rs.getInt("length"),
                        rs.getInt("width"),
                        rs.getInt("total_price")
                ));
            }

            return orders;
        }
    }

}