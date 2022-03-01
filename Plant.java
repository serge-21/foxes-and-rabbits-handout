import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class representing shared characteristics of all plants.
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class Plant extends Organism{
    // characteristics all plants share
    private int currentLevel;               // The current level of the plant
    private int waterLevel;                 // The water level of the plant (plants will die if they don't get enough water)
    private int sunLightLevel;              // The sunlight level of the plant (plants will die if they don't get enough sunlight)
    private final PlantStats plantStats;    // The statistics of the plant ranges from max level to food level

    /**
     * Create a plant. A plant can be created as with a field and
     * a location
     * @param stats the statistics of the plant
     * @param field The field currently occupied.
     * @param initLocation The location within the field.
     */
    public Plant(PlantStats stats, Field field,  Location initLocation) {
        super(stats, field, initLocation);
        this.plantStats = stats;
        this.currentLevel = 1;
        this.waterLevel = 1;
        this.sunLightLevel = 1;
    }

    /**
     * This method is responsible for making a J-panel that will hold all the statistics
     * of the plant this ranges from the water level of the plant to the sunlight level
     * all the stats will be displayed on the gui IF the user hovers over the plant
     *
     * @return the statistics of the animal in gui form
     */
    public JPanel getInspectPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new LineBorder(getStats().getColor(), 4));

        ArrayList<String> labelItems = new ArrayList<>(Arrays.asList(
                "Level:",this.getCurrentLevel() + "/" + ((PlantStats)this.getStats()).getMaxLevel(),
                "Water Level:", waterLevel + "",
                "Sunlight Level:", sunLightLevel + ""));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        panel.add(new JLabel(this.getStats().getName()), gbc);

        gbc.gridwidth = 1;
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
     * This is what the plant does most of the time: 'breed', photosynthesise, and grow.
     *
     * @param newPlants A list to return newly born plants.
     * @param isDay is it currently day or night ?
     * @param currentWeather the current weather
     */
    @Override
    public void act(List<Organism> newPlants, boolean isDay, Weather currentWeather) {
        if(isDay){
            grow(currentWeather);
            if(this.currentLevel > 2){
                giveBirth(newPlants);
            }
        }
    }

    /**
     * Allow the plants to grow according to the current weather
     * plants may die in this process
     *
     * @param currentWeather the current weather
     */
    private void grow(Weather currentWeather) {
        photosynthesis(currentWeather);
        transpiration(currentWeather);
        // allow growth
        if(this.sunLightLevel > 10 && this.waterLevel > 12){
            // then increment the level
            if(this.currentLevel < ((PlantStats)this.getStats()).getMaxLevel()){
                this.currentLevel += 1;
                // we should also reduce the resources of plants IFF they grow
                this.waterLevel = 3;
                this.sunLightLevel = 2;
            }
        }
    }

    /**
     * This method is responsible for the plants getting the water they need.
     * plants get water from rain
     *
     * @param currentWeather the current weather
     */
    private void transpiration(Weather currentWeather){
        // neater way of writing if else blocks ew
        this.waterLevel = (currentWeather.getActualDownfall() < 10) ? this.waterLevel - 1 : this.waterLevel + 1;
        if(this.waterLevel <= 0){
            setDead();
        }
    }

    /**
     * This method is responsible for the plants getting the sunlight they need
     * plants get the sunlight they need depending on the visibility in the current weather
     *
     * @param currentWeather the current weather
     */
    private void photosynthesis(Weather currentWeather){
        // neater way of writing if else blocks ew
        this.sunLightLevel = (currentWeather.getActualVisibility() < 10) ? this.sunLightLevel - 1 : this.sunLightLevel + 1;
        if(this.sunLightLevel <= 0){
            setDead();
        }
    }

    /**
     * set the plant to dead if the level is not 0 then reduce the level by 1
     * plants don't die as normal animals instead if the level of the plant is 0 then we allow the plants to die normally
     */
    @Override
    protected void setDead() {
        if(this.currentLevel <= 0){
            super.setDead();
        }else{
            currentLevel -= 1;
            setLocation(getLocation());
        }
    }

    /**
     * Check whether this plant is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newPlants A list to return newly born plants.
     */
    private void giveBirth(List<Organism> newPlants) {
        // New plants are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant young = new Plant(plantStats, field, loc);
            newPlants.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     *
     * @return The number of births (maybe zero).
     */
    private int breed() {
        int births = 0;
        if(getRand().nextDouble() <= this.getStats().getBreedingProbability()) {
            births = getRand().nextInt(4) + 1;
        }
        return births;
    }

    /**
     * A simple getter method that returns the currentLevel field
     *
     * @return the current level of the plant
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * A simple getter method that returns the plantStats field
     *
     * @return the stats of the plant
     */
    public EntityStats getStats() {
        return plantStats;
    }
}
