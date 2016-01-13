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
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.RightTupleSinkAdapter;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddRemoveRule {

    private static final Logger log = LoggerFactory.getLogger(AddRemoveRule.class);

    public static void addRule(TerminalNode tn, InternalWorkingMemory[] wms, InternalKnowledgeBase kBase) {
        if ( log.isTraceEnabled() ) {
            log.trace("Adding Rule {}", tn.getRule().getName() );
        }
        LeftTupleSource splitStartLeftTupleSource = getNetworkSplitPoint(tn);

        kBase.invalidateSegmentPrototype( splitStartLeftTupleSource, false );

        for (InternalWorkingMemory wm : wms) {

            if (splitStartLeftTupleSource.getAssociatedRuleSize() > 1) {
                List<PathMemory> pathMems = new ArrayList<PathMemory>();

                collectRtnPathMemories(splitStartLeftTupleSource, wm, pathMems, tn); // get all PathMemories, except current

                PathMemory newPmem = wm.getNodeMemory( tn );

                int s = getSegmentPos(splitStartLeftTupleSource, null);

                LeftTupleSink[] sinks = splitStartLeftTupleSource.getSinkPropagator().getSinks();
                if (sinks.length == 2 || (sinks.length == 3 && NodeTypeEnums.isBetaNode(sinks[2])) && ((BetaNode) sinks[2]).isRightInputIsRiaNode()) {
                    List<SegmentMemory[]> previousSmems = reInitPathMemories(wm, pathMems, null);

                    // can only be two if the adding node caused the split to be created
                    int p = 0;
                    SegmentMemory splitSmem = null;
                    for (PathMemory pmem : pathMems) {
                        pmem.setlinkedSegmentMask(0);
                        SegmentMemory[] smems = previousSmems.get(p);

                        for (int i = 0; i < smems.length; i++) {
                            SegmentMemory sm = smems[i];
                            if (sm == null) {
                                continue; // SegmentMemory is not yet initialized
                            }

                            if (i < s) {
                                correctSegmentBeforeSplitOnAdd(wm, newPmem, p, pmem, sm);
                            } else if (i == s) {
                                splitSmem = correctSegmentOnSplitOnAdd(splitStartLeftTupleSource, wm, newPmem, p, splitSmem, pmem, sm);
                            } else if (i > s) {
                                correctSegmentAfterSplitOnAdd(wm, pmem, i, sm);
                            }
                        }
                        p++;
                    }
                } else {
                    SegmentMemory sm = pathMems.get(0).getSegmentMemories()[s];
                    if (sm == null) {
                        continue; // Segments are initialised lazily, so the SM may not yet exist yet, and thus no processing needed
                    }
                    initNewSegment(splitStartLeftTupleSource, wm, sm);
                    correctSegmentBeforeSplitOnAdd(wm, newPmem, pathMems.get(0), sm);
                }
            }

            if (NodeTypeEnums.LeftInputAdapterNode == splitStartLeftTupleSource.getType() && splitStartLeftTupleSource.getAssociatedRuleSize() == 1) {
                // rule added with no sharing
                insertLiaFacts( splitStartLeftTupleSource, wm );
            }

            correctMemoryOnSplitsChanged( splitStartLeftTupleSource, wm );
            insertFacts( splitStartLeftTupleSource.getSinkPropagator().getLastLeftTupleSink(), wm);
        }
    }

     public static void removeRule(TerminalNode tn, InternalWorkingMemory[] wms, InternalKnowledgeBase kBase) {
         if ( log.isTraceEnabled() ) {
             log.trace( "Removing Rule {}", tn.getRule().getName() );
         }

         LeftTupleSource splitStartNode = getNetworkSplitPoint(tn);

         kBase.invalidateSegmentPrototype( splitStartNode, true );

         for ( InternalWorkingMemory wm : wms ) {
             PathMemory removedPmem = wm.getNodeMemory( tn );
             int s = getSegmentPos(splitStartNode, null);

             // if a segment is going to be merged it is necessary to flush all its staged left tuples before doing any change to the network
             flushSegmentIfMerge(wm, tn, splitStartNode, s);

             // must be done before segments are mutated
             flushStagedTuples(splitStartNode, removedPmem, wm, true);
             wm.flushPropagations();

             LeftTupleSink sink;
             if ( splitStartNode.getAssociatedRuleSize() == 1 ) {
                 // there is no sharing, so get the node after the root of the only SegmentMemory
                 SegmentMemory sm =  removedPmem.getSegmentMemories()[s];
                 if ( sm == null ) {
                     continue; // this rule has not been initialized yet
                 }
                 sink = ((LeftInputAdapterNode)sm.getRootNode()).getSinkPropagator().getFirstLeftTupleSink();
             } else {
                 // Sharing exists, get the root of the SegmentMemory after the split
                 SegmentMemory sm = removedPmem.getSegmentMemories()[s+1];
                 if ( sm == null ) {
                     List<PathMemory> pathMems = new ArrayList<PathMemory>();
                     collectRtnPathMemories(splitStartNode, wm, pathMems, tn); // get all PathMemories, except current
                     reInitPathMemories(wm, pathMems, tn.getRule() );
                     continue; // this rule has not been initialized yet
                 }
                 sink = (LeftTupleSink) sm.getRootNode();
             }
             deleteFacts( sink, wm);

             if ( splitStartNode.getAssociatedRuleSize() > 1 ) {
                 List<PathMemory> pathMems = new ArrayList<PathMemory>();
                 collectRtnPathMemories(splitStartNode, wm, pathMems, tn); // get all PathMemories, except current

                 List<SegmentMemory[]> previousSmems = reInitPathMemories(wm, pathMems, tn.getRule() );

                 if ( splitStartNode.getSinkPropagator().size() == 2 ) {
                     // can only be two if the removing node causes the split to be removed
                     int p = 0;
                     boolean firstRulePath = true;
                     for ( PathMemory pmem : pathMems) {
                         pmem.setlinkedSegmentMask(0);
                         SegmentMemory[] smems = previousSmems.get(p);

                         for (int i = 0; i < smems.length; i++ ) {
                             SegmentMemory sm = smems[i];
                             if ( sm == null ) {
                                 if (i == 0) {
                                     // if SegmentMemory has not been yet initialized enforce its creation here to allow merging
                                     sm = SegmentUtilities.createSegmentMemory( splitStartNode, wm );
                                 } else {
                                     continue;
                                 }
                             }

                             if ( i < s ) {
                                 correctSegmentBeforeSplitOnRemove(wm, removedPmem, pmem, sm, firstRulePath);
                             } else if ( i == s ) {
                                 if (smems[i+1] != null) {
                                     correctSegmentOnSplitOnRemove(wm,  sm, smems[i+1], pmem, removedPmem, firstRulePath);
                                     i++; // increase to skip merged segment
                                 }
                             } else if (i > s) {
                                 correctSegmentAfterSplitOnRemove(wm, pmem, i, sm);
                             }
                         }
                         firstRulePath &= pmem.getNodeType() == NodeTypeEnums.RightInputAdaterNode;
                         p++;
                     }
                 } else {
                     int p = 0;
                     for ( PathMemory pmem : pathMems) {
                         SegmentMemory[] smems = previousSmems.get(p++);
                         for (int i = 0; i < pmem.getSegmentMemories().length; i++) {
                             if ( smems[i] == null) {
                                 continue;
                             }
                             smems[i].removePathMemory( removedPmem );
                             pmem.setSegmentMemory(i, smems[i]);
                         }
                     }
                 }
             }
             if ( removedPmem.getRuleAgendaItem() != null && removedPmem.getRuleAgendaItem().isQueued() ) {
                 removedPmem.getRuleAgendaItem().dequeue();
             }

             correctMemoryOnSplitsChanged( splitStartNode, wm );
         }
     }

    private static void flushSegmentIfMerge(InternalWorkingMemory wm, TerminalNode tn, LeftTupleSource splitStartNode, int segmentPos) {
        if ( splitStartNode.getAssociatedRuleSize() == 2 ) {
            // only handle for the first PathMemory, all others are shared and duplicate until this point
            PathMemory pmem = getFirstRtnPathMemory(splitStartNode, wm, tn);
            SegmentMemory[] smems = pmem.getSegmentMemories();

            SegmentMemory sm1 = smems[segmentPos];
            SegmentMemory sm2 = smems[segmentPos+1];
            if ( sm1 != null && sm2 != null ) {
                if (sm1.getRootNode() == sm1.getTipNode() && NodeTypeEnums.LeftInputAdapterNode == sm1.getTipNode().getType()) {
                    sm1.setStagedTuples(sm2.getStagedLeftTuples());
                } else if ( !sm2.getStagedLeftTuples().isEmpty() ) {
                    flushStagedTuples(splitStartNode, pmem, wm, false);
                }
            }
        }
    }

    private static void flushStagedTuples(LeftTupleSource splitStartNode, PathMemory pmem, InternalWorkingMemory wm, boolean removeTuples) {
         int smemIndex = getSegmentPos( splitStartNode, null ); // index before the segments are merged
         SegmentMemory[] smems = pmem.getSegmentMemories();
         SegmentMemory sm;
         LeftTupleSink sink;
         Memory mem;
         long bit = 1;
         if ( splitStartNode.getAssociatedRuleSize() == 1 && (smems[0] == null || smems[0].getTipNode().getType() != NodeTypeEnums.LeftInputAdapterNode) ) {
             // there is no sharing
             sm = smems[0];
             if ( sm == null ) {
                 return; // segment has not yet been initialized
             }
             sink = ((LeftInputAdapterNode)sm.getRootNode()).getSinkPropagator().getFirstLeftTupleSink();
             mem = sm.getNodeMemories().get(1);
             bit = 2; // adjust bit to point to next node
         } else {
             sm = smems[++smemIndex]; // segment after the split being removed.
             if ( sm == null ) {
                 return; // segment has not yet been initialized
             }
             sink = (LeftTupleSink) sm.getRootNode();
             mem = sm.getNodeMemories().get(0);
         }

         // stages the LeftTuples for deletion in the target SegmentMemory, if necessary it looks up the nodes to find.
         if (removeTuples) {
            processLeftTuples( splitStartNode, sink, sm, wm, false );
         }

         // The graph must be fully updated before SegmentMemory and PathMemories are mutated
         if ( !sm.getStagedLeftTuples().isEmpty() && pmem.isRuleLinked() ) {
             new RuleNetworkEvaluator().outerEval( ( LeftInputAdapterNode ) smems[0].getRootNode(),
                                                   pmem, sink, bit, mem, smems, smemIndex,
                                                   sm.getStagedLeftTuples().takeAll(), wm,
                                                   new LinkedList<StackEntry>(),
                                                   true, pmem.getRuleAgendaItem().getRuleExecutor() );
         }
     }

    public static boolean flushLeftTupleIfNecessary( InternalWorkingMemory wm, SegmentMemory sm, LeftTuple leftTuple, boolean streamMode ) {
        PathMemory pmem = streamMode ?
                          sm.getPathMemories().get(0) :
                          sm.getFirstDataDrivenPathMemory();
        return pmem != null && forceFlushLeftTuple(pmem, sm, wm, leftTuple);
    }

    private static boolean forceFlushLeftTuple(PathMemory pmem, SegmentMemory sm, InternalWorkingMemory wm, LeftTuple leftTuple) {
        SegmentMemory[] smems = pmem.getSegmentMemories();
        if (smems[0] == null) {
            return false; // segment has not yet been initialized
        }

        LeftTupleSink sink;
        Memory mem;
        long bit = 1;
        if ( sm.getRootNode() instanceof LeftInputAdapterNode ) {
            sink = ((LeftInputAdapterNode)sm.getRootNode()).getSinkPropagator().getFirstLeftTupleSink();
            mem = sm.getNodeMemories().get(1);
            bit = 2; // adjust bit to point to next node
        } else {
            sink = (LeftTupleSink) sm.getRootNode();
            mem = sm.getNodeMemories().get(0);
        }

        TupleSets<LeftTuple> leftTupleSets = new TupleSetsImpl<LeftTuple>();
        if (leftTuple != null) {
            leftTupleSets.addInsert( leftTuple);
        }

        new RuleNetworkEvaluator().outerEval( ( LeftInputAdapterNode ) smems[0].getRootNode(),
                                              pmem, sink, bit, mem, smems, sm.getPos(), leftTupleSets, wm,
                                              new LinkedList<StackEntry>(),
                                              true, pmem.getOrCreateRuleAgendaItem(wm).getRuleExecutor() );
        return true;
     }


     private static List<SegmentMemory[]> reInitPathMemories(InternalWorkingMemory wm, List<PathMemory> pathMems, RuleImpl removingRule) {
         List<SegmentMemory[]> previousSmems = new ArrayList<SegmentMemory[]>();
         for ( PathMemory pmem : pathMems) {
             // Re initialise all the PathMemories
             previousSmems.add(pmem.getSegmentMemories());
             LeftTupleSource lts;
             LeftTupleSource startRianLts = null;
             if ( NodeTypeEnums.isTerminalNode(pmem.getNetworkNode())) {
                 lts = ((TerminalNode)pmem.getNetworkNode()).getLeftTupleSource();
             } else {
                 RightInputAdapterNode rian = (RightInputAdapterNode)pmem.getNetworkNode();
                 startRianLts = rian.getStartTupleSource();
                 lts = rian.getLeftTupleSource();
             }
             AbstractTerminalNode.initPathMemory(pmem, lts, startRianLts, wm, removingRule); // re-initialise the PathMemory
         }
         return previousSmems;
     }


     private static void correctSegmentBeforeSplitOnAdd(InternalWorkingMemory wm, PathMemory newPmem, PathMemory pmem, SegmentMemory sm) {
         while (sm != null) {
             setSegmentMemoryOnNewPath( wm, newPmem, sm );
             sm = sm.getPos() > 0 ? pmem.getSegmentMemories()[sm.getPos()-1] : null;
         }
     }

     private static void correctSegmentBeforeSplitOnAdd(InternalWorkingMemory wm, PathMemory newPmem, int p, PathMemory pmem, SegmentMemory sm) {
         pmem.setSegmentMemory( sm.getPos(), sm );
         if ( p == 0 ) {
             // only handle for the first PathMemory, all others are shared and duplicate until this point
             setSegmentMemoryOnNewPath( wm, newPmem, sm );
         }
     }

    private static void setSegmentMemoryOnNewPath( InternalWorkingMemory wm, PathMemory newPmem, SegmentMemory sm ) {
        newPmem.setSegmentMemory(sm.getPos(), sm);
        sm.addPathMemory( newPmem );
        sm.notifyRuleLinkSegment(wm);
    }

    private static void correctSegmentBeforeSplitOnRemove(InternalWorkingMemory wm, PathMemory removedPmem, PathMemory pmem, SegmentMemory sm, boolean firstRulePath) {
         pmem.setSegmentMemory( sm.getPos(), sm );
         if ( firstRulePath ) {
             // only handle for the first PathMemory, all others are shared and duplicate until this point
             sm.removePathMemory( removedPmem );
             sm.notifyRuleLinkSegment(wm);
         }
     }

     private static SegmentMemory correctSegmentOnSplitOnAdd(LeftTupleSource splitStartLeftTupleSource, InternalWorkingMemory wm, PathMemory newPmem, int p, SegmentMemory splitSmem, PathMemory pmem, SegmentMemory sm) {
         if ( p == 0 ) {
             // split is inside of a segment, so the segment must be split in half and a new one created
             splitSmem = splitSegment(sm, splitStartLeftTupleSource);

             correctSegmentMemoryAfterSplitOnAdd(splitSmem);

             pmem.setSegmentMemory(sm.getPos(), sm);
             pmem.setSegmentMemory(splitSmem.getPos(), splitSmem);

             newPmem.setSegmentMemory(sm.getPos(), sm);
             newPmem.setSegmentMemory(splitSmem.getPos(), splitSmem);

             sm.addPathMemory( newPmem );
             splitSmem.addPathMemory( newPmem );

             sm.notifyRuleLinkSegment(wm);
             splitSmem.notifyRuleLinkSegment(wm);

             initNewSegment(splitStartLeftTupleSource, wm, sm);
         } else {
             pmem.setSegmentMemory(sm.getPos(), sm);
             pmem.setSegmentMemory(splitSmem.getPos(), splitSmem);
         }
         return splitSmem;
     }

     private static void initNewSegment(LeftTupleSource splitStartLeftTupleSource, InternalWorkingMemory wm, SegmentMemory sm) {// Initialise new SegmentMemory
         LeftTupleSinkNode peerLts = splitStartLeftTupleSource.getSinkPropagator().getLastLeftTupleSink();

         if ( NodeTypeEnums.isBetaNode(peerLts) && ((BetaNode)peerLts).isRightInputIsRiaNode() ) {
             LeftTupleSink subNetworkLts = peerLts.getPreviousLeftTupleSinkNode();

             Memory memory = wm.getNodeMemory((MemoryFactory) subNetworkLts);
             SegmentMemory newSmem = SegmentUtilities.createChildSegment(wm, peerLts, memory);
             sm.add(newSmem);
         }

         Memory memory = wm.getNodeMemory((MemoryFactory) peerLts);
         SegmentMemory newSmem = SegmentUtilities.createChildSegment(wm, peerLts, memory);
         newSmem.setPos( sm.getPos()+1 );
         sm.add(newSmem);

         LeftTupleSource lts;
         if ( NodeTypeEnums.isTerminalNode(sm.getTipNode() ) ) {
             // if tip is RTN, then use parent
             lts = ((TerminalNode)sm.getTipNode()).getLeftTupleSource();
         } else {
             lts = (LeftTupleSource) sm.getTipNode();
         }

         processLeftTuples(lts, peerLts, newSmem, wm, true);
     }

    private static void correctMemoryOnSplitsChanged( LeftTupleSource splitStart, InternalWorkingMemory wm ) {
        if ( splitStart.getType() == NodeTypeEnums.UnificationNode) {
            ((QueryElementNode.QueryElementNodeMemory) wm.getNodeMemory( (MemoryFactory) splitStart )).correctMemoryOnSinksChanged();
        }
    }

    private static void correctSegmentOnSplitOnRemove(InternalWorkingMemory wm, SegmentMemory sm1,SegmentMemory sm2,  PathMemory pmem, PathMemory removedPmem, boolean firstRulePath) {
         if ( firstRulePath ) {
             mergeSegment(sm1, sm2);

             pmem.setSegmentMemory( sm1.getPos(), sm1 );

             sm1.removePathMemory( removedPmem);
             SegmentMemory oldSegment = removedPmem.getSegmentMemories()[sm1.getPos() + 1];
             // sm1 may not be linked yet to oldSegment because oldSegment has been just created
             if (sm1.contains( oldSegment )) {
                 sm1.remove( oldSegment );
             }

             sm1.notifyRuleLinkSegment(wm);
         } else {
             pmem.setSegmentMemory( sm1.getPos(), sm1 );
         }
     }

     private static void correctSegmentAfterSplitOnAdd(InternalWorkingMemory wm, PathMemory pmem, int i, SegmentMemory sm) {
         if ( sm.getPos() == i ) {
             // segment has not yet had it's pos and bitmasks updated
             correctSegmentMemoryAfterSplitOnAdd(sm);
             sm.notifyRuleLinkSegment(wm);
         }
         pmem.setSegmentMemory(sm.getPos(), sm);
     }

     private static void correctSegmentAfterSplitOnRemove(InternalWorkingMemory wm, PathMemory pmem, int i, SegmentMemory sm) {
         if ( sm.getPos() == i ) {
             // segment has not yet had it's pos and bitmasks updated
             correctSegmentMemoryAfterSplitOnRemove(sm);
             sm.notifyRuleLinkSegment(wm);
         }
         pmem.setSegmentMemory(sm.getPos(), sm );
     }

     public static void correctSegmentMemoryAfterSplitOnAdd(SegmentMemory sm) {
         sm.setPos(sm.getPos() + 1);
         sm.setSegmentPosMaskBit(sm.getSegmentPosMaskBit() << 1);
     }

     public static void correctSegmentMemoryAfterSplitOnRemove(SegmentMemory sm) {
         sm.setPos(sm.getPos() - 1);
         sm.setSegmentPosMaskBit(sm.getSegmentPosMaskBit() >> 1);
     }

     public static int getSegmentPos(LeftTupleSource lts, RuleImpl removingRule) {
         int counter = 0;
         while ( lts.getType() != NodeTypeEnums.LeftInputAdapterNode ) {
             if ( !SegmentUtilities.parentInSameSegment( lts, removingRule ) ) {
                 counter++;
             }
             lts = lts.getLeftTupleSource();
         }
         return counter;
     }

    private static void insertLiaFacts(LeftTupleSource startNode, InternalWorkingMemory wm) {
        // rule added with no sharing
        PropagationContextFactory pctxFactory = wm.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
        final PropagationContext pctx = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION, null, null, null);
        LeftInputAdapterNode lian = (LeftInputAdapterNode) startNode;
        RightTupleSinkAdapter liaAdapter = new RightTupleSinkAdapter(lian);
        lian.getObjectSource().updateSink(liaAdapter, pctx, wm);
    }

    private static void insertFacts(LeftTupleSink startNode, InternalWorkingMemory wm) {
        LeftTupleSink lts =  startNode;
        PropagationContextFactory pctxFactory = wm.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
        while (!NodeTypeEnums.isTerminalNode(lts) && lts.getLeftTupleSource().getType() != NodeTypeEnums.RightInputAdaterNode ) {
            if (NodeTypeEnums.isBetaNode(lts)) {
                BetaNode bn = (BetaNode) lts;
                if (!bn.isRightInputIsRiaNode() ) {
                    final PropagationContext pctx = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION, null, null, null);
                    bn.getRightInput().updateSink(bn,
                                                  pctx,
                                                  wm);
                } else {
                    insertSubnetworkFacts(bn, wm);
                }
            } else if ( lts.getType() == NodeTypeEnums.RightInputAdaterNode ) {
                // no need to delete anything, as this gets popagated during the rule evaluation
                return;
            }
            lts = ((LeftTupleSource) lts).getSinkPropagator().getFirstLeftTupleSink();
        }
    }

    private static void insertSubnetworkFacts(BetaNode bn, InternalWorkingMemory wm) {
        RightInputAdapterNode rian = ( RightInputAdapterNode ) bn.getRightInput();
        LeftTupleSource subLts =  rian.getLeftTupleSource();
        while ( subLts.getLeftTupleSource() != rian.getStartTupleSource() ) {
            subLts = subLts.getLeftTupleSource();
        }
        insertFacts( ( LeftTupleSink ) subLts, wm);
    }

    public static void deleteFacts(LeftTupleSink startNode, InternalWorkingMemory wm) {
        LeftTupleSink lts = startNode;
        while (!NodeTypeEnums.isTerminalNode(lts) && lts.getLeftTupleSource().getType() != NodeTypeEnums.RightInputAdaterNode ) {
            if (NodeTypeEnums.isBetaNode(lts)) {
                BetaNode bn = (BetaNode) lts;
                if (!bn.isRightInputIsRiaNode() ) {
                    BetaMemory bm;
                    if ( bn.getType() == NodeTypeEnums.AccumulateNode ) {
                        bm = ((AccumulateMemory)wm.getNodeMemory( bn )).getBetaMemory();
                    } else {
                        bm = ( BetaMemory ) wm.getNodeMemory( bn );
                    }

                    TupleMemory rtm = bm.getRightTupleMemory();
                    FastIterator it = rtm.fullFastIterator();
                    for (Tuple rightTuple = BetaNode.getFirstTuple( rtm, it ); rightTuple != null; ) {
                        Tuple next = (Tuple) it.next(rightTuple);
                        rtm.remove(rightTuple);
                        rightTuple.unlinkFromRightParent();
                        rightTuple = next;
                    }

                    if ( !bm.getStagedRightTuples().isEmpty() ) {
                        bm.setNodeDirtyWithoutNotify();
                    }
                    TupleSets<RightTuple> srcRightTuples = bm.getStagedRightTuples().takeAll();

                    unlinkRightTuples(srcRightTuples.getInsertFirst());
                    unlinkRightTuples(srcRightTuples.getUpdateFirst());
                    unlinkRightTuples(srcRightTuples.getDeleteFirst());

                    deleteFactsFromRightInput(bn, wm);
                } else {
                    deleteSubnetworkFacts(bn, wm);
                }
            } else if ( lts.getType() == NodeTypeEnums.RightInputAdaterNode ) {
                // no need to delete anything, as these would have been deleted by the left tuple processing.
                return;
            }
            lts = ((LeftTupleSource) lts).getSinkPropagator().getFirstLeftTupleSink();
        }
    }

    private static void deleteFactsFromRightInput(BetaNode bn, InternalWorkingMemory wm) {
        ObjectSource source = bn.getRightInput();
        if (source instanceof WindowNode) {
            WindowNode.WindowMemory memory = (WindowNode.WindowMemory) wm.getNodeMemory( ( (WindowNode) source ));
            for (EventFactHandle factHandle : memory.getFactHandles()) {
                for (RightTuple rightTuple = factHandle.getFirstRightTuple(); rightTuple != null; ) {
                    RightTuple nextRightTuple = rightTuple.getHandleNext();
                    if (source.equals( rightTuple.getTupleSink() )) {
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

    private static void deleteSubnetworkFacts(BetaNode bn, InternalWorkingMemory wm) {
        RightInputAdapterNode rian = ( RightInputAdapterNode ) bn.getRightInput();
        LeftTupleSource subLts =  rian.getLeftTupleSource();
        while ( subLts.getLeftTupleSource() != rian.getStartTupleSource() ) {
            subLts = subLts.getLeftTupleSource();
        }
        deleteFacts((LeftTupleSink) subLts, wm);
    }

    /**
     * Populates the SegmentMemory with staged LeftTuples. If the parent is not a Beta or From node, it iterates up to find the first node with memory. If necessary
     * It traverses to the LiaNode's ObjectTypeNode. It then iterates the LeftTuple chain, on a specific path to navigate down to where an existing LeftTuple is staged
     * as delete. Or a new LeftTuple is created and staged as an insert.
     */
    public static void processLeftTuples(LeftTupleSource node, LeftTupleSink peerNode, SegmentMemory smem, InternalWorkingMemory wm, boolean insert) {
        // *** if you make a fix here, it most likely needs to be in PhreakActivationIteratorToo ***

        // Must iterate up until a node with memory is found, this can be followed to find the LeftTuples to peer

        // creates the propagation path to follow
        List<LeftTupleSink> sinks = new ArrayList<LeftTupleSink>();
        sinks.add(peerNode);

        while (NodeTypeEnums.LeftInputAdapterNode != node.getType()) {
            Memory memory = wm.getNodeMemory((MemoryFactory) node);
            if (memory.getSegmentMemory() == null) {
                // segment has never been initialized, which means the rule has never been linked.
                return;
            }
            if (NodeTypeEnums.isBetaNode(node)) {
                BetaMemory bm;
                if (NodeTypeEnums.AccumulateNode == node.getType()) {
                    AccumulateMemory am = (AccumulateMemory) memory;
                    bm = am.getBetaMemory();
                    FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                    Tuple lt = BetaNode.getFirstTuple( bm.getLeftTupleMemory(), it );
                    for (; lt != null; lt = (LeftTuple) it.next(lt)) {
                        AccumulateContext accctx = (AccumulateContext) lt.getContextObject();
                        followPeer(accctx.getResultLeftTuple(), smem, sinks,  sinks.size()-1, insert);
                    }
                } else if ( NodeTypeEnums.ExistsNode == node.getType() ) {
                    bm = (BetaMemory) wm.getNodeMemory((MemoryFactory) node);
                    FastIterator it = bm.getRightTupleMemory().fullFastIterator(); // done off the RightTupleMemory, as exists only have unblocked tuples on the left side
                    RightTuple rt = (RightTuple) BetaNode.getFirstTuple( bm.getRightTupleMemory(), it );
                    for (; rt != null; rt = (RightTuple) it.next(rt)) {
                        for ( LeftTuple lt = rt.getBlocked(); lt != null; lt = lt.getBlockedNext() ) {
                            if ( lt.getFirstChild() != null ) {
                                followPeer(lt.getFirstChild(), smem, sinks,  sinks.size()-1, insert);
                            }
                        }
                    }
                } else {
                    bm = (BetaMemory) wm.getNodeMemory((MemoryFactory) node);
                    FastIterator it = bm.getLeftTupleMemory().fullFastIterator();
                    Tuple lt = BetaNode.getFirstTuple( bm.getLeftTupleMemory(), it );
                    for (; lt != null; lt = (LeftTuple) it.next(lt)) {
                        if ( lt.getFirstChild() != null ) {
                            followPeerFromLeftInput(lt.getFirstChild(), smem, sinks, insert);
                        }
                    }
                }
                return;
            } else if (NodeTypeEnums.FromNode == node.getType()) {
                FromMemory fm = (FromMemory) wm.getNodeMemory((MemoryFactory) node);
                TupleMemory ltm = fm.getBetaMemory().getLeftTupleMemory();
                FastIterator it = ltm.fullFastIterator();
                for (LeftTuple lt = (LeftTuple)ltm.getFirst(null); lt != null; lt = (LeftTuple) it.next(lt)) {
                    if ( lt.getFirstChild() != null ) {
                        followPeerFromLeftInput(lt.getFirstChild(), smem, sinks, insert);
                    }
                }
                return;
            }
            sinks.add((LeftTupleSink) node);
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
        final ObjectTypeNodeMemory omem = wm.getNodeMemory(otn);
        LeftTupleSink firstLiaSink = lian.getSinkPropagator().getFirstLeftTupleSink();
        Iterator<InternalFactHandle> it = omem.iterator();

        while (it.hasNext()) {
            InternalFactHandle fh = it.next();
            if (fh.getFirstLeftTuple() != null ) {
                for (LeftTuple childLt = fh.getFirstLeftTuple(); childLt != null; childLt = childLt.getHandleNext()) {
                    if ( childLt.getTupleSink() == firstLiaSink ) {
                        followPeer(childLt, smem, sinks,  sinks.size()-1, insert);
                    }
                }
            }
        }
    }

    private static void followPeerFromLeftInput(LeftTuple lt, SegmentMemory smem, List<LeftTupleSink> sinks, boolean insert) {
        // *** if you make a fix here, it most likely needs to be in PhreakActivationIteratorToo ***
        for (; lt != null; lt = lt.getHandleNext()) {
            followPeer(lt, smem, sinks, sinks.size() -1, insert);
        }
    }

    private static void followPeer(LeftTuple lt, SegmentMemory smem, List<LeftTupleSink> sinks, int i, boolean insert) {
        // *** if you make a fix here, it most likely needs to be in PhreakActivationIteratorToo ***

        LeftTupleSink sink = sinks.get(i);

        if ( i == 0 ) {
            if ( insert ) {
                if ( NodeTypeEnums.isBetaNode(sink) ) {
                    BetaNode bn = ( BetaNode ) sink;
                    if ( bn.isRightInputIsRiaNode() ) {
                        // must also create and stage the LeftTuple for the subnetwork
                        SegmentMemory subSmem = smem.getPrevious(); // Subnetwork segment will be before this one
                        insertPeerLeftTuple(lt, (LeftTupleSink)subSmem.getRootNode(), subSmem);
                    }
                }
                insertPeerLeftTuple(lt, sink, smem);
            } else {
                if ( NodeTypeEnums.isBetaNode(sink) ) {
                    BetaNode bn = ( BetaNode ) sink;
                    if ( bn.isRightInputIsRiaNode() ) {
                        // must also create and stage the LeftTuple for the subnetwork
                        SegmentMemory subSmem = smem.getPrevious(); // Subnetwork segment will be before this one
                        deletePeerLeftTuple(lt, (LeftTupleSink)subSmem.getRootNode(), subSmem);
                    }
                }
                deletePeerLeftTuple(lt, sink, smem);
            }
        } else {
            LeftTuple peer = lt;
            while (peer.getTupleSink() != sink) {
                peer = peer.getPeer();
            }

            if (NodeTypeEnums.AccumulateNode == peer.getTupleSink().getType()) {
                AccumulateContext accctx = (AccumulateContext) peer.getContextObject();
                followPeer(accctx.getResultLeftTuple(), smem, sinks,  i-1, insert);
            } else if ( peer.getFirstChild() != null ) {
                for (LeftTuple childLt = peer.getFirstChild(); childLt != null; childLt = childLt.getHandleNext()) {
                    followPeer(childLt, smem, sinks, i-1, insert);
                }
            }
        }
    }

    private static void deletePeerLeftTuple(LeftTuple lt, LeftTupleSink newNode, SegmentMemory smem) {
        LeftTuple peer = lt;
        LeftTuple previousPeer = null;
        while (peer.getTupleSink() != newNode) {
            previousPeer = peer;
            peer = peer.getPeer();
        }

        switch( peer.getStagedType() ) {
            case LeftTuple.INSERT: {
                // insert was never propagated, thus has no children, does not need to be staged.
                smem.getStagedLeftTuples().removeInsert(peer);
                break;
            }
            case LeftTuple.UPDATE: {
                smem.getStagedLeftTuples().removeUpdate(peer);
                // don't break, so that this falls through and calls addDelete
            }
            case LeftTuple.NONE: {
                smem.getStagedLeftTuples().addDelete(peer);
            }
            case LeftTuple.DELETE: {
                // do nothing, leave it staged for delete, added for documention help
            }
        }

        if (previousPeer == null) {
            // the first sink is being removed, which is the first peer. The next peer must be set as the first peer.
            LeftTuple leftPrevious = peer.getHandlePrevious();
            LeftTuple leftNext = peer.getHandleNext();

            LeftTuple rightPrevious = peer.getRightParentPrevious();
            LeftTuple rightNext = peer.getRightParentNext();

            LeftTuple newPeer = peer.getPeer();
            if ( newPeer != null ) {
                replaceChildLeftTuple(peer, leftPrevious, leftNext, rightPrevious, rightNext, newPeer);

            } else {
                // no peers to support this, so remove completely.
                lt.unlinkFromLeftParent();
                lt.unlinkFromRightParent();
            }
        } else {
            // mid or end LeftTuple peer is being removed
            previousPeer.setPeer(peer.getPeer());
        }
    }

    private static void replaceChildLeftTuple(LeftTuple peer, LeftTuple leftPrevious, LeftTuple leftNext, LeftTuple rightPrevious, LeftTuple rightNext, LeftTuple newPeer) {boolean isHandle = peer.getLeftParent() == null;
        InternalFactHandle fh = peer.getFactHandle();
        LeftTuple leftParent = peer.getLeftParent();
        RightTuple rightParent = peer.getRightParent();

        newPeer.setLeftParent( peer.getLeftParent() );
        newPeer.setRightParent( peer.getRightParent() );

        // replace left
        if ( leftPrevious == null && leftNext == null ) {
            // no other tuples, simply replace
            if ( isHandle ) {
                fh.removeLeftTuple( peer );
                fh.addFirstLeftTuple( newPeer );
            }   else {
                 peer.unlinkFromLeftParent();
                 leftParent.setFirstChild(newPeer);
                 leftParent.setLastChild(newPeer);
            }
        } else if ( leftNext != null ) {
            // replacing first
            newPeer.setHandleNext( leftNext );
            leftNext.setHandlePrevious( newPeer );
            if ( isHandle ) {
                fh.setFirstLeftTuple(newPeer);
            } else {
                leftParent.setFirstChild(newPeer);
            }
        } else {
            // replacing last
            newPeer.setHandlePrevious( leftPrevious );
            leftPrevious.setHandleNext( newPeer );
            if ( isHandle ) {
                fh.setLastLeftTuple(newPeer);
            } else {
                leftParent.setLastChild(newPeer);
            }
        }

        // replace right
        if ( rightParent != null ) {
            // LiaNode LeftTuples have no right parents
            if ( rightPrevious == null && rightNext == null ) {
                // no other tuples, simply replace
                peer.unlinkFromRightParent();
                rightParent.setFirstChild(newPeer);
                rightParent.setLastChild(newPeer);
            } else if ( rightNext != null ) {
                // replacing first
                newPeer.setRightParentNext(rightNext);
                rightNext.setRightParentPrevious(newPeer);
                rightParent.setFirstChild(newPeer);
            } else {
                // replacing last
                newPeer.setRightParentPrevious(rightPrevious);
                rightPrevious.setRightParentNext(newPeer);
                rightParent.setLastChild(newPeer);
            }
        }
    }

    private static void insertPeerLeftTuple(LeftTuple lt, LeftTupleSink newNode, SegmentMemory smem) {
        // add to end of peer list
        LeftTuple peer = lt;
        while (peer.getPeer() != null) {
            peer = peer.getPeer();
        }

        LeftTuple newPeer = newNode.createPeer(peer);
        smem.getStagedLeftTuples().addInsert(newPeer);
    }

    private static void collectRtnPathMemories(LeftTupleSource lt,
                                              InternalWorkingMemory wm,
                                              List<PathMemory> pathMems,
                                              TerminalNode excludeTerminalNode) {
        for (LeftTupleSink sink : lt.getSinkPropagator().getSinks()) {
            if (sink == excludeTerminalNode) {
                continue;
            }
            if (NodeTypeEnums.isLeftTupleSource(sink)) {
                collectRtnPathMemories((LeftTupleSource) sink, wm, pathMems, excludeTerminalNode);
            } else if (NodeTypeEnums.isTerminalNode(sink)) {
                // getting will cause an initialization of rtn, which will recursively initialise rians too.
                PathMemory pmem = (PathMemory) wm.getNodeMemory((MemoryFactory) sink);
                pathMems.add(pmem);
            } else if (NodeTypeEnums.RightInputAdaterNode == sink.getType()) {
                RiaNodeMemory riaMem = (RiaNodeMemory) wm.getNodeMemory((MemoryFactory) sink);
                pathMems.add(riaMem.getRiaPathMemory());
            } else {
                throw new RuntimeException("Error: Unknown Node. Defensive programming test..");
            }
        }
    }

    private static PathMemory getFirstRtnPathMemory(LeftTupleSource lt,
                                                    InternalWorkingMemory wm,
                                                    TerminalNode excludeTerminalNode) {
        for (LeftTupleSink sink : lt.getSinkPropagator().getSinks()) {
            if (sink == excludeTerminalNode) {
                continue;
            }
            if (NodeTypeEnums.isLeftTupleSource(sink)) {
                PathMemory result = getFirstRtnPathMemory((LeftTupleSource) sink, wm, excludeTerminalNode);
                if (result != null) {
                    return result;
                }
            } else if (NodeTypeEnums.isTerminalNode(sink)) {
                // getting will cause an initialization of rtn, which will recursively initialise rians too.
                return (PathMemory) wm.getNodeMemory((MemoryFactory) sink);
            } else if (NodeTypeEnums.RightInputAdaterNode == sink.getType()) {
                RiaNodeMemory riaMem = (RiaNodeMemory) wm.getNodeMemory((MemoryFactory) sink);
                return riaMem.getRiaPathMemory();
            } else {
                throw new RuntimeException("Error: Unknown Node. Defensive programming test..");
            }
        }
        return null;
    }

     private static LeftTupleSource getNetworkSplitPoint(TerminalNode tn) {
         LeftTupleSource lt = tn.getLeftTupleSource();

         // iterate to find split point, or to the root
         while ( lt.getType() != NodeTypeEnums.LeftInputAdapterNode && lt.getAssociatedRuleSize() == 1 ) {
             lt = lt.getLeftTupleSource();
         }

         return lt;
     }

     public static SegmentMemory splitSegment(SegmentMemory sm1, LeftTupleSource splitNode) {
         // create new segment, starting after split
         SegmentMemory sm2 = new SegmentMemory(splitNode.getSinkPropagator().getFirstLeftTupleSink()); // we know there is only one sink

         if ( sm1.getFirst() != null ) {
             for ( SegmentMemory sm = sm1.getFirst(); sm != null;) {
                 SegmentMemory next = sm.getNext();
                 sm1.remove(sm);
                 sm2.add(sm);
                 sm = next;
             }
         }

         sm1.add( sm2 );

         sm2.setPos(sm1.getPos());  // clone for now, it's corrected later
         sm2.setSegmentPosMaskBit(sm1.getSegmentPosMaskBit()); // clone for now, it's corrected later
         sm2.setLinkedNodeMask(sm1.getLinkedNodeMask());  // clone for now, it's corrected later

         sm2.mergePathMemories( sm1 );

         // re-assigned tip nodes
         sm2.setTipNode(sm1.getTipNode());
         sm1.setTipNode( splitNode ); // splitNode is now tip of original segment

         if ( sm1.getTipNode().getType() == NodeTypeEnums.LeftInputAdapterNode ) {
             if (  !sm1.getStagedLeftTuples().isEmpty() ) {
                 // Segments with only LiaNode's cannot have staged LeftTuples, so move them down to the new Segment
                sm2.getStagedLeftTuples().addAll(sm1.getStagedLeftTuples());
             }
         }

         // find the pos of the node in the segment
         int pos = nodeSegmentPosition(sm1, splitNode);

         splitNodeMemories(sm1, sm2, pos);

         splitBitMasks(sm1, sm2, pos);

         return sm2;
     }

     private static void mergeSegment(SegmentMemory sm1, SegmentMemory sm2) {
         // sm1 may not be linked yet to sm2 because sm2 has been just created
         if (sm1.contains( sm2 )) {
             sm1.remove( sm2 );
         }

         if ( sm2.getFirst() != null ) {
             for ( SegmentMemory sm = sm2.getFirst(); sm != null;) {
                 SegmentMemory next = sm.getNext();
                 sm1.add(sm);
                 sm2.remove(sm);
                 sm = next;
             }
         }

         sm1.setStagedTuples( sm2.getStagedLeftTuples() );

         // re-assigned tip nodes
         sm1.setTipNode(sm2.getTipNode());

         mergeNodeMemories(sm1, sm2);

         mergeBitMasks(sm1, sm2);
     }

     private static void splitBitMasks(SegmentMemory sm1, SegmentMemory sm2, int pos) {
         int splitPos = pos + 1; // +1 as zero based
         long currentAllLinkedMaskTest = sm1.getAllLinkedMaskTest();
         long currentLinkedNodeMask = sm1.getLinkedNodeMask();
         long mask = (1L << splitPos) - 1;

         sm1.setAllLinkedMaskTest( mask & currentAllLinkedMaskTest );
         sm1.setLinkedNodeMask( sm1.getLinkedNodeMask() & sm1.getAllLinkedMaskTest() );

         mask = currentAllLinkedMaskTest >> splitPos;
         sm2.setAllLinkedMaskTest( mask );
         sm2.setLinkedNodeMask( mask & (currentLinkedNodeMask >> splitPos) );
     }

     private static void mergeBitMasks(SegmentMemory sm1, SegmentMemory sm2) {
         LinkedList<Memory> smNodeMemories2 =  sm2.getNodeMemories();

         long mask = sm2.getAllLinkedMaskTest() << smNodeMemories2.size();
         sm1.setAllLinkedMaskTest( mask & sm1.getAllLinkedMaskTest() );

         mask = sm2.getAllLinkedMaskTest() << smNodeMemories2.size();
         sm1.setLinkedNodeMask(mask & sm1.getLinkedNodeMask());
     }

     private static void splitNodeMemories(SegmentMemory sm1, SegmentMemory sm2, int pos) {
         LinkedList<Memory> smNodeMemories1 =  sm1.getNodeMemories();
         LinkedList<Memory> smNodeMemories2 =  sm2.getNodeMemories();

         Memory mem = smNodeMemories1.getFirst();
         int nodePosMask = 1;
         for ( int i = 0,length = smNodeMemories1.size(); i < length; i++) {
             Memory next = mem.getNext();
             if ( i > pos ) {
                 smNodeMemories1.remove(mem);
                 smNodeMemories2.add(mem);
                 mem.setSegmentMemory(sm2);

                 // correct the NodePosMaskBit
                 BetaMemory bm = null;
                 if ( mem instanceof AccumulateNode.AccumulateMemory ) {
                     bm = ((AccumulateNode.AccumulateMemory) mem).getBetaMemory();
                 } else if ( mem instanceof BetaMemory ) {
                     bm = ( BetaMemory ) mem;
                 }
                 if ( bm != null ) {  // node may not be a beta
                     bm.setNodePosMaskBit(nodePosMask);
                 }
                 nodePosMask = nodePosMask << 1;
             }
             mem = next;
         }
     }

     private static void mergeNodeMemories(SegmentMemory sm1, SegmentMemory sm2) {
         LinkedList<Memory> smNodeMemories1 =  sm1.getNodeMemories();
         LinkedList<Memory> smNodeMemories2 =  sm2.getNodeMemories();



         int nodePosMask = 1;
         for ( int i = 0,length = smNodeMemories1.size(); i < length; i++) {
             nodePosMask = nodePosMask >> 1;
         }

         for ( Memory mem = smNodeMemories2.getFirst(); mem != null; ) {
             Memory next = mem.getNext();
             smNodeMemories2.remove(mem);
             smNodeMemories1.add(mem);
             mem.setSegmentMemory(sm1);

             // correct the NodePosMaskBit
             BetaMemory bm = null;
             if ( mem instanceof AccumulateNode.AccumulateMemory ) {
                 bm = ((AccumulateNode.AccumulateMemory) mem).getBetaMemory();
             } else if ( mem instanceof BetaMemory ) {
                 bm = ( BetaMemory ) mem;
             }
             if ( bm != null ) {  // node may not be a beta
                 bm.setNodePosMaskBit(nodePosMask);
             }
             nodePosMask = nodePosMask >> 1;
             mem = next;
         }
     }

     private static int nodeSegmentPosition(SegmentMemory sm1, LeftTupleSource splitNode) {
         LeftTupleSource lt = splitNode;
         int nodePos = 0;
         while ( lt != sm1.getRootNode() ) {
             lt = lt.getLeftTupleSource();
             nodePos++;
         }
         return nodePos;
     }
}
