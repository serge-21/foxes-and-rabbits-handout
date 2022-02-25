import java.awt.*;

public class PlantStats extends EntityStats {
    private int foodValue;
    private int maxLevel;

    private final PlantStats DEFAULT_STATS;

    /**
     * Created a container for the plant's statistics.
     * @param name The name of the plant.
     * @param entityType The type of the plant. Should be declared as an EntityType Enum
     * @param color The colour of the plant.
     * @param breedingProbability The probability that the plant will breed.
     * @param creationProbability The probability that the plant is places on the field.
     * @param foodValue
     * @param maxLevel
     */
    public PlantStats(String name, EntityType entityType, Color color, double breedingProbability, double creationProbability, int foodValue, int maxLevel){
        super(name, entityType, color, breedingProbability, creationProbability);
        this.foodValue = foodValue;
        this.maxLevel = maxLevel;

        DEFAULT_STATS = new PlantStats(this);
        }

    /**
     * Clones a pre-existing PlantStats.
     * @param clone The PlantStats you wish to clone.
     */
    protected PlantStats(PlantStats clone){
        super(clone);
        this.foodValue = clone.foodValue;
        this.maxLevel = clone.maxLevel;

        DEFAULT_STATS = this;
    }

    /**
     * Creates an AnimalStats with all values set to 0 and type to PREY.
     */
    public PlantStats(){
        super();
        this.foodValue = 0;
        this.maxLevel = 0;

        DEFAULT_STATS = new PlantStats(this);
    }

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
