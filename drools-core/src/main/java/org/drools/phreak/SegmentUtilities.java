package org.drools.phreak;

import org.drools.common.InternalWorkingMemory;
import org.drools.common.MemoryFactory;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.EvalConditionNode;
import org.drools.reteoo.EvalConditionNode.EvalMemory;
import org.drools.reteoo.FromNode;
import org.drools.reteoo.FromNode.FromMemory;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSinkNode;
import org.drools.reteoo.LeftTupleSinkPropagator;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.reteoo.RuleMemory;
import org.drools.reteoo.SegmentMemory;

public class SegmentUtilities {

    /**
     * Initialises the NodeSegment memory for all nodes in the segment.
     * @param wm
     */
    public static SegmentMemory createSegmentMemory(LeftTupleSource tupleSource ,
                                                    final InternalWorkingMemory wm) {
    	// find segment root
    	while ( BetaNode.parentInSameSegment(tupleSource)  ) {
    		tupleSource = tupleSource.getLeftTupleSource();
    	}
    	
    	LeftTupleSource segmentRoot = tupleSource;
    	
    	SegmentMemory smem = new SegmentMemory(segmentRoot);
    
    	// Iterate all nodes on the same segment, assigning their position as a bit mask value
    	// allLinkedTestMask is the resulting mask used to test if all nodes are linked in
    	long nodePosMask = 1;	
    	long allLinkedTestMask = 0;
    	
    	while ( true ) {
            if ( NodeTypeEnums.isBetaNode( tupleSource ) ) {
                BetaMemory betaMemory;
                BetaNode betaNode = ( BetaNode ) tupleSource;
                if ( NodeTypeEnums.AccumulateNode == tupleSource.getType() ) {       
                    betaMemory = (( AccumulateMemory ) smem.createNodeMemory( ( AccumulateNode ) tupleSource, wm  )).getBetaMemory();
                } else {                    
                    betaMemory = ( BetaMemory ) smem.createNodeMemory( betaNode, wm );
    
                }
    
                if ( betaNode.isRightInputIsRiaNode() ) {
                    // we need to iterate to find correct pair
                    // as there may be more than one set of sub networks, due to sharing.
                    LeftTupleSinkNode sinkNode = betaNode.getLeftTupleSource().getSinkPropagator().getFirstLeftTupleSink();
                    while ( sinkNode.getNextLeftTupleSinkNode() != betaNode ) {
                        sinkNode = sinkNode.getNextLeftTupleSinkNode();
                    }
                    
                    SegmentMemory subNetworkSegmentMemory = createSegmentMemory( ( LeftTupleSource ) sinkNode, wm );
                    betaMemory.setSubnetworkSegmentMemor( subNetworkSegmentMemory );
                }
                
                betaMemory.setSegmentMemory( smem );
                betaMemory.setNodePosMaskBit( nodePosMask );
                allLinkedTestMask = allLinkedTestMask | nodePosMask;
                if ( NodeTypeEnums.NotNode == tupleSource.getType() ||  NodeTypeEnums.AccumulateNode == tupleSource.getType())  {
                    // NotNode's and Accumulate are initialised as linkedin
                    smem.linkNode( nodePosMask, wm );
                }
                nodePosMask = nodePosMask << 1;
            } else if ( tupleSource.getType() == NodeTypeEnums.LeftInputAdapterNode ) {                
                LiaNodeMemory liaMemory = ( LiaNodeMemory ) smem.createNodeMemory( ( LeftInputAdapterNode ) tupleSource, wm );
                liaMemory.setSegmentMemory( smem );
                liaMemory.setNodePosMaskBit( nodePosMask );
                allLinkedTestMask = allLinkedTestMask | nodePosMask;
    
                nodePosMask = nodePosMask << 1;                
            } else if ( tupleSource.getType() == NodeTypeEnums.EvalConditionNode ) {
                EvalMemory evalMemory = ( EvalMemory ) smem.createNodeMemory( ( EvalConditionNode ) tupleSource, wm );
                evalMemory.setSegmentMemory( smem );
            } else if ( tupleSource.getType() == NodeTypeEnums.FromNode ) {
                FromMemory fromMemory = ( FromMemory ) smem.createNodeMemory( ( FromNode ) tupleSource, wm );
                fromMemory.getBetaMemory().setSegmentMemory( smem );
            }
            
            LeftTupleSinkPropagator sink = tupleSource.getSinkPropagator();
            LeftTupleSinkNode firstSink = (LeftTupleSinkNode) sink.getFirstLeftTupleSink() ;
            LeftTupleSinkNode secondSink = firstSink.getNextLeftTupleSinkNode();
            if ( secondSink == null ) {
                if ( NodeTypeEnums.isLeftTupleSource( firstSink ) ) {
                    tupleSource = ( LeftTupleSource ) firstSink;
                } else {
                    // rtn or rian
                    break;
                }
            } else if ( sink.size() == 2 && 
                           NodeTypeEnums.isBetaNode( secondSink ) && 
                               ((BetaNode)secondSink).isRightInputIsRiaNode() ) {
                // must be a subnetwork split, always take the non riaNode path
                tupleSource = ( LeftTupleSource )secondSink;
            } else {
                // not in same segment
                break;
            }   
            
    	}		
    	smem.setAllLinkedMaskTest( allLinkedTestMask );
    	smem.setTipNode( tupleSource );
    
    	
    	// iterate to find root and determine the SegmentNodes position in the RuleSegment
        LeftTupleSource parent = segmentRoot;	
        int ruleSegmentPosMask = 1;
        int counter = 0;
        while ( parent.getLeftTupleSource() != null ) {
               if ( !BetaNode.parentInSameSegment( parent ) ) {
                   // for each new found segment, increase the mask bit position
                   ruleSegmentPosMask = ruleSegmentPosMask << 1;
                   counter++;
               }    	        
           parent = parent.getLeftTupleSource();	          
        }
        smem.setSegmentPosMaskBit( ruleSegmentPosMask );
        smem.setPos( counter );
    	
    	SegmentUtilities.updateRiaAndTerminalMemory( 0, tupleSource, tupleSource, smem, wm );
    	return smem;
    }

