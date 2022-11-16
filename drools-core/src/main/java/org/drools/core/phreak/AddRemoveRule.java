/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.phreak;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.drools.core.common.ActivationsManager;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.impl.RuleBase;
import org.drools.core.reteoo.AbstractTerminalNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.AlphaTerminalNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.RightTupleSinkAdapter;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SegmentMemory.SegmentPrototype;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.common.PropagationContext;
import org.drools.core.reteoo.Tuple;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.core.phreak.BuildtimeSegmentUtilities.isAssociatedWith;
import static org.drools.core.phreak.BuildtimeSegmentUtilities.isRootNode;

public class AddRemoveRule {

    private static final Logger log = LoggerFactory.getLogger(AddRemoveRule.class);

    /**
     * This method is called after the rule nodes have been added to the network
     * For add tuples are processed after the segments and pmems have been adjusted
     *
     * @return
     */
    public static List<PathEndNode> addRule(TerminalNode tn, Collection<InternalWorkingMemory> wms, RuleBase kBase) {
        if (log.isTraceEnabled()) {
            log.trace("Adding Rule {}", tn.getRule().getName());
        }

        Set<LeftTupleNode> splitNodes = new HashSet<>();
        Arrays.stream(tn.getPathEndNodes()).forEach(n -> addNetworkSplitPoint(n, splitNodes));

        PathEndNodes pathEndNodes = getPathEndNodes(kBase, tn,  splitNodes);

        pathEndNodes.nodesToInvalidate.stream().forEach(node -> kBase.invalidateSegmentPrototype(node));

        // reset the node/segment masks and protos for the subject and other impact
        tn.resetPathMemSpec(null);
        BuildtimeSegmentUtilities.createPathProtoMemories(tn, null, kBase);

        // reset first, to avoid resetting stuff already recalculated - in the case of shares.
        resetPaths(null, kBase, pathEndNodes);

        // Insert the facts for the new paths. This will iterate each new path from EndNode to the splitStart - but will not process the splitStart itself (as tha already exist).
        // It does not matter that the prior segments have not yet been processed for splitting, as this will only apply for branches of paths that did not exist before

        for (InternalWorkingMemory wm : wms) {
            wm.flushPropagations();

            PathEndNodeMemories tnms = getPathEndMemories(wm, pathEndNodes);

            if (tn.getPathNodes()[0].getAssociatedTerminals().size() == 1) {
                // rule added with no sharing, so populate it's lian
                insertLiaFacts(tn.getPathNodes()[0], wm);
            } else if (tnms.subjectPmem != null) {
                // If the subject PathMemories are not yet initialized (by the flush) there are no Segments or tuples to process
                Map<PathMemory, SegmentMemory[]> prevSmemsLookup = reInitPathMemories(tnms.otherPmems, null);

                // must collect all visited SegmentMemories, for link notification
                handleExistingPaths(tn, prevSmemsLookup, tnms.otherPmems, wm, ExistingPathStrategy.ADD_STRATEGY);

                addNewPaths(wm, tnms.subjectPmems, tn);

                pathEndNodes.subjectSplits.stream().forEach( n -> processLeftTuples(n, wm, true, tn));
            }

            addExistingSegmentMemories(pathEndNodes, wm);

            insertFacts(pathEndNodes, wm);

            notifySegments(tnms, wm);
        }



        return new ArrayList<>(pathEndNodes.otherEndNodes);
    }

    private static void addExistingSegmentMemories(PathEndNodes pathEndNodes, InternalWorkingMemory wm) {
        // Iterates the path to find existing SegmentMemories that can be added to the PathMemory
        pathEndNodes.subjectEndNodes.forEach(endNode -> {
            Arrays.stream(endNode.getSegmentPrototypes()).forEach( proto -> {
                if (proto == null) { // ths proto is before the start of the subnetwork
                    return;
                }

                LeftTupleNode node = proto.getRootNode();
                Memory mem = wm.getNodeMemories().peekNodeMemory(node);

                if (mem != null && mem.getSegmentMemory() != null) {
                    SegmentMemory smem = mem.getSegmentMemory();

                    PathMemory pmem = (PathMemory) wm.getNodeMemories().peekNodeMemory(endNode);
                    if (pmem == null) {
                        pmem = RuntimeSegmentUtilities.initializePathMemory(wm, endNode);
                    }
                    if (pmem.getSegmentMemories()[proto.getPos()] == null) {
                        // we check null, as this might have been fixed due to eager initialisation
                        pmem.setSegmentMemory(smem.getPos(), smem);
                        smem.addPathMemory(pmem);
                    }
                }
            });
        });
    }

    /**
     * This method is called before the rule nodes are removed from the network.
     * For remove tuples are processed before the segments and pmems have been adjusted
     *
     * Note the
     */
    public static List<PathEndNode> removeRule(TerminalNode tn, Collection<InternalWorkingMemory> wms, RuleBase kBase) {
        if (log.isTraceEnabled()) {
            log.trace("Removing Rule {}", tn.getRule().getName());
        }

        Set<LeftTupleNode> splitNodes = new HashSet<>();
        Arrays.stream(tn.getPathEndNodes()).forEach(n -> addNetworkSplitPoint(n, splitNodes));

        PathEndNodes pathEndNodes = getPathEndNodes(kBase, tn,  splitNodes);

        PathEndNodeMemories[] tnmsList = new PathEndNodeMemories[wms.size()];
        int i = 0;
        for (InternalWorkingMemory wm : wms) {
            wm.flushPropagations();

            PathEndNodeMemories tnms = getPathEndMemories(wm, pathEndNodes);
            tnmsList[i++]  = tnms;

            if (!tnms.subjectPmems.isEmpty()) {
                if (tn.getPathNodes()[0].getAssociatedTerminals().size() == 1 &&
                    tnms.subjectPmem != null) {
                    pathEndNodes.subjectSplits.stream().forEach( n -> flushStagedTuples(n, tnms.subjectPmem, wm));
                } else {
                    flushStagedTuples(tn, tnms.subjectPmem, pathEndNodes, wm);
                }
                pathEndNodes.subjectSplits.stream().forEach( n -> processLeftTuples(n, wm, false, tn));

            }
        }

        pathEndNodes.nodesToInvalidate.stream().forEach(node -> kBase.invalidateSegmentPrototype(node));

        resetPaths(tn, kBase, pathEndNodes);

        i = 0;
        for (InternalWorkingMemory wm : wms) {
            PathEndNodeMemories tnms = tnmsList[i++];

            if (!tnms.subjectPmems.isEmpty()) {
                removeNewPaths(wm, tnms.subjectPmems);
                if (tn.getPathNodes()[0].getAssociatedTerminals().size() > 1 ) {
                    Map<PathMemory, SegmentMemory[]> prevSmemsLookup = reInitPathMemories(tnms.otherPmems, tn);

                    // must collect all visited SegmentMemories, for link notification
                    handleExistingPaths(tn, prevSmemsLookup, tnms.otherPmems, wm, ExistingPathStrategy.REMOVE_STRATEGY);

                    notifySegments(tnms, wm);
                }
            }

            if (tnms.subjectPmem != null && tnms.subjectPmem.isInitialized() && tnms.subjectPmem.getRuleAgendaItem().isQueued()) {
                // SubjectPmem can be null, if it was never initialized
                tnms.subjectPmem.getRuleAgendaItem().dequeue();
            }
        }

        return new ArrayList<>(pathEndNodes.otherEndNodes);
    }

    private static void resetPaths(TerminalNode tn, RuleBase kBase, PathEndNodes pathEndNodes) {
        // reset first, to avoid resetting stuff already relculated - in the case of shares.
        pathEndNodes.otherTermNodes.stream().forEach(other -> {
            other.resetPathMemSpec(tn);
        });

        pathEndNodes.otherTermNodes.stream().forEach(other -> {
            BuildtimeSegmentUtilities.createPathProtoMemories(other, tn, kBase);
        });
    }

