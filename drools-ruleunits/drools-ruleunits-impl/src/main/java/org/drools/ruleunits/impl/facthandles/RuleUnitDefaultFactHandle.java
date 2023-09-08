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
package org.drools.ruleunits.impl.facthandles;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultFactHandle;
import org.drools.base.rule.EntryPointId;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.impl.InternalStoreCallback;

public class RuleUnitDefaultFactHandle extends DefaultFactHandle implements RuleUnitInternalFactHandle {

    public RuleUnitDefaultFactHandle() {
    }

    public RuleUnitDefaultFactHandle(long id, Object object) {
        super(id, object);
    }

    public RuleUnitDefaultFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint wmEntryPoint) {
        super(id, object, recency, wmEntryPoint);
    }

    public RuleUnitDefaultFactHandle(long id, int identityHashCode, Object object, long recency, WorkingMemoryEntryPoint wmEntryPoint) {
        super(id, identityHashCode, object, recency, wmEntryPoint);
    }

    public RuleUnitDefaultFactHandle(long id, int identityHashCode, Object object, long recency, EntryPointId entryPointId) {
        super(id, identityHashCode, object, recency, entryPointId);
    }

    public RuleUnitDefaultFactHandle(long id, String wmEntryPointId, int identityHashCode, int objectHashCode, long recency, Object object) {
        super(id, wmEntryPointId, identityHashCode, objectHashCode, recency, object);
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
