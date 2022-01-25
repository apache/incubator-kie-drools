/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.impl.facthandles;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.EventFactHandle;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.rule.EntryPointId;
import org.drools.ruleunits.impl.InternalStoreCallback;
import org.drools.ruleunits.api.DataHandle;

public class RuleUnitEventFactHandle extends EventFactHandle implements RuleUnitInternalFactHandle {
    public RuleUnitEventFactHandle() {
    }

    public RuleUnitEventFactHandle(long id, Object object, long recency, long timestamp, long duration, WorkingMemoryEntryPoint wmEntryPoint) {
        super(id, object, recency, timestamp, duration, wmEntryPoint);
    }

    public RuleUnitEventFactHandle(long id, Object object, long recency, long timestamp, long duration, WorkingMemoryEntryPoint wmEntryPoint, boolean isTraitOrTraitable) {
        super(id, object, recency, timestamp, duration, wmEntryPoint, isTraitOrTraitable);
    }

    public RuleUnitEventFactHandle(long id, int identityHashCode, Object object, long recency, long timestamp, long duration, EntryPointId entryPointId, TraitTypeEnum traitType) {
        super(id, identityHashCode, object, recency, timestamp, duration, entryPointId, traitType);
    }

    private DataHandle dataHandle;
    private InternalStoreCallback dataStore;

    @Override
    public DataHandle getDataHandle() {
        return dataHandle;
    }

    @Override
    public void setDataHandle(DataHandle dataHandle) {
        this.dataHandle = dataHandle;
    }

    @Override
    public InternalStoreCallback getDataStore() {
        return dataStore;
    }

    @Override
    public void setDataStore(InternalStoreCallback dataStore) {
        this.dataStore = dataStore;
    }
}