    static void adjustSegment(InternalWorkingMemory wm, SegmentMemory smem, int smemSplitAdjustAmount) {
        smem.unlinkSegment(wm);
        SegmentPrototype proto = wm.getKnowledgeBase().getSegmentPrototype(smem.getRootNode());

        // need to preserve the current LinkedNodeMask, during update, so it can be restored.
        long currentLinkedNodeMask = smem.getLinkedNodeMask();
        proto.updateSegmentMemory(smem,wm);
        smem.setLinkedNodeMask(currentLinkedNodeMask);
    }

    public interface ExistingPathStrategy {
        ExistingPathStrategy ADD_STRATEGY = new AddExistingPaths();
        ExistingPathStrategy REMOVE_STRATEGY = new RemoveExistingPaths();

        SegmentMemory[] getSegmenMemories(PathMemory pmem);

        void handleSplit(PathMemory pmem, SegmentMemory[] prevSmems, SegmentMemory[] smems, int smemIndex, int prevSmemIndex,
                         LeftTupleNode parentNode, LeftTupleNode node, TerminalNode tn,
                         Set<Integer> visited, Map<LeftTupleNode, SegmentMemory> nodeToSegmentMap,
                         InternalWorkingMemory wm);

        void processSegmentMemories(SegmentMemory[] smems, PathMemory pmem);

        int incSmemIndex1(int smemIndex);
        int incSmemIndex2(int smemIndex);

        int incPrevSmemIndex1(int prevSmemIndex);
        int incPrevSmemIndex2(int prevSmemIndex);
    }

    public static class AddExistingPaths implements ExistingPathStrategy {

        @Override
        public SegmentMemory[] getSegmenMemories(PathMemory pmem) {
            return pmem.getSegmentMemories();
        }

        @Override
        public void handleSplit(PathMemory pmem, SegmentMemory[] prevSmems, SegmentMemory[] smems, int smemIndex, int prevSmemIndex,
                                LeftTupleNode parentNode, LeftTupleNode node, TerminalNode tn,
                                Set<Integer> visited, Map<LeftTupleNode, SegmentMemory> nodeToSegmentMap,
                                InternalWorkingMemory wm) {
            if (smems[smemIndex - 1] != null) {
                SegmentMemory sm2 = nodeToSegmentMap.get(node);
                if (sm2 == null) {
                    SegmentMemory sm1 = smems[smemIndex - 1];
                    correctMemoryOnSplitsChanged(parentNode, null, wm);
                    sm2 = splitSegment(wm, sm1, parentNode, wm.getKnowledgeBase());
                    nodeToSegmentMap.put(node, sm2);
                }

                smems[smemIndex] = sm2;
            }
        }

        @Override
        public void processSegmentMemories(SegmentMemory[] smems, PathMemory pmem) {

        }

        @Override
        public int incSmemIndex1(int smemIndex) {
            return smemIndex+1;
        }


        @Override
        public int incPrevSmemIndex1(int prevSmemIndex) {
            return prevSmemIndex;
        }

        @Override
        public int incSmemIndex2(int smemIndex) {
            return smemIndex;
        }

        @Override
        public int incPrevSmemIndex2(int prevSmemIndex) {
            return prevSmemIndex+1;
        }
    }

    public static class RemoveExistingPaths implements ExistingPathStrategy {

        @Override
        public SegmentMemory[] getSegmenMemories(PathMemory pmem) {
            return new SegmentMemory[ pmem.getSegmentMemories().length ];
        }

        @Override
        public void handleSplit(PathMemory pmem, SegmentMemory[] prevSmems, SegmentMemory[] smems, int smemIndex, int prevSmemIndex,
                                LeftTupleNode parentNode, LeftTupleNode node, TerminalNode tn,
                                Set<Integer> visited, Map<LeftTupleNode, SegmentMemory> nodeToSegmentMap,
                                InternalWorkingMemory wm) {
            if (visited.contains(node.getId())) {
                return;
            }

            correctMemoryOnSplitsChanged(parentNode, tn, wm);

            SegmentMemory sm1 = smems[smemIndex];
            SegmentMemory sm2 = prevSmems[prevSmemIndex];

            // if both are null, there is nothing to do.
            if (sm1 == null && sm2 == null) {
                return;
            }

            // Temporarily remove the terminal node of the rule to be removed from the rete network to avoid that
            // its path memory could be added to an existing segment memory during the merge of 2 segments
            LeftTupleSource removedTerminalSource = tn.getLeftTupleSource();
            removedTerminalSource.removeTupleSink( tn );

            // sm1 cannot be null, if they re to merge
            if (sm1 == null) {
                sm1 = RuntimeSegmentUtilities.createChildSegment(wm, parentNode);
                smems[smemIndex] = sm1;
                sm1.add(sm2);
            }

            // merge in sm2, if it is null
            if (sm2 != null) {
                mergeSegment(sm1, sm2, wm.getKnowledgeBase(), wm);
                sm2.unlinkSegment(wm);
            } else {
                SegmentPrototype proto = wm.getKnowledgeBase().getSegmentPrototype(sm1.getRootNode());
                proto.updateSegmentMemory(sm1, wm);
                long currentLinkedNodeMask = sm1.getLinkedNodeMask();
                proto.updateSegmentMemory(sm1,wm);
                sm1.setLinkedNodeMask(currentLinkedNodeMask);
            }

            sm1.unlinkSegment(wm);
            visited.add(node.getId());

            // Add back the the terminal node of the rule to be removed into the rete network to permit the network
            // traversal up from it and the removal of all the nodes exclusively belonging to the removed rule
            removedTerminalSource.addTupleSink( tn );
        }

        @Override
        public void processSegmentMemories(SegmentMemory[] smems, PathMemory pmem) {
            for (int i = 0; i < smems.length; i++) {
                if (smems[i] != null) {
                    pmem.setSegmentMemory( smems[i].getPos(), smems[i] );
                }
            }
        }

        @Override
        public int incSmemIndex1(int smemIndex) {
            return smemIndex;
        }

        @Override
        public int incPrevSmemIndex1(int prevSmemIndex) {
            return prevSmemIndex+1;
        }

        @Override
        public int incSmemIndex2(int smemIndex) {
            return smemIndex+1;
        }

        @Override
        public int incPrevSmemIndex2(int prevSmemIndex) {
            return prevSmemIndex;
        }
    }

