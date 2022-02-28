import java.util.List;

/**
 * A simple model of all predators.
 * predators age, move, eat preys, and die.
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class Predator extends Animal {
    private final AnimalStats animalStats;

    /**
     * Create a predator. A predator can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param stats the statistics of this animal this ranges from the hunger level to breeding age etc...
     * @param randomAge If true, the predator will have random age and hunger level.
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Predator(AnimalStats stats, boolean randomAge, Field field, Location initLocation) {
        super(stats, randomAge, field, initLocation, true, stats.getHungerValue(), stats.getMaxAge());
        animalStats = stats;
        setPrey();          // add all prey that this animal will be feeding on
    }

    /**
     * this method will set the prey of this animal
     * which is stored in an ArrayList of type class that
     * can be expanded later.
     */
    private void setPrey() {
        addPrey(Prey.class);
    }

    /**
     * This is what the predator does most of the time: it hunts for
     * prey. In the process, it might breed, die of hunger, die of thirst, or die of old age.
     * @param newPredator A list to return newly born predators.
     * @param isDay is it currently day or night ?
     * @param currentWeather the current weather to affect the animal.
     */
    public void act(List<Organism> newPredator, boolean isDay, Weather currentWeather) {
        incrementAge();     // age is unique and can't be updated with other stats.
        if(determineDay(isDay)){
            updateStatsOfAnimal();
            if(getIsAlive()) {
                if(getBreedCounter() <= 0){
                    giveBirth(newPredator);
                }
                moveLocationOfAnimal(currentWeather);
            }
        }

    }

    /**
     * Increase the age. This could result in the predator's death.
     */
    private void incrementAge() {
        setAgeInSteps(getAgeInSteps() + 1);
        if(getAgeInDays() > animalStats.getMaxAge()) {
            setDead();
        }
    }

    /**
     * Check whether this predator is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPredator A list to return newly born predators.
     */
    private void giveBirth(List<Organism> newPredator) {
        // New predators are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();

        // we need to call find mate first.
        if(findMate(this)){
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Predator young = new Predator(animalStats, false, field, loc);
                newPredator.add(young);
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
     * A predator can breed if it has reached the breeding age.
     * @return a boolean if this animal can breed or not
     */
    private boolean canBreed() {
        return getAgeInDays() >= animalStats.getBreedingAge();
    }
}