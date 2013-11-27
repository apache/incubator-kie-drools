package org.drools.reteoo.common;

import org.drools.core.FactException;
import org.drools.core.FactHandle;
import org.drools.core.RuleBase;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.Rule;
import org.drools.core.spi.Activation;
import org.kie.api.runtime.ObjectFilter;

import java.util.Collection;

public class ReteWorkingMemoryEntryPoint implements WorkingMemoryEntryPoint, InternalWorkingMemoryEntryPoint {

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

    @Override
    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        return ((InternalWorkingMemoryEntryPoint)delegate).getObjectTypeConfigurationRegistry();
    }

    @Override
    public RuleBase getRuleBase() {
        return ((InternalWorkingMemoryEntryPoint)delegate).getRuleBase();
    }

    @Override
    public void delete(FactHandle factHandle, Rule rule, Activation activation) throws FactException {
        ((InternalWorkingMemoryEntryPoint)delegate).delete(factHandle, rule, activation);
    }

    @Override
    public void update(org.kie.api.runtime.rule.FactHandle handle, Object object, long mask, Class<?> modifiedClass, Activation activation) throws FactException {
        ((InternalWorkingMemoryEntryPoint)delegate).update(handle, object, mask, modifiedClass, activation);
    }

    @Override
    public EntryPointId getEntryPoint() {
        return ((InternalWorkingMemoryEntryPoint)delegate).getEntryPoint();
    }

    @Override
    public InternalWorkingMemory getInternalWorkingMemory() {
        return ((InternalWorkingMemoryEntryPoint)delegate).getInternalWorkingMemory();
    }

    @Override
    public FactHandle getFactHandleByIdentity(Object object) {
        return ((InternalWorkingMemoryEntryPoint)delegate).getFactHandleByIdentity(object);
    }

    @Override
    public void reset() {
        ((InternalWorkingMemoryEntryPoint)delegate).reset();
    }

    @Override
    public ObjectStore getObjectStore() {
        return ((InternalWorkingMemoryEntryPoint)delegate).getObjectStore();
    }

    @Override
    public EntryPointNode getEntryPointNode() {
        return ((InternalWorkingMemoryEntryPoint)delegate).getEntryPointNode();
    }
}
