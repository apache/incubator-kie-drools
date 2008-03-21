package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalFactHandle;
import org.drools.util.Entry;
import org.drools.util.RightTupleList;

public class RightTuple
    implements
    Entry {
    private final InternalFactHandle handle;

    private RightTuple               handlePrevious;
    private RightTuple               handleNext;

    private RightTupleList            memory;

    private Entry                    previous;
    private Entry                    next;

    private LeftTuple                betaChildren;

    private LeftTuple                blocked;

    private RightTupleSink           sink;

    private int                      hashCode;

    public RightTuple(InternalFactHandle handle) {
        this.handle = handle;
        this.hashCode = this.handle.hashCode();
    }

    public RightTuple(InternalFactHandle handle,
                      RightTupleSink sink) {
        this.handle = handle;
        this.hashCode = this.handle.hashCode();
        this.sink = sink;

        //        RightTuple currentFirst = handle.getRightTuple();
        //        if ( currentFirst != null ) {
        //            currentFirst.handlePrevious =  this;
        //            this.handleNext = currentFirst;
        //        }
        //        
        //        handle.setRightTuple( this );                
    }

    public RightTuple(RightTuple parent) {
        this.handle = parent.getFactHandle();
        this.hashCode = this.handle.hashCode();
    }

    public RightTuple(RightTuple parent,
                      RightTupleSink sink) {
        this.handle = parent.getFactHandle();
        this.hashCode = this.handle.hashCode();

        this.sink = sink;

    }

    public RightTupleSink getRightTupleSink() {
        return this.sink;
    }

    //    public void unlinkFromRightParent() {
    //        if ( this.parent != null ) {
    //            if ( this.parentPrevious != null ) {
    //                this.parentPrevious.parentNext = this.parentNext;
    //            } else {
    //                // first one in the chain, so treat differently                
    //                this.parent.setAlphaChildren( this.parentNext );
    //            }
    //
    //            if ( this.parentNext != null ) {
    //                this.parentNext.parentPrevious = this.parentPrevious;
    //            }
    //        }
    //
    //        this.parent = null;
    //        this.parentPrevious = null;
    //        this.parentNext = null;
    //        this.blocked = null;
    //    }

    public InternalFactHandle getFactHandle() {
        return this.handle;
    }

    public LeftTuple getBlocked() {
        return blocked;
    }

    public void setBlocked(LeftTuple blocked) {
        this.blocked = blocked;
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

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    public int hashCode() {
        return this.hashCode;
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
        if ( (other == null) || (this.hashCode != other.hashCode) ) {
            return false;
        }

        return this.handle == other.handle;
    }

    public boolean equals(Object object) {
        return equals( (RightTuple) object );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        // TODO Auto-generated method stub

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // TODO Auto-generated method stub

    }

}
