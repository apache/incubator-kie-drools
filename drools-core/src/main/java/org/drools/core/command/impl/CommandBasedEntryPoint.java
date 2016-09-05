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

package org.drools.core.command.impl;

import org.drools.core.command.CommandService;
import org.drools.core.command.runtime.GetFactCountInEntryPointCommand;
import org.drools.core.command.runtime.rule.DeleteFromEntryPointCommand;
import org.drools.core.command.runtime.rule.GetFactHandleInEntryPointCommand;
import org.drools.core.command.runtime.rule.GetFactHandlesInEntryPointCommand;
import org.drools.core.command.runtime.rule.GetObjectInEntryPointCommand;
import org.drools.core.command.runtime.rule.GetObjectsInEntryPointCommand;
import org.drools.core.command.runtime.rule.InsertObjectInEntryPointCommand;
import org.drools.core.command.runtime.rule.UpdateInEntryPointCommand;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Collection;

public class CommandBasedEntryPoint implements EntryPoint {

    private final CommandService commandService;
    private final String entryPoint;

    public CommandBasedEntryPoint(CommandService commandService, String entryPoint) {
        this.commandService = commandService;
        this.entryPoint = entryPoint;
    }

    @Override
    public String getEntryPointId() {
        return entryPoint;
    }

    @Override
    public FactHandle insert(Object object) {
        return commandService.execute( new InsertObjectInEntryPointCommand( object, entryPoint ) );
    }

    @Override
    public void retract(FactHandle handle) {
        delete(handle);
    }

    @Override
    public void delete(FactHandle handle) {
        commandService.execute( new DeleteFromEntryPointCommand( handle, entryPoint ) );
    }

    @Override
    public void delete(FactHandle handle, FactHandle.State fhState) {
        commandService.execute( new DeleteFromEntryPointCommand( handle, entryPoint, fhState ) );
    }

    @Override
    public void update(FactHandle handle, Object object) {
        commandService.execute( new UpdateInEntryPointCommand( handle, object, entryPoint ) );
    }

    @Override
    public void update(FactHandle handle, Object object, String... modifiedProperties) {
        commandService.execute( new UpdateInEntryPointCommand( handle, object, entryPoint, modifiedProperties ) );
    }

    @Override
    public FactHandle getFactHandle(Object object) {
        return commandService.execute( new GetFactHandleInEntryPointCommand(object, entryPoint) );
    }

    @Override
    public Object getObject(FactHandle factHandle) {
        return commandService.execute( new GetObjectInEntryPointCommand(factHandle, entryPoint) );
    }

    @Override
    public Collection<? extends Object> getObjects() {
        return commandService.execute( new GetObjectsInEntryPointCommand(null, entryPoint) );
    }

    @Override
    public Collection<? extends Object> getObjects(ObjectFilter filter) {
        return commandService.execute( new GetObjectsInEntryPointCommand(filter, entryPoint) );
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles() {
        return (Collection<T>) commandService.execute( new GetFactHandlesInEntryPointCommand(entryPoint) );
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        return (Collection<T>) commandService.execute( new GetFactHandlesInEntryPointCommand(entryPoint, filter) );
    }

    @Override
    public long getFactCount() {
        return commandService.execute( new GetFactCountInEntryPointCommand(entryPoint) );
    }
}
