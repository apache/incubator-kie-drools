/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.kie.api.runtime.rule.EntryPoint;

import java.util.Collection;

import static org.drools.impl.adapters.FactHandleAdapter.adaptFactHandles;

public class WorkingMemoryEntryPointAdapter implements WorkingMemoryEntryPoint {

    private final EntryPoint delegate;

    public WorkingMemoryEntryPointAdapter(EntryPoint delegate) {
        this.delegate = delegate;
    }

    public String getEntryPointId() {
        return delegate.getEntryPointId();
    }

    public FactHandle insert(Object object) {
        return new FactHandleAdapter(delegate.insert(object));
    }

    public void retract(FactHandle handle) {
        delegate.retract(toKieFH(handle));
    }

    private org.kie.api.runtime.rule.FactHandle toKieFH(FactHandle handle) {
        if (handle instanceof org.kie.api.runtime.rule.FactHandle) {
            return (org.kie.api.runtime.rule.FactHandle)handle;
        }
        if (handle instanceof FactHandleAdapter) {
            return ((FactHandleAdapter)handle).getDelegate();
        }
        throw new RuntimeException("Cannot adapt " + handle + " of class " + handle.getClass().getName() + " to Drools 5 api");
    }

    public void update(FactHandle handle, Object object) {
        delegate.update(toKieFH(handle), object);
    }

    public FactHandle getFactHandle(Object object) {
        org.kie.api.runtime.rule.FactHandle factHandle = delegate.getFactHandle(object);
        return factHandle != null ? new FactHandleAdapter(factHandle) : null;
    }

    public Object getObject(FactHandle factHandle) {
        return delegate.getObject(toKieFH(factHandle));
    }

    public Collection<Object> getObjects() {
        return (Collection<Object>)delegate.getObjects();
    }

    public Collection<Object> getObjects(final ObjectFilter filter) {
        return (Collection<Object>)delegate.getObjects(new org.kie.api.runtime.ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return filter.accept(object);
            }
        });
    }

    public <T extends FactHandle> Collection<T> getFactHandles() {
        return (Collection<T>)adaptFactHandles(delegate.getFactHandles());
    }

    public <T extends FactHandle> Collection<T> getFactHandles(final ObjectFilter filter) {
        return (Collection<T>)adaptFactHandles(delegate.getFactHandles(new org.kie.api.runtime.ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return filter.accept(object);
            }
        }));
    }

    public long getFactCount() {
        return delegate.getFactCount();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkingMemoryEntryPointAdapter && delegate.equals(((WorkingMemoryEntryPointAdapter)obj).delegate);
    }
}
