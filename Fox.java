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
        super(randomAge, field, initLocation, false, RABBIT_FOOD_VALUE, MAX_AGE);
        setColor(Color.BLUE);
    }
    
    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newFoxes A list to return newly born foxes.
     * @param isDay is it currently day or night ?
     */
    public void act(List<Organism> newFoxes, boolean isDay)
    {
        if(determineDay(isDay)){
            incrementAge();
            incrementHunger();
            if(getIsAlive()) {
                giveBirth(newFoxes);
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
     * Increase the age. This could result in the fox's death.
     */
    private void incrementAge()
    {
        setAge(getAge() + 1);
        if(getAge() > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Object animal = field.getObjectAt(where);
            if (animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if (rabbit.getIsAlive()) {
                    rabbit.setDead();
                    setFoodLevel(RABBIT_FOOD_VALUE);
                    return where;
                }
            }
        }
        return null;
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
        if(findMate()){
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
     * This method is responsible for finding a mate.
     * The method will go check for each organism in the field
     * and IF they find a mate then the method will return true.
     *
     * @return a boolean value of whether we found a mate of Fox class.
     */
    @Override
    protected boolean findMate() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Object animal = field.getObjectAt(where);
            if (animal instanceof Fox && (((Fox) animal).getIsMale() != this.getIsMale())) {
                Fox potentialPartner = (Fox) animal;
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
     * A fox can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }
}
