/**
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Mark Proctor
 */
public class PrimitiveLongMap
    implements
    Externalizable {
    /**
     *
     */
    private static final long   serialVersionUID = 510l;

    private final static Object NULL             = new Serializable() {

                                                     /**
                                                      *
                                                      */
                                                     private static final long serialVersionUID = 510l;
                                                 };

    private int           indexIntervals;
    private int           intervalShifts;
    private int           midIntervalPoint;
    private int           tableSize;
    private int           shifts;
    private int           doubleShifts;
    private Page                firstPage;
    private Page                lastPage;
    private int                 lastPageId;
    private long                maxKey;
    private Page[]              pageIndex;
    private int                 totalSize;

    public PrimitiveLongMap() {
        this( 32,
              8 );
    }

    public PrimitiveLongMap(final int tableSize) {
        this( tableSize,
              8 );
    }

    public PrimitiveLongMap(final int tableSize,
                            final int indexIntervals) {
        // determine number of shifts for intervals
        int i = 1;
        int size = 2;
        while ( size < indexIntervals ) {
            size <<= 1;
            ++i;
        }
        this.indexIntervals = size;
        this.intervalShifts = i;

        // determine number of shifts for tableSize
        i = 1;
        size = 2;
        while ( size < tableSize ) {
            size <<= 1;
            ++i;
        }
        this.tableSize = size;
        this.shifts = i;
        this.doubleShifts = this.shifts << 1;

        // determine mid point of an interval
        this.midIntervalPoint = ((this.tableSize << this.shifts) << this.intervalShifts) >> 1;

        this.lastPageId = 0;

        init();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        indexIntervals  = in.readInt();
        intervalShifts  = in.readInt();
        midIntervalPoint  = in.readInt();
        tableSize  = in.readInt();
        shifts  = in.readInt();
        doubleShifts  = in.readInt();
        firstPage   = (Page)in.readObject();
        lastPage    = (Page)in.readObject();
        lastPageId  = in.readInt();
        maxKey  = in.readLong();
        pageIndex   = (Page[])in.readObject();
        totalSize  = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(indexIntervals);
        out.writeInt(intervalShifts);
        out.writeInt(midIntervalPoint);
        out.writeInt(tableSize);
        out.writeInt(shifts);
        out.writeInt(doubleShifts);
        out.writeObject(firstPage);
        out.writeObject(lastPage);
        out.writeInt(lastPageId);
        out.writeLong(maxKey);
        out.writeObject(pageIndex);
        out.writeInt(totalSize);
    }

    private void init() {
        // instantiate the first page
        // previous sibling of first page is null
        // next sibling of last page is null
        this.firstPage = new Page( null,
                                   this.lastPageId,
                                   this.tableSize );
        this.maxKey = this.lastPageId + 1 << this.doubleShifts;
        // create an index of one
        this.pageIndex = new Page[]{this.firstPage};

        // our first page is also our last page
        this.lastPage = this.firstPage;
    }

    public void clear() {
        init();
    }

    public boolean isEmpty() {
        return this.totalSize == 0;
    }

    public Object put(final long key,
                      Object value) {
        if ( key < 0 ) {
            throw new IllegalArgumentException( "-ve keys not supported: " + key );
        }

        // NULL is a placeholder to show the key exists
        // but contains a null value
        if ( value == null ) {
            value = PrimitiveLongMap.NULL;
        }

        final Page page = findPage( key );

        final Object oldValue = page.put( key,
                                          value );

        if ( oldValue == null ) {
            this.totalSize++;
        }

        return oldValue;
    }

    public Object remove(final long key) {
        if ( key > this.maxKey || key < 0 ) {
            return null;
        }

        final Page page = findPage( key );

        final Object oldValue = page.put( key,
                                          null );

        if ( this.lastPageId != 0 && this.lastPage.isEmpty() ) {
            shrinkPages( this.lastPageId );
        }

        if ( oldValue != null ) {
            this.totalSize--;
        }

        return oldValue;
    }

    public Object get(final long key) {
        if ( key > this.maxKey || key < 0 ) {
            return null;
        }

        Object value = findPage( key ).get( key );

        // NULL means the key exists, so return a real null
        if ( value == PrimitiveLongMap.NULL ) {
            value = null;
        }
        return value;
    }

    /**
     * gets the next populated key, after the given key position.
     * @param key
     * @return
     */
    public long getNext(long key) {
        final int currentPageId = (int) key >> this.doubleShifts;
        final int nextPageId = (int) (key+1) >> this.doubleShifts;

        if ( currentPageId != nextPageId ) {
            Page page = findPage( key + 1);
            while ( page.isEmpty() ) {
                page = page.getNextSibling();
            }
            key = this.doubleShifts << page.getPageId();
        } else {
            key += 1;
        }

        while ( !containsKey( key ) && key <= this.maxKey ) {
            key++;
        }

        if ( key > this.maxKey ) {
            key -= 1;
        }

        return key;
    }

    public int size() {
        return this.totalSize;
    }

    public Collection values() {
        final CompositeCollection collection = new CompositeCollection();
        Page page = this.firstPage;

        while ( page != null && page.getPageId() <= this.lastPageId ) {
            collection.addComposited( Arrays.asList( page.getValues() ) );
            page = page.getNextSibling();
        }
        return collection;
    }

    public boolean containsKey(final long key) {
        if ( key < 0 ) {
            return false;
        }

        return get( key ) != null;
    }

    /**
     * Expand index to accomodate given pageId Create empty TopNodes
     */
    public Page expandPages(final int toPageId) {
        for ( int x = this.lastPageId; x < toPageId; x++ ) {
            this.lastPage = new Page( this.lastPage,
                                      ++this.lastPageId,
                                      this.tableSize );
            // index interval, so expand index
            if ( this.lastPage.getPageId() % this.indexIntervals == 0 ) {
                final int newSize = this.pageIndex.length + 1;
                resizeIndex( newSize );
                this.pageIndex[newSize - 1] = this.lastPage;
            }
        }
        this.maxKey = (this.lastPageId + 1 << this.doubleShifts) - 1;
        return this.lastPage;
    }

    /**
     * Shrink index to accomodate given pageId
     */
    public void shrinkPages(final int toPageId) {
        for ( int x = this.lastPageId; x >= toPageId; x-- ) {
            // last page is on index so shrink index
            if ( (this.lastPageId) % this.indexIntervals == 0 && this.lastPageId != 0 ) {
                resizeIndex( this.pageIndex.length - 1 );
            }

            final Page page = this.lastPage.getPreviousSibling();
            page.setNextSibling( null );
            this.lastPage.clear();
            this.lastPage = page;
            this.lastPageId = page.getPageId();

        }
    }

    public void resizeIndex(final int newSize) {
        final Page[] newIndex = new Page[newSize];
        System.arraycopy( this.pageIndex,
                          0,
                          newIndex,
                          0,
                          (newSize > this.pageIndex.length) ? this.pageIndex.length : newSize );
        this.pageIndex = newIndex;
    }

    private Page findPage(final long key) {
        // determine Page
        final int pageId = (int) key >> this.doubleShifts;
        Page page;

        // if pageId is lastNodeId use lastNode reference
        if ( pageId == this.lastPageId ) {
            page = this.lastPage;
        }
        // if pageId is zero use first page reference
        else if ( pageId == 0 ) {
            page = this.firstPage;
        }
        // if pageId is greater than lastTopNodeId need to expand
        else if ( pageId > this.lastPageId ) {
            page = expandPages( pageId );
        } else {
            // determine offset
            final int offset = pageId >> this.intervalShifts;
            // are we before or after the halfway point of an index interval
            if ( (offset != (this.pageIndex.length - 1)) && ((key - (offset << this.intervalShifts << this.doubleShifts)) > this.midIntervalPoint) ) {
                // after so go to next node index and go backwards
                page = this.pageIndex[offset + 1];
                while ( page.getPageId() != pageId ) {
                    page = page.getPreviousSibling();
                }
            } else {
                // before so go to node index and go forwards
                page = this.pageIndex[offset];
                while ( page.getPageId() != pageId ) {
                    page = page.getNextSibling();
                }
            }
        }

        return page;
    }

    public static class Page
        implements
        Externalizable {
        /**
         *
         */
        private static final long serialVersionUID = 510l;
        private int         pageSize;
        private int         pageId;
        private int         shifts;
        private int         tableSize;
        private Page              nextSibling;
        private Page              previousSibling;
        private Object[][]        tables;
        private int               filledSlots;

        public Page() {

        }

        Page(final Page previousSibling,
             final int pageId,
             final int tableSize) {
            // determine number of shifts
            int i = 1;
            int size = 2;
            while ( size < tableSize ) {
                size <<= 1;
                ++i;
            }
            // make sure table size is valid
            this.tableSize = size;
            this.shifts = i;

            // create bi-directional link
            this.previousSibling = previousSibling;
            if ( this.previousSibling != null ) {
                this.previousSibling.setNextSibling( this );
            }
            this.pageId = pageId;
            this.pageSize = tableSize << this.shifts;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            pageSize    = in.readInt();
            pageId      = in.readInt();
            shifts      = in.readInt();
            tableSize   = in.readInt();
            nextSibling = (Page)in.readObject();
            previousSibling = (Page)in.readObject();
            tables      = (Object[][])in.readObject();
            filledSlots = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(pageSize);
            out.writeInt(pageId);
            out.writeInt(shifts);
            out.writeInt(tableSize);
            out.writeObject(nextSibling);
            out.writeObject(previousSibling);
            out.writeObject(tables);
            out.writeInt(filledSlots);
        }

        public int getPageId() {
            return this.pageId;
        }

        void setNextSibling(final Page nextSibling) {
            this.nextSibling = nextSibling;
        }

        public Page getNextSibling() {
            return this.nextSibling;
        }

        void setPreviousSibling(final Page previousSibling) {
            this.previousSibling = previousSibling;
        }

        public Page getPreviousSibling() {
            return this.previousSibling;
        }

        public Object get(long key) {
            if ( this.tables == null ) {
                return null;
            }
            // normalise key
            key -= this.pageSize * this.pageId;

            // determine table
            final int table = (int) key >> this.shifts;

            // determine offset
            final int offset = table << this.shifts;

            // tables[table][slot]
            return this.tables[table][(int) key - offset];
        }

        public Object put(long key,
                          final Object newValue) {
            if ( this.tables == null ) {
                // initiate tree;
                this.tables = new Object[this.tableSize][this.tableSize];
            }

            // normalise key
            key -= this.pageSize * this.pageId;

            // determine table
            final int table = (int) key >> this.shifts;

            // determine offset
            final int offset = table << this.shifts;

            // determine slot
            final int slot = (int) key - offset;

            // get old value
            final Object oldValue = this.tables[table][slot];
            this.tables[table][slot] = newValue;

            // update number of empty cells for TopNode
            if ( oldValue == null && newValue != null ) {
                this.filledSlots++;
            } else if ( oldValue != null && newValue == null ) {
                this.filledSlots--;
            }

            // if this page contains no values then null the array
            // to allow it to be garbage collected
            if ( this.filledSlots == 0 ) {
                this.tables = null;
            }

            return oldValue;
        }

        Object[][] getTables() {
            return this.tables;
        }

        Object[] getValues() {
            final Object[] values = new Object[this.filledSlots];
            if ( values.length == 0 ) {
                return values;
            }
            int x = 0;
            Object value;
            for ( int i = 0; i < this.tableSize; i++ ) {
                for ( int j = 0; j < this.tableSize; j++ ) {
                    value = this.tables[i][j];
                    if ( value != null ) {
                        // swap NULL out placeholder
                        // Also filter out InitialFact
                        if ( value == PrimitiveLongMap.NULL ) {
                            value = null;
                        }
                        values[x] = value;
                        x++;
                    }
                }
            }
            return values;
        }

        public boolean isEmpty() {
            return this.filledSlots == 0;
        }

        void clear() {
            this.previousSibling = null;
            this.nextSibling = null;
            this.tables = null;
        }
    }
}
