package org.drools.leaps.util;

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
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.drools.WorkingMemory;
import org.drools.leaps.ColumnConstraints;

/**
 * double linked list structure to store objects in the ordered list 
 * and iterate over the list for leaps 
 * 
 * @author Alexander Bagerman
 * 
 */
public class Table implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2614082619270512055L;

    private final TreeSet     set;

    protected TableRecord     headRecord;

    protected TableRecord     tailRecord;

    private boolean           empty            = true;

    private int               count            = 0;

    public Table(final Comparator comparator) {
        this.set = new TreeSet( new RecordComparator(comparator) );
    }

    public void clear() {
        this.headRecord = null;
        this.empty = true;
        this.count = 0;
        this.set.clear( );
    }

    /**
     * @param object
     *            to add
     */
    public void add( final Object object ) {
        final TableRecord newRecord = new TableRecord( object );
        if (this.empty) {
            this.headRecord = newRecord;
            this.empty = false;
        }
        else {
            try {
                // check on first key should work faster than check on empty
                // but logically we check on empty
                // if map empty it will throw exception
                final TableRecord bufRec = (TableRecord) this.set.headSet( newRecord )
                                                                 .last( );
                if (bufRec.right != null) {
                    bufRec.right.left = newRecord;
                }
                newRecord.right = bufRec.right;
                bufRec.right = newRecord;
                newRecord.left = bufRec;

            }
            catch (final NoSuchElementException nsee) {
                // means map is empty
                this.headRecord.left = newRecord;
                newRecord.right = this.headRecord;
                this.headRecord = newRecord;
            }
        }
        // check if the new record was added at the end of the list
        // and assign new value to the tail record
        if (newRecord.right == null) {
            this.tailRecord = newRecord;
        }
        //
        this.count++;
        //
        this.set.add( newRecord );
    }

    /**
     * Removes object from the table
     * 
     * @param object
     *            to remove from the table
     */
    public void remove( final Object object ) {
        if (!this.empty) {
            try {
                final TableRecord record = (TableRecord) this.set.tailSet( new TableRecord( object ) )
                                                                 .first( );

                if (record != null) {
                    if (record == this.headRecord) {
                        if (record.right != null) {
                            this.headRecord = record.right;
                            this.headRecord.left = null;
                        }
                        else {
                            // single element in table being valid
                            // table is empty now
                            this.headRecord = new TableRecord( null );
                            this.tailRecord = this.headRecord;
                            this.empty = true;
                        }
                    }
                    else if (record == this.tailRecord) {
                        // single element in the table case is being solved
                        // above in check for headRecord match
                        this.tailRecord = record.left;
                        this.tailRecord.right = null;
                    }
                    else {
                        // left
                        record.left.right = record.right;
                        record.right.left = record.left;
                    }
                    record.left = null;
                    record.right = null;
                }
                this.count--;
                //
                this.set.remove( record );
            }
            catch (final NoSuchElementException nsee) {
            }
        }
    }

    /**
     * @param object
     * @return indicator of presence of given object in the table
     */
    public boolean contains( final Object object ) {
        boolean ret = false;
        if (!this.empty) {
            ret = this.set.contains( new TableRecord( object ) );
        }
        return ret;
    }

    /**
     * @return TableIterator for this Table
     * @see org.drools.leaps.util.TableIterator
     * @see org.drools.leaps.util.IteratorFromPositionToTableStart
     */
    public TableIterator iterator() {
        TableIterator ret;
        if (this.empty) {
            ret = new IteratorFromPositionToTableStart( null, null );
        }
        else {
            ret = new IteratorFromPositionToTableStart( this.headRecord, this.headRecord );
        }
        return ret;
    }

    public TableIterator reverseOrderIterator() {
        TableIterator ret;
        if (this.empty) {
            ret = new IteratorFromPositionToTableEnd( null, null );
        }
        else {
            ret = new IteratorFromPositionToTableEnd( this.tailRecord, this.tailRecord );
        }
        return ret;
    }

    /**
     * retrieve iterator over portion of the table data starting at objectAtStart and positioning
     * iterator at objectAtPosition for resumed iterations 
     * 
     * @param objectAtStart -
     *            upper boundary of the iteration
     * @param objectAtPosition -
     *            starting point of the iteration
     * @return leaps table iterator
     * @throws TableOutOfBoundException
     */
    public TableIterator constrainedIteratorFromPositionToTableStart( final WorkingMemory workingMemory,
                                                                      final ColumnConstraints constraints,
                                                                      final Object objectAtStart,
                                                                      final Object objectAtPosition ) {
        return getIteratorFromPositionToTableStart( true,
                                                    workingMemory,
                                                    constraints,
                                                    objectAtStart,
                                                    objectAtPosition );
    }

    /**
     * retrieve iterator over portion of the table data starting at objectAtStart and positioning
     * iterator at objectAtPosition for resumed iterations 
     * 
     * @param objectAtStart -
     *            upper boundary of the iteration
     * @param objectAtPosition -
     *            starting point of the iteration
     * @return leaps table iterator
     * @throws TableOutOfBoundException
     */
    public TableIterator iteratorFromPositionToTableStart( final Object objectAtStart,
                                                           final Object objectAtPosition ) {
        return getIteratorFromPositionToTableStart( false,
                                                    null,
                                                    null,
                                                    objectAtStart,
                                                    objectAtPosition );
    }

    /**
     * retrieve iterator over portion of the table data starting at objectAtStart and positioning
     * iterator at objectAtPosition for resumed iterations 
     * 
     * @param objectAtStart -
     *            upper boundary of the iteration
     * @param objectAtPosition -
     *            starting point of the iteration
     * @return leaps table iterator
     * @throws TableOutOfBoundException
     */
    public TableIterator iteratorFromPositionToTableEnd( final Object objectAtStart ) {
        TableRecord record = null;
        try {
            record = (TableRecord) this.set.headSet( new TableRecord( objectAtStart ) )
                                                    .last( );
        }
        catch (final NoSuchElementException nsee) {
        }

        return new IteratorFromPositionToTableEnd( record, record );
    }

    /**
     * retrieve Markers structure for a given objectAtStart.  
     * 
     * @param objectAtStart -
     *            upper boundary of the iteration
     * @return Markers structure
     */
    private TableIterator getIteratorFromPositionToTableStart( final boolean isConstraint, 
                                                               final WorkingMemory workingMemory,
                                                               final ColumnConstraints constraints,
                                                               final Object objectAtStart,
                                                               final Object objectAtPosition ) {
        TableRecord startRecord = null;
        TableRecord currentRecord = null;

        TableRecord recordAtStart = new TableRecord( objectAtStart );
        if (!this.empty) { 
            try {
                // check on first key should work faster than check
                // to see if set has no elements
                // if set is empty it will throw exception
                startRecord = (TableRecord) this.set.tailSet( recordAtStart ).first( );
                if (objectAtStart == objectAtPosition) {
                    currentRecord = startRecord;
                }
                else {
                    // rewind to position
                    try {
                        // check on first key should work faster than check
                        // to see if set has no elements
                        // if set is empty it will throw exception
                        currentRecord = (TableRecord) this.set.tailSet( new TableRecord( objectAtPosition ) )
                                                              .first( );
                    }
                    catch (final NoSuchElementException nsee) {
                        currentRecord = startRecord;
                    }
                }
            }
            catch (final NoSuchElementException nsee) {
            }
        }

        if (isConstraint ) {
            return new ConstrainedIteratorFromPositionToTableStart( workingMemory,
                                                                constraints,
                                                                startRecord,
                                                                currentRecord );
        }
        else {
            return new IteratorFromPositionToTableStart( startRecord, currentRecord );
        }
    }

    /**
     * indicates if table has any elements
     * 
     * @return empty indicator
     */
    public boolean isEmpty() {
        return this.empty;
    }

    public String toString() {
        String ret = "";

        for (final Iterator it = this.iterator( ); it.hasNext( );) {
            ret = ret + it.next( ) + "\n";
        }
        return ret;
    }

    public int size() {
        return this.count;
    }

    public Object top() {
        return this.headRecord.object;
    }

    public Object bottom() {
        return this.tailRecord.object;
    }

    public static TableIterator singleItemIterator( final Object object ) {
        return new IteratorFromPositionToTableStart( new TableRecord( object ) );
    }

    public static TableIterator emptyIterator() {
        return new IteratorFromPositionToTableStart( null, null );
    }
}
