import java.util.ArrayList;
import java.util.List;

public abstract class Organism extends Entity {
    private int moveSpeed;                                      // current movement speed
    private boolean isInfected;                                         // to be passed in
    private ArrayList<Disease> diseases;

    /**
     * Create an Organism. An organism can be created with a field and
     * a location.
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Organism(EntityStats stats, Field field, Location initLocation){
        super(stats, field, initLocation);
        diseases = new ArrayList<>();
        this.isInfected = false;
    }

    /**
     * @param newOrganisms A list to return newly born animals.
     * @param isDay is it currently day or night ?
     */
    abstract public void act(List<Organism> newOrganisms, boolean isDay, Weather currentWeather);

    // either two different act methods or just deal with the code being repeated.

    public void addDisease(Disease disease){
        this.diseases.add(disease);
    }

    public boolean getIsInfected() {
        return isInfected;
    }
    public void setInfected(boolean infection){
        this.isInfected = infection;
    }

    public void setMoveSpeed(int moveSpeed){
        this.moveSpeed = moveSpeed;
    }
    public int getMoveSpeed(){
        return moveSpeed;
    }


    // this could be probably a bit more efficient but i will leave it for now.
    protected void dieDueToInfection(){
        for(Disease disease : this.diseases){
            if(isInfected && getRand().nextDouble() < disease.getDeadliness()){
                setDead();
            }
        }
    }

    // TODO this entire class has no point now.
    // TODO move class Organism into entity.
    // TODO ORRRRRRRR keep it for easier distinction ???
    // TODO DECIDE IN A WEEK FROM NOW.
}
