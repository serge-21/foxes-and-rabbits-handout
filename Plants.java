import java.awt.*;
import java.util.List;
import java.util.Random;

public class Plants extends Organism{

    private static final double BREEDING_PROBABILITY = 0.001;
    private static final Random rand = Randomizer.getRandom();

    public Plants(Field field,  Location initLocation){
        super(field, initLocation);
        setColor(Color.GREEN);
    }

    @Override
    public void act(List<Organism> newPlants, boolean isDay) {
//        // here for now plants will do nothing they will stay here and not die.
//        Location newLocation = getField().freeAdjacentLocation(getLocation());
//
//        if(newLocation != null){
//            setLocation(newLocation);
//        }

        // plant creates itself

//        Location location = getLocation();
        if(!isDay){
            giveBirth(newPlants);
        }
        setLocation(getLocation());
    }

    private void giveBirth(List<Organism> newPlants)
    {
        // New rabbits are born into adjacent locations.
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

    private int breed()
    {
        int births = 0;
        if(rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(4) + 1;
        }
        return births;
    }
}
