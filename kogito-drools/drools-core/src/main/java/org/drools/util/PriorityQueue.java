package org.drools.util;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Binary heap implementation of <code>Buffer</code> that provides for removal
 * based on <code>Comparator</code> ordering. <p/>The removal order of a
 * binary heap is based on either the natural sort order of its elements or a
 * specified {@linkComparator}. The {@link #remove()}method always returns the
 * first element as determined by the sort order. (The
 * <code>ascendingOrder</code> flag in the constructors can be used to reverse
 * the sort order, in which case {@link#remove()}will always remove the last
 * element.) The removal order is <i>not </i> the same as the order of
 * iteration; elements are returned by the iterator in no particular order. <p/>
 * The {@link #add(Object)}and {@link #remove()}operations perform in
 * logarithmic time. The {@link #get()}operation performs in constant time. All
 * other operations perform in linear time or worse. <p/>Note that this
 * implementation is not synchronized.
 * 
 * @author Peter Donald
 * @author Ram Chidambaram
 * @author Michael A. Smith
 * @author Paul Jack
 * @author Stephen Colebourne
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 * @version $Revision: 1.1 $ $Date: 2005/07/26 01:06:32 $
 */
public class PriorityQueue extends AbstractCollection
    implements
    Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 640473968693160007L;

    /**
     * The default capacity for the buffer.
     */
    private static final int  DEFAULT_CAPACITY = 13;

    /**
     * The elements in this buffer.
     */
    protected Object[]        elements;

    /**
     * The number of elements currently in this buffer.
     */
    protected int             size;

    /**
     * If true, the first element as determined by the sort order will be
     * returned. If false, the last element as determined by the sort order will
     * be returned.
     */
    protected boolean         ascendingOrder;

    /**
     * The comparator used to order the elements
     */
    protected Comparator      comparator;

    // -----------------------------------------------------------------------
    /**
     * Constructs a new empty buffer that sorts in ascending order by the
     * natural order of the objects added.
     */
    public PriorityQueue() {
        this( PriorityQueue.DEFAULT_CAPACITY,
              true,
              null );
    }

    /**
     * Constructs a new empty buffer that sorts in ascending order using the
     * specified comparator.
     * 
     * @param comparator
     *            the comparator used to order the elements, null means use
     *            natural order
     */
    public PriorityQueue(final Comparator comparator) {
        this( PriorityQueue.DEFAULT_CAPACITY,
              true,
              comparator );
    }

    /**
     * Constructs a new empty buffer specifying the sort order and using the
     * natural order of the objects added.
     * 
     * @param ascendingOrder
     *            if <code>true</code> the heap is created as a minimum heap;
     *            otherwise, the heap is created as a maximum heap
     */
    public PriorityQueue(final boolean ascendingOrder) {
        this( PriorityQueue.DEFAULT_CAPACITY,
              ascendingOrder,
              null );
    }

    /**
     * Constructs a new empty buffer specifying the sort order and comparator.
     * 
     * @param ascendingOrder
     *            true to use the order imposed by the given comparator; false
     *            to reverse that order
     * @param comparator
     *            the comparator used to order the elements, null means use
     *            natural order
     */
    public PriorityQueue(final boolean ascendingOrder,
                         final Comparator comparator) {
        this( PriorityQueue.DEFAULT_CAPACITY,
              ascendingOrder,
              comparator );
    }

    /**
     * Constructs a new empty buffer that sorts in ascending order by the
     * natural order of the objects added, specifying an initial capacity.
     * 
     * @param capacity
     *            the initial capacity for the buffer, greater than zero
     * @throws IllegalArgumentException
     *             if <code>capacity</code> is &lt;= <code>0</code>
     */
    public PriorityQueue(final int capacity) {
        this( capacity,
              true,
              null );
    }

    /**
     * Constructs a new empty buffer that sorts in ascending order using the
     * specified comparator and initial capacity.
     * 
     * @param capacity
     *            the initial capacity for the buffer, greater than zero
     * @param comparator
     *            the comparator used to order the elements, null means use
     *            natural order
     * @throws IllegalArgumentException
     *             if <code>capacity</code> is &lt;= <code>0</code>
     */
    public PriorityQueue(final int capacity,
                         final Comparator comparator) {
        this( capacity,
              true,
              comparator );
    }

    /**
     * Constructs a new empty buffer that specifying initial capacity and sort
     * order, using the natural order of the objects added.
     * 
     * @param capacity
     *            the initial capacity for the buffer, greater than zero
     * @param ascendingOrder
     *            if <code>true</code> the heap is created as a minimum heap;
     *            otherwise, the heap is created as a maximum heap.
     * @throws IllegalArgumentException
     *             if <code>capacity</code> is <code>&lt;= 0</code>
     */
    public PriorityQueue(final int capacity,
                         final boolean ascendingOrder) {
        this( capacity,
              ascendingOrder,
              null );
    }

    /**
     * Constructs a new empty buffer that specifying initial capacity, sort
     * order and comparator.
     * 
     * @param capacity
     *            the initial capacity for the buffer, greater than zero
     * @param ascendingOrder
     *            true to use the order imposed by the given comparator; false
     *            to reverse that order
     * @param comparator
     *            the comparator used to order the elements, null means use
     *            natural order
     * @throws IllegalArgumentException
     *             if <code>capacity</code> is <code>&lt;= 0</code>
     */
    public PriorityQueue(final int capacity,
                         final boolean ascendingOrder,
                         final Comparator comparator) {
        super();
        if ( capacity <= 0 ) {
            throw new IllegalArgumentException( "invalid capacity" );
        }
        this.ascendingOrder = ascendingOrder;

        // +1 as 0 is noop
        this.elements = new Object[capacity + 1];
        this.comparator = comparator;
    }

    // -----------------------------------------------------------------------
    /**
     * Checks whether the heap is ascending or descending order.
     * 
     * @return true if ascending order (a min heap)
     */
    public boolean isAscendingOrder() {
        return this.ascendingOrder;
    }

    /**
     * Gets the comparator being used for this buffer, null is natural order.
     * 
     * @return the comparator in use, null is natural order
     */
    public Comparator comparator() {
        return this.comparator;
    }

    // -----------------------------------------------------------------------
    /**
     * Returns the number of elements in this buffer.
     * 
     * @return the number of elements in this buffer
     */
    public int size() {
        return this.size;
    }

    /**
     * Clears all elements from the buffer.
     */
    public void clear() {
        this.elements = new Object[this.elements.length]; // for gc
        this.size = 0;
    }

    /**
     * Adds an element to the buffer. <p/>The element added will be sorted
     * according to the comparator in use.
     * 
     * @param element
     *            the element to be added
     * @return true always
     */
    public boolean add(final Object element) {
        if ( isAtCapacity() ) {
            grow();
        }
        // percolate element to it's place in tree
        if ( this.ascendingOrder ) {
            percolateUpMinHeap( element );
        } else {
            percolateUpMaxHeap( element );
        }
        return true;
    }

    /**
     * Gets the next element to be removed without actually removing it (peek).
     * 
     * @return the next element
     * @throws NoSuchElementException
     *             if the buffer is empty
     */
    public Object get() {
        if ( isEmpty() ) {
            throw new NoSuchElementException();
        } else {
            return this.elements[1];
        }
    }

    /**
     * Gets and removes the next element (pop).
     * 
     * @return the next element
     * @throws NoSuchElementException
     *             if the buffer is empty
     */
    public Object remove() {
        final Object result = get();
        this.elements[1] = this.elements[this.size--];

        // set the unused element to 'null' so that the garbage collector
        // can free the object if not used anywhere else.(remove reference)
        this.elements[this.size + 1] = null;

        if ( this.size != 0 ) {
            // percolate top element to it's place in tree
            if ( this.ascendingOrder ) {
                percolateDownMinHeap( 1 );
            } else {
                percolateDownMaxHeap( 1 );
            }
        }

        return result;
    }

    // -----------------------------------------------------------------------
    /**
     * Tests if the buffer is at capacity.
     * 
     * @return <code>true</code> if buffer is full; <code>false</code>
     *         otherwise.
     */
    protected boolean isAtCapacity() {
        // +1 as element 0 is noop
        return this.elements.length == this.size + 1;
    }

    /**
     * Percolates element down heap from the position given by the index. <p/>
     * Assumes it is a minimum heap.
     * 
     * @param index
     *            the index for the element
     */
    protected void percolateDownMinHeap(final int index) {
        final Object element = this.elements[index];
        int hole = index;

        while ( (hole * 2) <= this.size ) {
            int child = hole * 2;

            // if we have a right child and that child can not be percolated
            // up then move onto other child
            if ( child != this.size && compare( this.elements[child + 1],
                                                this.elements[child] ) < 0 ) {
                child++;
            }

            // if we found resting place of bubble then terminate search
            if ( compare( this.elements[child],
                          element ) >= 0 ) {
                break;
            }

            this.elements[hole] = this.elements[child];
            hole = child;
        }

        this.elements[hole] = element;
    }

    /**
     * Percolates element down heap from the position given by the index. <p/>
     * Assumes it is a maximum heap.
     * 
     * @param index
     *            the index of the element
     */
    protected void percolateDownMaxHeap(final int index) {
        final Object element = this.elements[index];
        int hole = index;

        while ( (hole * 2) <= this.size ) {
            int child = hole * 2;

            // if we have a right child and that child can not be percolated
            // up then move onto other child
            if ( child != this.size && compare( this.elements[child + 1],
                                                this.elements[child] ) > 0 ) {
                child++;
            }

            // if we found resting place of bubble then terminate search
            if ( compare( this.elements[child],
                          element ) <= 0 ) {
                break;
            }

            this.elements[hole] = this.elements[child];
            hole = child;
        }

        this.elements[hole] = element;
    }

    /**
     * Percolates element up heap from the position given by the index. <p/>
     * Assumes it is a minimum heap.
     * 
     * @param index
     *            the index of the element to be percolated up
     */
    protected void percolateUpMinHeap(final int index) {
        int hole = index;
        final Object element = this.elements[hole];
        while ( hole > 1 && compare( element,
                                     this.elements[hole / 2] ) < 0 ) {
            // save element that is being pushed down
            // as the element "bubble" is percolated up
            final int next = hole / 2;
            this.elements[hole] = this.elements[next];
            hole = next;
        }
        this.elements[hole] = element;
    }

    /**
     * Percolates a new element up heap from the bottom. <p/>Assumes it is a
     * minimum heap.
     * 
     * @param element
     *            the element
     */
    protected void percolateUpMinHeap(final Object element) {
        this.elements[++this.size] = element;
        percolateUpMinHeap( this.size );
    }

    /**
     * Percolates element up heap from from the position given by the index.
     * <p/>Assume it is a maximum heap.
     * 
     * @param index
     *            the index of the element to be percolated up
     */
    protected void percolateUpMaxHeap(final int index) {
        int hole = index;
        final Object element = this.elements[hole];

        while ( hole > 1 && compare( element,
                                     this.elements[hole / 2] ) > 0 ) {
            // save element that is being pushed down
            // as the element "bubble" is percolated up
            final int next = hole / 2;
            this.elements[hole] = this.elements[next];
            hole = next;
        }

        this.elements[hole] = element;
    }

    /**
     * Percolates a new element up heap from the bottom. <p/>Assume it is a
     * maximum heap.
     * 
     * @param element
     *            the element
     */
    protected void percolateUpMaxHeap(final Object element) {
        this.elements[++this.size] = element;
        percolateUpMaxHeap( this.size );
    }

    /**
     * Compares two objects using the comparator if specified, or the natural
     * order otherwise.
     * 
     * @param a
     *            the first object
     * @param b
     *            the second object
     * @return -ve if a less than b, 0 if they are equal, +ve if a greater than
     *         b
     */
    protected int compare(final Object a,
                          final Object b) {
        if ( this.comparator != null ) {
            return this.comparator.compare( a,
                                            b );
        } else {
            return ((Comparable) a).compareTo( b );
        }
    }

    /**
     * Increases the size of the heap to support additional elements
     */
    protected void grow() {
        final Object[] array = new Object[this.elements.length * 2];
        System.arraycopy( this.elements,
                          0,
                          array,
                          0,
                          this.elements.length );
        this.elements = array;
    }

    // -----------------------------------------------------------------------
    /**
     * Returns an iterator over this heap's elements.
     * 
     * @return an iterator over this heap's elements
     */
    public Iterator iterator() {
        return new Iterator() {

            private int index             = 1;

            private int lastReturnedIndex = -1;

            public boolean hasNext() {
                return this.index <= PriorityQueue.this.size;
            }

            public Object next() {
                if ( !hasNext() ) {
                    throw new NoSuchElementException();
                }
                this.lastReturnedIndex = this.index;
                this.index++;
                return PriorityQueue.this.elements[this.lastReturnedIndex];
            }

            public void remove() {
                if ( this.lastReturnedIndex == -1 ) {
                    throw new IllegalStateException();
                }
                PriorityQueue.this.elements[this.lastReturnedIndex] = PriorityQueue.this.elements[PriorityQueue.this.size];
                PriorityQueue.this.elements[PriorityQueue.this.size] = null;
                PriorityQueue.this.size--;
                if ( PriorityQueue.this.size != 0 && this.lastReturnedIndex <= PriorityQueue.this.size ) {
                    int compareToParent = 0;
                    if ( this.lastReturnedIndex > 1 ) {
                        compareToParent = compare( PriorityQueue.this.elements[this.lastReturnedIndex],
                                                   PriorityQueue.this.elements[this.lastReturnedIndex / 2] );
                    }
                    if ( PriorityQueue.this.ascendingOrder ) {
                        if ( this.lastReturnedIndex > 1 && compareToParent < 0 ) {
                            percolateUpMinHeap( this.lastReturnedIndex );
                        } else {
                            percolateDownMinHeap( this.lastReturnedIndex );
                        }
                    } else { // max heap
                        if ( this.lastReturnedIndex > 1 && compareToParent > 0 ) {
                            percolateUpMaxHeap( this.lastReturnedIndex );
                        } else {
                            percolateDownMaxHeap( this.lastReturnedIndex );
                        }
                    }
                }
                this.index--;
                this.lastReturnedIndex = -1;
            }

        };
    }

    /**
     * Returns a string representation of this heap. The returned string is
     * similar to those produced by standard JDK collections.
     * 
     * @return a string representation of this heap
     */
    public String toString() {
        final StringBuffer sb = new StringBuffer();

        sb.append( "[ " );

        for ( int i = 1; i < this.size + 1; i++ ) {
            if ( i != 1 ) {
                sb.append( ", " );
            }
            sb.append( this.elements[i] );
        }

        sb.append( " ]" );

        return sb.toString();
    }

}