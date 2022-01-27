/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.util.bitmask.BitMask;
import org.drools.ruleunits.impl.facthandles.RuleUnitInternalFactHandle;
import org.drools.ruleunits.impl.factory.DataHandleImpl;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataStore;

public class ListDataStore<T> implements DataStore<T>, InternalStoreCallback {
    private final Map<Object, DataHandle> store = new IdentityHashMap<>();

    private final List<EntryPointDataProcessor> entryPointSubscribers = new ArrayList<>();
    private final List<DataProcessor<T>> subscribers = new ArrayList<>();

    public DataHandle add(T t) {
        DataHandle dh = createDataHandle(t);
        store.put(t, dh);
        entryPointSubscribers.forEach(s -> internalInsert(dh, s));
        subscribers.forEach(s -> internalInsert(dh, s));
        return dh;
    }

    protected DataHandle createDataHandle(T t) {
        return new DataHandleImpl(t);
    }

    public DataHandle findHandle(long id) {
        for (DataHandle dh : store.values()) {
            DataHandleImpl dhi = (DataHandleImpl) dh;
            if (dhi.getId() == id) {
                return dh;
            }
        }
        throw new IllegalArgumentException("Cannot find id");
    }

    @Override
    public void update(DataHandle handle, T object) {
        entryPointSubscribers.forEach(s -> s.update(handle, handle.getObject()));
        subscribers.forEach(s -> s.update(handle, object));
    }

    @Override
    public void remove(Object object) {
        remove(store.get(object));
    }

    @Override
    public void remove(DataHandle handle) {
        entryPointSubscribers.forEach(s -> s.delete(handle));
        subscribers.forEach(s -> s.delete(handle));
        store.remove(handle.getObject());
    }

    @Override
    public void subscribe(DataProcessor processor) {
        if (processor instanceof EntryPointDataProcessor) {
            EntryPointDataProcessor subscriber = (EntryPointDataProcessor) processor;
            entryPointSubscribers.add(subscriber);
        } else {
            subscribers.add(processor);
        }
        store.values().forEach(dh -> internalInsert(dh, processor));
    }

    @Override
    public void update(RuleUnitInternalFactHandle fh, Object obj, BitMask mask, Class<?> modifiedClass, Activation activation) {
        DataHandle dh = ((RuleUnitInternalFactHandle) fh).getDataHandle();
        entryPointSubscribers.forEach(s -> s.update(dh, obj, mask, modifiedClass, activation));
        subscribers.forEach(s -> s.update(dh, (T) obj));
    }

    @Override
    public void delete(RuleUnitInternalFactHandle fh, RuleImpl rule, TerminalNode terminalNode, FactHandle.State fhState) {
        DataHandle dh = ((RuleUnitInternalFactHandle) fh).getDataHandle();
        entryPointSubscribers.forEach(s -> s.delete(dh, rule, terminalNode, fhState));
        subscribers.forEach(s -> s.delete(dh));
        store.remove(fh.getObject());
    }

    private void internalInsert(DataHandle dh, DataProcessor s) {
        FactHandle fh = s.insert(dh, dh.getObject());
        if (fh != null) {
            ((RuleUnitInternalFactHandle) fh).setDataStore(this);
            ((RuleUnitInternalFactHandle) fh).setDataHandle(dh);
        }
    }
}
