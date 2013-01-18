package org.drools.phreak;

import static org.drools.core.util.BitMaskUtil.intersect;

import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.common.MemoryFactory;
import org.drools.common.LeftTupleSets;
import org.drools.reteoo.CompositeLeftTupleSinkAdapter;
import org.drools.reteoo.EvalConditionNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSinkNode;
import org.drools.reteoo.LeftTupleSinkNodeList;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.SegmentMemory;
import org.drools.reteoo.EvalConditionNode.EvalMemory;

public class SegmentPropagator {
    
//    public static void propagateLia(SegmentMemory sourceSegment, StagedLeftTuples stagedLeftTuples, InternalWorkingMemory wm) {
//        LeftTupleSource source = ( LeftTupleSource )  sourceSegment.getTipNode();
//        
//        if ( sourceSegment.isEmpty() ) {
//            // We know it's a Composite  
//            LeftTupleSinkNodeList list = ((CompositeLeftTupleSinkAdapter)source.getSinkPropagator()).getRawSinks();
//            for (LeftTupleSinkNode sink = list.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
//                Memory memory = wm.getNodeMemories().getNodeMemory( ( MemoryFactory ) sink );
//                SegmentMemory smem = memory.getSegmentMemory();
//                sourceSegment.add( smem );
//            }
//        }
//                
//        processPeersLia(sourceSegment, stagedLeftTuples);
//    }
//
//    public static void processPeersLia(SegmentMemory sourceSegment, StagedLeftTuples leftTuples) {    
//        
//        // Process Deletes
//        SegmentMemory firstSmem = sourceSegment.getFirst();
//        if ( leftTuples.getDeleteFirst() != null ) {
//            for ( LeftTuple leftTuple = leftTuples.getDeleteFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {                        
//                SegmentMemory smem = firstSmem.getNext();
//                if ( smem != null ) {
//                    for ( LeftTuple peer = leftTuple.getPeer(); peer != null; peer = peer.getPeer() ) {
//                        StagedLeftTuples stagedLeftTuples = smem.getStagedLeftTuples();
//                        //(LeftTupleSink)smem.getTipNode()
//                        
//                        switch ( peer.getStagedType() ) {
//                            // handle clash with already staged entries
//                            case LeftTuple.INSERT:
//                                stagedLeftTuples.removeInsert( peer );
//                                break;
//                            case LeftTuple.UPDATE:
//                                stagedLeftTuples.removeUpdate( peer );
//                                break;
//                        }  
//                        stagedLeftTuples.addDelete( peer );
//                        smem = smem.getNext();
//                    }
//                }
//            }
//            firstSmem.getStagedLeftTuples().addAllDeletes( leftTuples.getDeleteFirst() );
//        }
//        
//        // Process Updates        
//        if ( leftTuples.getUpdateFirst() != null ) {
//            firstSmem = sourceSegment.getFirst(); //.getNext();
//            for ( LeftTuple leftTuple = leftTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext()) {            
//                SegmentMemory smem = firstSmem;
//                if ( smem != null ) {                
//                    for ( LeftTuple peer = leftTuple.getPeer(); peer != null; peer = peer.getPeer() ) {
//                        // if it's already propagated, leave it where it is, else filter by mask
//                        if ( peer.getStagedType() == LeftTuple.NONE &&
//                                intersect( peer.getPropagationContext().getModificationMask(), 
//                                           peer.getLeftTupleSink().getLeftInferredMask()) ) {
//                            StagedLeftTuples stagedLeftTuples = smem.getStagedLeftTuples();
//                            stagedLeftTuples.addUpdate( peer );
//                        }
//                        smem = smem.getNext();
//                    }
//                }            
//            }   
//            //firstSmem.getStagedLeftTuples().addAllUpdates( leftTuples.getUpdateFirst() );
//        }
//        
//        // Process Inserts
//        if ( leftTuples.getInsertFirst() != null ) { 
//            firstSmem = sourceSegment.getFirst();
//            for ( LeftTuple leftTuple = leftTuples.getInsertFirst(); leftTuple != null; leftTuple =  leftTuple.getStagedNext()) {            
//                SegmentMemory smem = firstSmem; //.getNext();
//                if ( smem != null ) {
//                    LeftTuple peer = leftTuple;
//                    for (; smem != null; smem = smem.getNext() ) {
//                        peer  = smem.getRootNode().createPeer( peer );
//                        long mask = peer.getLeftTupleSink().getLeftInferredMask();
//                        if ( mask == Long.MAX_VALUE ||
//                                intersect( peer.getPropagationContext().getModificationMask(), 
//                                           mask) ) {
//                            smem.getStagedLeftTuples().addInsert( peer );                            
//                        }                        
//                    }
//                }           
//            }
//            //firstSmem.getStagedLeftTuples().addAllInserts( leftTuples.getInsertFirst() );
//        }
//    }
//    
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
                        LeftTupleSets stagedLeftTuples = smem.getStagedLeftTuples();
                        switch ( peer.getStagedType() ) {
                            // handle clash with already staged entries
                            case LeftTuple.INSERT:
                                stagedLeftTuples.removeInsert( peer );
                                break;
                            case LeftTuple.UPDATE:
                                stagedLeftTuples.removeUpdate( peer );
                                break;
                        }  
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
                        LeftTupleSets stagedLeftTuples = smem.getStagedLeftTuples();
                        switch ( peer.getStagedType() ) {
                            // handle clash with already staged entries
                            case LeftTuple.INSERT:
                                stagedLeftTuples.removeInsert( peer );
                                break;
                        }  
                        stagedLeftTuples.addUpdate( peer );
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
                        peer  =  ((LeftTupleSink)smem.getRootNode()).createPeer( peer );
                        smem.getStagedLeftTuples().addInsert( peer );
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
