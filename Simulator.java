import java.util.*;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class Simulator {
    // Constants representing configuration information for the simulation.
    private static final int DEFAULT_WIDTH = 200;           // The default width for the grid.
    private static final int DEFAULT_DEPTH = 120;           // The default depth of the grid.
    public static final int STEP_PER_DAY = 24;              // The total number of steps in a day

    private static String currentSpeedSymbol;               // The current speed symbol
    private static int currentSpeed;                        // Current speed of simulation
    private final List<Organism> organisms;                 // List of animals in the field.
    private final Field field;                              // The current state of the field.

    private int step;                                       // The current step of the simulation with the time of the day
    private boolean isDay = true;                           // Defining the starting point
    private String daytime = "day";                         // The day string
    private int dayCount = 0;                               // The total number of days NOTE: days are different to steps
    private int time;                                       // The current time

    private final Weather weather;                          // The current weather
    private final SimulatorView view;                       // A graphical view of the simulation.
    private static boolean isRunning;                       // If the simulator is currently running
    private final Randomizer rand = new Randomizer();       // A random object to control behaviour
    private final ArrayList<EntityStats> DEFAULT_ENTITIES;  // List of all the default entities
    private ArrayList<EntityStats> possibleEntities;        // The list of all possible entities

    private static final ArrayList<Integer> speeds = new ArrayList<>(Arrays.asList(100, 50, 25, 800, 400, 200));
    private static final ArrayList<String> speedSymbols = new ArrayList<>(Arrays.asList("3","2","1","6","5","4"));

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        // setting all possible entities
        possibleEntities = new ArrayList<>();
        setAllPossibleEntities();

        // making them the default starting entities
        DEFAULT_ENTITIES = new ArrayList<>();
        for (EntityStats stat : possibleEntities){
            try {
                DEFAULT_ENTITIES.add(stat.clone());
            } catch (CloneNotSupportedException e) {
                //e.printStackTrace();
            }
        }

        currentSpeed = speeds.get(0);
        currentSpeedSymbol = speedSymbols.get(0);

        this.organisms = new ArrayList<>();
        this.field = new Field(depth, width);
        this.weather = new Weather();
        //Create a view of the state of each location in the field.
        this.view = new SimulatorView(depth, width, this, field);

        // Setup a valid starting point.
        reset();
        pickWeather();
    }

    /**
     * setting up all the starting entities, all of these can be edited, removed or added to
     */
    private void setAllPossibleEntities() {
        AnimalStats predator1 = new AnimalStats("Wolf", EntityStats.EntityType.PREDATOR, Color.RED,  1.0,0.37, true, 15, 130, 2, 20);
        possibleEntities.add(predator1);
        AnimalStats predator2 = new AnimalStats("Eagle", EntityStats.EntityType.PREDATOR, Color.BLUE,  1.4,0.28, true,16, 150, 2, 16);
        possibleEntities.add(predator2);
        AnimalStats prey1 = new AnimalStats("Mouse", EntityStats.EntityType.PREY, Color.MAGENTA,  20.0,1.0, false, 1, 200, 59, 200);
        possibleEntities.add(prey1);
        AnimalStats prey2 = new AnimalStats("Deer", EntityStats.EntityType.PREY, Color.ORANGE,  20.0,1.0,false,  1, 200, 58, 200);
        possibleEntities.add(prey2);
        PlantStats plant1 = new PlantStats("Grapes", EntityStats.EntityType.PLANT, Color.GREEN,  1.0,0.01, 14, 5);
        possibleEntities.add(plant1);
    }

    /**
     * Reset the entities to the default entities
     */
    public void resetEntities() {
        possibleEntities = new ArrayList<>();
        for (EntityStats stat : DEFAULT_ENTITIES){
            try {
                possibleEntities.add(stat.clone());
            } catch (CloneNotSupportedException e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * This method is responsible for adding an entity to the list of possible entities
     *
     * @param entity the entity we wish to add to the possible entities
     */
    public void addEntityToPossibilities(EntityStats entity) {
        possibleEntities.add(entity);
    }

    /**
     * This method is responsible for removing an entity from the list of all possible entities
     *
     * @param entity the entity we wish to remove
     */
    public void removeEntity(EntityStats entity) {
        possibleEntities.remove(entity);
    }

    /**
     * A simple getter method to return all the possibleEntities field
     *
     * @return the arraylist of all possible entities
     */
    public ArrayList<EntityStats> getPossibleEntities() {
        return possibleEntities;
    }

    /**
     * This method is responsible for picking the weather
     * and modifying the weather field
     */
    private void pickWeather() {
        this.weather.pickSeason();
        this.weather.generateVisibilityAndDownfall();
    }

    /**
     * This method is responsible for updating the day and time
     * additionally updates the weather each day
     */
    private void checkForDayChange() {
        // from 16 to 5 is night.
        time = step % STEP_PER_DAY;
        if(time < 4 || time > 16){
            // then it's night
            daytime = "night";
            isDay = false;
        }else {
            daytime = "day";
            isDay = true;
        }

        if(time == 0) {
            // do all changes
            dayCount += 1;
            pickWeather();
        }
    }

    /**
     * This method is responsible for providing a string representation of time
     *
     * @return a string representation of the time
     */
    public String updateTime() {
        if(time < 10){
            return  "0" + time + ":00";
        }else{
            return time + ":00";
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep() {
        step++;
        // first calculate if it is day or night
        checkForDayChange();

        // Provide space for newborn animals.
        List<Organism> newOrganisms = new ArrayList<>();
        // Let all animals act.
        for (Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism entity = it.next();
            entity.act(newOrganisms, isDay, this.weather);
            if (!entity.getIsAlive()){
                it.remove();
            }else{
                if(new Random().nextDouble() < 0.3){
                    entity.addDisease(new Disease(0.1, 0.2 , entity));
                }
            }
        }

        // Add the newly created organisms to the list.
        organisms.addAll(newOrganisms);
        showStatus();
    }

    /**
     * removing an entity from the list of organisms
     *
     * @param entityStats the entity we wish to remove
     */
    public void removeFromOrganisms(EntityStats entityStats) {
        organisms.removeIf(entity -> entity.getStats().equals(entityStats));
    }

    /**
     * updating the GUI and showing the current status of the simulation
     */
    public void showStatus() {
        view.showStatus(step, field, daytime, dayCount, updateTime(), this.weather);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        Randomizer.resetRandom();

        step = 0;
        time = 0;
        dayCount = 0;
        organisms.clear();
        populate();
        this.weather.resetWeather();
        pickWeather();
        // Show the starting state in the view.
        showStatus();
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate() {
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                EntityStats newEntity = getRandomEntity(getChanceLimit());
                if (newEntity != null && newEntity.isEnabled()) {
                    Location location = new Location(row, col);
                    addEntityToSimulator(newEntity, true, field, location);
                }
            }
        }
    }

    /**
     * This method is responsible for clearing the screen
     */
    public void clearScreen() {
        field.clear();
        organisms.clear();
    }

    /**
     * This method is responsible for adding a possible entity to the simulation
     *
     * @param entity The entity we wish to add
     * @param randomAge Does this entity start with a random age
     * @param field The field on which we are placing the entity
     * @param location The location of the entity on the field
     */
    public void addEntityToSimulator(EntityStats entity, boolean randomAge, Field field, Location location) {
        if (entity.getEntityType() == AnimalStats.EntityType.PREDATOR){
            Predator predator = new Predator((AnimalStats) entity, randomAge, field, location);
            organisms.add(predator);
        }
        else if (entity.getEntityType() == AnimalStats.EntityType.PREY){
            Prey prey = new Prey((AnimalStats) entity, randomAge, field, location);
            organisms.add(prey);
        }
        else if (entity.getEntityType() == AnimalStats.EntityType.PLANT) {
            Plant plant = new Plant((PlantStats) entity, field, location);
            organisms.add(plant);
        }
    }

    /**
     * This method is responsible for removing an entity from the simulation
     *
     * @param field The field we wish to remove the entity from
     * @param location The location on the field we wish to remove the entity from
     */
    public void removeEntityInSimulator(Field field, Location location) {
        field.clear(location);
    }

    /**
     * If the total spawn rate exceeds 100, this returns to larger value. Prevents some entities from never being spawned if too many at high percentage.
     *
     *  @return If the total probability exceeds 100, then the total, otherwise 100
     */
    private int getChanceLimit() {
        int limit = 1000;
        int total = 0;
        for (EntityStats entity : possibleEntities){
            total += entity.getCreationProbability();
        }
        return Math.max(total, limit);
    }

    /**
     * Returns a random possible entity.
     *
     * @param limit the max of the field of random numbers. The larger, the more likely of empty fields.
     * @return Returns a random possible entity.
     */
    private EntityStats getRandomEntity(int limit) {
        int total = 0;
        int probability = rand.nextInt(limit);
        for (EntityStats entity : possibleEntities){
            total += entity.getCreationProbability()*10;
            if (probability <= total){
                return entity;
            }
        }
        return null;
    }

    /**
     * Pause for a given time.
     *
     * @param millisecond  The time to pause for, in milliseconds
     */
    private void delay(int millisecond) {
        try {
            Thread.sleep(millisecond);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }

    /**
     * toggle the value of isRunning field
     * depending on the value of the field the simulation will be either running or not
     */
    public void toggleRunning(){
        isRunning = !isRunning;
    }

    /**
     * A simple getter method to return the is isRunning field
     *
     * @return the boolean value of the isRunning field
     */
    public boolean isRunning(){
        return isRunning;
    }

    /**
     * A simple getter method to return the speedSymbol field
     *
     * @return the String representation of the currentSpeedSymbol
     */
    public String getSpeedSymbol(){return currentSpeedSymbol;}

    /**
     * This method will increase the speed of the simulation
     * This allows for more variance
     */
    public void incSpeed() {
        int index = speeds.indexOf(currentSpeed) + 1;
        if (!(index < speeds.size())){
            index = 0;
        }
        currentSpeed = speeds.get(index);
        currentSpeedSymbol = speedSymbols.get(index);
    }

    /**
     * The main method
     *
     * @param args could be null
     */
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        simulator.reset();
        isRunning = true;
        while (true){
            // In case the simulation is paused.
            while (!isRunning){
                simulator.delay(200);
            }
            simulator.simulateOneStep();
            simulator.delay(currentSpeed);
        }
    }
}
