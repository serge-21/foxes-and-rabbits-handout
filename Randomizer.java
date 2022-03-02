import java.util.Random;

/**
 * Provide control over the randomization of the simulation. By using the fixed-seed
 * randomizer, repeated runs will perform exactly the same (which helps with testing).
 * Users are able to set the seed and therefore get different behaviour if they so wish
 *
 * @author Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class  Randomizer {
    private static final int DEFAULT_SEED = 1111;           // The default seed for control of randomization.
    private static int seed = DEFAULT_SEED;                 // The variable seed that is altered by the user
    private static Random rand = new Random(seed);          // A shared Random object, if required.

    /**
     * Constructor for objects of class Randomizer
     */
    public Randomizer() {}

    /**
     * This method is used to reset the object's seed
     */
    public static void resetRandom(){
        rand.setSeed(seed);
    }

    /**
     * A simple getter method to return the seed field
     *
     * @return an int value of the seed
     */
    public static int getSeed(){
        return seed;
    }

    /**
     * A simple setter method to set the value of the field seed
     *
     * @param newSeed the new seed we wish to set as the seed
     */
    public static void setSeed(int newSeed){
        seed = newSeed;
    }

    /**
     * This method is used to restore the default seed
     */
    public static void restoreDefaultSeed(){
        seed = DEFAULT_SEED;
    }

    /**
     * A simple method to generate a random number given a limit
     *
     * @param limit the limit we wish to stop at
     * @return a random int given the limit
     */
    public int nextInt(int limit){
        return rand.nextInt(limit);
    }

    /**
     * Provide a random generator.
     *
     * @return A random object.
     */
    public static Random getRandom()
    {
        return rand;
    }

    /**
     * reset the entire object completely
     */
    public void completeReset(){
        resetRandom();
    }
}
