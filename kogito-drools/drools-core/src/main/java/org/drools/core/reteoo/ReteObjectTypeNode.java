package org.drools.core.reteoo;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;

public class ReteObjectTypeNode extends ObjectTypeNode {

    public ReteObjectTypeNode() {
    }

    public ReteObjectTypeNode(int id, EntryPointNode source, ObjectType objectType, BuildContext context) {
        super(id, source, objectType, context);
    }

    public void attach( BuildContext context ) {
        super.attach( context );
        if (context == null ) {
            return;
        }

        // we need to call updateSink on Rete, because someone
        // might have already added facts matching this ObjectTypeNode
        // to working memories
        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION,
                                                                                               null, null, null);
            propagationContext.setEntryPoint( ((EntryPointNode) this.source).getEntryPoint() );
            this.source.updateSink( this,
                                    propagationContext,
                                    workingMemory );
        }
    }
}
