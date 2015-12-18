/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.index.TupleList;

public class RightTupleImpl extends BaseTuple implements RightTuple {

    private TupleList            memory;

    private LeftTuple            firstChild;
    private LeftTuple            lastChild;

    private LeftTuple            blocked;

    private RightTuple            tempNextRightTuple;
    private TupleMemory           tempRightTupleMemory;
    private LeftTuple             tempBlocked;

    public RightTupleImpl() { }
    
    public RightTupleImpl(InternalFactHandle handle) {
        // This constructor is here for DSL testing
        setFactHandle( handle );
    }

    public RightTupleImpl(InternalFactHandle handle,
                      RightTupleSink sink) {
        this( handle );
        this.sink = sink;

        // add to end of RightTuples on handle
        handle.addTupleInPosition( this );
    }

    public RightTupleSink getTupleSink() {
        return (RightTupleSink) sink;
    }
    
    public void reAdd() {
        getFactHandle().addLastRightTuple( this );
    }

    public void unlinkFromRightParent() {
        getFactHandle().removeRightTuple( this );
        setFactHandle( null );
        this.handlePrevious = null;
        this.handleNext = null;
        this.blocked = null;
        setPrevious( null );
        setNext( null );
        this.memory = null;
        this.firstChild = null;
        this.lastChild = null;
        this.sink = null;
    }

    public void unlinkFromLeftParent() {

    }

    public LeftTuple getBlocked() {
        return this.blocked;
    }
    
    public void setBlocked(LeftTuple leftTuple) {
        this.blocked = leftTuple;
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

    public TupleList getMemory() {
        return memory;
    }

    public void setMemory(TupleList memory) {
        this.memory = memory;
    }

    public RightTuple getHandlePrevious() {
        return (RightTuple) handlePrevious;
    }

    public RightTuple getHandleNext() {
        return (RightTuple) handleNext;
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
    
    public RightTuple getStagedNext() {
        return (RightTuple) stagedNext;
    }

    public RightTuple getStagedPrevious() {
        return (RightTuple) stagedPrevious;
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

    public TupleMemory getTempRightTupleMemory() {
        return tempRightTupleMemory;
    }

    public void setTempRightTupleMemory(TupleMemory tempRightTupleMemory) {
        this.tempRightTupleMemory = tempRightTupleMemory;
    }

    public int hashCode() {
        return getFactHandle().hashCode();
    }

    public String toString() {
        return getFactHandle().toString() + "\n";
    }

    public boolean equals(Object object) {
        if (!(object instanceof RightTupleImpl)) {
            return false;
        }

        if ( object == this ) {
            return true;
        }

        RightTupleImpl other = (RightTupleImpl) object;
        // A ReteTuple is  only the same if it has the same hashCode, factId and parent
        if ( (other == null) || (hashCode() != other.hashCode()) ) {
            return false;
        }

        return getFactHandle() == other.getFactHandle();
    }

    public void clear() {
        super.clear();
        this.memory = null;
    }

    public void clearStaged() {
        super.clearStaged();
        this.tempNextRightTuple = null;
        this.tempRightTupleMemory = null;
        this.tempBlocked = null;
    }

    @Override
    public Object getObject( int pattern ) {
        return pattern == 0 ? getFactHandle().getObject() : null;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public Object[] toObjects() {
        return new Object[] { getFactHandle().getObject() };
    }

    @Override
    public InternalFactHandle get( int pattern ) {
        return pattern == 0 ? getFactHandle() : null;
    }

    @Override
    public InternalFactHandle[] toFactHandles() {
        return new InternalFactHandle[] { getFactHandle() };
    }

    @Override
    public Tuple getParent() {
        return null;
    }

    @Override
    public Tuple getSubTuple( int elements ) {
        return elements == 1 ? this : null;
    }

    @Override
    public ObjectTypeNode.Id getInputOtnId() {
        return sink != null ? getTupleSink().getRightInputOtnId() : null;
    }

    @Override
    public LeftTupleSource getTupleSource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyTuple( PropagationContext context, InternalWorkingMemory workingMemory) {
        getTupleSink().modifyRightTuple( this, context, workingMemory );
    }

    @Override
    public void retractTuple( PropagationContext context, InternalWorkingMemory workingMemory ) {
        getTupleSink().retractRightTuple( this, context, workingMemory );
    }

    public InternalFactHandle getFactHandleForEvaluation() {
        return getFactHandle();
    }
}
