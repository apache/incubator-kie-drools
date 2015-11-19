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
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.index.TupleList;

import java.util.Arrays;

/**
 * A parent class for all specific LeftTuple specializations
 * @author etirelli
 *
 */
public class BaseLeftTuple extends BaseTuple implements LeftTuple {
    private static final long  serialVersionUID = 540l;

    private int                index;

    private LeftTuple          parent;

    // left and right tuples in parent
    private LeftTuple          leftParent;
    private LeftTuple          leftParentPrevious;
    private LeftTuple          leftParentNext;

    private RightTuple         rightParent;
    private LeftTuple          rightParentPrevious;
    private LeftTuple          rightParentNext;

    // children
    private LeftTuple          firstChild;
    private LeftTuple          lastChild;

    // node memory
    protected TupleList        memory;

    private LeftTuple            peer;

    public BaseLeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public BaseLeftTuple(InternalFactHandle factHandle,
                         Sink sink,
                         boolean leftTupleMemoryEnabled) {
        setFactHandle( factHandle );
        this.sink = sink;
        if ( leftTupleMemoryEnabled ) {
            factHandle.addLeftTupleInPosition( this );
        }
    }
    
    public BaseLeftTuple(InternalFactHandle factHandle,
                         LeftTuple leftTuple,
                         Sink sink) {
        setFactHandle( factHandle );
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple;
        this.sink = sink;
    }

    public BaseLeftTuple(LeftTuple leftTuple,
                         Sink sink,
                         PropagationContext pctx,
                         boolean leftTupleMemoryEnabled) {
        this.index = leftTuple.getIndex();
        this.parent = leftTuple;
        setPropagationContext( pctx );

        if ( leftTupleMemoryEnabled ) {
            this.leftParent = leftTuple;
            if ( leftTuple.getLastChild() != null ) {
                this.leftParentPrevious = leftTuple.getLastChild();
                this.leftParentPrevious.setLeftParentNext( this );
            } else {
                leftTuple.setFirstChild( this );
            }
            leftTuple.setLastChild( this );
        }
        
        this.sink = sink;
    }
    
    public BaseLeftTuple(LeftTuple leftTuple,
                         RightTuple rightTuple,
                         Sink sink) {
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple;
        setFactHandle( rightTuple.getFactHandle() );
        setPropagationContext( rightTuple.getPropagationContext() );

        this.leftParent = leftTuple;
        // insert at the end f the list
        if ( leftTuple.getLastChild() != null ) {
            this.leftParentPrevious = leftTuple.getLastChild();
            this.leftParentPrevious.setLeftParentNext( this );
        } else {
            leftTuple.setFirstChild( this );
        }
        leftTuple.setLastChild( this );
        
        // insert at the end of the list
        this.rightParent = rightTuple;
        if ( rightTuple.getLastChild() != null ) {
            this.rightParentPrevious = rightTuple.getLastChild();
            this.rightParentPrevious.setRightParentNext( this );
        } else {
            rightTuple.setFirstChild( this );
        }
        rightTuple.setLastChild( this );
        this.sink = sink;
    }    

    public BaseLeftTuple(LeftTuple leftTuple,
                         RightTuple rightTuple,
                         Sink sink,
                         boolean leftTupleMemoryEnabled) {
        this( leftTuple,
              rightTuple,
              null,
              null,
              sink,
              leftTupleMemoryEnabled );
    }
    
