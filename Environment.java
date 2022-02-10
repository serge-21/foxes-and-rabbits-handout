/**
 * Defines the conditions of the environment, like the visibility, downfall
 *
 * @author fox
 * @version 1.0
 */

public class Environment {

    private final double visibility;
    private final double downfall;

    /**
     * Constructor for objects of class Environment. Defines the final values for a specific condition.
     */
    public Environment(double visibility, double downfall){
        this.visibility = visibility;
        this.downfall = downfall;
    }

    /**
     * Returns the visibility value.
     * @return the visibility value.
     */
    public double getVisibility(){
        return visibility;
    }

    /**
     * Returns the downfall value.
     * @return the downfall value.
     */
    public double getDownfall(){
        return downfall;
    }
}
