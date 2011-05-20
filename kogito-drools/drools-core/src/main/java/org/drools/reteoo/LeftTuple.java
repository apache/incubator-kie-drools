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

import java.util.Arrays;

import org.drools.common.AgendaItem;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.LeftTupleList;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.Tuple;

public class LeftTuple
    implements
    Tuple,
    Entry {
    private static final long  serialVersionUID = 510l;

    private int                index;

    private InternalFactHandle handle;

    private LeftTuple          parent;

    private Object             object;

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
    public LeftTuple          firstChild;
    public LeftTuple          lastChild;

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
            LeftTuple first = handle.getLastLeftTuple();
            if ( first == null ) {
                // node other LeftTuples, just add.
                handle.setFirstLeftTuple( this );
                handle.setLastLeftTuple( this );
            } else {
                this.leftParentPrevious = handle.getLastLeftTuple();
                this.leftParentPrevious.leftParentNext = this;
                handle.setLastLeftTuple( this );
            }
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
            if ( leftTuple.lastChild != null ) {
                this.leftParentPrevious = leftTuple.lastChild;
                this.leftParentPrevious.leftParentNext = this;
            } else {
                leftTuple.firstChild = this;
            }
            leftTuple.lastChild = this;
        }
        
        this.sink = sink;
    }
    
    public LeftTuple(final LeftTuple leftTuple,
                     InternalFactHandle handle,                     
                     LeftTupleSink sink,
                     boolean leftTupleMemoryEnabled) {
        this.index = leftTuple.index + 1;
        this.parent = leftTuple;
        this.handle = handle;

        if ( leftTupleMemoryEnabled ) {
            this.leftParent = leftTuple;
            if ( leftTuple.lastChild != null ) {
                this.leftParentPrevious = leftTuple.lastChild;
                this.leftParentPrevious.leftParentNext = this;
            } else {
                leftTuple.firstChild = this;
            }
            leftTuple.lastChild = this;
        }
        
        this.sink = sink;
    }    

    public LeftTuple(final LeftTuple leftTuple,
                     final RightTuple rightTuple,
                     final LeftTupleSink sink,
                     final boolean leftTupleMemoryEnabled) {
        this( leftTuple,
              rightTuple,
              null,
              null,
              sink,
              leftTupleMemoryEnabled );
    }
    
    public LeftTuple(final LeftTuple leftTuple,
                     final RightTuple rightTuple,
                     final LeftTuple currentLeftChild,
                     final LeftTuple currentRightChild,
                     final LeftTupleSink sink,
                     final boolean leftTupleMemoryEnabled) {
        this.handle = rightTuple.getFactHandle();
        this.index = leftTuple.index + 1;
        this.parent = leftTuple;

        if ( leftTupleMemoryEnabled ) {
            this.leftParent = leftTuple;
            this.rightParent = rightTuple;
            if( currentLeftChild == null ) {
                // insert at the end of the list 
                if ( leftTuple.lastChild != null ) {
                    this.leftParentPrevious = leftTuple.lastChild;
                    this.leftParentPrevious.leftParentNext = this;
                } else {
                    leftTuple.firstChild = this;
                }
                leftTuple.lastChild = this;
            } else {
                // insert before current child
                this.leftParentNext = currentLeftChild;
                this.leftParentPrevious = currentLeftChild.leftParentPrevious;
                currentLeftChild.leftParentPrevious = this;
                if( this.leftParentPrevious == null ) {
                    this.leftParent.firstChild = this;
                } else {
                    this.leftParentPrevious.leftParentNext = this;
                }
            }
            
            if( currentRightChild == null ) {
                // insert at the end of the list
                if ( rightTuple.lastChild != null ) {
                    this.rightParentPrevious = rightTuple.lastChild;
                    this.rightParentPrevious.rightParentNext = this;
                } else {
                    rightTuple.firstChild = this;
                }
                rightTuple.lastChild = this;
            } else {
                // insert before current child
                this.rightParentNext = currentRightChild;
                this.rightParentPrevious = currentRightChild.rightParentPrevious;
                currentRightChild.rightParentPrevious = this;
                if( this.rightParentPrevious == null ) {
                    this.rightParent.firstChild = this;
                } else {
                    this.rightParentPrevious.rightParentNext = this;
                }
            }
        }
        
        this.sink = sink;
    }
    
    public void reAdd() {
        LeftTuple first = handle.getLastLeftTuple();
        if ( first == null ) {
            // node other LeftTuples, just add.
            handle.setFirstLeftTuple( this );
            handle.setLastLeftTuple( this );
        } else {
            
            handle.getLastLeftTuple().leftParentNext = this;
            this.leftParentPrevious = handle.getLastLeftTuple();
            handle.setLastLeftTuple( this );
        }
    }
    
    public void reAddLeft() {
        // The parent can never be the FactHandle (root LeftTuple) as that is handled by reAdd()
        // make sure we aren't already at the end
        if ( this.leftParentNext != null ) {
            if ( this.leftParentPrevious != null ) {
                // remove the current LeftTuple from the middle of the chain
                this.leftParentPrevious.leftParentNext = this.leftParentNext;
                this.leftParentNext.leftParentPrevious = this.leftParentPrevious;
            } else {
                if( this.leftParent.firstChild == this ) {
                    // remove the current LeftTuple from start start of the chain
                    this.leftParent.firstChild = this.leftParentNext;
                }
                this.leftParentNext.leftParentPrevious = null;
            }
            // re-add to end
            this.leftParentPrevious = this.leftParent.lastChild;
            this.leftParentPrevious.leftParentNext = this;
            this.leftParent.lastChild = this;
            this.leftParentNext = null;
        }
    }
    
    public void reAddRight() {
        // make sure we aren't already at the end        
        if ( this.rightParentNext != null ) {
            if ( this.rightParentPrevious != null ) {
                // remove the current LeftTuple from the middle of the chain
                this.rightParentPrevious.rightParentNext = this.rightParentNext;
                this.rightParentNext.rightParentPrevious = this.rightParentPrevious;
            } else {
                if( this.rightParent.firstChild == this ) {
                    // remove the current LeftTuple from the start of the chain
                    this.rightParent.firstChild = this.rightParentNext;
                }
                this.rightParentNext.rightParentPrevious = null;
            }
            // re-add to end            
            this.rightParentPrevious = this.rightParent.lastChild;
            this.rightParentPrevious.rightParentNext = this;
            this.rightParent.lastChild = this;
            this.rightParentNext = null;
        }
    }

    public void unlinkFromLeftParent() {
        LeftTuple previousParent = this.leftParentPrevious;
        LeftTuple nextParent = this.leftParentNext;

        if ( previousParent != null && nextParent != null ) {
            //remove  from middle
            this.leftParentPrevious.leftParentNext = nextParent;
            this.leftParentNext.leftParentPrevious = previousParent;
        } else if ( nextParent != null ) {
            //remove from first
            if ( this.leftParent != null ) {
                this.leftParent.firstChild = nextParent;
            } else {
                // This is relevant to the root node and only happens at rule removal time
                this.handle.setFirstLeftTuple( nextParent );
            }
            nextParent.leftParentPrevious = null;
        } else if ( previousParent != null ) {
            //remove from end
            if ( this.leftParent != null ) {
                this.leftParent.lastChild = previousParent;
            } else {
                // relevant to the root node, as here the parent is the FactHandle, only happens at rule removal time
                this.handle.setLastLeftTuple( previousParent );
            }
            previousParent.leftParentNext = null;
        } else {
            // single remaining item, no previous or next
            if( leftParent != null ) {
                this.leftParent.firstChild = null;
                this.leftParent.lastChild = null;
            } else {
                // it is a root tuple - only happens during rule removal
                this.handle.setFirstLeftTuple( null );
                this.handle.setLastLeftTuple( null );
            }
        }

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
        
        LeftTuple previousParent = this.rightParentPrevious;
        LeftTuple nextParent = this.rightParentNext;

        if ( previousParent != null && nextParent != null ) {
            // remove from middle
            this.rightParentPrevious.rightParentNext = this.rightParentNext;
            this.rightParentNext.rightParentPrevious = this.rightParentPrevious;
        } else if ( nextParent != null ) {
            // remove from the start
            this.rightParent.firstChild = nextParent;
            nextParent.rightParentPrevious = null;
        } else if ( previousParent != null ) {
            // remove from end     
            this.rightParent.lastChild = previousParent;
            previousParent.rightParentNext = null;
        } else {
            // single remaining item, no previous or next
            this.rightParent.firstChild = null;
            this.rightParent.lastChild = null;
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
    
    /* Had to add the set method because sink adapters must override 
     * the tuple sink set when the tuple was created.
     */
    public void setLeftTupleSink( LeftTupleSink sink ) {
        this.sink = sink;
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

//    public void setBetaChildren(LeftTuple leftTuple) {
//        this.firstChild = leftTuple;
//    }
//
//    public LeftTuple getBetaChildren() {
//        return this.firstChild;
//    }

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
    
    public Object getObject() {
        return this.object;
    }
    
    public void setObject(final Object object) {
        this.object = object;
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
    
    public String toTupleTree(int indent) {
        StringBuilder buf = new StringBuilder();
        char[] spaces = new char[indent];
        Arrays.fill( spaces, ' ' );
        String istr = new String( spaces );
        buf.append( istr );
        buf.append( this.toExternalString() );
        buf.append( "\n" );
        for( LeftTuple leftTuple = this.firstChild; leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
            buf.append( leftTuple.toTupleTree( indent+4 ) );
        }
        return buf.toString();
    }

    private String toExternalString() {
        StringBuilder builder = new StringBuilder();
        builder.append( String.format( "%08X", System.identityHashCode( this ) ) ).append( ":" );
        int[] ids = new int[this.index+1];
        LeftTuple entry = this;
        while( entry != null ) {
            ids[entry.index] = entry.getLastHandle().getId();
            entry = entry.parent;
        }
        builder.append( Arrays.toString( ids ) )
               .append( " activation=" )
               .append( this.object != null ? this.object : "null" )
               .append( " sink=" )
               .append( this.sink.getClass().getSimpleName() )
               .append( "(" ).append( sink.getId() ).append( ")" );
        return  builder.toString();
    }

    public void increaseActivationCountForEvents() {
        for ( LeftTuple entry = this; entry != null; entry = entry.getParent() ) {
            if( entry.getLastHandle().isEvent() ) {
                ((EventFactHandle)entry.getLastHandle()).increaseActivationsCount();
            }
        }
    }
    
    public void decreaseActivationCountForEvents() {
        for ( LeftTuple entry = this; entry != null; entry = entry.getParent() ) {
            if( entry.getLastHandle().isEvent() ) {
                ((EventFactHandle)entry.getLastHandle()).decreaseActivationsCount();
            }
        }
    }
    
}