    public BaseLeftTuple(LeftTuple leftTuple,
                         RightTuple rightTuple,
                         LeftTuple currentLeftChild,
                         LeftTuple currentRightChild,
                         Sink sink,
                         boolean leftTupleMemoryEnabled) {
        setFactHandle( rightTuple.getFactHandle() );
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple;
        setPropagationContext( rightTuple.getPropagationContext() );

        if ( leftTupleMemoryEnabled ) {
            this.leftParent = leftTuple;
            this.rightParent = rightTuple;
            if( currentLeftChild == null ) {
                // insert at the end of the list 
                if ( leftTuple.getLastChild() != null ) {
                    this.leftParentPrevious = leftTuple.getLastChild();
                    this.leftParentPrevious.setLeftParentNext( this );
                } else {
                    leftTuple.setFirstChild( this );
                }
                leftTuple.setLastChild( this );
            } else {
                // insert before current child
                this.leftParentNext = currentLeftChild;
                this.leftParentPrevious = currentLeftChild.getLeftParentPrevious();
                currentLeftChild.setLeftParentPrevious( this );
                if( this.leftParentPrevious == null ) {
                    this.leftParent.setFirstChild( this  );
                } else {
                    this.leftParentPrevious.setLeftParentNext( this );
                }
            }
            
            if( currentRightChild == null ) {
                // insert at the end of the list
                if ( rightTuple.getLastChild() != null ) {
                    this.rightParentPrevious = rightTuple.getLastChild();
                    this.rightParentPrevious.setRightParentNext( this );
                } else {
                    rightTuple.setFirstChild( this );
                }
                rightTuple.setLastChild( this );
            } else {
                // insert before current child
                this.rightParentNext = currentRightChild;
                this.rightParentPrevious = currentRightChild.getRightParentPrevious();
                currentRightChild.setRightParentPrevious( this );
                if( this.rightParentPrevious == null ) {
                    this.rightParent.setFirstChild( this );
                } else {
                    this.rightParentPrevious.setRightParentNext( this );
                }
            }
        }
        
        this.sink = sink;
    }
    
    @Override
    public void reAdd() {
        getFactHandle().addLastLeftTuple( this );
    }

    @Override
    public void reAddLeft() {
        // The parent can never be the FactHandle (root LeftTuple) as that is handled by reAdd()
        // make sure we aren't already at the end
        if ( this.leftParentNext != null ) {
            if ( this.leftParentPrevious != null ) {
                // remove the current LeftTuple from the middle of the chain
                this.leftParentPrevious.setLeftParentNext( this.leftParentNext );
                this.leftParentNext.setLeftParentPrevious( this.leftParentPrevious );
            } else {
                if( this.leftParent.getFirstChild() == this ) {
                    // remove the current LeftTuple from start start of the chain
                    this.leftParent.setFirstChild( this.leftParentNext );
                }
                this.leftParentNext.setLeftParentPrevious(  null );
            }
            // re-add to end
            this.leftParentPrevious = this.leftParent.getLastChild();
            this.leftParentPrevious.setLeftParentNext( this );
            this.leftParent.setLastChild( this );
            this.leftParentNext = null;
        }
    }

    @Override
    public void reAddRight() {
        // make sure we aren't already at the end        
        if ( this.rightParentNext != null ) {
            if ( this.rightParentPrevious != null ) {
                // remove the current LeftTuple from the middle of the chain
                this.rightParentPrevious.setRightParentNext( this.rightParentNext );
                this.rightParentNext.setRightParentPrevious( this.rightParentPrevious );
            } else {
                if( this.rightParent.getFirstChild() == this ) {
                    // remove the current LeftTuple from the start of the chain
                    this.rightParent.setFirstChild( this.rightParentNext );
                }
                this.rightParentNext.setRightParentPrevious( null );
            }
            // re-add to end            
            this.rightParentPrevious = this.rightParent.getLastChild();
            this.rightParentPrevious.setRightParentNext( this );
            this.rightParent.setLastChild( this );
            this.rightParentNext = null;
        }
    }

    @Override
    public void unlinkFromLeftParent() {
        LeftTuple previousParent = this.leftParentPrevious;
        LeftTuple nextParent = this.leftParentNext;

        if ( previousParent != null && nextParent != null ) {
            //remove  from middle
            this.leftParentPrevious.setLeftParentNext( nextParent );
            this.leftParentNext.setLeftParentPrevious( previousParent );
        } else if ( nextParent != null ) {
            //remove from first
            if ( this.leftParent != null ) {
                this.leftParent.setFirstChild( nextParent );
            } else {
                // This is relevant to the root node and only happens at rule removal time
                getFactHandle().removeLeftTuple( this );
            }
            nextParent.setLeftParentPrevious( null );
        } else if ( previousParent != null ) {
            //remove from end
            if ( this.leftParent != null ) {
                this.leftParent.setLastChild( previousParent );
            } else {
                // relevant to the root node, as here the parent is the FactHandle, only happens at rule removal time
                getFactHandle().removeLeftTuple( this );
            }
            previousParent.setLeftParentNext(  null );
        } else {
            // single remaining item, no previous or next
            if( leftParent != null ) {
                this.leftParent.setFirstChild( null );
                this.leftParent.setLastChild( null );
            } else {
                // it is a root tuple - only happens during rule removal
                getFactHandle().removeLeftTuple( this );
            }
        }

        this.leftParent = null;
        this.leftParentPrevious = null;
        this.leftParentNext = null;
    }

