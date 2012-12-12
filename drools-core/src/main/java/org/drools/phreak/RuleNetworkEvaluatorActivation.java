package org.drools.phreak;

import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.base.DroolsQuery;
import org.drools.common.AgendaItem;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.common.StagedLeftTuples;
import org.drools.common.StagedRightTuples;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;
import org.drools.core.util.index.LeftTupleList;
import org.drools.core.util.index.RightTupleList;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.ExistsNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.reteoo.RiaRuleMemory;
import org.drools.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleMemory;
import org.drools.reteoo.RuleMemory;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.SegmentMemory;
import org.drools.reteoo.Sink;
import org.drools.rule.ContextEntry;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

public class RuleNetworkEvaluatorActivation extends AgendaItem {

    private RuleMemory             rmem;

    private PhreakLianNode         pLiaNode  = new PhreakLianNode();
    private PhreakJoinNode         pJoinNode = new PhreakJoinNode();
    private PhreakNotNode          pNotNode  = new PhreakNotNode();
    private PhreakExistsNode       pExistsNode  = new PhreakExistsNode();
    private PhreakRuleTerminalNode pRtnNode  = new PhreakRuleTerminalNode();

    public RuleNetworkEvaluatorActivation() {

    }

    /**
     * Construct.
     *
     * @param tuple
     *            The tuple.
     * @param rule
     *            The rule.
     */
    public RuleNetworkEvaluatorActivation(final long activationNumber,
                                          final LeftTuple tuple,
                                          final int salience,
                                          final PropagationContext context,
                                          final RuleMemory rmem,
                                          final RuleTerminalNode rtn) {
        super( activationNumber, tuple, salience, context, rtn );
        this.rmem = rmem;
    }

    //    public int flushModifies(InternalWorkingMemory wm) {
    //        for ( SegmentMemory smem : rmem.getSegmentMemories() ) {
    //            RightTupleList list = smem.getStagedModifyRightTuple();
    //            if ( list.size() > 0 ) {
    //                BetaNode.flushModifyStagedRightTuples( list, wm );
    //            }
    //        }
    //        
    //        return 0;
    //    }

    //    public int evaluateNetwork__old(LeftTupleSource startNode,
    //                               LeftTupleSource lt,
    //                               InternalWorkingMemory wm) {
    //        int stagedTuplecount = 0;
    //
    //        while ( lt != null ) {
    //            if ( startNode == lt ) {
    //                // sub network reached it's start
    //                return stagedTuplecount;
    //            }
    //            if ( NodeTypeEnums.isBetaNode( lt ) ) {
    //                BetaNode betaNode = (BetaNode) lt;
    //                if ( betaNode.isRightInputIsRiaNode() ) {
    //                    RightInputAdapterNode riaNode = (RightInputAdapterNode) betaNode.getRightInput();
    //                    lt = lt.getLeftTupleSource();
    //                    stagedTuplecount = evaluateNetwork( lt, riaNode.getLeftTupleSource(), wm );
    //                    // skip this node, because rianodes always propagate.                    
    //                    continue;
    //                }
    //
    //                BetaMemory bm;
    //                if ( NodeTypeEnums.AccumulateNode == lt.getType() ) {
    //                    bm = ((AccumulateMemory) wm.getNodeMemory( (AccumulateNode) lt )).getBetaMemory();
    //                } else {
    //                    bm = (BetaMemory) wm.getNodeMemory( (BetaNode) lt );
    //                }
    //                RightTupleList list = bm.getStagedAssertRightTupleList();
    //                int length = (list.size() < 25) ? list.size() : 25;
    //
    //                RightTuple rightTuple = BetaNode.propagateAssertRightTuples( betaNode, list, length, wm );
    //
    //                if ( length == 25 && rightTuple.getNext() != null ) {
    //                    stagedTuplecount = stagedTuplecount + list.size() - 25;
    //                    list.split( rightTuple, length );
    //                } else {
    //                    list.clear();
    //                }
    //            } else if ( NodeTypeEnums.LeftInputAdapterNode == lt.getType() ) {
    ////                LiaNodeMemory lm = (LiaNodeMemory) wm.getNodeMemory( (LeftInputAdapterNode) lt );
    ////                LeftTupleList list = lm.getSegmentMemory().getStagedAssertLeftTuple();
    ////                int length = (list.size() < 25) ? list.size() : 25;
    ////
    ////                LeftTuple leftTuple = LeftInputAdapterNode.propagateLeftTuples( (LeftInputAdapterNode) lt, list, length, wm );
    ////
    ////                if ( length == 25 && leftTuple.getNext() != null ) {
    ////                    stagedTuplecount = stagedTuplecount + list.size() - 25;
    ////                    list.split( leftTuple, length );
    ////                } else {
    ////                    list.clear();
    ////                }
    //            }
    //            lt = lt.getLeftTupleSource();
    //        }
    //        return stagedTuplecount;
    //    }

    public int evaluateNetwork(InternalWorkingMemory wm) {
        SegmentMemory[] smems = rmem.getSegmentMemories();

        int smemIndex = 0;
        SegmentMemory smem = smems[smemIndex]; // 0
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) smem.getRootNode();

        LinkedList<Memory> nodeMemories = smem.getNodeMemories();

        LiaNodeMemory liaNodeMemory = (LiaNodeMemory) nodeMemories.getFirst();

        StagedLeftTuples trgTuples = new StagedLeftTuples();
        StagedLeftTuples srcTuples = smem.getStagedLeftTuples();
        pLiaNode.doNode( liaNode, liaNodeMemory, wm, srcTuples, trgTuples );

        LeftTupleSource node = null;
        Memory nodeMem = null;
        if ( liaNode == smem.getTipNode() ) {
            // Segment only contains LiaNode, so need to propagate peers
            SegmentPropagator.propagate( smem, trgTuples, wm );
            smem = smems[smemIndex]; // 1
            nodeMem = smem.getNodeMemories().getFirst();
            node = smem.getRootNode();
        } else {
            node = (LeftTupleSource) liaNode.getSinkPropagator().getFirstLeftTupleSink(); // we know it can only have one child
            nodeMem = liaNodeMemory.getNext();
        }

        eval( node, nodeMem, smems, smemIndex, trgTuples, null, wm);

