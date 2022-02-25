import java.awt.*;

/**
 * A container for all the unique values for an animal. It also makes a backup of the values if the originals want to be reassigned if they're changed
 *
 * @author -
 * @version -
 */
public class AnimalStats extends EntityStats {
    // Characteristics shared by all predator1s (class variables).
    private int breedingAge;                 // The age at which a predator1 can start to breed.
    private int maxAge;                     // The age to which a predator1 can live.
    //private double breedingProbability;    // The likelihood of a predator1 breeding.
    private int maxLitterSize;               // The maximum number of births.
    private int hungerValue;             // The food value of a single prey1. In effect, this is the
    // number of steps a predator1 can go before it has to eat again.

    private final AnimalStats DEFAULT_STATS;

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
    protected AnimalStats(AnimalStats clone){
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
    public AnimalStats(){
        super();
        this.breedingAge = 0;
        this.maxAge = 0;
        this.maxLitterSize = 0;
        this.hungerValue = 0;

        DEFAULT_STATS = new AnimalStats(this);
    }

    @Override
    public AnimalStats getDefaults(){
        return DEFAULT_STATS;
    }

    public int getBreedingAge(){
        return breedingAge;
    }
    public int getMaxAge(){
        return maxAge;
    }
    public int getMaxLitterSize(){
        return maxLitterSize;
    }
    public int getHungerValue(){
        return hungerValue;
    }

    public void setBreedingAge(Integer breedingAge) {
        this.breedingAge = breedingAge;
    }
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }
    public void setMaxLitterSize(int maxLitterSize) {
        this.maxLitterSize = maxLitterSize;
    }
    public void setHungerValue(int hungerValue) {
        this.hungerValue = hungerValue;
    }

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
}
