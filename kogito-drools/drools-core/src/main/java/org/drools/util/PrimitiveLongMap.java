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
import java.util.Arrays;
import java.util.Collection;



/**
 * 
 * @author Mark Proctor
 */
public class PrimitiveLongMap
    implements
    Serializable {
    private final static Object NULL = new Serializable() {
                                     };

    private final int           indexIntervals;
    private final int           intervalShifts;
    private final int           midIntervalPoint;
    private final int           tableSize;
    private final int           shifts;
    private final int           doubleShifts;
    private final Page          firstPage;
    private Page                lastPage;
    private int                 lastPageId;
    private long                maxKey;
    private Page[]              pageIndex;
    private int                 totalSize;

    public PrimitiveLongMap() {
        this( 32,
              8 );
    }

    public PrimitiveLongMap(int tableSize) {
        this( tableSize,
              8 );
    }

    public PrimitiveLongMap(int tableSize,
                            int indexIntervals) {
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

    public Object put(long key,
                      Object value) {
        if ( key < 0 ) {
            throw new IllegalArgumentException( "-ve keys not supported: " + key );
        }

        // NULL is a placeholder to show the key exists
        // but contains a null value
        if ( value == null ) {
            value = PrimitiveLongMap.NULL;
        }

        Page page = findPage( key );

        Object oldValue = page.put( key,
                                    value );

        if ( oldValue == null ) {
            this.totalSize++;
        }

        return oldValue;
    }

    public Object remove(long key) {
        if ( key > this.maxKey || key < 0 ) {
            return null;
        }

        Page page = findPage( key );

        Object oldValue = page.put( key,
                                    null );

        if ( this.lastPageId != 0 && this.lastPage.isEmpty() ) {
            shrinkPages( this.lastPageId );
        }

        if ( oldValue != null ) {
            this.totalSize--;
        }

        return oldValue;
    }

    public Object get(long key) {
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

    public int size() {
        return this.totalSize;
    }

    public Collection values() {
        CompositeCollection collection = new CompositeCollection();
        Page page = this.firstPage;

        while ( page != null && page.getPageId() <= this.lastPageId ) {
            collection.addComposited( Arrays.asList( page.getValues() ) );
            page = page.getNextSibling();
        }
        return collection;
    }

    public boolean containsKey(long key) {
        if ( key < 0 ) {
            return false;
        }

        return get( key ) != null;
    }

    /**
     * Expand index to accomodate given pageId Create empty TopNodes
     */
    public Page expandPages(int toPageId) {
        for ( int x = this.lastPageId; x < toPageId; x++ ) {
            this.lastPage = new Page( this.lastPage,
                                      ++this.lastPageId,
                                      this.tableSize );
            // index interval, so expand index
            if ( this.lastPage.getPageId() % this.indexIntervals == 0 ) {
                int newSize = this.pageIndex.length + 1;
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
    public void shrinkPages(int toPageId) {
        for ( int x = this.lastPageId; x >= toPageId; x-- ) {
            // last page is on index so shrink index
            if ( (this.lastPageId) % this.indexIntervals == 0 && this.lastPageId != 0 ) {
                resizeIndex( this.pageIndex.length - 1 );
            }

            Page page = this.lastPage.getPreviousSibling();
            page.setNextSibling( null );
            this.lastPage.clear();
            this.lastPage = page;
            this.lastPageId = page.getPageId();

        }
    }

    public void resizeIndex(int newSize) {
        Page[] newIndex = new Page[newSize];
        System.arraycopy( this.pageIndex,
                          0,
                          newIndex,
                          0,
                          newSize - 1 );
        this.pageIndex = newIndex;
    }

    private Page findPage(long key) {
        // determine Page
        int pageId = (int) key >> this.doubleShifts;
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
            int offset = pageId >> this.intervalShifts;
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

    private static class Page
        implements
        Serializable {
        private final int  pageSize;
        private final int  pageId;
        private final int  shifts;
        private final int  tableSize;
        private Page       nextSibling;
        private Page       previousSibling;
        private Object[][] tables;
        private int        filledSlots;

        Page(Page previousSibling,
             int pageId,
             int tableSize) {
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

        public int getPageId() {
            return this.pageId;
        }

        void setNextSibling(Page nextSibling) {
            this.nextSibling = nextSibling;
        }

        public Page getNextSibling() {
            return this.nextSibling;
        }

        void setPreviousSibling(Page previousSibling) {
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

            // determine page
            int page = (int) key >> this.shifts;

            // determine offset
            int offset = page << this.shifts;

            // tables[page][slot]
            return this.tables[page][(int) key - offset];
        }

        public Object put(long key,
                          Object newValue) {
            if ( this.tables == null ) {
                // initiate tree;
                this.tables = new Object[this.tableSize][this.tableSize];
            }

            // normalise key
            key -= this.pageSize * this.pageId;

            // determine page
            int table = (int) key >> this.shifts;

            // determine offset
            int offset = table << this.shifts;

            // determine slot
            int slot = (int) key - offset;

            // get old value
            Object oldValue = this.tables[table][slot];
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
            Object[] values = new Object[this.filledSlots];
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