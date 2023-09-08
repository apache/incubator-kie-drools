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
package org.drools.kiesession.entrypoints;

import java.io.Serializable;
import java.util.Collection;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.ReteEvaluator;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.base.rule.EntryPointId;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;

public class DisconnectedWorkingMemoryEntryPoint implements WorkingMemoryEntryPoint, Serializable {
    
    private String id;
    
    public DisconnectedWorkingMemoryEntryPoint(String id) {
        this.id = id;
    }

    public String getEntryPointId() {
        return this.id;
    }

    @Override
    public EntryPointId getEntryPoint() {
        return new EntryPointId( id );
    }

    @Override
    public ReteEvaluator getReteEvaluator() {
        return null;
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

    @Override
    public FactHandle insert( Object object, boolean dynamic ) {
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
    public void dispose() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public InternalRuleBase getKnowledgeBase() {
        return null;
    }

    @Override
    public void delete( FactHandle factHandle, RuleImpl rule, TerminalNode terminalNode ) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public void delete( FactHandle factHandle, RuleImpl rule, TerminalNode terminalNode, FactHandle.State fhState ) {
        throw new UnsupportedOperationException( "This method is not supported for disconnected objects" );
    }

    @Override
    public void update( FactHandle handle, Object object, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch) {
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
}
