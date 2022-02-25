import java.awt.*;
import java.util.Random;

/**
 * Write a description of class Entity here.
 *
 * @author Syraj Alkhalil
 * @version 10-02-2022
 */
public class Entity {

    private boolean isDrawable = true;
    //private Color color;
    private int age;
    private Field field;

    // these were copied from the organism
    private boolean isAlive;                                    // is the organism currently alive
    private Location location;
    private static final Random rand = Randomizer.getRandom();  // the randomness all organisms share

    private EntityStats entityStats;
    private final EntityStats DEFAULT_STATS;

    /**
     * Constructor for objects of class Entity
     */
    public Entity(EntityStats stats ,Field field, Location initLocation) {
        // initialise instance variables
        this.entityStats = stats;
        DEFAULT_STATS = entityStats;

        this.age = 0;
        this.isAlive = true;
        this.field = field;
        setLocation(initLocation);
    }

//    public Entity(){
//        // this will help in setting colours hopefully?
//    }


    public boolean getIsDrwable(){
        return isDrawable;
    }

//    public void setColor(Color color){
//        this.color = color;
//    }

//    public Color getColor() {
//        if(isDrawable){
//            return color;
//        }
//        return Color.white;
//    }

    public int getAge() {
        return age;
    }

    public void toggleDrawable(){
        isDrawable = !isDrawable;
    }

    public void setField(Field field) {
        this.field = field;
    }

    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    public Field getField() {
        return field;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation() {
        return location;
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

    protected boolean getIsAlive(){
        return isAlive;
    }

    protected void setAlive(boolean isAlive){
        this.isAlive = isAlive;
    }

    /**
     * return the rand field which contains a Random object
     */
    public static Random getRand() {
        return rand;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        isAlive = false;
        if(location != null) {
            getField().clear(getLocation());
            location  = null;
            setField(null);
        }
    }

    public EntityStats getStats() {
        return entityStats;
    }

    public EntityStats getDefaultStats() {
        return DEFAULT_STATS;
    }
}
