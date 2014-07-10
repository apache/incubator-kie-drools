/*
 * Copyright 20101 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.examples.sudoku;

import java.util.Set;
import java.util.HashSet;

/**
 * Abstract base class for the three types of cell groups of nine cells.
 */
public abstract class SetOfNine {
    
    private Set<Integer> free;

    protected SetOfNine() {
        free = new HashSet<Integer>( CellGroup.ALL_NINE);
    }
    
    /**
     * Redefine the set of acceptable values for this cell.
     * @param values the Integer objects representing the new set of acceptable values.
     */
    public void blockExcept(Integer... values) {
        free.clear();
        for( Integer value: values ){
            free.add(value);
        }
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
