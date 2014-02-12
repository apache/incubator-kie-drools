package org.drools.reteoo.nodes;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;

public class ReteAlphaNode extends AlphaNode {
    public ReteAlphaNode() {
    }

    public ReteAlphaNode(int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context) {
        super(id, constraint, objectSource, context);
    }

    public void attach(BuildContext context) {
        super.attach(context);
        if (context == null ) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION,
                                                                                               null, null, null);
            this.source.updateSink( this,
                                    propagationContext,
                                    workingMemory );
        }
    }
}
