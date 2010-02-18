package org.drools.reteoo;

import java.util.concurrent.atomic.AtomicReference;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.RightTupleList;

public class ConcurrentRightTuple extends RightTuple {
    private RightTupleList     memory;

    private AtomicReference<Entry>              previous;
    private AtomicReference<Entry>              next;

    public ConcurrentRightTuple() {

    }

    public ConcurrentRightTuple(InternalFactHandle handle,
                      RightTupleSink sink) {
        this.handle = handle;
        this.sink = sink;
        
        this.previous = new AtomicReference<Entry>();
        this.next = new AtomicReference<Entry>();

        RightTuple currentFirst = handle.getRightTuple();
        if ( currentFirst != null ) {
            currentFirst.setHandlePrevious( this );
            setHandleNext( currentFirst );
        }

        handle.setRightTuple( this );
    }

    public RightTupleList getMemory() {
        return memory;
    }

    public void setMemory(RightTupleList memory) {
        this.memory = memory;
    }

    public Entry getPrevious() {
        return previous.get();
    }

    public void setPrevious(Entry previous) {
        this.previous.set( previous );
    }
    
    public Entry getNext() {
        return next.get();
    }

    public void setNext(Entry next) {
        this.next.set( next );
    }



    public int hashCode() {
        return this.handle.hashCode();
    }

    public String toString() {
        return this.handle.toString() + "\n";
    }

    public boolean equals(ConcurrentRightTuple other) {
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
        return equals( (ConcurrentRightTuple) object );
    }
}
