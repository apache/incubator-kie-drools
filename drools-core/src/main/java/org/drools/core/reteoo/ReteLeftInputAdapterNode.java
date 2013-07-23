package org.drools.core.reteoo;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.ReteWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleComponent;
import org.kie.api.definition.rule.Rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

public class ReteLeftInputAdapterNode extends LeftInputAdapterNode {

    public ReteLeftInputAdapterNode() {
    }

    public ReteLeftInputAdapterNode(int id, ObjectSource source, BuildContext context) {
        super(id, source, context);
    }

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        boolean useLeftMemory = true;

        if ( !workingMemory.isSequential() ) {
            if ( !isLeftTupleMemoryEnabled() ) {
                // This is a hack, to not add closed DroolsQuery objects
                Object object = factHandle.getObject();
                if ( object instanceof DroolsQuery) {
                    if ( !((DroolsQuery)object).isOpen() ) {
                        useLeftMemory = false;
                    }
                }
            }

            this.sink.createAndPropagateAssertLeftTuple( factHandle,
                                                         context,
                                                         workingMemory,
                                                         useLeftMemory,
                                                         this );
        } else {
            ((ReteWorkingMemory)workingMemory).addLIANodePropagation( new LIANodePropagation( this,
                                                                         factHandle,
                                                                         context ) );
        }
    }

    public void retractLeftTuple(LeftTuple leftTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
                                                       context,
                                                       workingMemory );

    }

    public void modifyObject(InternalFactHandle factHandle,
                             final ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        this.sink.propagateModifyObject( factHandle,
                                         modifyPreviousTuples,
                                         context,
                                         workingMemory );
    }

    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
        this.sink.byPassModifyToBetaNode(factHandle,
                                         modifyPreviousTuples,
                                         context,
                                         workingMemory);
    }

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final RightTupleSinkAdapter adapter = new RightTupleSinkAdapter( sink,
                                                                         true );
        getObjectSource().updateSink(adapter,
                                     context,
                                     workingMemory);
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        if (!isInUse()) {
            getObjectSource().removeObjectSink(this);
            for ( InternalWorkingMemory wm : workingMemories ) {
                wm.clearNodeMemory( (MemoryFactory) this);
            }
        }
    }

}
