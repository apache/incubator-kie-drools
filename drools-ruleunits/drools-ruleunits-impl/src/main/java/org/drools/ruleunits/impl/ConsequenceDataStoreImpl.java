/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.DataStore;
import org.kie.api.runtime.rule.RuleContext;

public class ConsequenceDataStoreImpl<T> implements ConsequenceDataStore<T> {

    private final RuleContext ruleContext;

    private final DataStore<T> dataStore;

    public ConsequenceDataStoreImpl(RuleContext ruleContext, DataStore<T> dataStore) {
        this.ruleContext = ruleContext;
        this.dataStore = dataStore;
    }

    @Override
    public void add(T t) {
        dataStore.add(t);
    }

    @Override
    public void addLogical(T t) {
        ((InternalStoreCallback)dataStore).addLogical(ruleContext, t);
    }

    @Override
    public void update(T t) {
        dataStore.update(((InternalStoreCallback)dataStore).lookup(t), t);
    }

    @Override
    public void remove(T t) {
        dataStore.remove(t);
    }
}
