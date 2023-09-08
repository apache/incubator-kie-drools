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

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.util.bitmask.BitMask;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.SingletonStore;
import org.drools.ruleunits.impl.InternalStoreCallback;
import org.drools.ruleunits.impl.facthandles.RuleUnitInternalFactHandle;
import org.drools.ruleunits.impl.factory.DataHandleImpl;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleContext;

public class FieldDataStore<T> extends AbstractDataSource<T> implements SingletonStore<T>, InternalStoreCallback {

    private DataHandle handle = null;

    protected FieldDataStore() {

    }

    public DataHandle set(T t) {
        if (handle == null && t != null) {
            insert(t);
        } else {
            clear();
            if (t != null) {
                insert(t);
            }
        }

        return handle;
    }

    @Override
    public DataHandle lookup(Object object) {
        return handle != null && handle.getObject() == object ? handle : null;
    }

    @Override
    public void addLogical(RuleContext ruleContext, Object object) {
        entryPointSubscribers.forEach(eps -> eps.insertLogical(ruleContext, object));
    }

    private void insert(T t) {
        handle = createDataHandle(t);
        forEachSubscriber(s -> internalInsert(handle, s));
    }

    protected DataHandle createDataHandle(T t) {
        return new DataHandleImpl(t);
    }

    public void update() {
        if (handle == null) {
            return;
        }
        DataHandle dh = handle;
        forEachSubscriber(s -> s.update(dh, dh.getObject()));
    }

    @Override
    public void clear() {
        if (handle == null) {
            return;
        }
        DataHandle dh = handle;
        handle = null;
        forEachSubscriber(s -> s.delete(dh));
    }

    @Override
    public void subscribe(DataProcessor processor) {
        super.subscribe(processor);
        if (handle != null) {
            internalInsert(handle, processor);
        }
    }

    @Override
    public void update(RuleUnitInternalFactHandle fh, Object obj, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch) {
        update(fh.getDataHandle(), obj, mask, modifiedClass, internalMatch);
    }

    @Override
    public void update(DataHandle dh, Object obj, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch) {
        entryPointSubscribers.forEach(s -> s.update(dh, obj, mask, modifiedClass, internalMatch));
        subscribers.forEach(s -> s.update(dh, obj));
    }

    @Override
    public void delete(RuleUnitInternalFactHandle fh, RuleImpl rule, TerminalNode terminalNode, FactHandle.State fhState) {
        DataHandle dh = fh.getDataHandle();
        if (dh != this.handle) {
            throw new IllegalArgumentException("The given handle is not contained in this DataStore");
        }
        entryPointSubscribers.forEach(s -> s.delete(dh, rule, terminalNode, fhState));
        subscribers.forEach(s -> s.delete(dh));
        handle = null;
    }

    private void internalInsert(DataHandle dh, DataProcessor processor) {
        FactHandle fh = processor.insert(dh, dh == null ? null : dh.getObject());
        if (fh != null) {
            ((RuleUnitInternalFactHandle) fh).setDataStore(this);
            ((RuleUnitInternalFactHandle) fh).setDataHandle(dh);
        }
    }
}
