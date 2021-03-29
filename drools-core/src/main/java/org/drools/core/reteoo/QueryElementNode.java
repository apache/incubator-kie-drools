/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.InternalViewChangedEventListener;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl.MODE;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.common.TupleSets;
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.common.UpdateContext;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.phreak.StackEntry;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.AbductiveQuery;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.QueryArgument;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.QueryImpl;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractBaseLinkedListNode;

public class QueryElementNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    MemoryFactory<QueryElementNode.QueryElementNodeMemory> {

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    protected QueryElement    queryElement;

    private boolean           tupleMemoryEnabled;

    protected boolean         openQuery;

    private   boolean         dataDriven;

    private QueryArgument[]   argsTemplate;

    public QueryElementNode() {
        // for serialization
    }

    public QueryElementNode(final int id,
                            final LeftTupleSource tupleSource,
                            final QueryElement queryElement,
                            final boolean tupleMemoryEnabled,
                            final boolean openQuery,
                            final BuildContext context) {
        super(id, context);
        setLeftTupleSource(tupleSource);
        this.setObjectCount(leftInput.getObjectCount() + 1); // 'query' node increase the object count
        this.queryElement = queryElement;
        this.tupleMemoryEnabled = tupleMemoryEnabled;
        this.openQuery = openQuery;
        this.dataDriven = context != null && context.getRule().isDataDriven();
        initMasks( context, tupleSource );
        this.argsTemplate = initArgsTemplate( context );

        hashcode = calculateHashCode();
    }

