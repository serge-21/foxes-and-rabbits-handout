/**
 * Represent a location in a rectangular grid.
 *
 * @author David J. Barnes, Michael Kölling, Syraj Alkhalil and Cosmo Colman
 * @version 2022.02.27 (2)
 */
public class Location {
    // Row and column positions.
    private int row;
    private int col;

    /**
     * Represent a row and column.
     * @param row The row.
     * @param col The column.
     */
    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Implement content equality.
     */
    public boolean equals(Object obj) {
        if(obj instanceof Location) {
            Location other = (Location) obj;
            return row == other.getRow() && col == other.getCol();
        }
        else {
            return false;
        }
    }

    /**
     * Checks if the location is within a passed bound.
     *
     * @param rowBounds the max row value.
     * @param colBounds the max column value.
     * @return true if within bounds.
     */
    public boolean withinBounds(int rowBounds, int colBounds) {
        if ((row <= rowBounds && col <= colBounds) && (row >= 0 && col >= 0)){
            return true;
        }
        return false;
    }
    
    /**
     * Return a string of the form row,column
     * @return A string representation of the location.
     */
    public String toString() {
        return row + "," + col;
    }
    
    /**
     * Use the top 16 bits for the row value and the bottom for
     * the column. Except for very big grids, this should give a
     * unique hash code for each (row, col) pair.
     * @return A hashcode for the location.
     */
    public int hashCode() {
        return (row << 16) + col;
    }
    
    /**
     * @return The row.
     */
    public int getRow() {
        return row;
    }
    
    /**
     * @return The column.
     */
    public int getCol() {
        return col;
    }
}
