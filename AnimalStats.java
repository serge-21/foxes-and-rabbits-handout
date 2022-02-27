import java.awt.*;

/**
 * A container for all the unique values for an animal. It also makes a backup of the values if the originals want to be reassigned if they're changed
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class AnimalStats extends EntityStats {
    // Characteristics shared by all Animals
    private int breedingAge;                 // The age at which a predator1 can start to breed.
    private int maxAge;                      // The age to which a predator1 can live.
    private int maxLitterSize;               // The maximum number of births.
    private int hungerValue;                 // The food value of a single prey1. In effect, this is the

    private final AnimalStats DEFAULT_STATS; // The default stats of the animal

    /**
     * Created a container for the animal's statistics.
     * @param name The name of the animal.
     * @param entityType The type of the animal. Should be declared as an EntityType Enum
     * @param color The colour of the animal.
     * @param breedingProbability The probability that the animal will breed.
     * @param creationProbability The probability that the animal is places on the field.
     * @param breedingAge The Age at which the animal can start breeding.
     * @param maxAge The max age of the animal before it dies.
     * @param maxLitterSize The max amount of offspring the animal can produce.
     * @param hungerValue The number of steps before the animal need to eat again.
     */
    public AnimalStats(String name, EntityType entityType, Color color, double breedingProbability, double creationProbability, int breedingAge, int maxAge, int maxLitterSize, int hungerValue){
        super(name, entityType, color, breedingProbability, creationProbability);
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.maxLitterSize = maxLitterSize;
        this.hungerValue = hungerValue;

        DEFAULT_STATS = new AnimalStats(this);
    }

    /**
     * Clones a pre-existing AnimalStats.
     * @param clone The AnimalStats you wish to clone.
     */
    protected AnimalStats(AnimalStats clone) {
        super(clone);
        this.breedingAge = clone.breedingAge;
        this.maxAge = clone.maxAge;
        this.maxLitterSize = clone.maxLitterSize;
        this.hungerValue = clone.hungerValue;

        DEFAULT_STATS = this;
    }

    /**
     * Creates an AnimalStats with all values set to 0 and type to PREY.
     */
    public AnimalStats() {
        super();
        this.breedingAge = 0;
        this.maxAge = 0;
        this.maxLitterSize = 0;
        this.hungerValue = 0;

        DEFAULT_STATS = new AnimalStats(this);
    }

    /**
     * A simple getter method to get the default stats of the animal
     *
     * @return the default stats field
     */
    @Override
    public AnimalStats getDefaults() {
        return DEFAULT_STATS;
    }

    /**
     * resets the stats to the default
     */
    @Override
    public void resetToDefault() {
        super.resetToDefault();
        AnimalStats defaults = null;
        try {
            defaults = (AnimalStats)this.getDefaults().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.breedingAge = defaults.breedingAge;
        this.maxAge = defaults.maxAge;
        this.maxLitterSize = defaults.maxLitterSize;
        this.hungerValue = defaults.hungerValue;;
    }

    // a list of all the getter and setter methods

    /**
     * A simple getter method to return the breeding age
     *
     * @return the breedingAge field
     */
    public int getBreedingAge() {
        return breedingAge;
    }

    /**
     * A simple getter method to return the max age
     *
     * @return the maxAge field
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * A simple getter method to return the max litter for the animal
     *
     * @return the maxLitterSize field
     */
    public int getMaxLitterSize() {
        return maxLitterSize;
    }

    /**
     * A simple getter method to return the hunger value of the animal
     *
     * @return the hungerValue field
     */
    public int getHungerValue(){
        return hungerValue;
    }

    /**
     * A simple setter method to set the breeding age
     *
     * @param breedingAge the age we want the breeding to occur
     */
    public void setBreedingAge(Integer breedingAge) {
        this.breedingAge = breedingAge;
    }

    /**
     * A simple setter method to set the max age (Age at which the animal dies)
     *
     * @param maxAge the max age we want the animal to live for
     */
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * A simple setter method to set the max litter size (max number of offspring per animal)
     *
     * @param maxLitterSize the max offspring per type of animal
     */
    public void setMaxLitterSize(int maxLitterSize) {
        this.maxLitterSize = maxLitterSize;
    }

    /**
     * A simple setter method to set the hunger value this is used before/after eating
     * or if the animal doesn't eat at all
     *
     * @param hungerValue the food value of the animal consumed
     */
    public void setHungerValue(int hungerValue) {
        this.hungerValue = hungerValue;
    }
}
