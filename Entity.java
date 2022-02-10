import java.awt.*;

/**
 * Write a description of class Entity here.
 *
 * @author Syraj Alkhalil
 * @version 10-02-2022
 */
public class Entity {
    private boolean isDrawable;
    private int age;
    private Color color;
    private Field field;
    /**
     * Constructor for objects of class Entity
     */
    public Entity(boolean isDrawable, Field field) {
        // initialise instance variables
        this.age = 0;
        this.isDrawable = isDrawable;
        this.field = field;
    }

    public int getAge() {
        return age;
    }

    public void toggleDrawable(){
        isDrawable = !isDrawable;
    }

    public void setField(Field field) {
        this.field = field;
    }

    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    public Field getField() {
        return field;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
