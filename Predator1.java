import java.awt.*;
import java.util.List;

/**
 * A simple model of a predator1.
 * predator1es age, move, eat prey1s, and die.
 *
 * @author Syraj Alkhalil and Cosmo
 * @version 2016.02.29 (2)
 */
public class Predator1 extends Animal
{
    // Characteristics shared by all predator1s (class variables).
    private static final int BREEDING_AGE = 15;                 // The age at which a predator1 can start to breed.
    private static final int MAX_AGE = 130;                     // The age to which a predator1 can live.
    private static final double BREEDING_PROBABILITY = 0.37;    // The likelihood of a predator1 breeding.
    private static final int MAX_LITTER_SIZE = 2;               // The maximum number of births.
    private static final int PREY1_FOOD_VALUE = 20;             // The food value of a single prey1. In effect, this is the
                                                                // number of steps a predator1 can go before it has to eat again.

    /**
     * Create a predator1. A predator1 can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the predator1 will have random age and hunger level.
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Predator1(boolean randomAge, Field field, Location initLocation) {
        super(randomAge, field, initLocation, false, PREY1_FOOD_VALUE, MAX_AGE);
        setColor(Color.RED);
    }
    
    /**
     * This is what the predator1 does most of the time: it hunts for
     * prey1s. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newPredator1 A list to return newly born predator1es.
     * @param isDay is it currently day or night ?
     */
    public void act(List<Organism> newPredator1, boolean isDay)
    {
        if(determineDay(isDay)){
            incrementAge();
            incrementHunger();
            if(getIsAlive()) {
                giveBirth(newPredator1);
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
     * Increase the age. This could result in the predator1's death.
     */
    private void incrementAge()
    {
        setAge(getAge() + 1);
        if(getAge() > MAX_AGE) {
            setDead();
        }
    }
    

    
    /**
     * Look for prey1s adjacent to the current location.
     * Only the first live prey1 is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Object animal = field.getObjectAt(where);
            if (animal instanceof Prey1 prey1) {
                if (prey1.getIsAlive()) {
                    prey1.setDead();
                    setFoodLevel(PREY1_FOOD_VALUE);
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this predator1 is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPredator1 A list to return newly born predator1es.
     */
    private void giveBirth(List<Organism> newPredator1)
    {
        // New predator1es are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();

        // we need to call find mate first.
        if(findMate()){
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Predator1 young = new Predator1(false, field, loc);
                newPredator1.add(young);
            }
        }
    }

    @Override
    protected boolean findMate() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Object animal = field.getObjectAt(where);
            if (animal instanceof Predator1 potentialPartner && (((Predator1) animal).getIsMale() != this.getIsMale())) {
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
     * A predator1 can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }
}
