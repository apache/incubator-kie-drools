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


import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.TupleSets;
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.AbstractTerminalNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
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
import org.drools.core.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SegmentNodeMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;
import org.kie.api.definition.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.drools.core.phreak.SegmentUtilities.isRootNode;

public class AddRemoveRule {

    private static final Logger log = LoggerFactory.getLogger(AddRemoveRule.class);

    /**
     * This method is called after the rule nodes have been added to the network
     * For add tuples are processed after the segments and pmems have been adjusted
     *
     * @param tn
     * @param wms
     * @param kBase
     */
    public static void addRule(TerminalNode tn, InternalWorkingMemory[] wms, InternalKnowledgeBase kBase) {
        if (log.isTraceEnabled()) {
            log.trace("Adding Rule {}", tn.getRule().getName());
        }

        boolean hasProtos = kBase.hasSegmentPrototypes();
        boolean hasWms = wms.length > 0;

        if (!hasProtos && !hasWms) {
            return;
        }

        RuleImpl rule = tn.getRule();
        LeftTupleNode firstSplit = getNetworkSplitPoint(tn);
        PathEndNodes pathEndNodes = getPathEndNodes(kBase, firstSplit, tn, rule, hasProtos, hasWms);

        // Insert the facts for the new paths. This will iterate each new path from EndNode to the splitStart - but will not process the splitStart itself (as tha already exist).
        // It does not matter that the prior segments have not yet been processed for splitting, as this will only apply for branches of paths that did not exist before

        for (InternalWorkingMemory wm : wms) {
            wm.flushPropagations();

            if (NodeTypeEnums.LeftInputAdapterNode == firstSplit.getType() && firstSplit.getAssociationsSize() == 1) {
                // rule added with no sharing
                insertLiaFacts(firstSplit, wm);
            } else {
                PathEndNodeMemories tnms = getPathEndMemories(wm, pathEndNodes);

                if (tnms.subjectPmem == null) {
                    // If the existing PathMemories are not yet initialized there are no Segments or tuples to process
                    continue;
                }

                Map<PathMemory, SegmentMemory[]> prevSmemsLookup = reInitPathMemories(wm, tnms.otherPmems, null);

                // must collect all visited SegmentMemories, for link notification
                Set<SegmentMemory> smemsToNotify = handleExistingPaths(tn, prevSmemsLookup, tnms.otherPmems, wm, ExistingPathStrategy.ADD_STRATEGY);

                addNewPaths(wm, smemsToNotify, tnms.subjectPmems);

                processLeftTuples(firstSplit, wm, true, rule);

                notifySegments(smemsToNotify, wm);
            }
        }

        if (hasWms) {
            insertFacts( pathEndNodes, wms );
        }
    }

    /**
     * This method is called before the rule nodes are removed from the network.
     * For remove tuples are processed before the segments and pmems have been adjusted
     *
     * @param tn
     * @param wms
     * @param kBase
     */
    public static void removeRule(TerminalNode tn, InternalWorkingMemory[] wms, InternalKnowledgeBase kBase) {
        if (log.isTraceEnabled()) {
            log.trace("Removing Rule {}", tn.getRule().getName());
        }

        boolean hasProtos = kBase.hasSegmentPrototypes();
        boolean hasWms = wms.length > 0;

        if (!hasProtos && !hasWms) {
            return;
        }

        RuleImpl      rule       = tn.getRule();
        LeftTupleNode firstSplit = getNetworkSplitPoint(tn);
        PathEndNodes pathEndNodes = getPathEndNodes(kBase, firstSplit, tn, rule, hasProtos, hasWms);

        for (InternalWorkingMemory wm : wms) {
            wm.flushPropagations();

            PathEndNodeMemories tnms = getPathEndMemories(wm, pathEndNodes);

            if ( !tnms.subjectPmems.isEmpty() ) {
                if (NodeTypeEnums.LeftInputAdapterNode == firstSplit.getType() && firstSplit.getAssociationsSize() == 1) {
                    if (tnms.subjectPmem != null) {
                        flushStagedTuples(firstSplit, tnms.subjectPmem, wm);
                    }

                    processLeftTuples(firstSplit, wm, false, tn.getRule());

                    removeNewPaths(wm, tnms.subjectPmems);
                } else {
                    flushStagedTuples(tn, tnms.subjectPmem, pathEndNodes, wm);

                    processLeftTuples(firstSplit, wm, false, tn.getRule());

                    removeNewPaths(wm, tnms.subjectPmems);

                    Map<PathMemory, SegmentMemory[]> prevSmemsLookup = reInitPathMemories(wm, tnms.otherPmems, tn);

                    // must collect all visited SegmentMemories, for link notification
                    Set<SegmentMemory> smemsToNotify = handleExistingPaths(tn, prevSmemsLookup, tnms.otherPmems, wm, ExistingPathStrategy.REMOVE_STRATEGY);

                    notifySegments(smemsToNotify, wm);
                }
            }

            if (tnms.subjectPmem != null && tnms.subjectPmem.isInitialized() && tnms.subjectPmem.getRuleAgendaItem().isQueued()) {
                // SubjectPmem can be null, if it was never initialized
                tnms.subjectPmem.getRuleAgendaItem().dequeue();
            }
        }
    }

    public interface ExistingPathStrategy {
        ExistingPathStrategy ADD_STRATEGY = new AddExistingPaths();
        ExistingPathStrategy REMOVE_STRATEGY = new RemoveExistingPaths();

        SegmentMemory[] getSegmenMemories(PathMemory pmem);

        void adjustSegment(InternalWorkingMemory wm, Set<SegmentMemory> smemsToNotify, SegmentMemory smem, int smemSplitAdjustAmount);

