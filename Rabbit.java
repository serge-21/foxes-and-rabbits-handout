import java.awt.*;
import java.util.List;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Rabbit extends Animal
{
    // Characteristics shared by all rabbits (class variables).
    private static final int BREEDING_AGE = 5;                          // The age at which a rabbit can start to breed.
    private static final int MAX_AGE = 40;                              // The age to which a rabbit can live.
    private static final double BREEDING_PROBABILITY = 0.12;            // The likelihood of a rabbit breeding.
    private static final int MAX_LITTER_SIZE = 4;                       // The maximum number of births.
    private static final int PLANT_FOOD_VALUE = 40;                     // number of steps a rabbit can go before it has to eat again.

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a newborn) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Rabbit(boolean randomAge, Field field, Location initLocation) {
        super(randomAge, field, initLocation, false, PLANT_FOOD_VALUE, MAX_AGE);
        setColor(Color.ORANGE);
        setPrey();
    }

    public Rabbit(){
        setColor(Color.orange);
    }

    private void setPrey(){
        addPrey(Plants.class);
    }
    
    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newRabbits A list to return newly born rabbits.
     * @param isDay is it currently day or night ?
     */
    public void act(List<Organism> newRabbits, boolean isDay, Weather currentWeather)
    {
        if(determineDay(isDay)){
            updateStatsOfAnimal();
            incrementAge();     // age is unique and can't be updated with other stats.
            if(getIsAlive()) {
                if(getBreedCounter() <= 0){
                    giveBirth(newRabbits);
                }
                moveLocationOfAnimal(currentWeather);
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the rabbit's death.
     */
    private void incrementAge()
    {
        setAge(getAge() + 1);
        if(getAge() > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRabbits A list to return newly born rabbits.
     */
    private void giveBirth(List<Organism> newRabbits)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        setPregnant(false);
        if(findMate(this.getClass())){
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Rabbit young = new Rabbit(false, field, loc);
                newRabbits.add(young);
            }
        }
    }

    @Override
    protected int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (maybe zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && getRand().nextDouble() <= BREEDING_PROBABILITY) {
            births = getRand().nextInt(MAX_LITTER_SIZE) + 1;
            setBreedCounter(10);
        }
        return births;
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }
}
