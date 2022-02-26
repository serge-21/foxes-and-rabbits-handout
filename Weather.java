public class Weather {
    private final Randomizer rand = new Randomizer();
    private final Seasons[] possibleSeasons = Seasons.values();
    private Seasons currentSeason;
    private final int max = possibleSeasons.length;
    private int current = rand.nextInt(possibleSeasons.length);

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

    public void resetWeather(){
        rand.completeReset();
        current = rand.nextInt(possibleSeasons.length);
    }

    public void pickSeason(){
        // 256 322 99

//        if(current++ < max){
//            current = current+1;
//            this.currentSeason = possibleSeasons[rand.nextInt(possibleSeasons.length)];
//        }else{
//            current = 0;
//        }

        // 256 322 99 337/142/92
        if(current + 1 < max){
            current++;
        }else{
            current = 0;
        }
        this.currentSeason = possibleSeasons[current];
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
