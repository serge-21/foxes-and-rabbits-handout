import java.awt.*;
import java.util.List;

public abstract class Organism extends Entity {
    private boolean isAlive;
    private Location location;
    private int moveSpeed;

    public Organism(boolean isDrawable, Field field, Location initLocation){
        super(isDrawable, field);
        this.isAlive = true;
        setLocation(initLocation);
    }

    abstract public void act(List<Organism> newAnimals);

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
