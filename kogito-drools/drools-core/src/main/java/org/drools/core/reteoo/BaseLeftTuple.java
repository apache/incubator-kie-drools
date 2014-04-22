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

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.index.LeftTupleList;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;

import java.util.Arrays;

/**
 * A parent class for all specific LeftTuple specializations
 * @author etirelli
 *
 */
public class BaseLeftTuple
    implements
    Tuple,
    Entry, LeftTuple {
    private static final long  serialVersionUID = 540l;

    private int                index;

    private InternalFactHandle handle;

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

    private LeftTupleSink      sink;
    
    private PropagationContext   propagationContext;    
    
    // node memory
    protected LeftTupleList      memory;
    protected Entry              next;
    protected Entry              previous;    
    
    protected volatile short     stagedType;
    protected LeftTuple          stagedNext;
    protected LeftTuple          stagedPrevious;        

    private Object               object;
    
    private LeftTuple            peer;

    public BaseLeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public BaseLeftTuple(final InternalFactHandle factHandle,
                             final LeftTupleSink sink,
                             final boolean leftTupleMemoryEnabled) {
        this.handle = factHandle;
        this.sink = sink;
        if ( leftTupleMemoryEnabled ) {
            this.handle.addLeftTupleInPosition( this );
        }
    }
    
    public BaseLeftTuple(final InternalFactHandle factHandle,
                         final LeftTuple leftTuple,
                         final LeftTupleSink sink) {
        this.handle = factHandle;
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple;
        this.sink = sink;
    }

    public BaseLeftTuple(final LeftTuple leftTuple,
                         final LeftTupleSink sink,
                         final PropagationContext pctx,
                         final boolean leftTupleMemoryEnabled) {
        this.index = leftTuple.getIndex();
        this.parent = leftTuple;
        this.handle = null;
        this.propagationContext = pctx;

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
    
    public BaseLeftTuple(final LeftTuple leftTuple,
                             RightTuple rightTuple,
                             LeftTupleSink sink) {
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple;
        this.handle = rightTuple.getFactHandle();
        this.propagationContext = rightTuple.getPropagationContext();

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
        if ( rightTuple.lastChild != null ) {
            this.rightParentPrevious = rightTuple.lastChild;
            this.rightParentPrevious.setRightParentNext( this );
        } else {
            rightTuple.firstChild = this;
        }
        rightTuple.lastChild = this;        
        this.sink = sink;
    }    

    public BaseLeftTuple(final LeftTuple leftTuple,
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
    
    public BaseLeftTuple(final LeftTuple leftTuple,
                             final RightTuple rightTuple,
                             final LeftTuple currentLeftChild,
                             final LeftTuple currentRightChild,
                             final LeftTupleSink sink,
                             final boolean leftTupleMemoryEnabled) {
        this.handle = rightTuple.getFactHandle();
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple;
        this.propagationContext = rightTuple.getPropagationContext();

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
                if ( rightTuple.lastChild != null ) {
                    this.rightParentPrevious = rightTuple.lastChild;
                    this.rightParentPrevious.setRightParentNext( this );
                } else {
                    rightTuple.firstChild = this;
                }
                rightTuple.lastChild = this;
            } else {
                // insert before current child
                this.rightParentNext = currentRightChild;
                this.rightParentPrevious = currentRightChild.getRightParentPrevious();
                currentRightChild.setRightParentPrevious( this );
                if( this.rightParentPrevious == null ) {
                    this.rightParent.firstChild = this;
                } else {
                    this.rightParentPrevious.setRightParentNext( this );
                }
            }
        }
        
        this.sink = sink;
    }
    
    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#reAdd()
     */
    public void reAdd() {
        handle.addLastLeftTuple( this );
    }
    
    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#reAddLeft()
     */
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
    
    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#reAddRight()
     */
    public void reAddRight() {
        // make sure we aren't already at the end        
        if ( this.rightParentNext != null ) {
            if ( this.rightParentPrevious != null ) {
                // remove the current LeftTuple from the middle of the chain
                this.rightParentPrevious.setRightParentNext( this.rightParentNext );
                this.rightParentNext.setRightParentPrevious( this.rightParentPrevious );
            } else {
                if( this.rightParent.firstChild == this ) {
                    // remove the current LeftTuple from the start of the chain
                    this.rightParent.firstChild = this.rightParentNext;
                }
                this.rightParentNext.setRightParentPrevious( null );
            }
            // re-add to end            
            this.rightParentPrevious = this.rightParent.lastChild;
            this.rightParentPrevious.setRightParentNext( this );
            this.rightParent.lastChild = this;
            this.rightParentNext = null;
        }
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#unlinkFromLeftParent()
     */
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
                this.handle.removeLeftTuple( this );
            }
            nextParent.setLeftParentPrevious( null );
        } else if ( previousParent != null ) {
            //remove from end
            if ( this.leftParent != null ) {
                this.leftParent.setLastChild( previousParent );
            } else {
                // relevant to the root node, as here the parent is the FactHandle, only happens at rule removal time
                this.handle.removeLeftTuple( this );
            }
            previousParent.setLeftParentNext(  null );
        } else {
            // single remaining item, no previous or next
            if( leftParent != null ) {
                this.leftParent.setFirstChild( null );
                this.leftParent.setLastChild( null );
            } else {
                // it is a root tuple - only happens during rule removal
                this.handle.removeLeftTuple( this );
            }
        }

        this.leftParent = null;
        this.leftParentPrevious = null;
        this.leftParentNext = null;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#unlinkFromRightParent()
     */
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
            this.rightParent.firstChild = nextParent;
            nextParent.setRightParentPrevious( null );
        } else if ( previousParent != null ) {
            // remove from end     
            this.rightParent.lastChild = previousParent;
            previousParent.setRightParentNext(  null );
        } else {
            // single remaining item, no previous or next
            this.rightParent.firstChild = null;
            this.rightParent.lastChild = null;
        }

        this.rightParent = null;
        this.rightParentPrevious = null;
        this.rightParentNext = null;
    }
    
    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getQueueIndex()
     */
    public int getIndex() {
        return this.index;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getLeftTupleSink()
     */
    public LeftTupleSink getLeftTupleSink() {
        return sink;
    }
    
    /* Had to add the set method because sink adapters must override 
     * the tuple sink set when the tuple was created.
     */
    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setLeftTupleSink(org.kie.reteoo.LeftTupleSink)
     */
    public void setLeftTupleSink( LeftTupleSink sink ) {
        this.sink = sink;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getLeftParent()
     */
    public LeftTuple getLeftParent() {
        return leftParent;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setLeftParent(org.kie.reteoo.LeftTuple)
     */
    public void setLeftParent(LeftTuple leftParent) {
        this.leftParent = leftParent;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getLeftParentPrevious()
     */
    public LeftTuple getLeftParentPrevious() {
        return leftParentPrevious;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setLeftParentPrevious(org.kie.reteoo.LeftTuple)
     */
    public void setLeftParentPrevious(LeftTuple leftParentLeft) {
        this.leftParentPrevious = leftParentLeft;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getLeftParentNext()
     */
    public LeftTuple getLeftParentNext() {
        return leftParentNext;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setLeftParentNext(org.kie.reteoo.LeftTuple)
     */
    public void setLeftParentNext(LeftTuple leftParentright) {
        this.leftParentNext = leftParentright;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getRightParent()
     */
    public RightTuple getRightParent() {
        return rightParent;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setRightParent(org.kie.reteoo.RightTuple)
     */
    public void setRightParent(RightTuple rightParent) {
        this.rightParent = rightParent;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getRightParentPrevious()
     */
    public LeftTuple getRightParentPrevious() {
        return rightParentPrevious;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setRightParentPrevious(org.kie.reteoo.LeftTuple)
     */
    public void setRightParentPrevious(LeftTuple rightParentLeft) {
        this.rightParentPrevious = rightParentLeft;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getRightParentNext()
     */
    public LeftTuple getRightParentNext() {
        return rightParentNext;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setRightParentNext(org.kie.reteoo.LeftTuple)
     */
    public void setRightParentNext(LeftTuple rightParentRight) {
        this.rightParentNext = rightParentRight;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#get(int)
     */
    public InternalFactHandle get(final int index) {
        LeftTuple entry = this;
        while ( entry != null && ( entry.getIndex() != index || entry.getLastHandle() == null ) ) {
            entry = entry.getParent();
        }
        return entry == null ? null : entry.getHandle();
    }
    
    public void setFactHandle(InternalFactHandle handle) {
        this.handle = handle;
    }

    /* (non-Javadoc)

     * @see org.kie.reteoo.LeftTuple#getLastHandle()
     */
    public InternalFactHandle getLastHandle() {
        return this.handle;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#get(org.kie.rule.Declaration)
     */
    public InternalFactHandle get(final Declaration declaration) {
        return get( declaration.getPattern().getOffset() );
    }

     /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#toFactHandles()
     */
    public InternalFactHandle[] toFactHandles() {
        InternalFactHandle[] handles = new InternalFactHandle[this.index + 1];
        LeftTuple entry = this;
        while ( entry != null ) {
            if ( entry.getHandle() != null ) {
                // eval, not, exists have no right input
                handles[entry.getIndex()] = entry.getHandle();
            }
            entry = entry.getParent();
        }
        return handles;
    }
    

    public void clearBlocker() {
        throw new UnsupportedOperationException();
    }    

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setBlocker(org.kie.reteoo.RightTuple)
     */
    public void setBlocker(RightTuple blocker) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getBlocker()
     */
    public RightTuple getBlocker() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getBlockedPrevious()
     */
    public LeftTuple getBlockedPrevious() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setBlockedPrevious(org.kie.reteoo.LeftTuple)
     */
    public void setBlockedPrevious(LeftTuple blockerPrevious) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getBlockedNext()
     */
    public LeftTuple getBlockedNext() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setBlockedNext(org.kie.reteoo.LeftTuple)
     */
    public void setBlockedNext(LeftTuple blockerNext) {
        throw new UnsupportedOperationException();
    }
    
    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getObject()
     */
    public final Object getObject() {
        return this.object;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setObject(java.lang.Object)
     */
    public final void setObject(final Object object) {
        this.object = object;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#toString()
     */
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        LeftTuple entry = this;
        while ( entry != null ) {
            //buffer.append( entry.handle );
            buffer.append(entry.getHandle());
            if ( entry.getParent() != null ) {
                buffer.append("\n");
            }
            entry = entry.getParent();
        }
        return buffer.toString();
    }

    
    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#hashCode()
     */
    public int hashCode() {
        return handle == null ? 0 : handle.hashCode();
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#equals(org.kie.reteoo.LeftTuple)
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

        if ( this.handle != other.getHandle() ) {
            return false;
        }
        
        if ( this.parent == null ) {
            return (other.getParent() == null);
        } else {
            return this.parent.equals( other.getParent() );
        }
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if( object instanceof LeftTuple ) { 
            return equals( (LeftTuple) object );
        } else { 
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#size()
     */
    public int size() {
        return this.index + 1;
    }
    
    

    public InternalFactHandle getHandle() {
        return handle;
    }

    public void setHandle(InternalFactHandle handle) {
        this.handle = handle;
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

    public LeftTupleSink getSink() {
        return sink;
    }

    public void setSink(LeftTupleSink sink) {
        this.sink = sink;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setParent(LeftTuple parent) {
        this.parent = parent;
    }
    
    
    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getMemory()
     */
    public LeftTupleList getMemory() {
        return this.memory;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setMemory(org.kie.core.util.LeftTupleList)
     */
    public void setMemory(LeftTupleList memory) {
        this.memory = memory;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getPrevious()
     */
    public Entry getPrevious() {
        return previous;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setPrevious(org.kie.core.util.Entry)
     */
    public void setPrevious(Entry previous) {
        this.previous = previous;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setNext(org.kie.core.util.Entry)
     */
    public void setNext(final Entry next) {
        this.next = next;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#remove()
     */
    public Entry getNext() {
        return this.next;
    }               

    public short getStagedType() {
        return stagedType;
    }

    public void setStagedType(short stagedType) {
        this.stagedType = stagedType;
    }

    public LeftTuple getStagedNext() {
        return stagedNext;
    }

    public void setStagedNext(LeftTuple stageNext) {
        this.stagedNext = stageNext;
    }

    public LeftTuple getStagedPrevious() {
        return stagedPrevious;
    }

    public void setStagePrevious(LeftTuple stagePrevious) {
        this.stagedPrevious = stagePrevious;
    }
    
    public void clearStaged() {
        stagedType = LeftTuple.NONE;
        stagedNext = null;
        stagedPrevious = null;
        if (object == Boolean.TRUE) {
            object = null;
        }
    }

    public LeftTuple getPeer() {
        return peer;
    }

    public void setPeer(LeftTuple peer) {
        this.peer = peer;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getSubTuple(int)
     */
    public LeftTuple getSubTuple(final int elements) {
        LeftTuple entry = this;
        if ( elements <= this.size() ) {
            final int lastindex = elements - 1;

            while ( entry.getIndex() != lastindex || entry.getLastHandle() == null ) {
                entry = entry.getParent();
            }
        }
        return entry;
    }

    public LeftTuple skipEmptyHandles() {
        LeftTuple entry = this;
        while ( entry != null && entry.getLastHandle() == null ) {
            entry = entry.getParent();
        }
        return entry;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#toObjectArray()
     */
    public Object[] toObjectArray() {
        Object[] objects = new Object[this.index + 1];
        LeftTuple entry = this;
        while ( entry != null ) {
            if ( entry.getLastHandle() != null ) {
                // can be null for eval, not and exists that have no right input
                Object object = entry.getLastHandle().getObject();
                objects[entry.getIndex()] = object;
            }
            entry = entry.getParent();
        }
        return objects;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getParent()
     */
    public LeftTuple getParent() {
        return parent;
    }

    public LeftTuple getRootLeftTuple() {
        if ( parent == null ) {
            return this;
        }

        LeftTuple currentLt = parent;
        while (currentLt.getParent() != null ) {
            currentLt = currentLt.getParent();
        }
        return currentLt;
    }
    
    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#toTupleTree(int)
     */
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
            ids[entry.getIndex()] = entry.getLastHandle().getId();
            entry = entry.getParent();
        }
        builder.append( Arrays.toString( ids ) )
               .append( " sink=" )
               .append( this.sink.getClass().getSimpleName() )
               .append( "(" ).append( sink.getId() ).append( ")" );
        return  builder.toString();
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#increaseActivationCountForEvents()
     */
    public void increaseActivationCountForEvents() {
        for ( LeftTuple entry = this; entry != null; entry = entry.getParent() ) {
            if(entry.getLastHandle() != null &&  entry.getLastHandle().isEvent() ) {
                // can be null for eval, not and exists that have no right input
                ((EventFactHandle)entry.getLastHandle()).increaseActivationsCount();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#decreaseActivationCountForEvents()
     */
    public void decreaseActivationCountForEvents() {
        for ( LeftTuple entry = this; entry != null; entry = entry.getParent() ) {
            if( entry.getLastHandle() != null &&  entry.getLastHandle().isEvent() ) {
                // can be null for eval, not and exists that have no right input
                ((EventFactHandle)entry.getLastHandle()).decreaseActivationsCount();
            }
        }
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
    
    public void initPeer(BaseLeftTuple original, LeftTupleSink sink) {
        this.index = original.index;
        this.parent = original.parent;
        
        this.handle = original.handle;
        this.propagationContext = original.propagationContext;      
        this.sink = sink;

    }

    
}
