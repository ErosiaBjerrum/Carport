package app.entities;

public class MaterialItem {

    private int materialId;
    private String name;
    private int length;
    private double unitPrice;

    public MaterialItem(int materialId, String name, int length, double unitPrice) {
        this.materialId = materialId;
        this.name = name;
        this.length = length;
        this.unitPrice = unitPrice;
    }

    public int getMaterialId() {
        return materialId;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}