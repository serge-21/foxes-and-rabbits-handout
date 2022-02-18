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
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.01;
    // The probability that a rabbit will be created in any given grid position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.04;
    private static final double PREDITOR1_CREATION_PROBABILITY = 0.03;
    private static final double PREY1_CREATION_PROBABILITY = 0.04;
    private static final double PLANT_CREATION_PROBABILITY = 0.01;

    // List of animals in the field.
    private final List<Organism> organisms;
    // The current state of the field.
    private final Field field;
    // The current step of the simulation with the time of the day
    private int step;
    private boolean isDay = true;
    private String timeOfDay = "day";
    private int numOfDays = 0;
    private int time;

    // the current weather
    private Weather weather;
    // A graphical view of the simulation.
    private final SimulatorView view;

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
        
        organisms = new ArrayList<>();
        field = new Field(depth, width);
        weather = new Weather();
        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Prey1.class, Color.MAGENTA);
        view.setColor(Predator1.class, Color.RED);
        view.setColor(Plants.class, Color.GREEN);
        // Setup a valid starting point.
        reset();
        pickWeather();
    }

    private void pickWeather(){
        this.weather.pickSeason();
        this.weather.generateVisibilityAndDownfall();
//                possibleWeathers[new Random().nextInt(possibleWeathers.length)];
//        delay((int) this.weather.visibility);
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
        time = step % 24;
        if(time < 4 || time > 16){
            // then it's night
            timeOfDay = "night";
            isDay = false;
        }else {
            timeOfDay = "day";
            isDay = true;
        }

        if(time == 0) {
            // do all changes
            numOfDays += 1;
            pickWeather();
            System.out.println(this.weather.toString());
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
            if (!entity.getIsAlive()) {
                it.remove();
            }
        }

        // Add the newly created organisms to the list.
        organisms.addAll(newOrganisms);
        showStatus();
    }

    private void showStatus(){
        view.showStatus(step, field, timeOfDay, numOfDays, updateTime());
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
        // Show the starting state in the view.
        showStatus();
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    organisms.add(fox);
                }
                else if(rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    organisms.add(rabbit);
                }
                // else leave the location empty.
                else if(rand.nextDouble() <= PREDITOR1_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Predator1 rabbit = new Predator1(true, field, location);
                    organisms.add(rabbit);
                }
                else if(rand.nextDouble() <= PREY1_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Prey1 rabbit = new Prey1(true, field, location);
                    organisms.add(rabbit);
                }
                 else if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plants plant = new Plants(field, location);
                    organisms.add(plant);
                }
            }
        }
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

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        for(int i = 0; i< 1000; i++){
            simulator.simulateOneStep();

            simulator.delay(1000);
        }
    }
}
