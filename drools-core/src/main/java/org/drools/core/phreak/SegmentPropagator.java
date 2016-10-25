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

package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;

import static org.drools.core.phreak.AddRemoveRule.forceFlushLeftTuple;

public class SegmentPropagator {

    public static void propagate(SegmentMemory sourceSegment, TupleSets<LeftTuple> leftTuples, InternalWorkingMemory wm) {
        if (leftTuples.isEmpty()) {
            return;
        }

        LeftTupleSource source = ( LeftTupleSource )  sourceSegment.getTipNode();
        
        if ( sourceSegment.isEmpty() ) {
            SegmentUtilities.createChildSegments( wm, sourceSegment, source.getSinkPropagator() );
        }
                
        processPeers(sourceSegment, leftTuples, wm);
    }    
    
    private static void processPeers(SegmentMemory sourceSegment, TupleSets<LeftTuple> leftTuples, InternalWorkingMemory wm) {
        SegmentMemory firstSmem = sourceSegment.getFirst();

        // Process Deletes
        processPeerDeletes( leftTuples, leftTuples.getDeleteFirst(), firstSmem, wm );
        processPeerDeletes( leftTuples, leftTuples.getNormalizedDeleteFirst(), firstSmem, wm );

        // Process Updates
        for ( LeftTuple leftTuple = leftTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {
            SegmentMemory smem = firstSmem.getNext();
            if ( smem != null ) {
                for ( LeftTuple peer = leftTuple.getPeer(); peer != null; peer = peer.getPeer() ) {
                    // only stage, if not already staged, if insert, leave as insert
                    if ( peer.getStagedType() == LeftTuple.NONE ) {
                        peer.setPropagationContext( leftTuple.getPropagationContext() );
                        smem.getStagedLeftTuples().addUpdate( peer );
                    }

                    smem = smem.getNext();
                }
            }
        }

        // Process Inserts
        for ( LeftTuple leftTuple = leftTuples.getInsertFirst(); leftTuple != null; leftTuple =  leftTuple.getStagedNext()) {
            SegmentMemory smem = firstSmem.getNext();
            if ( smem != null ) {
                LeftTuple peer = leftTuple;
                for (; smem != null; smem = smem.getNext() ) {
                    if (peer.getPeer() != null) {
                        // if the tuple already has a peer avoid to create a new one ...
                        peer = peer.getPeer();
                        peer.setPropagationContext( leftTuple.getPropagationContext() );
                        // ... and update the staged LeftTupleSets according to its current staged state
                        PhreakJoinNode.updateChildLeftTuple(peer, smem.getStagedLeftTuples(), smem.getStagedLeftTuples());
                    } else {
                        peer = ((LeftTupleSink)smem.getRootNode()).createPeer( peer );
                        smem.getStagedLeftTuples().addInsert( peer );

                        if (smem.hasDataDrivenPathMemories()) {
                            for (PathMemory dataDrivenPmem : smem.getDataDrivenPathMemories()) {
                                // on insert only totally linked pmems need to be flushed
                                if (dataDrivenPmem.isRuleLinked()) {
                                    forceFlushLeftTuple(dataDrivenPmem, smem, wm, smem.getStagedLeftTuples());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        firstSmem.getStagedLeftTuples().addAll( leftTuples );
        leftTuples.resetAll();
    }

    private static void processPeerDeletes( TupleSets<LeftTuple> leftTuples, LeftTuple leftTuple, SegmentMemory firstSmem, InternalWorkingMemory wm ) {
        for (; leftTuple != null; leftTuple = leftTuple.getStagedNext()) {
            SegmentMemory smem = firstSmem.getNext();
            if ( smem != null ) {
                for ( LeftTuple peer = leftTuple.getPeer(); peer != null; peer = peer.getPeer() ) {
                    peer.setPropagationContext( leftTuple.getPropagationContext() );
                    TupleSets<LeftTuple> stagedLeftTuples = smem.getStagedLeftTuples();
                    // if the peer is already staged as insert or update the LeftTupleSets will reconcile it internally
                    stagedLeftTuples.addDelete( peer );

                    if (smem.hasDataDrivenPathMemories()) {
                        forceFlushLeftTuple(smem.getFirstDataDrivenPathMemory(), smem, wm, smem.getStagedLeftTuples());
                    }

                    smem = smem.getNext();
                }
            }
        }
    }
}
