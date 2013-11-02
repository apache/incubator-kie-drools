package org.drools.reteoo.common;

import org.drools.core.FactException;
import org.drools.core.FactHandle;
import org.drools.core.WorkingMemoryEntryPoint;
import org.kie.api.runtime.ObjectFilter;

import java.util.Collection;

public class ReteWorkingMemoryEntryPoint implements WorkingMemoryEntryPoint {

    private ReteWorkingMemory reteWm;
    private WorkingMemoryEntryPoint delegate;

    public ReteWorkingMemoryEntryPoint(ReteWorkingMemory reteWm, WorkingMemoryEntryPoint delegate) {
        this.reteWm = reteWm;
        this.delegate = delegate;
    }

    @Override
    public String getEntryPointId() {
        return delegate.getEntryPointId();
    }

    @Override
    public FactHandle insert(Object object) throws FactException {
        reteWm.initInitialFact();
        return delegate.insert(object);
    }

    @Override
    public FactHandle insert(Object object, boolean dynamic) throws FactException {
        reteWm.initInitialFact();
        return delegate.insert(object, dynamic);
    }

    @Override
    public void retract(org.kie.api.runtime.rule.FactHandle handle) throws FactException {
        delegate.retract( handle );
    }

    @Override
    public void delete(org.kie.api.runtime.rule.FactHandle handle) {
        delegate.delete(handle);
    }

    @Override
    public void update(org.kie.api.runtime.rule.FactHandle handle, Object object) throws FactException {
        delegate.update(handle, object);
    }

    @Override
    public org.kie.api.runtime.rule.FactHandle getFactHandle(Object object) {
        return delegate.getFactHandle( object );
    }

    @Override
    public Object getObject(org.kie.api.runtime.rule.FactHandle factHandle) {
        return delegate.getObject(factHandle);
    }

    @Override
    public Collection<? extends Object> getObjects() {
        return delegate.getObjects();
    }

    @Override
    public Collection<? extends Object> getObjects(ObjectFilter filter) {
        return delegate.getObjects(filter);
    }

    @Override
    public <T extends org.kie.api.runtime.rule.FactHandle> Collection<T> getFactHandles() {
        return delegate.getFactHandles();
    }

    @Override
    public <T extends org.kie.api.runtime.rule.FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        return delegate.getFactHandles(filter);
    }

    @Override
    public long getFactCount() {
        return delegate.getFactCount();
    }

    @Override
    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        return delegate.getWorkingMemoryEntryPoint(name);
    }

    @Override
    public void dispose() {
         delegate.dispose();
    }
}
