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

package org.drools.command.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.command.BatchExecutionCommand;
import org.drools.command.Command;
import org.drools.command.CommandFactoryService;
import org.drools.command.NewKnowledgeBuilderConfigurationCommand;
import org.drools.command.Setter;
import org.drools.command.runtime.BatchExecutionCommandImpl;
import org.drools.command.runtime.GetGlobalCommand;
import org.drools.command.runtime.SetGlobalCommand;
import org.drools.command.runtime.KBuilderSetPropertyCommand;
import org.drools.command.runtime.process.AbortWorkItemCommand;
import org.drools.command.runtime.process.CompleteWorkItemCommand;
import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.command.runtime.process.StartProcessCommand;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.command.runtime.rule.FromExternalFactHandleCommand;
import org.drools.command.runtime.rule.GetObjectCommand;
import org.drools.command.runtime.rule.GetObjectsCommand;
import org.drools.command.runtime.rule.InsertElementsCommand;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.command.runtime.rule.ModifyCommand;
import org.drools.command.runtime.rule.QueryCommand;
import org.drools.command.runtime.rule.RetractCommand;
import org.drools.command.runtime.rule.ModifyCommand.SetterImpl;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;

public class CommandFactoryServiceImpl implements CommandFactoryService {

    public Command newGetGlobal(String identifier) {
        return new GetGlobalCommand(identifier);
    }

    public Command newGetGlobal(String identifier, String outIdentifier) {
        GetGlobalCommand cmd = new GetGlobalCommand(identifier);
        cmd.setOutIdentifier(outIdentifier);
        return cmd;
    }

    public Command newInsertElements(Collection objects) {
        return new InsertElementsCommand(objects);
    }

    public Command newInsertElements(Collection objects, String outIdentifier, boolean returnObject, String entryPoint) {
        InsertElementsCommand cmd = new InsertElementsCommand(objects);
        cmd.setEntryPoint( entryPoint );
        cmd.setOutIdentifier( outIdentifier );
        cmd.setReturnObject( returnObject );
        return cmd;
    }

    public Command newInsert(Object object) {
        return new InsertObjectCommand(object);
    }

    public Command newInsert(Object object, String outIdentifier, boolean returnObject, String entryPoint) {
        InsertObjectCommand cmd = new InsertObjectCommand(object);
        cmd.setOutIdentifier(outIdentifier);
        cmd.setEntryPoint( entryPoint );
        cmd.setReturnObject( returnObject );
        return cmd;
    }

    public Command newRetract(FactHandle factHandle) {
        return new RetractCommand( factHandle );
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

    public Command newGetObjects() {
        return newGetObjects(null);
    }

    public Command newGetObjects(ObjectFilter filter) {
        return new GetObjectsCommand(filter);
    }

    public Command newSetGlobal(String identifier, Object object) {
        return new SetGlobalCommand(identifier, object);
    }

    public Command newSetGlobal(String identifier, Object object, boolean out) {
        SetGlobalCommand cmd = new SetGlobalCommand(identifier, object);
        return cmd;
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
        startProcess.setParameters((HashMap<String, Object>) parameters);
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

    public Command newQuery(String identifier, String name) {
        return new QueryCommand(identifier, name, null);
    }

    public Command newQuery(String identifier, String name, Object[] arguments) {
        return new QueryCommand(identifier, name, arguments);
    }

    public BatchExecutionCommand newBatchExecution(List<? extends Command> commands, String lookup) {
        return new BatchExecutionCommandImpl((List<GenericCommand<?>>) commands, lookup);
    }

    public Command newKBuilderSetPropertyCommand(String id, String name, String value) {
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
}
