import java.awt.*;
import java.util.List;

public class Plants extends Organism{
    // characteristics all plants share
    private static final double BREEDING_PROBABILITY = 0.001;       // how likely a plant will breed in a step
    public static final int foodValue = 14;                         // for now make it public
    private static final int MAX_LEVEL = 5;
    private int currentLevel;
    private int waterLevel;
    private int sunLightLevel;

    /**
     * Create a plant. A plant can be created as with a field and
     * a location
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Plants(Field field,  Location initLocation){
        super(field, initLocation);
        setColor(Color.GREEN);
        this.currentLevel = 1;
        this.waterLevel = 1;
        this.sunLightLevel = 1;
    }

    /**
     * This is what the plant does most of the time: FOR NOW nothing.
     * for now plants will do nothing they will stay here and not die.
     * @param newPlants A list to return newly born plants.
     * @param isDay is it currently day or night ?
     */
    @Override
    public void act(List<Organism> newPlants, boolean isDay, Weather currentWeather) {
        if(isDay){
            grow(currentWeather);
            if(this.currentLevel > 2){
                giveBirth(newPlants);
            }
        }
    }

    private void grow(Weather currentWeather){
        photosynthesis(currentWeather);
        transpiration(currentWeather);
        // allow growth
        if(this.sunLightLevel > 10 && this.waterLevel > 12){
            // then increment the level
            if(this.currentLevel < MAX_LEVEL){
                this.currentLevel += 1;
                // we should also reduce the resources of plants IFF they grow
                this.waterLevel = 3;
                this.sunLightLevel = 2;
            }
        }
//        System.out.println(this.sunLightLevel);
//        System.out.println(this.waterLevel);
//        System.out.println(this.currentLevel);
        // otherwise, do nothing
    }

    private void transpiration(Weather currentWeather){
        // neater way of writing if else blocks ew
        this.waterLevel = (currentWeather.getActualDownfall() < 10) ? this.waterLevel - 1 : this.waterLevel + 1;
        if(this.waterLevel <= 0){
            setDead();
        }
    }

    private void photosynthesis(Weather currentWeather){
        // neater way of writing if else blocks ew
        this.sunLightLevel = (currentWeather.getActualVisibility() < 10) ? this.sunLightLevel - 1 : this.sunLightLevel + 1;
        if(this.sunLightLevel <= 0){
            setDead();
        }
    }

    @Override
    protected void setDead() {
        if(this.currentLevel <= 0){
            super.setDead();
        }else{
            currentLevel -= 1;
            setLocation(getLocation());
        }
    }

    /**
     * Check whether or not this plant is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPlants A list to return newly born plants.
     */
    private void giveBirth(List<Organism> newPlants)
    {
        // New plants are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plants young = new Plants(field, loc);
            newPlants.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (maybe zero).
     */
    private int breed() {
        int births = 0;
        if(getRand().nextDouble() <= BREEDING_PROBABILITY) {
            births = getRand().nextInt(4) + 1;
        }
        return births;
    }
}
