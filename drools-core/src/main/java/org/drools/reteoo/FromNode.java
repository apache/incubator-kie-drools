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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.UpdateContext;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.index.LeftTupleList;
import org.drools.core.util.LinkedList;
import org.drools.marshalling.impl.PersisterHelper;
import org.drools.marshalling.impl.ProtobufInputMarshaller;
import org.drools.marshalling.impl.ProtobufInputMarshaller.TupleKey;
import org.drools.marshalling.impl.ProtobufMessages;
import org.drools.marshalling.impl.ProtobufMessages.FactHandle;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.rule.From;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;

public class FromNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    NodeMemory {
    private static final long          serialVersionUID = 510l;

    protected DataProvider               dataProvider;
    protected LeftTupleSource            tupleSource;
    protected AlphaNodeFieldConstraint[] alphaConstraints;
    protected BetaConstraints            betaConstraints;

    protected LeftTupleSinkNode          previousTupleSinkNode;
    protected LeftTupleSinkNode          nextTupleSinkNode;
    
    protected From                       from;
    protected Class<?>                   resultClass;

    protected boolean                    tupleMemoryEnabled;

    public FromNode() {
    }

    public FromNode(final int id,
                    final DataProvider dataProvider,
                    final LeftTupleSource tupleSource,
                    final AlphaNodeFieldConstraint[] constraints,
                    final BetaConstraints binder,
                    final boolean tupleMemoryEnabled,
                    final BuildContext context,
                    final From from) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.dataProvider = dataProvider;
        this.tupleSource = tupleSource;
        this.alphaConstraints = constraints;
        this.betaConstraints = (binder == null) ? EmptyBetaConstraints.getInstance() : binder;
        this.tupleMemoryEnabled = tupleMemoryEnabled;
        this.from = from;
        resultClass = ((ClassObjectType)this.from.getResultPattern().getObjectType()).getClassType();

