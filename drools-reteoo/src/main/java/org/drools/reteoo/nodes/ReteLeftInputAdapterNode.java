/*
 * Copyright 2015 JBoss Inc
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
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LIANodePropagation;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.reteoo.common.ReteWorkingMemory;

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

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder,
                               final InternalWorkingMemory[] workingMemories) {
        if (!isInUse()) {
            getObjectSource().removeObjectSink(this);
            for ( InternalWorkingMemory wm : workingMemories ) {
                wm.clearNodeMemory( this );
            }
            return true;
        }
        return false;
    }

}