    private static void handleExistingPaths(TerminalNode tn, Map<PathMemory, SegmentMemory[]> prevSmemsLookup,
                                                          List<PathMemory> pmems, InternalWorkingMemory wm, ExistingPathStrategy strategy) {
        Set<SegmentMemory>                visitedSegments  = new HashSet<>();
        Set<Integer> visitedNodes = new HashSet<>();
        Map<LeftTupleNode, SegmentMemory> nodeToSegmentMap = new HashMap<>();

        for (PathMemory pmem : pmems) {
            LeftTupleNode[] nodes = pmem.getPathEndNode().getPathNodes();

            SegmentMemory[] prevSmems = prevSmemsLookup.get(pmem);
            SegmentMemory[] smems     = strategy.getSegmenMemories(pmem);

            if (prevSmems.length == 0 && smems.length == 0) {
                continue;
            }

            LeftTupleNode node;
            int           prevSmemIndex         = 0;
            int           smemIndex             = 0;
            int           smemSplitAdjustAmount = 0;
            int           nodeIndex             = 0;

            // excluding the rule just added iterate while not split (i.e. find the next split, prior to this rule being added)
            // note it's checking for when the parent is the split, and thus node is the next root root.

            smems[smemIndex] = prevSmems[prevSmemIndex];
            do {
                node = nodes[nodeIndex++];
                LeftTupleSource parentNode = node.getLeftTupleSource();
                if (isSplit(parentNode)) {
                    smemIndex = strategy.incSmemIndex1(smemIndex);
                    prevSmemIndex = strategy.incPrevSmemIndex1(prevSmemIndex);
                    if (isSplit(parentNode, tn)) { // check if the split is there even without the processed rule
                        smemIndex = strategy.incSmemIndex2(smemIndex);
                        prevSmemIndex = strategy.incPrevSmemIndex2(prevSmemIndex);
                        smems[smemIndex] = prevSmems[prevSmemIndex];
                        if ( smems[smemIndex] != null && smemSplitAdjustAmount > 0 && visitedSegments.add(smems[smemIndex])) {
                            adjustSegment( wm, smems[smemIndex], smemSplitAdjustAmount );
                        }
                    } else {
                        strategy.handleSplit(pmem, prevSmems, smems, smemIndex, prevSmemIndex,
                                             parentNode, node, tn, visitedNodes,
                                             nodeToSegmentMap, wm);
                        smemSplitAdjustAmount++;
                    }
                }
            } while (!NodeTypeEnums.isEndNode(node));

            strategy.processSegmentMemories(smems, pmem);


        }
    }

    private static void addNewPaths(InternalWorkingMemory wm, List<PathMemory> pmems, TerminalNode tn) {
        for (PathMemory pmem : pmems) {
            LeftTupleSink tipNode = pmem.getPathEndNode();

            LeftTupleNode child  = tipNode;
            LeftTupleNode parent = tipNode.getLeftTupleSource();

            while (true) {
                if ( parent != null && parent.getAssociatedTerminals().size() != 1 && child.getAssociatedTerminals().size() == 1 ) {
                    // This is the split point that the new path enters an existing path.
                    // If the parent has other child SegmentMemorys then it must create a new child SegmentMemory
                    // If the parent is a query node, then it's internal data structure needs changing
                    // all right input data must be propagated
                    Memory mem = wm.getNodeMemories().peekNodeMemory( parent );
                    if ( mem != null && mem.getSegmentMemory() != null ) {
                        SegmentMemory sm = mem.getSegmentMemory();
                        if ( sm.getFirst() != null && sm.size() < parent.getSinkPropagator().size()) {
                            LeftTupleSink[] sinks = parent.getSinkPropagator().getSinks();
                            for (int i = sm.size(); i < sinks.length; i++) {
                                SegmentMemory childSmem = RuntimeSegmentUtilities.createChildSegment(wm, sinks[i]);
                                sm.add(childSmem);
                            }
                        }
                        correctMemoryOnSplitsChanged( parent, null, wm );
                    }
                } else {
                    Memory mem = wm.getNodeMemories().peekNodeMemory( child );
                    // The root of each segment
                    if ( mem != null ) {
                        SegmentMemory sm = mem.getSegmentMemory();

                        // make sure there is a proto  in that slot. If it's null, its a subnetwork and the node is before it's start.
                        if ( sm != null &&
                             pmem.getPathEndNode().getSegmentPrototypes()[sm.getPos()] != null &&
                             !sm.getPathMemories().contains( pmem ) ) {
                            sm.addPathMemory( pmem );
                            pmem.setSegmentMemory( sm.getPos(), sm );
                        }
                    }
                }

                if (parent == null) {
                    break;
                }

                child = parent;
                parent = parent.getLeftTupleSource();
            }
        }
    }


    private static void removeNewPaths(InternalWorkingMemory wm, List<PathMemory> pmems) {
        Set<Integer> visitedNodes = new HashSet<>();
        for (PathMemory pmem : pmems) {
            LeftTupleSink tipNode = pmem.getPathEndNode();

            LeftTupleNode child  = tipNode;
            LeftTupleNode parent = tipNode.getLeftTupleSource();

            while (true) {
                if (child.getAssociatedTerminals().size() == 1 && NodeTypeEnums.isBetaNode(child)) {
                    // If this is a beta node, it'll delete all the right input data
                    deleteRightInputData((LeftTupleSink) child, wm);
                }

                if (parent != null && parent.getAssociatedTerminals().size() != 1 && child.getAssociatedTerminals().size() == 1) {
                    // This is the split point that the new path enters an existing path.
                    // If the parent has other child SegmentMemorys then it must create a new child SegmentMemory
                    // If the parent is a query node, then it's internal data structure needs changing
                    // all right input data must be propagated
                    if (!visitedNodes.contains( child.getId() )) {
                        Memory mem = wm.getNodeMemories().peekNodeMemory( parent );
                        if ( mem != null && mem.getSegmentMemory() != null ) {
                            SegmentMemory sm = mem.getSegmentMemory();
                            if ( sm.getFirst() != null ) {
                                SegmentMemory childSm = wm.getNodeMemories().peekNodeMemory( child ).getSegmentMemory();
                                sm.remove( childSm );
                            }
                        }
                    }
                } else {
                    Memory mem = wm.getNodeMemories().peekNodeMemory(child);
                    // The root of each segment
                    if (mem != null) {
                        SegmentMemory sm = mem.getSegmentMemory();
                        if (sm != null && sm.getPathMemories().contains( pmem )) {
                            mem.getSegmentMemory().removePathMemory(pmem);
                        }
                    }
                }

                if (parent == null) {
                    break;
                }

                visitedNodes.add(child.getId());
                child = parent;
                parent = parent.getLeftTupleSource();
            }
        }
    }

    private static boolean isSplit(LeftTupleNode node) {
        return isSplit(node, null);
    }

    private static boolean isSplit(LeftTupleNode node, TerminalNode removingTN) {
        return node != null && BuildtimeSegmentUtilities.isTipNode(node, removingTN);
    }

    public static class Flushed {
        SegmentMemory segmentMemory;
        PathMemory pathMemory;

        public Flushed(SegmentMemory segmentMemory, PathMemory pathMemory) {
            this.segmentMemory = segmentMemory;
            this.pathMemory = pathMemory;
        }
    }

    private static void flushStagedTuples(TerminalNode tn, PathMemory pmem, PathEndNodes pathEndNodes, InternalWorkingMemory wm) {
        // first flush the subject rule, then flush any staging lists that are part of a merge
        if ( pmem.isInitialized() ) {
            RuleNetworkEvaluator.INSTANCE.evaluateNetwork(pmem, pmem.getRuleAgendaItem().getRuleExecutor(), wm);
        }

        // With the removing rules being flushed, we need to check any splits that will be merged, to see if they need flushing
        // Beware that flushing a higher up node, might again cause lower nodes to have more staged items. So track flushed items
        // incase they need to be reflushed
        List<Flushed> flushed = new ArrayList<>();

        for ( LeftTupleNode node : pathEndNodes.subjectSplits ) {
            if (!isSplit(node, tn)) { // check if the split is there even without the processed rule
                Memory mem = wm.getNodeMemories().peekNodeMemory(node);
                if ( mem != null) {
                    SegmentMemory smem = mem.getSegmentMemory();

                    if ( !smem.isEmpty() ) {
                        for ( SegmentMemory childSmem = smem.getFirst(); childSmem != null; childSmem = childSmem.getNext() ) {
                            if ( !childSmem.getStagedLeftTuples().isEmpty() ) {
                                PathMemory childPmem = childSmem.getPathMemories().get(0);
                                flushed.add( new Flushed(childSmem, childPmem));
                                forceFlushLeftTuple(childPmem, childSmem, wm, childSmem.getStagedLeftTuples().takeAll());
                            }
                        }
                    }
                }
            }
        }

        int flushCount = 1; // need to ensure that there is one full iteration, without any flushing. To avoid one flush causing populat of another already flushed segment
        while (!flushed.isEmpty() && flushCount != 0) {
            flushCount = 0;
            for (Flushed path : flushed) {
                if ( !path.segmentMemory.getStagedLeftTuples().isEmpty() ) {
                    flushCount++;
                    forceFlushLeftTuple(pmem, path.segmentMemory, wm, path.segmentMemory.getStagedLeftTuples().takeAll());
                }
            }
        }
    }

