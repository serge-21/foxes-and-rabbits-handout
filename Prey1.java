import java.awt.*;
import java.util.List;

/**
 * A simple model of a prey1.
 * prey1s age, move, breed, and die.
 * 
 * @author Syraj Alkhalil and Cosmo
 * @version 2016.02.29 (2)
 */
public class Prey1 extends Animal
{
    // Characteristics shared by all prey1s (class variables).
    private static final int BREEDING_AGE = 5;                  // The age at which a prey1 can start to breed.
    private static final int MAX_AGE = 50;                      // The age to which a prey1 can live.
    private static final double BREEDING_PROBABILITY = 0.07;    // The likelihood of a prey1 breeding.
    private static final int MAX_LITTER_SIZE = 3;               // The maximum number of births.
    private static final int PLANT_FOOD_VALUE = 40;             // number of steps a prey1 can go before it has to eat again.

    /**
     * Create a new prey1. A prey1 may be created with age
     * zero (a newborn) or with a random age.
     *
     * @param randomAge If true, the prey1 will have a random age.
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Prey1(boolean randomAge, Field field, Location initLocation) {
        super(randomAge, field, initLocation, true, PLANT_FOOD_VALUE, MAX_AGE);
        setColor(Color.MAGENTA);
    }
    
    /**
     * This is what the prey1 does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param newPrey1 A list to return newly born prey1s.
     * @param isDay is it currently day or night ?
     */
    public void act(List<Organism> newPrey1, boolean isDay)
    {
        if(determineDay(isDay)){
            incrementAge();
            incrementHunger();
            if(getIsAlive()) {
                giveBirth(newPrey1);
                // Move towards a source of food if found.
                Location newLocation = findFood();
                if(newLocation == null) {
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                } else {
                    // Overcrowding.
                    setDead();
                }
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the prey1's death.
     */
    private void incrementAge()
    {
        setAge(getAge() + 1);
        if(getAge() > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this prey1 is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPrey1 A list to return newly born prey1s.
     */
    private void giveBirth(List<Organism> newPrey1)
    {
        // New prey1s are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        if(findMate()){
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Prey1 young = new Prey1(false, field, loc);
                newPrey1.add(young);
            }
        }
    }


    /**
     * find the location of the food.
     * @return the location of the prey note this could be a plant
     */
    @Override
    protected Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Object animal = field.getObjectAt(where);
            if (animal instanceof Plants) {
                setFoodLevel(PLANT_FOOD_VALUE);
                return where;
            }
        }
        return null;
    }

    @Override
    protected boolean findMate() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Object animal = field.getObjectAt(where);
            if (animal instanceof Prey1 potentialPartner && (((Prey1) animal).getIsMale() != this.getIsMale())) {
                if (potentialPartner.getAge() >= BREEDING_AGE) {
                    return true;
                }
            }
        }
        return false;
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
     * A prey1 can breed if it has reached the breeding age.
     * @return true if the prey1 can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }
}
