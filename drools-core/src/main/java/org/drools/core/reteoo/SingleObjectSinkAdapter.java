/*
 * Copyright 2010 JBoss Inc
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
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SingleObjectSinkAdapter extends AbstractObjectSinkAdapter {

    private static final long serialVersionUID = 510l;

    protected ObjectSink      sink;

    public SingleObjectSinkAdapter() {
        super( null );
    }

    public SingleObjectSinkAdapter(final RuleBasePartitionId partitionId,
                                   final ObjectSink sink) {
        super( partitionId );
        this.sink = sink;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        sink = (ObjectSink) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( sink );
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
            bm = ( BetaMemory ) accMem.getBetaMemory();            
        } else if ( NodeTypeEnums.isBetaNode( sink ) ) {
            BetaNode betaNode = ( BetaNode ) sink;
            bm = (BetaMemory) BetaNode.getBetaMemoryFromRightInput(betaNode, wm);
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
            bm = ( BetaMemory ) accMem.getBetaMemory();            
        } else if ( NodeTypeEnums.isBetaNode( sink ) ) {
            BetaNode betaNode = ( BetaNode ) sink;
            bm = (BetaMemory) BetaNode.getBetaMemoryFromRightInput(betaNode, wm);                       
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
        if ( candidate.equals( sink ) ) {
            return (BaseNode) sink;
        }
        return null;
    }

    public ObjectSink[] getSinks() {
        return new ObjectSink[]{this.sink};
    }

    public int size() {
        return 1;
    }

}