    @Override
    public void unlinkFromRightParent() {
        if ( this.rightParent == null ) {
            // no right parent;
            return;
        }
        
        LeftTuple previousParent = this.rightParentPrevious;
        LeftTuple nextParent = this.rightParentNext;

        if ( previousParent != null && nextParent != null ) {
            // remove from middle
            this.rightParentPrevious.setRightParentNext( this.rightParentNext );
            this.rightParentNext.setRightParentPrevious( this.rightParentPrevious );
        } else if ( nextParent != null ) {
            // remove from the start
            this.rightParent.setFirstChild( nextParent );
            nextParent.setRightParentPrevious( null );
        } else if ( previousParent != null ) {
            // remove from end     
            this.rightParent.setLastChild( previousParent );
            previousParent.setRightParentNext( null );
        } else {
            // single remaining item, no previous or next
            this.rightParent.setFirstChild( null );
            this.rightParent.setLastChild( null );
        }

        this.rightParent = null;
        this.rightParentPrevious = null;
        this.rightParentNext = null;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public LeftTupleSink getTupleSink() {
        return (LeftTupleSink)sink;
    }
    
    /* Had to add the set method because sink adapters must override 
     * the tuple sink set when the tuple was created.
     */
    @Override
    public void setLeftTupleSink( LeftTupleSink sink ) {
        this.sink = sink;
    }

    @Override
    public LeftTuple getLeftParent() {
        return leftParent;
    }

    @Override
    public void setLeftParent(LeftTuple leftParent) {
        this.leftParent = leftParent;
    }

    @Override
    public LeftTuple getLeftParentPrevious() {
        return leftParentPrevious;
    }

    @Override
    public void setLeftParentPrevious(LeftTuple leftParentLeft) {
        this.leftParentPrevious = leftParentLeft;
    }

    @Override
    public LeftTuple getLeftParentNext() {
        return leftParentNext;
    }

    @Override
    public void setLeftParentNext(LeftTuple leftParentright) {
        this.leftParentNext = leftParentright;
    }

    @Override
    public RightTuple getRightParent() {
        return rightParent;
    }

    @Override
    public void setRightParent(RightTuple rightParent) {
        this.rightParent = rightParent;
    }

    @Override
    public LeftTuple getRightParentPrevious() {
        return rightParentPrevious;
    }

    @Override
    public void setRightParentPrevious(LeftTuple rightParentLeft) {
        this.rightParentPrevious = rightParentLeft;
    }

    @Override
    public LeftTuple getRightParentNext() {
        return rightParentNext;
    }

    @Override
    public void setRightParentNext(LeftTuple rightParentRight) {
        this.rightParentNext = rightParentRight;
    }

    @Override
    public InternalFactHandle get(int index) {
        LeftTuple entry = this;
        while ( entry != null && ( entry.getIndex() != index || entry.getFactHandle() == null ) ) {
            entry = entry.getParent();
        }
        return entry == null ? null : entry.getFactHandle();
    }

    public InternalFactHandle[] toFactHandles() {
        InternalFactHandle[] handles = new InternalFactHandle[this.index + 1];
        LeftTuple entry = this;
        while ( entry != null ) {
            if ( entry.getFactHandle() != null ) {
                // eval, not, exists have no right input
                handles[entry.getIndex()] = entry.getFactHandle();
            }
            entry = entry.getParent();
        }
        return handles;
    }

    public Object[] toObjects() {
        Object[] objs = new Object[this.index + 1];
        LeftTuple entry = this;
        while ( entry != null ) {
            if ( entry.getFactHandle() != null ) {
                // eval, not, exists have no right input
                objs[entry.getIndex()] = entry.getFactHandle().getObject();
            }
            entry = entry.getParent();
        }
        return objs;
    }

    public void clearBlocker() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlocker(RightTuple blocker) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RightTuple getBlocker() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LeftTuple getBlockedPrevious() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlockedPrevious(LeftTuple blockerPrevious) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LeftTuple getBlockedNext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlockedNext(LeftTuple blockerNext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        LeftTuple entry = this;
        while ( entry != null ) {
            //buffer.append( entry.handle );
            buffer.append(entry.getFactHandle());
            if ( entry.getParent() != null ) {
                buffer.append("\n");
            }
            entry = entry.getParent();
        }
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return getFactHandle() == null ? 0 : getFactHandle().hashCode();
    }

    @Override
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

        if ( getFactHandle() != other.getFactHandle() ) {
            return false;
        }
        
        if ( this.parent == null ) {
            return (other.getParent() == null);
        } else {
            return this.parent.equals( other.getParent() );
        }
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof LeftTuple && equals( (LeftTuple) object );
    }

