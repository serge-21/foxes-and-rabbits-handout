/**
 * This is the Seasons enum it holds four main seasons each with
 * possible lowest visibility and a downfall, in addition to the highest
 * visibility and a downfall that is used to generate an actual value for both.
 * This is used to give more realism in the simulation so that the values aren't
 * fixed per season.
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public enum Seasons {
    WINTER(0, 100, 300, 600),
    SPRING(200, 400, 50, 160),
    SUMMER(300, 500, 0, 70),
    AUTUMN(100, 300, 100, 200);

    // visibility is directly linked to the sunlight levels
    final int lowestVisibility;
    final int highestVisibility;

    // downfall is directly linked to rain
    final int lowestDownfall;
    final int highestDownfall;

    /**
     * main constructor of the seasons enum
     *
     * @param lowVis the lowest visibility possible in this season
     * @param highVis the highest visibility possible in this season
     * @param lowDownfall the lowest downfall possible in this season
     * @param highDownfall the highest downfall possible in this season
     */
    Seasons(int lowVis, int highVis, int lowDownfall, int highDownfall){
        this.lowestVisibility = lowVis;
        this.highestVisibility = highVis;
        this.lowestDownfall = lowDownfall;
        this.highestDownfall = highDownfall;
    }
}