    /**
     * Is the LeftTupleSource a node in the sub network for the RightInputAdapterNode
     * 
     * @param riaNode
     * @param leftTupleSource
     * @return
     */
    public static boolean inSubNetwork(RightInputAdapterNode riaNode, LeftTupleSource leftTupleSource) {
        LeftTupleSource startTupleSource = riaNode.getStartTupleSource();
        LeftTupleSource parent = riaNode.getLeftTupleSource();
        
        while ( parent != startTupleSource ) {
            if ( parent == leftTupleSource) {
                return true;
            }
            parent = parent.getLeftTupleSource();
        }
        
        return false;
    }

    public static void updateRiaAndTerminalMemory(int pos, LeftTupleSource lt,
                                                  LeftTupleSource originalLt,
                                                  SegmentMemory smem,
                                                  InternalWorkingMemory wm) {
        for ( LeftTupleSink sink : lt.getSinkPropagator().getSinks() ) {
    	    if (NodeTypeEnums.isLeftTupleSource( sink ) ) {
    	        if ( sink.getType() == NodeTypeEnums.NotNode ) {
    	            BetaMemory bm = ( BetaMemory) smem.createNodeMemory( (MemoryFactory) sink, wm  );
    	             if ( bm.getSegmentMemory() == null ) {
    	                 // Not nodes must be initialised
    	                 createSegmentMemory( (NotNode) sink, wm );
    	             }
    	        }
    	        updateRiaAndTerminalMemory(++pos, ( LeftTupleSource ) sink, originalLt, smem, wm);
    	    } else if ( sink.getType() == NodeTypeEnums.RightInputAdaterNode) {
    	        RiaNodeMemory memory = ( RiaNodeMemory ) smem.createNodeMemory( (MemoryFactory) sink, wm  );        	        
    	        // Only add the RIANode, if the LeftTupleSource is part of the RIANode subnetwork.
    	        if ( inSubNetwork( (RightInputAdapterNode)sink, originalLt ) ) {    	        
    	            smem.getRuleMemories().add( memory.getRuleSegments() );
    	        }
    	    } else if ( NodeTypeEnums.isTerminalNode( sink) ) {
    	        RuleMemory rmem = ( RuleMemory ) smem.createNodeMemory( (MemoryFactory) sink, wm  );
                smem.getRuleMemories().add( rmem );
                rmem.getSegmentMemories()[smem.getPos()] = smem;
    	        
    	    }
        }
    }

}
