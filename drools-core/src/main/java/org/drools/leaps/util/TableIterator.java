package org.drools.leaps.util;

import java.util.Iterator;

/**
 * Leaps specific iterator for leaps tables. relies on leaps table double link
 * list structure for navigating
 * 
 * 
 * @author ab
 * 
 */
public class TableIterator implements Iterator {
    /**
     * interator that was not initialized as "empty" iterator (one or another
     * record was submitted to constractor) will set it to false
     */
    private boolean empty = true;

    // fake record to start iteration to accomodate the first .next() call
    // start record is being assigned to to the rootRecord.right
    private final TableRecord rootRecord = new TableRecord(null);

    private TableRecord currentRecord;

    private TableRecord lastRecord;

    /**
     * constracts an leaps iterator to iterate over a single record. Used for
     * Dominant fact dimention iteration
     * 
     * @param record
     *            to iterate over
     */
    protected TableIterator() {
        this.empty = true;
    }

    /**
     * constracts an leaps iterator to iterate over a single record. Used for
     * Dominant fact dimention iteration
     * 
     * @param record
     *            to iterate over
     */
    protected TableIterator(TableRecord record) {
        this.empty = false;
        this.rootRecord.right = record;
        this.currentRecord = this.rootRecord;
        this.lastRecord = record;
    }

    protected TableIterator(TableRecord startRecord,
            TableRecord currentRecord, TableRecord lastRecord) {
        this.empty = false;
        this.rootRecord.right = startRecord;
        if (currentRecord.left != null) {
            this.currentRecord = currentRecord.left;
        } else {
            this.currentRecord = this.rootRecord;
        }
        this.lastRecord = lastRecord;
    }

    
    /**
     * single object iterator
     * 
     * @return table iterator
     */
    public static TableIterator baseFactIterator(Object object) {
        return new TableIterator(new TableRecord(object));
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public void reset() {
        this.currentRecord = this.rootRecord;
    }

    public boolean hasNext() {
        if (!this.empty) {
            return this.currentRecord != this.lastRecord;
        } else {
            return false;
        }
    }

    public Object next() {
        this.currentRecord = this.currentRecord.right;
        return this.currentRecord.object;
    }

    public Object current() {
        return this.currentRecord.object;
    }

    public Object peekNext() {
        return this.currentRecord.right.object;
    }

    public void remove() {
    }
}
