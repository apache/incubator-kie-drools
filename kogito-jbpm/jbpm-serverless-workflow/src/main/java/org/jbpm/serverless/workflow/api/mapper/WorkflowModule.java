/*
 *
 *   Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.jbpm.serverless.workflow.api.mapper;

import com.fasterxml.jackson.databind.module.SimpleModule;

import org.jbpm.serverless.workflow.api.WorkflowPropertySource;
import org.jbpm.serverless.workflow.api.deserializers.ChoiceDeserializer;
import org.jbpm.serverless.workflow.api.deserializers.DefaultChoiceOperatorDeserializer;
import org.jbpm.serverless.workflow.api.deserializers.DefaultStateTypeDeserializer;
import org.jbpm.serverless.workflow.api.deserializers.EventsActionsActionModeDeserializer;
import org.jbpm.serverless.workflow.api.deserializers.ExtensionDeserializer;
import org.jbpm.serverless.workflow.api.deserializers.OperationStateActionModeDeserializer;
import org.jbpm.serverless.workflow.api.deserializers.StateDeserializer;
import org.jbpm.serverless.workflow.api.deserializers.StringValueDeserializer;
import org.jbpm.serverless.workflow.api.events.EventsActions;
import org.jbpm.serverless.workflow.api.interfaces.Choice;
import org.jbpm.serverless.workflow.api.interfaces.Extension;
import org.jbpm.serverless.workflow.api.interfaces.State;
import org.jbpm.serverless.workflow.api.serializers.*;
import org.jbpm.serverless.workflow.api.choices.DefaultChoice;
import org.jbpm.serverless.workflow.api.states.DefaultState;
import org.jbpm.serverless.workflow.api.states.DelayState;
import org.jbpm.serverless.workflow.api.states.EventState;
import org.jbpm.serverless.workflow.api.states.OperationState;
import org.jbpm.serverless.workflow.api.states.ParallelState;
import org.jbpm.serverless.workflow.api.states.SwitchState;

public class WorkflowModule extends SimpleModule {

    private WorkflowPropertySource workflowPropertySource;
    private ExtensionSerializer extensionSerializer;
    private ExtensionDeserializer extensionDeserializer;

    public WorkflowModule() {
        this(null);
    }

    public WorkflowModule(WorkflowPropertySource workflowPropertySource) {
        super("workflow-module");
        this.workflowPropertySource = workflowPropertySource;
        extensionSerializer = new ExtensionSerializer();
        extensionDeserializer = new ExtensionDeserializer(workflowPropertySource);
        addDefaultSerializers();
        addDefaultDeserializers();
    }

    private void addDefaultSerializers() {
        addSerializer(new WorkflowSerializer());
        addSerializer(new EventStateSerializer());
        addSerializer(new DelayStateSerializer());
        addSerializer(new OperationStateSerializer());
        addSerializer(new ParallelStateSerializer());
        addSerializer(new SwitchStateSerializer());
        addSerializer(new SubflowStateSerializer());
        addSerializer(new RelayStateSerializer());
        addSerializer(new ForEachStateSerializer());
        addSerializer(new CallbackStateSerializer());
        addSerializer(extensionSerializer);
    }

    private void addDefaultDeserializers() {
        addDeserializer(State.class,
                        new StateDeserializer(workflowPropertySource));
        addDeserializer(Choice.class,
                        new ChoiceDeserializer());
        addDeserializer(String.class,
                        new StringValueDeserializer(workflowPropertySource));
        addDeserializer(EventsActions.ActionMode.class,
                        new EventsActionsActionModeDeserializer(workflowPropertySource));
        addDeserializer(OperationState.ActionMode.class,
                        new OperationStateActionModeDeserializer(workflowPropertySource));
        addDeserializer(DefaultState.Type.class,
                        new DefaultStateTypeDeserializer(workflowPropertySource));
        addDeserializer(DelayState.Type.class,
                        new DefaultStateTypeDeserializer(workflowPropertySource));
        addDeserializer(EventState.Type.class,
                        new DefaultStateTypeDeserializer(workflowPropertySource));
        addDeserializer(OperationState.Type.class,
                        new DefaultStateTypeDeserializer(workflowPropertySource));
        addDeserializer(ParallelState.Type.class,
                        new DefaultStateTypeDeserializer(workflowPropertySource));
        addDeserializer(SwitchState.Type.class,
                        new DefaultStateTypeDeserializer(workflowPropertySource));
        addDeserializer(DefaultChoice.Operator.class,
                        new DefaultChoiceOperatorDeserializer(workflowPropertySource));
        addDeserializer(Extension.class, extensionDeserializer);
    }

    public ExtensionSerializer getExtensionSerializer() {
        return extensionSerializer;
    }

    public ExtensionDeserializer getExtensionDeserializer() {
        return extensionDeserializer;
    }
}