package org.drools.reliability.core;

import org.drools.core.common.ConcurrentNodeMemories;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.RuleBase;
import org.drools.core.reteoo.SegmentMemory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReliableNodeMemories extends ConcurrentNodeMemories {

    private final Map<Integer, Memory> changedMemories = new HashMap<>();
    private final Set<SegmentMemory> changedSegments = new HashSet<>();

    public ReliableNodeMemories(RuleBase ruleBase) {
        super(ruleBase);
    }

    @Override
    public Memory getNodeMemory(MemoryFactory node, ReteEvaluator reteEvaluator) {
        Memory memory = super.getNodeMemory(node, reteEvaluator);
        changedMemories.put(node.getMemoryId(), memory);
        if (memory.getSegmentMemory() != null) {
            changedSegments.add(memory.getSegmentMemory());
        }
        return memory;
    }

    public void safepoint() {
        // TODO: persist memories
        //Storage<String, Object> componentsStorage = StorageManagerFactory.get().getStorageManager().getOrCreateStorageForSession(, "components");
        //componentsStorage.put("memories", changedMemories);
        //componentsStorage.put("segments", changedSegments);
        changedMemories.clear();
        changedSegments.clear();
    }
}
