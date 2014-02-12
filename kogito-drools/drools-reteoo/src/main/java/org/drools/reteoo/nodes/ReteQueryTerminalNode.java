package org.drools.reteoo.nodes;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.PropagationContext;

public class ReteQueryTerminalNode extends QueryTerminalNode {

    public ReteQueryTerminalNode() {
    }

    public ReteQueryTerminalNode(int id, LeftTupleSource source, RuleImpl rule, GroupElement subrule, int subruleIndex, BuildContext context) {
        super(id, source, rule, subrule, subruleIndex, context);
    }

    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        // find the DroolsQuery object
        LeftTuple entry = leftTuple.getRootLeftTuple();

        DroolsQuery query = (DroolsQuery) entry.getLastHandle().getObject();
        query.setQuery( this.query );

        // Add results to the adapter
        query.getQueryResultCollector().rowAdded( this.query,
                                                  leftTuple,
                                                  context,
                                                  workingMemory );
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        // find the DroolsQuery object

        LeftTuple entry = leftTuple.getRootLeftTuple();

        DroolsQuery query = (DroolsQuery) entry.getLastHandle().getObject();
        query.setQuery( this.query );

        // Add results to the adapter
        query.getQueryResultCollector().rowRemoved( this.query,
                                                    leftTuple,
                                                    context,
                                                    workingMemory );
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        // find the DroolsQuery object
        LeftTuple entry = leftTuple.getRootLeftTuple();

        DroolsQuery query = (DroolsQuery) entry.getLastHandle().getObject();
        query.setQuery( this.query );

        // Add results to the adapter
        query.getQueryResultCollector().rowUpdated( this.query,
                                                    leftTuple,
                                                    context,
                                                    workingMemory );
    }

    public void attach( BuildContext context ) {
        getLeftTupleSource().addTupleSink( this, context );
        if (context == null ) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION, null, null, null);
            getLeftTupleSource().updateSink( this, propagationContext, workingMemory );
        }
    }
}
