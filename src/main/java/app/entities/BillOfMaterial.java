package app.entities;

import java.util.ArrayList;
import java.util.List;

public class BillOfMaterial {

    private List<BOMLine> bomLines = new ArrayList<>();

    public void addLine(BOMLine bomLine) {
        bomLines.add(bomLine);
    }

    public List<BOMLine> getBomLines() {
        return bomLines;
    }

    public double getTotalPrice() {
        double total = 0;

        for (BOMLine bomLine : bomLines) {
            total += bomLine.getLinePrice();
        }

        return total;
    }
}