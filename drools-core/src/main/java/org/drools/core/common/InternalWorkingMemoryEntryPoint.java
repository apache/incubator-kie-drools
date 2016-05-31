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

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

public interface InternalWorkingMemoryEntryPoint extends EntryPoint {
    ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();

    InternalKnowledgeBase getKnowledgeBase();

    void delete(FactHandle factHandle,
                RuleImpl rule,
                Activation activation);

    void delete(FactHandle factHandle,
                RuleImpl rule,
                Activation activation,
                FactHandle.State fhState);

    void update(FactHandle handle,
                Object object,
                BitMask mask,
                Class<?> modifiedClass,
                Activation activation);

    TruthMaintenanceSystem getTruthMaintenanceSystem();

    EntryPointId getEntryPoint();
    InternalWorkingMemory getInternalWorkingMemory();

    FactHandle getFactHandleByIdentity(Object object);
    
    void reset();
    
    ObjectStore getObjectStore();

    FactHandleFactory getHandleFactory();
    
    EntryPointNode getEntryPointNode();

    void removeFromObjectStore(InternalFactHandle handle);
}
