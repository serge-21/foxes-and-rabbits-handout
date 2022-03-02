import java.util.ArrayList;
import java.util.List;

/**
 * The Organism class is responsible for distinguishing what can and can't be infected
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public abstract class Organism extends Entity {
    private boolean isInfected;                                 // Is this organism infected
    private final ArrayList<Disease> diseases;                  // the list of disease this organism has

    /**
     * Create an Organism. An organism can be created with a field and
     * a location.
     * @param stats the statistics of the organism
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Organism(EntityStats stats, Field field, Location initLocation) {
        super(stats, field, initLocation);
        diseases = new ArrayList<>();
        this.isInfected = false;
    }

    /**
     * @param newOrganisms A list to return newly born animals.
     * @param isDay is it currently day or night ?
     * @param currentWeather the current weather
     */
    abstract public void act(List<Organism> newOrganisms, boolean isDay, Weather currentWeather);

    /**
     * make this organism infected with the disease
     *
     * @param disease the disease we wish this organism to be infected by
     */
    public void addDisease(Disease disease) {
        this.diseases.add(disease);
    }

    public ArrayList<Disease> getDiseases(){
        return diseases;
    }

    /**
     * A simple getter method that return isInfected field
     *
     * @return is this organism infected
     */
    public boolean getIsInfected() {
        return isInfected;
    }

    /**
     * A simple setter field that sets the isInfected field
     *
     * @param infection true if this organism is infected false otherwise
     */
    public void setInfected(boolean infection) {
        this.isInfected = infection;
    }

    /**
     * let the current organism die due to infection
     * balancing this is a nightmare
     */
    protected void dieDueToInfection() {
        for(Disease disease : this.diseases){
            if(isInfected && getRand().nextDouble() < disease.getDeadliness()){
                setDead();
            }
        }
    }
}
