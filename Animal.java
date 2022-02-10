import java.awt.*;
import java.util.List;

/**
 * A class representing shared characteristics of animals.
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Organism{
    private boolean isMale;
    private boolean isNocturnal;
    private boolean isInfected;
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean isDrawable, Field field,  Location initLocation) {
        super(isDrawable, field, initLocation);
        this.isMale = true;
        this.isNocturnal = false;
        this. isInfected = false;
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);

    abstract protected Location findFood();

    abstract protected Location findMate();
}
