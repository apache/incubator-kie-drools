package org.drools.core.common;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleMemory;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.kie.api.KieBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhreakActivationIterator
    implements
    Iterator {
    private InternalWorkingMemory wm;

    private java.util.Iterator<AgendaItem> agendaItemIter;

    private LeftTuple currentLeftTuple;

    List<AgendaItem> agendaItems;

    PhreakActivationIterator() {

    }

    private PhreakActivationIterator(InternalWorkingMemory wm,
                                     KieBase kbase) {
        this.wm = wm;
        agendaItems = collectAgendaItems((InternalKnowledgeBase) kbase, wm);
        agendaItemIter =  agendaItems.iterator();
    }


    public static PhreakActivationIterator iterator(InternalWorkingMemory wm) {
        return new PhreakActivationIterator( wm,
                                             wm.getKnowledgeBase() );
    }

    public static PhreakActivationIterator iterator(StatefulKnowledgeSession ksession) {
        return new PhreakActivationIterator( ((InternalWorkingMemoryEntryPoint) ksession).getInternalWorkingMemory(),
                                             ksession.getKieBase() );
    }


    public Object next() {
        if ( agendaItemIter.hasNext() ) {
            return agendaItemIter.next();
        } else {
            return null;
        }
    }


    public static List<RuleTerminalNode> populateRuleTerminalNodes(InternalKnowledgeBase kbase, Set<RuleTerminalNode>  nodeSet) {
        Collection<BaseNode[]> nodesWithArray = ((InternalKnowledgeBase) kbase).getReteooBuilder().getTerminalNodes().values();

        for (BaseNode[] nodeArray : nodesWithArray) {
            for (BaseNode node : nodeArray) {
                if (node.getType() == NodeTypeEnums.RuleTerminalNode) {
                    nodeSet.add((RuleTerminalNode) node);
                }
            }
        }

        return Arrays.asList(nodeSet.toArray(new RuleTerminalNode[nodeSet.size()]));
    }

    public static List<AgendaItem> collectAgendaItems(InternalKnowledgeBase kbase, InternalWorkingMemory wm) {
        Set<RuleTerminalNode> nodeSet = new HashSet<RuleTerminalNode>();
        List<RuleTerminalNode> nodeList = populateRuleTerminalNodes(kbase, nodeSet);

        List<AgendaItem> agendaItems = new ArrayList<AgendaItem>();
        for ( RuleTerminalNode rtn : nodeList ) {
            if ( !nodeSet.contains(rtn) ) {
                // this node has already been processed
                continue;
            }
            processLeftTuples( rtn.getLeftTupleSource(), agendaItems, nodeSet, wm);
        }
        return agendaItems;
    }

    public static void processLeftTuples(LeftTupleSource node, List<AgendaItem> agendaItems, Set<RuleTerminalNode> nodeSet, InternalWorkingMemory wm) {
        LeftTupleSource node1 = node;
        while (NodeTypeEnums.LeftInputAdapterNode != node1.getType()) {
            node1 = node1.getLeftTupleSource();
        }
        int maxShareCount = node1.getAssociations().size();

        while (NodeTypeEnums.LeftInputAdapterNode != node.getType()) {
            Memory memory = wm.getNodeMemory((MemoryFactory) node);
            if (memory.getSegmentMemory() == null) {
                // segment has never been initialized, which means the rule has never been linked.
                return;
            }
            if ( node.getAssociations().size() == maxShareCount ) {
                // the recurse must start from the first split node, otherwise we get partial overlaps in propagations
                if (NodeTypeEnums.isBetaNode(node)) {
                    BetaMemory bm;
                    if (NodeTypeEnums.AccumulateNode == node.getType()) {
                        AccumulateMemory am = (AccumulateMemory) memory;
                        bm = am.getBetaMemory();
                        FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                        LeftTuple lt = ((BetaNode) node).getFirstLeftTuple(bm.getLeftTupleMemory(), it);
                        for (; lt != null; lt = (LeftTuple) it.next(lt)) {
                            AccumulateContext accctx = (AccumulateContext) lt.getObject();
                            collectFromPeers(accctx.getResultLeftTuple(), agendaItems, nodeSet, wm);
                        }
                    } else if ( NodeTypeEnums.ExistsNode == node.getType() ) {
                        bm = (BetaMemory) wm.getNodeMemory((MemoryFactory) node);
                        FastIterator it = bm.getRightTupleMemory().fullFastIterator(); // done off the RightTupleMemory, as exists only have unblocked tuples on the left side
                        RightTuple rt = ((BetaNode) node).getFirstRightTuple(bm.getRightTupleMemory(), it);
                        for (; rt != null; rt = (RightTuple) it.next(rt)) {
                            for ( LeftTuple lt = rt.getBlocked(); lt != null; lt = lt.getBlockedNext() ) {
                                if ( lt.getFirstChild() != null ) {
                                    collectFromPeers(lt.getFirstChild(), agendaItems, nodeSet, wm);
                                }
                            }
                        }
                    } else {
                        bm = (BetaMemory) wm.getNodeMemory((MemoryFactory) node);
                        FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                        LeftTuple lt = ((BetaNode) node).getFirstLeftTuple(bm.getLeftTupleMemory(), it);
                        for (; lt != null; lt = (LeftTuple) it.next(lt)) {
                            if ( lt.getFirstChild() != null ) {
                                collectFromLeftInput(lt.getFirstChild(), agendaItems, nodeSet, wm);
                            }
                        }
                    }
                    return;
                } else if (NodeTypeEnums.FromNode == node.getType()) {
                    FromMemory fm = (FromMemory) wm.getNodeMemory((MemoryFactory) node);
                    LeftTupleMemory ltm = fm.getBetaMemory().getLeftTupleMemory();
                    FastIterator it = ltm.fullFastIterator();
                    for (LeftTuple lt = ltm.getFirst(null); lt != null; lt = (LeftTuple) it.next(lt)) {
                        if ( lt.getFirstChild() != null ) {
                            collectFromLeftInput(lt.getFirstChild(), agendaItems, nodeSet, wm);
                        }
                    }
                    return;
                }
            }
            node = node.getLeftTupleSource();
        }

        // No beta or from nodes, so must retrieve LeftTuples from the LiaNode.
        // This is done by scanning all the LeftTuples referenced from the FactHandles in the ObjectTypeNode
        LeftInputAdapterNode lian = (LeftInputAdapterNode) node;
        Memory memory = wm.getNodeMemory((MemoryFactory) node);
        if (memory.getSegmentMemory() == null) {
            // segment has never been initialized, which means the rule has never been linked.
            return;
        }

        ObjectSource os = lian.getObjectSource();
        while (os.getType() != NodeTypeEnums.ObjectTypeNode) {
            os = os.getParentObjectSource();
        }
        ObjectTypeNode otn = (ObjectTypeNode) os;
        final ObjectTypeNodeMemory omem = (ObjectTypeNodeMemory) wm.getNodeMemory(otn);
        LeftTupleSink firstLiaSink = lian.getSinkPropagator().getFirstLeftTupleSink();

        java.util.Iterator<InternalFactHandle> it = omem.iterator();
        while (it.hasNext()) {
            InternalFactHandle fh = it.next();
            if (fh.getFirstLeftTuple() != null ) {
                for (LeftTuple childLt = fh.getFirstLeftTuple(); childLt != null; childLt = childLt.getLeftParentNext()) {
                    if ( childLt.getSink() == firstLiaSink ) {
                        collectFromLeftInput(childLt, agendaItems, nodeSet, wm);
                    }
                }
            }
        }
    }

    private static void collectFromLeftInput(LeftTuple lt, List<AgendaItem> agendaItems, Set<RuleTerminalNode> nodeSet, InternalWorkingMemory wm) {
        for (; lt != null; lt = lt.getLeftParentNext()) {
            collectFromPeers(lt, agendaItems, nodeSet, wm);
        }
    }

    private static void collectFromPeers(LeftTuple peer, List<AgendaItem> agendaItems, Set<RuleTerminalNode> nodeSet, InternalWorkingMemory wm) {
        while (peer != null) {
            if ( peer.getLeftTupleSink().getType() == NodeTypeEnums.AccumulateNode ) {
                AccumulateContext accctx = (AccumulateContext) peer.getObject();
                if (accctx != null) {
                    // the accumulate context can be null if the lefttuple hasn't been evaluated yet
                    collectFromLeftInput(accctx.getResultLeftTuple(), agendaItems, nodeSet, wm);
                }
            } else if ( peer.getFirstChild() != null ) {
                for (LeftTuple childLt = peer.getFirstChild(); childLt != null; childLt = childLt.getLeftParentNext()) {
                    collectFromLeftInput(childLt, agendaItems, nodeSet, wm);
                }
            } else if ( peer.getLeftTupleSink().getType() == NodeTypeEnums.RuleTerminalNode ) {
                agendaItems.add((AgendaItem) peer);
                nodeSet.remove(peer.getLeftTupleSink()); // remove this RuleTerminalNode, as we know we've visited it already
            }
            peer = peer.getPeer();
        }
    }

}
