import java.awt.*;
import java.util.List;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Fox extends Animal
{
    // Characteristics shared by all foxes (class variables).
    private static final int BREEDING_AGE = 15;                         // The age at which a fox can start to breed.
    private static final int MAX_AGE = 150;                             // The age to which a fox can live.
    private static final double BREEDING_PROBABILITY = 0.28;            // The likelihood of a fox breeding.
    private static final int MAX_LITTER_SIZE = 2;                       // The maximum number of births.
    private static final int RABBIT_FOOD_VALUE = 16;                    // number of steps a fox can go before it has to eat again.
                                                                        // The food value of a single rabbit. In effect, this is the
    /**
     * Create a fox. A fox can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Fox(boolean randomAge, Field field, Location initLocation) {
        super(randomAge, field, initLocation, true, RABBIT_FOOD_VALUE, MAX_AGE);
        setColor(Color.BLUE);
        setPrey();
    }

    private void setPrey(){
        addPrey(Rabbit.class);
    }
    
    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newFoxes A list to return newly born foxes.
     * @param isDay is it currently day or night ?
     */
    public void act(List<Organism> newFoxes, boolean isDay, Weather currentWeather)
    {
        if(determineDay(isDay)){
            updateStatsOfAnimal();
            incrementAge();     // age is unique and can't be updated with other stats.
            if(getIsAlive()) {
                if(getBreedCounter() <= 0){
                    giveBirth(newFoxes);
                }
                moveLocationOfAnimal(currentWeather);
            }
        }
    }

    /**
     * Increase the age. This could result in the fox's death.
     */
    private void incrementAge()
    {
        setAge(getAge() + 1);
        if(getAge() > MAX_AGE) {
            setDead();
        }
    }

    @Override
    protected int getBreedingAge() {
        return BREEDING_AGE;
    }
    
    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    private void giveBirth(List<Organism> newFoxes) {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();

        // need to find a mate first, otherwise it makes no sense to breed alone.
        if(findMate(this.getClass())){
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Fox young = new Fox(false, field, loc);
                newFoxes.add(young);
            }
        }
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
        }
        return births;
    }

    /**
     * A fox can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }
}
