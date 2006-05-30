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

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.leaps.ColumnConstraints;

/**
 * this class is for multi pass iterations to sort out facts that do not satisfy
 * alpha nodes
 * 
 * previous to the left
 * next to the right
 * 
 * @author Alexander Bagerman
 * 
 */
public class ConstrainedFactTableIterator
    implements
    TableIterator {
    private boolean         finishInitialPass = false;

    final WorkingMemory     workingMemory;

    final ColumnConstraints constraints;

    private int             size              = 0;

    private TableRecord     firstRecord;

    private TableRecord     lastRecord;

    private TableRecord     currentRecord;

    private TableRecord     nextRecord;

    private TableRecord     currentTableRecord;

    private TableRecord     lastTableRecord;

    protected ConstrainedFactTableIterator(final WorkingMemory workingMemory,
                                           final ColumnConstraints constraints,
                                           final TableRecord startRecord,
                                           final TableRecord currentRecord,
                                           final TableRecord lastRecord) {
        this.workingMemory = workingMemory;
        this.constraints = constraints;
        this.lastTableRecord = lastRecord;
        this.currentTableRecord = startRecord;
        boolean done = false;
        boolean reachCurrentRecord = false;
        while ( !done && this.currentTableRecord != null && !this.finishInitialPass ) {
            if ( !reachCurrentRecord && this.currentTableRecord == currentRecord ) {
                reachCurrentRecord = true;
            } else {
                if ( this.constraints.isAllowedAlpha( (InternalFactHandle) this.currentTableRecord.object,
                                                      null,
                                                      this.workingMemory ) ) {
                    this.add( this.currentTableRecord.object );
                }
                if ( reachCurrentRecord && !this.isEmpty() ) {
                    done = true;
                }
                if ( this.currentTableRecord == this.lastTableRecord ) {
                    this.finishInitialPass = true;
                }
                this.currentTableRecord = this.currentTableRecord.right;
            }
        }
        // 
        this.nextRecord = this.lastRecord;
    }

    private void add(final Object object) {
        final TableRecord record = new TableRecord( object );
        if ( this.firstRecord == null ) {
            this.firstRecord = record;
            this.lastRecord = record;
        } else {
            this.lastRecord.right = record;
            record.left = this.lastRecord;
            this.lastRecord = record;
        }
        this.size++;
    }

    public boolean isEmpty() {
        return this.firstRecord == null;
    }

    public void reset() {
        this.currentRecord = null;
        this.nextRecord = this.firstRecord;
    }

    public Object next() {
        this.currentRecord = this.nextRecord;
        if ( this.currentRecord != null ) {
            this.nextRecord = this.currentRecord.right;
        } else {
            throw new NoSuchElementException( "No more elements to return" );
        }
        return this.currentRecord.object;
    }

    public Object current() {
        return this.currentRecord.object;
    }

    public Object peekNext() {
        return this.nextRecord.object;
    }

    public void remove() {
    }

    public boolean hasNext() {
        if ( !this.finishInitialPass ) {
            if ( this.nextRecord == null ) {
                boolean found = false;
                while ( !found && this.currentTableRecord != null ) {
                    if ( this.constraints.isAllowedAlpha( (InternalFactHandle) this.currentTableRecord.object,
                                                          null,
                                                          this.workingMemory ) ) {
                        this.add( this.currentTableRecord.object );
                        found = true;
                    }
                    if ( this.currentTableRecord == this.lastTableRecord ) {
                        this.finishInitialPass = true;
                    }
                    this.currentTableRecord = this.currentTableRecord.right;
                }
                // 
                if ( found ) {
                    this.nextRecord = this.lastRecord;
                }
                return found;
            }
            return true;
        } else {
            return this.nextRecord != null;
        }
    }
}