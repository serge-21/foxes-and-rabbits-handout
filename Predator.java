import java.awt.*;
import java.util.List;

/**
 * A simple model of a predator1.
 * predator1es age, move, eat prey1s, and die.
 *
 * @author Syraj Alkhalil and Cosmo
 * @version 2016.02.29 (2)
 */
public class Predator extends Animal
{
//    // Characteristics shared by all predator1s (class variables).
//    private static final int breedingAge = 15;                 // The age at which a predator1 can start to breed.
//    private static final int maxAge = 130;                     // The age to which a predator1 can live.
//    private static final double breedingProbability = 0.37;    // The likelihood of a predator1 breeding.
//    private static final int maxLitterSize = 2;               // The maximum number of births.
//    private static final int hungerValue = 20;             // The food value of a single prey1. In effect, this is the
//    // number of steps a predator1 can go before it has to eat again.


    private AnimalStats animalStats;

    /**
     * Create a predator1. A predator1 can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the predator1 will have random age and hunger level.
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Predator(AnimalStats stats, boolean randomAge, Field field, Location initLocation) {
        super(stats, randomAge, field, initLocation, true, stats.getHungerValue(), stats.getMaxAge());
        animalStats = stats;
        //setColor(Color.RED);
        setPrey();
    }

    private void setPrey(){
        addPrey(Prey.class);
    }

    /**
     * This is what the predator1 does most of the time: it hunts for
     * prey1s. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newPredator A list to return newly born predator1es.
     * @param isDay is it currently day or night ?
     */
    public void act(List<Organism> newPredator, boolean isDay, Weather currentWeather)
    {
        if(determineDay(isDay)){
            updateStatsOfAnimal();
            incrementAge();     // age is unique and can't be updated with other stats.
            if(getIsAlive()) {
                if(getBreedCounter() <= 0){
                    giveBirth(newPredator);
                }
                moveLocationOfAnimal(currentWeather);
            }
        }

    }

    /**
     * Increase the age. This could result in the predator1's death.
     */
    private void incrementAge()
    {
        setAge(getAge() + 1);
        if(getAge() > animalStats.getMaxAge()) {
            setDead();
        }
    }

    /**
     * Check whether or not this predator1 is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPredator A list to return newly born predator1es.
     */
    private void giveBirth(List<Organism> newPredator)
    {
        // New predator1es are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();

        // we need to call find mate first.
        if(findMate(this.getClass())){
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Predator young = new Predator(animalStats, false, field, loc);
                newPredator.add(young);
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
     * A predator1 can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return getAge() >= animalStats.getBreedingAge();
    }

    public Predator createClone(boolean randomAge, Field field, Location initLocation){
        return new Predator(this.animalStats, randomAge, field, initLocation);
    }
}