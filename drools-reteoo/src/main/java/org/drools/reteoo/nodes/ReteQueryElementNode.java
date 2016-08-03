/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.reteoo.nodes;

import org.drools.core.base.DroolsQuery;
import org.drools.core.base.InternalViewChangedEventListener;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleIterator;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages.ActionQueue.Action;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.LeftTupleSourceUtils;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.QueryElement;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.Iterator;
import org.drools.core.util.index.TupleList;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class ReteQueryElementNode extends QueryElementNode {

    public ReteQueryElementNode() {
    }

    public ReteQueryElementNode(int id, LeftTupleSource tupleSource, QueryElement queryElement,
                                boolean tupleMemoryEnabled, boolean openQuery, BuildContext context) {
        super(id, tupleSource, queryElement, tupleMemoryEnabled, openQuery, context);
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTupleSourceUtils.doModifyLeftTuple(factHandle, modifyPreviousTuples, context, workingMemory,
                                               this, getLeftInputOtnId(), getLeftInferredMask());
    }

    public void assertLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        // the next call makes sure this node's memory is initialised
        workingMemory.getNodeMemory(this);

        InternalFactHandle handle = createFactHandle(context, workingMemory, leftTuple);

        DroolsQuery queryObject = createDroolsQuery(leftTuple, handle,
                                                    null, null, null, null,
                                                    workingMemory);

        QueryInsertAction action = new QueryInsertAction(context,
                                                         handle,
                                                         leftTuple,
                                                         this);
        queryObject.setAction(action); // this is necessary as queries can be re-entrant, so we can check this before re-sheduling
        // another action in the modify section. Make sure it's nulled after the action is done
        // i.e. scheduling an insert and then an update, before the insert is executed
        context.addInsertAction(action);

    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        boolean executeAsOpenQuery = openQuery;
        if (executeAsOpenQuery) {
            // There is no point in doing an open query if the caller is a non-open query.
            Object object = leftTuple.get(0).getObject();
            if (object instanceof DroolsQuery && !((DroolsQuery) object).isOpen()) {
                executeAsOpenQuery = false;
            }
        }

        if (!executeAsOpenQuery) {
            // Was never open so execute as a retract + assert
            if (leftTuple.getFirstChild() != null) {
                this.sink.propagateRetractLeftTuple(leftTuple,
                                                    context,
                                                    workingMemory);
            }
            assertLeftTuple(leftTuple,
                            context,
                            workingMemory);
            return;
        }

        InternalFactHandle handle = (InternalFactHandle) leftTuple.getContextObject();
        DroolsQuery queryObject = (DroolsQuery) handle.getObject();
        if (queryObject.getAction() != null) {
            // we already have an insert scheduled for this query, but have re-entered it
            // do nothing
            return;
        }

        queryObject.setParameters(getActualArguments( leftTuple, workingMemory ));

        QueryUpdateAction action = new QueryUpdateAction(context,
                                                         handle,
                                                         leftTuple,
                                                         this);
        context.addInsertAction(action);
    }

    public void retractLeftTuple(LeftTuple leftTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        QueryRetractAction action = new QueryRetractAction( context,
                                                            leftTuple,
                                                            this );
        context.addInsertAction( action );
    }

    public void attach(BuildContext context) {
        super.attach(context);
        if (context == null) {
            return;
        }

        for (InternalWorkingMemory workingMemory : context.getWorkingMemories()) {
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION,
                                                                                               null, null, null);
            this.leftInput.updateSink(this,
                                      propagationContext,
                                      workingMemory);
        }
    }

    protected boolean doRemove(RuleRemovalContext context,
                               ReteooBuilder builder,
                               InternalWorkingMemory[] workingMemories) {
        if (!isInUse()) {
            for (InternalWorkingMemory workingMemory : workingMemories) {
                workingMemory.clearNodeMemory(this);
            }
            getLeftTupleSource().removeTupleSink(this);
            return true;
        }
        return false;
    }

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        Iterator<LeftTuple> it = LeftTupleIterator.iterator(workingMemory, this);

        for (LeftTuple leftTuple = it.next(); leftTuple != null; leftTuple = it.next()) {
            LeftTuple childLeftTuple = leftTuple.getFirstChild();
            while (childLeftTuple != null) {
                RightTuple rightParent = childLeftTuple.getRightParent();
                sink.assertLeftTuple(sink.createLeftTuple(leftTuple, rightParent, childLeftTuple, null, sink, true),
                                     context,
                                     workingMemory);

                while (childLeftTuple != null && childLeftTuple.getRightParent() == rightParent) {
                    // skip to the next child that has a different right parent
                    childLeftTuple = childLeftTuple.getHandleNext();
                }
            }
        }
    }

    protected UnificationNodeViewChangedEventListener createCollector(LeftTuple leftTuple, int[] varIndexes, boolean tupleMemoryEnabled) {
        return new ReteUnificationNodeViewChangedEventListener(leftTuple,
                                                               varIndexes,
                                                               this,
                                                               tupleMemoryEnabled );
    }

    public static class ReteUnificationNodeViewChangedEventListener extends UnificationNodeViewChangedEventListener
            implements
            InternalViewChangedEventListener {

        public ReteUnificationNodeViewChangedEventListener(LeftTuple leftTuple,
                                                           int[] variables,
                                                           QueryElementNode node,
                                                           boolean tupleMemoryEnabled) {
            super(leftTuple, variables, node, tupleMemoryEnabled);
        }

        public void rowAdded(final RuleImpl rule,
                             LeftTuple resultLeftTuple,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {

            QueryTerminalNode node = (QueryTerminalNode) resultLeftTuple.getTupleSink();
            Declaration[] decls = node.getRequiredDeclarations();
            DroolsQuery dquery = (DroolsQuery) this.factHandle.getObject();
            Object[] objects = new Object[dquery.getElements().length];

            Declaration decl;
            for ( int variable : this.variables ) {
                decl = decls[variable];
                objects[variable] = decl.getValue( workingMemory,
                                                   resultLeftTuple.get( decl ).getObject() );
            }

            QueryElementFactHandle resultHandle = createQueryResultHandle(context,
                                                                          workingMemory,
                                                                          objects);

            RightTuple rightTuple = createResultRightTuple(resultHandle, resultLeftTuple, dquery.isOpen());

            this.node.getSinkPropagator().createChildLeftTuplesforQuery(this.leftTuple,
                                                                        rightTuple,
                                                                        true, // this must always be true, otherwise we can't
                                                                        // find the child tuples to iterate for evaluating the dquery results
                                                                        dquery.isOpen());

            TupleList rightTuples = dquery.getResultInsertRightTupleList();
            if (rightTuples == null) {
                rightTuples = new TupleList();
                dquery.setResultInsertRightTupleList(rightTuples);
                QueryResultInsertAction evalAction = new QueryResultInsertAction(context,
                                                                                 this.factHandle,
                                                                                 leftTuple,
                                                                                 this.node);
                context.getQueue2().addFirst(evalAction);
            }

            rightTuples.add(rightTuple);


        }

        protected RightTuple createResultRightTuple(QueryElementFactHandle resultHandle, LeftTuple resultLeftTuple, boolean open) {
            RightTuple rightTuple = new RightTupleImpl(resultHandle);
            if (open) {
                rightTuple.setBlocked( resultLeftTuple );
                resultLeftTuple.setContextObject( rightTuple );

            }
            rightTuple.setPropagationContext(resultLeftTuple.getPropagationContext());
            return rightTuple;
        }

        public void rowRemoved(final RuleImpl rule,
                               final LeftTuple resultLeftTuple,
                               final PropagationContext context,
                               final InternalWorkingMemory workingMemory) {
            RightTuple rightTuple = (RightTuple) resultLeftTuple.getContextObject();
            rightTuple.setBlocked( null );
            resultLeftTuple.setContextObject( null );

            DroolsQuery query = (DroolsQuery) this.factHandle.getObject();

            TupleList rightTuples = query.getResultRetractRightTupleList();
            if (rightTuples == null) {
                rightTuples = new TupleList();
                query.setResultRetractRightTupleList(rightTuples);
                QueryResultRetractAction retractAction = new QueryResultRetractAction(context,
                                                                                      this.factHandle,
                                                                                      leftTuple,
                                                                                      this.node);
                context.getQueue2().addFirst(retractAction);
            }
            if (rightTuple.getMemory() != null) {
                throw new RuntimeException();
            }
            rightTuples.add(rightTuple);
        }

        public void rowUpdated(final RuleImpl rule,
                               final LeftTuple resultLeftTuple,
                               final PropagationContext context,
                               final InternalWorkingMemory workingMemory) {
            RightTuple rightTuple = (RightTuple) resultLeftTuple.getContextObject();
            if (rightTuple.getMemory() != null) {
                // Already sheduled as an insert
                return;
            }

            rightTuple.setBlocked( null );
            resultLeftTuple.setContextObject( null );

            // We need to recopy everything back again, as we don't know what has or hasn't changed
            QueryTerminalNode node = (QueryTerminalNode) resultLeftTuple.getTupleSink();
            Declaration[] decls = node.getRequiredDeclarations();
            InternalFactHandle rootHandle = resultLeftTuple.get(0);
            DroolsQuery dquery = (DroolsQuery) rootHandle.getObject();

            Object[] objects = new Object[dquery.getElements().length];

            Declaration decl;
            for ( int variable : this.variables ) {
                decl = decls[variable];
                objects[variable] = decl.getValue( workingMemory,
                                                   resultLeftTuple.get( decl ).getObject() );
            }

            QueryElementFactHandle handle = (QueryElementFactHandle) rightTuple.getFactHandle();

            handle.setRecency(workingMemory.getFactHandleFactory().getAtomicRecency().incrementAndGet());
            handle.setObject(objects);

            if (dquery.isOpen()) {
                rightTuple.setBlocked(resultLeftTuple);
                resultLeftTuple.setContextObject( rightTuple );
            }

            // Don't need to recreate child links, as they will already be there form the first "add"

            TupleList rightTuples = dquery.getResultUpdateRightTupleList();
            if (rightTuples == null) {
                rightTuples = new TupleList();
                dquery.setResultUpdateRightTupleList(rightTuples);
                QueryResultUpdateAction updateAction = new QueryResultUpdateAction(context,
                                                                                   this.factHandle,
                                                                                   leftTuple,
                                                                                   this.node);
                context.getQueue2().addFirst(updateAction);
            }
            rightTuples.add(rightTuple);
        }

        public List<? extends Object> getResults() {
            throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support the getResults() method.");
        }

        public LeftTuple getLeftTuple() {
            return leftTuple;
        }

    }

    public static class QueryInsertAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {
        private PropagationContext context;

        private InternalFactHandle factHandle;

        private LeftTuple        leftTuple;
        private QueryElementNode node;

        public QueryInsertAction(PropagationContext context) {
            this.context = context;
        }

        public QueryInsertAction(PropagationContext context,
                                 InternalFactHandle factHandle,
                                 LeftTuple leftTuple,
                                 QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryInsertAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public Action serialize(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void execute(InternalWorkingMemory workingMemory) {
            // we null this as it blocks this query being called, to avoid re-entrant issues. i.e. scheduling an insert and then an update, before the insert is executed
            ((DroolsQuery) this.factHandle.getObject()).setAction(null);
            workingMemory.getEntryPointNode().assertQuery(factHandle,
                                                          context,
                                                          workingMemory);
        }

        public String toString() {
            return "[QueryInsertAction facthandle=" + factHandle + ",\n        leftTuple=" + leftTuple + "]\n";
        }
    }

    public static class QueryUpdateAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {
        private PropagationContext context;

        private InternalFactHandle factHandle;

        private LeftTuple        leftTuple;
        private QueryElementNode node;

        public QueryUpdateAction(PropagationContext context) {
            this.context = context;
        }

        public QueryUpdateAction(PropagationContext context,
                                 InternalFactHandle factHandle,
                                 LeftTuple leftTuple,
                                 QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryUpdateAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public Action serialize(MarshallerWriteContext context) {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void execute(InternalWorkingMemory workingMemory) {
            workingMemory.getEntryPointNode().modifyQuery(factHandle,
                                                          context,
                                                          workingMemory);
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute(((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory());
        }

        public String toString() {
            return "[QueryInsertModifyAction facthandle=" + factHandle + ",\n        leftTuple=" + leftTuple + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
        }
    }

    public static class QueryRetractAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {
        private PropagationContext context;
        private LeftTuple          leftTuple;
        private QueryElementNode   node;

        public QueryRetractAction(PropagationContext context) {
            this.context = context;
        }

        public QueryRetractAction(PropagationContext context,
                                  LeftTuple leftTuple,
                                  QueryElementNode node) {
            this.context = context;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryRetractAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public Action serialize(MarshallerWriteContext context) {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void execute(InternalWorkingMemory workingMemory) {
            InternalFactHandle factHandle = (InternalFactHandle) leftTuple.getContextObject();
            if (node.isOpenQuery()) {
                // iterate to the query terminal node, as the child leftTuples will get picked up there
                workingMemory.getEntryPointNode().retractObject(factHandle,
                                                                context,
                                                                workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf(workingMemory.getEntryPoint(),
                                                                                                                                     factHandle.getObject()),
                                                                workingMemory);
                //workingMemory.getFactHandleFactory().destroyFactHandle( factHandle );
            } else {
                // get child left tuples, as there is no open query
                if (leftTuple.getFirstChild() != null) {
                    node.getSinkPropagator().propagateRetractLeftTuple(leftTuple,
                                                                       context,
                                                                       workingMemory);
                }
            }
        }

        public String toString() {
            return "[QueryRetractAction leftTuple=" + leftTuple + "]\n";
        }
    }

    public static class QueryResultInsertAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {

        private PropagationContext context;

        private LeftTuple leftTuple;

        private InternalFactHandle factHandle;

        private QueryElementNode node;

        public QueryResultInsertAction(PropagationContext context) {
            this.context = context;
        }

        public QueryResultInsertAction(PropagationContext context,
                                       InternalFactHandle factHandle,
                                       LeftTuple leftTuple,
                                       QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryResultInsertAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public Action serialize(MarshallerWriteContext context) {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void execute(InternalWorkingMemory workingMemory) {
            DroolsQuery query = (DroolsQuery) factHandle.getObject();
            TupleList rightTuples = query.getResultInsertRightTupleList();
            query.setResultInsertRightTupleList(null); // null so further operations happen on a new stack element

            for (RightTuple rightTuple = (RightTuple) rightTuples.getFirst(); rightTuple != null; ) {
                RightTuple tmp = (RightTuple) rightTuple.getNext();
                rightTuples.remove(rightTuple);
                for (LeftTuple childLeftTuple = rightTuple.getFirstChild(); childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getRightParentNext()) {
                    node.getSinkPropagator().doPropagateAssertLeftTuple(context,
                                                                        workingMemory,
                                                                        childLeftTuple,
                                                                        (LeftTupleSink) childLeftTuple.getTupleSink());
                }
                rightTuple = tmp;
            }

            // @FIXME, this should work, but it's closing needed fact handles
            // actually an evaluation 34 appears on the stack twice....
            //            if ( !node.isOpenQuery() ) {
            //                workingMemory.getFactHandleFactory().destroyFactHandle( this.factHandle );
            //            }
        }

        public LeftTuple getLeftTuple() {
            return this.leftTuple;
        }

        public String toString() {
            return "[QueryEvaluationAction leftTuple=" + leftTuple + "]\n";
        }
    }

    public static class QueryResultRetractAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {
        private PropagationContext context;
        private LeftTuple          leftTuple;
        private InternalFactHandle factHandle;
        private QueryElementNode   node;

        public QueryResultRetractAction(PropagationContext context,
                                        InternalFactHandle factHandle,
                                        LeftTuple leftTuple,
                                        QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryResultRetractAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public Action serialize(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void execute(InternalWorkingMemory workingMemory) {
            DroolsQuery query = (DroolsQuery) factHandle.getObject();
            TupleList rightTuples = query.getResultRetractRightTupleList();
            query.setResultRetractRightTupleList(null); // null so further operations happen on a new stack element

            for (RightTuple rightTuple = (RightTuple) rightTuples.getFirst(); rightTuple != null; ) {
                RightTuple tmp = (RightTuple) rightTuple.getNext();
                rightTuples.remove(rightTuple);
                this.node.getSinkPropagator().propagateRetractRightTuple(rightTuple,
                                                                         context,
                                                                         workingMemory);
                rightTuple = tmp;
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute(((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory());
        }

        public LeftTuple getLeftTuple() {
            return this.leftTuple;
        }

        public String toString() {
            return "[QueryResultRetractAction leftTuple=" + leftTuple + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
        }
    }

    public static class QueryResultUpdateAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {
        private PropagationContext context;
        private LeftTuple          leftTuple;
        InternalFactHandle factHandle;
        private QueryElementNode node;

        public QueryResultUpdateAction(PropagationContext context,
                                       InternalFactHandle factHandle,
                                       LeftTuple leftTuple,
                                       QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryResultUpdateAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public Action serialize(MarshallerWriteContext context) {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void execute(InternalWorkingMemory workingMemory) {
            DroolsQuery query = (DroolsQuery) factHandle.getObject();
            TupleList rightTuples = query.getResultUpdateRightTupleList();
            query.setResultUpdateRightTupleList(null); // null so further operations happen on a new stack element

            for (RightTuple rightTuple = (RightTuple) rightTuples.getFirst(); rightTuple != null; ) {
                RightTuple tmp = (RightTuple) rightTuple.getNext();
                rightTuples.remove(rightTuple);
                this.node.getSinkPropagator().propagateModifyChildLeftTuple(rightTuple.getFirstChild(),
                                                                            rightTuple.getFirstChild().getLeftParent(),
                                                                            context,
                                                                            workingMemory,
                                                                            true);
                rightTuple = tmp;
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute(((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory());
        }

        public LeftTuple getLeftTuple() {
            return leftTuple;
        }

        public String toString() {
            return "[QueryResultUpdateAction leftTuple=" + leftTuple + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
        }

    }

}
