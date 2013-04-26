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

package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.index.RightTupleList;
import org.drools.core.spi.PropagationContext;

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
    
    protected short               stageType;
    protected RightTuple          stageNext;
    protected RightTuple          stagePrevious;

    private RightTuple            tempNextRightTuple;
    private RightTupleMemory      tempRightTupleMemory;
    private LeftTuple             tempBlocked;

    private PropagationContext    propagationContext;

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

        // add to end of RightTuples on handle
        handle.addRightTupleInPosition( this );
    }

    public RightTupleSink getRightTupleSink() {
        return this.sink;
    }
    
    public void reAdd() {
        handle.addLastRightTuple( this );
    }

    public void unlinkFromRightParent() {
        this.handle.removeRightTuple( this );
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
    
    public void setLeftTuple(LeftTuple leftTuple) {
        this.blocked = leftTuple;
    }
    
    public LeftTuple getLeftTuple() {
        return this.blocked;
    }

    public void addBlocked(LeftTuple leftTuple) {
        if ( this.blocked != null && leftTuple != null ) {
            leftTuple.setBlockedNext( this.blocked );
            this.blocked.setBlockedPrevious( leftTuple );
        }
        this.blocked = leftTuple;
    }

    public void removeBlocked(LeftTuple leftTuple) {
        LeftTuple previous =  leftTuple.getBlockedPrevious();
        LeftTuple next =  leftTuple.getBlockedNext();
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
        leftTuple.clearBlocker();
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

    public LeftTuple getFirstChild() {
        return firstChild;
    }

    public void setFirstChild(LeftTuple firstChild) {
        this.firstChild = firstChild;
    }

    public LeftTuple getLastChild() {
        return lastChild;
    }

    public void setLastChild(LeftTuple lastChild) {
        this.lastChild = lastChild;
    }
    
    public short getStagedType() {
        return this.stageType;
    }

    public void setStagedType(short stagedType) {
        this.stageType = stagedType;
    }

    public RightTuple getStagedNext() {
        return stageNext;
    }

    public void setStagedNext(RightTuple stageNext) {
        this.stageNext = stageNext;
    }

    public RightTuple getStagedPrevious() {
        return stagePrevious;
    }

    public void setStagePrevious(RightTuple stagePrevious) {
        this.stagePrevious = stagePrevious;
    }
    
    public void clearStaged() {
        this.stageType = LeftTuple.NONE;
        this.stageNext = null;
        this.stagePrevious = null;
        this.tempNextRightTuple = null;
        this.tempRightTupleMemory = null;
        this.tempBlocked = null;
    }

    public LeftTuple getTempBlocked() {
        return tempBlocked;
    }

    public void setTempBlocked(LeftTuple tempBlocked) {
        this.tempBlocked = tempBlocked;
    }

    public RightTuple getTempNextRightTuple() {
        return tempNextRightTuple;
    }

    public void setTempNextRightTuple(RightTuple tempNextRightTuple) {
        this.tempNextRightTuple = tempNextRightTuple;
    }

    public RightTupleMemory getTempRightTupleMemory() {
        return tempRightTupleMemory;
    }

    public void setTempRightTupleMemory(RightTupleMemory tempRightTupleMemory) {
        this.tempRightTupleMemory = tempRightTupleMemory;
    }

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

    public PropagationContext getPropagationContext() {
        return propagationContext;
    }

    public void setPropagationContext(PropagationContext propagationContext) {
        this.propagationContext = propagationContext;
    }

    public void clear() {
        this.previous = null;
        this.next = null;
        this.memory = null;
    }
}
