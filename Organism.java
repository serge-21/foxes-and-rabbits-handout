import java.util.List;
import java.util.Random;

public abstract class Organism extends Entity {
    private int moveSpeed;                                      // current movement speed
    private boolean isAlive;                                    // is the organism currently alive
    private Location location;                                  // the current location of the organism
    private static final Random rand = Randomizer.getRandom();  // the randomness all organisms share

    /**
     * Create an Organism. An organism can be created with a field and
     * a location.
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Organism(Field field, Location initLocation){
        super(field);
        this.isAlive = true;
        setLocation(initLocation);
    }
    /**
     * @param newOrganisms A list to return newly born animals.
     * @param isDay is it currently day or night ?
     */
    abstract public void act(List<Organism> newOrganisms, boolean isDay);

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        isAlive = false;
        if(location != null) {
            getField().clear(location);
            location = null;
            setField(null);
        }
    }

    public void setMoveSpeed(int moveSpeed){
        this.moveSpeed = moveSpeed;
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    /**
     * return the rand field which contains a Random object
     */
    public static Random getRand() {
        return rand;
    }

    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            getField().clear(location);
        }
        location = newLocation;
        getField().place(this, newLocation);
    }

    public int getMoveSpeed(){
        return moveSpeed;
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean getIsAlive(){
        return isAlive;
    }
}
