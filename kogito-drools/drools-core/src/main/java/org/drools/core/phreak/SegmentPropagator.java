package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.SegmentMemory;

public class SegmentPropagator {

    public static void propagate(SegmentMemory sourceSegment, LeftTupleSets stagedLeftTuples, InternalWorkingMemory wm) {
        LeftTupleSource source = ( LeftTupleSource )  sourceSegment.getTipNode();
        
        if ( sourceSegment.isEmpty() ) {
            SegmentUtilities.createChildSegments( wm, sourceSegment, source.getSinkPropagator() );
        }
                
        processPeers(sourceSegment, stagedLeftTuples);
    }    
    
    public static void processPeers(SegmentMemory sourceSegment, LeftTupleSets leftTuples) {
        
        // Process Deletes
        SegmentMemory firstSmem = sourceSegment.getFirst();
        if ( leftTuples.getDeleteFirst() != null ) {
            for ( LeftTuple leftTuple = leftTuples.getDeleteFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {                        
                SegmentMemory smem = firstSmem.getNext();
                if ( smem != null ) {
                    for ( LeftTuple peer = leftTuple.getPeer(); peer != null; peer = peer.getPeer() ) {
                        peer.setPropagationContext( leftTuple.getPropagationContext() );
                        LeftTupleSets stagedLeftTuples = smem.getStagedLeftTuples();
                        // if the peer is already staged as insert or update the LeftTupleSets will reconcile it internally
                        stagedLeftTuples.addDelete( peer );
                        smem = smem.getNext();
                    }
                }
            }
            firstSmem.getStagedLeftTuples().addAllDeletes( leftTuples );
        }
        
        // Process Updates        
        if ( leftTuples.getUpdateFirst() != null ) {
            firstSmem = sourceSegment.getFirst();
            for ( LeftTuple leftTuple = leftTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {            
                SegmentMemory smem = firstSmem.getNext();
                if ( smem != null ) {                
                    for ( LeftTuple peer = leftTuple.getPeer(); peer != null; peer = peer.getPeer() ) {
                        // only stage, if not already staged, if insert, leave as insert
                        if ( peer.getStagedType() == LeftTuple.NONE ) {
                            peer.setPropagationContext( leftTuple.getPropagationContext() );
                            LeftTupleSets stagedLeftTuples = smem.getStagedLeftTuples();
                            stagedLeftTuples.addUpdate( peer );
                        }
                        
                        smem = smem.getNext();
                    }
                }            
            }   
            firstSmem.getStagedLeftTuples().addAllUpdates( leftTuples );
        }
        
        // Process Inserts
        if ( leftTuples.getInsertFirst() != null ) { 
            firstSmem = sourceSegment.getFirst();
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
                        }
                    }
                }           
            }
            firstSmem.getStagedLeftTuples().addAllInserts( leftTuples );
        }
    }
    
    public static void processPeers(LeftTupleSets leftTuples, LeftTupleSets peerLeftTuples, LeftTupleSink sink) {    
        
        // Process Deletes
        for ( LeftTuple leftTuple = leftTuples.getDeleteFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {
            peerLeftTuples.addDelete( leftTuple.getPeer() );
        }
        
        // Process Updates        
        for ( LeftTuple leftTuple = leftTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {                        
            peerLeftTuples.addUpdate( leftTuple.getPeer() );
        }
        
        // Process Inserts        
        for ( LeftTuple leftTuple = leftTuples.getInsertFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {                        
            peerLeftTuples.addInsert( sink.createPeer( leftTuple ) );
        }                     
    }    
}
