import java.util.Random;

public class Weather {
    private Random rand = new Random();
    private Seasons[] possibleSeasons = Seasons.values();
    private Seasons currentSeason;

    private int actualVisibility;
    private int actualDownfall;

    public Weather(){
        pickSeason();
        generateVisibilityAndDownfall();
    }

    public void generateVisibilityAndDownfall(){
        this.actualVisibility  = rand.nextInt(this.currentSeason.highestVisibility) + this.currentSeason.lowestVisibility;
        this.actualDownfall = rand.nextInt(this.currentSeason.highestDownfall) + this.currentSeason.lowestDownfall;
    }

    public void pickSeason(){
        this.currentSeason = possibleSeasons[rand.nextInt(possibleSeasons.length)];
    }

    public int getActualVisibility(){
        return actualVisibility;
    }

    public int getActualDownfall(){
        return actualDownfall;
    }

    public String toString(){
        String s = "Current season: " + currentSeason.toString() + "\n";
        s += "actual vis: " + getActualVisibility();
        s += "\n";
        s += "actual downfall: " + getActualDownfall();
        return s;
    }

}
//public enum Weather {
//    CLEAR_SKY(100, 0),
//    LIGHT_RAIN(1000, 50),
//    HEAVY_RAIN(4000, 100),
//    LIGHT_FOG(7500, 0),
//    HEAVY_FOG(9000, 0);
//
//    final double visibility;
//    final double downfall;
//
//    Weather(double visibility, double downfall){
//        this.visibility = visibility;
//        this.downfall = downfall;
//    }
//}

