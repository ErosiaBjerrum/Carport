package app.entities;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Order {

    private int orderId;
    private int offerId;
    private int length;
    private int width;
    private int price;
    private String customerName;
    private Timestamp orderDate;

    public Order(int orderId, int offerId, int length, int width, int price, String customerName, Timestamp orderDate) {
        this.orderId = orderId;
        this.offerId = offerId;
        this.length = length;
        this.width = width;
        this.price = price;
        this.customerName = customerName;
        this.orderDate = orderDate;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getOfferId() {
        return offerId;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getPrice() {
        return price;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDateTime getOrderDateFormatted() {
        return orderDate.toLocalDateTime();
    }
}