    private static void flushStagedTuples(LeftTupleNode splitStartNode, PathMemory pmem, InternalWorkingMemory wm) {
        if ( !pmem.isInitialized() ) {
            // The rule has never been linked in and evaluated, so there will be nothing to flush.
            return;
        }
        if ( pmem.getSegmentMemories().length == 0) {
            // this is a AlphaTerminalNode, so ignore.
            return;
        }

        int             smemIndex = getSegmentPos(splitStartNode); // index before the segments are merged
        SegmentMemory[] smems     = pmem.getSegmentMemories();

        SegmentMemory   sm        = null;

        // If there is no sharing, then there will not be any staged tuples in later segemnts, and thus no need to search for them if the current sm is empty.
        int length = smems.length;
        if ( splitStartNode.getAssociatedTerminals().size() == 1 ) {
            length = 1;
        }

        while (smemIndex < length) {
            sm = smems[smemIndex];
            if (sm != null && !sm.getStagedLeftTuples().isEmpty()) {
                break;
            }
            smemIndex++;
        }

        if ( smemIndex < length ) {
            // it only found a SM that needed flushing, if smemIndex < length
            forceFlushLeftTuple(pmem, sm, wm, sm.getStagedLeftTuples().takeAll());
        }
    }

    public static boolean flushLeftTupleIfNecessary(ReteEvaluator reteEvaluator, SegmentMemory sm, boolean streamMode) {
        return flushLeftTupleIfNecessary(reteEvaluator, sm, null, streamMode, Tuple.NONE);
    }

    public static boolean flushLeftTupleIfNecessary(ReteEvaluator reteEvaluator, SegmentMemory sm, LeftTuple leftTuple, boolean streamMode, short stagedType) {
        PathMemory pmem = findPathToFlush(sm, leftTuple, streamMode);

        if ( pmem == null ) {
            return false;
        }

        forceFlushLeftTuple( pmem, sm, reteEvaluator, createLeftTupleTupleSets(leftTuple, stagedType) );
        forceFlushWhenRiaNode(reteEvaluator, pmem);
        return true;
    }

    public static PathMemory findPathToFlush(SegmentMemory sm, LeftTuple leftTuple, boolean streamMode) {
        boolean forceFlush = streamMode || ( leftTuple != null && leftTuple.getFactHandle() != null && leftTuple.getFactHandle().isEvent() );
        return forceFlush ? sm.getPathMemories().get(0) : sm.getFirstDataDrivenPathMemory();
    }

    public static TupleSets<LeftTuple> createLeftTupleTupleSets(LeftTuple leftTuple, short stagedType) {
        TupleSets<LeftTuple> leftTupleSets = new TupleSetsImpl<>();
        if (leftTuple != null) {
            switch (stagedType) {
                case Tuple.INSERT:
                    leftTupleSets.addInsert(leftTuple);
                    break;
                case Tuple.DELETE:
                    leftTupleSets.addDelete(leftTuple);
                    break;
                case Tuple.UPDATE:
                    leftTupleSets.addUpdate(leftTuple);
                    break;
            }
        }
        return leftTupleSets;
    }

    public static void forceFlushWhenRiaNode(ReteEvaluator reteEvaluator, PathMemory pmem) {
        for (PathMemory outPmem : findPathsToFlushFromRia(reteEvaluator, pmem)) {
            forceFlushPath(reteEvaluator, outPmem);
        }
    }

    public static List<PathMemory> findPathsToFlushFromRia(ReteEvaluator reteEvaluator, PathMemory pmem) {
        List<PathMemory> paths = null;
        if (pmem.isDataDriven() && pmem.getNodeType() == NodeTypeEnums.RightInputAdapterNode) {
            for (PathEndNode pnode : pmem.getPathEndNode().getPathEndNodes()) {
                if ( pnode instanceof TerminalNode ) {
                    PathMemory outPmem = reteEvaluator.getNodeMemory((TerminalNode) pnode);
                    if (outPmem.isDataDriven()) {
                        if (paths == null) {
                            paths = new ArrayList<>();
                        }
                        paths.add(outPmem);
                    }
                }
            }
        }
        return paths == null ? Collections.emptyList() : paths;
    }

    public static void forceFlushPath(ReteEvaluator reteEvaluator, PathMemory outPmem) {
        SegmentMemory outSmem = outPmem.getSegmentMemories()[0];
        if (outSmem != null) {
            forceFlushLeftTuple(outPmem, outSmem, reteEvaluator, new TupleSetsImpl<>());
        }
    }

    public static void forceFlushLeftTuple(PathMemory pmem, SegmentMemory sm, ReteEvaluator reteEvaluator, TupleSets<LeftTuple> leftTupleSets) {
        SegmentMemory[] smems = pmem.getSegmentMemories();

        LeftTupleNode node;
        Memory        mem;
        long          bit = 1;
        if ( sm.getRootNode().getType() == NodeTypeEnums.LeftInputAdapterNode && sm.getTipNode().getType() != NodeTypeEnums.LeftInputAdapterNode) {
            // The segment is the first and it has the lian shared with other nodes, the lian must be skipped, so adjust the bit and sink
            node =  sm.getRootNode().getSinkPropagator().getFirstLeftTupleSink();
            mem = sm.getNodeMemories().get(1);
            bit = 2; // adjust bit to point to next node
        } else {
            node =  sm.getRootNode();
            mem = sm.getNodeMemories().get(0);
        }

        PathMemory rtnPmem = NodeTypeEnums.isTerminalNode(pmem.getPathEndNode()) ?
                pmem :
                reteEvaluator.getNodeMemory((AbstractTerminalNode) pmem.getPathEndNode().getPathEndNodes()[0]);

        ActivationsManager activationsManager = pmem.getActualActivationsManager( reteEvaluator );
        RuleNetworkEvaluator.INSTANCE.outerEval(pmem, node, bit, mem, smems, sm.getPos(), leftTupleSets, activationsManager,
                                                new LinkedList<>(),
                                                true, rtnPmem.getOrCreateRuleAgendaItem(activationsManager).getRuleExecutor());
    }


    private static Map<PathMemory, SegmentMemory[]> reInitPathMemories(List<PathMemory> pathMems, TerminalNode removingTN) {
        Map<PathMemory, SegmentMemory[]> previousSmems = new HashMap<>();
        for (PathMemory pmem : pathMems) {
            // Re initialise all the PathMemories
            previousSmems.put(pmem, pmem.getSegmentMemories());

            PathEndNode pathEndNode = pmem.getPathEndNode();
            AbstractTerminalNode.initPathMemory(pathEndNode, pmem);
        }
        return previousSmems;
    }

    private static void notifySegments(PathEndNodeMemories tnms, InternalWorkingMemory wm) {
        tnms.otherPmems.stream().flatMap( pmem -> Arrays.stream(pmem.getSegmentMemories())).filter(Objects::nonNull).forEach( sm -> sm.notifyRuleLinkSegment(wm));
        tnms.subjectPmems.stream().flatMap( pmem -> Arrays.stream(pmem.getSegmentMemories())).filter(Objects::nonNull).forEach( sm -> sm.notifyRuleLinkSegment(wm));
    }

