/**
 * Provide a counter for a participant in the simulation.
 * This includes an identifying string and a count of how
 * many participants of this type currently exist within 
 * the simulation.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class Counter {
    private final String name;        // A name for this type of simulation participant
    private int count;                // How many of this type exist in the simulation.

    /**
     * Provide a name for one of the simulation types.
     * @param name  A name, e.g. "Fox".
     */
    public Counter(String name) {
        this.name = name;
        count = 0;
    }
    
    /**
     * @return The short description of this type.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The current count for this type.
     */
    public int getCount() {
        return count;
    }

    /**
     * Increment the current count by one.
     */
    public void increment() {
        count++;
    }
    
    /**
     * Reset the current count to zero.
     */
    public void reset() {
        count = 0;
    }
}
