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

public class EvalNodeLeftTuple
    implements
    Tuple,
    Entry, LeftTuple {
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

    // children
    private LeftTuple          firstChild;
    private LeftTuple          lastChild;

    private LeftTupleSink      sink;

    public EvalNodeLeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public EvalNodeLeftTuple(final InternalFactHandle factHandle,
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
                this.leftParentPrevious.setLeftParentNext( this );
                handle.setLastLeftTuple( this );
            }
        }
        this.sink = sink;
    }

    public EvalNodeLeftTuple(final LeftTuple leftTuple,
                         LeftTupleSink sink,
                         boolean leftTupleMemoryEnabled) {
        this.index = leftTuple.getIndex();
        this.parent = leftTuple.getParent();
        this.handle = leftTuple.getHandle();

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
    
    public EvalNodeLeftTuple(final LeftTuple leftTuple,
                             RightTuple rightTuple,
                             LeftTupleSink sink) {
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple;
        this.handle = rightTuple.getFactHandle();

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
        if ( rightTuple.lastChild != null ) {
            this.rightParentPrevious = rightTuple.lastChild;
            this.rightParentPrevious.setRightParentNext( this );
        } else {
            rightTuple.firstChild = this;
        }
        rightTuple.lastChild = this;        
        this.sink = sink;
    }    

    public EvalNodeLeftTuple(final LeftTuple leftTuple,
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
    
    public EvalNodeLeftTuple(final LeftTuple leftTuple,
                             final RightTuple rightTuple,
                             final LeftTuple currentLeftChild,
                             final LeftTuple currentRightChild,
                             final LeftTupleSink sink,
                             final boolean leftTupleMemoryEnabled) {
        this.handle = rightTuple.getFactHandle();
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple;

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
     * @see org.drools.reteoo.LeftTuple#reAdd()
     */
    public void reAdd() {
        LeftTuple first = handle.getLastLeftTuple();
        if ( first == null ) {
            // node other LeftTuples, just add.
            handle.setFirstLeftTuple( this );
            handle.setLastLeftTuple( this );
        } else {
            
            handle.getLastLeftTuple().setLeftParentNext( this );
            this.leftParentPrevious = handle.getLastLeftTuple();
            handle.setLastLeftTuple( this );
        }
    }
    
    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#reAddLeft()
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
     * @see org.drools.reteoo.LeftTuple#reAddRight()
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
     * @see org.drools.reteoo.LeftTuple#unlinkFromLeftParent()
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
                this.handle.setFirstLeftTuple( nextParent );
            }
            nextParent.setLeftParentPrevious( null );
        } else if ( previousParent != null ) {
            //remove from end
            if ( this.leftParent != null ) {
                this.leftParent.setLastChild( previousParent );
            } else {
                // relevant to the root node, as here the parent is the FactHandle, only happens at rule removal time
                this.handle.setLastLeftTuple( previousParent );
            }
            previousParent.setLeftParentNext(  null );
        } else {
            // single remaining item, no previous or next
            if( leftParent != null ) {
                this.leftParent.setFirstChild( null );
                this.leftParent.setLastChild( null );
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

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#unlinkFromRightParent()
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

        this.blocker = null;

        this.rightParent = null;
        this.rightParentPrevious = null;
        this.rightParentNext = null;
    }
    
    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getIndex()
     */
    public int getIndex() {
        return this.index;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getLeftTupleSink()
     */
    public LeftTupleSink getLeftTupleSink() {
        return sink;
    }
    
    /* Had to add the set method because sink adapters must override 
     * the tuple sink set when the tuple was created.
     */
    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setLeftTupleSink(org.drools.reteoo.LeftTupleSink)
     */
    public void setLeftTupleSink( LeftTupleSink sink ) {
        this.sink = sink;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getLeftParent()
     */
    public LeftTuple getLeftParent() {
        return leftParent;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setLeftParent(org.drools.reteoo.LeftTuple)
     */
    public void setLeftParent(LeftTuple leftParent) {
        this.leftParent = leftParent;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getLeftParentPrevious()
     */
    public LeftTuple getLeftParentPrevious() {
        return leftParentPrevious;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setLeftParentPrevious(org.drools.reteoo.LeftTuple)
     */
    public void setLeftParentPrevious(LeftTuple leftParentLeft) {
        this.leftParentPrevious = leftParentLeft;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getLeftParentNext()
     */
    public LeftTuple getLeftParentNext() {
        return leftParentNext;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setLeftParentNext(org.drools.reteoo.LeftTuple)
     */
    public void setLeftParentNext(LeftTuple leftParentright) {
        this.leftParentNext = leftParentright;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getRightParent()
     */
    public RightTuple getRightParent() {
        return rightParent;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setRightParent(org.drools.reteoo.RightTuple)
     */
    public void setRightParent(RightTuple rightParent) {
        this.rightParent = rightParent;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getRightParentPrevious()
     */
    public LeftTuple getRightParentPrevious() {
        return rightParentPrevious;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setRightParentPrevious(org.drools.reteoo.LeftTuple)
     */
    public void setRightParentPrevious(LeftTuple rightParentLeft) {
        this.rightParentPrevious = rightParentLeft;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getRightParentNext()
     */
    public LeftTuple getRightParentNext() {
        return rightParentNext;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setRightParentNext(org.drools.reteoo.LeftTuple)
     */
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

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#get(int)
     */
    public InternalFactHandle get(final int index) {
        LeftTuple entry = this;
        while ( entry.getIndex() != index ) {
            entry = entry.getParent();
        }
        return entry.getHandle();
    }
    
    public void setFactHandle(InternalFactHandle handle) {
        this.handle = handle;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getMemory()
     */
    public LeftTupleList getMemory() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setMemory(org.drools.core.util.LeftTupleList)
     */
    public void setMemory(LeftTupleList memory) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getPrevious()
     */
    public Entry getPrevious() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setPrevious(org.drools.core.util.Entry)
     */
    public void setPrevious(Entry previous) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setNext(org.drools.core.util.Entry)
     */
    public void setNext(final Entry next) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getNext()
     */
    public Entry getNext() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getLastHandle()
     */
    public InternalFactHandle getLastHandle() {
        return this.handle;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#get(org.drools.rule.Declaration)
     */
    public InternalFactHandle get(final Declaration declaration) {
        return get( declaration.getPattern().getOffset() );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getFactHandles()
     */
    public InternalFactHandle[] getFactHandles() {
        InternalFactHandle[] handles = new InternalFactHandle[this.index + 1];
        LeftTuple entry = this;
        int i = 0;
        while ( entry != null ) {
            handles[i++] = entry.getHandle();
            entry = entry.getParent();
        }
        return handles;
    }
     /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#toFactHandles()
     */
    public InternalFactHandle[] toFactHandles() {
        InternalFactHandle[] handles = new InternalFactHandle[this.index + 1];
        LeftTuple entry = this;

        while ( entry != null ) {
            handles[entry.getIndex()] = entry.getHandle();
            entry = entry.getParent();
        }
        return handles;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setBlocker(org.drools.reteoo.RightTuple)
     */
    public void setBlocker(RightTuple blocker) {
        this.blocker = blocker;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getBlocker()
     */
    public RightTuple getBlocker() {
        return this.blocker;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getBlockedPrevious()
     */
    public LeftTuple getBlockedPrevious() {
        return this.blockedPrevious;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setBlockedPrevious(org.drools.reteoo.LeftTuple)
     */
    public void setBlockedPrevious(LeftTuple blockerPrevious) {
        this.blockedPrevious = blockerPrevious;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getBlockedNext()
     */
    public LeftTuple getBlockedNext() {
        return this.blockedNext;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setBlockedNext(org.drools.reteoo.LeftTuple)
     */
    public void setBlockedNext(LeftTuple blockerNext) {
        this.blockedNext = blockerNext;
    }
    
    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getObject()
     */
    public Object getObject() {
        return this.object;
    }
    
    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setObject(java.lang.Object)
     */
    public void setObject(final Object object) {
        this.object = object;
    }

//    public int hashCode() {
//        return this.hashCode;
//    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#toString()
     */
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        LeftTuple entry = this;
        while ( entry != null ) {
            //buffer.append( entry.handle );
            buffer.append(entry.getHandle()).append("\n");
            entry = entry.getParent();
        }
        return buffer.toString();
    }

    
    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#hashCode()
     */
    public int hashCode() {
        return this.handle.hashCode();
    }
    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#equals(org.drools.reteoo.LeftTuple)
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
        
//        if ( this.sink.getId() != other.sink.getId() ) {
//            return false;
//        }
//        
//        if ( this.index != other.index ) {
//            return false;
//        }

        if ( this.parent == null ) {
            return (other.getParent() == null);
        } else {
            return this.parent.equals( other.getParent() );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        // we know the object is never null and always of the  type ReteTuple    
        return equals( (LeftTuple) object );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#size()
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
     * @see org.drools.reteoo.LeftTuple#getSubTuple(int)
     */
    public LeftTuple getSubTuple(final int elements) {
        LeftTuple entry = this;
        if ( elements < this.size() ) {
            final int lastindex = elements - 1;

            while ( entry.getIndex() != lastindex ) {
                entry = entry.getParent();
            }
        }
        return entry;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#toObjectArray()
     */
    public Object[] toObjectArray() {
        Object[] objects = new Object[this.index + 1];
        LeftTuple entry = this;
        while ( entry != null ) {
            Object object = entry.getLastHandle().getObject();
            objects[entry.getIndex()] = object;
            entry = entry.getParent();
        }
        return objects;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#getParent()
     */
    public LeftTuple getParent() {
        return parent;
    }
    
    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#toTupleTree(int)
     */
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
            ids[entry.getIndex()] = entry.getLastHandle().getId();
            entry = entry.getParent();
        }
        builder.append( Arrays.toString( ids ) )
               .append( " activation=" )
               .append( this.object != null ? this.object : "null" )
               .append( " sink=" )
               .append( this.sink.getClass().getSimpleName() )
               .append( "(" ).append( sink.getId() ).append( ")" );
        return  builder.toString();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#increaseActivationCountForEvents()
     */
    public void increaseActivationCountForEvents() {
        for ( LeftTuple entry = this; entry != null; entry = entry.getParent() ) {
            if( entry.getLastHandle().isEvent() ) {
                ((EventFactHandle)entry.getLastHandle()).increaseActivationsCount();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#decreaseActivationCountForEvents()
     */
    public void decreaseActivationCountForEvents() {
        for ( LeftTuple entry = this; entry != null; entry = entry.getParent() ) {
            if( entry.getLastHandle().isEvent() ) {
                ((EventFactHandle)entry.getLastHandle()).decreaseActivationsCount();
            }
        }
    }    
}