    private QueryArgument[] initArgsTemplate(BuildContext context) {
        ClassLoader classLoader = context.getKnowledgeBase().getRootClassLoader();
        QueryArgument[] originalArgs = this.queryElement.getArguments();
        QueryArgument[] args = new QueryArgument[originalArgs.length];
        for (int i = 0; i < originalArgs.length; i++) {
            args[i] = originalArgs[i] == null ? QueryArgument.NULL : originalArgs[i].normalize( classLoader );
        }
        return args;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );        
        queryElement = (QueryElement) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        openQuery = in.readBoolean();
        dataDriven = in.readBoolean();
        this.argsTemplate = (QueryArgument[]) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( queryElement );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeBoolean( openQuery );
        out.writeBoolean( dataDriven );
        out.writeObject( argsTemplate );
    }

    @Override
    public void networkUpdated(UpdateContext updateContext) {
        this.leftInput.networkUpdated(updateContext);
    }

    @Override
    public short getType() {
        return NodeTypeEnums.UnificationNode;
    }

    @Override
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
        if( context.getReaderContext() != null ) {
            handle = context.getReaderContext().createQueryHandle( leftTuple, workingMemory, getId() );
        }

        if (handle == null) {
            handle = workingMemory.getFactHandleFactory().newFactHandle( null, null, workingMemory, workingMemory );
        }
        return handle;
    }
    
    public DroolsQuery createDroolsQuery(LeftTuple leftTuple,
                                         InternalFactHandle handle,
                                         StackEntry stackEntry,
                                         final List<PathMemory> pmems,
                                         QueryElementNodeMemory qmem,
                                         LeftTupleSink sink,
                                         InternalWorkingMemory workingMemory) {
        UnificationNodeViewChangedEventListener collector = createCollector( leftTuple, queryElement.getVariableIndexes(), this.tupleMemoryEnabled );
        
        boolean executeAsOpenQuery = openQuery;
        if ( executeAsOpenQuery ) {
            // There is no point in doing an open query if the caller is a non-open query.
            Object object = leftTuple.get( 0 ).getObject();
            if ( object instanceof DroolsQuery && !((DroolsQuery) object).isOpen() ) {
                executeAsOpenQuery = false;
            }          
        }

        DroolsQuery queryObject = new DroolsQuery( this.queryElement.getQueryName(),
                                                   getActualArguments( leftTuple, workingMemory ),
                                                   collector,
                                                   executeAsOpenQuery,
                                                   stackEntry,
                                                   pmems,
                                                   qmem != null ? qmem.getResultLeftTuples() : null,
                                                   qmem,
                                                   sink);
        collector.setFactHandle( handle );
        handle.setObject( queryObject );
        leftTuple.setContextObject( handle ); // so it can be retracted later and destroyed
        return queryObject;
    }

    public Object[] getActualArguments( LeftTuple leftTuple, InternalWorkingMemory workingMemory ) {
        Object[] args = new Object[argsTemplate.length]; // the actual args, to be created from the  template
        for (int i = 0; i < argsTemplate.length; i++) {
            args[i] = argsTemplate[i].getValue( workingMemory, leftTuple );
        }
        return args;
    }

    protected UnificationNodeViewChangedEventListener createCollector( LeftTuple leftTuple, int[] varIndexes, boolean tupleMemoryEnabled ) {
        return new UnificationNodeViewChangedEventListener( leftTuple,
                                                            varIndexes,
                                                            this,
                                                            tupleMemoryEnabled );
    }

    @Override
    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    @Override
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    @Override
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    @Override
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    @Override
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

        @Override
        public void rowAdded(final RuleImpl rule,
                             LeftTuple resultLeftTuple,
                             InternalWorkingMemory workingMemory) {

            QueryTerminalNode queryTerminalNode = resultLeftTuple.getTupleSink();
            QueryImpl query = queryTerminalNode.getQuery();
            Declaration[] decls = queryTerminalNode.getRequiredDeclarations();
            DroolsQuery dquery = (DroolsQuery) this.factHandle.getObject();
            Object[] objects = new Object[ determineResultSize( query, dquery ) ];

            Declaration decl;
            for (int variable : this.variables) {
                decl = decls[variable];
                objects[variable] = decl.getValue(workingMemory, resultLeftTuple);
            }

            QueryElementFactHandle resultHandle = createQueryResultHandle(leftTuple.findMostRecentPropagationContext(),
                                                                          workingMemory,
                                                                          objects);
            
            RightTuple rightTuple = createResultRightTuple(resultHandle, resultLeftTuple, dquery.isOpen());

            boolean pass = true;
            if ( query.isAbductive() ) {
                AbductiveQuery aq = (( AbductiveQuery) query );
                int numArgs = aq.getAbducibleArgs().length;
                Object[] constructorArgs = new Object[ aq.getAbducibleArgs().length ];
                for ( int j = 0; j < numArgs; j++ ) {
                    int k = aq.mapArgToParam( j );
                    if ( objects[ k ] != null ) {
                        constructorArgs[ j ] = objects[ k ];
                    } else if ( dquery.getElements()[ k ] != null ) {
                        constructorArgs[ j ] = dquery.getElements()[ k ];
                    }
                }
                Object abduced = aq.abduce( constructorArgs );
                if ( abduced != null ) {
                    boolean firstAssertion = true;
                    ObjectStore store = workingMemory.getObjectStore();
                    InternalFactHandle handle = store.getHandleForObject( abduced );
                    if ( handle != null ) {
                        abduced = handle.getObject();
                        firstAssertion = false;
                    } else {
                        handle = workingMemory.getTruthMaintenanceSystem().insert( abduced,
                                                                                   MODE.POSITIVE.getId(),
                                                                                   query,
                                                                                   (RuleTerminalNodeLeftTuple) resultLeftTuple );
                    }
                    BeliefSet bs = handle.getEqualityKey() != null ? handle.getEqualityKey().getBeliefSet() : null;
                    if ( bs == null ) {
                        abduced = handle.getObject();
                    } else {
                        if ( ! bs.isPositive() ) {
                            pass = false;
                        } else {
                            if ( !firstAssertion ) {
                                workingMemory.getTruthMaintenanceSystem().insert( abduced,
                                                                                  MODE.POSITIVE.getId(),
                                                                                  query,
                                                                                  (RuleTerminalNodeLeftTuple) resultLeftTuple );
                            }
                        }
                    }
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

        private int determineResultSize( QueryImpl query, DroolsQuery dquery ) {
            int size = dquery.getElements().length;
            if (query.isAbductive() && (( AbductiveQuery ) query ).isReturnBound()) {
                size++;
            }
            return size;
        }

        protected RightTuple createResultRightTuple( QueryElementFactHandle resultHandle, LeftTuple resultLeftTuple, boolean open ) {
            RightTuple rightTuple = new RightTupleImpl( resultHandle );
            if ( open ) {
                rightTuple.setBlocked( resultLeftTuple );
                resultLeftTuple.setContextObject( rightTuple );

            }
            rightTuple.setPropagationContext( resultLeftTuple.getPropagationContext() );
            return rightTuple;
        }

        @SuppressWarnings("unchecked")
        protected QueryElementFactHandle createQueryResultHandle(PropagationContext context,
                                                               InternalWorkingMemory workingMemory,
                                                               Object[] objects) {
            QueryElementFactHandle handle = null;
            if (context.getReaderContext() != null ) {
                handle = context.getReaderContext().createQueryResultHandle( leftTuple, workingMemory, objects, node.getId() );
            }

            if (handle == null) {
                handle = new QueryElementFactHandle( objects,
                        workingMemory.getFactHandleFactory().getNextId(),
                        workingMemory.getFactHandleFactory().getNextRecency() );
            }

            return handle;
        }

        @Override
        public void rowRemoved(final RuleImpl rule,
                               final LeftTuple resultLeftTuple,
                               final InternalWorkingMemory workingMemory) {
            RightTuple rightTuple = (RightTuple) resultLeftTuple.getContextObject();
            rightTuple.setBlocked( null );
            resultLeftTuple.setContextObject( null );

            DroolsQuery query = (DroolsQuery) this.factHandle.getObject();
            TupleSets<LeftTuple> leftTuples = query.getResultLeftTupleSets();
            LeftTuple childLeftTuple = rightTuple.getFirstChild();

            if (childLeftTuple.isStagedOnRight()) {
                ( (SubnetworkTuple) childLeftTuple ).moveStagingFromRightToLeft();
            } else {
                short stagedTypeForQueries = childLeftTuple.getStagedTypeForQueries();// handle clash with already staged entries
                if (stagedTypeForQueries == LeftTuple.INSERT) {
                    leftTuples.removeInsert(childLeftTuple);
                    return;
                } else if (stagedTypeForQueries == LeftTuple.UPDATE) {
                    leftTuples.removeUpdate(childLeftTuple);
                }
            }

            leftTuples.addDelete(childLeftTuple);
        }

        @Override
        public void rowUpdated(final RuleImpl rule,
                               final LeftTuple resultLeftTuple,
                               final InternalWorkingMemory workingMemory) {
            RightTuple rightTuple = (RightTuple) resultLeftTuple.getContextObject();
            if ( rightTuple.getMemory() != null ) {
                // Already sheduled as an insert
                return;
            }

            rightTuple.setBlocked( null );
            resultLeftTuple.setContextObject( null );

            // We need to recopy everything back again, as we don't know what has or hasn't changed
            QueryTerminalNode queryTerminalNode = resultLeftTuple.getTupleSink();
            Declaration[] decls = queryTerminalNode.getRequiredDeclarations();
            InternalFactHandle rootHandle = resultLeftTuple.get( 0 );
            DroolsQuery dquery = (DroolsQuery) rootHandle.getObject();

            Object[] objects = new Object[dquery.getElements().length];

            Declaration decl;
            for (int variable : this.variables) {
                decl = decls[variable];
                objects[variable] = decl.getValue(workingMemory, resultLeftTuple);
            }

            QueryElementFactHandle handle = (QueryElementFactHandle) rightTuple.getFactHandle();

            handle.setRecency(workingMemory.getFactHandleFactory().getNextRecency());
            handle.setObject( objects );

            if ( dquery.isOpen() ) {
                rightTuple.setBlocked( resultLeftTuple );
                resultLeftTuple.setContextObject( rightTuple );
            }

            TupleSets<LeftTuple> leftTuples = dquery.getResultLeftTupleSets();
            LeftTuple childLeftTuple = rightTuple.getFirstChild();
            short stagedTypeForQueries = childLeftTuple.getStagedTypeForQueries();// handle clash with already staged entries
            if (stagedTypeForQueries == LeftTuple.INSERT) {
                leftTuples.removeInsert(childLeftTuple);
            } else if (stagedTypeForQueries == LeftTuple.UPDATE) {
                leftTuples.removeUpdate(childLeftTuple);
            }
            leftTuples.addUpdate( childLeftTuple  );
        }

        @Override
        public List<?> getResults() {
            throw new UnsupportedOperationException( getClass().getCanonicalName() + " does not support the getResults() method." );
        }

        public LeftTuple getLeftTuple() {
            return leftTuple;
        }

    }

    @Override
    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     boolean leftTupleMemoryEnabled) {
        return new QueryElementNodeLeftTuple( factHandle, this, leftTupleMemoryEnabled );
    }

    @Override
    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new QueryElementNodeLeftTuple(factHandle,leftTuple, sink );
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new QueryElementNodeLeftTuple( leftTuple,
                                              sink,
                                              pctx,
                                              leftTupleMemoryEnabled );
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new QueryElementNodeLeftTuple( leftTuple,
                                              rightTuple,
                                              sink );
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new QueryElementNodeLeftTuple( leftTuple,
                                              rightTuple,
                                              currentLeftChild,
                                              currentRightChild,
                                              sink,
                                              leftTupleMemoryEnabled );
    }

    private int calculateHashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (openQuery ? 1231 : 1237);
        result = prime * result + ((queryElement == null) ? 0 : queryElement.hashCode());
        result = prime * result + ((leftInput == null) ? 0 : leftInput.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if ( !(object instanceof QueryElementNode) || this.hashCode() != object.hashCode() ) {
            return false;
        }

        QueryElementNode other = (QueryElementNode) object;
        if ( this.leftInput.getId() != other.leftInput.getId() ) return false;
        if ( openQuery != other.openQuery ) return false;
        if ( !openQuery && dataDriven != other.dataDriven ) return false;
        if ( queryElement == null ) {
            if ( other.queryElement != null ) return false;
        } else if ( !queryElement.equals( other.queryElement ) ) return false;
        return true;
    }

    @Override
    public QueryElementNodeMemory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return new QueryElementNodeMemory(this);
    }
    
    public static class QueryElementNodeMemory extends AbstractBaseLinkedListNode<Memory> implements SegmentNodeMemory {
        private QueryElementNode node;

        private SegmentMemory smem;

        private SegmentMemory querySegmentMemory;

        private TupleSets<LeftTuple> resultLeftTuples;

        private long nodePosMaskBit;

        public QueryElementNodeMemory(QueryElementNode node) {
            this.node = node;

            // if there is only one sink there is no split and then no smem staging and no normalization
            // otherwise it uses special tuplset with alternative linking fields (rightParentPrev/Next)
            this.resultLeftTuples = node.getSinkPropagator().size() > 1 ?
                                    new QueryTupleSets() : new TupleSetsImpl<>();
        }

        public QueryElementNode getNode() {
            return this.node;
        }

        @Override
        public short getNodeType() {
            return NodeTypeEnums.QueryElementNode;
        }

        @Override
        public void setSegmentMemory(SegmentMemory smem) {
            this.smem = smem;
        }

        @Override
        public SegmentMemory getSegmentMemory() {
            return smem;
        }

        public SegmentMemory getQuerySegmentMemory() {
            return querySegmentMemory;
        }

        public void setQuerySegmentMemory(SegmentMemory querySegmentMemory) {
            this.querySegmentMemory = querySegmentMemory;
        }

        public TupleSets<LeftTuple> getResultLeftTuples() {
            return resultLeftTuples;
        }

        public void correctMemoryOnSinksChanged(TerminalNode removingTN) {
            if (resultLeftTuples instanceof QueryTupleSets ) {
                if (!SegmentUtilities.isTipNode( node, removingTN )) {
                    // a sink has been removed and now there is no longer a split
                    TupleSetsImpl<LeftTuple> newTupleSets = new TupleSetsImpl<>();
                    this.resultLeftTuples.addTo( newTupleSets );
                    this.resultLeftTuples = newTupleSets;
                }
            } else {
                if (SegmentUtilities.isTipNode( node, removingTN )) {
                    // a sink has been added and now there is a split
                    TupleSetsImpl<LeftTuple> newTupleSets = new QueryTupleSets();
                    this.resultLeftTuples.addTo( newTupleSets );
                    this.resultLeftTuples = newTupleSets;
                }
            }
        }

        @Override
        public long getNodePosMaskBit() {
            return nodePosMaskBit;
        }

        @Override
        public void setNodePosMaskBit(long segmentPos) {
            this.nodePosMaskBit = segmentPos;
        }

        @Override
        public void setNodeDirtyWithoutNotify() {
            smem.updateDirtyNodeMask( nodePosMaskBit );
        }

        @Override
        public void setNodeCleanWithoutNotify() {
            smem.updateCleanNodeMask( nodePosMaskBit );
        }

        @Override
        public void reset() {
            resultLeftTuples.resetAll();
        }

        public static class QueryTupleSets extends TupleSetsImpl<LeftTuple> {
            @Override
            protected LeftTuple getPreviousTuple( LeftTuple tuple ) {
                return tuple.getRightParentPrevious();
            }

            @Override
            protected void setPreviousTuple( LeftTuple tuple, LeftTuple stagedPrevious ) {
                tuple.setRightParentPrevious( stagedPrevious );
            }

            @Override
            protected LeftTuple getNextTuple( LeftTuple tuple ) {
                return tuple.getRightParentNext();
            }

            @Override
            protected void setNextTuple( LeftTuple tuple, LeftTuple stagedNext ) {
                tuple.setRightParentNext( stagedNext );
            }

            @Override
            protected void setStagedType( LeftTuple tuple, short type ) {
                tuple.setStagedTypeForQueries( type );
            }

            @Override
            protected short getStagedType( LeftTuple tuple ) {
                return tuple.getStagedTypeForQueries();
            }

            @Override
            public void addTo(TupleSets<LeftTuple> tupleSets) {
                addAllInsertsTo( tupleSets );
                addAllDeletesTo( tupleSets );
                addAllUpdatesTo( tupleSets );
            }

            private void addAllInsertsTo( TupleSets<LeftTuple> tupleSets ) {
                LeftTuple leftTuple = getInsertFirst();
                while (leftTuple != null) {
                    LeftTuple next = getNextTuple( leftTuple );
                    clear( leftTuple );
                    tupleSets.addInsert( leftTuple );
                    leftTuple = next;
                }
                setInsertFirst( null );
            }

            private void addAllUpdatesTo( TupleSets<LeftTuple> tupleSets ) {
                LeftTuple leftTuple = getUpdateFirst();
                while (leftTuple != null) {
                    LeftTuple next = getNextTuple( leftTuple );
                    clear( leftTuple );
                    tupleSets.addUpdate( leftTuple );
                    leftTuple = next;
                }
                setUpdateFirst( null );
            }

            private void addAllDeletesTo( TupleSets<LeftTuple> tupleSets ) {
                LeftTuple leftTuple = getDeleteFirst();
                while (leftTuple != null) {
                    LeftTuple next = getNextTuple( leftTuple );
                    clear( leftTuple );
                    tupleSets.addDelete( leftTuple );
                    leftTuple = next;
                }
                setDeleteFirst( null );
            }

            private void clear( LeftTuple leftTuple ) {
                setStagedType( leftTuple, Tuple.NONE );
                setPreviousTuple( leftTuple, null );
                setNextTuple( leftTuple, null );
            }
        }
    }

    @Override
    public ObjectTypeNode getObjectTypeNode() {
        return leftInput.getObjectTypeNode();
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        QueryElementNodeLeftTuple peer = new QueryElementNodeLeftTuple();
        peer.initPeer((BaseLeftTuple) original, this);
        original.setPeer(peer);
        return peer;
    }

    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + "(" + this.id + ", " + queryElement.getQueryName() + ")]";
    }

    @Override
    public void doAttach(BuildContext context ) {
        super.doAttach(context);
        this.leftInput.addTupleSink( this, context );
    }

    @Override
    protected boolean doRemove(RuleRemovalContext context,
                               ReteooBuilder builder) {
        if (!isInUse()) {
            getLeftTupleSource().removeTupleSink(this);
            return true;
        }
        return false;
    }
}
