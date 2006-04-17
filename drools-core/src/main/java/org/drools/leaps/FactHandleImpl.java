package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.drools.FactHandle;
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
    private Set     activatedTuples   = null;

    private List    notTuples         = null;

    private List    existsTuples      = null;

    private Set     logicalJustifiers = null;

    private boolean logicalyDependent = false;

    private int     dependencyCount   = 0;

    /**
     * actual object that is asserted to the system no getters just a direct
     * access to speed things up
     */
    public FactHandleImpl(long id,
                          Object object) {
        super( id,
               object );
    }

    void addActivatedTuple(LeapsTuple tuple) {
        if ( this.activatedTuples == null ) {
            this.activatedTuples = new HashSet();
        }
        this.activatedTuples.add( tuple );
    }

    void addNotTuple(LeapsTuple tuple,
                     int index) {
        if ( this.notTuples == null ) {
            this.notTuples = new LinkedList();
        }
        this.notTuples.add( new FactHandleTupleAssembly( tuple,
                                                         index ) );
    }

    void addExistsTuple(LeapsTuple tuple,
                        int index) {
        if ( this.existsTuples == null ) {
            this.existsTuples = new LinkedList();
        }
        this.existsTuples.add( new FactHandleTupleAssembly( tuple,
                                                            index ) );
    }

    Iterator getActivatedTuples() {
        if ( this.activatedTuples != null ) {
            return this.activatedTuples.iterator();
        }
        return null;
    }

    Iterator getNotTupleAssemblies() {
        if ( this.notTuples != null ) {
            return this.notTuples.iterator();
        }
        return null;
    }

    Iterator getExistsTupleAssemblies() {
        if ( this.existsTuples != null ) {
            return this.existsTuples.iterator();
        }
        return null;
    }

    void addLogicalDependency(LeapsTuple tuple) {
        if ( this.logicalJustifiers == null ) {
            this.logicalyDependent = true;
            this.logicalJustifiers = new HashSet();
        }
        this.logicalJustifiers.add( tuple );

        this.dependencyCount++;
    }

    void removeLogicalDependency(LeapsTuple tuple) {
        if ( this.dependencyCount > 0 ) {
            this.logicalJustifiers.remove( tuple );
        }
        this.dependencyCount--;
    }

    void removeAllLogicalDependencies() {
        if ( this.dependencyCount > 0 ) {
            for ( Iterator it = this.logicalJustifiers.iterator(); it.hasNext(); ) {
                this.removeLogicalDependency( (LeapsTuple) it.next() );
            }
        }
    }

    boolean isLogicalyValid() {
        if ( this.logicalyDependent ) {
            return this.dependencyCount != 0;
        }
        return true;
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
}
