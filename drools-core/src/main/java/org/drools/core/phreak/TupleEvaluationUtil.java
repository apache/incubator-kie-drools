package org.drools.core.phreak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.core.common.ActivationsManager;
import org.drools.core.common.Memory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.reteoo.AbstractTerminalNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.PathEndNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.Tuple;
import org.drools.core.util.LinkedList;

public class TupleEvaluationUtil {
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
                if ( pnode instanceof TerminalNode) {
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
        Memory mem;
        long          bit = 1;
        if ( sm.getRootNode().getType() == NodeTypeEnums.LeftInputAdapterNode && sm.getTipNode().getType() != NodeTypeEnums.LeftInputAdapterNode) {
            // The segment is the first and it has the lian shared with other nodes, the lian must be skipped, so adjust the bit and sink
            node =  sm.getRootNode().getSinkPropagator().getFirstLeftTupleSink();
            mem = sm.getNodeMemories()[1];
            bit = 2; // adjust bit to point to next node
        } else {
            node =  sm.getRootNode();
            mem = sm.getNodeMemories()[0];
        }

        PathMemory rtnPmem = NodeTypeEnums.isTerminalNode(pmem.getPathEndNode()) ?
                pmem :
                reteEvaluator.getNodeMemory((AbstractTerminalNode) pmem.getPathEndNode().getPathEndNodes()[0]);

        ActivationsManager activationsManager = pmem.getActualActivationsManager( reteEvaluator );
        RuleNetworkEvaluator.INSTANCE.outerEval(pmem, node, bit, mem, smems, sm.getPos(), leftTupleSets, activationsManager,
                new LinkedList<>(),
                true, rtnPmem.getOrCreateRuleAgendaItem(activationsManager).getRuleExecutor());
    }
}
