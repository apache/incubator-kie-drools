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
public class IteratorFromPositionToTableEnd extends IteratorFromPositionToTableStart {
    /**
     * @see IteratorFromPositionToTableStart
     */
    protected IteratorFromPositionToTableEnd(final TableRecord record) {
        super( record );
    }

    /**
     * @see IteratorFromPositionToTableStart
     */
    protected IteratorFromPositionToTableEnd(final TableRecord startRecord,
                                             final TableRecord currentRecord) {
        super( startRecord,
               currentRecord );
    }

    /**
     * the difference here is that we are going to the different direction that
     * base class next() method
     * 
     * @see IteratorFromPositionToTableStart
     * 
     */

    public Object next() {
        this.currentRecord = this.nextRecord;
        if ( this.currentRecord != null ) {
            this.nextRecord = this.currentRecord.left;
        } else {
            throw new NoSuchElementException( "No more elements to return" );
        }
        return this.currentRecord.object;
    }

}
