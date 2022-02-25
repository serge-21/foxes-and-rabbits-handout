import java.awt.*;

/**
 * A container for all the unique values for an entity. It also makes a backup of the values if the originals want to be reassigned if they're changed
 *
 * @author -
 * @version -
 */
public class EntityStats implements Cloneable{
    public enum EntityType{
        PREY, PREDATOR, PLANT
    }

    private String name;
    private EntityType entityType;
    private Color color;
    private boolean isEnabled;
    private double breedingProbability;
    private double creationProbability;

    private final EntityStats DEFAULT_STATS;

    /**
     * Created a container for the entity's statistics.
     * @param name The name of the entity.
     * @param entityType The type of the entity. Should be declared as an EntityType Enum
     * @param color The colour of the entity.
     * @param breedingProbability The probability that the entity will breed.
     * @param creationProbability The probability that the entity is places on the field.
     */
    public EntityStats(String name, EntityType entityType, Color color, double breedingProbability, double creationProbability){
        this.name = name;
        this.entityType = entityType;
        this.color = color;
        this.breedingProbability = breedingProbability;
        this.creationProbability = creationProbability;
        this.isEnabled = true; // Enabled by default

        DEFAULT_STATS = new EntityStats(this);
    }

    /**
     * Clones a pre-existing EntityStats.
     * @param clone The EntityStats you wish to clone.
     */
    protected EntityStats(EntityStats clone){
        this.name = clone.name;
        this.entityType = clone.entityType;
        this.color = clone.color;
        this.breedingProbability = clone.breedingProbability;
        this.creationProbability = clone.creationProbability;
        this.isEnabled = clone.isEnabled; // USELESS CODE CAUSE ITS ALWAYS TRUE BY DEFAULT BUT UHH

        // PREVENTS INFINITE LOOP (I THINK LOL)
        DEFAULT_STATS = this;
    }

    /**
     * Creates an EntityStats with all values set to 0 and type to PREY.
     */
    public EntityStats(){
        this.name = "SampleName";
        this.entityType = EntityType.PREY;
        this.color = Color.BLACK;
        this.breedingProbability = 0.0;
        this.creationProbability = 0.0;
        this.isEnabled = true; // Enabled by default

        DEFAULT_STATS = new EntityStats(this);
    }

    // DOES THIS WORK..? IDK
    // (OKAY, I THINK IT FINE LOL)
    /**
     * Clones the entity.
     * @return The entity cloned.
     * @throws CloneNotSupportedException
     */
    public EntityStats clone() throws CloneNotSupportedException {
        return (EntityStats) super.clone();
    }

    // FIXES JCOMBOBOX DISPLAY ERROR, FIX DUPE LATER
    // BASCALLY WHEN YOU PUT OBJECTS IN A JCOMBOBOX (WHICH IS A COLLECTION) IT DISPLAYS
    // WHATEVER VALUE IS RETURNED WHEN toString() IS CALLED. SO I ADDED IT BELOW TO FIX
    // THAT. WE CAN REMOVE getName AND REPLACE INSTANCES OF IT USED WITH toString TO EASILY
    // FIX THiS DUPE METHOD.
    /**
     * Returns the name of the Entity
     * @return Name of the Entity
     */
    public String toString() {
        return name;
    }

    /**
     * Returns the default values in the form of an EntityStats from when the object was first initialised.
     * @return the default values in the form of an EntityStats from when the object was first initialised.
     */
    public EntityStats getDefaults(){
        return DEFAULT_STATS;
    }

    /**
     * Returns the name of the Entity
     * @return Name of the Entity
     */
    public String getName() {
        return name;
    }
    public EntityType getEntityType(){
        return entityType;
    }
    public Color getColor() {
        return color;
    }
    public double getBreedingProbability(){
        return breedingProbability;
    }
    public double getCreationProbability(){
        return creationProbability;
    }
    public boolean isEnabled(){
        return isEnabled;
    }

    public void toggleEnabled() {
        isEnabled = !isEnabled;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    public void setCreationProbability(double creationProbability) {
        this.creationProbability = creationProbability;
    }
    public void setBreedingProbability(double breedingProbability) {
        this.breedingProbability = breedingProbability;
    }

    public void resetToDefault() {
        EntityStats defaults = null;
        try {
            defaults = getDefaults().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        //IDK IF THESE ARE ALLOWED OR I SHOULD MAKE THEM METHODS
        this.name = defaults.name;
        this.entityType = defaults.entityType;
        this.color = defaults.color;
        this.breedingProbability = defaults.breedingProbability;
        this.creationProbability = defaults.creationProbability;
        this.isEnabled = defaults.isEnabled;
    }
}
