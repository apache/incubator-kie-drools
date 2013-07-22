package org.drools.core.reteoo;

import org.drools.core.base.DroolsQuery;
import org.drools.core.base.InternalViewChangedEventListener;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.common.AbstractWorkingMemory.QueryInsertAction;
import org.drools.core.common.AbstractWorkingMemory.QueryResultInsertAction;
import org.drools.core.common.AbstractWorkingMemory.QueryResultRetractAction;
import org.drools.core.common.AbstractWorkingMemory.QueryResultUpdateAction;
import org.drools.core.common.AbstractWorkingMemory.QueryRetractAction;
import org.drools.core.common.AbstractWorkingMemory.QueryUpdateAction;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleIterator;
import org.drools.core.common.PropagationContextImpl;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.Rule;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.Iterator;
import org.drools.core.util.index.RightTupleList;
import org.kie.api.runtime.rule.Variable;

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
                                               (LeftTupleSink) this, getLeftInputOtnId(), getLeftInferredMask());
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
            Object object = ((InternalFactHandle) leftTuple.get(0)).getObject();
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

        InternalFactHandle handle = (InternalFactHandle) leftTuple.getObject();
        DroolsQuery queryObject = (DroolsQuery) handle.getObject();
        if (queryObject.getAction() != null) {
            // we already have an insert scheduled for this query, but have re-entered it
            // do nothing
            return;
        }

        Object[] argTemplate = this.queryElement.getArgTemplate(); // an array of declr, variable and literals
        Object[] args = new Object[argTemplate.length]; // the actual args, to be created from the  template

        // first copy everything, so that we get the literals. We will rewrite the declarations and variables next
        System.arraycopy(argTemplate,
                         0,
                         args,
                         0,
                         args.length);

        int[] declIndexes = this.queryElement.getDeclIndexes();

        for (int i = 0, length = declIndexes.length; i < length; i++) {
            Declaration declr = (Declaration) argTemplate[declIndexes[i]];

            Object tupleObject = leftTuple.get(declr).getObject();

            Object o;

            if (tupleObject instanceof DroolsQuery) {
                // If the query passed in a Variable, we need to use it
                ArrayElementReader arrayReader = (ArrayElementReader) declr.getExtractor();
                if (((DroolsQuery) tupleObject).getVariables()[arrayReader.getIndex()] != null) {
                    o = Variable.v;
                } else {
                    o = declr.getValue(workingMemory,
                                       tupleObject);
                }
            } else {
                o = declr.getValue(workingMemory,
                                   tupleObject);
            }

            args[declIndexes[i]] = o;
        }

        int[] varIndexes = this.queryElement.getVariableIndexes();
        for (int i = 0, length = varIndexes.length; i < length; i++) {
            if (argTemplate[varIndexes[i]] == Variable.v) {
                // Need to check against the arg template, as the varIndexes also includes re-declared declarations
                args[varIndexes[i]] = Variable.v;
            }
        }

        queryObject.setParameters(args);
        ((ReteUnificationNodeViewChangedEventListener) queryObject.getQueryResultCollector()).setVariables(varIndexes);

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

    protected void doCollectAncestors(NodeSet nodeSet) {
        getLeftTupleSource().collectAncestors(nodeSet);
    }

    public void attach(BuildContext context) {
        super.attach(context);
        if (context == null) {
            return;
        }

        for (InternalWorkingMemory workingMemory : context.getWorkingMemories()) {
            final PropagationContext propagationContext = new PropagationContextImpl(workingMemory.getNextPropagationIdCounter(),
                                                                                     PropagationContext.RULE_ADDITION,
                                                                                     null,
                                                                                     null,
                                                                                     null);
            this.leftInput.updateSink(this,
                                      propagationContext,
                                      workingMemory);
        }
    }

    protected void doRemove(RuleRemovalContext context,
                            ReteooBuilder builder,
                            InternalWorkingMemory[] workingMemories) {
        if (!isInUse()) {
            for (InternalWorkingMemory workingMemory : workingMemories) {
                workingMemory.clearNodeMemory(this);
            }
            getLeftTupleSource().removeTupleSink(this);
        }
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
                    childLeftTuple = childLeftTuple.getLeftParentNext();
                }
            }
        }
    }

    protected UnificationNodeViewChangedEventListener createCollector(LeftTuple leftTuple, int[] varIndexes, boolean tupleMemoryEnabled) {
        return new ReteUnificationNodeViewChangedEventListener(leftTuple,
                                                               varIndexes,
                                                               this,
                                                               tupleMemoryEnabled,
                                                               isUnlinkingEnabled());
    }

    public static class ReteUnificationNodeViewChangedEventListener extends UnificationNodeViewChangedEventListener
            implements
            InternalViewChangedEventListener {

        public ReteUnificationNodeViewChangedEventListener(LeftTuple leftTuple,
                                                           int[] variables,
                                                           QueryElementNode node,
                                                           boolean tupleMemoryEnabled,
                                                           boolean unlinkedEnabled) {
            super(leftTuple, variables, node, tupleMemoryEnabled, unlinkedEnabled);
        }

        public void rowAdded(final Rule rule,
                             LeftTuple resultLeftTuple,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {

            QueryTerminalNode node = (QueryTerminalNode) resultLeftTuple.getLeftTupleSink();
            Declaration[] decls = node.getDeclarations();
            DroolsQuery dquery = (DroolsQuery) this.factHandle.getObject();
            Object[] objects = new Object[dquery.getElements().length];

            Declaration decl;
            for (int i = 0, length = this.variables.length; i < length; i++) {
                decl = decls[this.variables[i]];
                objects[this.variables[i]] = decl.getValue(workingMemory,
                                                           resultLeftTuple.get(decl).getObject());
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

            RightTupleList rightTuples = dquery.getResultInsertRightTupleList();
            if (rightTuples == null) {
                rightTuples = new RightTupleList();
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
            RightTuple rightTuple = new RightTuple(resultHandle);
            if (open) {
                rightTuple.setLeftTuple(resultLeftTuple);
                resultLeftTuple.setObject(rightTuple);

            }
            rightTuple.setPropagationContext(resultLeftTuple.getPropagationContext());
            return rightTuple;
        }

        public void rowRemoved(final Rule rule,
                               final LeftTuple resultLeftTuple,
                               final PropagationContext context,
                               final InternalWorkingMemory workingMemory) {
            RightTuple rightTuple = (RightTuple) resultLeftTuple.getObject();
            rightTuple.setLeftTuple(null);
            resultLeftTuple.setObject(null);

            DroolsQuery query = (DroolsQuery) this.factHandle.getObject();

            RightTupleList rightTuples = query.getResultRetractRightTupleList();
            if (rightTuples == null) {
                rightTuples = new RightTupleList();
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

        public void rowUpdated(final Rule rule,
                               final LeftTuple resultLeftTuple,
                               final PropagationContext context,
                               final InternalWorkingMemory workingMemory) {
            RightTuple rightTuple = (RightTuple) resultLeftTuple.getObject();
            if (rightTuple.getMemory() != null) {
                // Already sheduled as an insert
                return;
            }

            rightTuple.setLeftTuple(null);
            resultLeftTuple.setObject(null);

            // We need to recopy everything back again, as we don't know what has or hasn't changed
            QueryTerminalNode node = (QueryTerminalNode) resultLeftTuple.getLeftTupleSink();
            Declaration[] decls = node.getDeclarations();
            InternalFactHandle rootHandle = resultLeftTuple.get(0);
            DroolsQuery dquery = (DroolsQuery) rootHandle.getObject();

            Object[] objects = new Object[dquery.getElements().length];

            Declaration decl;
            for (int i = 0, length = this.variables.length; i < length; i++) {
                decl = decls[this.variables[i]];
                objects[this.variables[i]] = decl.getValue(workingMemory,
                                                           resultLeftTuple.get(decl).getObject());
            }

            QueryElementFactHandle handle = (QueryElementFactHandle) rightTuple.getFactHandle();

            handle.setRecency(workingMemory.getFactHandleFactory().getAtomicRecency().incrementAndGet());
            handle.setObject(objects);

            if (dquery.isOpen()) {
                rightTuple.setLeftTuple(resultLeftTuple);
                resultLeftTuple.setObject(rightTuple);
            }

            // Don't need to recreate child links, as they will already be there form the first "add"

            RightTupleList rightTuples = dquery.getResultUpdateRightTupleList();
            if (rightTuples == null) {
                rightTuples = new RightTupleList();
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
}