    @Override
    public int size() {
        return this.index + 1;
    }

    @Override
    public LeftTuple getFirstChild() {
        return firstChild;
    }

    @Override
    public void setFirstChild(LeftTuple firstChild) {
        this.firstChild = firstChild;
    }

    @Override
    public LeftTuple getLastChild() {
        return lastChild;
    }

    @Override
    public void setLastChild(LeftTuple lastChild) {
        this.lastChild = lastChild;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void setParent(LeftTuple parent) {
        this.parent = parent;
    }

    @Override
    public TupleList getMemory() {
        return this.memory;
    }

    @Override
    public void setMemory(TupleList memory) {
        this.memory = memory;
    }

    @Override
    public LeftTuple getStagedNext() {
        return (LeftTuple) stagedNext;
    }

    @Override
    public LeftTuple getStagedPrevious() {
        return (LeftTuple) stagedPrevious;
    }

    @Override
    public void clearStaged() {
        super.clearStaged();
        if (getContextObject() == Boolean.TRUE) {
            setContextObject( null );
        }
    }

    @Override
    public LeftTuple getPeer() {
        return peer;
    }

    @Override
    public void setPeer(LeftTuple peer) {
        this.peer = peer;
    }

    @Override
    public LeftTuple getSubTuple(final int elements) {
        LeftTuple entry = this;
        if ( elements <= this.size() ) {
            final int lastindex = elements - 1;

            while ( entry.getIndex() != lastindex || entry.getFactHandle() == null ) {
                entry = entry.getParent();
            }
        }
        return entry;
    }

    @Override
    public Object[] toObjectArray() {
        Object[] objects = new Object[this.index + 1];
        LeftTuple entry = this;
        while ( entry != null ) {
            if ( entry.getFactHandle() != null ) {
                // can be null for eval, not and exists that have no right input
                Object object = entry.getFactHandle().getObject();
                objects[entry.getIndex()] = object;
            }
            entry = entry.getParent();
        }
        return objects;
    }

    @Override
    public LeftTuple getParent() {
        return parent;
    }

    @Override
    public String toTupleTree(int indent) {
        StringBuilder buf = new StringBuilder();
        char[] spaces = new char[indent];
        Arrays.fill( spaces, ' ' );
        String istr = new String( spaces );
        buf.append( istr );
        buf.append( toExternalString() );
        buf.append( "\n" );
        for( LeftTuple leftTuple = this.firstChild; leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
            buf.append( leftTuple.toTupleTree( indent+4 ) );
        }
        return buf.toString();
    }

    protected String toExternalString() {
        StringBuilder builder = new StringBuilder();
        builder.append( String.format( "%08X", System.identityHashCode( this ) ) ).append( ":" );
        int[] ids = new int[this.index+1];
        LeftTuple entry = this;
        while( entry != null ) {
            ids[entry.getIndex()] = entry.getFactHandle().getId();
            entry = entry.getParent();
        }
        builder.append( Arrays.toString( ids ) )
               .append( " sink=" )
               .append( this.sink.getClass().getSimpleName() )
               .append( "(" ).append( sink.getId() ).append( ")" );
        return  builder.toString();
    }

    @Override
    public void clear() {
        super.clear();
        this.memory = null;
    }   
    
    public void initPeer(BaseLeftTuple original, LeftTupleSink sink) {
        this.index = original.index;
        this.parent = original.parent;
        
        setFactHandle( original.getFactHandle() );
        setPropagationContext( original.getPropagationContext() );
        this.sink = sink;
    }

    @Override
    public Object getObject(int index) {
        return get(index).getObject();
    }
}
