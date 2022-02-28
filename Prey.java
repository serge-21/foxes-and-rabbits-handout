import java.util.List;

/**
 * A simple model of a prey1.
 * prey1s age, move, breed, and die.
 *
 * @author Syraj Alkhalil and Cosmo
 * @version 2016.02.29 (2)
 */
public class Prey extends Animal
{
//    // Characteristics shared by all prey1s (class variables).
//    private static final int BREEDING_AGE = 5;                  // The age at which a prey1 can start to breed.
//    private static final int MAX_AGE = 50;                      // The age to which a prey1 can live.
//    private static final double BREEDING_PROBABILITY = 0.07;    // The likelihood of a prey1 breeding.
//    private static final int MAX_LITTER_SIZE = 3;               // The maximum number of births.
//    private static final int PLANT_FOOD_VALUE = 40;             // number of steps a prey1 can go before it has to eat again.

    private AnimalStats animalStats;

    /**
     * Create a new prey1. A prey1 may be created with age
     * zero (a newborn) or with a random age.
     *
     * @param randomAge If true, the prey1 will have a random age.
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Prey(AnimalStats stats, boolean randomAge, Field field, Location initLocation) {
        super(stats, randomAge, field, initLocation, false, stats.getHungerValue(), stats.getMaxAge());
        animalStats = stats;
        //setColor(Color.MAGENTA);
        setPrey();
    }
    private void setPrey(){
        addPrey(Plant.class);
    }

    /**
     * This is what the prey1 does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param newPrey1 A list to return newly born prey1s.
     * @param isDay is it currently day or night ?
     */
    public void act(List<Organism> newPrey1, boolean isDay, Weather currentWeather)
    {
        incrementAge();     // age is unique and can't be updated with other stats.
        if(determineDay(isDay)){
            updateStatsOfAnimal();
            if(getIsAlive()) {
                if(getBreedCounter() <= 0){
                    giveBirth(newPrey1);
                }
                moveLocationOfAnimal(currentWeather);
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the prey1's death.
     */
    private void incrementAge()
    {
        setAgeInSteps(getAgeInSteps() + 1);
        if(getAgeInDays() > animalStats.getMaxAge()) {
            setDead();
        }
    }

    /**
     * Check whether or not this prey1 is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPrey A list to return newly born prey1s.
     */
    private void giveBirth(List<Organism> newPrey)
    {
        // New prey1s are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        if(findMate(this)){
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Prey young = new Prey(animalStats, false, field, loc);
                newPrey.add(young);
            }
        }
    }

    @Override
    protected int getBreedingAge() {
        return animalStats.getBreedingAge();
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (maybe zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && getRand().nextDouble() <= animalStats.getBreedingProbability()) {
            births = getRand().nextInt(animalStats.getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * A prey1 can breed if it has reached the breeding age.
     * @return true if the prey1 can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return getAgeInDays() >= animalStats.getBreedingAge();
    }

    public Prey createClone(boolean randomAge, Field field, Location initLocation){
        return new Prey(this.animalStats, randomAge, field, initLocation);
    }

    public EntityStats getStats() {
        return animalStats;
    }
}
