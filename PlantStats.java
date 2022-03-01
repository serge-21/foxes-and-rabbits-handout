import java.awt.*;

/**
 * A class representing shared statistics of all plants.
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class PlantStats extends EntityStats {
    private int foodValue;                      // The food value that the plant will give if eaten
    private int maxLevel;                       // The max level a plant can reach

    public static final int FOODVALUE_MAX = 30;
    public static final int MAXLEVEL_MAX = 10;

    private final PlantStats DEFAULT_STATS;     // The default statistics of the plant

    /**
     * Created a container for the plant's statistics.
     * @param name The name of the plant.
     * @param entityType The type of the plant. Should be declared as an EntityType Enum
     * @param color The colour of the plant.
     * @param creationProbability The probability that the plant is places on the field.
     * @param breedingProbability The probability that the plant will breed.
     * @param foodValue The food value that the plant provides after it gets eaten
     * @param maxLevel The max level a plant can reach
     */
    public PlantStats(String name, EntityType entityType, Color color, double creationProbability, double breedingProbability, int foodValue, int maxLevel){
        super(name, entityType, color, creationProbability, breedingProbability);
        this.foodValue = Math.min(foodValue, FOODVALUE_MAX);
        this.maxLevel = Math.min(maxLevel, MAXLEVEL_MAX);

        DEFAULT_STATS = new PlantStats(this);
        }

    /**
     * Clones a pre-existing PlantStats.
     * @param clone The PlantStats you wish to clone.
     */
    protected PlantStats(PlantStats clone) {
        super(clone);
        this.foodValue = clone.foodValue;
        this.maxLevel = clone.maxLevel;

        DEFAULT_STATS = this;
    }

    /**
     * Creates an AnimalStats with all values set to 0 and type to PREY.
     */
    public PlantStats() {
        super();
        this.foodValue = 1;
        this.maxLevel = 1;

        DEFAULT_STATS = new PlantStats(this);
    }

    /**
     * A simple getter method to get the stats of the plant
     *
     * @return the default stats of the plant
     */
    @Override
    public PlantStats getDefaults(){
        return DEFAULT_STATS;
    }

    public int getFoodValue() {
        return foodValue;
    }
    public int getMaxLevel() {
        return maxLevel;
    }

    public void setFoodValue(int foodValue) {
        this.foodValue = foodValue;
    }
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    @Override
    public void resetToDefault() {
        super.resetToDefault();
        PlantStats defaults = null;
        try {
            defaults = (PlantStats)this.getDefaults().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.foodValue = defaults.foodValue;
        this.maxLevel = defaults.maxLevel;
    }
}
