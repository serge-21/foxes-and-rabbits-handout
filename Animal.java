import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Organism{

    // all the field shared by all animals
    private static final Random rand = Randomizer.getRandom();          // shared by all animals
    private final boolean isMale = rand.nextDouble() < 0.5;             // random chance that each animal might be a male or female
    private final boolean isNocturnal;                                  // passed in
    private boolean isPregnant;                                         // is pregnant is for both the recovery period AND pregnancy
    private int breedCounter;
    private int foodLevel;                                              // passed in
    private ArrayList<Class> prey;
    private int waterLevel = 500;

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
    public Animal(AnimalStats stats, boolean randomAge, Field field,  Location initLocation, boolean nocturnal, int foodVal, int age) {
        super(stats, field, initLocation);
        this.isNocturnal = nocturnal;
        this.prey = new ArrayList<>();
        this.breedCounter = 10;

        if(randomAge){
            setAge(getRand().nextInt(age));
            setFoodLevel(getRand().nextInt(foodVal));
        } else {
            setAge(0);
            setFoodLevel(foodVal);
        }
    }

//    public Animal(){
//        this.isNocturnal = false;
//    }

    protected int getBreedCounter(){
        return this.breedCounter;
    }

    protected void setBreedCounter(int pregnancy){
        this.breedCounter = pregnancy;
    }

    protected void addPrey(Class prey){
        this.prey.add(prey);
    }

    protected ArrayList<Class> getPrey(){
        return this.prey;
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
     * Make the animal more thirsty. This could result in the animal's death.
     */
    protected void incrementThirst() {
        waterLevel -= 1;
        if(waterLevel <= 0) {
            setDead();
        }
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

    protected void setPregnant(boolean pregnant){
        this.isPregnant = pregnant;
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

    abstract protected int getBreedingAge();

    private void setWaterLevel(double waterLevel){
        this.waterLevel += waterLevel;
    }

    /**
     * find the location of the food.
     * @return the location of the prey note this could be a plant
     */
    protected Location findFood(ArrayList<Class> animalsToEat, Weather currentWeather) {
        // first we allow the animal to drink
        setWaterLevel(currentWeather.getActualDownfall());

        // then we try to find food.
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Object organism = field.getObjectAt(where);
            for(Class animal : animalsToEat){
                if(animal.isInstance(organism)){
                    if(organism instanceof Plant1){
                        ((Plant1) organism).setDead();
                        setFoodLevel(Plant1.foodValue);
                    }else {
                        Animal dinner = (Animal) organism;
                        dinner.setDead();
                        setFoodLevel(dinner.getFoodLevel());
                    }
                    return where;
                }
            }
        }
        return null;
    }

    private boolean getIsPregnant(){
        return isPregnant;
    }

    protected void moveLocationOfAnimal(Weather currentWeather){
        // Move towards a source of food if found.
        Location newLocation = findFood(getPrey(), currentWeather);
        if(newLocation == null) {
            // No food found - try to move to a free location.
            newLocation = getField().freeAdjacentLocation(getLocation());
        }
        // See if it was possible to move.
        if(newLocation != null) {
            setLocation(newLocation);
        } else {
            // Overcrowding.
            setDead();
        }
    }

    protected void updateStatsOfAnimal(){
        incrementThirst();
        incrementHunger();
        setBreedCounter(breedCounter-1);
    }

    /**
     * finds if it is possible to breed at a given location.
     * @return a boolean value of weather there is a mate to breed with
     */
    protected boolean findMate(Class matingPartner){
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Object animal = field.getObjectAt(where);
            if(matingPartner.isInstance(animal)){
                Animal potentialMate = (Animal) animal;
                if(potentialMate.getAge() >= potentialMate.getBreedingAge() && potentialMate.getIsMale() != this.getIsMale()){
                    if(!potentialMate.getIsMale() && !potentialMate.getIsPregnant()){
                        potentialMate.setPregnant(true);
                        potentialMate.setBreedCounter(10);
                        return true;
                    }else if (!this.getIsPregnant()){
                        this.setPregnant(true);
                        this.setBreedCounter(10);
                        return true;
                    }
                }
//                return potentialMate.getIsMale() != this.getIsMale() && potentialMate.getAge() >= potentialMate.getBreedingAge();
            }
        }
        return false;
    }
}
