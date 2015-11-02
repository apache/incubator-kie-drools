/*
 * Copyright 2015 JBoss Inc
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
 *
 */

package org.drools.core.command.impl;

import org.kie.api.command.Command;
import org.kie.api.command.Setter;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FluentCommandFactoryServiceImpl {
    private final CommandFactoryServiceImpl factory = new CommandFactoryServiceImpl();
    private final List<Command> commands = new ArrayList<Command>();

    public FluentCommandFactoryServiceImpl newGetGlobal(String identifier) {
        commands.add(factory.newGetGlobal(identifier));
        return this;
    }

    public FluentCommandFactoryServiceImpl newGetGlobal(String identifier, String outIdentifier) {
        commands.add(factory.newGetGlobal(identifier, outIdentifier));
        return this;
    }

    public FluentCommandFactoryServiceImpl newInsertElements(Iterable objects) {
        commands.add(factory.newInsertElements( objects ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newInsertElements(Iterable objects, String outIdentifier) {
        commands.add( factory.newInsertElements( objects, outIdentifier ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newInsertElements(Iterable objects, String outIdentifier, boolean returnObject, String entryPoint) {
        commands.add( factory.newInsertElements( objects, outIdentifier, returnObject, entryPoint ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newInsert(Object object) {
        commands.add( factory.newInsert( object ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newInsert(Object object, String outIdentifier) {
        commands.add( factory.newInsert( object, outIdentifier ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newInsert(Object object, String outIdentifier, boolean returnObject, String entryPoint) {
        commands.add( factory.newInsert( object, outIdentifier, returnObject, entryPoint ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newDelete(FactHandle factHandle) {
        commands.add( factory.newDelete( factHandle ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newDeleteObject(Object object,String entryPoint) {
        commands.add( factory.newDeleteObject( object, entryPoint ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newModify(FactHandle factHandle, List<Setter> setters) {
        commands.add( factory.newModify( factHandle, setters ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newGetObject(FactHandle factHandle) {
        commands.add( factory.newGetObject( factHandle ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newGetObject(FactHandle factHandle, String outIdentifier) {
        commands.add( factory.newGetObject( factHandle, outIdentifier ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newGetObjects() {
        commands.add( factory.newGetObjects() );
        return this;
    }

    public FluentCommandFactoryServiceImpl newGetObjects(String outIdentifier) {
        commands.add( factory.newGetObjects( outIdentifier ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newGetObjects(ObjectFilter filter) {
        commands.add( factory.newGetObjects( filter ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newGetObjects(ObjectFilter filter, String outIdentifier) {
        commands.add( factory.newGetObjects( filter, outIdentifier ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newSetGlobal(String identifier, Object object) {
        commands.add( factory.newSetGlobal( identifier, object ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newSetGlobal(String identifier, Object object, boolean out) {
        commands.add( factory.newSetGlobal( identifier, object, out ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newSetGlobal(String identifier, Object object, String outIdentifier) {
        commands.add( factory.newSetGlobal( identifier, object, outIdentifier ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newFireAllRules() {
        commands.add( factory.newFireAllRules() );
        return this;
    }

    public FluentCommandFactoryServiceImpl newFireAllRules(int max) {
        commands.add( factory.newFireAllRules( max ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newFireAllRules(String outidentifier) {
        commands.add( factory.newFireAllRules( outidentifier ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newGetFactHandle( Object object ) {
        commands.add( factory.newGetFactHandle( object ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newGetFactHandleInEntryPoint( Object object, String entryPoint ) {
        commands.add( factory.newGetFactHandleInEntryPoint( object, entryPoint ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newStartProcess(String processId) {
        commands.add( factory.newStartProcess( processId ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newStartProcess(String processId, Map<String, Object> parameters) {
        commands.add( factory.newStartProcess( processId, parameters ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newSignalEvent(String type, Object event) {
        commands.add( factory.newSignalEvent( type, event ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newSignalEvent(long processInstanceId, String type, Object event) {
        commands.add( factory.newSignalEvent( processInstanceId, type, event ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newCompleteWorkItem(long workItemId, Map<String, Object> results) {
        commands.add( factory.newCompleteWorkItem( workItemId, results ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newAbortWorkItem(long workItemId) {
        commands.add( factory.newAbortWorkItem( workItemId ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newRegisterWorkItemHandlerCommand(WorkItemHandler handler, String workItemName) {
        commands.add( factory.newRegisterWorkItemHandlerCommand( handler, workItemName ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newQuery(String identifier, String name) {
        commands.add( factory.newQuery( identifier, name ) );
        return this;
    }

    public FluentCommandFactoryServiceImpl newQuery(String identifier, String name, Object[] arguments) {
        commands.add( factory.newQuery( identifier, name, arguments ) );
        return this;
    }

    public List<Command> end() {
        return commands;
    }
}
