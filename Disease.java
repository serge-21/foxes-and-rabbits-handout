import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * The disease class which is responsible for infecting the animals and potentially killing them
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class Disease {
    private final double spreadRate;                                // the spread rate of the disease
    private final double deadliness;                                // how deadly the disease is
    private int infectionPeriod = 2;                                // how long the disease will last
    private final Entity mainHost;                                  // the main host of the disease
    private final HashMap<Organism, Integer> numOfInteractions;     // the number of infections per animal type this helps in mutations

    /**
     * make a disease that infects a certain type of animal
     *
     * @param spreadRate The spread rate (how contagious the disease is).
     * @param deadliness How deadly the disease is
     * @param mainHost the main type of host that this disease will infect
     */
    public Disease(double spreadRate, double deadliness, Organism mainHost) {
        this.spreadRate = spreadRate;
        this.deadliness = deadliness;
        this.mainHost = mainHost;
        numOfInteractions = new HashMap<>();
        numOfInteractions.put(mainHost, 10);
    }

    /**
     * This method is used to update how many animals the disease comes in contact with
     * this will be used to mutate the types of animals the disease can infect, for example
     * if the disease comes in contact with
     */
    public void updateInteractions() {
        Field field = mainHost.getField();
        if(field != null){
            List<Location> potentialVictim  = field.getNotNullAdjacentLocations(mainHost.getLocation());
            for(Location victim : potentialVictim){
                Organism organism = (Organism) field.getObjectAt(victim);
                if(numOfInteractions.get(organism) == null){
                    numOfInteractions.put(organism, 0);
                }else{
                    numOfInteractions.put(organism, numOfInteractions.get(organism) +1 );
                }

            }
        }
    }

    /**
     * This is essentially the act method for the disease.
     * the disease will look for not null locations around it and will try to infect these organisms
     * The disease is also affected by the weather.
     *
     * @param currentWeather the current weather
     */
    public void infect(Weather currentWeather) {
        updateInteractions();
        if((new Random().nextDouble() < (spreadRate - (currentWeather.getActualDownfall()*0.04))) && infectionPeriod > 0){
            // infect neighbouring squares with a fixed probability.
            Field field = mainHost.getField();
            List<Location> adjacent = field.getNotNullAdjacentLocations(mainHost.getLocation());
            for(Location where : adjacent){
                Organism organism = (Organism) field.getObjectAt(where);
                if(canInfect(organism) && !(organism).getIsInfected()){
                    if(new Random().nextDouble() < spreadRate){
                        organism.setInfected(true);
                        organism.addDisease(this);
                        organism.dieDueToInfection();
                    }
                }
            }
        }
        infectionPeriod--;
    }

    /**
     * A simple getter method that returns the deadliness field
     *
     * @return how deadly the disease is
     */
    public double getDeadliness() {
        return deadliness;
    }

    /**
     * decide if we can infect a given organism
     *
     * @param organism the organism we wish to infect
     * @return true if we can infect the organism
     */
    public boolean canInfect(Organism organism) {
        return numOfInteractions.get(organism) >= 10;
    }
}
