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
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.InternalViewChangedEventListener;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.common.LeftTupleSetsImpl;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.common.UpdateContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller.QueryElementContext;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller.TupleKey;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.phreak.StackEntry;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.AbductiveQuery;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Query;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.Rule;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.kie.api.runtime.rule.Variable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class QueryElementNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    MemoryFactory {

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    protected QueryElement      queryElement;

    private boolean           tupleMemoryEnabled;

    protected boolean           openQuery;

    public QueryElementNode() {
        // for serialization
    }

    public QueryElementNode(final int id,
                            final LeftTupleSource tupleSource,
                            final QueryElement queryElement,
                            final boolean tupleMemoryEnabled,
                            final boolean openQuery,
                            final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        setLeftTupleSource(tupleSource);
        this.queryElement = queryElement;
        this.tupleMemoryEnabled = tupleMemoryEnabled;
        this.openQuery = openQuery;
        initMasks( context, tupleSource );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );        
        queryElement = (QueryElement) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        openQuery = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( queryElement );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeBoolean( openQuery );
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.leftInput.networkUpdated(updateContext);
    }

    public short getType() {
        return NodeTypeEnums.UnificationNode;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return false;
    }

    public QueryElement getQueryElement() {
        return queryElement;
    }

    public boolean isOpenQuery() {
        return openQuery;
    }


    @SuppressWarnings("unchecked")
    public InternalFactHandle createFactHandle(final PropagationContext context,
                                               final InternalWorkingMemory workingMemory,
                                               final LeftTuple leftTuple ) {
        InternalFactHandle handle = null;
        ProtobufMessages.FactHandle _handle = null;
        if( context.getReaderContext() != null ) {
            Map<TupleKey, QueryElementContext> map = (Map<TupleKey, QueryElementContext>) context.getReaderContext().nodeMemories.get( getId() );
            if( map != null ) {
                _handle = map.get( PersisterHelper.createTupleKey( leftTuple ) ).handle;
            }
        }
        if( _handle != null ) {
            // create a handle with the given id
            handle = workingMemory.getFactHandleFactory().newFactHandle( _handle.getId(),
                                                                         null,
                                                                         _handle.getRecency(),
                                                                         null,
                                                                         workingMemory,
                                                                         workingMemory ); 
        } else {
            handle = workingMemory.getFactHandleFactory().newFactHandle( null,
                                                                         null,
                                                                         workingMemory,
                                                                         workingMemory ); 
        }
        return handle;
    }
    
    public DroolsQuery createDroolsQuery(LeftTuple leftTuple,
                                         InternalFactHandle handle,
                                         StackEntry stackEntry,
                                         final List<PathMemory> pmems,
                                         QueryElementNodeMemory qmem,
                                         LeftTupleSets trgLeftTuples,
                                         LeftTupleSink sink,
                                         InternalWorkingMemory workingMemory) {
        Object[] argTemplate = this.queryElement.getArgTemplate(); // an array of declr, variable and literals
        Object[] args = new Object[argTemplate.length]; // the actual args, to be created from the  template

        // first copy everything, so that we get the literals. We will rewrite the declarations and variables next
        System.arraycopy( argTemplate,
                          0,
                          args,
                          0,
                          args.length );

        int[] declIndexes = this.queryElement.getDeclIndexes();

        for ( int i = 0, length = declIndexes.length; i < length; i++ ) {
            Declaration declr = (Declaration) argTemplate[declIndexes[i]];

            Object tupleObject = leftTuple.get( declr ).getObject();

            Object o;

            if ( tupleObject instanceof DroolsQuery ) {
                // If the query passed in a Variable, we need to use it
                ArrayElementReader arrayReader = (ArrayElementReader) declr.getExtractor();
                if ( ((DroolsQuery) tupleObject).getVariables()[arrayReader.getIndex()] != null ) {
                    o = Variable.v;
                } else {
                    o = declr.getValue( workingMemory,
                                        tupleObject );
                }
            } else {
                o = declr.getValue( workingMemory,
                                    tupleObject );
            }

            args[declIndexes[i]] = o;
        }

        int[] varIndexes = this.queryElement.getVariableIndexes();
        for ( int i = 0, length = varIndexes.length; i < length; i++ ) {
            if ( argTemplate[varIndexes[i]] == Variable.v ) {
                // Need to check against the arg template, as the varIndexes also includes re-declared declarations
                args[varIndexes[i]] = Variable.v;
            }
        }

        UnificationNodeViewChangedEventListener collector = createCollector( leftTuple, varIndexes, this.tupleMemoryEnabled );
        
        boolean executeAsOpenQuery = openQuery;
        if ( executeAsOpenQuery ) {
            // There is no point in doing an open query if the caller is a non-open query.
            Object object = ((InternalFactHandle) leftTuple.get( 0 )).getObject();
            if ( object instanceof DroolsQuery && !((DroolsQuery) object).isOpen() ) {
                executeAsOpenQuery = false;
            }          
        }

        DroolsQuery queryObject = new DroolsQuery( this.queryElement.getQueryName(),
                                                   args,
                                                   collector,
                                                   executeAsOpenQuery,
                                                   stackEntry,
                                                   pmems,
                                                   trgLeftTuples,
                                                   qmem,
                                                   sink);

        collector.setFactHandle( handle );

        handle.setObject( queryObject );

        leftTuple.setObject( handle ); // so it can be retracted later and destroyed

        return queryObject;
    }

    protected UnificationNodeViewChangedEventListener createCollector( LeftTuple leftTuple, int[] varIndexes, boolean tupleMemoryEnabled ) {
        return new UnificationNodeViewChangedEventListener( leftTuple,
                                                            varIndexes,
                                                            this,
                                                            tupleMemoryEnabled );
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.leftInput;
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

    public static class UnificationNodeViewChangedEventListener
        implements
        InternalViewChangedEventListener {

        protected LeftTuple          leftTuple;

        protected QueryElementNode   node;

        protected InternalFactHandle factHandle;

        protected int[]              variables;

        protected boolean            tupleMemoryEnabled;

        public UnificationNodeViewChangedEventListener(LeftTuple leftTuple,
                                                       int[] variables,
                                                       QueryElementNode node,
                                                       boolean tupleMemoryEnabled) {
            this.leftTuple = leftTuple;
            this.variables = variables;
            this.node = node;
            this.tupleMemoryEnabled = tupleMemoryEnabled;
        }

        public InternalFactHandle getFactHandle() {
            return factHandle;
        }

        public void setFactHandle(InternalFactHandle factHandle) {
            this.factHandle = factHandle;
        }

        public void setVariables(int[] variables) {
            this.variables = variables;
        }

        public void rowAdded(final Rule rule,
                             LeftTuple resultLeftTuple,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {

            QueryTerminalNode node = (QueryTerminalNode) resultLeftTuple.getLeftTupleSink();
            Query query = node.getQuery();
            Declaration[] decls = node.getDeclarations();
            DroolsQuery dquery = (DroolsQuery) this.factHandle.getObject();
            Object[] objects = new Object[ determineResultSize( query, dquery ) ];

            Declaration decl;
            for ( int i = 0, length = this.variables.length; i < length; i++ ) {
                decl = decls[this.variables[i]];
                objects[this.variables[i]] = decl.getValue( workingMemory,
                                                            resultLeftTuple.get( decl ).getObject() );
            }

            QueryElementFactHandle resultHandle = createQueryResultHandle(context,
                                                                          workingMemory,
                                                                          objects);
            
            RightTuple rightTuple = createResultRightTuple(resultHandle, resultLeftTuple, dquery.isOpen());

            boolean pass = true;
            if ( query.isAbductive() ) {
                int numArgs = dquery.getElements().length;
                AbductiveQuery aq = (( AbductiveQuery) query );
                for ( int j = 0; j < numArgs; j++ ) {
                    if ( dquery.getElements()[ j ] != null ) {
                        objects[ j ] = dquery.getElements()[ j ];
                    }
                }
                Object abduced = aq.abduce( Arrays.copyOfRange( objects, 0, numArgs - 1 ) );
                if ( abduced != null ) {
                    EqualityKey key = ( (NamedEntryPoint) workingMemory.getEntryPoints().get( workingMemory.getEntryPointId() ) ).getTruthMaintenanceSystem().get( abduced );
                    InternalFactHandle handle = null;
                    if ( key != null ) {
                        handle = key.getFactHandle();
                        abduced = handle.getObject();
                    } else {
                        handle = (InternalFactHandle) ((InternalWorkingMemoryActions) workingMemory).insert( abduced,
                                                                                                             aq.getValue(),
                                                                                                             false,
                                                                                                             true,
                                                                                                             query,
                                                                                                             (RuleTerminalNodeLeftTuple) resultLeftTuple );
                    }
                    BeliefSet bs = handle.getEqualityKey().getBeliefSet();
                    if ( bs == null ) {
                        abduced = handle.getObject();
                    } else {
                        if ( ! bs.isPositive() ) {
                            pass = false;
                        }
                    }
                } else {
                    // query was successful, but nothing could be abduced.
                }
                objects[ objects.length - 1 ] = abduced;
            }

            if ( pass ) {
                LeftTupleSink sink = dquery.getLeftTupleSink();
                LeftTuple childLeftTuple = sink.createLeftTuple( this.leftTuple, rightTuple, sink );
                boolean stagedInsertWasEmpty = dquery.getResultLeftTupleSets().addInsert(childLeftTuple);
                if ( stagedInsertWasEmpty ) {
                    dquery.getQueryNodeMemory().setNodeDirtyWithoutNotify();
                }
            }


        }

        private int determineResultSize( Query query, DroolsQuery dquery ) {
            if ( ! query.isAbductive() ) {
                return dquery.getElements().length;
            } else {
                if ( (( AbductiveQuery ) query ).isReturnBound() ) {
                    return dquery.getElements().length + 1;
                } else {
                    return dquery.getElements().length;
                }
            }
        }

        protected RightTuple createResultRightTuple( QueryElementFactHandle resultHandle, LeftTuple resultLeftTuple, boolean open ) {
            RightTuple rightTuple = new RightTuple( resultHandle );
            if ( open ) {
                rightTuple.setLeftTuple( resultLeftTuple );
                resultLeftTuple.setObject( rightTuple );

            }
            rightTuple.setPropagationContext( resultLeftTuple.getPropagationContext() );
            return rightTuple;
        }

        @SuppressWarnings("unchecked")
        protected QueryElementFactHandle createQueryResultHandle(PropagationContext context,
                                                               InternalWorkingMemory workingMemory,
                                                               Object[] objects) {
            QueryElementFactHandle handle = null;
            ProtobufMessages.FactHandle _handle = null;
            if( context.getReaderContext() != null ) {
                Map<TupleKey, QueryElementContext> map = (Map<TupleKey, QueryElementContext>) context.getReaderContext().nodeMemories.get( node.getId() );
                if( map != null ) {
                    QueryElementContext _context = map.get( PersisterHelper.createTupleKey( leftTuple ) );
                    if( _context != null ) {
                        _handle = _context.results.removeFirst();
                    }
                }
            }
            if( _handle != null ) {
                // create a handle with the given id
                handle = new QueryElementFactHandle( objects,
                                                     _handle.getId(),
                                                     _handle.getRecency() );
            } else {
                handle = new QueryElementFactHandle( objects,
                                                     workingMemory.getFactHandleFactory().getAtomicId().incrementAndGet(),
                                                     workingMemory.getFactHandleFactory().getAtomicRecency().incrementAndGet() );
            }
            return handle;
        }

        public void rowRemoved(final Rule rule,
                               final LeftTuple resultLeftTuple,
                               final PropagationContext context,
                               final InternalWorkingMemory workingMemory) {
            RightTuple rightTuple = (RightTuple) resultLeftTuple.getObject();
            rightTuple.setLeftTuple( null );
            resultLeftTuple.setObject( null );

            DroolsQuery query = (DroolsQuery) this.factHandle.getObject();
            LeftTupleSets leftTuples = query.getResultLeftTupleSets();
            LeftTuple childLeftTuple = rightTuple.getFirstChild();

            switch ( childLeftTuple.getStagedType() ) {
                // handle clash with already staged entries
                case LeftTuple.INSERT :
                    leftTuples.removeInsert( childLeftTuple );
                    break;
                case LeftTuple.UPDATE :
                    leftTuples.removeUpdate( childLeftTuple );
                    break;
            }
            leftTuples.addDelete( childLeftTuple  );
            childLeftTuple.unlinkFromRightParent();
            childLeftTuple.unlinkFromLeftParent();
        }

        public void rowUpdated(final Rule rule,
                               final LeftTuple resultLeftTuple,
                               final PropagationContext context,
                               final InternalWorkingMemory workingMemory) {
            RightTuple rightTuple = (RightTuple) resultLeftTuple.getObject();
            if ( rightTuple.getMemory() != null ) {
                // Already sheduled as an insert
                return;
            }

            rightTuple.setLeftTuple( null );
            resultLeftTuple.setObject( null );

            // We need to recopy everything back again, as we don't know what has or hasn't changed
            QueryTerminalNode node = (QueryTerminalNode) resultLeftTuple.getLeftTupleSink();
            Declaration[] decls = node.getDeclarations();
            InternalFactHandle rootHandle = resultLeftTuple.get( 0 );
            DroolsQuery dquery = (DroolsQuery) rootHandle.getObject();

            Object[] objects = new Object[dquery.getElements().length];

            Declaration decl;
            for ( int i = 0, length = this.variables.length; i < length; i++ ) {
                decl = decls[this.variables[i]];
                objects[this.variables[i]] = decl.getValue( workingMemory,
                                                            resultLeftTuple.get( decl ).getObject() );
            }

            QueryElementFactHandle handle = (QueryElementFactHandle) rightTuple.getFactHandle();

            handle.setRecency(workingMemory.getFactHandleFactory().getAtomicRecency().incrementAndGet());
            handle.setObject( objects );

            if ( dquery.isOpen() ) {
                rightTuple.setLeftTuple( resultLeftTuple );
                resultLeftTuple.setObject( rightTuple );
            }

            LeftTupleSets leftTuples = dquery.getResultLeftTupleSets();
            LeftTuple childLeftTuple = rightTuple.getFirstChild();
            switch ( childLeftTuple.getStagedType() ) {
                // handle clash with already staged entries
                case LeftTuple.INSERT :
                    leftTuples.removeInsert( childLeftTuple );
                    break;
                case LeftTuple.UPDATE :
                    leftTuples.removeUpdate( childLeftTuple );
                    break;
            }
            leftTuples.addUpdate( childLeftTuple  );
        }

        public List< ? extends Object> getResults() {
            throw new UnsupportedOperationException( getClass().getCanonicalName() + " does not support the getResults() method." );
        }

        public LeftTuple getLeftTuple() {
            return leftTuple;
        }

    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new QueryElementNodeLeftTuple( factHandle,
                                              sink,
                                              leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final LeftTupleSink sink) {
        return new QueryElementNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new QueryElementNodeLeftTuple( leftTuple,
                                              sink,
                                              pctx,
                                              leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink) {
        return new QueryElementNodeLeftTuple( leftTuple,
                                              rightTuple,
                                              sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new QueryElementNodeLeftTuple( leftTuple,
                                              rightTuple,
                                              currentLeftChild,
                                              currentRightChild,
                                              sink,
                                              leftTupleMemoryEnabled );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (openQuery ? 1231 : 1237);
        result = prime * result + ((queryElement == null) ? 0 : queryElement.hashCode());
        result = prime * result + ((leftInput == null) ? 0 : leftInput.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        QueryElementNode other = (QueryElementNode) obj;
        if ( openQuery != other.openQuery ) return false;
        if ( queryElement == null ) {
            if ( other.queryElement != null ) return false;
        } else if ( !queryElement.equals( other.queryElement ) ) return false;
        if ( leftInput == null ) {
            if ( other.leftInput != null ) return false;
        } else if ( !leftInput.equals( other.leftInput ) ) return false;
        return true;
    }

    public Memory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return new QueryElementNodeMemory(this);
    }
    
    public static class QueryElementNodeMemory extends AbstractBaseLinkedListNode<Memory> implements Memory {
        private QueryElementNode node;

        private SegmentMemory smem;

        private SegmentMemory querySegmentMemory;

        private LeftTupleSets resultLeftTuples;

        private long          nodePosMaskBit;

        public QueryElementNodeMemory(QueryElementNode node) {
            this.node = node;
            // @FIXME I don't think this is thread safe
            this.resultLeftTuples = new LeftTupleSetsImpl();
        }

        public QueryElementNode getNode() {
            return this.node;
        }

        public short getNodeType() {
            return NodeTypeEnums.QueryElementNode;
        }

        public void setSegmentMemory(SegmentMemory smem) {
            this.smem = smem;
        }

        public SegmentMemory getSegmentMemory() {
            return smem;
        }

        public SegmentMemory getQuerySegmentMemory() {
            return querySegmentMemory;
        }

        public void setQuerySegmentMemory(SegmentMemory querySegmentMemory) {
            this.querySegmentMemory = querySegmentMemory;
        }

        public LeftTupleSets getResultLeftTuples() {
            return resultLeftTuples;
        }

        public long getNodePosMaskBit() {
            return nodePosMaskBit;
        }

        public void setNodePosMaskBit(long segmentPos) {
            this.nodePosMaskBit = segmentPos;
        }

        public void setNodeDirtyWithoutNotify() {
            smem.updateDirtyNodeMask( nodePosMaskBit );
        }

        public void setNodeCleanWithoutNotify() {
            smem.updateCleanNodeMask( nodePosMaskBit );
        }
    }

    protected ObjectTypeNode getObjectTypeNode() {
        return leftInput.getObjectTypeNode();
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        QueryElementNodeLeftTuple peer = new QueryElementNodeLeftTuple();
        peer.initPeer((BaseLeftTuple) original, this);
        original.setPeer(peer);
        return peer;
    }

    public String toString() {
        return "[" + this.getClass().getSimpleName() + "(" + this.id + ", " + queryElement.getQueryName() + ")]";
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
        this.leftInput.addTupleSink( this, context );
    }

    protected void doRemove(RuleRemovalContext context,
                            ReteooBuilder builder,
                            InternalWorkingMemory[] workingMemories) {
        if (!isInUse()) {
            getLeftTupleSource().removeTupleSink(this);
        }
    }
}
