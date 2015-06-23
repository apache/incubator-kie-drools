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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashMap.ObjectEntry;

public class ReteRightInputAdapterNode extends RightInputAdapterNode {

    public ReteRightInputAdapterNode() {
    }

    public ReteRightInputAdapterNode(int id, LeftTupleSource source, LeftTupleSource startTupleSource, BuildContext context) {
        super(id, source, startTupleSource, context);
    }

    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        Memory memory = workingMemory.getNodeMemory( this ); // while we don't do anything with this, it's needed to serialization has a hook point
                                                             // It will still keep the LT's in the serialization, but sucked form the child


        // creating a dummy fact handle to wrap the tuple
        final InternalFactHandle handle = createFactHandle( leftTuple, context, workingMemory );
        boolean useLeftMemory = true;
        if ( !isLeftTupleMemoryEnabled() ) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = ((InternalFactHandle) leftTuple.get( 0 )).getObject();
            if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                useLeftMemory = false;
            }
        }

        if ( useLeftMemory) {
            leftTuple.setObject(handle);
        }

        // propagate it
        this.sink.propagateAssertObject( handle,
                                         context,
                                         workingMemory );

//        if ( useLeftMemory) {
//            leftTuple.setObject( handle.getFirstRightTuple() );
//        }

    }

    /**
     * Retracts the corresponding tuple by retrieving and retracting
     * the fact created for it
     */
    public void retractLeftTuple(final LeftTuple tuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        // retrieve handle from memory
        final InternalFactHandle factHandle = (InternalFactHandle) tuple.getObject();

        for ( RightTuple rightTuple = factHandle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
            rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                              context,
                                                              workingMemory );
        }
        factHandle.clearRightTuples();

        for ( LeftTuple leftTuple = factHandle.getLastLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
            leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
                                                           context,
                                                           workingMemory );
        }
        factHandle.clearLeftTuples();
    }


    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        // add it to a memory mapping
        InternalFactHandle handle = (InternalFactHandle) leftTuple.getObject();

        // propagate it
        for ( RightTuple rightTuple = handle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
            rightTuple.getRightTupleSink().modifyRightTuple( rightTuple,
                                                             context,
                                                             workingMemory );
        }
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        BetaNode betaNode = (BetaNode) this.sink.getSinks()[0];

        Memory betaMemory = workingMemory.getNodeMemory( betaNode );
        BetaMemory bm;
        if ( betaNode.getType() == NodeTypeEnums.AccumulateNode ) {
            bm =  ((AccumulateMemory) betaMemory).getBetaMemory();
        } else {
            bm =  (BetaMemory) betaMemory;
        }

        // for RIA nodes, we need to store the ID of the created handles
        bm.getRightTupleMemory().iterator();
        if ( bm.getRightTupleMemory().size() > 0 ) {
            final org.drools.core.util.Iterator it = bm.getRightTupleMemory().iterator();
            for ( RightTuple entry = (RightTuple) it.next(); entry != null; entry = (RightTuple) it.next() ) {
                LeftTuple leftTuple = (LeftTuple) entry.getFactHandle().getObject();
                InternalFactHandle handle = (InternalFactHandle) leftTuple.getObject();
                sink.assertObject( (InternalFactHandle) handle,
                                   context,
                                   workingMemory );
            }
        }
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        // this is now done by the child beta node, as it needs the child beta memory
        // if ( !this.isInUse() ) {
        //     removeMemory(workingMemories);
        // }
        if ( !isInUse() ) {
            getLeftTupleSource().removeTupleSink(this);
        }
    }

    public void removeMemory(InternalWorkingMemory workingMemory) {
        BetaNode betaNode = (BetaNode) this.sink.getSinks()[0];

        Memory betaMemory = workingMemory.getNodeMemory( betaNode );
        BetaMemory bm;
        if ( betaNode.getType() == NodeTypeEnums.AccumulateNode ) {
            bm =  ((AccumulateMemory) betaMemory).getBetaMemory();
        } else {
            bm =  (BetaMemory) betaMemory;
        }

        if ( bm.getRightTupleMemory().size() > 0 ) {
            final Iterator it = bm.getRightTupleMemory().iterator();
            for ( RightTuple entry = (RightTuple) it.next(); entry != null; entry = (RightTuple) it.next() ) {
                LeftTuple leftTuple = (LeftTuple) entry.getFactHandle().getObject();
                leftTuple.unlinkFromLeftParent();
                leftTuple.unlinkFromRightParent();
            }
        }
        workingMemory.clearNodeMemory( this );
    }

    public Memory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        RiaNodeMemory rianMem = new RiaNodeMemory();

        return rianMem;
    }

}
