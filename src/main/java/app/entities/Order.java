package app.entities;

public class Order {

    private int orderId;
    private int offerId;
    private int length;
    private int width;
    private int price;

    public Order(int orderId, int offerId, int length, int width, int price) {
        this.orderId = orderId;
        this.offerId = offerId;
        this.length = length;
        this.width = width;
        this.price = price;
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
}