package app.services;

import app.entities.BOMLine;
import app.entities.BillOfMaterial;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {




    @Test
    void findBestLength() {
        Calculator calculator = new Calculator(600, 420, null);

        assertEquals(360, calculator.findBestLength(345));
        assertEquals(480, calculator.findBestLength(421));
        assertEquals(600, calculator.findBestLength(578));
        assertEquals(-1, calculator.findBestLength(780));
    }

    @Test
    void calcRafterCount() {
        Calculator calculator = new Calculator(600, 420, null);

        assertEquals(11, calculator.calcRafterCount());
    }

    @Test
    void calcRafterLength() {
        Calculator calculator = new Calculator(600, 421, null);

        assertEquals(480, calculator.calcRafterLength());
    }

    @Test
    void printRafterInfo() {
        Calculator calculator = new Calculator(600, 420, null);

        calculator.printRafterInfo();
    }

    @Test
    void calcBeamLengths() {
        Calculator calculator1 = new Calculator(400, 300, null);
        assertArrayEquals(new int[]{420}, calculator1.calcBeamLengths());

        Calculator calculator2 = new Calculator(720, 300, null);
        assertArrayEquals(new int[]{720}, calculator2.calcBeamLengths());

        Calculator calculator3 = new Calculator(745, 300, null);
        assertArrayEquals(new int[]{300, 480}, calculator3.calcBeamLengths());

        Calculator calculator4 = new Calculator(780, 300, null);
        assertArrayEquals(new int[]{300, 480}, calculator4.calcBeamLengths());
    }

    @Test
    void calcCarportReturnsBomWithRafters() throws SQLException {
        Calculator calculator = new Calculator(600, 420, null);

        BillOfMaterial billOfMaterial = calculator.calcCarport();

        assertEquals(1, billOfMaterial.getBomLines().size());

        BOMLine rafterLine = billOfMaterial.getBomLines().get(0);

        assertEquals("Spærtræ 45x195 mm", rafterLine.getMaterialItem().getName());
        assertEquals(420, rafterLine.getMaterialItem().getLength());
        assertEquals(11, rafterLine.getQuantity());
        assertTrue(rafterLine.getLinePrice() > 0);
    }



}