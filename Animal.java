import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Organism{
    private final boolean isMale = rand.nextDouble(1) < 0.5;
    private boolean isNocturnal;
    private boolean isInfected;
    private int foodLevel;
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field,  Location initLocation) {
        super(field, initLocation);
        this.isNocturnal = false;
        this.isInfected = false;
    }

    public boolean getIsMale(){
        return isMale;
    }

    public int getFoodLevel(){
        return foodLevel;
    }

    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    protected void incrementHunger() {
        foodLevel -= 1;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract protected Location findFood();

    abstract protected boolean findMate();
}
