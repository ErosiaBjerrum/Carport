package app.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Offer {
    private int offerId;
    private int length;
    private int width;
    private int price;
    private String customerName;
    private Timestamp offerDate;

    public Offer(int offerId, int length, int width, int price, String customerName, Timestamp offerDate) {
        this.offerId = offerId;
        this.length = length;
        this.width = width;
        this.price = price;
        this.customerName = customerName;
        this.offerDate = offerDate;
    }

    public Timestamp getOfferDate() {
        return offerDate;
    }

    public LocalDateTime getOfferDateFormatted() {
        return offerDate.toLocalDateTime();
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

}