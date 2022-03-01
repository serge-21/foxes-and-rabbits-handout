import java.util.List;

/**
 * A simple model of a prey.
 * preys age, move, breed, and die.
 *
 * @author Syraj Alkhalil and Cosmo
 * @version 2016.02.29 (2)
 */
public class Prey extends Animal {
    private final AnimalStats animalStats;

    /**
     * Create a new prey. A prey may be created with age
     * zero (a newborn) or with a random age.
     *
     * @param stats the statistics of this animal this ranges from the hunger level to breeding age etc...
     * @param randomAge If true, the prey will have a random age.
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Prey(AnimalStats stats, boolean randomAge, Field field, Location initLocation) {
        super(stats, randomAge, field, initLocation, stats.getHungerValue(), stats.getMaxAge());
        animalStats = stats;
        setPrey();
    }

    /**
     * this method will set the prey of this animal
     * which is stored in an ArrayList of type class that
     * can be expanded later.
     */
    private void setPrey(){
        addPrey(Plant.class);
    }

    /**
     * This is what the prey does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     *
     * @param newPrey A list to return newly born preys.
     * @param isDay is it currently day or night ?
     * @param currentWeather the current weather to affect the animal.
     */
    public void act(List<Organism> newPrey, boolean isDay, Weather currentWeather) {
        incrementAge();     // age is unique and can't be updated with other stats.
        if(determineDay(isDay)){
            updateStatsOfAnimal(currentWeather);
            if(getIsAlive()) {
                if(getBreedCounter() <= 0){
                    giveBirth(newPrey);
                }
                moveLocationOfAnimal(currentWeather);
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the prey's death.
     */
    private void incrementAge()
    {
        setAgeInSteps(getAgeInSteps() + 1);
        if(getAgeInDays() > animalStats.getMaxAge()) {
            setDead();
        }
    }

    /**
     * Check whether this prey is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newPrey A list to return newly born preys.
     */
    private void giveBirth(List<Organism> newPrey) {
        // New preys are born into adjacent locations.
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

    /**
     * this method is being overridden to help with overall generalisation in the super class
     * @return the breeding age of the predator
     */
    @Override
    protected int getBreedingAge() {
        return animalStats.getBreedingAge();
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (maybe zero).
     */
    private int breed() {
        int births = 0;
        if(canBreed() && getRand().nextDouble() <= animalStats.getBreedingProbability()) {
            births = getRand().nextInt(animalStats.getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * A prey can breed if it has reached the breeding age.
     * @return true if the prey can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return getAgeInDays() >= animalStats.getBreedingAge();
    }

    /**
     * A simple getter method to return the animalStats Field
     *
     * @return the animalStats field
     */
    public EntityStats getStats() {
        return animalStats;
    }
}
