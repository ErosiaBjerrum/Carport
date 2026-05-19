package app.entities;

import app.entities.MaterialItem;

public class BOMLine {

    private MaterialItem materialItem;
    private int quantity;

    public BOMLine(MaterialItem materialItem, int quantity) {
        this.materialItem = materialItem;
        this.quantity = quantity;
    }

    public String getName() {
        return materialItem.getName();
    }

    public int getLength() {
        return materialItem.getLength();
    }

    public int getQuantity() {
        return quantity;
    }

    public MaterialItem getMaterialItem() {
        return materialItem;
    }

    public double getUnitPrice() {
        return materialItem.getUnitPrice();
    }

    public double getLinePrice() {
        return getUnitPrice() * quantity;
    }
}