package org.drools.common;

import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.core.util.index.LeftTupleList;
import org.drools.core.util.index.RightTupleList;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RuleMemory;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.SegmentMemory;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

public class RuleNetworkEvaluatorActivation extends AgendaItem {
    
    private RuleMemory rmem;

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
        super(activationNumber, tuple, salience, context, rtn);
        this.rmem = rmem;
    }
    
    public int evaluateNetwork(InternalWorkingMemory wm) {
        return evaluateNetwork( null,  getRuleTerminalNode().getLeftTupleSource(), wm );        
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
    
    public int evaluateNetwork(LeftTupleSource startNode,
                               LeftTupleSource lt, 
                               InternalWorkingMemory wm) {
        int stagedTuplecount = 0;
        
        while ( lt != null ) {
            if ( startNode == lt ) {
                // sub network reached it's start
                return stagedTuplecount;
            }
            if ( NodeTypeEnums.isBetaNode(  lt ) ) {
                BetaNode betaNode = ( BetaNode ) lt;
                if (  betaNode.isRightInputIsRiaNode() ) {
                    RightInputAdapterNode riaNode = ( RightInputAdapterNode ) betaNode.getRightInput();
                    lt = lt.getLeftTupleSource();
                    stagedTuplecount = evaluateNetwork( lt, riaNode.getLeftTupleSource(), wm );
                    // skip this node, because rianodes always propagate.                    
                    continue;
                }
                
                BetaMemory bm;
                if ( NodeTypeEnums.AccumulateNode == lt.getType() ) {
                   bm = (( AccumulateMemory ) wm.getNodeMemory( ( AccumulateNode ) lt )).getBetaMemory(); 
                } else {
                    bm =( BetaMemory)  wm.getNodeMemory( (BetaNode) lt );                    
                }
                RightTupleList list = bm.getStagedAssertRightTupleList();
                int length = ( list.size() < 25 ) ? list.size()  : 25;

                RightTuple rightTuple = BetaNode.propagateAssertRightTuples(betaNode,list, length, wm);
                
                if ( length == 25 && rightTuple.getNext() != null ) {
                    stagedTuplecount = stagedTuplecount + list.size() - 25;
                    list.split( rightTuple, length );
                } else {
                    list.clear();
                }
            } else if (  NodeTypeEnums.LeftInputAdapterNode == lt.getType() ) {
                LiaNodeMemory lm =( LiaNodeMemory)  wm.getNodeMemory( (LeftInputAdapterNode) lt );
                LeftTupleList list = lm.getStagedLeftTupleList();
                int length = ( list.size() < 25 ) ? list.size()  : 25;

                LeftTuple leftTuple = LeftInputAdapterNode.propagateLeftTuples((LeftInputAdapterNode) lt,list, length, wm);
                
                if ( length == 25 && leftTuple.getNext() != null ) {
                    stagedTuplecount = stagedTuplecount + list.size() - 25;
                    list.split( leftTuple, length );
                } else {
                    list.clear();
                }                
            }
            lt = lt.getLeftTupleSource();
        }
        return stagedTuplecount;
    }
    
//    public void flushRetractRightTuples(BetaNode betaNode, RightTupleList list, InternalWorkingMemory wm)  ) {
//        //RightTuple rightTuple = BetaNode.propagateRightTuples(betaNode, list, wm);
//    }
    
    /**
     * Helper class used for testing purposes
     * @param wm
     */
    public static void evaluateLazyItems(WorkingMemory wm) {
        InternalWorkingMemory iwm = (InternalWorkingMemory) wm;
        Map<Rule, BaseNode[]> map = ((InternalRuleBase)iwm.getRuleBase()).getReteooBuilder().getTerminalNodes();
        for ( BaseNode[] nodes : map.values() ) {
            for ( BaseNode node : nodes) {
                RuleTerminalNode rtn = ( RuleTerminalNode ) node;
                RuleMemory rs = ( RuleMemory ) iwm.getNodeMemory( rtn );
                RuleNetworkEvaluatorActivation item = ( RuleNetworkEvaluatorActivation) rs.getAgendaItem();
                if ( item != null ) {
                    item.dequeue();
                    int count = ((RuleNetworkEvaluatorActivation)item).evaluateNetwork( iwm );
                    if ( count > 0 ) {
                        ((InternalAgenda)iwm.getAgenda()).addActivation( item );
                    }
                }
            }
         }        
    }

    
    public boolean isRuleNetworkEvaluatorActivation() {
        return true;
    }
    
}
