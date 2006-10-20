package org.drools.leaps;

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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.drools.common.DefaultFactHandle;
import org.drools.leaps.util.Table;

/**
 * class container for each object asserted / retracted into the system
 * 
 * @author Alexander Bagerman
 * 
 */
public class LeapsFactHandle extends DefaultFactHandle {

    private List activatedTuples = null;

    private List notTuples       = null;

    private List existsTuples    = null;

    public LeapsFactHandle(final long id,
                           final Object object) {
        super( id,
               object );
    }

    protected void addActivatedTuple(final LeapsTuple tuple) {
        if ( this.activatedTuples == null ) {
            this.activatedTuples = new LinkedList();
        }
        this.activatedTuples.add( tuple );
    }

    protected void addNotTuple(final LeapsTuple tuple,
                               final int index) {
        if ( this.notTuples == null ) {
            this.notTuples = new LinkedList();
        }
        this.notTuples.add( new FactHandleTupleAssembly( FactHandleTupleAssembly.NOT,
                                                         tuple,
                                                         index ) );
    }

    protected void addExistsTuple(final LeapsTuple tuple,
                                  final int index) {
        if ( this.existsTuples == null ) {
            this.existsTuples = new LinkedList();
        }
        this.existsTuples.add( new FactHandleTupleAssembly( FactHandleTupleAssembly.EXISTS,
                                                            tuple,
                                                            index ) );
    }

    protected Iterator getActivatedTuples() {
        if ( this.activatedTuples != null ) {
            return this.activatedTuples.iterator();
        }
        return null;
    }

    protected Iterator getNotTupleAssemblies() {
        if ( this.notTuples != null ) {
            return this.notTuples.iterator();
        }
        return null;
    }

    protected Iterator getExistsTupleAssemblies() {
        if ( this.existsTuples != null ) {
            return this.existsTuples.iterator();
        }
        return null;
    }

    protected void clearActivatedTuples() {
        this.activatedTuples = null;
    }

    protected void clearExistsTuples() {
        this.existsTuples = null;
    }

    protected void clearNotTuples() {
        this.notTuples = null;
    }

    private LinkedList hashes = null;

    protected void addHash(final Table table) {
        if ( this.hashes == null ) {
            this.hashes = new LinkedList();
        }
        this.hashes.add( table );
    }

    protected void removeFromHash() {
        if ( this.hashes != null ) {
            for ( final Iterator it = this.hashes.iterator(); it.hasNext(); ) {
                ((Table) it.next()).remove( this );
            }
            this.hashes.clear();
        }
    }
}
