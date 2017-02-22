/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.ruleunit;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.common.ConcurrentNodeMemories;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NodeMemories;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import static org.drools.core.ruleunit.RuleUnitUtil.RULE_UNIT_ENTRY_POINT;
import static org.drools.core.ruleunit.RuleUnitUtil.getUnitName;

public class RuleUnitsNodeMemories implements NodeMemories {

    private final InternalKnowledgeBase kBase;

    private NodeMemories unitMemories;

    private final Map<RuleUnit.Identity, RuleUnitMemory> unitMemoriesMap = new HashMap<>();

    public RuleUnitsNodeMemories( InternalKnowledgeBase kBase ) {
        this.kBase = kBase;
    }

    public void bindRuleUnit( StatefulKnowledgeSessionImpl session, RuleUnit ruleUnit ) {
        RuleUnit.Identity ruId = ruleUnit.getUnitIdentity();
        RuleUnitMemory memory = unitMemoriesMap.get(ruId);
        if (memory == null) {
            unitMemoriesMap.put(ruId, createMemory(kBase, session, ruleUnit));
        } else {
            unitMemories = memory.memories;
            // TODO property reactive update of rule unit only if necessary
            //session.getEntryPoint( RULE_UNIT_ENTRY_POINT ).update( memory.handle, ruleUnit );
        }
    }

    private RuleUnitMemory createMemory(InternalKnowledgeBase kBase, StatefulKnowledgeSessionImpl session, RuleUnit ruleUnit) {
        RuleUnitMemory memory = new RuleUnitMemory( kBase, ruleUnit );
        unitMemories = memory.memories;
        session.initInitialFact(kBase, null);
        memory.handle = session.getEntryPoint( RULE_UNIT_ENTRY_POINT ).insert( ruleUnit );
        return memory;
    }

    public void unbindRuleUnit() {
        unitMemories = null;
    }

    @Override
    public <T extends Memory> T getNodeMemory( MemoryFactory<T> node, InternalWorkingMemory wm ) {
        return unitMemories.getNodeMemory( node, wm );
    }

    @Override
    public void clearNodeMemory( MemoryFactory node ) {
        unitMemories.clearNodeMemory( node );
    }

    @Override
    public void clear() {
        unitMemories.clear();
    }

    @Override
    public Memory peekNodeMemory( int memoryId ) {
        return unitMemories.peekNodeMemory( memoryId );
    }

    @Override
    public int length() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetAllMemories( StatefulKnowledgeSession session ) {
        unitMemories.resetAllMemories( session );
    }

    private static class RuleUnitMemory {
        NodeMemories memories;
        FactHandle handle;

        RuleUnitMemory(InternalKnowledgeBase kBase, RuleUnit ruleUnit) {
            memories = new ConcurrentNodeMemories( kBase, getUnitName(ruleUnit) );
        }
    }
}
