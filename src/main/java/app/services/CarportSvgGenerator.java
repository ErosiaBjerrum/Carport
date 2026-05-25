package app.services;

public class CarportSvgGenerator {

    public String generateSvg(int length, int width) {

        double scale = Math.min(600.0 / length, 300.0 / width);

        int carportLength = (int) (length * scale);
        int carportWidth = (int) (width * scale);

        int svgWidth = carportLength + 220;
        int svgHeight = carportWidth + 180;

        int x = (svgWidth - carportLength) / 2;
        int y = 80;

        StringBuilder rafters = new StringBuilder();

        int maxRafterSpacing = 60;
        int rafterSpaces = (int) Math.ceil(length / (double) maxRafterSpacing);
        double rafterSpacingPixels = carportLength / (double) rafterSpaces;

        for (int i = 0; i <= rafterSpaces; i++) {
            int rafterX = x + (int) Math.round(i * rafterSpacingPixels);

            rafters.append("""
        <line x1="%d" y1="%d"
              x2="%d" y2="%d"
              stroke="black"
              stroke-width="2"/>
        """.formatted(
                    rafterX,
                    y,
                    rafterX,
                    y + carportWidth
            ));
        }

        StringBuilder posts = new StringBuilder();

        int maxPostSpacing = 310;
        int spaces = (int) Math.ceil(length / (double) maxPostSpacing);
        double spacingPixels = carportLength / (double) spaces;

        for (int i = 0; i <= spaces; i++) {
            int postX = x + (int) Math.round(i * spacingPixels);

            posts.append("""
            <rect x="%d" y="%d" width="12" height="12" fill="black"/>
            <rect x="%d" y="%d" width="12" height="12" fill="black"/>
            """.formatted(postX - 6, y - 6, postX - 6, y + carportWidth - 6));
        }

        return """
        <svg width="%d" height="%d" xmlns="http://www.w3.org/2000/svg">

            <!-- Øverste rem -->
            <line x1="%d" y1="%d" x2="%d" y2="%d"
                  stroke="black" stroke-width="6"/>

            <!-- Nederste rem -->
            <line x1="%d" y1="%d" x2="%d" y2="%d"
                  stroke="black" stroke-width="6"/>

            <!-- Spær -->
            %s

            <!-- Stolper -->
            %s

            <!-- Målstreg længde -->
            <line x1="%d" y1="%d" x2="%d" y2="%d"
                  stroke="black" stroke-width="1"/>
            <text x="%d" y="%d" font-size="16" text-anchor="middle">
                %d cm
            </text>

            <!-- Målstreg bredde -->
            <line x1="%d" y1="%d" x2="%d" y2="%d"
                  stroke="black" stroke-width="1"/>
            <text x="%d" y="%d" font-size="16">
                %d cm
            </text>

        </svg>
        """.formatted(
                svgWidth, svgHeight,

                x, y,
                x + carportLength, y,

                x, y + carportWidth,
                x + carportLength, y + carportWidth,

                rafters.toString(),
                posts.toString(),

                x, y + carportWidth + 35,
                x + carportLength, y + carportWidth + 35,
                x + carportLength / 2, y + carportWidth + 58,
                length,

                x + carportLength + 35, y,
                x + carportLength + 35, y + carportWidth,
                x + carportLength + 45, y + carportWidth / 2,
                width
        );
    }

}
