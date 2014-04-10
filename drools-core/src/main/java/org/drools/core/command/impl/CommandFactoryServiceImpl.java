/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.drools.core.command.NewKnowledgeBuilderConfigurationCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.drools.core.command.runtime.KBuilderSetPropertyCommand;
import org.drools.core.command.runtime.SetGlobalCommand;
import org.drools.core.command.runtime.process.AbortWorkItemCommand;
import org.drools.core.command.runtime.process.CompleteWorkItemCommand;
import org.drools.core.command.runtime.process.RegisterWorkItemHandlerCommand;
import org.drools.core.command.runtime.process.SignalEventCommand;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.command.runtime.rule.*;
import org.drools.core.command.runtime.rule.ModifyCommand.SetterImpl;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.command.Setter;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CommandFactoryServiceImpl implements KieCommands {

    public Command newGetGlobal(String identifier) {
        return new GetGlobalCommand(identifier);
    }

    public Command newGetGlobal(String identifier, String outIdentifier) {
        GetGlobalCommand cmd = new GetGlobalCommand(identifier);
        cmd.setOutIdentifier(outIdentifier);
        return cmd;
    }

    public Command newInsertElements(Iterable objects) {
        return new InsertElementsCommand( i2c(objects) );
    }

    public Command newInsertElements(Iterable objects, String outIdentifier) {
        InsertElementsCommand cmd = new InsertElementsCommand( i2c(objects) );
        cmd.setOutIdentifier(outIdentifier);
        return cmd;
    }

    public Command newInsertElements(Iterable objects, String outIdentifier, boolean returnObject, String entryPoint) {
        InsertElementsCommand cmd = new InsertElementsCommand( i2c(objects) );
        cmd.setEntryPoint( entryPoint );
        cmd.setOutIdentifier( outIdentifier );
        cmd.setReturnObject( returnObject );
        return cmd;
    }

    private Collection i2c(Iterable i) {
        if (i instanceof Collection) {
            return (Collection) i;
        }
        Collection c = new ArrayList();
        for (Object o : i) {
            c.add(o);
        }
        return c;
    }

    public Command newInsert(Object object) {
        return new InsertObjectCommand(object);
    }

    public Command newInsert(Object object, String outIdentifier) {
        InsertObjectCommand cmd = new InsertObjectCommand(object);
        cmd.setOutIdentifier( outIdentifier );
        return cmd;
    }

    public Command newInsert(Object object, String outIdentifier, boolean returnObject, String entryPoint) {
        InsertObjectCommand cmd = new InsertObjectCommand(object);
        cmd.setOutIdentifier(outIdentifier);
        cmd.setEntryPoint( entryPoint );
        cmd.setReturnObject( returnObject );
        return cmd;
    }

    public Command newDelete(FactHandle factHandle) {
        return new DeleteCommand( factHandle );
    }
    
    public Setter newSetter(String accessor,
                             String value) {
        return new SetterImpl(accessor, value);
    }
    
    public Command newModify(FactHandle factHandle,
                             List<Setter> setters) {
        return new ModifyCommand(factHandle, setters);
    }

    public Command newGetObject(FactHandle factHandle) {
        return new GetObjectCommand(factHandle);
    }

    public Command newGetObject(FactHandle factHandle, String outIdentifier) {
        return new GetObjectCommand(factHandle, outIdentifier);
    }

    public Command newGetObjects() {
        return newGetObjects((ObjectFilter) null);
    }

    public Command newGetObjects(String outIdentifier) {
        return newGetObjects(null, outIdentifier);
    }

    public Command newGetObjects(ObjectFilter filter) {
        return new GetObjectsCommand(filter);
    }

    public Command newGetObjects(ObjectFilter filter, String outIdentifier) {
        return new GetObjectsCommand(filter, outIdentifier);
    }

    public Command newSetGlobal(String identifier, Object object) {
        return new SetGlobalCommand(identifier, object);
    }

    public Command newSetGlobal(String identifier, Object object, boolean out) {
        if (out) {
            return newSetGlobal(identifier, object, identifier);
        } else {
            return newSetGlobal(identifier, object);
        }
    }

    public Command newSetGlobal(String identifier, Object object,
            String outIdentifier) {
        SetGlobalCommand cmd = new SetGlobalCommand(identifier, object);
        cmd.setOutIdentifier(outIdentifier);
        return cmd;
    }

    public Command newFireAllRules() {
        return new FireAllRulesCommand();
    }

    public Command newFireAllRules(int max) {
        return new FireAllRulesCommand(max);
    }

        public Command newFireAllRules(String outidentifier) {
        return new FireAllRulesCommand(outidentifier);
    }

    public Command newStartProcess(String processId) {
        StartProcessCommand startProcess = new StartProcessCommand();
        startProcess.setProcessId(processId);
        return startProcess;
    }

    public Command newStartProcess(String processId,
            Map<String, Object> parameters) {
        StartProcessCommand startProcess = new StartProcessCommand();
        startProcess.setProcessId(processId);
        startProcess.setParameters(parameters);
        return startProcess;
    }

    public Command newSignalEvent(String type,
                               Object event) {
        return new SignalEventCommand( type, event );
    }
    
    public Command newSignalEvent(long processInstanceId,
                               String type,
                               Object event) {
        return new SignalEventCommand( processInstanceId, type, event );
    }
    
    public Command newCompleteWorkItem(long workItemId,
                                       Map<String, Object> results) {
        return new CompleteWorkItemCommand(workItemId, results);
    }
    
    public Command newAbortWorkItem(long workItemId) {
        return new AbortWorkItemCommand( workItemId);
    }

    public Command newRegisterWorkItemHandlerCommand(WorkItemHandler handler,
                                                     String workItemName) {
        return new RegisterWorkItemHandlerCommand( workItemName, handler );
    }    
    
    public Command newQuery(String identifier, String name) {
        return new QueryCommand(identifier, name, null);
    }

    public Command newQuery(String identifier, String name, Object[] arguments) {
        return new QueryCommand(identifier, name, arguments);
    }

    public BatchExecutionCommand newBatchExecution(List<? extends Command> commands) {
        return newBatchExecution( commands, null );
    }

    public BatchExecutionCommand newBatchExecution(List<? extends Command> commands, String lookup) {
        return new BatchExecutionCommandImpl((List<GenericCommand<?>>) commands, lookup);
    }

    @Deprecated
    public Command newKBuilderSetPropertyCommand(String id, String name, String value) {
        return new KBuilderSetPropertyCommand(id, name, value);
    }
    
    public Command newKnowledgeBuilderSetPropertyCommand(String id, String name, String value) {
        return new KBuilderSetPropertyCommand(id, name, value);
    }    
    
    public Command newNewKnowledgeBuilderConfigurationCommand(String localId){
        return new NewKnowledgeBuilderConfigurationCommand(localId);
    }
    
    public Command<FactHandle> fromExternalFactHandleCommand(String factHandleExternalForm) {
        return fromExternalFactHandleCommand(factHandleExternalForm, false);
    }
    
    public Command<FactHandle> fromExternalFactHandleCommand(String factHandleExternalForm,
            boolean disconnected) {
        return new FromExternalFactHandleCommand(factHandleExternalForm, disconnected);
    }

    public Command newAgendaGroupSetFocus(String name) {
        return new AgendaGroupSetFocusCommand(name);
    }
}
