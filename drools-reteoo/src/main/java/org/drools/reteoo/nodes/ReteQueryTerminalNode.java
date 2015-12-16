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
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;

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
        Tuple entry = leftTuple.getRootTuple();

        DroolsQuery query = (DroolsQuery) entry.getFactHandle().getObject();
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

        Tuple entry = leftTuple.getRootTuple();

        DroolsQuery query = (DroolsQuery) entry.getFactHandle().getObject();
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
        Tuple entry = leftTuple.getRootTuple();

        DroolsQuery query = (DroolsQuery) entry.getFactHandle().getObject();
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
