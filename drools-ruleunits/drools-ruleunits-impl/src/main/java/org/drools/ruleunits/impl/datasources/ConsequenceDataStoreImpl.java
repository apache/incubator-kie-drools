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
package org.drools.ruleunits.impl.datasources;

import org.drools.core.impl.InternalRuleBase;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.util.bitmask.AllSetBitMask;
import org.drools.util.bitmask.BitMask;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.impl.InternalStoreCallback;
import org.kie.api.runtime.rule.RuleContext;

import static org.drools.kiesession.entrypoints.NamedEntryPoint.calculateUpdateBitMask;

public class ConsequenceDataStoreImpl<T> implements ConsequenceDataStore<T> {

    private final RuleContext ruleContext;

    private final DataStore<T> dataStore;

    public ConsequenceDataStoreImpl(RuleContext ruleContext, DataStore<T> dataStore) {
        this.ruleContext = ruleContext;
        this.dataStore = dataStore;
    }

    @Override
    public void add(T object) {
        dataStore.add(object);
    }

    @Override
    public void addLogical(T object) {
        ((InternalStoreCallback)dataStore).addLogical(ruleContext, object);
    }

    @Override
    public void update(T object, String... modifiedProperties) {
        BitMask bitMask = modifiedProperties.length == 0 ? AllSetBitMask.get() : calculateUpdateBitMask((InternalRuleBase) ruleContext.getKieBase(), object, modifiedProperties);
        update(object, bitMask);
    }

    public void update(T object, BitMask bitMask) {
        ((InternalStoreCallback)dataStore).update(((InternalStoreCallback)dataStore).lookup(object), object, bitMask, object.getClass(), (InternalMatch) ruleContext.getMatch());
    }

    @Override
    public void remove(T object) {
        dataStore.remove(object);
    }
}
