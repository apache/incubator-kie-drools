package org.drools.examples.sudoku;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/**
 * Abstract base class for the three types of cell groups of nine cells.
 */
public abstract class SetOfNine {
    
    private Set<Integer> free;

    protected SetOfNine() {
        free = new HashSet<>( CellGroup.ALL_NINE);
    }
    
    /**
     * Redefine the set of acceptable values for this cell.
     * @param values the Integer objects representing the new set of acceptable values.
     */
    public void blockExcept(Integer... values) {
        free.clear();
        Collections.addAll(free, values);
    }

    /**
     * Remove an Integer from the values still to be assigned to some cell of this group. 
     * @param i an Integer object
     */
    public void blockValue(Integer i) {
        free.remove(i);
    }
    
    /**
     * Returns the set of Integers that still need to be assigned to some cell of this group.
     * @return a Set of Integer objects.
     */
    public Set<Integer> getFree() {
        return free;
    }
    
    /**
     * Returns the number of Integers that still need to be assigned to some cell of this group.
     * @return an int value
     */
    public int getFreeCount() {
        return free.size();
    }
    /**
     * Returns the first (only) permissible Integer value.
     * @return an Integer object
     */
    public Integer getFreeValue() {
        return free.iterator().next();
    }
}
