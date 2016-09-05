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

package org.drools.core.common;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;

import java.io.Serializable;
import java.util.Collection;

public class DisconnectedWorkingMemoryEntryPoint implements InternalWorkingMemoryEntryPoint, Serializable {
    
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

    public Collection<? extends Object> getObjects() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public Collection<? extends Object> getObjects(ObjectFilter filter) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public FactHandle insert(Object object) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public void retract(FactHandle handle) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public void delete(FactHandle handle) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public void delete(FactHandle handle, FactHandle.State fhState) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public void update(FactHandle handle,
                       Object object) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    public void update(FactHandle handle,
                       Object object,
                       String... modifiedProperties) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public InternalKnowledgeBase getKnowledgeBase() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public void delete( FactHandle factHandle, RuleImpl rule, Activation activation ) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public void delete( FactHandle factHandle, RuleImpl rule, Activation activation, FactHandle.State fhState ) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public void update( FactHandle handle, Object object, BitMask mask, Class<?> modifiedClass, Activation activation ) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public EntryPointId getEntryPoint() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public InternalWorkingMemory getInternalWorkingMemory() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public FactHandle getFactHandleByIdentity( Object object ) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public ObjectStore getObjectStore() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public FactHandleFactory getHandleFactory() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public EntryPointNode getEntryPointNode() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public void removeFromObjectStore( InternalFactHandle handle ) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }
}
