package org.drools.serialization.protobuf;

import java.io.IOException;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.common.PropagationContext;
import org.drools.core.reteoo.Tuple;

public class WorkingMemoryReteAssertAction
        extends PropagationEntry.AbstractPropagationEntry
        implements WorkingMemoryAction {
    protected InternalFactHandle factHandle;

    protected boolean            removeLogical;

    protected boolean            updateEqualsMap;

    protected RuleImpl ruleOrigin;

    protected Tuple tuple;

    protected WorkingMemoryReteAssertAction() { }

    public WorkingMemoryReteAssertAction(MarshallerReaderContext context) throws IOException {
        this.factHandle = context.getHandles().get( context.readLong() );
        this.removeLogical = context.readBoolean();
        this.updateEqualsMap = context.readBoolean();

        if ( context.readBoolean() ) {
            String pkgName = context.readUTF();
            String ruleName = context.readUTF();
            InternalKnowledgePackage pkg = context.getKnowledgeBase().getPackage( pkgName );
            this.ruleOrigin = pkg.getRule( ruleName );
        }
        if ( context.readBoolean() ) {
            this.tuple = context.getTerminalTupleMap().get( context.readInt() );
        }
    }

    public void internalExecute(ReteEvaluator reteEvaluator) {
        PropagationContextFactory pctxFactory = RuntimeComponentFactory.get().getPropagationContextFactory();

        final PropagationContext context = pctxFactory.createPropagationContext(reteEvaluator.getNextPropagationIdCounter(), PropagationContext.Type.INSERTION,
                this.ruleOrigin, this.tuple != null ? this.tuple.getTupleSink() : null, this.factHandle);
        reteEvaluator.getKnowledgeBase().getRete().assertObject(this.factHandle, context, reteEvaluator);
    }
}