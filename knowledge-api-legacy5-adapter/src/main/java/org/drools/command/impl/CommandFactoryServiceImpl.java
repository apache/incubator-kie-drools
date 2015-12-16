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

package org.drools.command.impl;

import org.drools.command.BatchExecutionCommand;
import org.drools.command.Command;
import org.drools.command.CommandFactoryService;
import org.drools.command.Setter;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.rule.FactHandle;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CommandFactoryServiceImpl implements CommandFactoryService {

    @Override
    public Command newInsert(Object object) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newInsert(Object object, String outIdentifier, boolean returnObject, String entryPoint) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newInsertElements(Collection objects) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newInsertElements(Collection objects, String outIdentifier, boolean returnObject, String entryPoint) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newRetract(FactHandle factHandle) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Setter newSetter(String accessor, String value) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newModify(FactHandle factHandle, List<Setter> setters) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newFireAllRules() {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newFireAllRules(int max) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newFireAllRules(String outidentifier) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newGetObject(FactHandle factHandle) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newGetObject(FactHandle factHandle, String outIdentifier) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newGetObjects() {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newGetObjects(String outIdentifier) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newGetObjects(ObjectFilter filter) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newGetObjects(ObjectFilter filter, String outIdentifier) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newSetGlobal(String identifie, Object object) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newSetGlobal(String identifier, Object object, boolean out) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newSetGlobal(String identifier, Object object, String outIdentifier) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newGetGlobal(String identifier) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newGetGlobal(String identifier, String outIdentifier) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newStartProcess(String processId) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newStartProcess(String processId, Map<String, Object> parameters) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newSignalEvent(String type, Object event) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newSignalEvent(long processInstanceId, String type, Object event) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newQuery(String identifier, String name) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newQuery(String identifier, String name, Object[] arguments) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public BatchExecutionCommand newBatchExecution(List<? extends Command> commands, String lookup) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newRegisterWorkItemHandlerCommand(WorkItemHandler handler, String workItemName) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newAbortWorkItem(long workItemId) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newCompleteWorkItem(long workItemId, Map<String, Object> results) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newKBuilderSetPropertyCommand(String id, String name, String value) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newKnowledgeBuilderSetPropertyCommand(String id, String name, String value) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command newNewKnowledgeBuilderConfigurationCommand(String localId) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command<FactHandle> fromExternalFactHandleCommand(String factHandleExternalForm) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public Command<FactHandle> fromExternalFactHandleCommand(String factHandleExternalForm, boolean disconnected) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }
}
