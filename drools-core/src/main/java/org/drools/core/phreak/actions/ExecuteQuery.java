package org.drools.core.phreak.actions;

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.base.DroolsQueryImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.phreak.RuntimeSegmentUtilities;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.TerminalNode;

public class ExecuteQuery extends PropagationEntryWithResult<ReteEvaluator, QueryTerminalNode[]> {

    private final String             queryName;
    private final DroolsQueryImpl    queryObject;
    private final InternalFactHandle handle;
    private final PropagationContext pCtx;
    private final boolean            calledFromRHS;

    public ExecuteQuery(String queryName, DroolsQueryImpl queryObject, InternalFactHandle handle, PropagationContext pCtx, boolean calledFromRHS) {
        this.queryName     = queryName;
        this.queryObject   = queryObject;
        this.handle        = handle;
        this.pCtx          = pCtx;
        this.calledFromRHS = calledFromRHS;
    }

    @Override
    public void internalExecute(ReteEvaluator reteEvaluator) {
        QueryTerminalNode[] tnodes = reteEvaluator.getKnowledgeBase().getReteooBuilder().getTerminalNodesForQuery(queryName);
        if (tnodes == null) {
            throw new RuntimeException("Query '" + queryName + "' does not exist");
        }

        QueryTerminalNode tnode = tnodes[0];

        if (queryObject.getElements().length != tnode.getQuery().getParameters().length) {
            throw new RuntimeException("Query '" + queryName + "' has been invoked with a wrong number of arguments. Expected " +
                                       tnode.getQuery().getParameters().length + ", actual " + queryObject.getElements().length);
        }

        LeftTupleSource lts = tnode.getLeftTupleSource();
        while (!NodeTypeEnums.isLeftInputAdapterNode(lts)) {
            lts = lts.getLeftTupleSource();
        }
        LeftInputAdapterNode               lian = (LeftInputAdapterNode) lts;
        LeftInputAdapterNode.LiaNodeMemory lmem = reteEvaluator.getNodeMemory(lian);
        if (lmem.getSegmentMemory() == null) {
            RuntimeSegmentUtilities.getOrCreateSegmentMemory(lmem, lts, reteEvaluator);
        }

        LeftInputAdapterNode.doInsertObject(handle, pCtx, lian, reteEvaluator, lmem, false, queryObject.isOpen());

        for (PathMemory rm : lmem.getSegmentMemory().getPathMemories()) {
            RuleAgendaItem evaluator = reteEvaluator.getActivationsManager().createRuleAgendaItem(Integer.MAX_VALUE, rm, (TerminalNode) rm.getPathEndNode());
            evaluator.getRuleExecutor().setDirty(true);
            evaluator.getRuleExecutor().evaluateNetworkAndFire(reteEvaluator, null, 0, -1);
        }

        done(tnodes);
    }

    @Override
    public boolean isCalledFromRHS() {
        return calledFromRHS;
    }
}
