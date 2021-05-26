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

import java.util.Iterator;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;

import static org.drools.core.phreak.AddRemoveRule.forceFlushLeftTuple;
import static org.drools.core.phreak.AddRemoveRule.forceFlushWhenRiaNode;

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

        Iterator<SegmentMemory> peersIterator = sourceSegment.getPeersWithDataDrivenPathMemoriesIterator();
        while (peersIterator.hasNext()) {
            SegmentMemory smem = peersIterator.next();
            for (PathMemory dataDrivenPmem : smem.getDataDrivenPathMemories()) {
                if (smem.getStagedLeftTuples().getDeleteFirst() == null &&
                    smem.getStagedLeftTuples().getUpdateFirst() == null &&
                    !dataDrivenPmem.isRuleLinked()) {
                    // skip flushing segments that have only inserts staged and the path is not linked
                    continue;
                }
                forceFlushLeftTuple(dataDrivenPmem, smem, wm, smem.getStagedLeftTuples());
                forceFlushWhenRiaNode(wm, dataDrivenPmem);
            }
        }
    }

    private static void processPeers(SegmentMemory sourceSegment, TupleSets<LeftTuple> leftTuples, InternalWorkingMemory wm) {
        SegmentMemory firstSmem = sourceSegment.getFirst();

        processPeerDeletes( leftTuples, leftTuples.getDeleteFirst(), firstSmem, wm );
        processPeerDeletes( leftTuples, leftTuples.getNormalizedDeleteFirst(), firstSmem, wm );
        processPeerUpdates( leftTuples, firstSmem );
        processPeerInserts( leftTuples, firstSmem );

        firstSmem.getStagedLeftTuples().addAll( leftTuples );
        leftTuples.resetAll();
    }

    private static void processPeerInserts(TupleSets<LeftTuple> leftTuples, SegmentMemory firstSmem) {
        for (LeftTuple leftTuple = leftTuples.getInsertFirst(); leftTuple != null; leftTuple =  leftTuple.getStagedNext()) {
            SegmentMemory smem = firstSmem.getNext();
            if ( smem != null ) {
                // It's possible for a deleted tuple and set of peers, to be cached on a delete (such as with accumulates).
                // So we should check if the instances already exist and use them if they do.
                if (leftTuple.getPeer() == null) {
                    LeftTuple peer = leftTuple;
                    // peers do not exist, so create and add them.
                    for (; smem != null; smem = smem.getNext()) {
                        LeftTupleSink sink = smem.getSinkFactory();
                        peer = sink.createPeer(peer); // pctx is set during peer cloning
                        smem.getStagedLeftTuples().addInsert( peer );
                    }
                } else {
                    LeftTuple peer = leftTuple.getPeer();
                    // peers exist, so update them as an insert, which also handles staged clashing.
                    for (; smem != null; smem = smem.getNext()) {
                        peer.setPropagationContext( leftTuple.getPropagationContext() );
                        // ... and update the staged LeftTupleSets according to its current staged state
                        updateChildLeftTupleDuringInsert(peer, smem.getStagedLeftTuples(), smem.getStagedLeftTuples());
                        peer = peer.getPeer();
                    }
                }
            }
        }
    }

    private static void processPeerUpdates(TupleSets<LeftTuple> leftTuples, SegmentMemory firstSmem) {
        for (LeftTuple leftTuple = leftTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {
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
    }

    public static void updateChildLeftTupleDuringInsert(LeftTuple childLeftTuple,
                                                        TupleSets<LeftTuple> stagedLeftTuples,
                                                        TupleSets<LeftTuple> trgLeftTuples) {
        switch ( childLeftTuple.getStagedType() ) {
            // handle clash when they re already staged entries
            case LeftTuple.INSERT:
                // was staged as insert before, remove it from staging and now process
                stagedLeftTuples.removeInsert( childLeftTuple );
                break;
            case LeftTuple.UPDATE:
                throw new IllegalStateException("It should not be possible that an existing udpate is staged, when an insert is later requested.");
        }
        trgLeftTuples.addInsert( childLeftTuple );
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
                    smem = smem.getNext();
                }
            }
        }
    }
}
