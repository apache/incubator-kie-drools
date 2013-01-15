package org.drools.phreak;

import static org.drools.core.util.BitMaskUtil.intersect;

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
import org.drools.common.MemoryFactory;
import org.drools.common.NetworkNode;
import org.drools.common.StagedLeftTuples;
import org.drools.common.StagedRightTuples;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;
import org.drools.core.util.index.RightTupleList;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.AccumulateNode.AccumulateContext;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.AccumulateNode.ActivitySource;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.ExistsNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSinkNode;
import org.drools.reteoo.LeftTupleSinkPropagator;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.reteoo.ReteooWorkingMemory.EvaluateResultConstraints;
import org.drools.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleMemory;
import org.drools.reteoo.RuleMemory;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.SegmentMemory;
import org.drools.rule.Accumulate;
import org.drools.rule.ContextEntry;
import org.drools.rule.Rule;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleNetworkEvaluatorActivation extends AgendaItem {

    private static final Logger    log         = LoggerFactory.getLogger( RuleNetworkEvaluatorActivation.class );

    private RuleMemory             rmem;

    //    private PhreakLianNode         pLiaNode     = new PhreakLianNode();
    private PhreakJoinNode         pJoinNode   = new PhreakJoinNode();
    private PhreakNotNode          pNotNode    = new PhreakNotNode();
    private PhreakExistsNode       pExistsNode = new PhreakExistsNode();
    private PhreakAccumulateNode   pAccNode    = new PhreakAccumulateNode();
    private PhreakRuleTerminalNode pRtnNode    = new PhreakRuleTerminalNode();

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

    public int evaluateNetwork(InternalWorkingMemory wm) {
        if ( log.isTraceEnabled() ) {
            log.trace( "Start Evaluating Rule[name={}]", getRule().getName() );
        }
        SegmentMemory[] smems = rmem.getSegmentMemories();

        int smemIndex = 0;
        SegmentMemory smem = smems[smemIndex]; // 0
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) smem.getRootNode();

        //LinkedList<Memory> nodeMemories = smem.getNodeMemories();

        //LiaNodeMemory liaNodeMemory = (LiaNodeMemory) nodeMemories.getFirst();

        
        
        NetworkNode node;
        Memory nodeMem;
        if ( liaNode == smem.getTipNode() ) {
            // segment only has liaNode in it
            // nothing is staged in the liaNode, so skip to next segment           
            smem = smems[++smemIndex]; // 1
            node = smem.getRootNode();
            nodeMem = smem.getNodeMemories().getFirst();
        } else {
            // lia is in shared segment, so point to next node
            LeftTupleSinkPropagator sink = liaNode.getSinkPropagator();
            LeftTupleSinkNode firstSink = (LeftTupleSinkNode) sink.getFirstLeftTupleSink() ;
            LeftTupleSinkNode secondSink = firstSink.getNextLeftTupleSinkNode(); 
            if ( sink.size() == 2 ) {
                // As we check above for segment splits, if the sink size is 2, it must be a subnetwork.
                // Always take the non riaNode path
                node = (LeftTupleSource) secondSink;
            } else {
                node = (LeftTupleSource) firstSink;
            }   
            nodeMem = smem.getNodeMemories().getFirst().getNext(); // skip the liaNode memory
        }
        
        StagedLeftTuples srcTuples = smem.getStagedLeftTuples();
        
//        pLiaNode.doNode( liaNode, liaNodeMemory, wm, srcTuples, trgTuples );
//
//        LeftTupleSource node = null;
//        Memory nodeMem = null;
//        if ( liaNode == smem.getTipNode() ) {
//            // Segment only contains LiaNode, so need to propagate peers
//            SegmentPropagator.propagateLia( smem, trgTuples, wm );
//            smem = smems[++smemIndex]; // 1
//            trgTuples = smem.getStagedLeftTuples();
//            nodeMem = smem.getNodeMemories().getFirst();
//            node = smem.getRootNode();
//        } else {
//            // we know it can only have one child, or two if there is a subnetwork
//            LeftTupleSinkPropagator sink = liaNode.getSinkPropagator();
//            LeftTupleSinkNode firstSink = (LeftTupleSinkNode) sink.getFirstLeftTupleSink() ;
//            LeftTupleSinkNode secondSink = firstSink.getNextLeftTupleSinkNode(); 
//            if ( sink.size() == 2 ) {
//                // As we check above for segment splits, if the sink size is 2, it must be a subnetwork.
//                // Always take the non riaNode path
//                node = (LeftTupleSource) secondSink;
//            } else {
//                node = (LeftTupleSource) firstSink;
//            }
//            
//            if ( node.getLeftInferredMask() != Long.MAX_VALUE ) {
//                long mask = node.getLeftInferredMask();
//                // there is a mask, so all updates and inserts (as a result of an update) must be filtered
//                for ( LeftTuple leftTuple = trgTuples.getUpdateFirst(); leftTuple != null; ) {
//                    LeftTuple next = leftTuple.getStagedNext();
//                    if (  !intersect( leftTuple.getPropagationContext().getModificationMask(), 
//                                     mask ) ) {
//                        trgTuples.removeUpdate( leftTuple );
//                    }
//                    leftTuple = next;
//                }
//                
//                // remove inserts, that was a result of an update
//                for ( LeftTuple leftTuple = trgTuples.getInsertFirst(); leftTuple != null; ) {
//                    LeftTuple next = leftTuple.getStagedNext();
//                    if (  !intersect( leftTuple.getPropagationContext().getModificationMask(), 
//                                     mask ) ) {
//                        trgTuples.removeUpdate( leftTuple );
//                    }
//                    leftTuple = next;
//                }                
//            }
//            
//            nodeMem = liaNodeMemory.getNext();
//        }

        eval( ( LeftTupleSink ) node, nodeMem, smems, smemIndex, srcTuples, null, wm);

        return 0;
    }
    
    public StagedLeftTuples eval( NetworkNode node, Memory nodeMem, SegmentMemory[] smems, int smemIndex, StagedLeftTuples trgTuples, StagedLeftTuples stagedLeftTuples, InternalWorkingMemory wm) {
        StagedLeftTuples srcTuples;
        
        SegmentMemory smem = smems[smemIndex];
        while ( true ) {
//            if ( log.isTraceEnabled() ) {
//                log.trace( "Start Node[offset={}, type={}] Rule[name={}]", getRule().getName() );
//            }
            srcTuples = trgTuples; // previous target, is now the source
            
            if ( NodeTypeEnums.isTerminalNode( node ) ) {
                RuleTerminalNode rtn = rmem.getRuleTerminalNode();
                pRtnNode.doNode( rtn, wm, srcTuples );
                return null;
            } else if ( NodeTypeEnums.RightInputAdaterNode == node.getType() ) {
                return trgTuples;
            }           
            
            if ( node == smem.getTipNode() && smem.getFirst() != null) {
                // we are about to process the segment tip, allow it to merge insert/update/delete clashes
                // Can happen if the next segments have not yet been initialized
                stagedLeftTuples = smem.getFirst().getStagedLeftTuples();
            } else {
                stagedLeftTuples = null;
            }          
            
            LeftTupleSinkNode sink = ((LeftTupleSource)node).getSinkPropagator().getFirstLeftTupleSink();

            trgTuples = new StagedLeftTuples();
            
            if ( NodeTypeEnums.isBetaNode( node ) ) {
                BetaNode betaNode = ( BetaNode )node;
                
                BetaMemory bm = null;
                AccumulateMemory am = null;
                if ( NodeTypeEnums.AccumulateNode == node.getType() ) {
                    am = (AccumulateMemory) nodeMem;
                } else {
                    bm = (BetaMemory) nodeMem;    
                }                
                
                if ( betaNode.isRightInputIsRiaNode() ) {
                    // if the subnetwork is nested in this segment, it will create srcTuples containing
                    // peer LeftTuples, suitable for the node in the main path. 
                    srcTuples = doRiaNode( wm,
                                           srcTuples,
                                           betaNode,
                                           bm );                    
                }
                
                switch( node.getType() ) {
                    case  NodeTypeEnums.JoinNode:                    
                        pJoinNode.doNode( (JoinNode) node, sink, 
                                          bm, wm, srcTuples, trgTuples, stagedLeftTuples );
                        break;
                    case  NodeTypeEnums.NotNode:
                        pNotNode.doNode( (NotNode) node, sink, 
                                         bm, wm, srcTuples, trgTuples, stagedLeftTuples );
                        break;
                    case  NodeTypeEnums.ExistsNode:
                        pExistsNode.doNode( (ExistsNode) node, sink, 
                                            bm, wm, srcTuples, trgTuples, stagedLeftTuples );
                        break;
                    case  NodeTypeEnums.AccumulateNode:
                        pAccNode.doNode( (AccumulateNode) node, sink, 
                                         am, wm, srcTuples, trgTuples, stagedLeftTuples );
                        break;                           
                }
            }
            
            if ( node != smem.getTipNode() ) {
                // get next node and node memory in the segment
                LeftTupleSink nextSink = sink.getNextLeftTupleSinkNode();
                if ( nextSink == null ) {
                    node = sink;
                } else {
                    // there is a nested subnetwork, take out path
                    node = nextSink;
                }
                
                nodeMem = nodeMem.getNext();
            } else {                            
                // Reached end of segment, start on new segment.
                SegmentPropagator.propagate( smem, trgTuples, wm );
                smem = smems[smemIndex++];
                trgTuples = smem.getStagedLeftTuples();
                node = ( LeftTupleSink ) smem.getRootNode(); 
                nodeMem = smem.getNodeMemories().getFirst();
            }
        }
    }

    private StagedLeftTuples doRiaNode(InternalWorkingMemory wm,
                           StagedLeftTuples srcTuples,
                           BetaNode betaNode,
                           BetaMemory bm) {
        SegmentMemory subSmem = bm.getSubnetworkSegmentMemory();
        
        if ( betaNode.getLeftTupleSource().getSinkPropagator().size() == 2 ) {
            // sub network is not part of  share split, so need to handle propagation
            // this ensures the first LeftTuple is actually the subnetwork node
            // and the main outer network now receives the peer, notice the swap at the end "srcTuples == peerTuples"
            StagedLeftTuples peerTuples = new StagedLeftTuples();
            SegmentPropagator.processPeers( srcTuples, peerTuples, betaNode );
            // Make sure subnetwork Segment has tuples to process
            StagedLeftTuples subnetworkStaged =  subSmem.getStagedLeftTuples();
            subnetworkStaged.addAllDeletes( srcTuples.getDeleteFirst() );
            subnetworkStaged.addAllUpdates( srcTuples.getUpdateFirst() );
            subnetworkStaged.addAllInserts( srcTuples.getInsertFirst() );  
            srcTuples = peerTuples;
        }                    
        
        RightInputAdapterNode riaNode = ( RightInputAdapterNode ) betaNode.getRightInput();
        RiaNodeMemory riaNodeMemory = ( RiaNodeMemory ) wm.getNodeMemory( (MemoryFactory) betaNode.getRightInput() );                    
        StagedLeftTuples riaStagedTuples = eval( ( LeftTupleSink ) subSmem.getRootNode(), subSmem.getNodeMemories().getFirst(), riaNodeMemory.getRiaRuleMemory().getSegmentMemories(), subSmem.getPos(), subSmem.getStagedLeftTuples(), null, wm );
        
        for ( LeftTuple leftTuple = riaStagedTuples.getInsertFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            InternalFactHandle handle = riaNode.createFactHandle( leftTuple, leftTuple.getPropagationContext(), wm );
            RightTuple rightTuple = new RightTuple( handle, betaNode ); 
            leftTuple.setObject( rightTuple );
            bm.getStagedRightTuples().addInsert( rightTuple );
        }
        
        for ( LeftTuple leftTuple = riaStagedTuples.getDeleteFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            bm.getStagedRightTuples().addDelete( (RightTuple) leftTuple.getObject()  );
        }
        
        for ( LeftTuple leftTuple = riaStagedTuples.getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            bm.getStagedRightTuples().addUpdate( (RightTuple) leftTuple.getObject()  );
        }   
        
        return srcTuples;
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

//    public static class PhreakLianNode {
//        public void doNode(LeftInputAdapterNode liaNode,
//                           LiaNodeMemory lm,
//                           InternalWorkingMemory wm,
//                           StagedLeftTuples srcLeftTuples,
//                           StagedLeftTuples trgLeftTuples) {
//            int size = srcLeftTuples.insertSize();
//            //            if ( size >= 24 ) {
//            //                LeftTuple leftTuple = srcLeftTuples.getInsertFirst();
//            //                for ( int i = 0; i < 24; i++ ) {
//            //                    leftTuple = leftTuple.getStagedNext();
//            //                }
//            //                srcLeftTuples.splitInsert( leftTuple, 25 );
//            //                trgLeftTuples.setInsert( leftTuple.getStagedPrevious(), 25 );
//            //            } else {
//            trgLeftTuples.setInsert( srcLeftTuples.getInsertFirst(), srcLeftTuples.insertSize() );
//            srcLeftTuples.setInsert( null, 0 );
//            //            }
//
//            trgLeftTuples.setDelete( srcLeftTuples.getDeleteFirst() );
//            trgLeftTuples.setUpdate( srcLeftTuples.getUpdateFirst() );
//
//            srcLeftTuples.setDelete( null );
//            srcLeftTuples.setUpdate( null );
//        }
//    }

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
                                        srcLeftTuples );
            }

            if ( srcRightTuples.getUpdateFirst() != null ) {
                doRightUpdates( joinNode, sink, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples   );
            }

            if ( srcLeftTuples.getUpdateFirst() != null ) {
                doLeftUpdates( joinNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples   );
            }

            if ( srcRightTuples.getInsertFirst() != null ) {
                doRightInserts( joinNode, sink, bm, wm, srcRightTuples, trgLeftTuples  );
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
                if ( leftTuple.getMemory() != null ) {
                    // it may have been staged and never actually added
                    ltm.remove( leftTuple );
                }

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
                if (  rightTuple.getMemory() != null ) {
                    // it may have been staged and never actually added
                    rtm.remove( rightTuple );
                };

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
                                        srcLeftTuples );
            }

            if ( srcRightTuples.getUpdateFirst() != null ) {
                doRightUpdates( notNode, sink, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcLeftTuples.getUpdateFirst() != null ) {
                doLeftUpdates( notNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcRightTuples.getInsertFirst() != null ) {
                doRightInserts( notNode, sink, bm, wm, srcRightTuples, trgLeftTuples );
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

            unlinkNotNodeOnRightInsert( notNode,
                                        bm,
                                        wm );
            
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

        public static void unlinkNotNodeOnRightInsert(NotNode notNode,
                                                BetaMemory bm,
                                                InternalWorkingMemory wm) {
            if ( bm.getSegmentMemory().isSegmentLinked() && !notNode.isRightInputIsRiaNode() && notNode.isEmptyBetaConstraints() ) {
                    // this must be processed here, rather than initial insert, as we need to link the blocker
                    // @TODO this could be more efficient, as it means the entire StagedLeftTuples for all previous nodes where evaluated, needlessly. 
                    bm.unlinkNode( wm );
            }
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
                    if ( leftTuple.getMemory() != null ) {
                        // it may have been staged and never actually added
                        ltm.remove( leftTuple );
                    }

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

                if (  rightTuple.getMemory() != null ) {
                    // it may have been staged and never actually added
                    rtm.remove( rightTuple );
                }

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
                                        srcLeftTuples );
            }

            if ( srcRightTuples.getUpdateFirst() != null ) {
                doRightUpdates( existsNode, sink, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcLeftTuples.getUpdateFirst() != null ) {
                doLeftUpdates( existsNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
            }

            if ( srcRightTuples.getInsertFirst() != null ) {
                doRightInserts( existsNode, sink, bm, wm, srcRightTuples, trgLeftTuples );
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
                    if ( leftTuple.getMemory() != null ) {
                        // it may have been staged and never actually added
                        ltm.remove( leftTuple );
                    }                   
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
                if (  rightTuple.getMemory() != null ) {
                    // it may have been staged and never actually added
                    rtm.remove( rightTuple );
                }
                                       
                if ( rightTuple.getBlocked() != null ) {
                    final RightTuple rootBlocker = (RightTuple) it.next(rightTuple);
                    
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
                }
                rightTuple.nullBlocked();
                rightTuple.clearStaged();
                rightTuple = next;
            }
            srcRightTuples.setDelete( null );
        }
    }    
    
    public static class PhreakAccumulateNode {
        public void doNode(AccumulateNode accNode,
                           LeftTupleSink sink,
                           AccumulateMemory am,
                           InternalWorkingMemory wm,
                           StagedLeftTuples srcLeftTuples,
                           StagedLeftTuples trgLeftTuples,
                           StagedLeftTuples stagedLeftTuples) {
            boolean useLeftMemory = false;
            StagedRightTuples srcRightTuples = am.getBetaMemory().getStagedRightTuples();
            
            // We need to collect which leftTuple where updated, so that we can
            // add their result tuple to the real target tuples later
            StagedLeftTuples tempLeftTuples = new StagedLeftTuples();

            if ( srcLeftTuples.getDeleteFirst() != null ) {
                // use the real target here, as dealing direct with left tuples
                doLeftDeletes( accNode, am, wm, srcLeftTuples, trgLeftTuples );
            }
            
            if ( srcRightTuples.getDeleteFirst() != null ) {
                doRightDeletes( accNode, am, wm, srcRightTuples, tempLeftTuples );
            }            

            if ( srcLeftTuples.getUpdateFirst() != null || srcRightTuples.getUpdateFirst() != null ) {
                dpUpdatesReorderMemory( am.getBetaMemory(),
                                        wm,
                                        srcRightTuples,
                                        srcLeftTuples );
            }
            
            if ( srcLeftTuples.getUpdateFirst() != null ) {
                doLeftUpdates( accNode, sink, am, wm, srcLeftTuples, tempLeftTuples );
            }            

            if ( srcRightTuples.getUpdateFirst() != null ) {
                doRightUpdates( accNode, sink, am, wm, srcRightTuples, tempLeftTuples );
            }

            if ( srcRightTuples.getInsertFirst() != null ) {
                doRightInserts( accNode, sink, am, wm, srcRightTuples, tempLeftTuples );
            }

            if ( srcLeftTuples.getInsertFirst() != null ) {
                doLeftInserts( accNode, sink, am, wm, srcLeftTuples, tempLeftTuples );
            }
            
            Accumulate accumulate = accNode.getAccumulate();
            // we do not need collect retracts. RightTuple retracts end up as updates for lefttuples. 
            // LeftTuple retracts are already on the trgLeftTuples 
            for ( LeftTuple leftTuple = tempLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                evaluateResultConstraints( accNode, sink, accumulate, leftTuple, leftTuple.getPropagationContext(), 
                                           wm, am, (AccumulateContext) leftTuple.getObject(), useLeftMemory, 
                                           trgLeftTuples, stagedLeftTuples );
                leftTuple.clearStaged();
                leftTuple = next;                
            }
            
            for ( LeftTuple leftTuple = tempLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                evaluateResultConstraints( accNode, sink, accumulate, leftTuple, leftTuple.getPropagationContext(), 
                                           wm, am, (AccumulateContext) leftTuple.getObject(), useLeftMemory, 
                                           trgLeftTuples, stagedLeftTuples );
                leftTuple.clearStaged();
                leftTuple = next;                  
            }            
            
            srcRightTuples.setInsert( null, 0 );
            srcRightTuples.setDelete( null );
            srcRightTuples.setUpdate( null );             
            
            srcLeftTuples.setInsert( null, 0 );
            srcLeftTuples.setDelete( null );
            srcLeftTuples.setUpdate( null );            
        }

        public void doLeftInserts(AccumulateNode accNode,
                                  LeftTupleSink sink,
                                  AccumulateMemory am,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;

            Accumulate accumulate = accNode.getAccumulate();            
            BetaMemory bm = am.getBetaMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = accNode.getRawConstraints();
            FastIterator it = accNode.getRightIterator( rtm );

            for ( LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                PropagationContext context = leftTuple.getPropagationContext();
                
                AccumulateContext accresult = new AccumulateContext();

                boolean useLeftMemory = true;
                if ( !tupleMemoryEnabled ) {
                    // This is a hack, to not add closed DroolsQuery objects
                    Object object = ((InternalFactHandle) leftTuple.get( 0 )).getObject();
                    if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                        useLeftMemory = false;
                    }
                }

                if ( useLeftMemory ) {
                    ltm.add( leftTuple );
                    leftTuple.setObject( accresult );
                }         
                
                accresult.context = accumulate.createContext();

                accumulate.init( am.workingMemoryContext,
                                 accresult.context,
                                 leftTuple,
                                 wm );

                constraints.updateFromTuple( contextEntry,
                                             wm,
                                             leftTuple );

                FastIterator rightIt = accNode.getRightIterator( rtm );

                for ( RightTuple rightTuple = accNode.getFirstRightTuple( leftTuple,
                                                                          rtm,
                                                                          context,
                                                                          rightIt ); rightTuple != null; rightTuple = (RightTuple) rightIt.next( rightTuple ) ) {
                    InternalFactHandle handle = rightTuple.getFactHandle();
                    if ( constraints.isAllowedCachedLeft( contextEntry,
                                                          handle ) ) {
                        // add a match
                        addMatch( accNode,
                                  accumulate,
                                  leftTuple,
                                  rightTuple,
                                  null,
                                  null,
                                  wm,
                                  am,
                                  accresult,
                                  useLeftMemory );
                    }
                }

                leftTuple.clearStaged();
                trgLeftTuples.addInsert( leftTuple );
                
                constraints.resetTuple( contextEntry );              
                
                leftTuple = next;
            }
            srcLeftTuples.setInsert( null, 0 );
            constraints.resetTuple( contextEntry );
        }

        public void doRightInserts(AccumulateNode accNode,
                                   LeftTupleSink sink,
                                   AccumulateMemory am,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            boolean tupleMemoryEnabled = true;


            Accumulate accumulate = accNode.getAccumulate();            

            BetaMemory bm = am.getBetaMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = accNode.getRawConstraints();
            FastIterator it = accNode.getLeftIterator( ltm );

            for ( RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                rtm.add( rightTuple );
                PropagationContext context = rightTuple.getPropagationContext();

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );

                FastIterator leftIt = accNode.getLeftIterator( ltm );
                
                for ( LeftTuple leftTuple = accNode.getFirstLeftTuple( rightTuple, ltm, context, leftIt ); leftTuple != null; leftTuple = (LeftTuple) leftIt.next( leftTuple ) ) {
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                        addMatch( accNode,
                                  accumulate,
                                  leftTuple,
                                  rightTuple,
                                  null,
                                  null,
                                  wm,
                                  am,
                                  accctx,
                                  tupleMemoryEnabled );
                        
                        // right inserts and updates are done first
                        // so any existing leftTuples we know are updates, but only add if not already added
                        if ( leftTuple.getStagedType() == LeftTuple.NONE ) {
                            trgLeftTuples.addUpdate( leftTuple );
                        }
                        
                    }
                }

                rightTuple.clearStaged();
                rightTuple = next;
            }
            srcRightTuples.setInsert( null, 0 );
            constraints.resetFactHandle( contextEntry );
        }

        public void doLeftUpdates(AccumulateNode accNode,
                                  LeftTupleSink sink,
                                  AccumulateMemory am,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            
            BetaMemory bm = am.getBetaMemory();
            RightTupleMemory rtm = bm.getRightTupleMemory();            
            Accumulate accumulate = accNode.getAccumulate();                        
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = accNode.getRawConstraints();
            FastIterator rightIt = accNode.getRightIterator( rtm );

            for ( LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                PropagationContext context = leftTuple.getPropagationContext();

                constraints.updateFromTuple( contextEntry,
                                             wm,
                                             leftTuple );

                RightTuple rightTuple = accNode.getFirstRightTuple( leftTuple,
                                                                     rtm,
                                                                     context,
                                                                     rightIt );

                LeftTuple childLeftTuple = leftTuple.getFirstChild();

                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
                // if rightTuple is null, we assume there was a bucket change and that bucket is empty
                if ( childLeftTuple != null && rtm.isIndexed() && !rightIt.isFullIterator() &&  (rightTuple == null || (rightTuple.getMemory() !=  childLeftTuple.getRightParent().getMemory())) ) {
                    // our index has changed, so delete all the previous matchings
                    removePreviousMatchesForLeftTuple( accNode,
                                                       accumulate,
                                                       leftTuple,
                                                       wm,
                                                       am,
                                                       accctx );

                    childLeftTuple = null; // null so the next check will attempt matches for new bucket
                }

                // we can't do anything if RightTupleMemory is empty
                if ( rightTuple != null ) {
                    doLeftUpdatesProcessChildren( accNode,
                                                  am,
                                                  wm,
                                                  bm,
                                                  accumulate,
                                                  constraints,
                                                  rightIt,
                                                  leftTuple,
                                                  accctx,
                                                  rightTuple,
                                                  childLeftTuple );
                }
                
                leftTuple.clearStaged();
                trgLeftTuples.addUpdate( leftTuple );             
                
                leftTuple = next;
            }
            srcLeftTuples.setUpdate( null );
            constraints.resetTuple( contextEntry );
        }

        private void doLeftUpdatesProcessChildren(AccumulateNode accNode,
                                                  AccumulateMemory am,
                                                  InternalWorkingMemory wm,
                                                  BetaMemory bm,
                                                  Accumulate accumulate,
                                                  BetaConstraints constraints,
                                                  FastIterator rightIt,
                                                  LeftTuple leftTuple,
                                                  final AccumulateContext accctx,
                                                  RightTuple rightTuple,
                                                  LeftTuple childLeftTuple) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; rightTuple != null; rightTuple = (RightTuple) rightIt.next( rightTuple ) ) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();
                    if ( constraints.isAllowedCachedLeft( bm.getContext(),
                                                          handle ) ) {
                        // add a new match
                        addMatch( accNode,
                                  accumulate,
                                  leftTuple,
                                  rightTuple,
                                  null,
                                  null,
                                  wm,
                                  am,
                                  accctx,
                                  true );
                    }
                }
            } else {
                boolean isDirty = false;
                // in the same bucket, so iterate and compare
                for ( ; rightTuple != null; rightTuple = (RightTuple) rightIt.next( rightTuple ) ) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();

                    if ( constraints.isAllowedCachedLeft( bm.getContext(),
                                                         handle ) ) {
                        if ( childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple ) {
                            // add a new match
                            addMatch( accNode,
                                      accumulate,
                                      leftTuple,
                                      rightTuple,
                                      childLeftTuple,
                                      null,
                                      wm,
                                      am,
                                      accctx,
                                      true );
                        } else {
                            // we must re-add this to ensure deterministic iteration
                            LeftTuple temp = childLeftTuple.getLeftParentNext();
                            childLeftTuple.reAddRight();
                            childLeftTuple = temp;
                        }
                    } else if ( childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple ) {
                        LeftTuple temp = childLeftTuple.getLeftParentNext();
                        // remove the match
                        removeMatch( accNode,
                                     accumulate,
                                     rightTuple,
                                     childLeftTuple,
                                     wm,
                                     am,
                                     accctx,
                                     false );
                        childLeftTuple = temp;
                        // the next line means that when a match is removed from the current leftTuple
                        // and the accumulate does not support the reverse operation, then the whole
                        // result is dirty (since removeMatch above is not recalculating the total)
                        // and we need to do this later
                        isDirty = !accumulate.supportsReverse();
                    }
                    // else do nothing, was false before and false now.
                }
                if ( isDirty ) {
                    reaccumulateForLeftTuple( accNode,
                                              accumulate,
                                              leftTuple,
                                              wm,
                                              am,
                                              accctx );
                }
            }
        }

        public void doRightUpdates(AccumulateNode accNode,
                                   LeftTupleSink sink,
                                   AccumulateMemory am,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples trgLeftTuples) {
            boolean tupleMemory = true;
            
            BetaMemory bm = am.getBetaMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            BetaConstraints constraints = accNode.getRawConstraints();
            Accumulate accumulate = accNode.getAccumulate();
            FastIterator leftIt = accNode.getLeftIterator( ltm );

            for ( RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                PropagationContext context = rightTuple.getPropagationContext();

                LeftTuple childLeftTuple = rightTuple.getFirstChild();

                LeftTuple leftTuple = accNode.getFirstLeftTuple( rightTuple, ltm, context, leftIt );

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );

                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
                // We assume a bucket change if leftTuple == null
                if ( childLeftTuple != null && ltm.isIndexed() && !leftIt.isFullIterator() && (leftTuple == null || (leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory())) ) {
                    // our index has changed, so delete all the previous matches
                    removePreviousMatchesForRightTuple( accNode,
                                                        accumulate,
                                                        rightTuple,
                                                        context,
                                                        wm,
                                                        am,
                                                        childLeftTuple );
                    childLeftTuple = null; // null so the next check will attempt matches for new bucket
                }

                // if LeftTupleMemory is empty, there are no matches to modify
                if ( leftTuple != null ) {
                    if ( leftTuple.getStagedType() == LeftTuple.NONE ) {
                        trgLeftTuples.addUpdate( leftTuple );
                    }
                    
                    doRightUpdatesProcessChildren( accNode,
                                                   am,
                                                   wm,
                                                   bm,
                                                   constraints,
                                                   accumulate,
                                                   leftIt,
                                                   rightTuple,
                                                   childLeftTuple,
                                                   leftTuple );
                }
                
                rightTuple.clearStaged();
                rightTuple = next;
            }
            srcRightTuples.setUpdate( null );
            constraints.resetFactHandle( contextEntry );
        }

        private void doRightUpdatesProcessChildren(AccumulateNode accNode,
                                                   AccumulateMemory am,
                                                   InternalWorkingMemory wm,
                                                   BetaMemory bm,
                                                   BetaConstraints constraints,
                                                   Accumulate accumulate,
                                                   FastIterator leftIt,
                                                   RightTuple rightTuple,
                                                   LeftTuple childLeftTuple,
                                                   LeftTuple leftTuple) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; leftTuple != null; leftTuple = ( LeftTuple ) leftIt.next( leftTuple ) ) {
                    if (constraints.isAllowedCachedRight( bm.getContext(),
                                                          leftTuple ) ) {
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                        // add a new match
                        addMatch( accNode,
                                  accumulate,
                                  leftTuple,
                                  rightTuple,
                                  null,
                                  null,
                                  wm,
                                  am,
                                  accctx,
                                  true );
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; leftTuple != null; leftTuple = (LeftTuple) leftIt.next( leftTuple ) ) {
                    if ( constraints.isAllowedCachedRight( bm.getContext(),
                                                                leftTuple ) ) {
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                        LeftTuple temp = null;
                        if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {
                            temp = childLeftTuple.getRightParentNext();
                            // we must re-add this to ensure deterministic iteration
                            childLeftTuple.reAddLeft();
                            removeMatch( accNode,
                                         accumulate,
                                         rightTuple,
                                         childLeftTuple,
                                         wm,
                                         am,
                                         accctx,
                                         true );
                            childLeftTuple = temp;
                        }
                        // add a new match
                        addMatch( accNode,
                                  accumulate,
                                  leftTuple,
                                  rightTuple,
                                  null,
                                  childLeftTuple,
                                  wm,
                                  am,
                                  accctx,
                                  true );
                        if ( temp != null ) {
                            childLeftTuple = temp;
                        }
                    } else if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {

                        LeftTuple temp = childLeftTuple.getRightParentNext();
                        final AccumulateContext accctx = (AccumulateContext)leftTuple.getObject();
                        // remove the match
                        removeMatch( accNode,
                                     accumulate,
                                     rightTuple,
                                     childLeftTuple,
                                     wm,
                                     am,
                                     accctx,
                                     true );

                        childLeftTuple = temp;
                    }
                    // else do nothing, was false before and false now.
                }
            }
        }


        public void doLeftDeletes(AccumulateNode accNode,
                                  AccumulateMemory am,
                                  InternalWorkingMemory wm,
                                  StagedLeftTuples srcLeftTuples,
                                  StagedLeftTuples trgLeftTuples) {
            BetaMemory bm = am.getBetaMemory();
            LeftTupleMemory ltm = bm.getLeftTupleMemory();
            ContextEntry[] contextEntry = bm.getContext();
            Accumulate accumulate = accNode.getAccumulate();
            
            for ( LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();
                if ( leftTuple.getMemory() != null ) {
                    // it may have been staged and never actually added
                    ltm.remove( leftTuple );
    
                
                    final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                    leftTuple.setObject( null );
    
                    removePreviousMatchesForLeftTuple( accNode,
                                                       accumulate,
                                                       leftTuple,
                                                       wm,
                                                       am,
                                                       accctx );
    
                    if ( accctx.propagated ) {
                        trgLeftTuples.addDelete( accctx.resultLeftTuple );
                    } else {
                        // if not propagated, just destroy the result fact handle
                        // workingMemory.getFactHandleFactory().destroyFactHandle( accctx.result.getFactHandle() );
                    }
                }
                
                leftTuple.clearStaged();
                leftTuple = next;
            }
            srcLeftTuples.setDelete( null );
        }

        public void doRightDeletes(AccumulateNode accNod,
                                   AccumulateMemory am,
                                   InternalWorkingMemory wm,
                                   StagedRightTuples srcRightTuples,
                                   StagedLeftTuples trgLeftTuples) {
            RightTupleMemory rtm = am.getBetaMemory().getRightTupleMemory();

            for ( RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
                RightTuple next = rightTuple.getStagedNext();
                if (  rightTuple.getMemory() != null ) {
                    // it may have been staged and never actually added
                    rtm.remove( rightTuple );

                    if ( rightTuple.getFirstChild() != null ) {
                        LeftTuple childLeftTuple = rightTuple.getFirstChild();
    
                        while ( childLeftTuple != null ) {
                            LeftTuple nextLeft = childLeftTuple.getRightParentNext();;
                            childLeftTuple.unlinkFromLeftParent();      
                            
                            if ( childLeftTuple.getStagedType() == LeftTuple.NONE ) {
                                trgLeftTuples.addUpdate( childLeftTuple );
                            }
                            
                            childLeftTuple = nextLeft; 
                        }
                        
                    }
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
            srcRightTuples.setDelete( null );
        }        

        public void evaluateResultConstraints( final AccumulateNode accNode,
                                               final LeftTupleSink sink,
                                               final Accumulate accumulate,
                                               final LeftTuple leftTuple,
                                               final PropagationContext context,
                                               final InternalWorkingMemory workingMemory,
                                               final AccumulateMemory memory,
                                               final AccumulateContext accctx,
                                               final boolean useLeftMemory,
                                               final StagedLeftTuples trgLeftTuples, 
                                               final StagedLeftTuples stagedLeftTuples ) {
            // get the actual result
            final Object[] resultArray = accumulate.getResult( memory.workingMemoryContext,
                                                               accctx.context,
                                                               leftTuple,
                                                               workingMemory );
            Object result = accumulate.isMultiFunction() ? resultArray : resultArray[0];
            if (result == null) {
                return;
            }

            if ( accctx.getResultFactHandle() == null ) {
                final InternalFactHandle handle = accNode.createResultFactHandle( context, 
                                                                                  workingMemory, 
                                                                                  leftTuple,
                                                                                  result );

                accctx.setResultFactHandle( handle );
                
                accctx.setResultLeftTuple( accNode.createLeftTuple( handle, leftTuple, sink )  );
            } else {
                accctx.getResultFactHandle().setObject( result );
            }

            // First alpha node filters
            AlphaNodeFieldConstraint[] resultConstraints = accNode.getResultConstraints();
            BetaConstraints resultBinder = accNode.getResultBinder();
            boolean isAllowed = result != null;
            for ( int i = 0, length = resultConstraints.length; isAllowed && i < length; i++ ) {
                if ( !resultConstraints[i].isAllowed( accctx.resultFactHandle,
                                                      workingMemory,
                                                      memory.alphaContexts[i] ) ) {
                    isAllowed = false;
                }
            }
            if ( isAllowed ) {
                resultBinder.updateFromTuple( memory.resultsContext,
                                              workingMemory,
                                              leftTuple );
                if ( !resultBinder.isAllowedCachedLeft( memory.resultsContext,
                                                        accctx.getResultFactHandle() ) ) {
                    isAllowed = false;
                }
                resultBinder.resetTuple( memory.resultsContext );
            }

            
            LeftTuple childLeftTuple = ( LeftTuple ) accctx.getResultLeftTuple();
            childLeftTuple.setPropagationContext( leftTuple.getPropagationContext() );
            if ( accctx.propagated == true ) {
                switch ( childLeftTuple.getStagedType() ) {
                    // handle clash with already staged entries
                    case LeftTuple.INSERT :
                        stagedLeftTuples.removeInsert( childLeftTuple );
                        break;
                    case LeftTuple.UPDATE :
                        stagedLeftTuples.removeUpdate( childLeftTuple );
                        break;
                }   
                
                if ( isAllowed ) {
                    // modify 
                    trgLeftTuples.addUpdate( childLeftTuple);
                } else {
                    // retract                 
                    trgLeftTuples.addDelete( childLeftTuple );
                    accctx.propagated = false;
                }
            } else if ( isAllowed ) {
                // assert
                trgLeftTuples.addInsert( childLeftTuple );
                accctx.propagated = true;
            }

        }        
        
        public  static void addMatch( final AccumulateNode accNode,
                                      final Accumulate accumulate,
                                      final LeftTuple leftTuple,
                                      final RightTuple rightTuple,
                                      final LeftTuple currentLeftChild,
                                      final LeftTuple currentRightChild,
                                      final InternalWorkingMemory wm,
                                      final AccumulateMemory am,
                                      final AccumulateContext accresult,
                                      final boolean useLeftMemory ) {
            LeftTuple tuple = leftTuple;
            InternalFactHandle handle = rightTuple.getFactHandle();
            if ( accNode.isUnwrapRightObject() ) {
                // if there is a subnetwork, handle must be unwrapped
                tuple = (LeftTuple) handle.getObject();
                //handle = tuple.getLastHandle();
            }
            accumulate.accumulate( am.workingMemoryContext,
                                   accresult.context,
                                   tuple,
                                   handle,
                                   wm );

            // in sequential mode, we don't need to keep record of matched tuples
            if ( useLeftMemory ) {
                // linking left and right by creating a new left tuple
                accNode.createLeftTuple( leftTuple,
                                         rightTuple,
                                         currentLeftChild,
                                         currentRightChild,
                                         accNode,
                                         true );
            }
        }
        
        /**
         * Removes a match between left and right tuple
         *
         * @param rightTuple
         * @param match
         * @param result
         */
        public static void removeMatch( final AccumulateNode accNode,
                                         final Accumulate accumulate,
                                         final RightTuple rightTuple,
                                         final LeftTuple match,
                                         final InternalWorkingMemory wm,
                                         final AccumulateMemory am,
                                         final AccumulateContext accctx,
                                         final boolean reaccumulate ) {
            // save the matching tuple
            LeftTuple leftTuple = match.getLeftParent();

            // removing link between left and right
            match.unlinkFromLeftParent();
            match.unlinkFromRightParent();

            // if there is a subnetwork, we need to unwrap the object from inside the tuple
            InternalFactHandle handle = rightTuple.getFactHandle();
            LeftTuple tuple = leftTuple;
            if ( accNode.isUnwrapRightObject() ) {
                tuple = (LeftTuple) handle.getObject();
            }

            if ( accumulate.supportsReverse() ) {
                // just reverse this single match
                accumulate.reverse( am.workingMemoryContext,
                                         accctx.context,
                                         tuple,
                                         handle,
                                         wm );
            } else {
                // otherwise need to recalculate all matches for the given leftTuple
                if ( reaccumulate ) {
                    reaccumulateForLeftTuple( accNode,
                                              accumulate,
                                              leftTuple,
                                              wm,
                                              am,
                                              accctx );

                }
            }
        }        
        

        public static void reaccumulateForLeftTuple( final AccumulateNode accNode,
                                                     final Accumulate accumulate,
                                                     final LeftTuple leftTuple,
                                                     final InternalWorkingMemory wm,
                                                     final AccumulateMemory am,
                                                     final AccumulateContext accctx ) {
            accumulate.init( am.workingMemoryContext,
                             accctx.context,
                             leftTuple,
                             wm );
            for ( LeftTuple childMatch = leftTuple.getFirstChild(); childMatch != null; childMatch = childMatch.getLeftParentNext() ) {
                InternalFactHandle childHandle = childMatch.getRightParent().getFactHandle();
                LeftTuple tuple = leftTuple;
                if ( accNode.isUnwrapRightObject() ) {
                    tuple = (LeftTuple) childHandle.getObject();
                    childHandle = tuple.getLastHandle();
                }
                accumulate.accumulate( am.workingMemoryContext,
                                       accctx.context,
                                       tuple,
                                       childHandle,
                                       wm );
            }
        }        
        
        public static void removePreviousMatchesForRightTuple( final AccumulateNode accNode,
                                                               final Accumulate accumulate,
                                                               final RightTuple rightTuple,
                                                               final PropagationContext context,
                                                               final InternalWorkingMemory workingMemory,
                                                               final AccumulateMemory memory,
                                                               final LeftTuple firstChild ) {
           for ( LeftTuple match = firstChild; match != null; ) {
               final LeftTuple next = match.getRightParentNext();
               
               final LeftTuple parent = match.getLeftParent();
               final AccumulateContext accctx = (AccumulateContext) parent.getObject();
               removeMatch( accNode,
                            accumulate,
                            rightTuple,
                            match,
                            workingMemory,
                            memory,
                            accctx,
                            true );    
               
               match = next;
           }
       }  
        
        public static void removePreviousMatchesForLeftTuple( final AccumulateNode accNode,
                                                              final Accumulate accumulate,
                                                              final LeftTuple leftTuple,
                                                              final InternalWorkingMemory workingMemory,
                                                              final AccumulateMemory memory,
                                                              final AccumulateContext accctx ) {
            for ( LeftTuple match = leftTuple.getFirstChild(); match != null;  ) {
                LeftTuple next = match.getLeftParentNext();
                match.unlinkFromRightParent();
                match.unlinkFromLeftParent();
                match = next;
            }
            // since there are no more matches, the following call will just re-initialize the accumulation
            accumulate.init( memory.workingMemoryContext,
                             accctx.context,
                             leftTuple,
                             workingMemory );
        }
        

//        /**
//         * Removes a match between left and right tuple
//         *
//         * @param rightTuple
//         * @param match
//         * @param result
//         */
//        private static void removeMatch( final RightTuple rightTuple,
//                                  final LeftTuple match,
//                                  final InternalWorkingMemory workingMemory,
//                                  final AccumulateMemory memory,
//                                  final AccumulateContext accctx,
//                                  final boolean reaccumulate ) {
//            // save the matching tuple
//            LeftTuple leftTuple = match.getLeftParent();
//
//            if ( match != null ) {
//                // removing link between left and right
//                match.unlinkFromLeftParent();
//                match.unlinkFromRightParent();
//            }
//
//            // if there is a subnetwork, we need to unwrap the object from inside the tuple
//            InternalFactHandle handle = rightTuple.getFactHandle();
//            LeftTuple tuple = leftTuple;
//            if ( this.unwrapRightObject ) {
//                tuple = (LeftTuple) handle.getObject();
//                //handle = tuple.getLastHandle();
//            }
//
//            if ( this.accumulate.supportsReverse() ) {
//                // just reverse this single match
//                this.accumulate.reverse( memory.workingMemoryContext,
//                                         accctx.context,
//                                         tuple,
//                                         handle,
//                                         workingMemory );
//            } else {
//                // otherwise need to recalculate all matches for the given leftTuple
//                if ( reaccumulate ) {
//                    reaccumulateForLeftTuple( leftTuple,
//                                              workingMemory,
//                                              memory,
//                                              accctx );
//
//                }
//            }
//        }
//
//        private static void reaccumulateForLeftTuple( final LeftTuple leftTuple,
//                                               final InternalWorkingMemory workingMemory,
//                                               final AccumulateMemory memory,
//                                               final AccumulateContext accctx ) {
//            this.accumulate.init( memory.workingMemoryContext,
//                                  accctx.context,
//                                  leftTuple,
//                                  workingMemory );
//            for ( LeftTuple childMatch = getFirstMatch( leftTuple,
//                                                        accctx,
//                                                        false ); childMatch != null; childMatch = childMatch.getLeftParentNext() ) {
//                InternalFactHandle childHandle = childMatch.getRightParent().getFactHandle();
//                LeftTuple tuple = leftTuple;
//                if ( this.unwrapRightObject ) {
//                    tuple = (LeftTuple) childHandle.getObject();
//                    childHandle = tuple.getLastHandle();
//                }
//                this.accumulate.accumulate( memory.workingMemoryContext,
//                                            accctx.context,
//                                            tuple,
//                                            childHandle,
//                                            workingMemory );
//            }
//        }
//
//        private static void removePreviousMatchesForLeftTuple( final LeftTuple leftTuple,
//                                                        final InternalWorkingMemory workingMemory,
//                                                        final AccumulateMemory memory,
//                                                        final AccumulateContext accctx ) {
//            // so we just split the list keeping the head 
//            LeftTuple[] matchings = splitList( leftTuple,
//                                               accctx,
//                                               false );
//            for ( LeftTuple match = matchings[0]; match != null; match = match.getLeftParentNext() ) {
//                // can't unlink from the left parent as it was already unlinked during the splitList call above
//                match.unlinkFromRightParent();
//            }
//            // since there are no more matches, the following call will just re-initialize the accumulation
//            this.accumulate.init( memory.workingMemoryContext,
//                                  accctx.context,
//                                  leftTuple,
//                                  workingMemory );
//        }
//
//        private static void removePreviousMatchesForRightTuple( final RightTuple rightTuple,
//                                                         final PropagationContext context,
//                                                         final InternalWorkingMemory workingMemory,
//                                                         final AccumulateMemory memory,
//                                                         final LeftTuple firstChild ) {
//            for ( LeftTuple match = firstChild; match != null; ) {
//                final LeftTuple tmp = match.getRightParentNext();
//                final LeftTuple parent = match.getLeftParent();
//                final AccumulateContext accctx = (AccumulateContext) parent.getObject();
//                removeMatch( rightTuple,
//                             match,
//                             workingMemory,
//                             memory,
//                             accctx,
//                             true );
//                if ( accctx.getAction() == null ) {
//                    // schedule a test to evaluate the constraints, this is an optimisation for sub networks
//                    // We set Source to LEFT, even though this is a right propagation, because it might end up
//                    // doing multiple right propagations anyway
//                    EvaluateResultConstraints action = new EvaluateResultConstraints( ActivitySource.LEFT,
//                                                                                      parent,
//                                                                                      context,
//                                                                                      workingMemory,
//                                                                                      memory,
//                                                                                      accctx,
//                                                                                      true,
//                                                                                      this );  
//                    accctx.setAction( action );
//                    context.addInsertAction( action );
//                }          
//                match = tmp;
//            }
//        }
//
//        protected static LeftTuple[] splitList( final LeftTuple parent,
//                                         final AccumulateContext accctx,
//                                         final boolean isUpdatingSink ) {
//            LeftTuple[] matchings = new LeftTuple[2];
//
//            // save the matchings list
//            matchings[0] = getFirstMatch( parent,
//                                          accctx,
//                                          isUpdatingSink );
//            matchings[1] = matchings[0] != null ? parent.getLastChild() : null;
//
//            // update the tuple for the actual propagations
//            if ( matchings[0] != null ) {
//                if ( parent.getFirstChild() == matchings[0] ) {
//                    parent.setFirstChild( null );
//                }
//                parent.setLastChild( matchings[0].getLeftParentPrevious() );
//                if ( parent.getLastChild() != null ) {
//                    parent.getLastChild().setLeftParentNext( null );
//                    matchings[0].setLeftParentPrevious( null );
//                }
//            }
//
//            return matchings;
//        }
//
//        private static void restoreList( final LeftTuple parent,
//                                  final LeftTuple[] matchings ) {
//            // concatenate matchings list at the end of the children list
//            if ( parent.getFirstChild() == null ) {
//                parent.setFirstChild( matchings[0] );
//                parent.setLastChild( matchings[1] );
//            } else if ( matchings[0] != null ) {
//                parent.getLastChild().setLeftParentNext( matchings[0] );
//                matchings[0].setLeftParentPrevious( parent.getLastChild() );
//                parent.setLastChild( matchings[1] );
//            }
//        }
//
//        /**
//         * Skips the propagated tuple handles and return the first handle
//         * in the list that correspond to a match
//         * 
//         * @param leftTuple
//         * @param accctx
//         * @return
//         */
//        private static LeftTuple getFirstMatch( final LeftTuple leftTuple,
//                                                final AccumulateContext accctx,
//                                                final boolean isUpdatingSink ) {
//            // unlink all right matches 
//            LeftTuple child = leftTuple.getFirstChild();
//
//            if ( accctx.propagated ) {
//                // To do that, we need to skip the first N children that are in fact
//                // the propagated tuples
//                int target = isUpdatingSink ? this.sink.size() - 1 : this.sink.size();
//                for ( int i = 0; i < target; i++ ) {
//                    child = child.getLeftParentNext();
//                }
//            }
//            return child;
//        }    
    }
    

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
                                              StagedLeftTuples srcLeftTuples) {
        LeftTupleMemory ltm = bm.getLeftTupleMemory();
        RightTupleMemory rtm = bm.getRightTupleMemory();

        // sides must first be re-ordered, to ensure iteration integrity
        for ( LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            if ( leftTuple.getMemory() != null ) {
                ltm.removeAdd( leftTuple );
                for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; ) {
                    LeftTuple childNext = childLeftTuple.getLeftParentNext();
                    childLeftTuple.reAddRight();
                    childLeftTuple = childNext;
                }
            }
            leftTuple = next;
        }

        for ( RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            if ( rightTuple.getMemory() != null ) {
                rtm.removeAdd( rightTuple );
                for ( LeftTuple childLeftTuple = rightTuple.getFirstChild(); childLeftTuple != null; ) {
                    LeftTuple childNext = childLeftTuple.getLeftParentNext();
                    childLeftTuple.reAddLeft();
                    childLeftTuple = childNext;
                }
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
