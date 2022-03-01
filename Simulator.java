import java.util.*;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 200;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 120;

    private static final ArrayList<Integer> speeds = new ArrayList<Integer>(Arrays.asList(100, 200, 400, 800));
    private static int currentSpeed;
    private static final ArrayList<String> speedSymbols = new ArrayList<String>(Arrays.asList("1", "2", "4", "8"));
    private static String currentSpeedSymbol;

    public static final int STEP_PER_DAY = 24;

    // List of animals in the field.
    private final List<Organism> organisms;
    // The current state of the field.
    private final Field field;
    // The current step of the simulation with the time of the day
    private int step;
    private boolean isDay = true;
    private String daytime = "day";
    private int daycount = 0;
    private int time;

    // the current weather
    private Weather weather;
    // A graphical view of the simulation.
    private final SimulatorView view;

    // if the simulator is currently running
    private static boolean isRunning;

    private Randomizer rand = new Randomizer();

    private final ArrayList<EntityStats> DEFAULT_ENTITIES;
    private ArrayList<EntityStats> possibleEntities;



    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        possibleEntities = new ArrayList<>();
        AnimalStats predator1 = new AnimalStats("Wolf", EntityStats.EntityType.PREDATOR, Color.RED,  1.0,0.37, true, 15, 130, 2, 20);
        possibleEntities.add(predator1);
        AnimalStats predator2 = new AnimalStats("Eagle", EntityStats.EntityType.PREDATOR, Color.BLUE,  4.0,0.28, false,16, 150, 2, 16);
        possibleEntities.add(predator2);
        AnimalStats prey1 = new AnimalStats("Mouse", EntityStats.EntityType.PREY, Color.MAGENTA,  3.0,0.07, true, 5, 50, 3, 40);
        possibleEntities.add(prey1);
        AnimalStats prey2 = new AnimalStats("Deer", EntityStats.EntityType.PREY, Color.ORANGE,  1.0,0.12,false,  6, 40, 4, 40);
        possibleEntities.add(prey2);
        PlantStats plant1 = new PlantStats("Grapes", EntityStats.EntityType.PLANT, Color.GREEN,  1.0,0.01, 14, 5);
        possibleEntities.add(plant1);                                                // PLANT BREEDING MEANT TO BE 0.001

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

        organisms = new ArrayList<>();
        field = new Field(depth, width);
        weather = new Weather();
        //Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, this, field);

        // Setup a valid starting point.
        reset();
        pickWeather();
    }


    public void resetEntities(){
        possibleEntities = new ArrayList<>();
        for (EntityStats stat : DEFAULT_ENTITIES){
            try {
                possibleEntities.add(stat.clone());
            } catch (CloneNotSupportedException e) {
                //e.printStackTrace();
            }
        }
    }

    public void addEntityToPossibilities(EntityStats entity){
        possibleEntities.add(entity);
    }

    public void removeEntity(EntityStats entity){
        possibleEntities.remove(entity);
    }

    public ArrayList<EntityStats> getPossibleEntities(){
        return possibleEntities;
    }

    private void pickWeather(){
        this.weather.pickSeason();
        this.weather.generateVisibilityAndDownfall();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            // delay(60);   // uncomment this to run more slowly
        }
    }

    private void checkForDayChange(){
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
            daycount += 1;
            pickWeather();
        }
    }

    public String updateTime(){
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
    public void simulateOneStep()
    {
        step++;
        // first calculate if it is day or night
        checkForDayChange();

        // Provide space for newborn animals.
        List<Organism> newOrganisms = new ArrayList<>();
        // Let all animals act.
        for (Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism entity = it.next();
            entity.act(newOrganisms, isDay, this.weather);
            if (!entity.getIsAlive()){ // || !possibleEntities.contains(entity.getStats())) {
                it.remove();
            }
        }

        // Add the newly created organisms to the list.
        organisms.addAll(newOrganisms);
        showStatus();
    }

    public void removeFromOrganisms(EntityStats entityStats){
        // || !possibleEntities.contains(entity.getStats())) {
        organisms.removeIf(entity -> entity.getStats().equals(entityStats));
    }

    public void showStatus(){
        view.showStatus(step, field, daytime, daycount, updateTime(), this.weather);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        Randomizer.resetRandom();

        step = 0;
        time = 0;
        daycount = 0;
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
    private void populate()
    {
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

    public void clearScreen(){
        field.clear();
        organisms.clear();
    }

    public void addEntityToSimulator(EntityStats entity, boolean randomAge, Field field, Location location){
        if (entity.getEntityType() == AnimalStats.EntityType.PREDATOR){
            Predator predator = new Predator((AnimalStats) entity, true, field, location);
            organisms.add(predator);
        }
        else if (entity.getEntityType() == AnimalStats.EntityType.PREY){
            Prey prey = new Prey((AnimalStats) entity, true, field, location);
            organisms.add(prey);
        }
        else if (entity.getEntityType() == AnimalStats.EntityType.PLANT) {
            Plant plant = new Plant((PlantStats) entity, field, location);
            organisms.add(plant);
        }
    }

    public void removeEntityInSimulator(Field field, Location location){
        field.clear(location);
    }

    /**
     * If the total spawnrate exeedes 100, this returns to larger value. Prevents some entities from never being spawned if too many at high percentage.
     * @return If the total probability exceeds 100, then the total, otherwise 100
     */
    private int getChanceLimit(){
        int limit = 1000;
        int total = 0;
        for (EntityStats entity : possibleEntities){
            total += entity.getCreationProbability();
        }
        if (total > limit){
            return total;
        }
        else {
            return limit;
        }
    }

    /**
     * Returns a random possible entitiy.
     * @param limit the max of the field of random numbers. The larger the more likely of empty fields.
     * @return Returns a random possible entity.
     */
    private EntityStats getRandomEntity(int limit){
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
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }

    public void toggleRunning(){
        isRunning = !isRunning;
    }

    public boolean isRunning(){
        return isRunning;
    }

    public String getSpeedSymbol(){return currentSpeedSymbol;}

    public void incSpeed(){
        int index = speeds.indexOf(currentSpeed) + 1;
        if (!(index < speeds.size())){
            index = 0;
        }
        currentSpeed = speeds.get(index);
        currentSpeedSymbol = speedSymbols.get(index);
    }

    public static void decSpeed(){
        int index = speeds.indexOf(currentSpeed) - 1;
        if (!(index < 0)){
            index = speeds.get(speeds.size() - 1);
        }
        currentSpeed = speeds.get(index);
        currentSpeedSymbol = speedSymbols.get(index);
    }

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        simulator.reset();
        isRunning = true;
        //for(int i = 0; i< 1000; i++){
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
