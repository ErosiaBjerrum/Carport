package app.entities;

public class Offer {
    private int offerId;
    private int length;
    private int width;
    private int price;

    public Offer(int offerId, int length, int width, int price) {
        this.offerId = offerId;
        this.length = length;
        this.width = width;
        this.price = price;
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