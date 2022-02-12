import java.awt.*;
import java.util.List;

public class Plants extends Organism{
    // characteristics all plants share
    private static final double BREEDING_PROBABILITY = 0.001;       // how likely a plant will breed in a step

    /**
     * Create a plant. A plant can be created as with a field and
     * a location
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Plants(Field field,  Location initLocation){
        super(field, initLocation);
        setColor(Color.GREEN);
    }

    /**
     * This is what the plant does most of the time: FOR NOW nothing.
     * for now plants will do nothing they will stay here and not die.
     * @param newPlants A list to return newly born plants.
     * @param isDay is it currently day or night ?
     */
    @Override
    public void act(List<Organism> newPlants, boolean isDay) {
//        Location newLocation = getField().freeAdjacentLocation(getLocation());
//
//        if(newLocation != null){
//            setLocation(newLocation);
//        }

        // plant creates itself
//        Location location = getLocation();
//        if(!isDay){

//            giveBirth(newPlants);
//        }
//        setLocation(getLocation());
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
