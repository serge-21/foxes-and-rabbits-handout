import java.util.Random;

// TODO make the seasons change periodically

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
