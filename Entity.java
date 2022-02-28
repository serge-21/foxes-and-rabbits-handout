import java.util.Random;

/**
 * An entity is a super class that all entities represented on the simulation share.
 * This includes the disease, animals, plants etc...
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class Entity {
    private int ageInSteps;                                     // get the age of the entity in steps
    private Field field;                                        // The field occupied by the entity
    private boolean isAlive;                                    // Is the entity currently alive
    private Location location;                                  // The location of the entity
    private static final Random rand = Randomizer.getRandom();  // The randomness all entities share
    private final EntityStats entityStats;                      // The entity stats these are subject to change

    /**
     * Constructor for objects of class Entity
     */
    public Entity(EntityStats stats ,Field field, Location initLocation) {
        // initialise instance variables
        this.entityStats = stats;
        this.ageInSteps = 0;
        this.isAlive = true;
        this.field = field;
        setLocation(initLocation);
    }

    /**
     * A simple method to return the age of the entity in days instead of steps
     *
     * @return the current age of the entity
     */
    public double getAgeInDays() {
        double value = getAgeInSteps() / 24.0;
        return Math.floor(value * 100) / 100;
    }

    /**
     * A simple getter method to get the age field
     *
     * @return the current age of the entity
     */
    public int getAgeInSteps() {
        return ageInSteps;
    }

    /**
     * A simple setter method to set the field
     *
     * @param field the field we want the entity to act in.
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * Return the animal's field.
     *
     * @return The animal's field.
     */
    public Field getField() {
        return field;
    }


    /**
     * A simple setter method to set the age of the entity
     *
     * @param ageInSteps the age we want the entity to have
     */
    public void setAgeInSteps(int ageInSteps) {
        this.ageInSteps = ageInSteps;
    }

    /**
     * Return the animal's location.
     *
     * @return The animal's location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Place the animal at the new location in the given field.
     *
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation) {
        if(location != null) {
            getField().clear(location);
        }
        location = newLocation;
        getField().place(this, newLocation);
    }

    /**
     * A simple getter method to get the isAlive field
     *
     * @return true if the entity is alive
     */
    protected boolean getIsAlive() {
        return isAlive;
    }

    /**
     * return the rand field which contains a Random object
     *
     * @return the random object we have
     */
    public static Random getRand() {
        return rand;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead() {
        isAlive = false;
        if(location != null) {
            getField().clear(getLocation());
            location  = null;
            setField(null);
        }
    }

    /**
     * A simple getter method that returns the EntityStats field
     *
     * @return the entity statistics
     */
    public EntityStats getStats() {
        return entityStats;
    }
}
