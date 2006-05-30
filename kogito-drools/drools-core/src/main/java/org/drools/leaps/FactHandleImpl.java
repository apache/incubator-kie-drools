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

import org.drools.FactHandle;
import org.drools.common.EqualityKey;
import org.drools.common.InternalFactHandle;

/**
 * class container for each object asserted / retracted into the system
 * 
 * @author Alexander Bagerman
 * 
 */
public class FactHandleImpl extends Handle
    implements
    InternalFactHandle {
    /**
     * 
     */
    private static final long serialVersionUID = -6453652241440771664L;

    private List              activatedTuples  = null;

    private List              notTuples        = null;

    private List              existsTuples     = null;

    private EqualityKey       key              = null;    

    /**
     * actual object that is asserted to the system no getters just a direct
     * access to speed things up
     */
    public FactHandleImpl(final long id,
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
        this.notTuples.add( new FactHandleTupleAssembly( tuple,
                                                         index ) );
    }

    protected void addExistsTuple(final LeapsTuple tuple,
                                  final int index) {
        if ( this.existsTuples == null ) {
            this.existsTuples = new LinkedList();
        }
        this.existsTuples.add( new FactHandleTupleAssembly( tuple,
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

    /**
     * @see FactHandle
     */
    public String toExternalForm() {
        return "f-" + this.getId();
    }

    /**
     * @see Object
     */
    public String toString() {
        return toExternalForm();
    }

    public EqualityKey getEqualityKey() {
        return this.key;
    }

    public void setEqualityKey(final EqualityKey key) {
        this.key = key;
    }

    public void setObject(final Object object) {
        // TODO Auto-generated method stub
    }

    public void invalidate() {
        // TODO Auto-generated method stub

    }

    public void setRecency(final long recency) {
        // TODO Auto-generated method stub

    }
}