        return 0;
    }
    
    public StagedLeftTuples eval( LeftTupleSource node, Memory nodeMem, SegmentMemory[] smems, int smemIndex, StagedLeftTuples trgTuples, StagedLeftTuples stagedLeftTuples, InternalWorkingMemory wm) {
        StagedLeftTuples srcTuples = null;
        
        boolean foundSegmentTip = false;
        
        SegmentMemory smem = smems[smemIndex];
        while ( true ) {
            srcTuples = trgTuples; // previous target, is now the source
            
            if ( NodeTypeEnums.isTerminalNode( node ) ) {
                RuleTerminalNode rtn = rmem.getRuleTerminalNode();
                pRtnNode.doNode( rtn, wm, srcTuples );
                break; // returns null;
            } else if ( NodeTypeEnums.RightInputAdaterNode == node.getType() ) {
                return trgTuples;
            } else if ( nodeMem == null ) {
                // Reached end of segment, start on new segment.
                SegmentPropagator.propagate( smem, trgTuples, wm );
                smem = smems[smemIndex++];
                node = smem.getRootNode(); 
                nodeMem = smem.getNodeMemories().getFirst();
            }
          
            
            if ( node == smem.getTipNode() && smem.getFirst() != null) {
                // we are about to process the segment tip, so allow it to merge insert/update/delete clashes
                // can be null if next sink is RTN, or RiaNode
                stagedLeftTuples = smem.getFirst().getStagedLeftTuples();
            } else {
                stagedLeftTuples = null;
            }          

            trgTuples = new StagedLeftTuples();
            
            if ( NodeTypeEnums.isBetaNode( node ) ) {
                BetaNode betaNode = ( BetaNode )node;
                BetaMemory bm = (BetaMemory) nodeMem;
                if ( betaNode.isRightInputIsRiaNode() ) {
                    StagedLeftTuples peerTuples = new StagedLeftTuples();
                    SegmentPropagator.processPeers( srcTuples, peerTuples, betaNode );
                    
                    // Make sure subnetwork Segment has tuples to process
                    SegmentMemory subSmem = bm.getSubnetworkSegmentMemory();
                    StagedLeftTuples subnetworkStaged =  subSmem.getStagedLeftTuples();
                    subnetworkStaged.addAllDeletes( srcTuples.getDeleteFirst() );
                    subnetworkStaged.addAllUpdates( srcTuples.getUpdateFirst() );
                    subnetworkStaged.addAllInserts( srcTuples.getInsertFirst() );
                   
                    List<RuleMemory> ruleMemories = subSmem.getRuleMemories();
                    
                    RiaRuleMemory rm = null;
                    if ( ruleMemories.size() == 1 ) {
                        rm = ( RiaRuleMemory) ruleMemories.get( 0 ) ;
                    } else {
                        for ( int i = 0, size = ruleMemories.size(); i < size; i++ ) {
                            if ( ruleMemories.get( i ) instanceof RiaRuleMemory ) {
                                rm = ( RiaRuleMemory) ruleMemories.get( i ) ;
                            }
                        }
                    }
                    
                    RightInputAdapterNode riaNode = ( RightInputAdapterNode ) betaNode.getRightInput();
                    // At this point we have the tuples to apply to the left and to the right of the subnetwork root node
                    StagedLeftTuples riaStagedTuples = eval( subSmem.getRootNode(), subSmem.getNodeMemories().getFirst(), rm.getSegmentMemories(), 0, srcTuples, null, wm );
                    StagedRightTuples riaStageRight = bm.getStagedRightTuples();
                    for ( LeftTuple leftTuple = riaStagedTuples.getInsertFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
                        InternalFactHandle handle = riaNode.createFactHandle( leftTuple, leftTuple.getPropagationContext(), wm );
                        RightTuple rightTuple = betaNode.createRightTuple( handle, betaNode, leftTuple.getPropagationContext() );
                        //riaStagedTuples.
                    }
                    
                }
                
                switch( node.getType() ) {
                    case  NodeTypeEnums.JoinNode:                    
                        pJoinNode.doNode( (JoinNode) node, node.getSinkPropagator().getFirstLeftTupleSink(), bm, wm, srcTuples, trgTuples, stagedLeftTuples );
                        break;
                    case  NodeTypeEnums.NotNode:
                        pNotNode.doNode( (NotNode) node, node.getSinkPropagator().getFirstLeftTupleSink(), bm, wm, srcTuples, trgTuples, stagedLeftTuples );
                        break;
                    case  NodeTypeEnums.ExistsNode:
                        pExistsNode.doNode( (ExistsNode) node, node.getSinkPropagator().getFirstLeftTupleSink(), bm, wm, srcTuples, trgTuples, stagedLeftTuples );
                        break;                        
                }
            }

//            LeftTupleSink sink = node.getSinkPropagator().getFirstLeftTupleSink();
//
//            
//            if ( node == smem.getTipNode() ) {
//                smem.getSegmentPropagator().propagate( trgTuples, wm ); // merge children segments
//                smem = smems[smemIndex++];
//                node = smem.getRootNode();
//                continue;
//            }
            
            if ( node.getSinkPropagator().size() > 1 ) {
              // must be a subnetwork split, so create new staging area to handle left and right merging.
              stagedLeftTuples = new StagedLeftTuples();
              LeftTupleSource subNetworkNode = (LeftTupleSource) node.getSinkPropagator().getFirstLeftTupleSink();;
              //eval( subNetworkNode, nodeMemory, )
              
            } 
            
            node = (LeftTupleSource) node.getSinkPropagator().getFirstLeftTupleSink();
//            if ( node == smem.getTipNode() ) {
//                // we are about to process the segment tip, so allow it to merge insert/update/delete clashes
//                if ( smem.getFirst() != null ) { // can be null if sink is RTN
//                    stagedLeftTuples = smem.getFirst().getStagedLeftTuples();
//                }
//                foundSegmentTip = true;
//            } else if ( node.getSinkPropagator().size() > 1 ) {
//                // must be a subnetwork split, so create new staging area to handle left and right merging.
//                stagedLeftTuples = new StagedLeftTuples();
//            } else {
//                //eval(node.getSinkPropagator().getFirstLeftTupleSink());
//                stagedLeftTuples = null;
//            }
            nodeMem = nodeMem.getNext();
        }        
        return null;
    }

    public int evaluateSegment(SegmentMemory smem,
                               InternalWorkingMemory wm) {

        return 0;
    }

    /**
     * Helper class used for testing purposes
     * @param wm
     */
    public static void evaluateLazyItems(WorkingMemory wm) {
        InternalWorkingMemory iwm = (InternalWorkingMemory) wm;
        Map<Rule, BaseNode[]> map = ((InternalRuleBase) iwm.getRuleBase()).getReteooBuilder().getTerminalNodes();
        for ( BaseNode[] nodes : map.values() ) {
            for ( BaseNode node : nodes ) {
                RuleTerminalNode rtn = (RuleTerminalNode) node;
                RuleMemory rs = (RuleMemory) iwm.getNodeMemory( rtn );
                RuleNetworkEvaluatorActivation item = rs.getAgendaItem();
                if ( item != null ) {
                    item.dequeue();
                    int count = item.evaluateNetwork(iwm);
                    if ( count > 0 ) {
                        ((InternalAgenda) iwm.getAgenda()).addActivation( item );
                    }
                }
            }
        }
    }

    public boolean isRuleNetworkEvaluatorActivation() {
        return true;
    }

    public static class PhreakLianNode {
        public void doNode(LeftInputAdapterNode liaNode,
                           LiaNodeMemory lm,
                           InternalWorkingMemory wm,
                           StagedLeftTuples srcLeftTuples,
                           StagedLeftTuples trgLeftTuples) {
            int size = srcLeftTuples.insertSize();
            //            if ( size >= 24 ) {
            //                LeftTuple leftTuple = srcLeftTuples.getInsertFirst();
            //                for ( int i = 0; i < 24; i++ ) {
            //                    leftTuple = leftTuple.getStagedNext();
            //                }
            //                srcLeftTuples.splitInsert( leftTuple, 25 );
            //                trgLeftTuples.setInsert( leftTuple.getStagedPrevious(), 25 );
            //            } else {
            trgLeftTuples.setInsert( srcLeftTuples.getInsertFirst(), srcLeftTuples.insertSize() );
            srcLeftTuples.setInsert( null, 0 );
            //            }

            trgLeftTuples.setDelete( srcLeftTuples.getDeleteFirst() );
            trgLeftTuples.setUpdate( srcLeftTuples.getUpdateFirst() );

            srcLeftTuples.setDelete( null );
            srcLeftTuples.setUpdate( null );
        }
    }

    public static class PhreakJoinNode {
        public void doNode(JoinNode joinNode,
                           LeftTupleSink sink,
                           BetaMemory bm,
                           InternalWorkingMemory wm,
                           StagedLeftTuples srcLeftTuples,
                           StagedLeftTuples trgLeftTuples,
                           StagedLeftTuples stagedLeftTuples) {
            
            StagedRightTuples srcRightTuples = bm.getStagedRightTuples();

            if ( srcRightTuples.getDeleteFirst() != null ) {
                doRightDeletes( joinNode, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcLeftTuples.getDeleteFirst() != null ) {
                doLeftDeletes( joinNode, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples  );
            }

            if ( srcLeftTuples.getUpdateFirst() != null || srcRightTuples.getUpdateFirst() != null ) {
                dpUpdatesReorderMemory( bm,
                                        wm,
                                        srcRightTuples,
                                        srcLeftTuples,
                                        trgLeftTuples );
            }

            if ( srcRightTuples.getUpdateFirst() != null ) {
                doRightUpdates( joinNode, sink, bm, wm, srcRightTuples, srcLeftTuples, trgLeftTuples, stagedLeftTuples   );
            }

            if ( srcLeftTuples.getUpdateFirst() != null ) {
                doLeftUpdates( joinNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples   );
            }

            if ( srcRightTuples.getInsertFirst() != null ) {
                doRightInserts( joinNode, sink, bm, wm, srcRightTuples, srcLeftTuples, trgLeftTuples  );
            }

            if ( srcLeftTuples.getInsertFirst() != null ) {
                doLeftInserts( joinNode, sink, bm, wm, srcLeftTuples, trgLeftTuples );
            }
            
            srcRightTuples.setInsert( null, 0 );
            srcRightTuples.setDelete( null );
            srcRightTuples.setUpdate( null );             
            
            srcLeftTuples.setInsert( null, 0 );
            srcLeftTuples.setDelete( null );
            srcLeftTuples.setUpdate( null );            
        }

        public void doLeftInserts(JoinNode joinNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;

            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = joinNode.getRawConstraints();
            FastIterator it = joinNode.getRightIterator( rtm );

            for ( LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                PropagationContext context = leftTuple.getPropagationContext();
                boolean useLeftMemory = true;

                if ( !tupleMemoryEnabled ) {
                    // This is a hack, to not add closed DroolsQuery objects
                    Object object = leftTuple.get( 0 ).getObject();
                    if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                        useLeftMemory = false;
                    }
                }

                if ( useLeftMemory ) {
                    ltm.add( leftTuple );
                }

                constraints.updateFromTuple( contextEntry,
                                             wm,
                                             leftTuple );

                for ( RightTuple rightTuple = joinNode.getFirstRightTuple( leftTuple,
                                                                           rtm,
                                                                           context,
                                                                           it ); rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                    if ( constraints.isAllowedCachedLeft( contextEntry,
                                                          rightTuple.getFactHandle() ) ) {
                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                       rightTuple,
                                                                       null,
                                                                       null,
                                                                       sink,
                                                                       tupleMemory ) );
                    }
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            srcLeftTuples.setInsert( null, 0 );
            constraints.resetTuple( contextEntry );
        }

        public void doRightInserts(JoinNode joinNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples srcLeftTuples,
                                   StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;

            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = joinNode.getRawConstraints();
            FastIterator it = joinNode.getLeftIterator( ltm );

            for ( RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                rtm.add( rightTuple );
                PropagationContext context = rightTuple.getPropagationContext();

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );

                for ( LeftTuple leftTuple = joinNode.getFirstLeftTuple( rightTuple, ltm, context, it ); leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
                    trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                   rightTuple,
                                                                   null,
                                                                   null,
                                                                   sink,
                                                                   tupleMemory ) );
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
            srcRightTuples.setInsert( null, 0 );
            constraints.resetFactHandle( contextEntry );
        }

        public void doLeftUpdates(JoinNode joinNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples,
                                  StagedLeftTuples stagedLeftTuples) {
            boolean tupleMemory = true;
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = joinNode.getRawConstraints();
            FastIterator it = joinNode.getRightIterator( rtm );

            for ( LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                PropagationContext context = leftTuple.getPropagationContext();

                constraints.updateFromTuple( contextEntry,
                                             wm,
                                             leftTuple );

                RightTuple rightTuple = joinNode.getFirstRightTuple( leftTuple,
                                                                     rtm,
                                                                     context,
                                                                     it );

                LeftTuple childLeftTuple = leftTuple.getFirstChild();

                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
                // if rightTuple is null, we assume there was a bucket change and that bucket is empty        
                if ( childLeftTuple != null && rtm.isIndexed() && !it.isFullIterator() && (rightTuple == null || (rightTuple.getMemory() != childLeftTuple.getRightParent().getMemory())) ) {
                    // our index has changed, so delete all the previous propagations
                    while ( childLeftTuple != null ) {
                        childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
                    }
                    // childLeftTuple is now null, so the next check will attempt matches for new bucket
                }

                // we can't do anything if RightTupleMemory is empty
                if ( rightTuple != null ) {
                    doLeftUpdatesProcessChildren( childLeftTuple, leftTuple, rightTuple, stagedLeftTuples, tupleMemory, contextEntry, constraints, sink, it, trgLeftTuples );
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            srcLeftTuples.setUpdate( null );
            constraints.resetTuple( contextEntry );
        }

        public LeftTuple doLeftUpdatesProcessChildren(LeftTuple childLeftTuple,
                                                      LeftTuple leftTuple,
                                                      RightTuple rightTuple,
                                                      StagedLeftTuples stagedLeftTuples,
                                                      boolean tupleMemory,
                                                      ContextEntry[] contextEntry,
                                                      BetaConstraints constraints,
                                                      LeftTupleSink sink,
                                                      FastIterator it,
                                                      StagedLeftTuples trgLeftTuples) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                    if ( constraints.isAllowedCachedLeft( contextEntry,
                                                          rightTuple.getFactHandle() ) ) {
                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                       rightTuple,
                                                                       null,
                                                                       null,
                                                                       sink,
                                                                       tupleMemory ) );
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                    if ( constraints.isAllowedCachedLeft( contextEntry,
                                                          rightTuple.getFactHandle() ) ) {
                        // insert, childLeftTuple is not updated
                        if ( childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple ) {
                            trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                           rightTuple,
                                                                           null,
                                                                           null,
                                                                           sink,
                                                                           tupleMemory ) );
                        } else {
                            switch ( childLeftTuple.getStagedType() ) {
                            // handle clash with already staged entries
                                case LeftTuple.INSERT :
                                    stagedLeftTuples.removeInsert( childLeftTuple );
                                    break;
                                case LeftTuple.UPDATE :
                                    stagedLeftTuples.removeUpdate( childLeftTuple );
                                    break;
                            }

                            // update, childLeftTuple is updated
                            trgLeftTuples.addUpdate( childLeftTuple );

                            childLeftTuple.reAddRight();
                            childLeftTuple = childLeftTuple.getLeftParentNext();
                        }
                    } else if ( childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple ) {
                        // delete, childLeftTuple is updated
                        childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
                    }
                }
            }

            return childLeftTuple;
        }

        public void doRightUpdates(JoinNode joinNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples srcLeftTuples,
                                   StagedLeftTuples trgLeftTuples,
                                   StagedLeftTuples stagedLeftTuples) {
            boolean tupleMemory = true;
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = joinNode.getRawConstraints();
            FastIterator it = joinNode.getLeftIterator( ltm );

            for ( RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                PropagationContext context = rightTuple.getPropagationContext();

                LeftTuple childLeftTuple = rightTuple.getFirstChild();

                LeftTuple leftTuple = joinNode.getFirstLeftTuple( rightTuple, ltm, context, it );

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );

                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
                // We assume a bucket change if leftTuple == null        
                if ( childLeftTuple != null && ltm.isIndexed() && !it.isFullIterator() && (leftTuple == null || (leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory())) ) {
                    // our index has changed, so delete all the previous propagations
                    while ( childLeftTuple != null ) {
                        childLeftTuple = deleteRightChild( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    }
                    // childLeftTuple is now null, so the next check will attempt matches for new bucket                    
                }

                // we can't do anything if LeftTupleMemory is empty
                if ( leftTuple != null ) {
                    doRightUpdatesProcessChildren( childLeftTuple, leftTuple, rightTuple, stagedLeftTuples, tupleMemory, contextEntry, constraints, sink, it, trgLeftTuples );
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
            srcRightTuples.setUpdate( null );
            constraints.resetFactHandle( contextEntry );
        }

        public LeftTuple doRightUpdatesProcessChildren(LeftTuple childLeftTuple,
                                                       LeftTuple leftTuple,
                                                       RightTuple rightTuple,
                                                       StagedLeftTuples stagedLeftTuples,
                                                       boolean tupleMemory,
                                                       ContextEntry[] contextEntry,
                                                       BetaConstraints constraints,
                                                       LeftTupleSink sink,
                                                       FastIterator it,
                                                       StagedLeftTuples trgLeftTuples) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                       rightTuple,
                                                                       null,
                                                                       null,
                                                                       sink,
                                                                       tupleMemory ) );
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        // insert, childLeftTuple is not updated
                        if ( childLeftTuple == null || childLeftTuple.getLeftParent() != leftTuple ) {
                            trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                           rightTuple,
                                                                           null,
                                                                           null,
                                                                           sink,
                                                                           tupleMemory ) );
                        } else {
                            switch ( childLeftTuple.getStagedType() ) {
                            // handle clash with already staged entries
                                case LeftTuple.INSERT :
                                    stagedLeftTuples.removeInsert( childLeftTuple );
                                    break;
                                case LeftTuple.UPDATE :
                                    stagedLeftTuples.removeUpdate( childLeftTuple );
                                    break;
                            }

                            // update, childLeftTuple is updated
                            trgLeftTuples.addUpdate( childLeftTuple );

                            childLeftTuple.reAddLeft();
                            childLeftTuple = childLeftTuple.getRightParentNext();
                        }
                    } else if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {
                        // delete, childLeftTuple is updated
                        childLeftTuple = deleteRightChild( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    }
                }
            }

            return childLeftTuple;
        }

        public void doLeftDeletes(JoinNode joinNode,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples,
                                  StagedLeftTuples stagedLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();

            for ( LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                ltm.remove( leftTuple );

                if ( leftTuple.getFirstChild() != null ) {
                    LeftTuple childLeftTuple = leftTuple.getFirstChild();

                    while ( childLeftTuple != null ) {
                        childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
                    }
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            srcLeftTuples.setDelete( null );
        }

        public void doRightDeletes(JoinNode joinNode,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples trgLeftTuples,
                                   StagedLeftTuples stagedLeftTuples) {
            RightTupleMemory rtm = bm.getRightTupleMemory();

            for ( RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                rtm.remove( rightTuple );

                if ( rightTuple.getFirstChild() != null ) {
                    LeftTuple childLeftTuple = rightTuple.getFirstChild();

                    while ( childLeftTuple != null ) {
                        childLeftTuple = deleteRightChild( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    }
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
            srcRightTuples.setDelete( null );
        }
    }

    public static class PhreakNotNode {
        public void doNode(NotNode notNode,
                           LeftTupleSink sink,
                           BetaMemory bm,
                           InternalWorkingMemory wm,
                           StagedLeftTuples srcLeftTuples,
                           StagedLeftTuples trgLeftTuples,
                           StagedLeftTuples stagedLeftTuples) {
            StagedRightTuples srcRightTuples = bm.getStagedRightTuples();

            if ( srcRightTuples.getDeleteFirst() != null ) {
                doRightDeletes( notNode, sink, bm, wm, srcRightTuples, trgLeftTuples );
            }

            if ( srcLeftTuples.getDeleteFirst() != null ) {
                doLeftDeletes( notNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcLeftTuples.getUpdateFirst() != null || srcRightTuples.getUpdateFirst() != null ) {
                dpUpdatesReorderMemory( bm,
                                        wm,
                                        srcRightTuples,
                                        srcLeftTuples,
                                        trgLeftTuples );
            }

            if ( srcRightTuples.getUpdateFirst() != null ) {
                doRightUpdates( notNode, sink, bm, wm, srcRightTuples, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcLeftTuples.getUpdateFirst() != null ) {
                doLeftUpdates( notNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcRightTuples.getInsertFirst() != null ) {
                doRightInserts( notNode, sink, bm, wm, srcRightTuples, srcLeftTuples, trgLeftTuples );
            }

            if ( srcLeftTuples.getInsertFirst() != null ) {
                doLeftInserts( notNode, sink, bm, wm, srcLeftTuples, trgLeftTuples );
            }
            
            srcRightTuples.setInsert( null, 0 );
            srcRightTuples.setDelete( null );
            srcRightTuples.setUpdate( null );             
            
            srcLeftTuples.setInsert( null, 0 );
            srcLeftTuples.setDelete( null );
            srcLeftTuples.setUpdate( null );             
        }

        public void doLeftInserts(NotNode notNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;

            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = notNode.getRawConstraints();
            FastIterator it = notNode.getRightIterator( rtm );

            for ( LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                PropagationContext context = leftTuple.getPropagationContext();

                boolean useLeftMemory = true;
                if ( !tupleMemoryEnabled ) {
                    // This is a hack, to not add closed DroolsQuery objects
                    Object object = leftTuple.get( 0 ).getObject();
                    if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                        useLeftMemory = false;
                    }
                }

                constraints.updateFromTuple( contextEntry,
                                             wm,
                                             leftTuple );

                for ( RightTuple rightTuple = notNode.getFirstRightTuple( leftTuple, rtm, context, it ); rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                    if ( constraints.isAllowedCachedLeft( contextEntry,
                                                          rightTuple.getFactHandle() ) ) {
                        leftTuple.setBlocker( rightTuple );

                        if ( useLeftMemory ) {
                            rightTuple.addBlocked( leftTuple );
                        }

                        break;
                    }
                }

                if ( leftTuple.getBlocker() == null ) {
                    // tuple is not blocked, so add to memory so other fact handles can attempt to match
                    if ( useLeftMemory ) {
                        ltm.add( leftTuple );
                    }

                    trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                   sink,
                                                                   tupleMemory ) );
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            constraints.resetTuple( contextEntry );
        }

        public void doRightInserts(NotNode notNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples srcLeftTuples,
                                   StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;

            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = notNode.getRawConstraints();
            FastIterator it = notNode.getLeftIterator( ltm );

            StagedLeftTuples stagedLeftTuples = null;
            if ( !bm.getSegmentMemory().isEmpty() ) {
                stagedLeftTuples = bm.getSegmentMemory().getFirst().getStagedLeftTuples();
            }

            if ( bm.getSegmentMemory().isSegmentLinked() && !notNode.isRightInputIsRiaNode() && notNode.isEmptyBetaConstraints() ) {
                    // this must be processed here, rather than initial insert, as we need to link the blocker
                    // @TODO this could be more efficient, as it means the entire StagedLeftTuples for all previous nodes where evaluated, needlessly. 
                    bm.unlinkNode( wm );
            }
            
            for ( RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                rtm.add( rightTuple );
                PropagationContext context = rightTuple.getPropagationContext();

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );
                for ( LeftTuple leftTuple = notNode.getFirstLeftTuple( rightTuple, ltm, context, it ); leftTuple != null; ) {
                    // preserve next now, in case we remove this leftTuple 
                    LeftTuple temp = (LeftTuple) it.next( leftTuple );

                    // we know that only unblocked LeftTuples are  still in the memory
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        leftTuple.setBlocker( rightTuple );
                        rightTuple.addBlocked( leftTuple );

                        // this is now blocked so remove from memory
                        ltm.remove( leftTuple );

                        // subclasses like ForallNotNode might override this propagation
                        // ** @TODO (mdp) need to not break forall
                        deleteLeftChild( trgLeftTuples, leftTuple, stagedLeftTuples );
                    }

                    leftTuple = temp;
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
            constraints.resetFactHandle( contextEntry );
        }

        public void doLeftUpdates(NotNode notNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples,
                                  StagedLeftTuples stagedLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;

            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = notNode.getRawConstraints();
            FastIterator rightIt = notNode.getRightIterator( rtm );

            for ( LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                PropagationContext context = leftTuple.getPropagationContext();
                RightTuple firstRightTuple = notNode.getFirstRightTuple( leftTuple, rtm, context, rightIt );

                // If in memory, remove it, because we'll need to add it anyway if it's not blocked, to ensure iteration order
                RightTuple blocker = leftTuple.getBlocker();
                if ( blocker == null ) {
                    ltm.remove( leftTuple );
                } else {
                    // check if we changed bucket
                    if ( rtm.isIndexed() && !rightIt.isFullIterator() ) {
                        // if newRightTuple is null, we assume there was a bucket change and that bucket is empty                
                        if ( firstRightTuple == null || firstRightTuple.getMemory() != blocker.getMemory() ) {
                            removeBlocker( leftTuple, blocker );
                            blocker = null;
                        }
                    }
                }

                constraints.updateFromTuple( contextEntry,
                                             wm,
                                             leftTuple );

                // if we where not blocked before (or changed buckets), or the previous blocker no longer blocks, then find the next blocker
                if ( blocker == null || !constraints.isAllowedCachedLeft( contextEntry,
                                                                          blocker.getFactHandle() ) ) {
                    if ( blocker != null ) {
                        // remove previous blocker if it exists, as we know it doesn't block any more
                        removeBlocker( leftTuple, blocker );
                    }

                    // find first blocker, because it's a modify, we need to start from the beginning again        
                    for ( RightTuple newBlocker = firstRightTuple; newBlocker != null; newBlocker = (RightTuple) rightIt.next( newBlocker ) ) {
                        if ( constraints.isAllowedCachedLeft( contextEntry,
                                                              newBlocker.getFactHandle() ) ) {
                            leftTuple.setBlocker( newBlocker );
                            newBlocker.addBlocked( leftTuple );

                            break;
                        }
                    }

                    LeftTuple childLeftTuple = leftTuple.getFirstChild();

                    if ( leftTuple.getBlocker() != null ) {
                        // blocked
                        if ( leftTuple.getFirstChild() != null ) {
                            // blocked, with previous children, so must have not been previously blocked, so retract
                            // no need to remove, as we removed at the start
                            // to be matched against, as it's now blocked
                            deleteRightChild( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                        } // else: it's blocked now and no children so blocked before, thus do nothing             
                    } else if ( childLeftTuple == null ) {
                        // not blocked, with no children, must have been previously blocked so assert
                        ltm.add( leftTuple ); // add to memory so other fact handles can attempt to match
                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                       sink,
                                                                       tupleMemory ) );
                    } else {
                        switch ( childLeftTuple.getStagedType() ) {
                        // handle clash with already staged entries
                            case LeftTuple.INSERT :
                                stagedLeftTuples.removeInsert( childLeftTuple );
                                break;
                            case LeftTuple.UPDATE :
                                stagedLeftTuples.removeUpdate( childLeftTuple );
                                break;
                        }
                        // not blocked, with children, so wasn't previous blocked and still isn't so modify                
                        ltm.add( leftTuple ); // add to memory so other fact handles can attempt to match                
                        trgLeftTuples.addUpdate( childLeftTuple );
                        childLeftTuple.reAddLeft();
                    }
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            constraints.resetTuple( contextEntry );
        }



        public void doRightUpdates(NotNode notNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples srcLeftTuples,
                                   StagedLeftTuples trgLeftTuples,
                                   StagedLeftTuples stagedLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;

            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = notNode.getRawConstraints();

            FastIterator leftIt = notNode.getLeftIterator( ltm );
            FastIterator rightIt = notNode.getRightIterator( rtm );

            for ( RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                if ( bm.getLeftTupleMemory() == null || (bm.getLeftTupleMemory().size() == 0 && rightTuple.getBlocked() == null) ) {
                    // do nothing here, as we know there are no left tuples

                    //normally do this at the end, but as we are exiting early, make sure the buckets are still correct.
                    bm.getRightTupleMemory().removeAdd( rightTuple );
                    rightTuple.clearStaged();
                    rightTuple = next;                    
                    continue;
                }

                PropagationContext context = rightTuple.getPropagationContext();

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );

                LeftTuple firstLeftTuple = notNode.getFirstLeftTuple( rightTuple, ltm, context, leftIt );

                LeftTuple firstBlocked = rightTuple.getBlocked();
                // we now have  reference to the first Blocked, so null it in the rightTuple itself, so we can rebuild
                rightTuple.nullBlocked();

                // first process non-blocked tuples, as we know only those ones are in the left memory.
                for ( LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
                    // preserve next now, in case we remove this leftTuple 
                    LeftTuple temp = (LeftTuple) leftIt.next( leftTuple );

                    // we know that only unblocked LeftTuples are  still in the memory
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        leftTuple.setBlocker( rightTuple );
                        rightTuple.addBlocked( leftTuple );

                        // this is now blocked so remove from memory
                        ltm.remove( leftTuple );

                        // subclasses like ForallNotNode might override this propagation
                        if ( leftTuple.getFirstChild() != null ) {
                            deleteRightChild( leftTuple.getFirstChild(), trgLeftTuples, stagedLeftTuples );
                        }
                    }

                    leftTuple = temp;
                }

                if ( firstBlocked != null ) {
                    // now process existing blocks, we only process existing and not new from above loop
                    boolean useComparisonIndex = rtm.getIndexType().isComparison();
                    RightTuple rootBlocker = useComparisonIndex ? null : (RightTuple) rightIt.next( rightTuple );

                    RightTupleList list = rightTuple.getMemory();

                    // we must do this after we have the next in memory
                    // We add to the end to give an opportunity to re-match if in same bucket
                    rtm.removeAdd( rightTuple );

                    if ( !useComparisonIndex && rootBlocker == null && list == rightTuple.getMemory() ) {
                        // we are at the end of the list, so set to self, to give self a chance to rematch
                        rootBlocker = rightTuple;
                    }

                    // iterate all the existing previous blocked LeftTuples
                    for ( LeftTuple leftTuple = firstBlocked; leftTuple != null; ) {
                        LeftTuple temp = leftTuple.getBlockedNext();

                        leftTuple.clearBlocker();

                        constraints.updateFromTuple( contextEntry,
                                                     wm,
                                                     leftTuple );

                        if ( useComparisonIndex ) {
                            rootBlocker = notNode.getFirstRightTuple( leftTuple, rtm, context, rightIt );
                        }

                        // we know that older tuples have been checked so continue next
                        for ( RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) rightIt.next( newBlocker ) ) {
                            if ( constraints.isAllowedCachedLeft( contextEntry,
                                                                  newBlocker.getFactHandle() ) ) {
                                leftTuple.setBlocker( newBlocker );
                                newBlocker.addBlocked( leftTuple );

                                break;
                            }
                        }

                        if ( leftTuple.getBlocker() == null ) {
                            // was previous blocked and not in memory, so add
                            ltm.add( leftTuple );

                            // subclasses like ForallNotNode might override this propagation
                            trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                           sink,
                                                                           tupleMemory ) );
                        }

                        leftTuple = temp;
                    }
                } else {
                    // we had to do this at the end, rather than beginning as this 'if' block needs the next memory tuple
                    rtm.removeAdd( rightTuple );
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }

            constraints.resetFactHandle( contextEntry );
            constraints.resetTuple( contextEntry );
        }

        public void doLeftDeletes(NotNode notNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples,
                                  StagedLeftTuples stagedLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();

            for ( LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                RightTuple blocker = leftTuple.getBlocker();
                if ( blocker == null ) {
                    ltm.remove( leftTuple );

                    LeftTuple childLeftTuple = leftTuple.getFirstChild();

                    if ( childLeftTuple != null ) { // NotNode only has one child
                        childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
                    }
                } else {
                    blocker.removeBlocked( leftTuple );
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        public void doRightDeletes(NotNode notNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;

            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = notNode.getRawConstraints();
            FastIterator it = notNode.getRightIterator( rtm );

            for ( RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                // assign now, so we can remove from memory before doing any possible propagations
                final RightTuple rootBlocker = (RightTuple) it.next( rightTuple );

                rtm.remove( rightTuple );

                if ( rightTuple.getBlocked() != null ) {
                    PropagationContext context = rightTuple.getPropagationContext();
    
                    for ( LeftTuple leftTuple = rightTuple.getBlocked(); leftTuple != null; ) {
                        LeftTuple temp = leftTuple.getBlockedNext();
    
                        leftTuple.clearBlocker();
    
                        constraints.updateFromTuple( contextEntry,
                                                     wm,
                                                     leftTuple );
    
                        // we know that older tuples have been checked so continue next
                        for ( RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) it.next( newBlocker ) ) {
                            if ( constraints.isAllowedCachedLeft( contextEntry,
                                                                  newBlocker.getFactHandle() ) ) {
                                leftTuple.setBlocker( newBlocker );
                                newBlocker.addBlocked( leftTuple );
    
                                break;
                            }
                        }
    
                        if ( leftTuple.getBlocker() == null ) {
                            // was previous blocked and not in memory, so add
                            ltm.add( leftTuple );
    
                            trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                           sink,
                                                                           tupleMemory ) );
                        }
    
                        leftTuple = temp;
                    }
                }

                rightTuple.nullBlocked();
                rightTuple.clearStaged();
                rightTuple = next;
            }      

            constraints.resetTuple( contextEntry );
        }
    }

    public static class PhreakExistsNode {
        public void doNode(ExistsNode existsNode,
                           LeftTupleSink sink,
                           BetaMemory bm,
                           InternalWorkingMemory wm,
                           StagedLeftTuples srcLeftTuples,
                           StagedLeftTuples trgLeftTuples,
                           StagedLeftTuples stagedLeftTuples) {
            StagedRightTuples srcRightTuples = bm.getStagedRightTuples();

            if ( srcRightTuples.getDeleteFirst() != null ) {
                doRightDeletes( existsNode, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcLeftTuples.getDeleteFirst() != null ) {
                doLeftDeletes( existsNode, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcLeftTuples.getUpdateFirst() != null || srcRightTuples.getUpdateFirst() != null ) {
                dpUpdatesReorderMemory( bm,
                                        wm,
                                        srcRightTuples,
                                        srcLeftTuples,
                                        trgLeftTuples );
            }

            if ( srcRightTuples.getUpdateFirst() != null ) {
                doRightUpdates( existsNode, sink, bm, wm, srcRightTuples, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcLeftTuples.getUpdateFirst() != null ) {
                doLeftUpdates( existsNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcRightTuples.getInsertFirst() != null ) {
                doRightInserts( existsNode, sink, bm, wm, srcRightTuples, srcLeftTuples, trgLeftTuples );
            }

            if ( srcLeftTuples.getInsertFirst() != null ) {
                doLeftInserts( existsNode, sink, bm, wm, srcLeftTuples, trgLeftTuples );
            }
            
            srcRightTuples.setInsert( null, 0 );
            srcRightTuples.setDelete( null );
            srcRightTuples.setUpdate( null );             
            
            srcLeftTuples.setInsert( null, 0 );
            srcLeftTuples.setDelete( null );
            srcLeftTuples.setUpdate( null );             
        }

        public void doLeftInserts(ExistsNode existsNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;

            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = existsNode.getRawConstraints();
            FastIterator it = existsNode.getRightIterator( rtm );

            for ( LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                PropagationContext context = leftTuple.getPropagationContext();
                boolean useLeftMemory = true;
                if ( !tupleMemoryEnabled ) {
                    // This is a hack, to not add closed DroolsQuery objects
                    Object object = ((InternalFactHandle) context.getFactHandle()).getObject();
                    if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                        useLeftMemory = false;
                    }
                }

                constraints.updateFromTuple( contextEntry,
                                             wm,
                                            leftTuple );
                
                for ( RightTuple rightTuple = existsNode.getFirstRightTuple(leftTuple, rtm, context, it); rightTuple != null; rightTuple = (RightTuple) it.next(rightTuple)) {
                    if ( constraints.isAllowedCachedLeft( contextEntry,
                                                          rightTuple.getFactHandle() ) ) {

                        leftTuple.setBlocker( rightTuple );
                        if ( useLeftMemory ) {
                            rightTuple.addBlocked( leftTuple );
                        }

                        break;
                    }
                }

                if ( leftTuple.getBlocker() != null ) {
                    // tuple is not blocked to propagate
                    trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                   sink,
                                                                   tupleMemory ) );
                } else if ( useLeftMemory ) {
                    // LeftTuple is not blocked, so add to memory so other RightTuples can match
                    ltm.add( leftTuple );
                }
                leftTuple.clearStaged();
                leftTuple = next;
            }
            srcLeftTuples.setInsert( null, 0 );
            constraints.resetTuple( contextEntry );
        }

        public void doRightInserts(ExistsNode existsNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples srcLeftTuples,
                                   StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;

            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = existsNode.getRawConstraints();
            FastIterator it = existsNode.getLeftIterator( ltm );

            for ( RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                rtm.add( rightTuple );
                PropagationContext context = rightTuple.getPropagationContext();

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );

                for ( LeftTuple leftTuple = existsNode.getFirstLeftTuple( rightTuple, ltm, context, it ); leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
                    // preserve next now, in case we remove this leftTuple 
                    LeftTuple temp = (LeftTuple) it.next(leftTuple);

                    // we know that only unblocked LeftTuples are  still in the memory
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        leftTuple.setBlocker( rightTuple );
                        rightTuple.addBlocked( leftTuple );

                        ltm.remove( leftTuple );

                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                       sink,
                                                                       tupleMemory ) );
                    }

                    leftTuple = temp;
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
            srcRightTuples.setInsert( null, 0 );
            constraints.resetFactHandle( contextEntry );
        }

        public void doLeftUpdates(ExistsNode existsNode,
                                  LeftTupleSink sink,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples,
                                  StagedLeftTuples stagedLeftTuples) {
            boolean tupleMemory = true;
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = existsNode.getRawConstraints();
            FastIterator rightIt = existsNode.getRightIterator( rtm );

            for ( LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                PropagationContext context = leftTuple.getPropagationContext();
                
                RightTuple firstRightTuple = existsNode.getFirstRightTuple(leftTuple, rtm, context, rightIt);
                
                // If in memory, remove it, because we'll need to add it anyway if it's not blocked, to ensure iteration order
                RightTuple blocker = leftTuple.getBlocker();
                if ( blocker == null ) {
                    if ( leftTuple.getMemory().isStagingMemory() ) {
                        leftTuple.getMemory().remove( leftTuple );
                    } else {
                        ltm.remove( leftTuple );
                    }
                    leftTuple.setMemory( null );
                } else {
                    // check if we changed bucket
                    if ( rtm.isIndexed()&& !rightIt.isFullIterator()  ) {                
                        // if newRightTuple is null, we assume there was a bucket change and that bucket is empty                
                        if ( firstRightTuple == null || firstRightTuple.getMemory() != blocker.getMemory() ) {
                            // we changed bucket, so blocker no longer blocks
                            removeBlocker(leftTuple, blocker);
                            blocker = null;
                        }
                    }
                }

                constraints.updateFromTuple( contextEntry,
                                             wm,
                                             leftTuple );

                // if we where not blocked before (or changed buckets), or the previous blocker no longer blocks, then find the next blocker
                if ( blocker == null || !constraints.isAllowedCachedLeft( contextEntry,
                                                                          blocker.getFactHandle() ) ) {

                    if ( blocker != null ) {
                        // remove previous blocker if it exists, as we know it doesn't block any more
                        removeBlocker(leftTuple, blocker);
                    }
                    
                    // find first blocker, because it's a modify, we need to start from the beginning again        
                    for ( RightTuple newBlocker = firstRightTuple; newBlocker != null; newBlocker = (RightTuple) rightIt.next(newBlocker) ) {
                        if ( constraints.isAllowedCachedLeft( contextEntry,
                                                              newBlocker.getFactHandle() ) ) {
                            leftTuple.setBlocker( newBlocker );
                            newBlocker.addBlocked( leftTuple );

                            break;
                        }
                    }
                }

                if ( leftTuple.getBlocker() == null ) {
                    // not blocked
                    ltm.add( leftTuple ); // add to memory so other fact handles can attempt to match                    

                    if ( leftTuple.getFirstChild() != null ) {
                        // with previous children, delete
                        if ( leftTuple.getFirstChild() != null ) {
                            LeftTuple childLeftTuple = leftTuple.getFirstChild();

                            while ( childLeftTuple != null ) {
                                childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
                            }
                        }
                    }
                    // with no previous children. do nothing.
                } else if ( leftTuple.getFirstChild() == null ) {
                    // blocked, with no previous children, insert
                    trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                   sink,
                                                                   tupleMemory ) );
                } else {
                    // blocked, with previous children, modify
                    if ( leftTuple.getFirstChild() != null ) {
                        LeftTuple childLeftTuple = leftTuple.getFirstChild();

                        while ( childLeftTuple != null ) {
                            switch ( childLeftTuple.getStagedType() ) {
                                // handle clash with already staged entries
                                case LeftTuple.INSERT :
                                    stagedLeftTuples.removeInsert( childLeftTuple );
                                    break;
                                case LeftTuple.UPDATE :
                                    stagedLeftTuples.removeUpdate( childLeftTuple );
                                    break;
                            }

                            // update, childLeftTuple is updated
                            trgLeftTuples.addUpdate( childLeftTuple );
                            childLeftTuple.reAddRight();
                            childLeftTuple = childLeftTuple.getLeftParentNext();
                        }
                    }                    
                }
                
                leftTuple.clearStaged();
                leftTuple = next;
            }
            srcLeftTuples.setUpdate( null );
            constraints.resetTuple( contextEntry );
        }

        public void doRightUpdates(ExistsNode existsNode,
                                   LeftTupleSink sink,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples srcLeftTuples,
                                   StagedLeftTuples trgLeftTuples,
                                   StagedLeftTuples stagedLeftTuples) {
            boolean tupleMemory = true;
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = existsNode.getRawConstraints();
            FastIterator leftIt = existsNode.getLeftIterator( ltm );
            FastIterator rightIt = existsNode.getRightIterator( rtm );

            for ( RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                PropagationContext context = rightTuple.getPropagationContext();
                
                LeftTuple firstLeftTuple = existsNode.getFirstLeftTuple( rightTuple, ltm, context, leftIt );
                
                LeftTuple firstBlocked = rightTuple.getBlocked();
                // we now have  reference to the first Blocked, so null it in the rightTuple itself, so we can rebuild
                rightTuple.nullBlocked();
                
                // first process non-blocked tuples, as we know only those ones are in the left memory.
                for ( LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
                    // preserve next now, in case we remove this leftTuple 
                    LeftTuple temp = (LeftTuple) leftIt.next( leftTuple );

                    // we know that only unblocked LeftTuples are  still in the memory
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        leftTuple.setBlocker( rightTuple );
                        rightTuple.addBlocked( leftTuple );

                        // this is now blocked so remove from memory
                        ltm.remove( leftTuple );

                        // subclasses like ForallNotNode might override this propagation
                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                       sink,
                                                                       tupleMemory ) );
                    }

                    leftTuple = temp;
                }

                if ( firstBlocked != null ) {
                    boolean useComparisonIndex = rtm.getIndexType().isComparison();

                    // now process existing blocks, we only process existing and not new from above loop
                    RightTuple rootBlocker = useComparisonIndex ? null : (RightTuple) rightIt.next(rightTuple);
                  
                    RightTupleList list = rightTuple.getMemory();
                    
                    // we must do this after we have the next in memory
                    // We add to the end to give an opportunity to re-match if in same bucket
                    rtm.removeAdd( rightTuple );

                    if ( !useComparisonIndex && rootBlocker == null && list == rightTuple.getMemory() ) {
                        // we are at the end of the list, but still in same bucket, so set to self, to give self a chance to rematch
                        rootBlocker = rightTuple;
                    }  
                    
                    // iterate all the existing previous blocked LeftTuples
                    for ( LeftTuple leftTuple = (LeftTuple) firstBlocked; leftTuple != null; ) {
                        LeftTuple temp = leftTuple.getBlockedNext();

                        leftTuple.clearBlocker(); // must null these as we are re-adding them to the list

                        constraints.updateFromTuple( contextEntry,
                                                     wm,
                                                     leftTuple );

                        if (useComparisonIndex) {
                            rootBlocker = existsNode.getFirstRightTuple( leftTuple, rtm, context, rightIt );
                        }

                        // we know that older tuples have been checked so continue next
                        for ( RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) rightIt.next( newBlocker ) ) {
                            if ( constraints.isAllowedCachedLeft( contextEntry,
                                                                  newBlocker.getFactHandle() ) ) {
                                leftTuple.setBlocker( newBlocker );
                                newBlocker.addBlocked( leftTuple );

                                break;
                            }
                        }

                        if ( leftTuple.getBlocker() == null ) {
                            // was previous blocked and not in memory, so add
                            ltm.add( leftTuple );

                            LeftTuple childLeftTuple = leftTuple.getFirstChild();
                            while ( childLeftTuple != null ) {
                                childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
                            }
                        }

                        leftTuple = temp;
                    }
                } else {
                    // we had to do this at the end, rather than beginning as this 'if' block needs the next memory tuple
                    rtm.removeAdd( rightTuple );         
                }
                
                rightTuple.clearStaged();
                rightTuple = next;
            }
            srcRightTuples.setUpdate( null );
            constraints.resetFactHandle( contextEntry );
        }

        public void doLeftDeletes(ExistsNode existsNode,
                                  BetaMemory bm,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples,
                                  StagedLeftTuples stagedLeftTuples) {
            LeftTupleMemory ltm = bm.getLeftTupleMemory();

            for ( LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                RightTuple blocker = leftTuple.getBlocker();
                if ( blocker == null ) {
                    ltm.remove( leftTuple );                    
                } else {
                    if ( leftTuple.getFirstChild() != null ) {
                        LeftTuple childLeftTuple = leftTuple.getFirstChild();

                        while ( childLeftTuple != null ) {
                            childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
                        }
                    }                    
                    blocker.removeBlocked( leftTuple );
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
            srcLeftTuples.setDelete( null );
        }

        public void doRightDeletes(ExistsNode existsNode,
                                   BetaMemory bm,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples trgLeftTuples,
                                   StagedLeftTuples stagedLeftTuples) {
            boolean tupleMemory = true;
            RightTupleMemory rtm = bm.getRightTupleMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = existsNode.getRawConstraints();
            FastIterator it = existsNode.getRightIterator( rtm );            

            for ( RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                rtm.remove( rightTuple );
                rightTuple.setMemory( null );
                
                final RightTuple rootBlocker = (RightTuple) it.next(rightTuple);
                       
                if ( rightTuple.getBlocked() == null ) {
                    return;
                }

                for ( LeftTuple leftTuple = rightTuple.getBlocked(); leftTuple != null; ) {
                    LeftTuple temp = leftTuple.getBlockedNext();

                    leftTuple.clearBlocker();

                    constraints.updateFromTuple( contextEntry,
                                                  wm,
                                                  leftTuple );

                    // we know that older tuples have been checked so continue previously
                    for ( RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) it.next(newBlocker ) ) {
                        if ( constraints.isAllowedCachedLeft( contextEntry,
                                                              newBlocker.getFactHandle() ) ) {
                            leftTuple.setBlocker( newBlocker );
                            newBlocker.addBlocked( leftTuple );

                            break;
                        }
                    }

                    if ( leftTuple.getBlocker() == null ) {
                        // was previous blocked and not in memory, so add
                        ltm.add( leftTuple );

                        LeftTuple childLeftTuple = leftTuple.getFirstChild();
                        while ( childLeftTuple != null ) {
                            childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
                        }
                    }

                    leftTuple = temp;
                }
                rightTuple.nullBlocked();
                rightTuple.clearStaged();
                rightTuple = next;
            }
            srcRightTuples.setDelete( null );
        }
    }    
    
