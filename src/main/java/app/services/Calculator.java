package app.services;

import app.config.ConnectionPool;
import app.entities.BOMLine;
import app.entities.BillOfMaterial;
import app.entities.MaterialItem;
import app.persistence.MaterialMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Calculator {
    int[] availableLengths = {300, 360, 420, 480, 540, 600, 660, 720};
    private List<MaterialItem> materialItems = new ArrayList<>();
    private int length;
    private int width;
    private ConnectionPool connectionPool;

    public Calculator(int length, int width, ConnectionPool connectionPool) {
        this.length = length;
        this.width = width;
        this.connectionPool = connectionPool;
    }

    public BillOfMaterial calcCarport() throws SQLException {
        BillOfMaterial billOfMaterial = new BillOfMaterial();

        addRaftersToBillOfMaterial(billOfMaterial);
        addBeamsToBillOfMaterial(billOfMaterial);
        addPostsToBillOfMaterial(billOfMaterial);

        return billOfMaterial;
    }

    public double calcPostSpacing() {
        int maxDistanceBetweenPosts = 310;

        int spaces = (int) Math.ceil(length / (double) maxDistanceBetweenPosts);

        return length / (double) spaces;
    }

    // Stolper
    public int calcPostCount() {
        int maxDistanceBetweenPosts = 310;

        int spaces = (int) Math.ceil(length / (double) maxDistanceBetweenPosts);
        int postsPerSide = spaces + 1;

        return postsPerSide * 2;
    }

    // Spær
    // Find længde
    public int findBestLength(int requiredLength) {

        for (int availableLength : availableLengths) {
            if (availableLength >= requiredLength) {
                return availableLength;
            }
        }
        return -1;
    }

    //Find antal spær
    public int calcRafterCount() {
        int maxDistanceBetweenRafters = 60;

        int spaces = (int) Math.ceil(length / (double) maxDistanceBetweenRafters);
        int totalRafters = spaces + 1;

        return totalRafters;
    }

    public int calcRafterLength() {
        return findBestLength(width);
    }

    public void printRafterInfo() {
        int rafterCount = calcRafterCount();
        int rafterLength = calcRafterLength();

        System.out.println("Spær: " + rafterCount + " stk. á " + rafterLength + " cm");
    }

    // Remme
    public int[] calcBeamLengths() {

        if (length <= 720) {
            return new int[] { findBestLength(length) };
        }

        int bestFirst = 0;
        int bestSecond = 0;
        int leastWaste = Integer.MAX_VALUE;
        int bestBalance = Integer.MAX_VALUE;

        for (int first : availableLengths) {
            for (int second : availableLengths) {

                int totalLength = first + second;

                if (totalLength >= length) {
                    int waste = totalLength - length;
                    int balance = Math.abs(first - second);

                    if (waste < leastWaste || waste == leastWaste && balance < bestBalance) {
                        leastWaste = waste;
                        bestBalance = balance;
                        bestFirst = first;
                        bestSecond = second;
                    }
                }
            }
        }

        return new int[] { bestFirst, bestSecond };
    }

        // >>> FØJ MATERIALER TIL STYKLISTE <<<

    public void addRaftersToBillOfMaterial(BillOfMaterial billOfMaterial) throws SQLException {
        int rafterCount = calcRafterCount();
        int rafterLength = calcRafterLength();

        MaterialItem rafter = MaterialMapper.getMaterialItem("Spærtræ 45x195 mm", rafterLength);

        if (rafter == null) {
            throw new RuntimeException("Materiale ikke fundet: Spærtræ 45x195 mm, " + rafterLength + " cm");
        }

        billOfMaterial.addLine(new BOMLine(rafter, rafterCount));
    }

    public void addBeamsToBillOfMaterial(BillOfMaterial billOfMaterial) throws SQLException {
        int[] beamLengths = calcBeamLengths();

        for (int beamLength : beamLengths) {
            MaterialItem beam = MaterialMapper.getMaterialItem("Rem 45x195 mm", beamLength);

            if (beam == null) {
                throw new RuntimeException("Materiale ikke fundet: Rem 45x195 mm, " + beamLength + " cm");
            }

            billOfMaterial.addLine(new BOMLine(beam, 2));
        }
    }

    public void addPostsToBillOfMaterial(BillOfMaterial billOfMaterial) throws SQLException {
        int postCount = calcPostCount();
        int postLength = 300;

        MaterialItem post = MaterialMapper.getMaterialItem("Stolpe 97x97 mm", postLength);

        if (post == null) {
            throw new RuntimeException("Materiale ikke fundet: Stolpe 97x97 mm, " + postLength + " cm");
        }

        billOfMaterial.addLine(new BOMLine(post, postCount));
    }

}