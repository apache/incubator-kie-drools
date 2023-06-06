package org.drools.reliability.core;

import org.drools.core.common.ConcurrentNodeMemories;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.Storage;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.SegmentMemory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.HashMap;
import java.util.Map;

public class ReliableNodeMemories extends ConcurrentNodeMemories {

    private final Map<Integer, Memory> changedMemories = new HashMap<>();
    private final Map<Integer,SegmentMemory> changedSegments = new HashMap<>();
    private final Storage<Integer, Object> storage;

    public ReliableNodeMemories(InternalRuleBase ruleBase, Storage<Integer, Object> storage) {
        super(ruleBase);
        this.storage = storage;
    }

    @Override
    public Memory getNodeMemory(MemoryFactory node, ReteEvaluator reteEvaluator) {
        Memory memory = super.getNodeMemory(node, reteEvaluator);
        changedMemories.put(node.getMemoryId(), memory);
        if (memory.getSegmentMemory() != null) {
            changedSegments.put(memory.getSegmentMemory().getRootNode().getId(),memory.getSegmentMemory());
        }
        return memory;
    }

    public void safepoint() {
        // TODO: persist memories
        for (Integer key : changedMemories.keySet()){
            storage.put(key,changedMemories.get(key));
        }
        for (Integer key : changedSegments.keySet()){
            storage.put(key, changedSegments.get(key));
        }

        changedMemories.clear();
        changedSegments.clear();

        if (storage.requiresFlush()) {this.storage.flush();}

    }

    public void reInit(StatefulKnowledgeSession session) {
        super.reInit(this.storage, session);
    }
}
