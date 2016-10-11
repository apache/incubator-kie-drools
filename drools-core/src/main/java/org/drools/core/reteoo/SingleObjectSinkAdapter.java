/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SingleObjectSinkAdapter implements ObjectSinkPropagator {

    private static final long serialVersionUID = 510l;

    private ObjectSink      sink;
    private ObjectSink[]    sinks;

    public SingleObjectSinkAdapter() { }

    public SingleObjectSinkAdapter(final ObjectSink sink) {
        this.sink = sink;
        this.sinks = new ObjectSink[]{this.sink};
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.sink = (ObjectSink) in.readObject();
        this.sinks = new ObjectSink[]{this.sink};
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( sink );
    }

    public ObjectSinkPropagator addObjectSink(ObjectSink sink, int alphaNodeHashingThreshold) {
        final CompositeObjectSinkAdapter sinkAdapter = new CompositeObjectSinkAdapter( alphaNodeHashingThreshold );
        sinkAdapter.addObjectSink( this.sink, alphaNodeHashingThreshold );
        sinkAdapter.addObjectSink( sink, alphaNodeHashingThreshold );
        return sinkAdapter;
    }

    public ObjectSinkPropagator removeObjectSink(final ObjectSink sink) {
        if (this.sink.equals( sink )) {
            return EmptyObjectSinkAdapter.getInstance();
        }
        throw new IllegalArgumentException( "Cannot remove " + sink + " when this sink propagator only contains " + this.sink );
    }

    public void propagateAssertObject(final InternalFactHandle factHandle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        this.sink.assertObject( factHandle,
                                context,
                                workingMemory );
    }

    public void propagateModifyObject(InternalFactHandle factHandle,
                                            ModifyPreviousTuples modifyPreviousTuples,
                                            PropagationContext context,
                                            InternalWorkingMemory workingMemory) {
        this.sink.modifyObject( factHandle,
                                       modifyPreviousTuples,
                                       context,
                                       workingMemory );
    }
    
    public void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                        final ModifyPreviousTuples modifyPreviousTuples,
                                        final PropagationContext context,
                                        final InternalWorkingMemory workingMemory) {
        sink.byPassModifyToBetaNode( factHandle, modifyPreviousTuples, context, workingMemory );
    }
    
    public void  doLinkRiaNode(InternalWorkingMemory wm) {
        staticDoLinkRiaNode( sink, wm );
    }
    
    public static void staticDoLinkRiaNode(ObjectSink sink, InternalWorkingMemory wm) {
        BetaMemory bm;
        if ( sink.getType() == NodeTypeEnums.AccumulateNode ) {
            AccumulateNode accnode = ( AccumulateNode ) sink;
            AccumulateMemory accMem = ( AccumulateMemory ) wm.getNodeMemory( accnode );
            bm = accMem.getBetaMemory();
        } else if ( NodeTypeEnums.isBetaNode( sink ) ) {
            BetaNode betaNode = ( BetaNode ) sink;
            bm = BetaNode.getBetaMemoryFromRightInput(betaNode, wm);
        } else {
            throw new RuntimeException( "Should not be possible to have link into a node of type" + sink);
        }

        if ( bm.getStagedRightTuples().isEmpty() ) {
            if ( bm.getRightTupleMemory().size() == 0 ) {
                bm.linkNode(wm);
            } else {
                bm.setNodeDirty(wm);
            }
        }
    }
    
    public void  doUnlinkRiaNode( InternalWorkingMemory wm) {
        staticDoUnlinkRiaNode( sink, wm );
    }   
    
    public static void staticDoUnlinkRiaNode(ObjectSink sink,  InternalWorkingMemory wm) {
        BetaMemory bm;
        if ( sink.getType() == NodeTypeEnums.AccumulateNode ) {
            AccumulateNode accnode = ( AccumulateNode ) sink;
            AccumulateMemory accMem = ( AccumulateMemory ) wm.getNodeMemory( accnode );
            bm = accMem.getBetaMemory();
        } else if ( NodeTypeEnums.isBetaNode( sink ) ) {
            BetaNode betaNode = ( BetaNode ) sink;
            bm = BetaNode.getBetaMemoryFromRightInput(betaNode, wm);
        } else {
            throw new RuntimeException( "Should not be possible to have link into a node of type" + sink);
        }

        if (sink.getType() == NodeTypeEnums.NotNode) {
            bm.linkNode(wm);
        } else {
            bm.unlinkNode(wm);
        }
    }

    public BaseNode getMatchingNode(BaseNode candidate) {
        return sink.thisNodeEquals(candidate) ? (BaseNode) sink : null;
    }

    public ObjectSink[] getSinks() {
        return sinks;
    }

    public int size() {
        return 1;
    }

    public boolean isEmpty() {
        return false;
    }
}
