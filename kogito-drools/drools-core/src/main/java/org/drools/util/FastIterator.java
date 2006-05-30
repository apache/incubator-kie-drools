package org.drools.util;

/*
 * Javolution - Java(TM) Solution for Real-Time and Embedded Systems
 * Copyright (C) 2005 - Javolution (http://javolution.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.drools.util.FastCollection.Record;

/**
 * <p> This class represents an iterator over a {@link Fastcollection).
 *     Iterations are thread-safe if the collections records are not removed 
 *     or inserted at arbitrary position (appending/prepending is fine).</p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.7, March 17, 2005
 */
final class FastIterator
    implements
    Iterator {

    private FastCollection _collection;

    private Record         _current;

    private Record         _next;

    private Record         _tail;

    public static FastIterator valueOf(final FastCollection collection) {
        final FastIterator iterator = new FastIterator();
        iterator._collection = collection;
        iterator._next = collection.head().getNext();
        iterator._tail = collection.tail();
        return iterator;
    }

    private FastIterator() {
    }

    public boolean hasNext() {
        return (this._next != this._tail);
    }

    public Object next() {
        if ( this._next == this._tail ) {
            throw new NoSuchElementException();
        }
        this._current = this._next;
        this._next = this._next.getNext();
        return this._collection.valueOf( this._current );
    }

    public void remove() {
        if ( this._current != null ) {
            // Uses the previous record (not affected by the remove)
            // to set the next record.
            final Record previous = this._current.getPrevious();
            this._collection.delete( this._current );
            this._current = null;
            this._next = previous.getNext();
        } else {
            throw new IllegalStateException();
        }
    }
}