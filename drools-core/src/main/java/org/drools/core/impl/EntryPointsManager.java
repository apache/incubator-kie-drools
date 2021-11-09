/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.rule.EntryPointId;

public class EntryPointsManager {

    private final ReteEvaluator reteEvaluator;
    private final InternalKnowledgeBase kBase;

    InternalWorkingMemoryEntryPoint defaultEntryPoint;

    private final Map<String, WorkingMemoryEntryPoint> entryPoints = new ConcurrentHashMap<>();

    public EntryPointsManager(ReteEvaluator reteEvaluator) {
        this.reteEvaluator = reteEvaluator;
        this.kBase = reteEvaluator.getKnowledgeBase();
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

    public NamedEntryPoint createNamedEntryPoint(EntryPointNode addedNode, EntryPointId id) {
        return kBase.getConfiguration().getComponentFactory().getNamedEntryPointFactory().createNamedEntryPoint(addedNode, id, reteEvaluator);
    }

    public void updateEntryPointsCache() {
        if (kBase.getAddedEntryNodeCache() != null) {
            for (EntryPointNode addedNode : kBase.getAddedEntryNodeCache()) {
                EntryPointId id = addedNode.getEntryPoint();
                if (EntryPointId.DEFAULT.equals(id)) continue;
                WorkingMemoryEntryPoint wmEntryPoint = createNamedEntryPoint(addedNode, id);
                entryPoints.put(id.getEntryPointId(), wmEntryPoint);
            }
        }

        if (kBase.getRemovedEntryNodeCache() != null) {
            for (EntryPointNode removedNode : kBase.getRemovedEntryNodeCache()) {
                entryPoints.remove(removedNode.getEntryPoint().getEntryPointId());
            }
        }
    }

    public void reset() {
        defaultEntryPoint.reset();
        updateEntryPointsCache();
    }

    private void initDefaultEntryPoint() {
        this.defaultEntryPoint = createDefaultEntryPoint();
        this.entryPoints.clear();
        this.entryPoints.put("DEFAULT", this.defaultEntryPoint);
    }

    private InternalWorkingMemoryEntryPoint createDefaultEntryPoint() {
        EntryPointNode epn = this.kBase.getRete().getEntryPointNode( EntryPointId.DEFAULT );
        return createNamedEntryPoint(epn, EntryPointId.DEFAULT);
    }
}
