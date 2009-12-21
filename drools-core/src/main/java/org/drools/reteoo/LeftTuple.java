package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.Tuple;
import org.drools.util.Entry;
import org.drools.util.LeftTupleList;

public class LeftTuple
    implements
    Tuple,
    Entry {
    private static final long  serialVersionUID = 400L;

    private int                index;

    private InternalFactHandle handle;

    private LeftTuple          parent;

    private Activation         activation;

    private RightTuple         blocker;

    private LeftTuple          blockedPrevious;

    private LeftTuple          blockedNext;

    // left and right tuples in parent
    private LeftTuple          leftParent;
    private LeftTuple          leftParentPrevious;
    private LeftTuple          leftParentNext;

    private RightTuple         rightParent;
    private LeftTuple          rightParentPrevious;
    private LeftTuple          rightParentNext;

    // node memory
    private LeftTupleList      memory;
    private Entry              next;
    private Entry              previous;

    // children
    private LeftTuple          children;

    private LeftTupleSink      sink;

    public LeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public LeftTuple(final InternalFactHandle factHandle,
                     LeftTupleSink sink,
                     boolean leftTupleMemoryEnabled) {
        this.handle = factHandle;

        if ( leftTupleMemoryEnabled ) {
            LeftTuple currentFirst = handle.getLeftTuple();
            if ( currentFirst != null ) {
                currentFirst.leftParentPrevious = this;
                this.leftParentNext = currentFirst;
            }

            handle.setLeftTuple( this );
        }
        this.sink = sink;
    }

    public LeftTuple(final LeftTuple leftTuple,
                     LeftTupleSink sink,
                     boolean leftTupleMemoryEnabled) {
        this.index = leftTuple.index;
        this.parent = leftTuple.parent;
        this.handle = leftTuple.handle;

        if ( leftTupleMemoryEnabled ) {
            this.leftParent = leftTuple;
            this.leftParentNext = leftTuple.children;
            if ( this.leftParentNext != null ) {
                this.leftParentNext.leftParentPrevious = this;
            }
            this.leftParent.children = this;
        }
        
        this.sink = sink;
    }

    public LeftTuple(final LeftTuple leftTuple,
                     final RightTuple rightTuple,
                     LeftTupleSink sink,
                     boolean leftTupleMemoryEnabled) {
        this.handle = rightTuple.getFactHandle();
        this.index = leftTuple.index + 1;
        this.parent = leftTuple;

        if ( leftTupleMemoryEnabled ) {
            this.rightParent = rightTuple;
            this.rightParentNext = this.rightParent.getBetaChildren();
            if ( this.rightParentNext != null ) {
                this.rightParentNext.rightParentPrevious = this;
            }
            this.rightParent.setBetaChildren( this );

            this.leftParent = leftTuple;
            this.leftParentNext = leftTuple.children;
            if ( this.leftParentNext != null ) {
                this.leftParentNext.leftParentPrevious = this;
            }
            this.leftParent.children = this;
        }
        
        this.sink = sink;
    }

    public void unlinkFromLeftParent() {
        LeftTuple previous = this.leftParentPrevious;
        LeftTuple next = this.leftParentNext;

        if ( previous != null && next != null ) {
            //remove  from middle
            this.leftParentPrevious.leftParentNext = this.leftParentNext;
            this.leftParentNext.leftParentPrevious = this.leftParentPrevious;
        } else if ( next != null ) {
            if ( this.leftParent != null ) { 
                //remove from first
                this.leftParent.children = this.leftParentNext;
            } else {
                this.handle.setLeftTuple( this.leftParentNext );
            }

            this.leftParentNext.leftParentPrevious = null;
        } else if ( previous != null ) {
            //remove from end
            this.leftParentPrevious.leftParentNext = null;
        } else {
            if ( this.leftParent != null ) { 
                this.leftParent.children = null;
            } else {
                this.handle.setLeftTuple( null );
            }
        }

        //this.parent  = null;

        this.leftParent = null;
        this.leftParentPrevious = null;
        this.leftParentNext = null;
        //
        this.blocker = null;
    }

    public void unlinkFromRightParent() {
        if ( this.rightParent == null ) {
            // no right parent;
            return;
        }
        LeftTuple previous = this.rightParentPrevious;
        LeftTuple next = this.rightParentNext;

        if ( previous != null && next != null ) {
            //remove  from middle
            this.rightParentPrevious.rightParentNext = this.rightParentNext;
            this.rightParentNext.rightParentPrevious = this.rightParentPrevious;
        } else if ( next != null ) {
            //remove from first
            this.rightParent.setBetaChildren( this.rightParentNext );
            this.rightParentNext.rightParentPrevious = null;
        } else if ( previous != null ) {
            //remove from end
            this.rightParentPrevious.rightParentNext = null;
        } else {
            this.rightParent.setBetaChildren( null );
        }

        this.blocker = null;

        this.rightParent = null;
        this.rightParentPrevious = null;
        this.rightParentNext = null;
    }
    
    public int getIndex() {
        return this.index;
    }

    public LeftTupleSink getLeftTupleSink() {
        return sink;
    }

    public LeftTuple getLeftParent() {
        return leftParent;
    }

    public void setLeftParent(LeftTuple leftParent) {
        this.leftParent = leftParent;
    }

    public LeftTuple getLeftParentPrevious() {
        return leftParentPrevious;
    }

    public void setLeftParentPrevious(LeftTuple leftParentLeft) {
        this.leftParentPrevious = leftParentLeft;
    }

    public LeftTuple getLeftParentNext() {
        return leftParentNext;
    }

    public void setLeftParentNext(LeftTuple leftParentright) {
        this.leftParentNext = leftParentright;
    }

    public RightTuple getRightParent() {
        return rightParent;
    }

    public void setRightParent(RightTuple rightParent) {
        this.rightParent = rightParent;
    }

    public LeftTuple getRightParentPrevious() {
        return rightParentPrevious;
    }

    public void setRightParentPrevious(LeftTuple rightParentLeft) {
        this.rightParentPrevious = rightParentLeft;
    }

    public LeftTuple getRightParentNext() {
        return rightParentNext;
    }

    public void setRightParentNext(LeftTuple rightParentRight) {
        this.rightParentNext = rightParentRight;
    }

    public void setBetaChildren(LeftTuple leftTuple) {
        this.children = leftTuple;
    }

    public LeftTuple getBetaChildren() {
        return this.children;
    }

    public InternalFactHandle get(final int index) {
        LeftTuple entry = this;
        while ( entry.index != index ) {
            entry = entry.parent;
        }
        return entry.handle;
    }

    public LeftTupleList getMemory() {
        return this.memory;
    }

    public void setMemory(LeftTupleList memory) {
        this.memory = memory;
    }

    public Entry getPrevious() {
        return previous;
    }

    public void setPrevious(Entry previous) {
        this.previous = previous;
    }

    public void setNext(final Entry next) {
        this.next = next;
    }

    public Entry getNext() {
        return this.next;
    }

    public InternalFactHandle getLastHandle() {
        return this.handle;
    }

    public InternalFactHandle get(final Declaration declaration) {
        return get( declaration.getPattern().getOffset() );
    }

    public Activation getActivation() {
        return this.activation;
    }

    /**
     * Returns the fact handles in reverse order
     */
    public InternalFactHandle[] getFactHandles() {
        InternalFactHandle[] handles = new InternalFactHandle[this.index + 1];
        LeftTuple entry = this;
        int i = 0;
        while ( entry != null ) {
            handles[i++] = entry.handle;
            entry = entry.parent;
        }
        return handles;
    }
     public InternalFactHandle[] toFactHandles() {
        InternalFactHandle[] handles = new InternalFactHandle[this.index + 1];
        LeftTuple entry = this;

        while ( entry != null ) {
            handles[entry.index] = entry.handle;
            entry = entry.parent;
        }
        return handles;
    }    

    public void setBlocker(RightTuple blocker) {
        this.blocker = blocker;
    }

    public RightTuple getBlocker() {
        return this.blocker;
    }

    public LeftTuple getBlockedPrevious() {
        return this.blockedPrevious;
    }

    public void setBlockedPrevious(LeftTuple blockerPrevious) {
        this.blockedPrevious = blockerPrevious;
    }

    public LeftTuple getBlockedNext() {
        return this.blockedNext;
    }

    public void setBlockedNext(LeftTuple blockerNext) {
        this.blockedNext = blockerNext;
    }

    public void setActivation(final Activation activation) {
        this.activation = activation;
    }

//    public int hashCode() {        
//        return this.hashCode;
//    }

    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        LeftTuple entry = this;
        while ( entry != null ) {
            //buffer.append( entry.handle );
            buffer.append(entry.handle).append("\n");
            entry = entry.parent;
        }
        return buffer.toString();
    }

    
    public int hashCode() {
        return this.handle.hashCode();
    }
    /**
     * We use this equals method to avoid the cast
     * @param tuple
     * @return
     */
    public boolean equals(final LeftTuple other) {
        // we know the object is never null and always of the  type LeftTuple
        if ( other == this ) {
            return true;
        } else if( other == null ) {
            return false;
        }

        // A LeftTuple is  only the same if it has the same hashCode, factId and parent
        if ( this.hashCode() != other.hashCode() ) {
            return false;
        }

        if ( this.handle != other.handle ) {
            return false;
        }
        
//        if ( this.sink.getId() != other.sink.getId() ) {
//            return false;
//        }
//        
//        if ( this.index != other.index ) {
//            return false;
//        }

        if ( this.parent == null ) {
            return (other.parent == null);
        } else {
            return this.parent.equals( other.parent );
        }
    }

    public boolean equals(final Object object) {
        // we know the object is never null and always of the  type ReteTuple    
        return equals( (LeftTuple) object );
    }

    public int size() {
        return this.index + 1;
    }

    /**
     * Returns the ReteTuple that contains the "elements"
     * first elements in this tuple.
     * 
     * Use carefully as no cloning is made during this process.
     * 
     * This method is used by TupleStartEqualsConstraint when
     * joining a subnetwork tuple into the main network tuple;
     * 
     * @param elements the number of elements to return, starting from
     * the begining of the tuple
     * 
     * @return a ReteTuple containing the "elements" first elements
     * of this tuple or null if "elements" is greater than size; 
     */
    public LeftTuple getSubTuple(final int elements) {
        LeftTuple entry = this;
        if ( elements < this.size() ) {
            final int lastindex = elements - 1;

            while ( entry.index != lastindex ) {
                entry = entry.parent;
            }
        }
        return entry;
    }

    public Object[] toObjectArray() {
        Object[] objects = new Object[this.index + 1];
        LeftTuple entry = this;
        while ( entry != null ) {
            Object object = entry.getLastHandle().getObject();
            objects[entry.index] = object;
            entry = entry.parent;
        }
        return objects;
    }

    public LeftTuple getParent() {
        return parent;
    }
}
