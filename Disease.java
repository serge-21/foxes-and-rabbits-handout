import java.util.HashMap;
import java.util.List;

public class Disease extends Entity{
    private double spreadRate;
    private double deadliness;
    private HashMap<Organism, Integer> numOfInteractions;

    public Disease(AnimalStats temp, Field field, double spreadRate, double deadliness, Organism mainHost, Location initLocation){
        super(temp, field, initLocation);
        this.spreadRate = spreadRate;
        this.deadliness = deadliness;
        numOfInteractions = new HashMap<>();
        numOfInteractions.put(mainHost, 10);
    }

    public void updateInteractions(){
        Field field = getField();
        List<Location> potentialVictim  = field.getNotNullAdjacentLocations(getLocation());
        for(Location victim : potentialVictim){
            Organism organism = (Organism) field.getObjectAt(victim);
            if(numOfInteractions.get(organism) == null){
                numOfInteractions.put(organism, 0);
            }else{
                numOfInteractions.put(organism, numOfInteractions.get(organism) +1 );
            }
            // numOfInteractions.getOrDefault(organism + 1, 0) maybe simplify using this?
        }
    }

    // this is essentially the act method for the disease.
    // maybe make it affected by the weather??????
    public void infect(Weather currentWeather){
        updateInteractions();
        if(getRand().nextDouble() < spreadRate){
            // infect neighbouring squares with a fixed probability.
            Field field = getField();
            List<Location> adjacent = field.getNotNullAdjacentLocations(getLocation());
            for(Location where : adjacent){
                Organism organism = (Organism) field.getObjectAt(where);
                if(canInfect(organism) && !organism.getIsInfected()){
                    if(getRand().nextDouble() < spreadRate){
                        organism.setInfected(true);
                        organism.addDisease(this);
                    }
                }
            }
        }
    }

    public double getDeadliness(){
        return deadliness;
    }

    public boolean canInfect(Organism organism){
        return numOfInteractions.get(organism) >= 10;
    }
}
