/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.reteoo;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.common.LeftTupleSetsImpl;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NetworkNode;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.reteoo.TimerNode.TimerNodeMemory;
import org.drools.core.util.AtomicBitwiseLong;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.drools.core.phreak.SegmentUtilities.getQuerySegmentMemory;

public class SegmentMemory extends LinkedList<SegmentMemory>
        implements
        LinkedListNode<SegmentMemory> {

    protected static final Logger log = LoggerFactory.getLogger(SegmentMemory.class);
    protected static final boolean isLogTraceEnabled = log.isTraceEnabled();

    private          NetworkNode        rootNode;
    private          NetworkNode        tipNode;
    private          LinkedList<Memory> nodeMemories;
    private          AtomicBitwiseLong  linkedNodeMask;
    private          AtomicBitwiseLong  dirtyNodeMask;
    private          long               allLinkedMaskTest;
    private          List<PathMemory>   pathMemories;
    private          long               segmentPosMaskBit;
    private          int                pos;
    private volatile LeftTupleSets      stagedLeftTuples;
    private          boolean            active;
    private          SegmentMemory      previous;
    private          SegmentMemory      next;

    private transient List<PathMemory>  dataDrivenPMems;

    public SegmentMemory(NetworkNode rootNode) {
        this.rootNode = rootNode;
        this.linkedNodeMask = new AtomicBitwiseLong();
        this.dirtyNodeMask = new AtomicBitwiseLong();
        this.pathMemories = new ArrayList<PathMemory>(1);
        this.nodeMemories = new LinkedList<Memory>();
        this.stagedLeftTuples = new LeftTupleSetsImpl();
    }

    public NetworkNode getRootNode() {
        return rootNode;
    }

    public NetworkNode getTipNode() {
        return tipNode;
    }

    public void setTipNode(NetworkNode tipNode) {
        this.tipNode = tipNode;
    }

    public LeftTupleSink getSinkFactory() {
        return (LeftTupleSink) rootNode;
    }

    public void setSinkFactory(LeftTupleSink sink) {
    }

    public <T extends Memory> T createNodeMemory(MemoryFactory<T> memoryFactory,
                                                 InternalWorkingMemory wm) {
        T memory = wm.getNodeMemory(memoryFactory);
        nodeMemories.add(memory);
        return memory;
    }

    public LinkedList<Memory> getNodeMemories() {
        return nodeMemories;
    }

    public long getLinkedNodeMask() {
        return linkedNodeMask.get();
    }

    public void setLinkedNodeMask(long linkedNodeMask) {
        this.linkedNodeMask.set( linkedNodeMask );
        //this.linkedNodeMask = linkedNodeMask;
    }

    public long getDirtyNodeMask() {
        return dirtyNodeMask.get();
    }

    public void resetDirtyNodeMask() {
        dirtyNodeMask.set( 0 );
    }

    public void updateDirtyNodeMask(long mask) {
        dirtyNodeMask.getAndBitwiseOr(mask);
    }

    public void updateCleanNodeMask(long mask) {
        dirtyNodeMask.getAndBitwiseReset(mask);
        //dirtyNodeMask = dirtyNodeMask & ~( 1 << mask );
    }


    public String getRuleNames() {
        StringBuilder sbuilder = new StringBuilder();
        for (int i = 0; i < pathMemories.size(); i++) {
            if (i > 0) {
                sbuilder.append(", ");
            }
            sbuilder.append(pathMemories.get(i));
        }

        return sbuilder.toString();
    }

    public void linkNode(long mask,
                         InternalWorkingMemory wm) {
        linkedNodeMask.getAndBitwiseOr( mask );
        //dirtyNodeMask = dirtyNodeMask | mask;
        if (isLogTraceEnabled) {
            log.trace("LinkNode notify=true nmask={} smask={} spos={} rules={}", mask, linkedNodeMask, pos, getRuleNames());
        }

        notifyRuleLinkSegment(wm);
    }

    public void linkNodeWithoutRuleNotify(long mask) {
        linkedNodeMask.getAndBitwiseOr( mask );
        //dirtyNodeMask = dirtyNodeMask | mask;
        if (isLogTraceEnabled) {
            log.trace("LinkNode notify=false nmask={} smask={} spos={} rules={}", mask, linkedNodeMask, pos, getRuleNames());
        }

        linkSegmentWithoutRuleNotify();
    }

    public void linkSegmentWithoutRuleNotify(InternalWorkingMemory wm, long mask) {
        //dirtyNodeMask = dirtyNodeMask | mask;
        dirtyNodeMask.getAndBitwiseOr( mask );
        linkSegmentWithoutRuleNotify();
    }

    private void linkSegmentWithoutRuleNotify() {
        if (isSegmentLinked()) {
            for (int i = 0, length = pathMemories.size(); i < length; i++) {
                // do not use foreach, don't want Iterator object creation
                pathMemories.get(i).linkNodeWithoutRuleNotify(segmentPosMaskBit);
            }
        }
    }

    public void notifyRuleLinkSegment(InternalWorkingMemory wm, long mask) {
        //dirtyNodeMask = dirtyNodeMask | mask;
        dirtyNodeMask.getAndBitwiseOr( mask );
        notifyRuleLinkSegment(wm);
    }

    public void notifyRuleLinkSegment(InternalWorkingMemory wm) {
        if (isSegmentLinked()) {
            for (int i = 0, length = pathMemories.size(); i < length; i++) {
                // do not use foreach, don't want Iterator object creation
                pathMemories.get(i).linkSegment(segmentPosMaskBit, wm);
            }
        }
    }

    public void unlinkNode(long mask,
                           InternalWorkingMemory wm) {
        boolean linked = isSegmentLinked();
        // some node unlinking does not unlink the segment, such as nodes after a Branch CE
        linkedNodeMask.getAndBitwiseXor(mask);
        dirtyNodeMask.getAndBitwiseOr( mask );
        //dirtyNodeMask = dirtyNodeMask | mask;


        if (isLogTraceEnabled) {
            log.trace("UnlinkNode notify=true nmask={} smask={} spos={} rules={}", mask, linkedNodeMask, pos, getRuleNames());
        }

        if (linked && !isSegmentLinked()) {
            for (int i = 0, length = pathMemories.size(); i < length; i++) {
                // do not use foreach, don't want Iterator object creation
                pathMemories.get(i).unlinkedSegment(segmentPosMaskBit,
                                                    wm);
            }
        } else {
            // if not unlinked, then we still need to notify if the rule is linked
            for (int i = 0, length = pathMemories.size(); i < length; i++) {
                // do not use foreach, don't want Iterator object creation
                if (pathMemories.get(i).isRuleLinked() ){
                    pathMemories.get(i).doLinkRule(wm);
                }
            }
        }
    }

    public void unlinkNodeWithoutRuleNotify(long mask) {
        linkedNodeMask.getAndBitwiseXor( mask );
        //dirtyNodeMask = dirtyNodeMask | mask;
        if (isLogTraceEnabled) {
            log.trace("UnlinkNode notify=false nmask={} smask={} spos={} rules={}", mask, linkedNodeMask, pos, getRuleNames());
        }
    }

    public long getAllLinkedMaskTest() {
        return allLinkedMaskTest;
    }

    public void setAllLinkedMaskTest(long allLinkedTestMask) {
        this.allLinkedMaskTest = allLinkedTestMask;
    }

    public boolean isSegmentLinked() {
        return (linkedNodeMask.get() & allLinkedMaskTest) == allLinkedMaskTest;
    }

    public List<PathMemory> getPathMemories() {
        return pathMemories;
    }

    public PathMemory getFirstDataDrivenPathMemory() {
        return getDataDrivenPathMemories().get(0);
    }

    private List<PathMemory> getDataDrivenPathMemories() {
        if (dataDrivenPMems == null) {
            dataDrivenPMems = new ArrayList<PathMemory>();
            for (PathMemory pmem : pathMemories) {
                RuleImpl rule = pmem.getRule();
                if (rule != null && rule.isDataDriven()) {
                    dataDrivenPMems.add(pmem);
                }
            }
            if (dataDrivenPMems.isEmpty()) {
                dataDrivenPMems.add(null);
            }
        }
        return dataDrivenPMems;
    }

    public void setPathMemories(List<PathMemory> ruleSegments) {
        this.pathMemories = ruleSegments;
    }

    public long getSegmentPosMaskBit() {
        return segmentPosMaskBit;
    }

    public void setSegmentPosMaskBit(long nodeSegmenMask) {
        this.segmentPosMaskBit = nodeSegmenMask;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean evaluating) {
        this.active = evaluating;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public LeftTupleSets getStagedLeftTuples() {
        return stagedLeftTuples;
    }

    public void setStagedTuples(LeftTupleSets stagedTuples) {
        this.stagedLeftTuples = stagedTuples;
    }

    public SegmentMemory getNext() {
        return this.next;
    }

    public void setNext(SegmentMemory next) {
        this.next = next;
    }

    public SegmentMemory getPrevious() {
        return this.previous;
    }

    public void setPrevious(SegmentMemory previous) {
        this.previous = previous;
    }

    public void nullPrevNext() {
        previous = null;
        next = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * rootNode.getId();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (!super.equals(obj)) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        SegmentMemory other = (SegmentMemory) obj;
        if (rootNode == null) {
            if (other.rootNode != null) { return false; }
        } else if (rootNode.getId() != other.rootNode.getId()) { return false; }
        return true;
    }

    public Prototype asPrototype() {
        return new Prototype(this);
    }

    public List<NetworkNode> getNodesInSegment() {
        List<NetworkNode> nodes = new java.util.LinkedList<NetworkNode>();
        NetworkNode currentNode = tipNode;
        while (currentNode != rootNode) {
            nodes.add(0, currentNode);
            currentNode = ((LeftTupleSinkNode)currentNode).getLeftTupleSource();
        }
        nodes.add(0, currentNode);
        return nodes;
    }

    public void reset(Prototype prototype) {
        this.dirtyNodeMask.set(0);
        this.linkedNodeMask.set( prototype != null ? prototype.linkedNodeMask : 0 );
        stagedLeftTuples.resetAll();
    }

    public static class Prototype {
        private NetworkNode                 rootNode;
        private NetworkNode                 tipNode;
        private long                        linkedNodeMask;
        private long                        allLinkedMaskTest;
        private long                        segmentPosMaskBit;
        private int                         pos;
        private List<MemoryPrototype>       memories = new ArrayList<MemoryPrototype>();
        private List<NetworkNode>           nodesInSegment;

        private Prototype(SegmentMemory smem) {
            this.rootNode = smem.rootNode;
            this.tipNode = smem.tipNode;
            this.linkedNodeMask = smem.linkedNodeMask.get();
            this.allLinkedMaskTest = smem.allLinkedMaskTest;
            this.segmentPosMaskBit = smem.segmentPosMaskBit;
            this.pos = smem.pos;
            for (Memory mem = smem.nodeMemories.getFirst(); mem != null; mem = mem.getNext()) {
                memories.add(MemoryPrototype.get(mem));
            }
        }

        public SegmentMemory newSegmentMemory(InternalWorkingMemory wm) {
            SegmentMemory smem = new SegmentMemory(rootNode);
            smem.tipNode = tipNode;
            smem.linkedNodeMask = new AtomicBitwiseLong( linkedNodeMask );
            smem.allLinkedMaskTest = allLinkedMaskTest;
            smem.segmentPosMaskBit = segmentPosMaskBit;
            smem.pos = pos;
            int i = 0;
            for (NetworkNode node : getNodesInSegment(smem)) {
                Memory mem = wm.getNodeMemory((MemoryFactory) node);
                mem.setSegmentMemory(smem);
                smem.getNodeMemories().add(mem);
                MemoryPrototype proto = memories.get(i++);
                if (proto != null) {
                    proto.populateMemory(wm, mem);
                }
            }

            return smem;
        }

        private List<NetworkNode> getNodesInSegment(SegmentMemory smem) {
            if (nodesInSegment == null) {
                nodesInSegment = smem.getNodesInSegment();
            }
            return nodesInSegment;
        }
    }

    public abstract static class MemoryPrototype {
        public static MemoryPrototype get(Memory memory) {
            if (memory instanceof BetaMemory) {
                return new BetaMemoryPrototype((BetaMemory)memory);
            }
            if (memory instanceof LeftInputAdapterNode.LiaNodeMemory) {
                return new LiaMemoryPrototype((LeftInputAdapterNode.LiaNodeMemory)memory);
            }
            if (memory instanceof QueryElementNode.QueryElementNodeMemory) {
                return new QueryMemoryPrototype((QueryElementNode.QueryElementNodeMemory)memory);
            }
            if (memory instanceof TimerNodeMemory) {
                return new TimerMemoryPrototype((TimerNodeMemory)memory);
            }
            if (memory instanceof AccumulateNode.AccumulateMemory) {
                return new AccumulateMemoryPrototype((AccumulateNode.AccumulateMemory)memory);
            }
            if (memory instanceof ReactiveFromNode.ReactiveFromMemory) {
                return new ReactiveFromMemoryPrototype((ReactiveFromNode.ReactiveFromMemory)memory);
            }
            return null;
        }

        public abstract void populateMemory(InternalWorkingMemory wm, Memory memory);
    }

    public static class BetaMemoryPrototype extends MemoryPrototype {

        private final long nodePosMaskBit;
        private RightInputAdapterNode riaNode;

        private BetaMemoryPrototype(BetaMemory betaMemory) {
            this.nodePosMaskBit = betaMemory.getNodePosMaskBit();
            if (betaMemory.getRiaRuleMemory() != null) {
                riaNode = betaMemory.getRiaRuleMemory().getRightInputAdapterNode();
            }
        }

        @Override
        public void populateMemory(InternalWorkingMemory wm, Memory memory) {
            BetaMemory betaMemory = (BetaMemory)memory;
            betaMemory.setNodePosMaskBit(nodePosMaskBit);
            if (riaNode != null) {
                RightInputAdapterNode.RiaNodeMemory riaMem = (RightInputAdapterNode.RiaNodeMemory)wm.getNodeMemory(riaNode);
                betaMemory.setRiaRuleMemory(riaMem.getRiaPathMemory());
            }
        }
    }

    public static class LiaMemoryPrototype extends MemoryPrototype {

        private final long nodePosMaskBit;

        private LiaMemoryPrototype(LeftInputAdapterNode.LiaNodeMemory liaMemory) {
            this.nodePosMaskBit = liaMemory.getNodePosMaskBit();
        }

        @Override
        public void populateMemory(InternalWorkingMemory wm, Memory liaMemory) {
            ((LeftInputAdapterNode.LiaNodeMemory)liaMemory).setNodePosMaskBit(nodePosMaskBit);
        }
    }

    public static class ReactiveFromMemoryPrototype extends MemoryPrototype {

        private final long nodePosMaskBit;

        private ReactiveFromMemoryPrototype(ReactiveFromNode.ReactiveFromMemory memory) {
            this.nodePosMaskBit = memory.getBetaMemory().getNodePosMaskBit();
        }

        @Override
        public void populateMemory(InternalWorkingMemory wm, Memory memory) {
            ((ReactiveFromNode.ReactiveFromMemory)memory).getBetaMemory().setNodePosMaskBit(nodePosMaskBit);
        }
    }

    public static class QueryMemoryPrototype extends MemoryPrototype {

        private final long nodePosMaskBit;
        private final QueryElementNode queryNode;

        private QueryMemoryPrototype(QueryElementNode.QueryElementNodeMemory queryMemory) {
            nodePosMaskBit = queryMemory.getNodePosMaskBit();
            this.queryNode = queryMemory.getNode();
        }

        @Override
        public void populateMemory(InternalWorkingMemory wm, Memory mem) {
            QueryElementNodeMemory qmem = (QueryElementNodeMemory)  mem;
            SegmentMemory querySmem = getQuerySegmentMemory(wm, (LeftTupleSource)qmem.getSegmentMemory().getRootNode(), queryNode);
            qmem.setQuerySegmentMemory(querySmem);
            qmem.setNodePosMaskBit(nodePosMaskBit);
        }
    }

    public static class TimerMemoryPrototype extends MemoryPrototype {

        private final long nodePosMaskBit;

        private TimerMemoryPrototype(TimerNodeMemory timerMemory) {
            nodePosMaskBit = timerMemory.getNodePosMaskBit();
        }

        @Override
        public void populateMemory(InternalWorkingMemory wm, Memory mem) {
            TimerNodeMemory tmem = (TimerNodeMemory)  mem;
            tmem.setNodePosMaskBit(nodePosMaskBit);
        }
    }

    public static class AccumulateMemoryPrototype extends MemoryPrototype {

        private final BetaMemoryPrototype betaProto;

        private AccumulateMemoryPrototype(AccumulateNode.AccumulateMemory accMemory) {
            betaProto = new BetaMemoryPrototype(accMemory.getBetaMemory());
        }

        @Override
        public void populateMemory(InternalWorkingMemory wm, Memory accMemory) {
            betaProto.populateMemory(wm, ((AccumulateNode.AccumulateMemory)accMemory).getBetaMemory());
        }
    }

    public static class FromMemoryPrototype extends MemoryPrototype {

        private final BetaMemoryPrototype betaProto;

        private FromMemoryPrototype(FromNode.FromMemory fromMemory) {
            betaProto = new BetaMemoryPrototype(fromMemory.getBetaMemory());
        }

        @Override
        public void populateMemory(InternalWorkingMemory wm, Memory fromMemory) {
            betaProto.populateMemory(wm, ((FromNode.FromMemory) fromMemory).getBetaMemory());
        }
    }
}
