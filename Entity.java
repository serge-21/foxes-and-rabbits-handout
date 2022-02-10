import java.awt.*;

/**
 * Write a description of class Entity here.
 *
 * @author Syraj Alkhalil
 * @version 10-02-2022
 */
public class Entity {
    private static boolean isDrawable;
    private int age;
    private static Color color;
    private Field field;
    /**
     * Constructor for objects of class Entity
     */
    public Entity(boolean isDrawable, Field field) {
        // initialise instance variables
        this.age = 0;
        Entity.isDrawable = isDrawable;
        this.field = field;
    }

    public static boolean getIsDrwable(){
        return isDrawable;
    }

    public void setColor(Color color){
        Entity.color = color;
    }

    public static Color getColor() {
        if(isDrawable){
            return color;
        }
        return Color.white;
    }

    public int getAge() {
        return age;
    }

    public static void toggleDrawable(){
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
