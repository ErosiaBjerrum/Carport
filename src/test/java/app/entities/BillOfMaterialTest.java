package app.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BillOfMaterialTest {

    @Test
    void billOfMaterialCalculatesTotalPrice() {
        MaterialItem rem = new MaterialItem(1, "Rem", 600, 120.0);
        MaterialItem spaer = new MaterialItem(2, "Spær", 420, 80.0);

        BOMLine line1 = new BOMLine(rem, 2);
        BOMLine line2 = new BOMLine(spaer, 11);

        BillOfMaterial bom = new BillOfMaterial();
        bom.addLine(line1);
        bom.addLine(line2);

        assertEquals(1120.0, bom.getTotalPrice());
    }
}