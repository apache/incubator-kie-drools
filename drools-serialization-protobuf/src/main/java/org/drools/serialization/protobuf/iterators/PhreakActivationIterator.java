/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.serialization.protobuf.iterators;

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.*;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;

import java.util.*;

public class PhreakActivationIterator
    implements
    Iterator {

    private java.util.Iterator<InternalMatch> agendaItemIter;

    List<InternalMatch> internalMatches;

    PhreakActivationIterator() {

    }

    private PhreakActivationIterator(ReteEvaluator reteEvaluator,
                                     InternalRuleBase kbase) {
        internalMatches = collectAgendaItems(kbase, reteEvaluator);
        agendaItemIter =  internalMatches.iterator();
    }

    public static PhreakActivationIterator iterator(ReteEvaluator reteEvaluator) {
        return new PhreakActivationIterator( reteEvaluator, reteEvaluator.getKnowledgeBase() );
    }

    public Object next() {
        if ( agendaItemIter.hasNext() ) {
            return agendaItemIter.next();
        } else {
            return null;
        }
    }

    public static List<RuleTerminalNode> populateRuleTerminalNodes(InternalRuleBase kbase, Set<RuleTerminalNode>  nodeSet) {
        Collection<TerminalNode[]> nodesWithArray = kbase.getReteooBuilder().getTerminalNodes().values();

        for (TerminalNode[] nodeArray : nodesWithArray) {
            for (TerminalNode node : nodeArray) {
                if (node.getType() == NodeTypeEnums.RuleTerminalNode) {
                    nodeSet.add((RuleTerminalNode) node);
                }
            }
        }

        return Arrays.asList(nodeSet.toArray(new RuleTerminalNode[nodeSet.size()]));
    }

    public static List<InternalMatch> collectAgendaItems(InternalRuleBase kbase, ReteEvaluator reteEvaluator) {
        Set<RuleTerminalNode> nodeSet = new HashSet<>();
        List<RuleTerminalNode> nodeList = populateRuleTerminalNodes(kbase, nodeSet);

        List<InternalMatch> internalMatches = new ArrayList<>();
        for ( RuleTerminalNode rtn : nodeList ) {
            if ( !nodeSet.contains(rtn) ) {
                // this node has already been processed
                continue;
            }
            processLeftTuples(rtn.getLeftTupleSource(), internalMatches, nodeSet, reteEvaluator);
        }
        return internalMatches;
    }

    public static void processLeftTuples(LeftTupleSource node, List<InternalMatch> internalMatches, Set<RuleTerminalNode> nodeSet, ReteEvaluator reteEvaluator) {
        LeftTupleSource node1 = node;
        while (NodeTypeEnums.LeftInputAdapterNode != node1.getType()) {
            node1 = node1.getLeftTupleSource();
        }
        int maxShareCount = node1.getAssociationsSize();

        while (NodeTypeEnums.LeftInputAdapterNode != node.getType()) {
            Memory memory = reteEvaluator.getNodeMemories().peekNodeMemory(node);
            if (memory == null || memory.getSegmentMemory() == null) {
                // segment has never been initialized, which means the rule has never been linked.
                return;
            }
            if ( node.getAssociationsSize() == maxShareCount ) {
                // the recurse must start from the first split node, otherwise we get partial overlaps in propagations
                if (NodeTypeEnums.isBetaNode(node)) {
                    BetaMemory bm;
                    if (NodeTypeEnums.AccumulateNode == node.getType()) {
                        AccumulateMemory am = (AccumulateMemory) memory;
                        bm = am.getBetaMemory();
                        FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                        Tuple lt = BetaNode.getFirstTuple( bm.getLeftTupleMemory(), it );
                        for (; lt != null; lt = (LeftTuple) it.next(lt)) {
                            AccumulateContext accctx = (AccumulateContext) lt.getContextObject();
                            collectFromPeers((LeftTuple) accctx.getResultLeftTuple(), internalMatches, nodeSet, reteEvaluator);
                        }
                    } else if ( NodeTypeEnums.ExistsNode == node.getType() ) {
                        bm = (BetaMemory) reteEvaluator.getNodeMemories().peekNodeMemory(node);
                        if (bm != null) {
                            bm = (BetaMemory) reteEvaluator.getNodeMemories().peekNodeMemory(node);
                            FastIterator it = bm.getRightTupleMemory().fullFastIterator(); // done off the RightTupleMemory, as exists only have unblocked tuples on the left side
                            RightTuple rt = (RightTuple) BetaNode.getFirstTuple(bm.getRightTupleMemory(), it);
                            for (; rt != null; rt = (RightTuple) it.next(rt)) {
                                for (LeftTuple lt = rt.getBlocked(); lt != null; lt = lt.getBlockedNext()) {
                                    if (lt.getFirstChild() != null) {
                                        collectFromPeers(lt.getFirstChild(), internalMatches, nodeSet, reteEvaluator);
                                    }
                                }
                            }
                        }
                    } else {
                        bm = (BetaMemory) reteEvaluator.getNodeMemories().peekNodeMemory(node);
                        if (bm != null) {
                            FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                            Tuple lt = BetaNode.getFirstTuple(bm.getLeftTupleMemory(), it);
                            for (; lt != null; lt = (LeftTuple) it.next(lt)) {
                                if (lt.getFirstChild() != null) {
                                    collectFromLeftInput(lt.getFirstChild(), internalMatches, nodeSet, reteEvaluator);
                                }
                            }
                        }
                    }
                    return;
                } else if (NodeTypeEnums.FromNode == node.getType()) {
                    FromMemory fm = (FromMemory) reteEvaluator.getNodeMemories().peekNodeMemory(node);
                    if (fm != null) {
                        TupleMemory ltm = fm.getBetaMemory().getLeftTupleMemory();
                        FastIterator it = ltm.fullFastIterator();
                        for (LeftTuple lt = (LeftTuple) ltm.getFirst(null); lt != null; lt = (LeftTuple) it.next(lt)) {
                            if (lt.getFirstChild() != null) {
                                collectFromLeftInput(lt.getFirstChild(), internalMatches, nodeSet, reteEvaluator);
                            }
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
        if ( !lian.isTerminal() ) {
            Memory memory = reteEvaluator.getNodeMemories().peekNodeMemory(node);
            if (memory == null || memory.getSegmentMemory() == null) {
                // segment has never been initialized, which means the rule has never been linked.
                return;
            }
        }

        ObjectSource os = lian.getObjectSource();
        while (os.getType() != NodeTypeEnums.ObjectTypeNode) {
            os = os.getParentObjectSource();
        }
        ObjectTypeNode otn = (ObjectTypeNode) os;
        LeftTupleSink firstLiaSink = lian.getSinkPropagator().getFirstLeftTupleSink();

        java.util.Iterator<InternalFactHandle> it = otn.getFactHandlesIterator((InternalWorkingMemory) reteEvaluator);
        while (it.hasNext()) {
            InternalFactHandle fh = it.next();
            fh.forEachLeftTuple( lt -> {
                if ( lt.getTupleSink() == firstLiaSink ) {
                    collectFromLeftInput(lt, internalMatches, nodeSet, reteEvaluator);
                }
            });
        }
    }

    private static void collectFromLeftInput(LeftTuple lt, List<InternalMatch> internalMatches, Set<RuleTerminalNode> nodeSet, ReteEvaluator reteEvaluator) {
        for (; lt != null; lt = lt.getHandleNext()) {
            collectFromPeers(lt, internalMatches, nodeSet, reteEvaluator);
        }
    }

    private static void collectFromPeers(LeftTuple peer, List<InternalMatch> internalMatches, Set<RuleTerminalNode> nodeSet, ReteEvaluator reteEvaluator) {
        while (peer != null) {
            if ( peer.getTupleSink().getType() == NodeTypeEnums.AccumulateNode ) {
                Object accctx = peer.getContextObject();
                if (accctx instanceof AccumulateContext) {
                    // lefttuple representing an accumulated value now have that value as context object (it was null before) and must be skipped here
                    collectFromLeftInput((LeftTuple) ((AccumulateContext) accctx).getResultLeftTuple(), internalMatches, nodeSet, reteEvaluator);
                }
            } else if ( peer.getFirstChild() != null ) {
                for (LeftTuple childLt = peer.getFirstChild(); childLt != null; childLt = childLt.getHandleNext()) {
                    collectFromLeftInput(childLt, internalMatches, nodeSet, reteEvaluator);
                }
            } else if ( peer.getTupleSink().getType() == NodeTypeEnums.RuleTerminalNode ) {
                internalMatches.add((InternalMatch) peer);
                nodeSet.remove(peer.getTupleSink()); // remove this RuleTerminalNode, as we know we've visited it already
            }
            peer = peer.getPeer();
        }
    }

}
