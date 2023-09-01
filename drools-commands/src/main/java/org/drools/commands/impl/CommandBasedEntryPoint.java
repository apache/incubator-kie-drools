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
package org.drools.commands.impl;

import org.kie.api.runtime.ExecutableRunner;
import org.drools.commands.runtime.GetFactCountInEntryPointCommand;
import org.drools.commands.runtime.rule.DeleteFromEntryPointCommand;
import org.drools.commands.runtime.rule.GetFactHandleInEntryPointCommand;
import org.drools.commands.runtime.rule.GetFactHandlesInEntryPointCommand;
import org.drools.commands.runtime.rule.GetObjectInEntryPointCommand;
import org.drools.commands.runtime.rule.GetObjectsInEntryPointCommand;
import org.drools.commands.runtime.rule.InsertObjectInEntryPointCommand;
import org.drools.commands.runtime.rule.UpdateInEntryPointCommand;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Collection;

public class CommandBasedEntryPoint implements EntryPoint {

    private final ExecutableRunner runner;
    private final String entryPoint;

    public CommandBasedEntryPoint( ExecutableRunner runner, String entryPoint ) {
        this.runner = runner;
        this.entryPoint = entryPoint;
    }

    @Override
    public String getEntryPointId() {
        return entryPoint;
    }

    @Override
    public FactHandle insert(Object object) {
        return runner.execute( new InsertObjectInEntryPointCommand( object, entryPoint ) );
    }

    @Override
    public void retract(FactHandle handle) {
        delete(handle);
    }

    @Override
    public void delete(FactHandle handle) {
        runner.execute( new DeleteFromEntryPointCommand( handle, entryPoint ) );
    }

    @Override
    public void delete(FactHandle handle, FactHandle.State fhState) {
        runner.execute( new DeleteFromEntryPointCommand( handle, entryPoint, fhState ) );
    }

    @Override
    public void update(FactHandle handle, Object object) {
        runner.execute( new UpdateInEntryPointCommand( handle, object, entryPoint ) );
    }

    @Override
    public void update(FactHandle handle, Object object, String... modifiedProperties) {
        runner.execute( new UpdateInEntryPointCommand( handle, object, entryPoint, modifiedProperties ) );
    }

    @Override
    public FactHandle getFactHandle(Object object) {
        return runner.execute( new GetFactHandleInEntryPointCommand(object, entryPoint) );
    }

    @Override
    public Object getObject(FactHandle factHandle) {
        return runner.execute( new GetObjectInEntryPointCommand(factHandle, entryPoint) );
    }

    @Override
    public Collection<? extends Object> getObjects() {
        return runner.execute( new GetObjectsInEntryPointCommand(null, entryPoint) );
    }

    @Override
    public Collection<? extends Object> getObjects(ObjectFilter filter) {
        return runner.execute( new GetObjectsInEntryPointCommand(filter, entryPoint) );
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles() {
        return (Collection<T>) runner.execute( new GetFactHandlesInEntryPointCommand(entryPoint) );
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        return (Collection<T>) runner.execute( new GetFactHandlesInEntryPointCommand(entryPoint, filter) );
    }

    @Override
    public long getFactCount() {
        return runner.execute( new GetFactCountInEntryPointCommand(entryPoint) );
    }
}
