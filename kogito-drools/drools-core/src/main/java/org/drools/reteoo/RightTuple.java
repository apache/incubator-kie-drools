/*
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.RightTupleList;

public class RightTuple
    implements
    Entry {
    protected InternalFactHandle handle;

    private RightTuple           handlePrevious;
    private RightTuple           handleNext;

    private RightTupleList       memory;

    private Entry                previous;
    private Entry                next;

    public LeftTuple             firstChild;
    public LeftTuple             lastChild;

    private LeftTuple            blocked;

    protected RightTupleSink     sink;

    public RightTuple() {

    }
    
    public RightTuple(InternalFactHandle handle) {
        // This constructor is here for DSL testing
        this.handle = handle;
    }

    public RightTuple(InternalFactHandle handle,
                      RightTupleSink sink) {
        this.handle = handle;
        this.sink = sink;

        RightTuple last = handle.getLastRightTuple();
        if ( last == null ) {
            // no other RightTuples, just add.
            handle.setFirstRightTuple( this );
            handle.setLastRightTuple( this );
        } else {
            // add to end of RightTuples on handle
            this.handlePrevious = last;
            last.setHandleNext( this );
            this.handle.setLastRightTuple( this );
        }
    }

    public RightTupleSink getRightTupleSink() {
        return this.sink;
    }
    
    public void reAdd() {
        RightTuple last = handle.getLastRightTuple();
        if ( last == null ) {
            // node other RightTuples, just add.
            handle.setFirstRightTuple( this );
            handle.setLastRightTuple( this );
        } else {
            this.handleNext = null; // null this in case it was set when this RightTuple was last used
            // add to end of RightTuples on handle
            this.handlePrevious = last;
            last.setHandleNext( this );
            this.handle.setLastRightTuple( this );
        }        
    }

    public void unlinkFromRightParent() {
        RightTuple previousParent = this.handlePrevious;
        RightTuple nextParent = this.handleNext;

        if ( previousParent != null && nextParent != null ) {
            // remove  from middle
            this.handlePrevious.handleNext = nextParent;
            this.handleNext.handlePrevious = previousParent;
        } else if ( nextParent != null ) {
            // remove from first
            this.handleNext.handlePrevious = null;
            this.handle.setFirstRightTuple( this.handleNext );
        } else if ( previousParent != null ) {
            // remove from end
            this.handlePrevious.handleNext = null;
            this.handle.setLastRightTuple( this.handlePrevious );
        } else {
            // single remaining item, no previous or next
            this.handle.setFirstRightTuple( null );
            this.handle.setLastRightTuple( null );
        }

        this.handle = null;
        this.handlePrevious = null;
        this.handleNext = null;
        this.blocked = null;
        this.previous = null;
        this.next = null;
        this.memory = null;
        this.firstChild = null;
        this.lastChild = null;
        this.sink = null;
    }

    public InternalFactHandle getFactHandle() {
        return this.handle;
    }

    public LeftTuple getBlocked() {
        return this.blocked;
    }
    
    public void nullBlocked() {
        this.blocked = null;
    }

    public void addBlocked(LeftTuple leftTuple) {
        if ( this.blocked != null && leftTuple != null ) {
            leftTuple.setBlockedNext( this.blocked );
            this.blocked.setBlockedPrevious( leftTuple );
        }
        this.blocked = leftTuple;
    }

    public void removeBlocked(LeftTuple leftTuple) {
        LeftTuple previous = (LeftTuple) leftTuple.getBlockedPrevious();
        LeftTuple next = (LeftTuple) leftTuple.getBlockedNext();
        if ( previous != null && next != null ) {
            //remove  from middle
            previous.setBlockedNext( next );
            next.setBlockedPrevious( previous );
        } else if ( next != null ) {
            //remove from first
            this.blocked = next;
            next.setBlockedPrevious( null );
        } else if ( previous != null ) {
            //remove from end
            previous.setBlockedNext( null );
        } else {
            this.blocked = null;
        }
    }

    public RightTupleList getMemory() {
        return memory;
    }

    public void setMemory(RightTupleList memory) {
        this.memory = memory;
    }

    public Entry getPrevious() {
        return previous;
    }

    public void setPrevious(Entry previous) {
        this.previous = previous;
    }

    public RightTuple getHandlePrevious() {
        return handlePrevious;
    }

    public void setHandlePrevious(RightTuple handlePrevious) {
        this.handlePrevious = handlePrevious;
    }

    public RightTuple getHandleNext() {
        return handleNext;
    }

    public void setHandleNext(RightTuple handleNext) {
        this.handleNext = handleNext;
    }

    public Entry getNext() {
        return next;
    }

    public void setNext(Entry next) {
        this.next = next;
    }

    //    public LeftTuple getFirstChild() {
    //        return firstChild;
    //    }
    //
    //    public void setFirstChildren(LeftTuple betachildren) {
    //        this.firstChild = betachildren;
    //    }

    public int hashCode() {
        return this.handle.hashCode();
    }

    public String toString() {
        return this.handle.toString() + "\n";
    }

    public boolean equals(RightTuple other) {
        // we know the object is never null and always of the  type ReteTuple
        if ( other == this ) {
            return true;
        }

        // A ReteTuple is  only the same if it has the same hashCode, factId and parent
        if ( (other == null) || (hashCode() != other.hashCode()) ) {
            return false;
        }

        return this.handle == other.handle;
    }

    public boolean equals(Object object) {
        return equals( (RightTuple) object );
    }
}
