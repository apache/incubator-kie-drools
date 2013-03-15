package org.drools.phreak;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.drools.reteoo.*;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.ConditionalBranchNode.ConditionalBranchMemory;
import org.drools.reteoo.EvalConditionNode.EvalMemory;
import org.drools.reteoo.FromNode.FromMemory;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.rule.constraint.QueryNameConstraint;

public class SegmentUtilities {

//    public static RightInputAdapterNode getOuterMostRiaNode(RightInputAdapterNode riaNode, LeftTupleSource startLTs) {
//        if ( riaNode.getStartTupleSource() != startLTs ) {
//            // This is a nested subnetwork, so we know there must be atleast one outer subnetwork
//            LeftTupleSource lts = riaNode.getLeftTupleSource();
//            while ( true ) {
//                if ( NodeTypeEnums.isBetaNode(lts) && (( BetaNode )lts).isRightInputIsRiaNode() ) {
//                    return getOuterMostRiaNode( ( RightInputAdapterNode ) ((BetaNode)lts).getRightInput(), startLTs );
//                }
//                lts = lts.getLeftTupleSource();
//            }
//        } else {
//            return riaNode;
//        }
//    }

    /**
     * Initialises the NodeSegment memory for all nodes in the segment.
     * @param wm
     */
    public static SegmentMemory createSegmentMemory(LeftTupleSource tupleSource ,
                                                    final InternalWorkingMemory wm) {
        boolean initRtn = false;
        if ( tupleSource.getType() == NodeTypeEnums.LeftInputAdapterNode ) {
            initRtn = true;
        }

    	// find segment root
    	while ( tupleSource.getType() != NodeTypeEnums.LeftInputAdapterNode &&
                SegmentUtilities.parentInSameSegment(tupleSource)  ) {
    		tupleSource = tupleSource.getLeftTupleSource();
    	}

    	LeftTupleSource segmentRoot = tupleSource;

        if ( initRtn ) {
            initialiseRtnMemory(segmentRoot, wm);
        }

    	SegmentMemory smem = new SegmentMemory(segmentRoot);
    
    	// Iterate all nodes on the same segment, assigning their position as a bit mask value
    	// allLinkedTestMask is the resulting mask used to test if all nodes are linked in
    	long nodePosMask = 1;	
    	long allLinkedTestMask = 0;
    	
    	while ( true ) {
            if ( NodeTypeEnums.isBetaNode( tupleSource ) ) {
                BetaMemory bm;
                BetaNode betaNode = ( BetaNode ) tupleSource;
                if ( NodeTypeEnums.AccumulateNode == tupleSource.getType() ) {       
                    bm = (( AccumulateMemory ) smem.createNodeMemory( ( AccumulateNode ) tupleSource, wm  )).getBetaMemory();
                } else {                    
                    bm = ( BetaMemory ) smem.createNodeMemory( betaNode, wm );
    
                }
                // this must be set first, to avoid recursion as sub networks can be initialised multiple ways
                // and bm.getSegmentMemory == null check can be used to avoid recursion.
                bm.setSegmentMemory(smem);
    
                if ( betaNode.isRightInputIsRiaNode() ) {
                    // Iterate to find outermost rianode
                    RightInputAdapterNode riaNode = (RightInputAdapterNode) betaNode.getRightInput();
                    //riaNode = getOuterMostRiaNode(riaNode, betaNode.getLeftTupleSource());

                    // Iterat
                    LeftTupleSource subnetworkLts = riaNode.getLeftTupleSource();
                    while ( subnetworkLts.getLeftTupleSource() != riaNode.getStartTupleSource()) {
                        subnetworkLts = subnetworkLts.getLeftTupleSource();
                    }

                    Memory rootSubNetwokrMem = wm.getNodeMemory( (MemoryFactory)  subnetworkLts);
                    SegmentMemory subNetworkSegmentMemory = rootSubNetwokrMem.getSegmentMemory();
                    if ( subNetworkSegmentMemory == null ) {
                        // we need to stop recursion here
                        createSegmentMemory( ( LeftTupleSource ) subnetworkLts, wm );
                    }

                    RiaNodeMemory riaMem = ( RiaNodeMemory ) wm.getNodeMemory( (MemoryFactory) riaNode );
                    bm.setRiaRuleMemory(riaMem.getRiaPathMemory());
                    if ( riaMem.getRiaPathMemory().getAllLinkedMaskTest() > 0 ) {
                        // only ria's with reactive subnetworks can be disabled and thus need checking
                        allLinkedTestMask = allLinkedTestMask | nodePosMask;
                    }
                } else if ( ( !(NodeTypeEnums.NotNode == tupleSource.getType() && !((NotNode)tupleSource).isEmptyBetaConstraints()) &&
                           NodeTypeEnums.AccumulateNode != tupleSource.getType()) ) {
                        // non empty not nodes and accumulates can never be disabled and thus don't need checking
                        allLinkedTestMask = allLinkedTestMask | nodePosMask;

                }
                bm.setNodePosMaskBit(nodePosMask);
                if ( NodeTypeEnums.NotNode == tupleSource.getType() ) {
                    // not nodes start up linked in
                    smem.linkNodeWithoutRuleNotify(bm.getNodePosMaskBit());
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
            }  else if ( tupleSource.getType() == NodeTypeEnums.ConditionalBranchNode ) {
                ConditionalBranchMemory evalMemory = ( ConditionalBranchMemory ) smem.createNodeMemory( ( ConditionalBranchNode ) tupleSource, wm );
                evalMemory.setSegmentMemory( smem );
            } else if ( tupleSource.getType() == NodeTypeEnums.FromNode ) {
                FromMemory fromMemory = ( FromMemory ) smem.createNodeMemory( ( FromNode ) tupleSource, wm );
                fromMemory.getBetaMemory().setSegmentMemory( smem );
            } else if ( tupleSource.getType() == NodeTypeEnums.QueryElementNode ) {
                // Initialize the QueryElementNode and have it's memory reference the actual query SegmentMemory
                QueryElementNode queryNode = ( QueryElementNode ) tupleSource;
                LeftInputAdapterNode liaNode = getQueryLiaNode(queryNode.getQueryElement().getQueryName(), getQueryOtn(segmentRoot));
                LiaNodeMemory liam = ( LiaNodeMemory ) wm.getNodeMemory( (MemoryFactory) liaNode );                
                SegmentMemory querySmem = liam.getSegmentMemory();
                if (  querySmem == null ) {
                    querySmem = createSegmentMemory( liaNode, wm );
                }                               
                QueryElementNodeMemory queryNodeMem = ( QueryElementNodeMemory ) smem.createNodeMemory( queryNode, wm );
                queryNodeMem.setQuerySegmentMemory( querySmem );
                queryNodeMem.setSegmentMemory( smem );                
            }

            if ( tupleSource.getSinkPropagator().size() == 1 ) {
                LeftTupleSinkNode sink = (LeftTupleSinkNode) tupleSource.getSinkPropagator().getFirstLeftTupleSink() ;
                if ( NodeTypeEnums.isLeftTupleSource( sink ) ) {
                    tupleSource = ( LeftTupleSource ) sink;
                } else {
                    // rtn or rian
                    // While not technically in a segment, we want to be able to iterate easily from the last node memory to the ria/rtn memory
                    // we don't use createNodeMemory, as these may already have been created by, but not added, by the method updateRiaAndTerminalMemory
                    if ( sink.getType() == NodeTypeEnums.RightInputAdaterNode) {
                        RiaNodeMemory memory = ( RiaNodeMemory) wm.getNodeMemory( (MemoryFactory) sink );
                        smem.getNodeMemories().add( memory.getRiaPathMemory() );
                        memory.getRiaPathMemory().setSegmentMemory( smem );
                    } else if ( NodeTypeEnums.isTerminalNode( sink) ) {
                        PathMemory rmem = (PathMemory) wm.getNodeMemory( (MemoryFactory) sink );
                        smem.getNodeMemories().add( rmem );
                        rmem.setSegmentMemory( smem );
                    }
                    smem.setTipNode( sink );
                    break;
                }
            } else {
                // not in same segment
                smem.setTipNode( tupleSource );
                break;
            }
//            LeftTupleSinkPropagator sink = tupleSource.getSinkPropagator();
//            LeftTupleSinkNode firstSink = (LeftTupleSinkNode) sink.getFirstLeftTupleSink() ;
//            LeftTupleSinkNode secondSink = firstSink.getNextLeftTupleSinkNode();
//            if ( secondSink == null ) {
//                if ( NodeTypeEnums.isLeftTupleSource( firstSink ) ) {
//                    tupleSource = ( LeftTupleSource ) firstSink;
//                } else {
//                    // rtn or rian
//                    // While not technically in a segment, we want to be able to iterate easily from the last node memory to the ria/rtn memory
//                    // we don't use createNodeMemory, as these may already have been created by, but not added, by the method updateRiaAndTerminalMemory
//                    if ( firstSink.getType() == NodeTypeEnums.RightInputAdaterNode) {
//                        RiaNodeMemory memory = ( RiaNodeMemory) wm.getNodeMemory( (MemoryFactory) firstSink );
//                        smem.getNodeMemories().add( memory.getRiaPathMemory() );
//                        memory.getRiaPathMemory().setSegmentMemory( smem );
//                    } else if ( NodeTypeEnums.isTerminalNode( firstSink) ) {
//                        PathMemory rmem = (PathMemory) wm.getNodeMemory( (MemoryFactory) firstSink );
//                        smem.getNodeMemories().add( rmem );
//                        rmem.setSegmentMemory( smem );
//                    }
//                    smem.setTipNode( firstSink );
//                    break;
//                }
//            } else if ( sink.size() == 2 &&
//                           NodeTypeEnums.isBetaNode( secondSink ) &&
//                               ((BetaNode)secondSink).isRightInputIsRiaNode() ) {
//                // must be a subnetwork split, always take the non riaNode path
//                tupleSource = ( LeftTupleSource )secondSink;
//            } else {
//                // not in same segment
//                smem.setTipNode( tupleSource );
//                break;
//            }
//
    	}		
    	smem.setAllLinkedMaskTest( allLinkedTestMask );    	
    	
    	// iterate to find root and determine the SegmentNodes position in the RuleSegment
        LeftTupleSource pathRoot = segmentRoot;
        int ruleSegmentPosMask = 1;
        int counter = 0;
        while ( pathRoot.getType() != NodeTypeEnums.LeftInputAdapterNode ) {
               if ( !SegmentUtilities.parentInSameSegment( pathRoot ) ) {
                   // for each new found segment, increase the mask bit position
                   ruleSegmentPosMask = ruleSegmentPosMask << 1;
                   counter++;
               }
            pathRoot = pathRoot.getLeftTupleSource();
        }
        smem.setSegmentPosMaskBit( ruleSegmentPosMask );
        smem.setPos( counter );
    	
    	updateRiaAndTerminalMemory( tupleSource, tupleSource, smem, wm );
    	return smem;
    }

    public static void createChildSegments(final InternalWorkingMemory wm,
                                            SegmentMemory smem,
                                            LeftTupleSinkPropagator sinkProp) {
        for ( LeftTupleSinkNode sink = ( LeftTupleSinkNode ) sinkProp.getFirstLeftTupleSink(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            Memory memory = wm.getNodeMemory( (MemoryFactory ) sink );

             if ( !( NodeTypeEnums.isTerminalNode( sink  ) || sink.getType() == NodeTypeEnums.RightInputAdaterNode ) ) {
                if ( memory.getSegmentMemory() == null ) {
                    SegmentUtilities.createSegmentMemory( (LeftTupleSource ) sink, wm );
                }
            } else {
                // RTNS and RiaNode's have their own segment, if they are the child of a split.
                if ( memory.getSegmentMemory() == null ) {
                    SegmentMemory childSmem = new SegmentMemory(sink);
                    PathMemory rmem;
                    if ( NodeTypeEnums.isTerminalNode( sink  ) ) {
                        rmem = (PathMemory) memory;
                    } else {
                        rmem =  ((RiaNodeMemory) memory ).getRiaPathMemory();
                    }
                    rmem.getSegmentMemories()[ rmem.getSegmentMemories().length -1 ] = childSmem;
                    rmem.setSegmentMemory( childSmem );
                    childSmem.getPathMemories().add( rmem );

                    childSmem.setTipNode( sink );
                    childSmem.setSinkFactory( sink );
                }
            }
            smem.add( memory.getSegmentMemory() );
        }
    }

    /**
     * Is the LeftTupleSource a node in the sub network for the RightInputAdapterNode
     * To be in the same network, it must be a node is after the two output of the parent
     * and before the rianode.
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

    /**
     * This adds the segment memory to the terminal node or ria node's list of memories.
     * In the case of the terminal node this allows it to know that all segments from
     * the tip to root are linked.
     * In the case of the ria node its all the segments up to the start of the subnetwork.
     * This is because the rianode only cares if all of it's segments are linked, then
     * it sets the bit of node it is the right input for.
     * @param lt
     * @param originalLt
     * @param smem
     * @param wm
     */
    public static void updateRiaAndTerminalMemory(LeftTupleSource lt,
                                                  LeftTupleSource originalLt,
                                                  SegmentMemory smem,
                                                  InternalWorkingMemory wm) {
        for ( LeftTupleSink sink : lt.getSinkPropagator().getSinks() ) {
    	    if (NodeTypeEnums.isLeftTupleSource( sink ) ) {
                if ( NodeTypeEnums.NotNode == sink.getType() && ((NotNode)sink).isEmptyBetaConstraints() ) {
    	            BetaMemory bm = ( BetaMemory) wm.getNodeMemory( (MemoryFactory) sink );
    	             if ( bm.getSegmentMemory() == null ) {
    	                 // Not nodes must be initialised
    	                 createSegmentMemory( (NotNode) sink, wm );
    	             }
    	        }
    	        updateRiaAndTerminalMemory(( LeftTupleSource ) sink, originalLt, smem, wm);
    	    } else if ( sink.getType() == NodeTypeEnums.RightInputAdaterNode) {
    	        // Only add the RIANode, if the LeftTupleSource is part of the RIANode subnetwork.
    	        if ( inSubNetwork( (RightInputAdapterNode)sink, originalLt ) ) {
    	            RiaNodeMemory riaMem = ( RiaNodeMemory) wm.getNodeMemory( (MemoryFactory) sink );
    	            PathMemory rmem = (PathMemory) riaMem.getRiaPathMemory();
                    smem.getPathMemories().add( rmem );
                    rmem.getSegmentMemories()[smem.getPos()] = smem;
    	        }
    	    } else if ( NodeTypeEnums.isTerminalNode( sink) ) {    	        
    	        PathMemory rmem = (PathMemory) wm.getNodeMemory( (MemoryFactory) sink );
                smem.getPathMemories().add( rmem );
                rmem.getSegmentMemories()[smem.getPos()] = smem;
                if ( smem.isSegmentLinked() ) {
                    // not's can cause segments to be linked, and the rules need to be notified for evaluation
                    smem.notifyRuleLinkSegment(wm);
                }
    	    }
        }
    }

    public static void initialiseRtnMemory(LeftTupleSource lt,
                                           InternalWorkingMemory wm) {
        for ( LeftTupleSink sink : lt.getSinkPropagator().getSinks() ) {
            if (NodeTypeEnums.isLeftTupleSource( sink ) ) {
                initialiseRtnMemory((LeftTupleSource) sink, wm);
            } else  if ( NodeTypeEnums.isTerminalNode( sink) ) {
                // getting will cause an initialization of rtn, which will recursively initialise rians too.
                PathMemory rmem = (PathMemory) wm.getNodeMemory( (MemoryFactory) sink );
            }
        }
    }

    public static boolean parentInSameSegment(LeftTupleSource lt) {
        return lt.getLeftTupleSource().getSinkPropagator().size() == 1;
        // comments out for now, as the optimization to preserve subnetwork segments down one side is troublesome.
//        LeftTupleSource parent = lt.getLeftTupleSource();
//        if ( parent != null && ( parent.getSinkPropagator().size() == 1 ||
//               // same segment, if it's a subnetwork split and we are on the non subnetwork side of the split
//             ( parent.getSinkPropagator().size() == 2 &&
//               NodeTypeEnums.isBetaNode( lt ) &&
//               ((BetaNode)lt).isRightInputIsRiaNode() ) ) ) {
//            return true;
//        } else {
//            return false;
//        }
    }
    
    public static ObjectTypeNode getQueryOtn(LeftTupleSource lts) {
        while ( !(lts instanceof LeftInputAdapterNode ) ) {
            lts = lts.getLeftTupleSource();
        }
        
        LeftInputAdapterNode liaNode = ( LeftInputAdapterNode ) lts;
        ObjectSource os = liaNode.getObjectSource();
        while ( !(os instanceof EntryPointNode) ) {
            os = os.getParentObjectSource();
        }
        
        return ((EntryPointNode)os).getQueryNode();
    }
    
    public static LeftInputAdapterNode getQueryLiaNode(String queryName, ObjectTypeNode queryOtn) {
        if ( queryOtn.getSinkPropagator() instanceof CompositeObjectSinkAdapter ) {
            CompositeObjectSinkAdapter sink = ( CompositeObjectSinkAdapter ) queryOtn.getSinkPropagator();
            if ( sink.getHashableSinks() != null ) {
                for ( AlphaNode alphaNode = ( AlphaNode ) sink.getHashableSinks().getFirst(); alphaNode != null; alphaNode = ( AlphaNode ) alphaNode.getNextObjectSinkNode() ) {
                    QueryNameConstraint nameConstraint = ( QueryNameConstraint ) alphaNode.getConstraint();
                    if ( queryName.equals( nameConstraint.getQueryName() ) ) {
                        return ( LeftInputAdapterNode ) alphaNode.getSinkPropagator().getSinks()[0];
                    }
                }
            }
            
            Iterator it = sink.getHashedSinkMap().iterator();
            for ( ObjectEntry entry = ( ObjectEntry ) it.next(); entry != null; entry = ( ObjectEntry ) it.next() ) {
                AlphaNode alphaNode = ( AlphaNode ) entry.getValue();                
                QueryNameConstraint nameConstraint = ( QueryNameConstraint ) alphaNode.getConstraint();
                if ( queryName.equals( nameConstraint.getQueryName() ) ) {
                    return ( LeftInputAdapterNode ) alphaNode.getSinkPropagator().getSinks()[0];
                }                
            }
        } else {
            AlphaNode alphaNode = ( AlphaNode ) queryOtn.getSinkPropagator().getSinks()[0];
            QueryNameConstraint nameConstraint = ( QueryNameConstraint ) alphaNode.getConstraint();
            if ( queryName.equals( nameConstraint.getQueryName() ) ) {
                return ( LeftInputAdapterNode ) alphaNode.getSinkPropagator().getSinks()[0];
            }
            return ( LeftInputAdapterNode ) queryOtn.getSinkPropagator().getSinks()[0];
        }
        
        throw new RuntimeException( "Unable to find query '" + queryName + "'" );        
    }

}