//    public static class PhreakAccumulateNode {
//        public void doNode(AccumulateNode accNode,
//                           LeftTupleSink sink,
//                           BetaMemory bm,
//                           InternalWorkingMemory wm,
//                           StagedLeftTuples srcLeftTuples,
//                           StagedLeftTuples trgLeftTuples) {
//            StagedRightTuples srcRightTuples = bm.getStagedRightTuples();
//
//            if ( srcRightTuples.getDeleteFirst() != null ) {
//                doRightDeletes( accNode, bm, wm, srcRightTuples, trgLeftTuples );
//            }
//
//            if ( srcLeftTuples.getDeleteFirst() != null ) {
//                doLeftDeletes( accNode, bm, wm, srcLeftTuples, trgLeftTuples );
//            }
//
//            if ( srcLeftTuples.getUpdateFirst() != null || srcRightTuples.getUpdateFirst() != null ) {
//                dpUpdatesReorderMemory( bm,
//                                        wm,
//                                        srcRightTuples,
//                                        srcLeftTuples,
//                                        trgLeftTuples );
//            }
//
//            if ( srcRightTuples.getUpdateFirst() != null ) {
//                doRightUpdates( accNode, sink, bm, wm, srcRightTuples, srcLeftTuples, trgLeftTuples );
//            }
//
//            if ( srcLeftTuples.getUpdateFirst() != null ) {
//                doLeftUpdates( accNode, sink, bm, wm, srcLeftTuples, trgLeftTuples );
//            }
//
//            if ( srcRightTuples.getInsertFirst() != null ) {
//                doRightInserts( accNode, sink, bm, wm, srcRightTuples, srcLeftTuples, trgLeftTuples );
//            }
//
//            if ( srcLeftTuples.getInsertFirst() != null ) {
//                doLeftInserts( accNode, sink, bm, wm, srcLeftTuples, trgLeftTuples );
//            }
//            
//            srcRightTuples.setInsert( null, 0 );
//            srcRightTuples.setDelete( null );
//            srcRightTuples.setUpdate( null );             
//            
//            srcLeftTuples.setInsert( null, 0 );
//            srcLeftTuples.setDelete( null );
//            srcLeftTuples.setUpdate( null );            
//        }
//
//        public void doLeftInserts(AccumulateNode accNode,
//                                  LeftTupleSink sink,
//                                  BetaMemory bm,
//                                  InternalWorkingMemory wm,
//                                  StagedLeftTuples srcLeftTuples,
//                                  StagedLeftTuples trgLeftTuples) {
//            boolean tupleMemory = true;
//            boolean tupleMemoryEnabled = true;
//
//            LeftTupleMemory ltm = bm.getLeftTupleMemory();
//            RightTupleMemory rtm = bm.getRightTupleMemory();
//            ContextEntry[] contextEntry = bm.getContext();
//            BetaConstraints constraints = accNode.getRawConstraints();
//            FastIterator it = accNode.getRightIterator( rtm );
//
//            for ( LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
//                LeftTuple next = leftTuple.getStagedNext();
//                PropagationContext context = leftTuple.getPropagationContext();
//                boolean useLeftMemory = true;
//
//                if ( !tupleMemoryEnabled ) {
//                    // This is a hack, to not add closed DroolsQuery objects
//                    Object object = leftTuple.get( 0 ).getObject();
//                    if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
//                        useLeftMemory = false;
//                    }
//                }
//
//                if ( useLeftMemory ) {
//                    ltm.add( leftTuple );
//                }
//
//                constraints.updateFromTuple( contextEntry,
//                                             wm,
//                                             leftTuple );
//
//                for ( RightTuple rightTuple = accNode.getFirstRightTuple( leftTuple,
//                                                                           rtm,
//                                                                           context,
//                                                                           it ); rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
//                    if ( constraints.isAllowedCachedLeft( contextEntry,
//                                                          rightTuple.getFactHandle() ) ) {
//                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
//                                                                       rightTuple,
//                                                                       null,
//                                                                       null,
//                                                                       sink,
//                                                                       tupleMemory ) );
//                    }
//                }
//                leftTuple.clearStaged();
//                leftTuple = next;
//            }
//            srcLeftTuples.setInsert( null, 0 );
//            constraints.resetTuple( contextEntry );
//        }
//
//        public void doRightInserts(AccumulateNode accNode,
//                                   LeftTupleSink sink,
//                                   BetaMemory bm,
//                                   InternalWorkingMemory wm,
//                                   StagedRightTuples srcRightTuples,
//                                   StagedLeftTuples srcLeftTuples,
//                                   StagedLeftTuples trgLeftTuples) {
//            boolean tupleMemory = true;
//            boolean tupleMemoryEnabled = true;
//
//            LeftTupleMemory ltm = bm.getLeftTupleMemory();
//            RightTupleMemory rtm = bm.getRightTupleMemory();
//            ContextEntry[] contextEntry = bm.getContext();
//            BetaConstraints constraints = accNode.getRawConstraints();
//            FastIterator it = accNode.getLeftIterator( ltm );
//
//            for ( RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
//                RightTuple next = rightTuple.getStagedNext();
//                rtm.add( rightTuple );
//                PropagationContext context = rightTuple.getPropagationContext();
//
//                constraints.updateFromFactHandle( contextEntry,
//                                                  wm,
//                                                  rightTuple.getFactHandle() );
//
//                for ( LeftTuple leftTuple = accNode.getFirstLeftTuple( rightTuple, ltm, context, it ); leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
//                    trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
//                                                                   rightTuple,
//                                                                   null,
//                                                                   null,
//                                                                   sink,
//                                                                   tupleMemory ) );
//                }
//                rightTuple.clearStaged();
//                rightTuple = next;
//            }
//            srcRightTuples.setInsert( null, 0 );
//            constraints.resetFactHandle( contextEntry );
//        }
//
//        public void doLeftUpdates(AccumulateNode accNode,
//                                  LeftTupleSink sink,
//                                  BetaMemory bm,
//                                  InternalWorkingMemory wm,
//                                  StagedLeftTuples srcLeftTuples,
//                                  StagedLeftTuples trgLeftTuples) {
//            boolean tupleMemory = true;
//            RightTupleMemory rtm = bm.getRightTupleMemory();
//            ContextEntry[] contextEntry = bm.getContext();
//            BetaConstraints constraints = accNode.getRawConstraints();
//            FastIterator it = accNode.getRightIterator( rtm );
//
//            StagedLeftTuples stagedLeftTuples = null;
//            if ( !bm.getSegmentMemory().isEmpty() ) {
//                stagedLeftTuples = bm.getSegmentMemory().getFirst().getStagedLeftTuples();
//            }
//
//            for ( LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
//                LeftTuple next = leftTuple.getStagedNext();
//                PropagationContext context = leftTuple.getPropagationContext();
//
//                constraints.updateFromTuple( contextEntry,
//                                             wm,
//                                             leftTuple );
//
//                RightTuple rightTuple = accNode.getFirstRightTuple( leftTuple,
//                                                                     rtm,
//                                                                     context,
//                                                                     it );
//
//                LeftTuple childLeftTuple = leftTuple.getFirstChild();
//
//                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
//                // if rightTuple is null, we assume there was a bucket change and that bucket is empty        
//                if ( childLeftTuple != null && rtm.isIndexed() && !it.isFullIterator() && (rightTuple == null || (rightTuple.getMemory() != childLeftTuple.getRightParent().getMemory())) ) {
//                    // our index has changed, so delete all the previous propagations
//                    while ( childLeftTuple != null ) {
//                        childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
//                    }
//                    // childLeftTuple is now null, so the next check will attempt matches for new bucket
//                }
//
//                // we can't do anything if RightTupleMemory is empty
//                if ( rightTuple != null ) {
//                    doLeftUpdatesProcessChildren( childLeftTuple, leftTuple, rightTuple, stagedLeftTuples, tupleMemory, contextEntry, constraints, sink, it, trgLeftTuples );
//                }
//                leftTuple.clearStaged();
//                leftTuple = next;
//            }
//            srcLeftTuples.setUpdate( null );
//            constraints.resetTuple( contextEntry );
//        }
//
//        public LeftTuple doLeftUpdatesProcessChildren(LeftTuple childLeftTuple,
//                                                      LeftTuple leftTuple,
//                                                      RightTuple rightTuple,
//                                                      StagedLeftTuples stagedLeftTuples,
//                                                      boolean tupleMemory,
//                                                      ContextEntry[] contextEntry,
//                                                      BetaConstraints constraints,
//                                                      LeftTupleSink sink,
//                                                      FastIterator it,
//                                                      StagedLeftTuples trgLeftTuples) {
//            if ( childLeftTuple == null ) {
//                // either we are indexed and changed buckets or
//                // we had no children before, but there is a bucket to potentially match, so try as normal assert
//                for ( ; rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
//                    if ( constraints.isAllowedCachedLeft( contextEntry,
//                                                          rightTuple.getFactHandle() ) ) {
//                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
//                                                                       rightTuple,
//                                                                       null,
//                                                                       null,
//                                                                       sink,
//                                                                       tupleMemory ) );
//                    }
//                }
//            } else {
//                // in the same bucket, so iterate and compare
//                for ( ; rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
//                    if ( constraints.isAllowedCachedLeft( contextEntry,
//                                                          rightTuple.getFactHandle() ) ) {
//                        // insert, childLeftTuple is not updated
//                        if ( childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple ) {
//                            trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
//                                                                           rightTuple,
//                                                                           null,
//                                                                           null,
//                                                                           sink,
//                                                                           tupleMemory ) );
//                        } else {
//                            switch ( childLeftTuple.getStagedType() ) {
//                            // handle clash with already staged entries
//                                case LeftTuple.INSERT :
//                                    stagedLeftTuples.removeInsert( childLeftTuple );
//                                    break;
//                                case LeftTuple.UPDATE :
//                                    stagedLeftTuples.removeUpdate( childLeftTuple );
//                                    break;
//                            }
//
//                            // update, childLeftTuple is updated
//                            trgLeftTuples.addUpdate( childLeftTuple );
//
//                            childLeftTuple.reAddRight();
//                            childLeftTuple = childLeftTuple.getLeftParentNext();
//                        }
//                    } else if ( childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple ) {
//                        // delete, childLeftTuple is updated
//                        childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
//                    }
//                }
//            }
//
//            return childLeftTuple;
//        }
//
//        public void doRightUpdates(AccumulateNode accNode,
//                                   LeftTupleSink sink,
//                                   BetaMemory bm,
//                                   InternalWorkingMemory wm,
//                                   StagedRightTuples srcRightTuples,
//                                   StagedLeftTuples srcLeftTuples,
//                                   StagedLeftTuples trgLeftTuples) {
//            boolean tupleMemory = true;
//            LeftTupleMemory ltm = bm.getLeftTupleMemory();
//            ContextEntry[] contextEntry = bm.getContext();
//            BetaConstraints constraints = accNode.getRawConstraints();
//            FastIterator it = accNode.getLeftIterator( ltm );
//
//            StagedLeftTuples stagedLeftTuples = null;
//            if ( !bm.getSegmentMemory().isEmpty() ) {
//                stagedLeftTuples = bm.getSegmentMemory().getFirst().getStagedLeftTuples();
//            }
//
//            for ( RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
//                RightTuple next = rightTuple.getStagedNext();
//                PropagationContext context = rightTuple.getPropagationContext();
//
//                LeftTuple childLeftTuple = rightTuple.getFirstChild();
//
//                LeftTuple leftTuple = accNode.getFirstLeftTuple( rightTuple, ltm, context, it );
//
//                constraints.updateFromFactHandle( contextEntry,
//                                                  wm,
//                                                  rightTuple.getFactHandle() );
//
//                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
//                // We assume a bucket change if leftTuple == null        
//                if ( childLeftTuple != null && ltm.isIndexed() && !it.isFullIterator() && (leftTuple == null || (leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory())) ) {
//                    // our index has changed, so delete all the previous propagations
//                    while ( childLeftTuple != null ) {
//                        childLeftTuple = deleteRightChild( childLeftTuple, trgLeftTuples, stagedLeftTuples );
//                    }
//                    // childLeftTuple is now null, so the next check will attempt matches for new bucket                    
//                }
//
//                // we can't do anything if LeftTupleMemory is empty
//                if ( leftTuple != null ) {
//                    doRightUpdatesProcessChildren( childLeftTuple, leftTuple, rightTuple, stagedLeftTuples, tupleMemory, contextEntry, constraints, sink, it, trgLeftTuples );
//                }
//                rightTuple.clearStaged();
//                rightTuple = next;
//            }
//            srcRightTuples.setUpdate( null );
//            constraints.resetFactHandle( contextEntry );
//        }
//
//        public LeftTuple doRightUpdatesProcessChildren(LeftTuple childLeftTuple,
//                                                       LeftTuple leftTuple,
//                                                       RightTuple rightTuple,
//                                                       StagedLeftTuples stagedLeftTuples,
//                                                       boolean tupleMemory,
//                                                       ContextEntry[] contextEntry,
//                                                       BetaConstraints constraints,
//                                                       LeftTupleSink sink,
//                                                       FastIterator it,
//                                                       StagedLeftTuples trgLeftTuples) {
//            if ( childLeftTuple == null ) {
//                // either we are indexed and changed buckets or
//                // we had no children before, but there is a bucket to potentially match, so try as normal assert
//                for ( ; leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
//                    if ( constraints.isAllowedCachedRight( contextEntry,
//                                                           leftTuple ) ) {
//                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
//                                                                       rightTuple,
//                                                                       null,
//                                                                       null,
//                                                                       sink,
//                                                                       tupleMemory ) );
//                    }
//                }
//            } else {
//                // in the same bucket, so iterate and compare
//                for ( ; leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
//                    if ( constraints.isAllowedCachedRight( contextEntry,
//                                                           leftTuple ) ) {
//                        // insert, childLeftTuple is not updated
//                        if ( childLeftTuple == null || childLeftTuple.getLeftParent() != leftTuple ) {
//                            trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
//                                                                           rightTuple,
//                                                                           null,
//                                                                           null,
//                                                                           sink,
//                                                                           tupleMemory ) );
//                        } else {
//                            switch ( childLeftTuple.getStagedType() ) {
//                            // handle clash with already staged entries
//                                case LeftTuple.INSERT :
//                                    stagedLeftTuples.removeInsert( childLeftTuple );
//                                    break;
//                                case LeftTuple.UPDATE :
//                                    stagedLeftTuples.removeUpdate( childLeftTuple );
//                                    break;
//                            }
//
//                            // update, childLeftTuple is updated
//                            trgLeftTuples.addUpdate( childLeftTuple );
//
//                            childLeftTuple.reAddLeft();
//                            childLeftTuple = childLeftTuple.getRightParentNext();
//                        }
//                    } else if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {
//                        // delete, childLeftTuple is updated
//                        childLeftTuple = deleteRightChild( childLeftTuple, trgLeftTuples, stagedLeftTuples );
//                    }
//                }
//            }
//
//            return childLeftTuple;
//        }
//
//        public void doLeftDeletes(AccumulateNode accNod,
//                                  BetaMemory bm,
//                                  InternalWorkingMemory wm,
//                                  StagedLeftTuples srcLeftTuples,
//                                  StagedLeftTuples trgLeftTuples) {
//            LeftTupleMemory ltm = bm.getLeftTupleMemory();
//
//            StagedLeftTuples stagedLeftTuples = null;
//            if ( !bm.getSegmentMemory().isEmpty() ) {
//                stagedLeftTuples = bm.getSegmentMemory().getFirst().getStagedLeftTuples();
//            }
//
//            for ( LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
//                LeftTuple next = leftTuple.getStagedNext();
//                ltm.remove( leftTuple );
//
//                if ( leftTuple.getFirstChild() != null ) {
//                    LeftTuple childLeftTuple = leftTuple.getFirstChild();
//
//                    while ( childLeftTuple != null ) {
//                        childLeftTuple = deleteLeftChild( trgLeftTuples, childLeftTuple, stagedLeftTuples );
//                    }
//                }
//                leftTuple.clearStaged();
//                leftTuple = next;
//            }
//            srcLeftTuples.setDelete( null );
//        }
//
//        public void doRightDeletes(AccumulateNode accNod,
//                                   BetaMemory bm,
//                                   InternalWorkingMemory wm,
//                                   StagedRightTuples srcRightTuples,
//                                   StagedLeftTuples trgLeftTuples) {
//            RightTupleMemory rtm = bm.getRightTupleMemory();
//
//            StagedLeftTuples stagedLeftTuples = null;
//            if ( !bm.getSegmentMemory().isEmpty() ) {
//                stagedLeftTuples = bm.getSegmentMemory().getFirst().getStagedLeftTuples();
//            }
//
//            for ( RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
//                RightTuple next = rightTuple.getStagedNext();
//                rtm.remove( rightTuple );
//
//                if ( rightTuple.getFirstChild() != null ) {
//                    LeftTuple childLeftTuple = rightTuple.getFirstChild();
//
//                    while ( childLeftTuple != null ) {
//                        childLeftTuple = deleteRightChild( childLeftTuple, trgLeftTuples, stagedLeftTuples );
//                    }
//                }
//                rightTuple.clearStaged();
//                rightTuple = next;
//            }
//            srcRightTuples.setDelete( null );
//        }
//    }
    

    public static class PhreakRuleTerminalNode {
        public void doNode(RuleTerminalNode rtnNode,
                           InternalWorkingMemory wm,
                           StagedLeftTuples srcLeftTuples) {

            if ( srcLeftTuples.getDeleteFirst() != null ) {
                doLeftDeletes( rtnNode, wm, srcLeftTuples );
            }

            if ( srcLeftTuples.getUpdateFirst() != null ) {
                doLeftUpdates( rtnNode, wm, srcLeftTuples );
            }

            if ( srcLeftTuples.getInsertFirst() != null ) {
                doLeftInserts( rtnNode, wm, srcLeftTuples );
            }
        }

        public void doLeftInserts(RuleTerminalNode rtnNode,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples) {

            for ( LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                rtnNode.assertLeftTuple( leftTuple, leftTuple.getPropagationContext(), wm );
                leftTuple.clearStaged();
                leftTuple = next;
            }
            srcLeftTuples.setInsert( null, 0 );
        }

        public void doLeftUpdates(RuleTerminalNode rtnNode,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples) {

            for ( LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                rtnNode.modifyLeftTuple( leftTuple, leftTuple.getPropagationContext(), wm );
                leftTuple.clearStaged();
                leftTuple = next;
            }
            srcLeftTuples.setUpdate( null );
        }

        public void doLeftDeletes(RuleTerminalNode rtnNode,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples) {

            for ( LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                rtnNode.retractLeftTuple( leftTuple, leftTuple.getPropagationContext(), wm );
                leftTuple.clearStaged();
                leftTuple = next;
            }
            srcLeftTuples.setDelete( null );
        }
    }

    public static LeftTuple deleteLeftChild(StagedLeftTuples trgLeftTuples,
                                            LeftTuple childLeftTuple,
                                            StagedLeftTuples stagedLeftTuples) {
        switch ( childLeftTuple.getStagedType() ) {
            // handle clash with already staged entries
            case LeftTuple.INSERT :
                stagedLeftTuples.removeInsert( childLeftTuple );
                break;
            case LeftTuple.UPDATE :
                stagedLeftTuples.removeUpdate( childLeftTuple );
                break;
        }

        LeftTuple next = childLeftTuple.getLeftParentNext();

        trgLeftTuples.addDelete( childLeftTuple );
        childLeftTuple.unlinkFromRightParent();
        childLeftTuple.unlinkFromLeftParent();

        return next;
    }

    public static LeftTuple deleteRightChild(LeftTuple childLeftTuple,
                                             StagedLeftTuples trgLeftTuples,
                                             StagedLeftTuples stagedLeftTuples) {
        switch ( childLeftTuple.getStagedType() ) {
            // handle clash with already staged entries
            case LeftTuple.INSERT :
                stagedLeftTuples.removeInsert( childLeftTuple );
                break;
            case LeftTuple.UPDATE :
                stagedLeftTuples.removeUpdate( childLeftTuple );
                break;
        }

        LeftTuple next = childLeftTuple.getRightParentNext();

        trgLeftTuples.addDelete( childLeftTuple );
        childLeftTuple.unlinkFromRightParent();
        childLeftTuple.unlinkFromLeftParent();

        return next;
    }

    public static void dpUpdatesReorderMemory(BetaMemory bm,
                                              InternalWorkingMemory wm,
                                              StagedRightTuples srcRightTuples,
                                              StagedLeftTuples srcLeftTuples,
                                              StagedLeftTuples trgLeftTuples) {
        LeftTupleMemory ltm = bm.getLeftTupleMemory();
        RightTupleMemory rtm = bm.getRightTupleMemory();

        // sides must first be re-ordered, to ensure iteration integrity
        for ( LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            ltm.removeAdd( leftTuple );
            for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; ) {
                LeftTuple childNext = childLeftTuple.getLeftParentNext();
                childLeftTuple.reAddRight();
                childLeftTuple = childNext;
            }
            leftTuple = next;
        }

        for ( RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            rtm.removeAdd( rightTuple );
            for ( LeftTuple childLeftTuple = rightTuple.getFirstChild(); childLeftTuple != null; ) {
                LeftTuple childNext = childLeftTuple.getLeftParentNext();
                childLeftTuple.reAddLeft();
                childLeftTuple = childNext;
            }
            rightTuple = next;
        }
    }
    
    public static void removeBlocker(LeftTuple leftTuple,
                                     RightTuple blocker) {
        blocker.removeBlocked( leftTuple );
        leftTuple.clearBlocker();
    }

}
