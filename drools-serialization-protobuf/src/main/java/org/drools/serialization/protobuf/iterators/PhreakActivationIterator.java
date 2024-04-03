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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;

public class PhreakActivationIterator
    implements
    Iterator<InternalMatch> {

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

    public InternalMatch next() {
        if ( agendaItemIter.hasNext() ) {
            return agendaItemIter.next();
        } else {
            return null;
        }
    }

    public static List<InternalMatch> collectAgendaItems(InternalRuleBase kbase, ReteEvaluator reteEvaluator) {
        List<InternalMatch> internalMatches = new ArrayList<>();

        for (TerminalNode[] nodeArray : kbase.getReteooBuilder().getTerminalNodes().values()) {
            for (TerminalNode node : nodeArray) {
                if (node.getType() == NodeTypeEnums.RuleTerminalNode) {
                    PathMemory pathMemory = (PathMemory) reteEvaluator.getNodeMemories().peekNodeMemory(node);
                    if (pathMemory != null && pathMemory.getRuleAgendaItem() != null) {
                        LinkedList<TupleImpl> dormantMatches = pathMemory.getRuleAgendaItem().getRuleExecutor().getDormantMatches();
                        for (RuleTerminalNodeLeftTuple lt = (RuleTerminalNodeLeftTuple) dormantMatches.getFirst(); lt != null; lt = (RuleTerminalNodeLeftTuple) lt.getNext()) {
                            internalMatches.add(lt);
                        }
                        LinkedList<TupleImpl> activeMatches = pathMemory.getRuleAgendaItem().getRuleExecutor().getActiveMatches();
                        for (RuleTerminalNodeLeftTuple lt = (RuleTerminalNodeLeftTuple) activeMatches.getFirst(); lt != null; lt = (RuleTerminalNodeLeftTuple) lt.getNext()) {
                            internalMatches.add(lt);
                        }
                    }
                }
            }
        }

        return internalMatches;
    }

    // --- OLD

    public static List<InternalMatch> collectAgendaItems_OLD(InternalRuleBase kbase, ReteEvaluator reteEvaluator) {
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

    private static List<RuleTerminalNode> populateRuleTerminalNodes(InternalRuleBase kbase, Set<RuleTerminalNode> nodeSet) {
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

    private static void processLeftTuples(LeftTupleSource node, List<InternalMatch> internalMatches, Set<RuleTerminalNode> nodeSet, ReteEvaluator reteEvaluator) {
        LeftTupleSource node1 = node;
        while (!NodeTypeEnums.isLeftInputAdapterNode((node1))) {
            node1 = node1.getLeftTupleSource();
        }
        int maxShareCount = node1.getAssociationsSize();

        while (!NodeTypeEnums.isLeftInputAdapterNode(node)) {
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
                        for (; lt != null; lt = (TupleImpl) it.next(lt)) {
                            AccumulateContext accctx = (AccumulateContext) lt.getContextObject();
                            collectFromPeers((TupleImpl) accctx.getResultLeftTuple(), internalMatches, nodeSet, reteEvaluator);
                        }
                    } else if ( NodeTypeEnums.ExistsNode == node.getType() ) {
                        bm = (BetaMemory) reteEvaluator.getNodeMemories().peekNodeMemory(node);
                        if (bm != null) {
                            bm = (BetaMemory) reteEvaluator.getNodeMemories().peekNodeMemory(node);
                            FastIterator it = bm.getRightTupleMemory().fullFastIterator(); // done off the RightTupleMemory, as exists only have unblocked tuples on the left side
                            RightTuple   rt = (RightTuple) BetaNode.getFirstTuple(bm.getRightTupleMemory(), it);
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
                            for (; lt != null; lt = (TupleImpl) it.next(lt)) {
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
                        for (TupleImpl lt = (TupleImpl) ltm.getFirst(null); lt != null; lt = (TupleImpl) it.next(lt)) {
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
                if (lt.getSink() == firstLiaSink ) {
                    collectFromLeftInput(lt, internalMatches, nodeSet, reteEvaluator);
                }
            });
        }
    }

    private static void collectFromLeftInput(TupleImpl lt, List<InternalMatch> internalMatches, Set<RuleTerminalNode> nodeSet, ReteEvaluator reteEvaluator) {
        for (; lt != null; lt = lt.getHandleNext()) {
            collectFromPeers(lt, internalMatches, nodeSet, reteEvaluator);
        }
    }

    private static void collectFromPeers(TupleImpl peer, List<InternalMatch> internalMatches, Set<RuleTerminalNode> nodeSet, ReteEvaluator reteEvaluator) {
        while (peer != null) {
            if (peer.getSink().getType() == NodeTypeEnums.AccumulateNode ) {
                Object accctx = peer.getContextObject();
                if (accctx instanceof AccumulateContext) {
                    // lefttuple representing an accumulated value now have that value as context object (it was null before) and must be skipped here
                    collectFromLeftInput((TupleImpl) ((AccumulateContext) accctx).getResultLeftTuple(), internalMatches, nodeSet, reteEvaluator);
                }
            } else if ( peer.getFirstChild() != null ) {
                for (TupleImpl childLt = peer.getFirstChild(); childLt != null; childLt = childLt.getHandleNext()) {
                    collectFromPeers(childLt, internalMatches, nodeSet, reteEvaluator);
                }
            } else if (peer.getSink().getType() == NodeTypeEnums.RuleTerminalNode ) {
                internalMatches.add((InternalMatch) peer);
                nodeSet.remove(peer.getSink()); // remove this RuleTerminalNode, as we know we've visited it already
            }
            peer = peer.getPeer();
        }
    }

}
