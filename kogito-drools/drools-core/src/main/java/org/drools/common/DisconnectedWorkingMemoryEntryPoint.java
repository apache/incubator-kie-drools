package org.drools.common;

import java.io.Serializable;
import java.util.Collection;

import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class DisconnectedWorkingMemoryEntryPoint implements WorkingMemoryEntryPoint, Serializable {
    
    private String id;
    
    public DisconnectedWorkingMemoryEntryPoint(String id) {
        this.id = id;
    }

    public String getEntryPointId() {
        return this.id;
    }

    public long getFactCount() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public FactHandle getFactHandle(Object object) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public <T extends FactHandle> Collection<T> getFactHandles() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public Object getObject(FactHandle factHandle) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public Collection<Object> getObjects() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public Collection<Object> getObjects(ObjectFilter filter) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public FactHandle insert(Object object) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public void retract(FactHandle handle) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public void update(FactHandle handle,
                       Object object) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

}
