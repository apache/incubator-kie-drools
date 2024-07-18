package org.drools.core.reteoo;

import org.drools.base.base.ObjectType;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.AbstractLinkedListNode;
import org.drools.core.util.LinkedList;
import org.drools.util.bitmask.BitMask;

import java.util.List;

public class ObjectRouter {

    private RouteAdapter[] adapters;

    private LinkedList<Filter>[] activeFilters;

    public ObjectRouter() {
    }

    public void setRouterAdapters(RouteAdapter[] adapters) {
        this.adapters = adapters;
        activeFilters = new LinkedList[adapters.length];
        for (int i = 0; i < activeFilters.length; i++) {
            activeFilters[i] = new LinkedList<>();
        }
    }

    public void addFilter(Filter filter, int index) {
        activeFilters[index].add(filter);
    }

//    public void received(InternalFactHandle factHandle, PropagationContext pctx, ReteEvaluator reteEvaluator, RouteAdapter adapter) {
//        System.out.println(adapter.getObjectTypeNode() + " : " + adapter.adapterIndex);
//
//        for (Filter filter = activeFilters[adapter.adapterIndex].getFirst(); filter != null; filter = filter.getNext()) {
//            filter.assertObject(factHandle, pctx, reteEvaluator);
//        }
//    }

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext pctx,
                             final ReteEvaluator reteEvaluator) {
//        System.out.println(adapter.getObjectTypeNode() + " : " + adapter.adapterIndex);
//
//        for (Filter filter = activeFilters[adapter.adapterIndex].getFirst(); filter != null; filter = filter.getNext()) {
//            filter.assertObject(factHandle, pctx, reteEvaluator);
//        }
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             ReteEvaluator reteEvaluator) {

    }

    public void retractRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
        // for now assuming we don't have any deletes, but we could add this later.
    }

    public void modifyRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
        // for now assuming we don't have any modifies, but we could add this later.
        // typically just gets treated as another add, as the processor is not that stateful (for this use case) - to keep it fast.
    }


    public static class Filter extends AbstractLinkedListNode<Filter>  {
        private AlphaNodeFieldConstraint constraint;
        private SequenceNode             node;


        public Filter(AlphaNodeFieldConstraint constraint) {
            this.constraint = constraint;
        }

        public AlphaNodeFieldConstraint getConstraint() {
            return constraint;
        }

        public void assertObject(final InternalFactHandle factHandle,
                                 final PropagationContext pctx,
                                 final ReteEvaluator reteEvaluator) {
            if (constraint.isAllowed(factHandle, reteEvaluator)) {
                System.out.println("true : " + factHandle.getObject());
                //router.received(factHandle, pctx, reteEvaluator, this);
            }
        }

        public void modifyObject(InternalFactHandle factHandle,
                                 ModifyPreviousTuples modifyPreviousTuples,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator) {

        }

        public void retractRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
            // for now assuming we don't have any deletes, but we could add this later.
        }

        public void modifyRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
            // for now assuming we don't have any modifies, but we could add this later.
            // typically just gets treated as another add, as the processor is not that stateful (for this use case) - to keep it fast.
        }
    }

    /**
     * Used with the updateSink method, so that the parent ObjectSource
     * can  update the  TupleSink
     */
    public static class RouteAdapter extends ObjectSource
            implements
            ObjectSinkNode,
            RightTupleSink {

        private ObjectRouter router;
        private int          adapterIndex;

        private ObjectTypeNodeId otnId;


        public RouteAdapter(int id, ObjectSource source, RuleBasePartitionId partitionId, ObjectRouter router, int adapterIndex) {
            super(id, source, partitionId);
            this.router = router;
            this.adapterIndex = adapterIndex;
        }

        public void assertObject(final InternalFactHandle factHandle,
                                 final PropagationContext pctx,
                                 final ReteEvaluator reteEvaluator) {
            //router.received(factHandle, pctx, reteEvaluator, this);

        }

        public void modifyObject(InternalFactHandle factHandle,
                                 ModifyPreviousTuples modifyPreviousTuples,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator) {

        }

        @Override
        public void retractRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
            // for now assuming we don't have any deletes, but we could add this later.
        }

        @Override
        public void modifyRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
            // for now assuming we don't have any modifies, but we could add this later.
            // typically just gets treated as another add, as the processor is not that stateful (for this use case) - to keep it fast.
        }

        public int getId() {
            return 0;
        }

        public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                           ModifyPreviousTuples modifyPreviousTuples,
                                           PropagationContext context,
                                           ReteEvaluator reteEvaluator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSinkNode getNextObjectSinkNode() {
            return null;
        }

        @Override
        public void setNextObjectSinkNode(ObjectSinkNode next) {

        }

        @Override
        public ObjectSinkNode getPreviousObjectSinkNode() {
            return null;
        }

        @Override
        public void setPreviousObjectSinkNode(ObjectSinkNode previous) {

        }

        @Override
        public BitMask calculateDeclaredMask(ObjectType modifiedType, List<String> settableProperties) {
            return null;
        }

        @Override
        public void updateSink(ObjectSink sink, PropagationContext context, InternalWorkingMemory workingMemory) {

        }

        @Override
        public ObjectTypeNodeId getInputOtnId() {
            return otnId;
        }

        public int getType() {
            return NodeTypeEnums.SequenceNode; // need to update enums for multi input (mdp)
        }

        public void doAttach(BuildContext context) {
            super.doAttach(context);
            this.source.addObjectSink(this);
        }
    }
}
