import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Organism{

    // all the field shared by all animals
    private static final Random rand = Randomizer.getRandom();          // shared by all animals
    private final boolean isMale = rand.nextDouble() < 0.5;     // random chance that each animal might be a male or female
    private final boolean isNocturnal;                                  // passed in
    private int foodLevel;                                              // passed in
    private boolean isInfected;                                         // to be passed in

    /**
     * Create a new animal at location in field.
     *
     * @param randomAge to give more variance if we have a random age we set the food-level and age to random values
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     * @param nocturnal a boolean value of weather the animal is nocturnal
     * @param foodVal the food value of the animal when born
     * @param age the age of the animal when born
     */
    public Animal(boolean randomAge, Field field,  Location initLocation, boolean nocturnal, int foodVal, int age) {
        super(field, initLocation);
        this.isNocturnal = nocturnal;
        this.isInfected = false;

        if(randomAge){
            setAge(getRand().nextInt(age));
            setFoodLevel(getRand().nextInt(foodVal));
        } else {
            setAge(0);
            setFoodLevel(foodVal);
        }
    }

    /**
     * a getter method to get the isMale field.
     * @return a boolean value of weather the current Animal is male
     */
    public boolean getIsMale(){
        return isMale;
    }

    /**
     * a getter method to get the foodLevel field.
     * food level of 0 means the animal will die of hunger
     * @return an int value of how hungry an animal is.
     */
    public int getFoodLevel(){
        return foodLevel;
    }

    /**
     * a getter method to get the isNocturnal field.
     * @return a boolean value of when the animal is active at night or not.
     */
    protected boolean getNocturnal(){
        return isNocturnal;
    }

    /**
     * Make the animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger() {
        foodLevel -= 1;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * set the food level of the animal
     */
    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }



    /**
     * check if it is day or night
     */
    protected boolean determineDay(boolean isDay){
        return ((isDay && !getNocturnal()) || (!isDay && getNocturnal()));
    }

    /**
     * find the location of the food.
     * @return the location of the prey note this could be a plant
     */
    abstract protected Location findFood();

    /**
     * finds if it is possible to breed at a given location.
     * @return a boolean value of weather there is a mate to breed with
     */
    abstract protected boolean findMate();

}
