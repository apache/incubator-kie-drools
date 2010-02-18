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

package org.drools.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * An IteratorChain is an Iterator that wraps a number of Iterators.
 * <p>
 * This class makes multiple iterators look like one to the caller When any
 * method from the Iterator interface is called, the IteratorChain will delegate
 * to a single underlying Iterator. The IteratorChain will invoke the Iterators
 * in sequence until all Iterators are exhausted.
 * <p>
 * Under many circumstances, linking Iterators together in this manner is more
 * efficient (and convenient) than reading out the contents of each Iterator
 * into a List and creating a new Iterator.
 * <p>
 * Calling a method that adds new Iterator <i>after a method in the Iterator
 * interface has been called </i> will result in an
 * UnsupportedOperationException. Subclasses should <i>take care </i> to not
 * alter the underlying List of Iterators.
 * <p>
 * NOTE: As from version 3.0, the IteratorChain may contain no iterators. In
 * this case the class will function as an empty iterator.
 * 
 * @since Commons Collections 2.1
 * @version $Revision: 1.1 $ $Date: 2005/07/26 01:06:32 $
 * 
 * @author Morgan Delagrange
 * @author Stephen Colebourne
 */
public class IteratorChain
    implements
    Iterator {

    /** The chain of iterators */
    protected final List iteratorChain        = new ArrayList();
    /** The index of the current iterator */
    protected int        currentIteratorIndex = 0;
    /** The current iterator */
    protected Iterator   currentIterator      = null;
    /**
     * The "last used" Iterator is the Iterator upon which next() or hasNext()
     * was most recently called used for the remove() operation only
     */
    protected Iterator   lastUsedIterator     = null;
    /**
     * ComparatorChain is "locked" after the first time compare(Object,Object)
     * is called
     */
    protected boolean    isLocked             = false;

    // -----------------------------------------------------------------------
    /**
     * Construct an IteratorChain with no Iterators.
     * <p>
     * You will normally use {@link #addIterator(Iterator)}to add some
     * iterators after using this constructor.
     */
    public IteratorChain() {
        super();
    }

    /**
     * Construct an IteratorChain with a single Iterator.
     * 
     * @param iterator
     *            first Iterator in the IteratorChain
     * @throws NullPointerException
     *             if the iterator is null
     */
    public IteratorChain(final Iterator iterator) {
        super();
        addIterator( iterator );
    }

    /**
     * Constructs a new <code>IteratorChain</code> over the two given
     * iterators.
     * 
     * @param a
     *            the first child iterator
     * @param b
     *            the second child iterator
     * @throws NullPointerException
     *             if either iterator is null
     */
    public IteratorChain(final Iterator a,
                         final Iterator b) {
        super();
        addIterator( a );
        addIterator( b );
    }

    /**
     * Constructs a new <code>IteratorChain</code> over the array of
     * iterators.
     * 
     * @param iterators
     *            the array of iterators
     * @throws NullPointerException
     *             if iterators array is or contains null
     */
    public IteratorChain(final Iterator[] iterators) {
        super();
        for ( int i = 0; i < iterators.length; i++ ) {
            addIterator( iterators[i] );
        }
    }

    /**
     * Constructs a new <code>IteratorChain</code> over the collection of
     * iterators.
     * 
     * @param iterators
     *            the collection of iterators
     * @throws NullPointerException
     *             if iterators collection is or contains null
     * @throws ClassCastException
     *             if iterators collection doesn't contain an iterator
     */
    public IteratorChain(final Collection iterators) {
        super();
        for ( final Iterator it = iterators.iterator(); it.hasNext(); ) {
            final Iterator item = (Iterator) it.next();
            addIterator( item );
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Add an Iterator to the end of the chain
     * 
     * @param iterator
     *            Iterator to add
     * @throws IllegalStateException
     *             if I've already started iterating
     * @throws NullPointerException
     *             if the iterator is null
     */
    public void addIterator(final Iterator iterator) {
        checkLocked();
        if ( iterator == null ) {
            throw new NullPointerException( "Iterator must not be null" );
        }
        this.iteratorChain.add( iterator );
    }

    /**
     * Set the Iterator at the given index
     * 
     * @param index
     *            index of the Iterator to replace
     * @param iterator
     *            Iterator to place at the given index
     * @throws IndexOutOfBoundsException
     *             if index &lt; 0 or index &gt; size()
     * @throws IllegalStateException
     *             if I've already started iterating
     * @throws NullPointerException
     *             if the iterator is null
     */
    public void setIterator(final int index,
                            final Iterator iterator) throws IndexOutOfBoundsException {
        checkLocked();
        if ( iterator == null ) {
            throw new NullPointerException( "Iterator must not be null" );
        }
        this.iteratorChain.set( index,
                                iterator );
    }

    /**
     * Get the list of Iterators (unmodifiable)
     * 
     * @return the unmodifiable list of iterators added
     */
    public List getIterators() {
        return Collections.unmodifiableList( this.iteratorChain );
    }

    /**
     * Number of Iterators in the current IteratorChain.
     * 
     * @return Iterator count
     */
    public int size() {
        return this.iteratorChain.size();
    }

    /**
     * Determine if modifications can still be made to the IteratorChain.
     * IteratorChains cannot be modified once they have executed a method from
     * the Iterator interface.
     * 
     * @return true if IteratorChain cannot be modified, false if it can
     */
    public boolean isLocked() {
        return this.isLocked;
    }

    /**
     * Checks whether the iterator chain is now locked and in use.
     */
    private void checkLocked() {
        if ( this.isLocked == true ) {
            throw new UnsupportedOperationException( "IteratorChain cannot be changed after the first use of a method from the Iterator interface" );
        }
    }

    /**
     * Lock the chain so no more iterators can be added. This must be called
     * from all Iterator interface methods.
     */
    private void lockChain() {
        if ( this.isLocked == false ) {
            this.isLocked = true;
        }
    }

    /**
     * Updates the current iterator field to ensure that the current Iterator is
     * not exhausted
     */
    protected void updateCurrentIterator() {
        if ( this.currentIterator == null ) {
            if ( this.iteratorChain.isEmpty() ) {
                this.currentIterator = Collections.EMPTY_LIST.iterator();
            } else {
                this.currentIterator = (Iterator) this.iteratorChain.get( 0 );
            }
            // set last used iterator here, in case the user calls remove
            // before calling hasNext() or next() (although they shouldn't)
            this.lastUsedIterator = this.currentIterator;
        }

        while ( this.currentIterator.hasNext() == false && this.currentIteratorIndex < this.iteratorChain.size() - 1 ) {
            this.currentIteratorIndex++;
            this.currentIterator = (Iterator) this.iteratorChain.get( this.currentIteratorIndex );
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Return true if any Iterator in the IteratorChain has a remaining element.
     * 
     * @return true if elements remain
     */
    public boolean hasNext() {
        lockChain();
        updateCurrentIterator();
        this.lastUsedIterator = this.currentIterator;

        return this.currentIterator.hasNext();
    }

    /**
     * Returns the next Object of the current Iterator
     * 
     * @return Object from the current Iterator
     * @throws java.util.NoSuchElementException
     *             if all the Iterators are exhausted
     */
    public Object next() {
        lockChain();
        updateCurrentIterator();
        this.lastUsedIterator = this.currentIterator;

        return this.currentIterator.next();
    }

    /**
     * Removes from the underlying collection the last element returned by the
     * Iterator. As with next() and hasNext(), this method calls remove() on the
     * underlying Iterator. Therefore, this method may throw an
     * UnsupportedOperationException if the underlying Iterator does not support
     * this method.
     * 
     * @throws UnsupportedOperationException
     *             if the remove operator is not supported by the underlying
     *             Iterator
     * @throws IllegalStateException
     *             if the next method has not yet been called, or the remove
     *             method has already been called after the last call to the
     *             next method.
     */
    public void remove() {
        lockChain();
        updateCurrentIterator();

        this.lastUsedIterator.remove();
    }

}