    private static void correctMemoryOnSplitsChanged(LeftTupleNode splitStart, TerminalNode removingTN, InternalWorkingMemory wm) {
        if (splitStart.getType() == NodeTypeEnums.UnificationNode) {
            QueryElementNode.QueryElementNodeMemory mem = (QueryElementNode.QueryElementNodeMemory) wm.getNodeMemories().peekNodeMemory(splitStart);
            if (mem != null) {
                mem.correctMemoryOnSinksChanged(removingTN);
            }
        }
    }



    private static int getSegmentPos(LeftTupleNode lts) {
        int counter = 0;
        while (lts.getType() != NodeTypeEnums.LeftInputAdapterNode) {
            lts = lts.getLeftTupleSource();
            if (BuildtimeSegmentUtilities.isTipNode(lts, null)) {
                counter++;
            }
        }
        return counter;
    }

    private static void insertLiaFacts(LeftTupleNode startNode, InternalWorkingMemory wm) {
        // rule added with no sharing
        PropagationContextFactory pctxFactory = RuntimeComponentFactory.get().getPropagationContextFactory();
        final PropagationContext  pctx        = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.Type.RULE_ADDITION, null, null, null);
        LeftInputAdapterNode      lian        = (LeftInputAdapterNode) startNode;
        RightTupleSinkAdapter     liaAdapter  = new RightTupleSinkAdapter(lian);
        lian.getObjectSource().updateSink(liaAdapter, pctx, wm);
    }

    private static void insertFacts(PathEndNodes endNodes, InternalWorkingMemory wm) {
        Set<Integer> visited = new HashSet<>();

        for ( PathEndNode endNode : endNodes.subjectEndNodes ) {
            LeftTupleNode[]  nodes = endNode.getPathNodes();
            for ( int i = 0; i < nodes.length; i++ ) {
                LeftTupleNode node = nodes[i];
                if  ( NodeTypeEnums.isBetaNode(node) && node.getAssociatedTerminals().size() == 1 ) {
                    if (!visited.add( node.getId() )) {
                        continue;// this is to avoid rentering a path, and processing nodes twice. This can happen for nested subnetworks.
                    }
                    BetaNode bn = (BetaNode) node;

                    if (!bn.isRightInputIsRiaNode()) {
                        PropagationContextFactory pctxFactory = RuntimeComponentFactory.get().getPropagationContextFactory();
                        final PropagationContext pctx = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.Type.RULE_ADDITION, null, null, null);
                        bn.getRightInput().updateSink(bn, pctx, wm);
                    }
                }
            }
        }
    }

    private static void deleteRightInputData(LeftTupleSink node, InternalWorkingMemory wm) {
        if (wm.getNodeMemories().peekNodeMemory(node) != null) {
            BetaNode   bn = (BetaNode) node;
            BetaMemory bm;
            if (bn.getType() == NodeTypeEnums.AccumulateNode) {
                bm = ((AccumulateMemory) wm.getNodeMemory(bn)).getBetaMemory();
            } else {
                bm = (BetaMemory) wm.getNodeMemory(bn);
            }

            TupleMemory  rtm = bm.getRightTupleMemory();
            FastIterator it  = rtm.fullFastIterator();
            for (Tuple rightTuple = BetaNode.getFirstTuple(rtm, it); rightTuple != null; ) {
                Tuple next = (Tuple) it.next(rightTuple);
                rtm.remove(rightTuple);
                rightTuple.unlinkFromRightParent();
                rightTuple = next;
            }

            if (!bm.getStagedRightTuples().isEmpty()) {
                bm.setNodeDirtyWithoutNotify();
            }
            TupleSets<RightTuple> srcRightTuples = bm.getStagedRightTuples().takeAll();

            unlinkRightTuples(srcRightTuples.getInsertFirst());
            unlinkRightTuples(srcRightTuples.getUpdateFirst());
            unlinkRightTuples(srcRightTuples.getDeleteFirst());

            deleteFactsFromRightInput(bn, wm);
        }
    }

    private static void deleteFactsFromRightInput(BetaNode bn, InternalWorkingMemory wm) {
        ObjectSource source = bn.getRightInput();
        if (source instanceof WindowNode) {
            WindowNode.WindowMemory memory = wm.getNodeMemory(((WindowNode) source));
            for (EventFactHandle factHandle : memory.getFactHandles()) {
                factHandle.forEachRightTuple( rt -> {
                    if (source.equals(rt.getTupleSink())) {
                        rt.unlinkFromRightParent();
                    }
                });
            }
        }
    }

    private static void unlinkRightTuples(RightTuple rightTuple) {
        for (RightTuple rt = rightTuple; rt != null; ) {
            RightTuple next = rt.getStagedNext();
            // this RightTuple could have been already unlinked by the former cycle
            if (rt.getFactHandle() != null) {
                rt.unlinkFromRightParent();
            }
            rt = next;
        }
    }

    /**
     * Populates the SegmentMemory with staged LeftTuples. If the parent is not a Beta or From node, it iterates up to find the first node with memory. If necessary
     * It traverses to the LiaNode's ObjectTypeNode. It then iterates the LeftTuple chains, where an existing LeftTuple is staged
     * as delete. Or a new LeftTuple is created and staged as an insert.
     */
    private static void processLeftTuples(LeftTupleNode node, InternalWorkingMemory wm, boolean insert, TerminalNode tn) {
        // *** if you make a fix here, it most likely needs to be in PhreakActivationIteratorToo ***

        // Must iterate up until a node with memory is found, this can be followed to find the LeftTuples
        // which provide the potential peer of the tuple being added or removed

        if ( node instanceof AlphaTerminalNode ) {
            processLeftTuplesOnLian( wm, insert, tn, (LeftInputAdapterNode) node );
            return;
        }

        Memory memory = wm.getNodeMemories().peekNodeMemory(node);
        if (memory == null || memory.getSegmentMemory() == null) {
            // segment has never been initialized, which means the rule(s) have never been linked and thus no Tuples to fix
            return;
        }
        SegmentMemory sm = memory.getSegmentMemory();

        while (NodeTypeEnums.LeftInputAdapterNode != node.getType()) {

            if (NodeTypeEnums.isBetaNode(node)) {
                BetaMemory    bm;
                if (NodeTypeEnums.AccumulateNode == node.getType()) {
                    AccumulateMemory am = (AccumulateMemory) memory;
                    bm = am.getBetaMemory();
                    FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                    Tuple        lt = BetaNode.getFirstTuple(bm.getLeftTupleMemory(), it);
                    for (; lt != null; lt = (LeftTuple) it.next(lt)) {
                        AccumulateContext accctx = (AccumulateContext) lt.getContextObject();
                        visitChild(accctx.getResultLeftTuple(), insert, wm, tn);
                    }
                } else if (NodeTypeEnums.ExistsNode == node.getType() &&
                           !((BetaNode)node).isRightInputIsRiaNode()) { // do not process exists with subnetworks
                    // If there is a subnetwork, then there is no populated RTM, but the LTM is populated,
                    // so this would be procsssed in the "else".

                    bm = (BetaMemory) wm.getNodeMemory((MemoryFactory) node);
                    FastIterator it = bm.getRightTupleMemory().fullFastIterator(); // done off the RightTupleMemory, as exists only have unblocked tuples on the left side
                    RightTuple   rt = (RightTuple) BetaNode.getFirstTuple(bm.getRightTupleMemory(), it);
                    for (; rt != null; rt = (RightTuple) it.next(rt)) {
                        for (LeftTuple lt = rt.getBlocked(); lt != null; lt = lt.getBlockedNext()) {
                            visitChild(wm, insert, tn, it, lt);
                        }
                    }
                } else {
                    bm = (BetaMemory) wm.getNodeMemory((MemoryFactory) node);
                    FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                    Tuple        lt = BetaNode.getFirstTuple(bm.getLeftTupleMemory(), it);
                    visitChild(wm, insert, tn, it, lt);
                }
                return;
            } else if (NodeTypeEnums.FromNode == node.getType()) {
                FromMemory   fm  = (FromMemory) wm.getNodeMemory((MemoryFactory) node);
                TupleMemory  ltm = fm.getBetaMemory().getLeftTupleMemory();
                FastIterator it  = ltm.fullFastIterator();
                for (LeftTuple lt = (LeftTuple) ltm.getFirst(null); lt != null; lt = (LeftTuple) it.next(lt)) {
                    visitChild(lt, insert, wm, tn);
                }
                return;
            }
            if (sm.getRootNode() == node) {
                sm = wm.getNodeMemory((MemoryFactory<Memory>) node.getLeftTupleSource()).getSegmentMemory();
            }
            node = node.getLeftTupleSource();
        }

        // No beta or from nodes, so must retrieve LeftTuples from the LiaNode.
        // This is done by scanning all the LeftTuples referenced from the FactHandles in the ObjectTypeNode
        processLeftTuplesOnLian( wm, insert, tn, (LeftInputAdapterNode) node );
    }

    private static void processLeftTuplesOnLian( InternalWorkingMemory wm, boolean insert, TerminalNode tn, LeftInputAdapterNode lian ) {
        ObjectSource os = lian.getObjectSource();
        while (os.getType() != NodeTypeEnums.ObjectTypeNode) {
            os = os.getParentObjectSource();
        }
        ObjectTypeNode otn  = (ObjectTypeNode) os;
        final ObjectTypeNodeMemory omem = wm.getNodeMemory(otn);
        if (omem == null) {
            // no OTN memory yet, i.e. no inserted matching objects, so no Tuples to process
            return;
        }

        Iterator<InternalFactHandle> it = omem.iterator();
        while (it.hasNext()) {
            InternalFactHandle fh = it.next();
            fh.forEachLeftTuple( lt -> {
                LeftTuple nextLt = lt.getHandleNext();

                // Each lt is for a different lian, skip any lian not associated with the rule. Need to use lt parent (souce) not child to check the lian.
                if (isAssociatedWith(lt.getTupleSource(), tn)) {
                    visitChild(lt, insert, wm, tn);

                    if (lt.getHandlePrevious() != null) {
                        lt.getHandlePrevious().setHandleNext( nextLt );
                        if (nextLt != null) {
                            nextLt.setHandlePrevious( lt.getHandlePrevious() );
                        }
                    }
                }
            });
        }
    }

    private static void visitChild(InternalWorkingMemory wm, boolean insert, TerminalNode tn, FastIterator it, Tuple lt) {
        for (; lt != null; lt = (LeftTuple) it.next(lt)) {
            LeftTuple childLt = lt.getFirstChild();
            while (childLt != null) {
                LeftTuple nextLt = childLt.getHandleNext();
                visitChild(childLt, insert, wm, tn);
                childLt = nextLt;
            }
        }
    }

    private static void visitChild(LeftTuple lt, boolean insert, InternalWorkingMemory wm, TerminalNode tn) {
        LeftTuple prevLt = null;
        LeftTupleSinkNode sink = lt.getTupleSink();

        for ( ; sink != null; sink = sink.getNextLeftTupleSinkNode() ) {

            if ( lt != null ) {
                if (isAssociatedWith(lt.getTupleSink(), tn)) {

                    if (lt.getTupleSink().getAssociatedTerminals().size() > 1) {
                        if (lt.getFirstChild() != null) {
                            for ( LeftTuple child = lt.getFirstChild(); child != null; child =  child.getHandleNext() ) {
                                visitChild(child, insert, wm, tn);
                            }
                        } else if (lt.getTupleSink().getType() == NodeTypeEnums.RightInputAdapterNode) {
                            insertPeerRightTuple(lt, wm, tn, insert);
                        }
                    } else if (!insert) {
                        iterateLeftTuple( lt, wm );
                        LeftTuple lt2 = null;
                        for ( LeftTuple peerLt = lt.getPeer();
                              peerLt != null && isAssociatedWith(peerLt.getTupleSink(), tn) && peerLt.getTupleSink().getAssociatedTerminals().size() == 1;
                              peerLt = peerLt.getPeer() ) {
                            iterateLeftTuple( peerLt, wm );
                            lt2 = peerLt;
                        }

                        // this sink is not shared and is associated with the rule being removed delete it's children
                        deleteLeftTuple(lt, lt2, prevLt);
                        break; // only one rule is deleted at a time, we know there are no more peers to delete so break.
                    }
                }

                prevLt = lt;
                lt = lt.getPeer();
            } else {
                // there is a sink without a peer LT, so create the peer LT
                prevLt = insertPeerLeftTuple(prevLt, sink, wm, insert);
            }
        }
    }

    private static void insertPeerRightTuple( LeftTuple lt, InternalWorkingMemory wm, TerminalNode tn, boolean insert ) {
        // There's a shared RightInputAdapterNode, so check if one of its sinks is associated only to the new rule
        LeftTuple prevLt = null;
        RightInputAdapterNode rian = lt.getTupleSink();

        for (ObjectSink sink : rian.getObjectSinkPropagator().getSinks()) {
            if (lt != null) {
                if (prevLt != null && !insert && isAssociatedWith(sink, tn) && sink.getAssociatedTerminals().size() == 1) {
                    prevLt.setPeer( null );
                }
                prevLt = lt;
                lt = lt.getPeer();
            } else if (insert) {
                BetaMemory bm = (BetaMemory) wm.getNodeMemory( (BetaNode) sink );
                prevLt = rian.createPeer( prevLt );
                bm.linkNode( (BetaNode) sink, wm );
                bm.getStagedRightTuples().addInsert((RightTuple)prevLt);
            }
        }
    }

    /**
     * Create all missing peers
     */
    private static LeftTuple insertPeerLeftTuple(LeftTuple lt, LeftTupleSinkNode node, InternalWorkingMemory wm, boolean insert) {
        LeftTuple peer = node.createPeer(lt);

        if ( node.getLeftTupleSource() instanceof AlphaTerminalNode ) {
            if (insert) {
                TerminalNode rtn = ( TerminalNode ) node;
                InternalAgenda agenda = wm.getAgenda();
                RuleAgendaItem agendaItem = AlphaTerminalNode.getRuleAgendaItem( wm, agenda, rtn, insert );
                PhreakRuleTerminalNode.doLeftTupleInsert( rtn, agendaItem.getRuleExecutor(), agenda, agendaItem, peer );
            }
            return peer;
        }

        LeftInputAdapterNode.LiaNodeMemory liaMem = null;
        if ( node.getLeftTupleSource().getType() == NodeTypeEnums.LeftInputAdapterNode ) {
            liaMem = wm.getNodeMemory(((LeftInputAdapterNode) node.getLeftTupleSource()));
        }

        Memory memory = wm.getNodeMemories().peekNodeMemory(node);
        if (memory == null || memory.getSegmentMemory() == null) {
            throw new IllegalStateException("Defensive Programming: this should not be possilbe, as the addRule code should init child segments if they are needed ");
        }

        if ( liaMem == null) {
            memory.getSegmentMemory().getStagedLeftTuples().addInsert(peer);
        } else {
            // If parent is Lian, then this must be called, so that any linking or unlinking can be done.
            LeftInputAdapterNode.doInsertSegmentMemoryWithFlush(wm, true, liaMem, memory.getSegmentMemory(), peer, node.getLeftTupleSource().isStreamMode());
        }

        return peer;
    }

    private static void iterateLeftTuple(LeftTuple lt, InternalWorkingMemory wm) {
        if (NodeTypeEnums.isTerminalNode(lt.getTupleSink())) {
            PathMemory pmem = (PathMemory) wm.getNodeMemories().peekNodeMemory( lt.getTupleSink() );
            if (pmem != null) {
                PhreakRuleTerminalNode.doLeftDelete( pmem.getActualActivationsManager( wm ), pmem.getRuleAgendaItem().getRuleExecutor(), lt );
            }
        } else {
            if (lt.getContextObject() instanceof AccumulateContext) {
                LeftTuple resultLt = (( AccumulateContext ) lt.getContextObject()).getResultLeftTuple();
                if (resultLt != null) {
                    iterateLeftTuple( resultLt, wm );
                }
            }
            for (LeftTuple child = lt.getFirstChild(); child != null; child = child.getHandleNext()) {
                for (LeftTuple peer = child; peer != null; peer = peer.getPeer()) {
                    if (peer.getPeer() == null) {
                        // it's unnnecessary to visit the unshared networks, so only iterate the last peer
                        iterateLeftTuple( peer, wm );
                    }
                }
            }
        }
    }

    private static void deleteLeftTuple(LeftTuple removingLt, LeftTuple removingLt2, LeftTuple prevLt) {
        // only the first LT in a peer chain is hooked into left and right parents or the FH.
        // If the first LT is being remove, those hooks need to be shifted to the next peer,
        // or nulled if there is no next peer.
        // When there is a subnetwork, it needs to shift to the peer of the next lt.
        // if it is not the first LT in the peer chain, leftParent and rightParent are null.
        // And the previous peer will need to point to the peer after removingLt, or removingLt2 if it exists.

        boolean isFirstLt = prevLt == null; // is this the first LT in a peer chain chain
        LeftTuple nextPeerLt    = (removingLt2 == null ) ? removingLt.getPeer() : removingLt2.getPeer(); // if there is a subnetwork, skip to the peer after that

        if( !isFirstLt ) {
            // This LT is not the first tuple in a peer chain. So just correct the peer chain linked list
            prevLt.setPeer( nextPeerLt );
        } else {
            if ( nextPeerLt == null ) {
                removingLt.unlinkFromLeftParent();
                removingLt.unlinkFromRightParent();
                return;
            }

            InternalFactHandle fh = removingLt.getFactHandle();

            // This is the first LT in a peer chain. Only this LT is hooked into the left and right parent LT and RT and
            // if it's the root (form the lian) it will be hooked itno the FH.
            LeftTuple leftPrevious = removingLt.getHandlePrevious();
            LeftTuple leftNext     = removingLt.getHandleNext();

            LeftTuple rightPrevious = removingLt.getRightParentPrevious();
            LeftTuple rightNext     = removingLt.getRightParentNext();

            LeftTuple  leftParent  = removingLt.getLeftParent();
            RightTuple rightParent = removingLt.getRightParent();

            // This tuple is the first peer and thus is linked into the left parent LT.

            nextPeerLt.setFactHandle(removingLt.getFactHandle());

            // correct the linked list
            if (leftPrevious != null) {
                nextPeerLt.setHandlePrevious(leftPrevious);
                leftPrevious.setHandleNext(nextPeerLt);
            }

            if (leftNext != null) {
                nextPeerLt.setHandleNext(leftNext);
                leftNext.setHandlePrevious(nextPeerLt);
            }

            // correct the linked list
            if (rightPrevious != null) {
                nextPeerLt.setRightParentPrevious(rightPrevious);
                rightPrevious.setRightParentNext(nextPeerLt);
            }

            if (rightNext != null) {
                nextPeerLt.setRightParentNext(rightNext);
                rightNext.setRightParentPrevious(nextPeerLt);
            }

            // correct the parent's first/last references
            if (leftParent!=null) {
                nextPeerLt.setLeftParent(leftParent);

                if (leftParent.getFirstChild() == removingLt) {
                    leftParent.setFirstChild(nextPeerLt);
                }

                if (leftParent.getLastChild() == removingLt) {
                    leftParent.setLastChild(nextPeerLt);
                }
            } else {
                // is the LT for the LIAN, if so we need to process the FH too
                fh.removeLeftTuple(removingLt);
                if (leftPrevious == null) {
                    // The removed tuple was first in linked list, add the peer at its original position
                    fh.addFirstLeftTuple( nextPeerLt );
                }
            }

            if ( rightParent != null ) {
                // This tuple is the first peer and thus is linked into the right parent RT.
                nextPeerLt.setRightParent(rightParent);

                // correct the parent's first/last references
                // if nextLT is null, it's ok for parent's reference to be null
                if (rightParent.getFirstChild() == removingLt) {
                    // if next peer exists, set it to this
                    rightParent.setFirstChild(nextPeerLt);
                }

                if (rightParent.getLastChild() == removingLt) {
                    rightParent.setLastChild(nextPeerLt);
                }
            }
        }
    }

    private static void addNetworkSplitPoint(LeftTupleNode node, Set<LeftTupleNode> splitNodes) {
        // It's ok to add nodes that are predecessors of existing nodes in the network.
        // due to subnetworks, both may need their LTs processing.
        while (node.getLeftTupleSource()  != null) {
            if (node.getLeftTupleSource().getAssociatedTerminals().size() != 1) {
                if ( node.getAssociatedTerminals().size() == 1) {
                    splitNodes.add(node.getLeftTupleSource());
                }
                return;
            }
            node = node.getLeftTupleSource();
        }

        // this only happens if it reaches the Lian, which we need to return if nothing else is reached.
        splitNodes.add(node);
    }

    public static SegmentMemory splitSegment(InternalWorkingMemory wm, SegmentMemory sm1, LeftTupleNode splitNode, RuleBase kbase) {
        // create new segment, starting after split
        LeftTupleNode childNode = splitNode.getSinkPropagator().getFirstLeftTupleSink();
        SegmentPrototype proto2 = kbase.getSegmentPrototype(childNode);
        SegmentMemory sm2 = proto2.newSegmentMemory(wm); // we know there is only one sink
        wm.getNodeMemories().peekNodeMemory( childNode ).setSegmentMemory( sm2 );

        // Move the children of sm1 to sm2
        if (sm1.getFirst() != null) {
            for (SegmentMemory sm = sm1.getFirst(); sm != null; ) {
                SegmentMemory next = sm.getNext();
                sm1.remove(sm);
                sm2.add(sm);
                sm = next;
            }
        }

        sm1.add(sm2);

        sm2.mergePathMemories(sm1);

        // preserve values that get changed updateSegmentMemory, for split
        long currentLinkedNodeMask  = sm1.getLinkedNodeMask();
        SegmentPrototype proto1 = kbase.getSegmentPrototype(sm1.getRootNode());
        proto1.updateSegmentMemory(sm1, wm);

        if (sm1.getTipNode().getType() == NodeTypeEnums.LeftInputAdapterNode) {
            if (!sm1.getStagedLeftTuples().isEmpty()) {
                // Segments with only LiaNode's cannot have staged LeftTuples, so move them down to the new Segment
                sm2.getStagedLeftTuples().addAll(sm1.getStagedLeftTuples());
            }
        }

        splitBitMasks(sm1, sm2, currentLinkedNodeMask);

        return sm2;
    }

    private static void mergeSegment(SegmentMemory sm1, SegmentMemory sm2, RuleBase kbase, InternalWorkingMemory wm) {
        if (sm1.getTipNode().getType() == NodeTypeEnums.LeftInputAdapterNode && !sm2.getStagedLeftTuples().isEmpty()) {
            // If a rule has not been linked, lia can still have child segments with staged tuples that did not get flushed
            // these are safe to just move to the parent SegmentMemory
            sm1.getStagedLeftTuples().addAll(sm2.getStagedLeftTuples());
        }

        // sm1 may not be linked yet to sm2 because sm2 has been just created
        if (sm1.contains(sm2)) {
            sm1.remove(sm2);
        }

        if (sm2.getFirst() != null) {
            for (SegmentMemory sm = sm2.getFirst(); sm != null; ) {
                SegmentMemory next = sm.getNext();
                sm2.remove(sm);
                sm1.add(sm);
                sm = next;
            }
        }

        // preserve values that get changed updateSegmentMemory, for merge
        long currentLinkedNodeMask = sm1.getLinkedNodeMask();
        SegmentPrototype proto1 = kbase.getSegmentPrototype(sm1.getRootNode());
        proto1.updateSegmentMemory(sm1, wm);

        mergeBitMasks(sm1, sm2, currentLinkedNodeMask);


    }

    private static void splitBitMasks(SegmentMemory sm1, SegmentMemory sm2, long currentLinkedNodeMask) {
        // @TODO Haven't made this work for more than 64 nodes, as per SegmentUtilities.nextNodePosMask (mdp)
        int  splitPos              = sm1.getSegmentPrototype().getNodesInSegment().length; // +1 as zero based
        long currentDirtyNodeMask  = sm1.getDirtyNodeMask();
        long splitAsBinary         = (1L << splitPos) - 1;

        sm1.setDirtyNodeMask(currentDirtyNodeMask & splitAsBinary);
        sm1.setLinkedNodeMask(currentLinkedNodeMask& sm1.getAllLinkedMaskTest());

        sm2.setLinkedNodeMask(currentLinkedNodeMask >> splitPos);
        sm2.setDirtyNodeMask(currentDirtyNodeMask >> splitPos);
    }

    private static void mergeBitMasks(SegmentMemory sm1, SegmentMemory sm2, long currentLinkedNodeMask) {
        int shiftBits = 0;
        for (LeftTupleNode node : sm1.getSegmentPrototype().getNodesInSegment()) {
            if (node == sm2.getRootNode()) {
                break;
            }
            shiftBits++;
        }
        long linkedBitsToAdd = sm2.getLinkedNodeMask() << shiftBits;
        long dirtyBitsToAdd = sm2.getLinkedNodeMask() << shiftBits;
        sm1.setLinkedNodeMask(linkedBitsToAdd | currentLinkedNodeMask);
        sm1.setDirtyNodeMask(dirtyBitsToAdd | sm1.getDirtyNodeMask());

    }

    private static PathEndNodeMemories getPathEndMemories(InternalWorkingMemory wm,
                                                          PathEndNodes pathEndNodes) {
        PathEndNodeMemories tnMems = new PathEndNodeMemories();

        for (LeftTupleNode node : pathEndNodes.otherEndNodes) {
            PathMemory pmem = (PathMemory) wm.getNodeMemories().peekNodeMemory(node);
            if (pmem != null) {
                tnMems.otherPmems.add(pmem);
            }
        }

        tnMems.subjectPmem = (PathMemory) wm.getNodeMemories().peekNodeMemory(pathEndNodes.subjectTermNode);
        if (tnMems.subjectPmem == null && !tnMems.otherPmems.isEmpty()) {
            // If "other pmem's are initialized, then the subject needs to be initialized too.
            tnMems.subjectPmem = RuntimeSegmentUtilities.initializePathMemory(wm, pathEndNodes.subjectTermNode);
        }

        for (PathEndNode node : pathEndNodes.subjectEndNodes) {
            PathMemory pmem = (PathMemory) wm.getNodeMemories().peekNodeMemory(node);
            if (pmem == null && !tnMems.otherPmems.isEmpty()) {
                pmem = RuntimeSegmentUtilities.initializePathMemory(wm, node);
            }
            if (pmem != null) {
                tnMems.subjectPmems.add(pmem);
            }
        }

        return tnMems;
    }

    private static class PathEndNodeMemories {
        PathMemory subjectPmem;
        List<PathMemory> subjectPmems = new ArrayList<>();
        List<PathMemory> otherPmems = new ArrayList<>();
    }

    private static PathEndNodes getPathEndNodes(RuleBase kBase,
                                                TerminalNode tn,
                                                Set<LeftTupleNode> splitNodes) {
        PathEndNodes endNodes = new PathEndNodes();
        endNodes.subjectTermNode = tn;
        endNodes.subjectEndNodes.add(tn);
        endNodes.subjectSplits.addAll(splitNodes);

        Set<Integer> visited = new HashSet<>();
        if (!endNodes.subjectSplits.isEmpty()) {
            // first the root node of the segment that is being split
            endNodes.subjectSplits.stream().forEach(n -> invalidateRootNode(kBase, n, endNodes));

            // collect all the PathEndNodes frmo the split point
            endNodes.subjectSplits.stream().forEach(n -> collectPathEndNodes(kBase, n, endNodes, tn,visited));
        }

        return endNodes;
    }

    private static void collectPathEndNodes(RuleBase kBase,
                                            LeftTupleNode lt,
                                            PathEndNodes endNodes,
                                            TerminalNode tn,
                                            Set<Integer> visited) {
        // Traverses the sinks in reverse order in order to collect PathEndNodes so that
        // the outermost (sub)network are evaluated before the innermost one
        for (LeftTupleSinkNode sink = lt.getSinkPropagator().getLastLeftTupleSink(); sink != null; sink = sink.getPreviousLeftTupleSinkNode()) {
            if (!visited.add(sink.getId())) {
                // do not visit this node, if it's already in the visited set
                continue;
            }
            if ( isRootNode( sink, null )) {
                endNodes.nodesToInvalidate.add(sink);
            }

            if (NodeTypeEnums.isLeftTupleSource(sink)) {
                collectPathEndNodes(kBase, sink, endNodes, tn, visited);
            } else if (NodeTypeEnums.isTerminalNode(sink)) {
                if (sink != tn) {
                    endNodes.otherTermNodes.add((TerminalNode) sink);
                    endNodes.otherEndNodes.add((TerminalNode) sink);
                }
            } else if (NodeTypeEnums.RightInputAdapterNode == sink.getType()) {
                if (BuildtimeSegmentUtilities.sinkNotExclusivelyAssociatedWithTerminal(tn, sink)) {
                    // Add every terminal node in it's PathEndNodes array. The set removes duplicates.
                    // This is a bit brute force heavy, but the code isn't currently smart enough to do this partially, so it'll always just start from all assocaited tns.
                    Arrays.stream(((PathEndNode) sink).getPathEndNodes()).filter( n -> NodeTypeEnums.isTerminalNode(n)).forEach( endNode -> {
                        if ( endNode != tn) {
                            endNodes.otherTermNodes.add((TerminalNode) endNode);
                        }
                    });
                    endNodes.otherEndNodes.add( (PathEndNode) sink );
                } else {
                    endNodes.subjectEndNodes.add( (PathEndNode) sink );
                }
            } else {
                if (!(NodeTypeEnums.isTerminalNode(sink) && isAssociatedWith(sink, tn))) {
                    throw new RuntimeException("Error: Unknown Node. Defensive programming test..");
                }
            }
        }
    }

    private static void invalidateRootNode( RuleBase kBase, LeftTupleNode lt, PathEndNodes endNodes ) {
        while (!isRootNode( lt, null )) {
            lt = lt.getLeftTupleSource();
        }
        endNodes.nodesToInvalidate.add(lt);
    }

    private static class PathEndNodes {
        TerminalNode   subjectTermNode;
        Set<PathEndNode>   subjectEndNodes = new HashSet<>();
        Set<LeftTupleNode> subjectSplits   = new HashSet<>();
        Set<PathEndNode>   otherEndNodes   = new HashSet<>();

        Set<TerminalNode>   otherTermNodes   = new HashSet<>();

        private Set<LeftTupleNode> nodesToInvalidate = new HashSet<>();
    }
}