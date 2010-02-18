/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.drools.core.util;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

/** 
 * Implements an {@link java.util.Iterator Iterator} over any array.
 * <p>
 * The array can be either an array of object or of primitives. If you know 
 * that you have an object array, the 
 * {@link org.apache.commons.collections.iterators.ObjectArrayIterator ObjectArrayIterator}
 * class is a better choice, as it will perform better.
 * <p>
 * The iterator implements a {@link #reset} method, allowing the reset of 
 * the iterator back to the start if required.
 *
 * @since Commons Collections 1.0
 * @version $Revision$ $Date$
 *
 * @author James Strachan
 * @author Mauricio S. Moura
 * @author Michael A. Smith
 * @author Neil O'Toole
 * @author Stephen Colebourne
 */
public class ArrayIterator implements java.util.Iterator {

    /** The array to iterate over */    
    protected Object array;
    /** The end index to loop to */
	protected int endIndex = 0;
    /** The current iterator index */
	protected int index = 0;
    
   
    /**
     * Constructs an ArrayIterator that will iterate over the values in the
     * specified array.
     *
     * @param array the array to iterate over.
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     */
    public ArrayIterator(final Object array) {
        setArray(array);
    }

    /**
     * Checks whether the index is valid or not.
     * 
     * @param bound  the index to check
     * @param type  the index type (for error messages)
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    protected void checkBound(final int bound, final String type ) {
        if (bound > this.endIndex) {
            throw new ArrayIndexOutOfBoundsException(
              "Attempt to make an ArrayIterator that " + type +
              "s beyond the end of the array. "
            );
        }
        if (bound < 0) {
            throw new ArrayIndexOutOfBoundsException(
              "Attempt to make an ArrayIterator that " + type +
              "s before the start of the array. "
            );
        }
    }

    // Iterator interface
    //-----------------------------------------------------------------------
    /**
     * Returns true if there are more elements to return from the array.
     *
     * @return true if there is a next element to return
     */
    public boolean hasNext() {
        return (index < endIndex);
    }

    /**
     * Returns the next element in the array.
     *
     * @return the next element in the array
     * @throws NoSuchElementException if all the elements in the array
     *  have already been returned
     */
    public Object next() {
        if (hasNext() == false) {
            throw new NoSuchElementException();
        }
        return Array.get(array, index++);
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException("remove() method is not supported");
    }

    // Properties
    //-----------------------------------------------------------------------
    /**
     * Gets the array that this iterator is iterating over. 
     *
     * @return the array this iterator iterates over, or <code>null</code> if
     *  the no-arg constructor was used and {@link #setArray(Object)} has never
     *  been called with a valid array.
     */
    public Object getArray() {
        return array;
    }
    
    /**
     * Sets the array that the ArrayIterator should iterate over.
     * <p>
     * If an array has previously been set (using the single-arg constructor
     * or this method) then that array is discarded in favour of this one.
     * Iteration is restarted at the start of the new array.
     * Although this can be used to reset iteration, the {@link #clear()} method
     * is a more effective choice.
     *
     * @param array the array that the iterator should iterate over.
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     */
    private void setArray(final Object array) {
        // Array.getLength throws IllegalArgumentException if the object is not
        // an array or NullPointerException if the object is null.  This call
        // is made before saving the array and resetting the index so that the
        // array iterator remains in a consistent state if the argument is not
        // an array or is null.
        this.endIndex = Array.getLength(array);
        this.array = array;
        this.index = 0;
    }

}
