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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.UpdateContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller.TupleKey;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.FactHandle;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.From;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.index.LeftTupleList;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FromNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    MemoryFactory {
    private static final long          serialVersionUID = 510l;

    protected DataProvider               dataProvider;
    protected AlphaNodeFieldConstraint[] alphaConstraints;
    protected BetaConstraints            betaConstraints;

    protected LeftTupleSinkNode          previousTupleSinkNode;
    protected LeftTupleSinkNode          nextTupleSinkNode;
    
    protected From                       from;
    protected Class<?>                   resultClass;

    protected boolean                    tupleMemoryEnabled;

    protected transient ObjectTypeConf   objectTypeConf;

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
        super(id, context);
        this.dataProvider = dataProvider;
        setLeftTupleSource(tupleSource);
        this.alphaConstraints = constraints;
        this.betaConstraints = (binder == null) ? EmptyBetaConstraints.getInstance() : binder;
        this.tupleMemoryEnabled = tupleMemoryEnabled;
        this.from = from;
        resultClass = this.from.getResultClass();

        initMasks(context, tupleSource);
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        dataProvider = (DataProvider) in.readObject();
        alphaConstraints = (AlphaNodeFieldConstraint[]) in.readObject();
        betaConstraints = (BetaConstraints) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        from = (From) in.readObject();
        resultClass = from.getResultClass();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( dataProvider );
        out.writeObject( alphaConstraints );
        out.writeObject( betaConstraints );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeObject( from );
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public AlphaNodeFieldConstraint[] getAlphaConstraints() {
        return alphaConstraints;
    }

    public BetaConstraints getBetaConstraints() {
        return betaConstraints;
    }
    

    public Class< ? > getResultClass() {
        return resultClass;
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.leftInput.networkUpdated(updateContext);
    }

    @SuppressWarnings("unchecked")
    public RightTuple createRightTuple( final LeftTuple leftTuple,
                                        final PropagationContext context,
                                        final InternalWorkingMemory workingMemory,
                                        final Object object ) {
        InternalFactHandle handle;
        ProtobufMessages.FactHandle _handle = null;
        if ( objectTypeConf == null ) {
            // use default entry point and object class. Notice that at this point object is assignable to resultClass
            objectTypeConf = new ClassObjectTypeConf( workingMemory.getEntryPoint(), resultClass, workingMemory.getKnowledgeBase() );
        }
        if( context.getReaderContext() != null ) {
            Map<ProtobufInputMarshaller.TupleKey, List<FactHandle>> map = (Map<ProtobufInputMarshaller.TupleKey, List<ProtobufMessages.FactHandle>>) context.getReaderContext().nodeMemories.get( getId() );
            if( map != null ) {
                TupleKey key = PersisterHelper.createTupleKey(leftTuple);
                List<FactHandle> list = map.get( key );
                if( list != null && ! list.isEmpty() ) {
                    // it is a linked list, so the operation is fairly efficient
                    _handle = ((java.util.LinkedList<ProtobufMessages.FactHandle>)list).removeFirst();
                    if( list.isEmpty() ) {
                        map.remove(key);
                    }
                }
            }
        }
        if( _handle != null ) {
            // create a handle with the given id
            handle = workingMemory.getFactHandleFactory().newFactHandle( _handle.getId(),
                                                                         object,
                                                                         _handle.getRecency(),
                                                                         objectTypeConf,
                                                                         workingMemory,
                                                                         null );
        } else {
            handle = workingMemory.getFactHandleFactory().newFactHandle( object,
                                                                         objectTypeConf,
                                                                         workingMemory,
                                                                         null );
        }

        return newRightTuple( handle );
    }


    protected RightTuple newRightTuple(InternalFactHandle handle) {
        return new RightTuple( handle );

    }

    public void addToCreatedHandlesMap(final Map<Object, RightTuple> matches,
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


    public Memory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        BetaMemory beta = new BetaMemory( new LeftTupleList(),
                                          null,
                                          this.betaConstraints.createContext(),
                                          NodeTypeEnums.FromNode );
        return new FromMemory( beta,
                               this.dataProvider,
                               this.alphaConstraints );
    }
   

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        FromNodeLeftTuple peer = new FromNodeLeftTuple();
        peer.initPeer( (BaseLeftTuple) original, this );
        original.setPeer( peer );
        return peer;
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

    public static class FromMemory extends AbstractBaseLinkedListNode<Memory>
        implements
        Serializable,
        Memory {
        private static final long serialVersionUID = 510l;

        private DataProvider      dataProvider;

        public BetaMemory         betaMemory;
        public Object             providerContext;
        public ContextEntry[]     alphaContexts;

        public FromMemory(BetaMemory betaMemory,
                          DataProvider dataProvider,
                          AlphaNodeFieldConstraint[] constraints) {
            this.betaMemory = betaMemory;
            this.dataProvider = dataProvider;
            this.providerContext = dataProvider.createContext();
            this.alphaContexts = new ContextEntry[constraints.length];
            for ( int i = 0; i < constraints.length; i++ ) {
                this.alphaContexts[i] = constraints[i].createContextEntry();
            }
        }

        public short getNodeType() {
            return NodeTypeEnums.FromNode;
        }

        public SegmentMemory getSegmentMemory() {
            return betaMemory.getSegmentMemory();
        }

        public void setSegmentMemory(SegmentMemory segmentMemory) {
            betaMemory.setSegmentMemory(segmentMemory);
        }

        public BetaMemory getBetaMemory() {
            return betaMemory;
        }

        public void setBetaMemory(BetaMemory betaMemory) {
            this.betaMemory = betaMemory;
        }

        public void reset() {
            this.betaMemory.reset();
            this.providerContext = dataProvider.createContext();
        }
    }
    
    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final LeftTupleSink sink) {
        return new FromNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(leftTuple, sink, pctx,
                                     leftTupleMemoryEnabled );
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
        return this.leftInput;
    }

    protected ObjectTypeNode getObjectTypeNode() {
        return leftInput.getObjectTypeNode();
    }

    @Override
    public void assertLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retractLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyLeftTuple(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void updateSink(LeftTupleSink sink, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    public void attach( BuildContext context ) {
        betaConstraints.init(context, getType());
        this.leftInput.addTupleSink( this, context );
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder,
                               final InternalWorkingMemory[] workingMemories) {

        if ( !this.isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            return true;
        }
        return false;
    }

}
