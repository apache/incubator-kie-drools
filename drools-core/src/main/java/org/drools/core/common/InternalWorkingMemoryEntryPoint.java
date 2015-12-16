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

import org.drools.core.spi.FactHandleFactory;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

public interface InternalWorkingMemoryEntryPoint extends EntryPoint {
    ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();
    InternalKnowledgeBase getKnowledgeBase();
    public void delete(final FactHandle factHandle,
                        final RuleImpl rule,
                        final Activation activation);
    public void update(FactHandle handle,
                       Object object,
                       BitMask mask,
                       Class<?> modifiedClass,
                       Activation activation);

    public TruthMaintenanceSystem getTruthMaintenanceSystem();

    public EntryPointId getEntryPoint();
    public InternalWorkingMemory getInternalWorkingMemory();

    public FactHandle getFactHandleByIdentity(final Object object);
    
    void reset();
    
    ObjectStore getObjectStore();

    FactHandleFactory getHandleFactory();
    
    EntryPointNode getEntryPointNode();
}
