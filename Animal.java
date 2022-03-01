import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public abstract class Animal extends Organism{
    // all the field shared by all animals
    private static final Random rand = Randomizer.getRandom();          // a random chance of things happening shared by all animals
    private final boolean isMale = rand.nextDouble() < 0.5;             // random chance that each animal might be a male or female
    //private final boolean isNocturnal;                                  // is the animal nocturnal
    private boolean isPregnant;                                         // is pregnant is for both the recovery period AND pregnancy
    private int breedCounter;                                           // recovery from being pregnant
    private int foodLevel;                                              // how hungry is the animal if food level is 0 animal dies
    private final ArrayList<Class> prey;                                // arraylist of all animals that can be eaten by this animal
    private int waterLevel = 500;                                       // how thirsty is the animal if water level is 0 animal dies

    /**
     * Create a new animal at location in field.
     *
     * @param stats the statistics of this animal this ranges from the hunger level to breeding age etc...
     * @param randomAge to give more variance if we have a random age we set the food-level and age to random values
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     //* @param nocturnal a boolean value of weather the animal is nocturnal
     * @param foodVal the food value of the animal when born
     * @param age the age of the animal when born
     */
    public Animal(AnimalStats stats, boolean randomAge, Field field,  Location initLocation, boolean nocturnal, int foodVal, int age) {
        super(stats, field, initLocation);
        //this.isNocturnal = nocturnal;
        this.prey = new ArrayList<>();
        this.breedCounter = 10;

        // give the animal a random age + food level if we want to
        if(randomAge){
            setAgeInSteps(getRand().nextInt(age));
            setFoodLevel(getRand().nextInt(foodVal));
        } else {
            setAgeInSteps(0);
            setFoodLevel(foodVal);
        }
    }

    /**
     * This method is responsible for making a J-panel that will hold all the statistics
     * of the animal this ranges from the sex of the animal to the food level or if the animal is pregnant
     * all the stats will be displayed on the gui IF the user hovers over the animal
     *
     * @return the statistics of the animal in gui form
     */
    public JPanel getInspectPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new LineBorder(getStats().getColor(), 4));

        ArrayList<String> labelItems = new ArrayList<>(Arrays.asList(
                "Age:",this.getAgeInDays() + "/" + ((AnimalStats)this.getStats()).getMaxAge(),
                "Pregnant:", (isPregnant + "").toUpperCase(),
                "Food Level:", foodLevel + "",
                "Water Level:", waterLevel + ""));

        JLabel sexSymbol = new JLabel("   F   ");
        sexSymbol.setOpaque(true);
        sexSymbol.setBackground(Color.PINK);
        if (this.isMale){
            sexSymbol.setText("   M   ");
            sexSymbol.setBackground(Color.cyan);
            labelItems.remove(2);
            labelItems.remove(2);
        }

        // positioning the information
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        panel.add(new JLabel(this.getStats().getName()), gbc);

        gbc.gridx = 1;
        panel.add(sexSymbol, gbc);

        gbc.gridy++;
        for (int index = 0; index < labelItems.size(); index += 2){
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0;
            panel.add(new JLabel(labelItems.get(index)), gbc);

            gbc.anchor = GridBagConstraints.CENTER;
            gbc.gridx = 1;
            panel.add(new JLabel(labelItems.get(index+1)), gbc);

            gbc.gridy++;
        }

        return panel;
    }

    /**
     * The breed counter is responsible for how much longer is left before
     * the animal is able to breed again all (measurements are done in steps).
     *
     * @return the number of steps before the animal can breed again
     */
    protected int getBreedCounter() {
        return this.breedCounter;
    }

    /**
     * This method is responsible for setting the length of the pregnancy per animal.
     *
     * @param pregnancy this is how long the pregnancy is going to last in steps
     */
    protected void setBreedCounter(int pregnancy) {
        this.breedCounter = pregnancy;
    }

    /**
     * Add the class to the arraylist so that this animal may feed on the animals of the specified class.
     *
     * @param prey a class of the animals that can be eaten by the current animal
     */
    protected void addPrey(Class prey) {
        this.prey.add(prey);
    }

    /**
     * A simple getter method to get the prey field.
     *
     * @return the arraylist of animals that this animal may eat
     */
    protected ArrayList<Class> getPrey() {
        return this.prey;
    }
    
    /**
     * A getter method to get the isMale field.
     *
     * @return a boolean value of weather the current Animal is male
     */
    public boolean getIsMale() {
        return isMale;
    }

    /**
     * A getter method to get the foodLevel field.
     * food level of 0 means the animal will die of hunger
     * @return an int value of how hungry an animal is.
     */
    public int getFoodLevel() {
        return foodLevel;
    }

    /**
     * A getter method to get the waterLevel field.
     * water level of 0 means the animal will die of thirst
     *
     * @return an int value of how thirsty an animal is.
     */
    public int getWaterLevel() {
        return waterLevel;
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

//    /**
//     * a getter method to get the isNocturnal field.
//     *
//     * @return a boolean value of when the animal is active at night or not.
//     */
//    protected boolean getNocturnal(){
//        return isNocturnal;
//    }

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
     * Adjust if the animal is pregnant or not
     *
     * @param pregnant boolean value, 1 means pregnant and 0 means not pregnant
     */
    protected void setPregnant(boolean pregnant) {
        this.isPregnant = pregnant;
    }

    /**
     * A simple getter method to get the field isPregnant
     *
     * @return if the animal is pregnant
     */
    private boolean getIsPregnant() {
        return isPregnant;
    }

    /**
     * set the food level of the animal
     */
    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    /**
     * check if it is day or night note
     *
     * @param isDay boolean value of the day, 0 means night, 1 means day
     * @return true if the animal can move during this time
     */
    protected boolean determineDay(boolean isDay) {
        return ((isDay && !((AnimalStats)getStats()).isNocturnal()) || (!isDay && ((AnimalStats)getStats()).isNocturnal()));
    }

    /**
     * Adjust the water value of the waterLevel field
     *
     * @param waterLevel a double value of the water the animal is drinking
     */
    private void setWaterLevel(double waterLevel) {
        this.waterLevel += waterLevel;
    }

    /**
     * find the location of the food.
     *
     * @return the location of the prey note this could be a plant
     * @param animalsToEat a list of the animals to be eaten
     * @param currentWeather the current weather
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
                    if(organism instanceof Plant){
                        ((Plant) organism).setDead();
                        setFoodLevel(((PlantStats)(((Plant) organism).getStats())).getFoodValue());
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

    /**
     * Allow the animal to move locations
     *
     * @param currentWeather the current weather
     */
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

    /**
     * Update the general statistics of the animal
     * this includes: the hunger, thirst, and the pregnancy
     */
    protected void updateStatsOfAnimal(){
        incrementThirst();
        incrementHunger();
        setBreedCounter(breedCounter-1);
    }

    /**
     * finds if it is possible to breed at a given location.
     *
     * @param matingPartner the class of animals
     * @return a boolean value of weather there is a mate to breed with
     */
    protected boolean findMate(Entity matingPartner){
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Entity animal = (Entity) field.getObjectAt(where);
            if(animal != null && matingPartner.getStats().getName().equals(animal.getStats().getName())){
                Animal potentialMate = (Animal) animal;
                if(potentialMate.getAgeInDays() >= potentialMate.getBreedingAge() && potentialMate.getIsMale() != this.getIsMale()){
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
            }
        }
        return false;
    }

    /**
     * A simple getter method to get the breeding age. The age at which this animal is allowed to breed
     *
     * @return the breeding age
     */
    abstract protected int getBreedingAge();
}
