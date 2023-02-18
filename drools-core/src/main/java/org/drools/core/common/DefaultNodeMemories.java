/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.common;

import java.util.HashSet;
import java.util.Set;

import org.drools.core.impl.RuleBase;
import org.drools.core.reteoo.SegmentMemory;
import org.kie.internal.runtime.StatefulKnowledgeSession;


public class DefaultNodeMemories implements NodeMemories {

    private Memory[] memories;

    private final RuleBase ruleBase;

    public DefaultNodeMemories(RuleBase ruleBase) {
        this.ruleBase = ruleBase;
        clear();
    }

    public void clearNodeMemory( MemoryFactory node ) {
        if ( peekNodeMemory(node.getMemoryId()) != null ) {
            memories[node.getMemoryId()] = null;
        }
    }
    
    public void clear() {
        memories = new Memory[ruleBase.getMemoryCount()];
    }

    public void resetAllMemories(StatefulKnowledgeSession session) {
        RuleBase kBase = (RuleBase) session.getKieBase();
        Set<SegmentMemory> smemSet = new HashSet<>();

        for (int i = 0; i < memories.length; i++) {
            Memory memory = memories[i];
            if (memory != null) {
                memory.reset();
                smemSet.add(memory.getSegmentMemory());
            }
        }

        smemSet.forEach(smem -> resetSegmentMemory(session, kBase, smem));
    }

    private void resetSegmentMemory(StatefulKnowledgeSession session, RuleBase kBase, SegmentMemory smem) {
        if (smem != null) {
            smem.reset(kBase.getSegmentPrototype(smem));
            if (smem.isSegmentLinked()) {
                smem.notifyRuleLinkSegment((InternalWorkingMemory) session);
            }
        }
    }

    public Memory getNodeMemory(MemoryFactory node, ReteEvaluator reteEvaluator) {
        if( node.getMemoryId() >= memories.length ) {
            resize( node );
        }
        Memory memory = memories[node.getMemoryId()];

        if( memory == null ) {
            memory = createNodeMemory( node, reteEvaluator );
        }

        return memory;
    }


    /**
     * Checks if a memory does not exists for the given node and
     * creates it.
     */
    private Memory createNodeMemory( MemoryFactory node, ReteEvaluator reteEvaluator ) {
        // need to try again in a synchronized code block to make sure
        // it was not created yet
        Memory memory = memories[node.getMemoryId()];
        if( memory == null ) {
            memory = node.createMemory( ruleBase.getRuleBaseConfiguration(), reteEvaluator );
            memories[node.getMemoryId()] = memory;
        }
        return memory;
    }

    /**
     * @param node
     */
    private void resize( MemoryFactory node ) {
        if( node.getMemoryId() >= memories.length ) {
            // adding some buffer for new nodes, so that we reduce array copies
            int size = Math.max( ruleBase.getMemoryCount(), node.getMemoryId() + 32 );
            Memory[] newMem = new Memory[size];
            System.arraycopy(memories, 0, newMem, 0, memories.length);
            memories = newMem;
        }
    }

    public Memory peekNodeMemory(int memoryId ) {
        return memoryId < memories.length ? memories[memoryId] : null;
    }

    public int length() {
        return memories.length;
    }

}
