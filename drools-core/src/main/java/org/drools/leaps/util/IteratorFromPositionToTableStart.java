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

import java.util.NoSuchElementException;

/**
 * Leaps specific iterator for leaps tables. relies on leaps table double link
 * list structure for navigation
 * 
 * @author Alexander Bagerman
 * 
 */
public class IteratorFromPositionToTableStart implements TableIterator {
    /**
     * interator that was not initialized as "empty" iterator (one or another
     * record was submitted to constractor) will set it to false
     */
    TableRecord firstRecord;

    TableRecord currentRecord;

    TableRecord nextRecord;

    /**
     * constracts an leaps iterator to iterate over a single record. Used for
     * Dominant fact dimention iteration
     * 
     * @param record
     *            to iterate over
     */

    protected IteratorFromPositionToTableStart(final TableRecord record) {
        this.firstRecord = record;
        this.currentRecord = null;
        this.nextRecord = this.firstRecord;
    }

    protected IteratorFromPositionToTableStart(final TableRecord startRecord,
            final TableRecord currentRecord) {
        this.firstRecord = startRecord;
        this.nextRecord = currentRecord;
        this.currentRecord = null;
    }

    public boolean isEmpty() {
        return this.firstRecord == null;
    }

    public void reset() {
        this.currentRecord = null;
        this.nextRecord = this.firstRecord;
    }

    public boolean hasNext() {
        return this.nextRecord != null;
    }

    public Object next() {
        this.currentRecord = this.nextRecord;
        if (this.currentRecord != null) {
            this.nextRecord = this.currentRecord.right;
        }
        else {
            throw new NoSuchElementException( "No more elements to return" );
        }
        return this.currentRecord.object;
    }

    public Object peekNext() {
        return this.nextRecord.object;
    }

    public void remove() {
    }
}
