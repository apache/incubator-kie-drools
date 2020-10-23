/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.serverless.workflow.api.mapper;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.jbpm.serverless.workflow.api.deserializers.*;
import org.jbpm.serverless.workflow.api.events.EventDefinition;
import org.jbpm.serverless.workflow.api.events.OnEvents;
import org.jbpm.serverless.workflow.api.interfaces.Extension;
import org.jbpm.serverless.workflow.api.interfaces.State;
import org.jbpm.serverless.workflow.api.interfaces.WorkflowPropertySource;
import org.jbpm.serverless.workflow.api.schedule.Schedule;
import org.jbpm.serverless.workflow.api.serializers.*;
import org.jbpm.serverless.workflow.api.states.DefaultState;
import org.jbpm.serverless.workflow.api.states.OperationState;
import org.jbpm.serverless.workflow.api.states.ParallelState;

public class WorkflowModule extends SimpleModule {

    private static final long serialVersionUID = 510l;

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
        addSerializer(new InjectStateSerializer());
        addSerializer(new ForEachStateSerializer());
        addSerializer(new CallbackStateSerializer());
        addSerializer(extensionSerializer);
    }

    private void addDefaultDeserializers() {
        addDeserializer(State.class,
                new StateDeserializer(workflowPropertySource));
        addDeserializer(String.class,
                new StringValueDeserializer(workflowPropertySource));
        addDeserializer(OnEvents.ActionMode.class,
                new OnEventsActionModeDeserializer(workflowPropertySource));
        addDeserializer(OperationState.ActionMode.class,
                new OperationStateActionModeDeserializer(workflowPropertySource));
        addDeserializer(DefaultState.Type.class,
                new DefaultStateTypeDeserializer(workflowPropertySource));
        addDeserializer(EventDefinition.Kind.class, new EventDefinitionKindDeserializer(workflowPropertySource));
        addDeserializer(ParallelState.CompletionType.class, new ParallelStateCompletionTypeDeserializer(workflowPropertySource));
        addDeserializer(Schedule.DirectInvoke.class, new ScheduleDirectInvokeDeserializer(workflowPropertySource));
        addDeserializer(Extension.class, extensionDeserializer);

    }

    public ExtensionSerializer getExtensionSerializer() {
        return extensionSerializer;
    }

    public ExtensionDeserializer getExtensionDeserializer() {
        return extensionDeserializer;
    }
}