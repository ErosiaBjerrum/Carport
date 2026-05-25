package app.services;

import app.entities.BillOfMaterial;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    void findBestLength() {
        Calculator calculator = new Calculator(600, 420, null);

        int result = calculator.findBestLength(421);

        assertEquals(480, result);
    }

    @Test
    void calcRafterCount() {
        Calculator calculator = new Calculator(600, 420, null);

        int result = calculator.calcRafterCount();

        assertEquals(11, result);
    }

    @Test
    void addRaftersToBillOfMaterial() throws SQLException {
        Calculator calculator = new Calculator(600, 420, null);
        BillOfMaterial billOfMaterial = new BillOfMaterial();

        calculator.addRaftersToBillOfMaterial(billOfMaterial);

        assertEquals(1, billOfMaterial.getBomLines().size());
        assertEquals("Spærtræ 45x195 mm", billOfMaterial.getBomLines().get(0).getName());
        assertEquals(420, billOfMaterial.getBomLines().get(0).getLength());
        assertEquals(11, billOfMaterial.getBomLines().get(0).getQuantity());
    }


}