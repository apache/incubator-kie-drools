package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.RightTupleList;

public class RightTuple
    implements
    Entry {
    protected InternalFactHandle handle;

    private RightTuple         handlePrevious;
    private RightTuple         handleNext;

    private RightTupleList     memory;

    private Entry              previous;
    private Entry              next;

    private LeftTuple          betaChildren;

    private LeftTuple          blocked;

    protected RightTupleSink     sink;

    public RightTuple() {

    }

    public RightTuple(InternalFactHandle handle,
                      RightTupleSink sink) {
        this.handle = handle;
        this.sink = sink;

        RightTuple currentFirst = handle.getRightTuple();
        if ( currentFirst != null ) {
            currentFirst.handlePrevious = this;
            this.handleNext = currentFirst;
        }

        handle.setRightTuple( this );
    }

    public RightTupleSink getRightTupleSink() {
        return this.sink;
    }

    public void unlinkFromRightParent() {
        if ( this.handle != null ) {
            if( this.handlePrevious != null ) {
                this.handlePrevious.handleNext = this.handleNext;
            }
            if( this.handleNext != null ) {
                this.handleNext.handlePrevious = this.handlePrevious;
            }
            if( this.handle.getRightTuple() == this ) {
                this.handle.setRightTuple( this.handleNext );
            }
        }
        this.handle = null;
        this.handlePrevious = null;
        this.handleNext = null;
        this.blocked = null;
        this.previous = null;
        this.next = null;
        this.memory = null;
        this.betaChildren = null;
        this.sink = null;
    }

    public InternalFactHandle getFactHandle() {
        return this.handle;
    }

    public LeftTuple getBlocked() {
        return this.blocked;
    }    

    public void setBlocked(LeftTuple leftTuple) {
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
            this.blocked = next ;
            next.setBlockedPrevious( null );
        } else if ( previous != null ) {
            //remove from end
            previous.setBlockedNext( null );
        } else {
            this.blocked =  null;
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

    public LeftTuple getBetaChildren() {
        return betaChildren;
    }

    public void setBetaChildren(LeftTuple betachildren) {
        this.betaChildren = betachildren;
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
}
