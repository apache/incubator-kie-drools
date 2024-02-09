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

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.util.bitmask.BitMask;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.impl.InternalStoreCallback;
import org.drools.ruleunits.impl.facthandles.RuleUnitInternalFactHandle;
import org.drools.ruleunits.impl.factory.DataHandleImpl;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleContext;

public class ListDataStore<T> extends AbstractDataSource<T> implements  Iterable<T>, DataStore<T>, InternalStoreCallback {

    private final Map<T, DataHandle> store = new IdentityHashMap<>();

    protected ListDataStore() {

    }

    @Override
    public Iterator<T> iterator() {
        return store.keySet().iterator();
    }

    public DataHandle add(T t) {
        DataHandle dh = createDataHandle(t);
        store.put(t, dh);
        forEachSubscriber(s -> internalInsert(dh, s));
        return dh;
    }

    @Override
    public void addLogical(RuleContext ruleContext, Object object) {
        entryPointSubscribers.forEach(eps -> eps.insertLogical(ruleContext, object));
    }

    // used by kogito-runtimes, check if it can be removed
    public DataHandle findHandle(long id) {
        for (DataHandle dh : store.values()) {
            DataHandleImpl dhi = (DataHandleImpl) dh;
            if (dhi.getId() == id) {
                return dh;
            }
        }
        throw new IllegalArgumentException("Cannot find id");
    }

    protected DataHandle createDataHandle(T t) {
        return new DataHandleImpl(t);
    }

    @Override
    public DataHandle lookup(Object object) {
        return store.get(object);
    }

    @Override
    public void remove(T object) {
        remove(lookup(object));
    }

    @Override
    public void remove(DataHandle handle) {
        forEachSubscriber(s -> s.delete(handle));
        store.remove(handle.getObject());
    }

    @Override
    public void subscribe(DataProcessor processor) {
        super.subscribe(processor);
        store.values().forEach(dh -> internalInsert(dh, processor));
    }

    @Override
    public void update(DataHandle handle, T object) {
        switchObjectOnDataHandle(handle, object);
        forEachSubscriber(s -> s.update(handle, object));
    }

    @Override
    public void update(RuleUnitInternalFactHandle fh, Object obj, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch) {
        update(fh.getDataHandle(), obj, mask, modifiedClass, internalMatch);
    }

    @Override
    public void update(DataHandle dh, Object obj, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch) {
        switchObjectOnDataHandle(dh, (T) obj);
        entryPointSubscribers.forEach(s -> s.update(dh, obj, mask, modifiedClass, internalMatch));
        subscribers.forEach(s -> s.update(dh, obj));
    }

    private void switchObjectOnDataHandle(DataHandle handle, T object) {
        if (handle.getObject() != object) {
            store.remove(handle.getObject());
            ((DataHandleImpl) handle).setObject(object);
            store.put(object, handle);
        }
    }

    @Override
    public void delete(RuleUnitInternalFactHandle fh, RuleImpl rule, TerminalNode terminalNode, FactHandle.State fhState) {
        DataHandle dh = fh.getDataHandle();
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
