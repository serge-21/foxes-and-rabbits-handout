import java.util.Random;

/**
 * Provide control over the randomization of the simulation. By using the shared, fixed-seed 
 * randomizer, repeated runs will perform exactly the same (which helps with testing). Set 
 * 'useShared' to false to get different random behaviour every time.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class  Randomizer
{
    // The default seed for control of randomization.
    private static final int SEED = 1111;
    // A shared Random object, if required.
    private static final Random rand = new Random(SEED);
    // Determine whether a shared random generator is to be provided.
    private static final boolean useShared = true;

    /**
     * Constructor for objects of class Randomizer
     */
    public Randomizer()
    {
    }

    /**
     * Rolls a random decimal number from 0 to 100 and if the number is lower than the parameter, it returns true.
     * @param odds the percentage change of an event happening (from 0 to 100)
     * @return True if the roll is successful.
     */
    public static boolean roll(double odds)
    {
        if (odds <= 100 && odds >= 0) {
            return odds < rand.nextDouble() * 100;
        }
        else{
            // out of range error
            return false;
        }
    }

    /**
     * Provide a random generator.
     * @return A random object.
     */
    public static Random getRandom()
    {
        if(useShared) {
            return rand;
        }
        else {
            return new Random();
        }
    }
    
    /**
     * Reset the randomization.
     * This will have no effect if randomization is not through
     * a shared Random generator.
     */
    public static void reset()
    {
        if(useShared) {
            rand.setSeed(SEED);
        }
    }
}
