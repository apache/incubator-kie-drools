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
package org.drools.kiesession.entrypoints;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.EntryPointsManager;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.base.rule.EntryPointId;

public class NamedEntryPointsManager implements EntryPointsManager {

    private final ReteEvaluator reteEvaluator;
    private final InternalRuleBase ruleBase;

    InternalWorkingMemoryEntryPoint defaultEntryPoint;

    private final Map<String, WorkingMemoryEntryPoint> entryPoints = new ConcurrentHashMap<>();

    public NamedEntryPointsManager(ReteEvaluator reteEvaluator) {
        this.reteEvaluator = reteEvaluator;
        this.ruleBase = reteEvaluator.getKnowledgeBase();
        initDefaultEntryPoint();
        updateEntryPointsCache();
    }

    public InternalWorkingMemoryEntryPoint getDefaultEntryPoint() {
        return defaultEntryPoint;
    }

    public WorkingMemoryEntryPoint getEntryPoint(String name) {
        return entryPoints.get(name);
    }

    public Collection<WorkingMemoryEntryPoint> getEntryPoints() {
        return this.entryPoints.values();
    }

    private InternalWorkingMemoryEntryPoint createNamedEntryPoint(EntryPointNode addedNode) {
        return RuntimeComponentFactory.get().getEntryPointFactory().createEntryPoint(addedNode, addedNode.getEntryPoint(), reteEvaluator);
    }

    public void updateEntryPointsCache() {
        if (ruleBase.getAddedEntryNodeCache() != null) {
            for (EntryPointNode addedNode : ruleBase.getAddedEntryNodeCache()) {
                entryPoints.computeIfAbsent(addedNode.getEntryPoint().getEntryPointId(), x -> createNamedEntryPoint(addedNode));
            }
        }

        if (ruleBase.getRemovedEntryNodeCache() != null) {
            for (EntryPointNode removedNode : ruleBase.getRemovedEntryNodeCache()) {
                entryPoints.remove(removedNode.getEntryPoint().getEntryPointId());
            }
        }
    }

    public void reset() {
        defaultEntryPoint.reset();
        updateEntryPointsCache();
    }

    private void initDefaultEntryPoint() {
        this.defaultEntryPoint = createNamedEntryPoint( this.ruleBase.getRete().getEntryPointNode( EntryPointId.DEFAULT ) );
        this.entryPoints.clear();
        this.entryPoints.put(EntryPointId.DEFAULT.getEntryPointId(), this.defaultEntryPoint);
    }
}
