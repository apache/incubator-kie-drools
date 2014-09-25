package org.drools.reteoo.common;

import org.drools.core.util.bitmask.BitMask;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.spi.FactHandleFactory;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.rule.EntryPointId;
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
    public FactHandle insert(Object object) {
        reteWm.initInitialFact();
        return delegate.insert(object);
    }

    @Override
    public FactHandle insert(Object object, boolean dynamic) {
        reteWm.initInitialFact();
        return delegate.insert(object, dynamic);
    }

    @Override
    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return ((InternalWorkingMemoryEntryPoint)delegate).getTruthMaintenanceSystem();
    }

    @Override
    public FactHandleFactory getHandleFactory() {
        return ((InternalWorkingMemoryEntryPoint)delegate).getHandleFactory();
    }

    @Override
    public void retract(FactHandle handle) {
        delegate.retract(handle);
    }

    @Override
    public void delete(FactHandle handle) {
        delegate.delete(handle);
    }

    @Override
    public void update(FactHandle handle, Object object) {
        delegate.update(handle, object);
    }

    @Override
    public FactHandle getFactHandle(Object object) {
        return delegate.getFactHandle(object);
    }

    @Override
    public Object getObject(FactHandle factHandle) {
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
    public InternalKnowledgeBase getKnowledgeBase() {
        return ((InternalWorkingMemoryEntryPoint)delegate).getKnowledgeBase();

    }

    @Override
    public void delete(FactHandle factHandle, RuleImpl rule, Activation activation) {
        ((InternalWorkingMemoryEntryPoint)delegate).delete(factHandle, rule, activation);
    }

    @Override
    public void update(FactHandle handle, Object object, BitMask mask, Class<?> modifiedClass, Activation activation) {
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
