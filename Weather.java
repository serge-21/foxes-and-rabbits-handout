/**
 * This is the weather class it holds an instance of the Seasons
 * and generates a visibility and downfall according to the season.
 * Everytime the generation of both the downfall and the visibility is randomised
 * to give more variety and realism to the simulation.
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class Weather {
    private final Randomizer rand = new Randomizer();                   // a random chance of things happening
    private final Seasons[] possibleSeasons = Seasons.values();         // all the possible values of seasons
    private Seasons currentSeason;                                      // current season
    private final int max = possibleSeasons.length;                     // the total number of the seasons
    private int current = rand.nextInt(possibleSeasons.length);         // the index of the current season

    private int actualVisibility;                                       // the visibility generated
    private int actualDownfall;                                         // the downfall generated

    /**
     * the main constructor of the weather class
     * initialise the weather object with a seasons, visibility, and a downfall
     */
    public Weather(){
        pickSeason();
        generateVisibilityAndDownfall();
    }

    /**
     * Generating the visibility and downfall of the weather with some variability using the randomizer instance
     * Java 11 doesn't allow for nextInt(a, b) so this alternative method was used to give a more realistic
     * representation of the weather.
     */
    public void generateVisibilityAndDownfall(){
        this.actualVisibility  = rand.nextInt(this.currentSeason.highestVisibility) + this.currentSeason.lowestVisibility;
        this.actualDownfall = rand.nextInt(this.currentSeason.highestDownfall) + this.currentSeason.lowestDownfall;
    }

    /**
     * This method is used to reset the weather completely.
     */
    public void resetWeather(){
        rand.completeReset();
        current = rand.nextInt(possibleSeasons.length);
    }

    /**
     * This method is responsible for picking a season out of the
     * seasons present in the simulation, this method will pick seasons periodically
     * i.e. the seasons will be in constant cycle for a more accurate representation
     */
    public void pickSeason(){
        if(current + 1 < max){
            current++;
        }else{
            current = 0;
        }
        this.currentSeason = possibleSeasons[current];
    }

    /**
     * A simple getter method to return the actualVisibility field
     *
     * @return the int value of the actual visibility
     */
    public int getActualVisibility(){
        return actualVisibility;
    }

    /**
     * A simple getter method to return the actualDownfall field
     *
     * @return the int value of the actual downfall
     */
    public int getActualDownfall(){
        return actualDownfall;
    }

    /**
     * Overriding the method toString to represent the weather in a good way
     *
     * @return the string representation of the current weather
     */
    @Override
    public String toString(){
        String s = "Current season: " + currentSeason.toString() + "<br>";
        s += "Current visibility: " + getActualVisibility();
        s += "<br>";
        s += "Current downfall: " + getActualDownfall();
        return s;
    }
}
