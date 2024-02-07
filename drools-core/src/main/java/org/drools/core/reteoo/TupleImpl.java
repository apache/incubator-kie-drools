/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo;

import org.drools.base.rule.Declaration;
import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.util.index.TupleList;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public abstract class TupleImpl implements Tuple<TupleImpl> {
    private static final long          serialVersionUID = 540l;

    private            int           index;

    private TupleImpl parent;
    private TupleImpl rightParent;
    private TupleImpl rightParentPrevious;
    private TupleImpl rightParentNext;
    private   short     stagedType;

    private Object contextObject;

    protected InternalFactHandle handle;

    private PropagationContext propagationContext;

    protected TupleImpl stagedNext;
    protected TupleImpl stagedPrevious;

    private TupleImpl previous;
    private TupleImpl next;

    private TupleImpl leftParent;
    protected TupleImpl handlePrevious;
    protected TupleImpl handleNext;

    private Sink sink;

    private boolean expired;

    // node memory
    protected TupleList memory;
    // children
    protected TupleImpl               firstChild;
    protected TupleImpl               lastChild;
    private TupleImpl               peer;
    private short                   stagedTypeForQueries;

    public TupleImpl() {
        // constructor needed for serialisation
    }

    public TupleImpl(InternalFactHandle factHandle,
                     Sink sink,
                     boolean leftTupleMemoryEnabled) {
        setSink(sink);
        this.handle = factHandle;
        if ( leftTupleMemoryEnabled ) {
            factHandle.addLastLeftTuple( this );
        }
    }

    public TupleImpl(InternalFactHandle factHandle,
                     TupleImpl leftTuple,
                     Sink sink) {
        setSink(sink);
        this.handle = factHandle;
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple.getNextParentWithHandle();
        this.leftParent = leftTuple;
    }

    public TupleImpl(TupleImpl leftTuple,
                     Sink sink,
                     PropagationContext pctx,
                     boolean leftTupleMemoryEnabled) {
        setSink(sink);
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple.getNextParentWithHandle();
        this.leftParent = leftTuple;
        setPropagationContext( pctx );

        if ( leftTupleMemoryEnabled ) {
            if ( leftTuple.getLastChild() != null ) {
                this.handlePrevious = leftTuple.getLastChild();
                this.handlePrevious.setHandleNext( this );
            } else {
                leftTuple.setFirstChild( this );
            }
            leftTuple.setLastChild( this );
        }
    }

    public TupleImpl(TupleImpl leftTuple,
                     TupleImpl rightTuple,
                     Sink sink) {
        setSink(sink);
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple.getNextParentWithHandle();
        this.leftParent = leftTuple;
        this.rightParent = rightTuple;

        this.handle = rightTuple.getFactHandle();
        setPropagationContext( rightTuple.getPropagationContext() );

        // insert at the end of the list
        if ( leftTuple.getLastChild() != null ) {
            this.handlePrevious = leftTuple.getLastChild();
            this.handlePrevious.setHandleNext( this );
        } else {
            leftTuple.setFirstChild( this );
        }
        leftTuple.setLastChild( this );

        // insert at the end of the list
        if ( rightTuple.getLastChild() != null ) {
            this.rightParentPrevious = rightTuple.getLastChild();
            this.rightParentPrevious.setRightParentNext( this );
        } else {
            rightTuple.setFirstChild( this );
        }
        rightTuple.setLastChild( this );
    }

    public TupleImpl(TupleImpl leftTuple,
                     TupleImpl rightTuple,
                     TupleImpl currentLeftChild,
                     TupleImpl currentRightChild,
                     Sink sink,
                     boolean leftTupleMemoryEnabled) {
        setSink(sink);
        this.handle = rightTuple.getFactHandle();
        this.index = leftTuple.getIndex() + 1;
        this.parent = leftTuple.getNextParentWithHandle();
        this.leftParent = leftTuple;
        this.rightParent = rightTuple;
        setPropagationContext( rightTuple.getPropagationContext() );

        if ( leftTupleMemoryEnabled ) {
            if( currentLeftChild == null ) {
                // insert at the end of the list
                if ( leftTuple.getLastChild() != null ) {
                    this.handlePrevious = leftTuple.getLastChild();
                    this.handlePrevious.setHandleNext( this );
                } else {
                    leftTuple.setFirstChild( this );
                }
                leftTuple.setLastChild( this );
            } else {
                // insert before current child
                this.handleNext = currentLeftChild;
                this.handlePrevious = currentLeftChild.getHandlePrevious();
                currentLeftChild.setHandlePrevious( this );
                if( this.handlePrevious == null ) {
                    this.leftParent.setFirstChild( this  );
                } else {
                    this.handlePrevious.setHandleNext( this );
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
    }

    public Object getObject(Declaration declaration) {
        return getObject(declaration.getTupleIndex());
    }

    public Object getContextObject() {
        return this.contextObject;
    }

    public final void setContextObject( final Object contextObject ) {
        this.contextObject = contextObject;
    }

    public short getStagedType() {
        return stagedType;
    }

    public void setStagedType(short stagedType) {
        this.stagedType = stagedType;
    }

    @Override
    public TupleImpl getFirstChild() {
        return firstChild;
    }

    @Override
    public void setFirstChild(TupleImpl firstChild) {
        this.firstChild = firstChild;
    }

    @Override
    public TupleImpl getLastChild() {
        return lastChild;
    }

    @Override
    public void setLastChild(TupleImpl lastChild) {
        this.lastChild = lastChild;
    }

    public TupleImpl getRightParent() {
        return rightParent;
    }

    public void setRightParent(TupleImpl rightParent) {
        this.rightParent = rightParent;
    }

    public TupleImpl getRightParentPrevious() {
        return rightParentPrevious;
    }

    public void setRightParentPrevious(TupleImpl rightParentLeft) {
        this.rightParentPrevious = rightParentLeft;
    }

    public TupleImpl getRightParentNext() {
        return rightParentNext;
    }

    public void setRightParentNext(TupleImpl rightParentRight) {
        this.rightParentNext = rightParentRight;
    }

    @Override
    public FactHandle get(int index) {
        TupleImpl entry =  this;
        while ( entry.getIndex() != index) {
            entry = entry.getParent();
        }
        return entry.getFactHandle();
    }

    public FactHandle[] toFactHandles() {
        // always use the count of the node that created join (not the sink target)
        FactHandle[] handles = new FactHandle[((LeftTupleSinkNode)getSink()).getLeftTupleSource().getObjectCount()];
        TupleImpl    entry   =  skipEmptyHandles();
        for(int i = handles.length-1; i >= 0; i--) {
            handles[i] = entry.getFactHandle();
            entry = entry.getParent();
        }
        return handles;
    }

    public Object[] toObjects(boolean reverse) {
        // always use the count of the node that created join (not the sink target)
        Object[]  objs  = new Object[((LeftTupleSinkNode)getSink()).getLeftTupleSource().getObjectCount()];
        TupleImpl entry =  skipEmptyHandles();

        if (!reverse) {
            for (int i = objs.length - 1; i >= 0; i--) {
                objs[i] = entry.getFactHandle().getObject();
                entry = entry.getParent();
            }
        } else {
            for (int i = 0; i < objs.length; i++) {
                objs[i] = entry.getFactHandle().getObject();
                entry = entry.getParent();
            }
        }

        return objs;
    }

    public void clearBlocker() {
        throw new UnsupportedOperationException();
    }

    public void setBlocker(RightTuple blocker) {
        throw new UnsupportedOperationException();
    }

    public RightTuple getBlocker() {
        throw new UnsupportedOperationException();
    }

    public LeftTuple getBlockedPrevious() {
        throw new UnsupportedOperationException();
    }

    public void setBlockedPrevious(LeftTuple blockerPrevious) {
        throw new UnsupportedOperationException();
    }

    public LeftTuple getBlockedNext() {
        throw new UnsupportedOperationException();
    }

    public void setBlockedNext(LeftTuple blockerNext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        Tuple entry = skipEmptyHandles();;
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
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        TupleImpl other =  (TupleImpl) object;

        // A AbstractTuple is  only the same if it has the same hashCode, factId and parent
        if ( this.hashCode() != other.hashCode() || getFactHandle() != other.getFactHandle() ) {
            return false;
        }

        if ( this.parent == null ) {
            return (other.getParent() == null);
        } else {
            return this.parent.equals( other.getParent() );
        }
    }

    @Override
    public int size() {
        return this.index + 1;
    }

    @Override
    public TupleImpl getStagedNext() {
        return  stagedNext;
    }

    @Override
    public TupleImpl getStagedPrevious() {
        return  stagedPrevious;
    }

    public void clearStaged() {
        if (getContextObject() == Boolean.TRUE) {
            setContextObject( null );
        }
        this.stagedType = TupleImpl.NONE;
        this.stagedNext = null;
        this.stagedPrevious = null;
    }

    public TupleImpl getPeer() {
        return peer;
    }

    public void setPeer(TupleImpl peer) {
        this.peer = peer;
    }

    @Override
    public TupleImpl getSubTuple(final int elements) {
        TupleImpl entry = this;
        if ( elements <= this.size() ) {
            final int lastindex = elements - 1;

            while ( entry.getIndex() != lastindex ) {
                // This uses getLeftParent, instead of getParent, as the subnetwork tuple
                // parent could be any node
                entry = entry.getParent();
            }
        }
        return entry;
    }

    @Override
    public TupleImpl getParent() {
        return parent;
    }

    protected String toExternalString() {
        StringBuilder builder = new StringBuilder();
        builder.append( String.format( "%08X", System.identityHashCode( this ) ) ).append( ":" );
        long[] ids = new long[this.index+1];
        Tuple entry = skipEmptyHandles();;
        while( entry != null ) {
            ids[entry.getIndex()] = entry.getFactHandle().getId();
            entry = entry.getParent();
        }
        builder.append(Arrays.toString(ids))
               .append( " sink=" )
               .append( this.getSink().getClass().getSimpleName() )
               .append( "(" ).append( getSink().getId() ).append( ")" );
        return  builder.toString();
    }

    @Override
    public void clear() {
        this.previous = null;
        this.next = null;
        this.memory = null;
    }

    public InternalFactHandle getFactHandle() {
        return handle;
    }

    /**
     * This method is used by the consequence invoker (generated via asm by the ConsequenceGenerator)
     * to always pass to the consequence the original fact handle even in case when it has been
     * cloned and linked by a WindowNode
     */
    public InternalFactHandle getOriginalFactHandle() {
        InternalFactHandle linkedFH = handle.isEvent() ? ((DefaultEventHandle)handle).getLinkedFactHandle() : null;
        return linkedFH != null ? linkedFH : handle;
    }

    public void setFactHandle( FactHandle handle ) {
        this.handle = (InternalFactHandle) handle;
    }

    public PropagationContext getPropagationContext() {
        return propagationContext;
    }

    public void setPropagationContext(PropagationContext propagationContext) {
        this.propagationContext = propagationContext;
    }

    public void setStagedNext(TupleImpl stageNext) {
        this.stagedNext = stageNext;
    }

    public void setStagedPrevious( TupleImpl stagedPrevious) {
        this.stagedPrevious = stagedPrevious;
    }

    public TupleImpl getPrevious() {
        return previous;
    }

    public void setPrevious(TupleImpl previous) {
        this.previous = previous;
    }

    public TupleImpl getNext() {
        return next;
    }

    public void setNext(TupleImpl next) {
        this.next = next;
    }

    @Override
    public FactHandle get(Declaration declaration) {
        return get(declaration.getTupleIndex());
    }

    @Override
    public TupleImpl getTuple(int index) {
        TupleImpl entry = this;
        while ( entry.getIndex() != index) {
            entry = entry.getParent();
        }
        return entry;
    }

    @Override
    public TupleImpl getRootTuple() {
        return getTuple(0);
    }

    @Override
    public TupleImpl skipEmptyHandles() {
        // because getParent now only returns a tuple that as an FH, we only need to cheeck the current tuple,
        // and not the parent chain
        return getFactHandle() == null ? getParent() : this;
    }

    public TupleImpl getLeftParent() {
        return leftParent;
    }

    public void setLeftParent(TupleImpl leftParent) {
        this.leftParent = leftParent;
    }

    public TupleImpl getNextParentWithHandle() {
        // if parent is null, then we are LIAN
        return (handle!=null) ? this : parent != null ? parent.getNextParentWithHandle() : this;
    }

    public abstract void reAdd();

    public void reAddLeft() {
        // The parent can never be the FactHandle (root AbstractTuple) as that is handled by reAdd()
        // make sure we aren't already at the end
        if ( this.handleNext != null ) {
            if ( this.handlePrevious != null ) {
                // remove the current AbstractTuple from the middle of the chain
                this.handlePrevious.setHandleNext( this.handleNext );
                this.handleNext.setHandlePrevious( this.handlePrevious );
            } else {
                if( this.leftParent.getFirstChild() == this ) {
                    // remove the current AbstractTuple from start start of the chain
                    this.leftParent.setFirstChild( getHandleNext() );
                }
                this.handleNext.setHandlePrevious( null );
            }
            // re-add to end
            this.handlePrevious = this.leftParent.getLastChild();
            this.handlePrevious.setHandleNext( this );
            this.leftParent.setLastChild( this );
            this.handleNext = null;
        }
    }

    public void reAddRight() {
        // make sure we aren't already at the end
        if ( this.rightParentNext != null ) {
            if ( this.rightParentPrevious != null ) {
                // remove the current AbstractTuple from the middle of the chain
                this.rightParentPrevious.setRightParentNext( this.rightParentNext );
                this.rightParentNext.setRightParentPrevious( this.rightParentPrevious );
            } else {
                if( this.rightParent.getFirstChild() == this ) {
                    // remove the current AbstractTuple from the start of the chain
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
        TupleImpl previousParent = getHandlePrevious();
        TupleImpl nextParent     = getHandleNext();

        if ( previousParent != null && nextParent != null ) {
            //remove  from middle
            this.handlePrevious.setHandleNext( nextParent );
            this.handleNext.setHandlePrevious( previousParent );
        } else if ( nextParent != null ) {
            //remove from first
            if ( this.leftParent != null ) {
                this.leftParent.setFirstChild( nextParent );
            } else {
                // This is relevant to the root node and only happens at rule removal time
                getFactHandle().removeLeftTuple( this );
            }
            nextParent.setHandlePrevious( null );
        } else if ( previousParent != null ) {
            //remove from end
            if ( this.leftParent != null ) {
                this.leftParent.setLastChild( previousParent );
            } else {
                // relevant to the root node, as here the parent is the FactHandle, only happens at rule removal time
                getFactHandle().removeLeftTuple( this );
            }
            previousParent.setHandleNext( null );
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

        this.handlePrevious = null;
        this.handleNext = null;
    }

    @Override
    public void unlinkFromRightParent() {
        doUnlinkFromRightParent();
    }

    public void doUnlinkFromRightParent() {
        if ( this.rightParent == null ) {
            // no right parent;
            return;
        }

        TupleImpl previousParent = this.rightParentPrevious;
        TupleImpl nextParent     = this.rightParentNext;

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

        this.rightParentPrevious = null;
        this.rightParentNext = null;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    /* Had to add the set method because sink adapters must override
     * the tuple sink set when the tuple was created.
     */
    public void setLeftTupleSink( LeftTupleSink sink ) {
        setSink(sink);
    }

    @Override
    public TupleImpl getHandlePrevious() {
        return handlePrevious;
    }

    @Override
    public void setHandlePrevious(TupleImpl handlePrevious) {
        this.handlePrevious = handlePrevious;
    }

    @Override
    public TupleImpl getHandleNext() {
        return handleNext;
    }

    @Override
    public void setHandleNext(TupleImpl handleNext) {
        this.handleNext = handleNext;
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    public void setExpired() {
        this.expired = true;
    }

    public Sink getSink() {
        return sink;
    }

    protected void setSink(Sink sink) {
        this.sink = sink;
    }

    @Override
    public TupleList getMemory() {
        return this.memory;
    }

    @Override
    public void setMemory(TupleList memory) {
        this.memory = memory;
    }

    public void initPeer(TupleImpl original, Sink sink) {
        this.index = original.index;
        this.parent = original.parent;
        this.leftParent = original.leftParent;

        setFactHandle( original.getFactHandle() );
        setPropagationContext( original.getPropagationContext() );
        setSink(sink);
    }

    @Override
    public Object getObject(int index) {
        return get(index).getObject();
    }

    @Override
    public abstract ObjectTypeNodeId getInputOtnId();


    public InternalFactHandle getFactHandleForEvaluation() {
        throw new UnsupportedOperationException("Only RightTupleImpl implements this");
    }

    public short getStagedTypeForQueries() {
        return stagedTypeForQueries;
    }

    public void setStagedTypeForQueries(short stagedTypeForQueries) {
        this.stagedTypeForQueries = stagedTypeForQueries;
    }

    public boolean isStagedOnRight() {
        return false;
    }

    public Collection<Object> getAccumulatedObjects() {
        if (getFirstChild() == null) {
            return Collections.emptyList();
        }
        Collection<Object> result = new ArrayList<>();
        if ( getContextObject() instanceof AccumulateNode.AccumulateContext ) {
            for (TupleImpl child = getFirstChild(); child != null; child = child.getHandleNext()) {
                result.add(child.getContextObject());
            }
        }

        if ( getFirstChild().getRightParent().isSubnetworkTuple()) {
            TupleImpl leftParent = getFirstChild().getRightParent().getLeftParent();
            result.addAll( leftParent.getAccumulatedObjects() );
        }
        return result;
    }

    public abstract boolean isLeftTuple();

    public boolean isFullMatch() {
        return false;
    }

    public boolean isSubnetworkTuple() {
        return false;
    }
}