        initMasks(context, tupleSource);
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        dataProvider = (DataProvider) in.readObject();
        tupleSource = (LeftTupleSource) in.readObject();
        alphaConstraints = (AlphaNodeFieldConstraint[]) in.readObject();
        betaConstraints = (BetaConstraints) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        from = (From) in.readObject();
        resultClass = ((ClassObjectType)this.from.getResultPattern().getObjectType()).getClassType();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( dataProvider );
        out.writeObject( tupleSource );
        out.writeObject( alphaConstraints );
        out.writeObject( betaConstraints );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeObject( from );
    }

    /**
     * @inheritDoc
     */
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        Map<Object, RightTuple> matches = null;
        boolean useLeftMemory = true;       
        if ( !this.tupleMemoryEnabled ) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = ((InternalFactHandle) leftTuple.get( 0 )).getObject();
            if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                useLeftMemory = false;
            }
        }

        if ( useLeftMemory ) {
            memory.betaMemory.getLeftTupleMemory().add( leftTuple );
            matches = new LinkedHashMap<Object, RightTuple>();
            leftTuple.setObject( matches );
        }         

        this.betaConstraints.updateFromTuple( memory.betaMemory.getContext(),
                                              workingMemory,
                                              leftTuple );

        for ( final java.util.Iterator< ? > it = this.dataProvider.getResults( leftTuple,
                                                                               workingMemory,
                                                                               context,
                                                                               memory.providerContext ); it.hasNext(); ) {
            final Object object = it.next();
            if ( !resultClass.isAssignableFrom( object.getClass() ) ) {
                continue; // skip anything if it not assignable
            }

            RightTuple rightTuple = createRightTuple( leftTuple,
                                                      context,
                                                      workingMemory,
                                                      object );

            checkConstraintsAndPropagate( leftTuple,
                                          rightTuple,
                                          context,
                                          workingMemory,
                                          memory,
                                          useLeftMemory );
            if ( useLeftMemory ) {
                addToCreatedHandlesMap( matches,
                                        rightTuple );
            }
        }

        this.betaConstraints.resetTuple( memory.betaMemory.getContext() );
    }

    @SuppressWarnings("unchecked")
    protected RightTuple createRightTuple( final LeftTuple leftTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory,
                                           final Object object ) {
        InternalFactHandle handle = null;
        ProtobufMessages.FactHandle _handle = null;
        if( context.getReaderContext() != null ) {
            Map<ProtobufInputMarshaller.TupleKey, List<ProtobufMessages.FactHandle>> map = (Map<ProtobufInputMarshaller.TupleKey, List<ProtobufMessages.FactHandle>>) context.getReaderContext().nodeMemories.get( getId() );
            if( map != null ) {
                TupleKey key = PersisterHelper.createTupleKey( leftTuple );
                List<FactHandle> list = map.get( key );
                if( list.isEmpty() ) {
                    map.remove( key );
                } else {
                    // it is a linked list, so the operation is fairly efficient
                    _handle = ((java.util.LinkedList<ProtobufMessages.FactHandle>)list).removeFirst();
                }
            }
        }
        if( _handle != null ) {
            // create a handle with the given id
            handle = workingMemory.getFactHandleFactory().newFactHandle( _handle.getId(),
                                                                         object,
                                                                         _handle.getRecency(),
                                                                         null, // set this to null, otherwise it uses the driver fact's entrypoint
                                                                         workingMemory,
                                                                         null ); 
        } else {
            handle = workingMemory.getFactHandleFactory().newFactHandle( object,
                                                                         null, // set this to null, otherwise it uses the driver fact's entrypoint
                                                                         workingMemory,
                                                                         null ); 
        }

        RightTuple rightTuple = newRightTuple( handle, null );
        return rightTuple;
    }

    protected RightTuple newRightTuple(InternalFactHandle handle, Object o) {
        return new RightTuple( handle,
                               null );

    }

    private void addToCreatedHandlesMap(final Map<Object, RightTuple> matches,
                                        final RightTuple rightTuple) {
        if ( rightTuple.getFactHandle().isValid() ) {
            Object object = rightTuple.getFactHandle().getObject();
            // keeping a list of matches
            RightTuple existingMatch = matches.get( object );
            if ( existingMatch != null ) {
                // this is for the obscene case where two or more objects returned by "from"
                // have the same hash code and evaluate equals() to true, so we need to preserve
                // all of them to avoid leaks
                rightTuple.setNext( existingMatch );
            }
            matches.put( object,
                         rightTuple );
        }
    }

    @SuppressWarnings("unchecked")
    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        memory.betaMemory.getLeftTupleMemory().removeAdd( leftTuple );

        final Map<Object, RightTuple> previousMatches = (Map<Object, RightTuple>) leftTuple.getObject();
        final Map<Object, RightTuple> newMatches = new HashMap<Object, RightTuple>();
        leftTuple.setObject( newMatches );

        this.betaConstraints.updateFromTuple( memory.betaMemory.getContext(),
                                              workingMemory,
                                              leftTuple );

        FastIterator rightIt = LinkedList.fastIterator;
        for ( final java.util.Iterator< ? > it = this.dataProvider.getResults( leftTuple,
                                                                               workingMemory,
                                                                               context,
                                                                               memory.providerContext ); it.hasNext(); ) {
            final Object object = it.next();
            if ( !resultClass.isAssignableFrom( object.getClass() ) ) {
                continue; // skip anything if it not assignable
            }
            
            RightTuple rightTuple = previousMatches.remove( object );

            if ( rightTuple == null ) {
                // new match, propagate assert
                rightTuple = createRightTuple( leftTuple,
                                               context,
                                               workingMemory, 
                                               object );
            } else {
                // previous match, so reevaluate and propagate modify
                if ( rightIt.next( rightTuple ) != null ) {
                    // handle the odd case where more than one object has the same hashcode/equals value
                    previousMatches.put( object,
                                         (RightTuple) rightIt.next( rightTuple ) );
                    rightTuple.setNext( null );
                }
            }

            checkConstraintsAndPropagate( leftTuple,
                                          rightTuple,
                                          context,
                                          workingMemory,
                                          memory,
                                          true );
            addToCreatedHandlesMap( newMatches,
                                    rightTuple );
        }

        this.betaConstraints.resetTuple( memory.betaMemory.getContext() );

        for ( RightTuple rightTuple : previousMatches.values() ) {
            for ( RightTuple current = rightTuple; current != null; current = (RightTuple) rightIt.next( current ) ) {
                retractMatch( leftTuple,
                              current,
                              context,
                              workingMemory );
            }
        }
    }

    protected void checkConstraintsAndPropagate( final LeftTuple leftTuple,
                                                 final RightTuple rightTuple,
                                                 final PropagationContext context,
                                                 final InternalWorkingMemory workingMemory,
                                                 final FromMemory memory,
                                                 final boolean useLeftMemory ) {
        boolean isAllowed = true;
        if ( this.alphaConstraints != null ) {
            // First alpha node filters
            for ( int i = 0, length = this.alphaConstraints.length; i < length; i++ ) {
                if ( !this.alphaConstraints[i].isAllowed( rightTuple.getFactHandle(),
                                                          workingMemory,
                                                          memory.alphaContexts[i] ) ) {
                    // next iteration
                    isAllowed = false;
                    break;
                }
            }
        }

        if ( isAllowed && this.betaConstraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                                    rightTuple.getFactHandle() ) ) {

            if ( rightTuple.firstChild == null ) {
                // this is a new match, so propagate as assert
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    rightTuple,
                                                    null,
                                                    null,
                                                    context,
                                                    workingMemory,
                                                    useLeftMemory );
            } else {
                // this is an existing match, so propagate as a modify
                this.sink.propagateModifyChildLeftTuple( rightTuple.firstChild,
                                                         leftTuple,
                                                         context,
                                                         workingMemory,
                                                         useLeftMemory );
            }
        } else {
            retractMatch( leftTuple,
                          rightTuple,
                          context,
                          workingMemory );
        }
    }

    protected void retractMatch(final LeftTuple leftTuple,
                                final RightTuple rightTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        if ( rightTuple.firstChild != null ) {
            // there was a previous match, so need to retract
            this.sink.propagateRetractChildLeftTuple( rightTuple.firstChild,
                                                      leftTuple,
                                                      context,
                                                      workingMemory );

        }
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );
        memory.betaMemory.getLeftTupleMemory().remove( leftTuple );
        this.sink.propagateRetractLeftTuple( leftTuple,
                                             context,
                                             workingMemory );
        unlinkCreatedHandles( workingMemory,
                              memory,
                              leftTuple );
    }

    @SuppressWarnings("unchecked")
    private void unlinkCreatedHandles(final InternalWorkingMemory workingMemory,
                                      final FromMemory memory,
                                      final LeftTuple leftTuple) {
        Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getObject();
        FastIterator rightIt = LinkedList.fastIterator;
        for ( RightTuple rightTuple : matches.values() ) {
            for ( RightTuple current = rightTuple; current != null; ) {
                RightTuple next = (RightTuple) rightIt.next( current );
                current.unlinkFromRightParent();
                current = next;
            }
        }
    }

    public void attach( BuildContext context ) {
        betaConstraints.init(context, getType());
        this.tupleSource.addTupleSink( this, context );
        if (context == null) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.tupleSource.networkUpdated(updateContext);
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {

        if ( !node.isInUse() ) {
            removeTupleSink( (LeftTupleSink) node );
        }

        if ( !this.isInUse() ) {
            for ( InternalWorkingMemory workingMemory : workingMemories ) {
                FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );
                Iterator it = memory.betaMemory.getLeftTupleMemory().iterator();
                for ( LeftTuple leftTuple = (LeftTuple) it.next(); leftTuple != null; leftTuple = (LeftTuple) it.next() ) {
                    unlinkCreatedHandles( workingMemory,
                                          memory,
                                          leftTuple );
                    leftTuple.unlinkFromLeftParent();
                    leftTuple.unlinkFromRightParent();
                }
                workingMemory.clearNodeMemory( this );
            }
        }

        this.tupleSource.remove( context,
                                 builder,
                                 this,
                                 workingMemories );
    }

    @SuppressWarnings("unchecked")
    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        FastIterator rightIter = LinkedList.fastIterator;
        final Iterator tupleIter = memory.betaMemory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getObject();
            for ( RightTuple rightTuples : matches.values() ) {
                for ( RightTuple rightTuple = rightTuples; rightTuple != null; rightTuple = (RightTuple) rightIter.next( rightTuple ) ) {
                    boolean isAllowed = true;
                    if ( this.alphaConstraints != null ) {
                        // First alpha node filters
                        for ( int i = 0, length = this.alphaConstraints.length; i < length; i++ ) {
                            if ( !this.alphaConstraints[i].isAllowed( rightTuple.getFactHandle(),
                                                                      workingMemory,
                                                                      memory.alphaContexts[i] ) ) {
                                // next iteration
                                isAllowed = false;
                                break;
                            }
                        }
                    }

                    if ( isAllowed && this.betaConstraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                                                rightTuple.getFactHandle() ) ) {
                        sink.assertLeftTuple( sink.createLeftTuple( leftTuple,
                                                                    rightTuple,
                                                                    null,
                                                                    null,
                                                                    sink,
                                                                    this.tupleMemoryEnabled ),
                                              context,
                                              workingMemory );
                    }                                      
                }
            }
        }
    }

    public Memory createMemory(final RuleBaseConfiguration config) {
        BetaMemory beta = new BetaMemory( new LeftTupleList(),
                                          null,
                                          this.betaConstraints.createContext(),
                                          NodeTypeEnums.FromNode );
        return new FromMemory( beta,
                               this.dataProvider.createContext(),
                               this.alphaConstraints );
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    public short getType() {
        return NodeTypeEnums.FromNode;
    } 

    public static class FromMemory
        implements
        Serializable,
        Memory {
        private static final long serialVersionUID = 510l;

        public BetaMemory         betaMemory;
        public Object             providerContext;
        public ContextEntry[]     alphaContexts;

        public FromMemory(BetaMemory betaMemory,
                          Object providerContext,
                          AlphaNodeFieldConstraint[] constraints) {
            this.betaMemory = betaMemory;
            this.providerContext = providerContext;
            this.alphaContexts = new ContextEntry[constraints.length];
            for ( int i = 0; i < constraints.length; i++ ) {
                this.alphaContexts[i] = constraints[i].createContextEntry();
            }
        }

        public short getNodeType() {
            return NodeTypeEnums.FromNode;
        }
    }
    
    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }    
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(leftTuple, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink) {
        return new FromNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.tupleSource;
    }

    protected ObjectTypeNode getObjectTypeNode() {
        return tupleSource.getObjectTypeNode();
    }
}