        void handleSplit(PathMemory pmem, SegmentMemory[] prevSmems, SegmentMemory[] smems, int smemIndex, int prevSmemIndex,
                         LeftTupleNode parentNode, LeftTupleNode node, TerminalNode tn,
                         Set<LeftTupleNode> visited, Set<SegmentMemory> smemsToNotify, Map<LeftTupleNode, SegmentMemory> nodeToSegmentMap,
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
        public void adjustSegment(InternalWorkingMemory wm, Set<SegmentMemory> smemsToNotify, SegmentMemory smem, int smemSplitAdjustAmount) {
            smemsToNotify.add(smem);
            smem.unlinkSegment(wm);
            correctSegmentMemoryAfterSplitOnAdd(smem, smemSplitAdjustAmount);
        }

        @Override
        public void handleSplit(PathMemory pmem, SegmentMemory[] prevSmems, SegmentMemory[] smems, int smemIndex, int prevSmemIndex,
                                LeftTupleNode parentNode, LeftTupleNode node, TerminalNode tn,
                                Set<LeftTupleNode> visited, Set<SegmentMemory> smemsToNotify, Map<LeftTupleNode, SegmentMemory> nodeToSegmentMap,
                                InternalWorkingMemory wm) {
            if (smems[smemIndex - 1] != null) {
                SegmentMemory sm2 = nodeToSegmentMap.get(node);
                if (sm2 == null) {
                    SegmentMemory sm1 = smems[smemIndex - 1];
                    correctMemoryOnSplitsChanged(parentNode, null, wm);
                    sm2 = splitSegment(wm, sm1, parentNode);
                    nodeToSegmentMap.put(node, sm2);
                    smemsToNotify.add(sm1);
                    smemsToNotify.add(sm2);
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
        public void adjustSegment(InternalWorkingMemory wm, Set<SegmentMemory> smemsToNotify, SegmentMemory smem, int smemSplitAdjustAmount) {
            smemsToNotify.add(smem);
            smem.unlinkSegment(wm);
            correctSegmentMemoryAfterSplitOnRemove(smem, smemSplitAdjustAmount);
        }

        @Override
        public void handleSplit(PathMemory pmem, SegmentMemory[] prevSmems, SegmentMemory[] smems, int smemIndex, int prevSmemIndex,
                                LeftTupleNode parentNode, LeftTupleNode node, TerminalNode tn,
                                Set<LeftTupleNode> visited, Set<SegmentMemory> smemsToNotify, Map<LeftTupleNode, SegmentMemory> nodeToSegmentMap,
                                InternalWorkingMemory wm) {
            if (visited.contains(node)) {
                return;
            }

            correctMemoryOnSplitsChanged(parentNode, tn, wm);

            SegmentMemory sm1 = smems[smemIndex];
            SegmentMemory sm2 = prevSmems[prevSmemIndex];

            if (sm1 != null && sm2 == null) {
                sm2 = SegmentUtilities.createChildSegment(wm,node);
                prevSmems[prevSmemIndex] = sm2;
                sm1.add(sm2);
            } else if (sm1 == null && sm2 != null) {
                sm1 = SegmentUtilities.createChildSegment(wm, parentNode);
                smems[smemIndex] = sm1;
                sm1.add(sm2);
            }

            if (sm1 != null && sm2 != null) {
                mergeSegment(sm1, sm2);
                smemsToNotify.add(sm1);
                sm1.unlinkSegment(wm);
                sm2.unlinkSegment(wm);
                visited.add(node);
            }
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

    private static Set<SegmentMemory> handleExistingPaths(TerminalNode tn, Map<PathMemory, SegmentMemory[]> prevSmemsLookup,
                                                          List<PathMemory> pmems, InternalWorkingMemory wm, ExistingPathStrategy strategy) {
        Set<SegmentMemory>                smemsToNotify    = new HashSet<SegmentMemory>();
        Set<SegmentMemory>                visitedSegments  = new HashSet<SegmentMemory>();
        Set<LeftTupleNode> visitedNodes = new HashSet<LeftTupleNode>();
        Map<LeftTupleNode, SegmentMemory> nodeToSegmentMap = new HashMap<LeftTupleNode, SegmentMemory>();

        for (PathMemory pmem : pmems) {
            LeftTupleNode[] nodes = pmem.getPathEndNode().getPathNodes();

            SegmentMemory[] prevSmems = prevSmemsLookup.get(pmem);
            SegmentMemory[] smems     = strategy.getSegmenMemories(pmem);

            LeftTupleNode node;
            int           prevSmemIndex         = 0;
            int           smemIndex             = 0;
            int           smemSplitAdjustAmount = 0;
            int           nodeIndex             = 0;
            int           nodeTypesInSegment    = 0;

            // excluding the rule just added iterate while not split (i.e. find the next split, prior to this rule being added)
            // note it's checking for when the parent is the split, and thus node is the next root root.

            smems[smemIndex] = prevSmems[prevSmemIndex];
            do {
                node = nodes[nodeIndex++];
                LeftTupleNode parentNode = node.getLeftTupleSource();
                nodeTypesInSegment = SegmentUtilities.updateNodeTypesMask( parentNode, nodeTypesInSegment );
                if (isSplit(parentNode)) {
                    smemIndex = strategy.incSmemIndex1(smemIndex);
                    prevSmemIndex = strategy.incPrevSmemIndex1(prevSmemIndex);
                    if (isSplit(parentNode, tn)) { // check if the split is there even without the processed rule
                        smemIndex = strategy.incSmemIndex2(smemIndex);
                        prevSmemIndex = strategy.incPrevSmemIndex2(prevSmemIndex);
                        smems[smemIndex] = prevSmems[prevSmemIndex];
                        if ( smems[smemIndex] != null && smemSplitAdjustAmount > 0 && visitedSegments.add(smems[smemIndex])) {
                            strategy.adjustSegment( wm, smemsToNotify, smems[smemIndex], smemSplitAdjustAmount );
                        }
                    } else {
                        strategy.handleSplit(pmem, prevSmems, smems, smemIndex, prevSmemIndex,
                                             parentNode, node, tn, visitedNodes,
                                             smemsToNotify, nodeToSegmentMap, wm);
                        smemSplitAdjustAmount++;
                    }
                    SegmentUtilities.checkEagerSegmentCreation( (LeftTupleSource) parentNode, wm, nodeTypesInSegment );
                    nodeTypesInSegment = 0;
                }
            } while (!NodeTypeEnums.isEndNode(node));
            strategy.processSegmentMemories(smems, pmem);
        }
        return smemsToNotify;
    }

    private static void addNewPaths(InternalWorkingMemory wm, Set<SegmentMemory> smemsToNotify, List<PathMemory> pmems) {
        // Multiple paths may be renetrant, in the case of a second subnetwork on the same rule.
        // Must make sure we don't duplicate the child smem, and when found, just update it with new pmem.
        Set<LeftTupleNode> visited = new HashSet<LeftTupleNode>();
        for (PathMemory pmem : pmems) {
            LeftTupleSink tipNode = (LeftTupleSink) pmem.getPathEndNode();

            LeftTupleNode child  = tipNode;
            LeftTupleNode parent = tipNode.getLeftTupleSource();

            while (true) {
                if (visited.add(child)) {
                    if ( parent != null && parent.getAssociationsSize() != 1 && child.getAssociationsSize() == 1 ) {
                        // This is the split point that the new path enters an existing path.
                        // If the parent has other child SegmentMemorys then it must create a new child SegmentMemory
                        // If the parent is a query node, then it's internal data structure needs changing
                        // all right input data must be propagated
                        Memory mem = wm.getNodeMemories().peekNodeMemory( parent.getId() );
                        if ( mem != null && mem.getSegmentMemory() != null ) {
                            SegmentMemory sm = mem.getSegmentMemory();
                            if ( sm.getFirst() != null && sm.size() < parent.getSinkPropagator().size()) {
                                LeftTupleSink[] sinks = parent.getSinkPropagator().getSinks();
                                for (int i = sm.size(); i < sinks.length; i++) {
                                    SegmentMemory childSmem = SegmentUtilities.createChildSegment( wm, sinks[i] );
                                    sm.add( childSmem );
                                    pmem.setSegmentMemory( childSmem.getPos(), childSmem );
                                    smemsToNotify.add( childSmem );
                                }
                            }
                            correctMemoryOnSplitsChanged( parent, null, wm );
                        }
                    } else {
                        Memory mem = wm.getNodeMemories().peekNodeMemory( child.getId() );
                        // The root of each segment
                        if ( mem != null ) {
                            SegmentMemory sm = mem.getSegmentMemory();
                            if ( sm != null && !sm.getPathMemories().contains( pmem ) ) {
                                sm.addPathMemory( pmem );
                                pmem.setSegmentMemory( sm.getPos(), sm );
                                sm.notifyRuleLinkSegment( wm, pmem );
                            }
                        }
                    }
                } else {
                    Memory mem = wm.getNodeMemories().peekNodeMemory( child.getId() );
                    if ( mem != null ) {
                        mem.getSegmentMemory().notifyRuleLinkSegment( wm, pmem );
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
        Set<Integer> visitedNodes = new HashSet<Integer>();
        for (PathMemory pmem : pmems) {
            LeftTupleSink tipNode = (LeftTupleSink) pmem.getPathEndNode();

            LeftTupleNode child  = tipNode;
            LeftTupleNode parent = tipNode.getLeftTupleSource();

            while (true) {
                if (child.getAssociationsSize() == 1 && NodeTypeEnums.isBetaNode(child)) {
                    // If this is a beta node, it'll delete all the right input data
                    deleteRightInputData((LeftTupleSink) child, wm);
                }

                if (parent != null && parent.getAssociationsSize() != 1 && child.getAssociationsSize() == 1) {
                    // This is the split point that the new path enters an existing path.
                    // If the parent has other child SegmentMemorys then it must create a new child SegmentMemory
                    // If the parent is a query node, then it's internal data structure needs changing
                    // all right input data must be propagated
                    if (!visitedNodes.contains( child.getId() )) {
                        Memory mem = wm.getNodeMemories().peekNodeMemory( parent.getId() );
                        if ( mem != null && mem.getSegmentMemory() != null ) {
                            SegmentMemory sm = mem.getSegmentMemory();
                            if ( sm.getFirst() != null ) {
                                SegmentMemory childSm = wm.getNodeMemories().peekNodeMemory( child.getId() ).getSegmentMemory();
                                sm.remove( childSm );
                            }
                        }
                    }
                } else {
                    Memory mem = wm.getNodeMemories().peekNodeMemory(child.getId());
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
        return node != null && SegmentUtilities.isTipNode( node, removingTN );
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
            new RuleNetworkEvaluator().evaluateNetwork(pmem, pmem.getRuleAgendaItem().getRuleExecutor(), wm);
        }

        // With the removing rules being flushed, we need to check any splits that will be merged, to see if they need flushing
        // Beware that flushing a higher up node, might again cause lower nodes to have more staged items. So track flushed items
        // incase they need to be reflushed
        List<Flushed> flushed = new ArrayList<Flushed>();

        for ( LeftTupleNode node : pathEndNodes.subjectSplits ) {
            if (!isSplit(node, tn)) { // check if the split is there even without the processed rule
                Memory mem = wm.getNodeMemories().peekNodeMemory(node.getId());
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
        int             smemIndex = getSegmentPos(splitStartNode); // index before the segments are merged
        SegmentMemory[] smems     = pmem.getSegmentMemories();

        SegmentMemory   sm        = null;

        // If there is no sharing, then there will not be any staged tuples in later segemnts, and thus no need to search for them if the current sm is empty.
        int length = smems.length;
        if ( splitStartNode.getAssociationsSize() == 1 ) {
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

    public static boolean flushLeftTupleIfNecessary(InternalWorkingMemory wm, SegmentMemory sm, LeftTuple leftTuple, boolean streamMode) {
        PathMemory pmem = streamMode ?
                          getPathMemoryToFlushInStreamMode( sm, leftTuple ) :
                          getPathMemoryToFlushForEagerEvaluation( sm, leftTuple );

        if ( pmem == null ) {
            return false;
        }

        TupleSets<LeftTuple> leftTupleSets = new TupleSetsImpl<LeftTuple>();
        if (leftTuple != null) {
            leftTupleSets.addInsert(leftTuple);
        }

        forceFlushLeftTuple( pmem, sm, wm, leftTupleSets );

        if (pmem.isDataDriven() && pmem.getNodeType() == NodeTypeEnums.RightInputAdaterNode) {
            for (PathEndNode pnode : pmem.getPathEndNode().getPathEndNodes()) {
                if ( pnode instanceof TerminalNode ) {
                    PathMemory outPmem = wm.getNodeMemory( (TerminalNode) pnode );
                    if (outPmem.isDataDriven()) {
                        SegmentMemory outSmem = outPmem.getSegmentMemories()[0];
                        if (outSmem != null) {
                            forceFlushLeftTuple( outPmem, outSmem, wm, new TupleSetsImpl<LeftTuple>() );
                        }
                    }
                }
            }
        }

        return true;
    }

    private static PathMemory getPathMemoryToFlushForEagerEvaluation( SegmentMemory sm, LeftTuple leftTuple ) {
        PathMemory pmem = sm.getFirstDataDrivenPathMemory();
        if ( leftTuple == null && pmem != null && !pmem.isRuleLinked() ) {
            // skip the first, we already know it isn't linked
            pmem = getFirstLinkedPathMemory( sm, sm.getDataDrivenPathMemories(), 1 );
        }
        return pmem;
    }

    private static PathMemory getPathMemoryToFlushInStreamMode( SegmentMemory sm, LeftTuple leftTuple ) {
        return leftTuple != null && leftTuple.getPropagationContext().isMarshalling() ?
               sm.getPathMemories().get(0) : // marshalling requires flushing even if the pmem is unlinked
               getFirstLinkedPathMemory( sm, sm.getPathMemories(), 0 );
    }

    private static PathMemory getFirstLinkedPathMemory( SegmentMemory sm, List<PathMemory> pmems, int start ) {
        for (int i = start; i < pmems.size(); i++) {
            if (pmems.get(i).isRuleLinked()) {
                return pmems.get(i);
            }
        }
        return null;
    }

    public static void forceFlushLeftTuple(PathMemory pmem, SegmentMemory sm, InternalWorkingMemory wm, TupleSets<LeftTuple> leftTupleSets) {
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
                             wm.getNodeMemory((AbstractTerminalNode) pmem.getPathEndNode().getPathEndNodes()[0]);

        new RuleNetworkEvaluator().outerEval(pmem, node, bit, mem, smems, sm.getPos(), leftTupleSets, wm,
                                             new LinkedList<StackEntry>(),
                                             true, rtnPmem.getOrCreateRuleAgendaItem(wm).getRuleExecutor());
    }


    private static Map<PathMemory, SegmentMemory[]> reInitPathMemories(InternalWorkingMemory wm, List<PathMemory> pathMems, TerminalNode removingTN) {
        Map<PathMemory, SegmentMemory[]> previousSmems = new HashMap<PathMemory, SegmentMemory[]>();
        for (PathMemory pmem : pathMems) {
            // Re initialise all the PathMemories
            previousSmems.put(pmem, pmem.getSegmentMemories());
            LeftTupleSource startRianLts = null;
            if (!NodeTypeEnums.isTerminalNode(pmem.getPathEndNode())) {
                RightInputAdapterNode rian = (RightInputAdapterNode) pmem.getPathEndNode();
                startRianLts = rian.getStartTupleSource();
            }
            AbstractTerminalNode.initPathMemory(pmem, startRianLts, wm, removingTN); // re-initialise the PathMemory
        }
        return previousSmems;
    }

    private static void notifySegments(Set<SegmentMemory> smems, InternalWorkingMemory wm) {
        for (SegmentMemory sm : smems) {
            sm.notifyRuleLinkSegment(wm);
        }
    }

    private static void correctMemoryOnSplitsChanged(LeftTupleNode splitStart, TerminalNode removingTN, InternalWorkingMemory wm) {
        if (splitStart.getType() == NodeTypeEnums.UnificationNode) {
            QueryElementNode.QueryElementNodeMemory mem = (QueryElementNode.QueryElementNodeMemory) wm.getNodeMemories().peekNodeMemory(splitStart.getId());
            if (mem != null) {
                mem.correctMemoryOnSinksChanged(removingTN);
            }
        }
    }


    public static void correctSegmentMemoryAfterSplitOnAdd(SegmentMemory sm) {
        correctSegmentMemoryAfterSplitOnAdd(sm, 1);
    }

    public static void correctSegmentMemoryAfterSplitOnAdd(SegmentMemory sm, int i) {
        sm.setPos(sm.getPos() + i);
        sm.setSegmentPosMaskBit(sm.getSegmentPosMaskBit() << i);
    }

    public static void correctSegmentMemoryAfterSplitOnRemove(SegmentMemory sm, int i) {
        sm.setPos(sm.getPos() - i);
        sm.setSegmentPosMaskBit(sm.getSegmentPosMaskBit() >> i);
    }

    private static int getSegmentPos(LeftTupleNode lts) {
        int counter = 0;
        while (lts.getType() != NodeTypeEnums.LeftInputAdapterNode) {
            lts = lts.getLeftTupleSource();
            if (SegmentUtilities.isTipNode(lts, null)) {
                counter++;
            }
        }
        return counter;
    }

    private static void insertLiaFacts(LeftTupleNode startNode, InternalWorkingMemory wm) {
        // rule added with no sharing
        PropagationContextFactory pctxFactory = wm.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
        final PropagationContext  pctx        = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION, null, null, null);
        LeftInputAdapterNode      lian        = (LeftInputAdapterNode) startNode;
        RightTupleSinkAdapter     liaAdapter  = new RightTupleSinkAdapter(lian);
        lian.getObjectSource().updateSink(liaAdapter, pctx, wm);
    }

    private static void insertFacts(PathEndNodes endNodes, InternalWorkingMemory[] wms) {
        Set<LeftTupleNode> visited = new HashSet<LeftTupleNode>();

        for ( PathEndNode endNode : endNodes.subjectEndNodes ) {
            LeftTupleNode[]  nodes = endNode.getPathNodes();
            for ( int i = nodes.length-1; i >= 0 && nodes[i].getAssociationsSize() == 1; i-- ) {
                LeftTupleNode node = nodes[i];
                if  ( NodeTypeEnums.isBetaNode(node)  ) {
                    if (!visited.add( node )) {
                        continue;// this is to avoid rentering a path, and processing nodes twice. This can happen for nested subnetworks.
                    }
                    BetaNode bn = (BetaNode) node;

                    if (!bn.isRightInputIsRiaNode()) {
                        for ( int j = 0; j < wms.length; j++ ) {
                            PropagationContextFactory pctxFactory = wms[j].getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
                            final PropagationContext pctx = pctxFactory.createPropagationContext(wms[j].getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION, null, null, null);
                            bn.getRightInput().updateSink(bn,
                                                          pctx,
                                                          wms[j]);
                        }
                    }
                }
            }
        }
    }

    private static void deleteRightInputData(LeftTupleSink node, InternalWorkingMemory wm) {
        if (wm.getNodeMemories().peekNodeMemory(node.getId()) != null) {
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
            WindowNode.WindowMemory memory = (WindowNode.WindowMemory) wm.getNodeMemory(((WindowNode) source));
            for (EventFactHandle factHandle : memory.getFactHandles()) {
                for (RightTuple rightTuple = factHandle.getFirstRightTuple(); rightTuple != null; ) {
                    RightTuple nextRightTuple = rightTuple.getHandleNext();
                    if (source.equals(rightTuple.getTupleSink())) {
                        rightTuple.unlinkFromRightParent();
                    }
                    rightTuple = nextRightTuple;
                }
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
    private static void processLeftTuples(LeftTupleNode node, InternalWorkingMemory wm, boolean insert, Rule rule) {
        // *** if you make a fix here, it most likely needs to be in PhreakActivationIteratorToo ***

        // Must iterate up until a node with memory is found, this can be followed to find the LeftTuples
        // which provide the potential peer of the tuple being added or removed

        Memory memory = wm.getNodeMemories().peekNodeMemory(node.getId());
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
                        visitChild(accctx.getResultLeftTuple(), insert, wm, rule);
                    }
                } else if (NodeTypeEnums.ExistsNode == node.getType()) {
                    bm = (BetaMemory) wm.getNodeMemory((MemoryFactory) node);
                    FastIterator it = bm.getRightTupleMemory().fullFastIterator(); // done off the RightTupleMemory, as exists only have unblocked tuples on the left side
                    RightTuple   rt = (RightTuple) BetaNode.getFirstTuple(bm.getRightTupleMemory(), it);
                    for (; rt != null; rt = (RightTuple) it.next(rt)) {
                        for (LeftTuple lt = rt.getBlocked(); lt != null; lt = lt.getBlockedNext()) {
                            visitChild(wm, insert, rule, it, lt);
                        }
                    }
                } else {
                    bm = (BetaMemory) wm.getNodeMemory((MemoryFactory) node);
                    FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                    Tuple        lt = BetaNode.getFirstTuple(bm.getLeftTupleMemory(), it);
                    visitChild(wm, insert, rule, it, lt);
                }
                return;
            } else if (NodeTypeEnums.FromNode == node.getType()) {
                FromMemory   fm  = (FromMemory) wm.getNodeMemory((MemoryFactory) node);
                TupleMemory  ltm = fm.getBetaMemory().getLeftTupleMemory();
                FastIterator it  = ltm.fullFastIterator();
                for (LeftTuple lt = (LeftTuple) ltm.getFirst(null); lt != null; lt = (LeftTuple) it.next(lt)) {
                    visitChild(lt, insert, wm, rule);
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
        LeftInputAdapterNode lian = (LeftInputAdapterNode) node;

        ObjectSource os = lian.getObjectSource();
        while (os.getType() != NodeTypeEnums.ObjectTypeNode) {
            os = os.getParentObjectSource();
        }
        ObjectTypeNode             otn  = (ObjectTypeNode) os;
        final ObjectTypeNodeMemory omem = wm.getNodeMemory(otn);
        if (omem == null) {
            // no OTN memory yet, i.e. no inserted matching objects, so no Tuples to process
            return;
        }

        Iterator<InternalFactHandle> it = omem.iterator();
        while (it.hasNext()) {
            InternalFactHandle fh = it.next();
            LeftTuple lt = fh.getFirstLeftTuple();
            while (lt != null) {
                LeftTuple nextLt = lt.getHandleNext();

                // Each lt is for a different lian, skip any lian not associated with the rule. Need to use lt parent (souce) not child to check the lian.
                if (lt.getTupleSource().isAssociatedWith(rule)) {
                    visitChild(lt, insert, wm, rule);

                    if (lt.getHandlePrevious() != null) {
                        lt.getHandlePrevious().setHandleNext( nextLt );
                    }
                    if (nextLt != null) {
                        nextLt.setHandlePrevious( lt.getHandlePrevious() );
                    }
                }

                lt = nextLt;
            }
        }
    }

    private static void visitChild(InternalWorkingMemory wm, boolean insert, Rule rule, FastIterator it, Tuple lt) {
        for (; lt != null; lt = (LeftTuple) it.next(lt)) {
            LeftTuple childLt = lt.getFirstChild();
            while (childLt != null) {
                LeftTuple nextLt = childLt.getHandleNext();
                visitChild(childLt, insert, wm, rule);
                childLt = nextLt;
            }
        }
    }

    private static void visitChild(LeftTuple lt, boolean insert, InternalWorkingMemory wm, Rule rule) {
        LeftTuple prevLt = null;
        LeftTupleSinkNode sink = lt.getTupleSink();

        for ( ; sink != null; sink = sink.getNextLeftTupleSinkNode() ) {

            if ( lt != null ) {
                if (lt.getTupleSink().isAssociatedWith(rule)) {

                    if (lt.getTupleSink().getAssociationsSize() > 1) {
                        if (lt.getFirstChild() != null) {
                            for ( LeftTuple child = lt.getFirstChild(); child != null; child =  child.getHandleNext() ) {
                                visitChild(child, insert, wm, rule);
                            }
                        } else if (lt.getTupleSink().getType() == NodeTypeEnums.RightInputAdaterNode) {
                            insertPeerRightTuple(lt, wm, rule, insert);
                        }
                    } else if (!insert) {
                        iterateLeftTuple( lt, wm );
                        LeftTuple lt2 = null;
                        for ( LeftTuple peerLt = lt.getPeer();
                              peerLt != null && peerLt.getTupleSink().isAssociatedWith(rule) && peerLt.getTupleSink().getAssociationsSize() == 1;
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
                prevLt = insertPeerLeftTuple(prevLt, sink, wm);
            }
        }
    }

    private static void insertPeerRightTuple( LeftTuple lt, InternalWorkingMemory wm, Rule rule, boolean insert ) {
        // There's a shared RightInputAdaterNode, so check if one of its sinks is associated only to the new rule
        LeftTuple prevLt = null;
        RightInputAdapterNode rian = lt.getTupleSink();

        for (ObjectSink sink : rian.getObjectSinkPropagator().getSinks()) {
            if (lt != null) {
                if (prevLt != null && !insert && sink.isAssociatedWith(rule) && sink.getAssociationsSize() == 1) {
                    prevLt.setPeer( null );
                }
                prevLt = lt;
                lt = lt.getPeer();
            } else if (insert) {
                BetaMemory bm = (BetaMemory) wm.getNodeMemory( (BetaNode) sink );
                prevLt = rian.createPeer( prevLt );
                bm.linkNode( wm );
                bm.getStagedRightTuples().addInsert((RightTuple)prevLt);
            }
        }
    }

    /**
     * Create all missing peers
     */
    private static LeftTuple insertPeerLeftTuple(LeftTuple lt, LeftTupleSinkNode node, InternalWorkingMemory wm) {
        LeftInputAdapterNode.LiaNodeMemory liaMem = null;
        if ( node.getLeftTupleSource().getType() == NodeTypeEnums.LeftInputAdapterNode ) {
            liaMem = wm.getNodeMemory(((LeftInputAdapterNode) node.getLeftTupleSource()));
        }

        LeftTuple peer = node.createPeer(lt);
        Memory memory = wm.getNodeMemories().peekNodeMemory(node.getId());
        if (memory == null || memory.getSegmentMemory() == null) {
            throw new IllegalStateException("Defensive Programming: this should not be possilbe, as the addRule code should init child segments if they are needed ");
        }

        if ( liaMem == null) {
            memory.getSegmentMemory().getStagedLeftTuples().addInsert(peer);
        } else {
            // If parent is Lian, then this must be called, so that any linking or unlinking can be done.
            LeftInputAdapterNode.doInsertSegmentMemory(wm, true, liaMem, memory.getSegmentMemory(), peer, node.getLeftTupleSource().isStreamMode());
        }

        return peer;
    }

    private static void iterateLeftTuple(LeftTuple lt, InternalWorkingMemory wm) {
        if (NodeTypeEnums.isTerminalNode(lt.getTupleSink())) {
            PathMemory pmem = (PathMemory) wm.getNodeMemories().peekNodeMemory( lt.getTupleSink().getId() );
            if (pmem != null) {
                PhreakRuleTerminalNode.doLeftDelete( wm, pmem.getRuleAgendaItem().getRuleExecutor(), lt );
            }
        } else {
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

            boolean   isRootLt = (isFirstLt && removingLt.getLeftParent() == null); // is the LT for the LIAN, if so we need to process the FH too.
            InternalFactHandle fh = removingLt.getFactHandle();

            // This is the first LT in a peer chain. Only this LT is hooked into the left and right parent LT and RT and
            // if it's the root (form the lian) it will be hooked itno the FH.
            LeftTuple leftPrevious = removingLt.getHandlePrevious();
            LeftTuple leftNext     = removingLt.getHandleNext();

            LeftTuple rightPrevious = removingLt.getRightParentPrevious();
            LeftTuple rightNext     = removingLt.getRightParentNext();

            LeftTuple          leftParent  = removingLt.getLeftParent();
            RightTuple         rightParent = removingLt.getRightParent();

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
            } else if ( isRootLt ) {
                if (fh.getFirstLeftTuple() == removingLt) {
                    fh.setFirstLeftTuple(nextPeerLt);
                }

                if (fh.getLastLeftTuple() == removingLt) {
                    fh.setLastLeftTuple(nextPeerLt);
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

    private static LeftTupleNode getNetworkSplitPoint(LeftTupleNode node) {
        while (node.getType() != NodeTypeEnums.LeftInputAdapterNode && node.getAssociationsSize() == 1) {
            node = node.getLeftTupleSource();
        }

        return node;
    }

    public static SegmentMemory splitSegment(InternalWorkingMemory wm, SegmentMemory sm1, LeftTupleNode splitNode) {
        // create new segment, starting after split
        LeftTupleNode childNode = splitNode.getSinkPropagator().getFirstLeftTupleSink();
        SegmentMemory sm2 = new SegmentMemory(childNode); // we know there is only one sink
        wm.getNodeMemories().peekNodeMemory( childNode.getId() ).setSegmentMemory( sm2 );

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

        sm2.setPos(sm1.getPos());  // clone for now, it's corrected later
        sm2.setSegmentPosMaskBit(sm1.getSegmentPosMaskBit()); // clone for now, it's corrected later
        sm2.setLinkedNodeMask(sm1.getLinkedNodeMask());  // clone for now, it's corrected later

        sm2.mergePathMemories(sm1);

        // re-assigned tip nodes
        sm2.setTipNode(sm1.getTipNode());
        sm1.setTipNode(splitNode); // splitNode is now tip of original segment

        if (sm1.getTipNode().getType() == NodeTypeEnums.LeftInputAdapterNode) {
            if (!sm1.getStagedLeftTuples().isEmpty()) {
                // Segments with only LiaNode's cannot have staged LeftTuples, so move them down to the new Segment
                sm2.getStagedLeftTuples().addAll(sm1.getStagedLeftTuples());
            }
        }

        // find the pos of the node in the segment
        int pos = nodeSegmentPosition(sm1, splitNode);

        splitNodeMemories(sm1, sm2, pos);

        splitBitMasks(sm1, sm2, pos);

        correctSegmentMemoryAfterSplitOnAdd(sm2);

        return sm2;
    }

    private static void mergeSegment(SegmentMemory sm1, SegmentMemory sm2) {
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
        // re-assigned tip nodes
        sm1.setTipNode(sm2.getTipNode());

        mergeNodeMemories(sm1, sm2);

        mergeBitMasks(sm1, sm2);
    }

    private static void splitBitMasks(SegmentMemory sm1, SegmentMemory sm2, int pos) {
        int  splitPos                 = pos + 1; // +1 as zero based
        long currentAllLinkedMaskTest = sm1.getAllLinkedMaskTest();
        long currentLinkedNodeMask    = sm1.getLinkedNodeMask();
        long mask                     = (1L << splitPos) - 1;

        sm1.setAllLinkedMaskTest(mask & currentAllLinkedMaskTest);
        sm1.setLinkedNodeMask(sm1.getLinkedNodeMask() & sm1.getAllLinkedMaskTest());

        mask = currentAllLinkedMaskTest >> splitPos;
        sm2.setAllLinkedMaskTest(mask);
        sm2.setLinkedNodeMask(mask & (currentLinkedNodeMask >> splitPos));
    }

    private static void mergeBitMasks(SegmentMemory sm1, SegmentMemory sm2) {
        LinkedList<Memory> smNodeMemories2 = sm2.getNodeMemories();

        long mask = sm2.getAllLinkedMaskTest() << smNodeMemories2.size();
        sm1.setAllLinkedMaskTest(mask & sm1.getAllLinkedMaskTest());

        mask = sm2.getAllLinkedMaskTest() << smNodeMemories2.size();
        sm1.setLinkedNodeMask(mask & sm1.getLinkedNodeMask());
    }

    private static void splitNodeMemories(SegmentMemory sm1, SegmentMemory sm2, int pos) {
        LinkedList<Memory> smNodeMemories1 = sm1.getNodeMemories();
        LinkedList<Memory> smNodeMemories2 = sm2.getNodeMemories();

        Memory mem         = smNodeMemories1.getFirst();
        int    nodePosMask = 1;
        for (int i = 0, length = smNodeMemories1.size(); i < length; i++) {
            Memory next = mem.getNext();
            if (i > pos) {
                smNodeMemories1.remove(mem);
                smNodeMemories2.add(mem);
                mem.setSegmentMemory(sm2);

                // correct the NodePosMaskBit
                if (mem instanceof SegmentNodeMemory) {
                    ( (SegmentNodeMemory) mem ).setNodePosMaskBit( nodePosMask );
                }
                nodePosMask = nodePosMask << 1;
            }
            mem = next;
        }
    }

    private static void mergeNodeMemories(SegmentMemory sm1, SegmentMemory sm2) {
        LinkedList<Memory> smNodeMemories1 = sm1.getNodeMemories();
        LinkedList<Memory> smNodeMemories2 = sm2.getNodeMemories();


        int nodePosMask = 1;
        for (int i = 0, length = smNodeMemories1.size(); i < length; i++) {
            nodePosMask = nodePosMask >> 1;
        }

        for (Memory mem = smNodeMemories2.getFirst(); mem != null; ) {
            Memory next = mem.getNext();
            smNodeMemories2.remove(mem);
            smNodeMemories1.add(mem);
            mem.setSegmentMemory(sm1);

            // correct the NodePosMaskBit
            if (mem instanceof SegmentNodeMemory) {
                ( (SegmentNodeMemory) mem ).setNodePosMaskBit( nodePosMask );
            }
            nodePosMask = nodePosMask >> 1;
            mem = next;
        }
    }

    private static int nodeSegmentPosition(SegmentMemory sm1, LeftTupleNode splitNode) {
        LeftTupleNode lt = splitNode;
        int nodePos = 0;
        while (lt != sm1.getRootNode()) {
            lt = lt.getLeftTupleSource();
            nodePos++;
        }
        return nodePos;
    }

    private static PathEndNodeMemories getPathEndMemories(InternalWorkingMemory wm,
                                                          PathEndNodes pathEndNodes) {
        PathEndNodeMemories tnMems = new PathEndNodeMemories();

        for (LeftTupleNode node : pathEndNodes.otherEndNodes) {
            if (node.getType() == NodeTypeEnums.RightInputAdaterNode) {
                RiaNodeMemory riaMem = (RiaNodeMemory) wm.getNodeMemories().peekNodeMemory(node.getId());
                if (riaMem != null) {
                    tnMems.otherPmems.add(riaMem.getRiaPathMemory());
                }
            } else {
                PathMemory pmem = (PathMemory) wm.getNodeMemories().peekNodeMemory(node.getId());
                if (pmem != null) {
                    tnMems.otherPmems.add(pmem);
                }
            }
        }

        tnMems.subjectPmem = (PathMemory) wm.getNodeMemories().peekNodeMemory(pathEndNodes.subjectEndNode.getId());
        if (tnMems.subjectPmem == null && !tnMems.otherPmems.isEmpty()) {
            // If "other pmem's are initialized, then the subject needs to be initialized too.
            tnMems.subjectPmem = (PathMemory) wm.getNodeMemory((MemoryFactory<Memory>) pathEndNodes.subjectEndNode);
        }

        for (LeftTupleNode node : pathEndNodes.subjectEndNodes) {
            if (node.getType() == NodeTypeEnums.RightInputAdaterNode) {
                RiaNodeMemory riaMem = (RiaNodeMemory) wm.getNodeMemories().peekNodeMemory(node.getId());
                if (riaMem == null && !tnMems.otherPmems.isEmpty()) {
                    riaMem = (RiaNodeMemory) wm.getNodeMemory((MemoryFactory<Memory>) node);
                }
                if (riaMem != null) {
                    tnMems.subjectPmems.add(riaMem.getRiaPathMemory());
                }
            } else {
                PathMemory pmem = (PathMemory) wm.getNodeMemories().peekNodeMemory(node.getId());
                if (pmem != null) {
                    tnMems.subjectPmems.add(pmem);
                }
            }
        }

        return tnMems;
    }

    private static class PathEndNodeMemories {
        PathMemory subjectPmem;
        List<PathMemory> subjectPmems = new ArrayList<PathMemory>();
        List<PathMemory> otherPmems = new ArrayList<PathMemory>();
    }

    private static PathEndNodes getPathEndNodes(InternalKnowledgeBase kBase,
                                                LeftTupleNode lt,
                                                TerminalNode tn,
                                                Rule processedRule,
                                                boolean hasProtos,
                                                boolean hasWms) {
        PathEndNodes endNodes = new PathEndNodes();
        endNodes.subjectEndNode = (PathEndNode) tn;
        endNodes.subjectEndNodes.add((PathEndNode) tn);
        if (hasWms && SegmentUtilities.isTipNode(lt, null)) {
            endNodes.subjectSplit = lt;
            endNodes.subjectSplits.add(lt);
        }

        if (hasProtos) {
            invalidateRootNode( kBase, lt );
        }

        collectPathEndNodes(kBase, lt, endNodes, tn, processedRule, hasProtos, hasWms, hasProtos && isSplit(lt));

        return endNodes;
    }

    private static void collectPathEndNodes(InternalKnowledgeBase kBase,
                                            LeftTupleNode lt,
                                            PathEndNodes endNodes,
                                            TerminalNode tn,
                                            Rule processedRule,
                                            boolean hasProtos,
                                            boolean hasWms,
                                            boolean isBelowNewSplit) {
        // Traverses the sinks in reverse order in order to collect PathEndNodes so that
        // the outermost (sub)network are evaluated before the innermost one
        for (LeftTupleSinkNode sink = lt.getSinkPropagator().getLastLeftTupleSink(); sink != null; sink = sink.getPreviousLeftTupleSinkNode()) {
            if (sink == tn) {
                continue;
            }
            if (hasProtos) {
                if (isBelowNewSplit) {
                    if ( isRootNode( sink, null )) {
                        kBase.invalidateSegmentPrototype( sink );
                    }
                } else {
                    isBelowNewSplit = isSplit(sink);
                    if (isBelowNewSplit) {
                        invalidateRootNode( kBase, sink );
                    }
                }
            }
            if (NodeTypeEnums.isLeftTupleSource(sink)) {
                if (hasWms && SegmentUtilities.isTipNode(sink, null)) {
                    if (!SegmentUtilities.isTipNode(sink, tn)) {
                        endNodes.subjectSplits.add(sink);
                    }
                }

                collectPathEndNodes(kBase, sink, endNodes, tn, processedRule, hasProtos, hasWms, isBelowNewSplit);
            } else if (NodeTypeEnums.isTerminalNode(sink)) {
                endNodes.otherEndNodes.add((PathEndNode) sink);
            } else if (NodeTypeEnums.RightInputAdaterNode == sink.getType()) {
                if (sink.isAssociatedWith( processedRule )) {
                    endNodes.subjectEndNodes.add( (PathEndNode) sink );
                }
                if (sink.getAssociationsSize() > 1 || !sink.isAssociatedWith(processedRule)) {
                    endNodes.otherEndNodes.add( (PathEndNode) sink );
                }
            } else {
                throw new RuntimeException("Error: Unknown Node. Defensive programming test..");
            }
        }
    }

    private static void invalidateRootNode( InternalKnowledgeBase kBase, LeftTupleNode lt ) {
        while (!isRootNode( lt, null )) {
            lt = lt.getLeftTupleSource();
        }
        kBase.invalidateSegmentPrototype( lt );
    }

    private static class PathEndNodes {
        PathEndNode   subjectEndNode;
        LeftTupleNode subjectSplit;
        List<PathEndNode>   subjectEndNodes = new ArrayList<PathEndNode>();
        List<LeftTupleNode> subjectSplits   = new ArrayList<LeftTupleNode>();
        List<PathEndNode>   otherEndNodes   = new ArrayList<PathEndNode>();
    }
}
