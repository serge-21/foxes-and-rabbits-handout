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

    // additional extra stats might be added later
    Seasons(int lowVis, int highVis, int lowDownfall, int highDownfall){
        this.lowestVisibility = lowVis;
        this.highestVisibility = highVis;
        this.lowestDownfall = lowDownfall;
        this.highestDownfall = highDownfall;
    }
}
