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
public class ConstrainedIteratorFromPositionToTableStart extends IteratorFromPositionToTableStart {
    private boolean         finishInitialPass = false;

    final WorkingMemory     workingMemory;

    final ColumnConstraints constraints;

    private TableRecord     currentTableRecord;

    protected ConstrainedIteratorFromPositionToTableStart(final WorkingMemory workingMemory,
            final ColumnConstraints constraints,
            final TableRecord startRecord,
            final TableRecord currentRecord) {
        super( null );
        this.workingMemory = workingMemory;
        this.constraints = constraints;
        this.currentTableRecord = startRecord;
        boolean done = false;
        boolean reachCurrentRecord = false;
        while (!done && this.currentTableRecord != null && !this.finishInitialPass) {
            if (!reachCurrentRecord && this.currentTableRecord == currentRecord) {
                reachCurrentRecord = true;
            }
            else {
                if (this.constraints.isAllowedAlpha( (InternalFactHandle) this.currentTableRecord.object,
                                                     null,
                                                     this.workingMemory )) {
                    this.add( this.currentTableRecord.object );
                }
                if (reachCurrentRecord && !this.isEmpty( )) {
                    done = true;
                }
                if (this.currentTableRecord.right == null) {
                    this.finishInitialPass = true;
                }
                this.currentTableRecord = this.currentTableRecord.right;
            }
        }
    }

    private void add( final Object object ) {
        final TableRecord record = new TableRecord( object );
        if (this.firstRecord == null) {
            this.firstRecord = record;
            this.currentRecord = record;
        }
        else {
            this.currentRecord.right = record;
            record.left = this.currentRecord;
            this.currentRecord = record;
        }

        this.nextRecord = this.currentRecord;
    }

    public boolean hasNext() {
        if (!this.finishInitialPass) {
            if (this.nextRecord == null) {
                boolean found = false;
                while (!found && this.currentTableRecord != null) {
                    if (this.constraints.isAllowedAlpha( (InternalFactHandle) this.currentTableRecord.object,
                                                         null,
                                                         this.workingMemory )) {
                        this.add( this.currentTableRecord.object );
                        found = true;
                    }
                    if (this.currentTableRecord == null) {
                        this.finishInitialPass = true;
                    }
                    this.currentTableRecord = this.currentTableRecord.right;
                }

                return found;
            }
            else {
                return true;
            }
        }
        else {
            return super.hasNext( );
        }
    }
}
