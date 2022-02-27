import java.awt.*;

/**
 * A container for all the unique values for an animal. It also makes a backup of the values if the originals want to be reassigned if they're changed
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class EntityStats implements Cloneable{

    /**
     * this is the main enum that distinguishes the type of animals
     */
    public enum EntityType{
        PREY, PREDATOR, PLANT
    }

    private String name;                            // The name of the entity
    private EntityType entityType;                  // The type of the entity
    private Color color;                            // The colour of the entity
    private boolean isEnabled;                      // The what exactly is this ?
    private double breedingProbability;             // The breeding probability i.e. how likely it is that this entity will breed
    private double creationProbability;             // The creation probability i.e. how likely it is that this entity will get created in the first place

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
        this.isEnabled = true;                              // Enabled by default
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
        this.color = null;
        this.breedingProbability = 0.0;
        this.creationProbability = 0.0;
        this.isEnabled = true; // Enabled by default

        DEFAULT_STATS = new EntityStats(this);
    }

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
    public EntityStats getDefaults() {
        return DEFAULT_STATS;
    }

    /**
     * Returns the name of the Entity
     * @return Name of the Entity
     */
    public String getName() {
        return name;
    }

    /**
     * A simple getter method to return the entityType field
     *
     * @return the entity type for example animal/plant/ etc...
     */
    public EntityType getEntityType() {
        return entityType;
    }

    /**
     * A simple getter method to return the colour of the entity
     *
     * @return the colour of the entity
     */
    public Color getColor() {
        return color;
    }

    /**
     * A simple getter method to return the breedingProbability field
     *
     * @return the breeding probability as a double
     */
    public double getBreedingProbability() {
        return breedingProbability;
    }

    /**
     * A simple getter method that returns the creation probability field
     *
     * @return the creation probability as a double
     */
    public double getCreationProbability() {
        return creationProbability;
    }

    /**
     * no clue what this does ask cosmo
     *
     * @return
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * A simple method to toggle the isEnabled field
     */
    public void toggleEnabled() {
        isEnabled = !isEnabled;
    }

    /**
     * A simple setter method to set the name of the entity
     *
     * @param name the name we wish to give the entity
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * A simple setter method to set the colour of the entity
     *
     * @param color the colour we wish to give the entity
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * A simple setter method to set the entity type
     *
     * @param entityType the type we wish to give to the entity
     */
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    /**
     * A simple setter method to set the creation probability of the entity
     *
     * @param creationProbability the probability of creating this entity
     */
    public void setCreationProbability(double creationProbability) {
        this.creationProbability = creationProbability;
    }

    /**
     * A simple setter method to set the breeding probability of the entity
     *
     * @param breedingProbability the probability of this entity breeding
     */
    public void setBreedingProbability(double breedingProbability) {
        this.breedingProbability = breedingProbability;
    }

    /**
     * this method will restore all the default stats of a given entity
     */
    public void resetToDefault() {
        EntityStats defaults = null;
        try {
            defaults = getDefaults().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.name = defaults.name;
        this.entityType = defaults.entityType;
        this.color = defaults.color;
        this.breedingProbability = defaults.breedingProbability;
        this.creationProbability = defaults.creationProbability;
        this.isEnabled = defaults.isEnabled;
    }
}
