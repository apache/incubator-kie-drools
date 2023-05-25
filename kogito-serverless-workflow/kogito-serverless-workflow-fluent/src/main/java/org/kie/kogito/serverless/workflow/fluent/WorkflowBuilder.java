/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.fluent;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.events.EventDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.start.Start;
import io.serverlessworkflow.api.states.DefaultState;
import io.serverlessworkflow.api.workflow.Constants;
import io.serverlessworkflow.api.workflow.Events;
import io.serverlessworkflow.api.workflow.Functions;

public class WorkflowBuilder {

    private static ObjectMapper mapper = ObjectMapperFactory.get();

    public static WorkflowBuilder workflow(String id) {
        return workflow(id, id, "1_0");
    }

    public static WorkflowBuilder workflow(String id, String name, String version) {
        return new WorkflowBuilder(id, name, version);
    }

    private Workflow workflow;
    private List<FunctionDefinition> functions = new LinkedList<>();
    private List<EventDefinition> events = new LinkedList<>();
    private List<State> states = new LinkedList<>();

    private WorkflowBuilder(String id, String name, String version) {
        this.workflow = new Workflow().withId(id).withName(name).withVersion(version);
    }

    public WorkflowBuilder constant(String name, Object value) {
        Constants constants = workflow.getConstants();
        if (constants == null) {
            constants = new Constants(new ObjectMapper().createObjectNode());
            workflow.setConstants(constants);
        }
        ((ObjectNode) constants.getConstantsDef()).set(name, JsonObjectUtils.fromValue(value));
        return this;
    }

    public WorkflowBuilder metadata(String name, String value) {
        Map<String, String> metadata = workflow.getMetadata();
        if (metadata == null) {
            metadata = new HashMap<>();
            workflow.setMetadata(metadata);
        }
        metadata.put(name, value);
        return this;
    }

    public TransitionBuilder<WorkflowBuilder> start(StateBuilder<?, ?> stateBuilder) {
        addFunctions(stateBuilder.getFunctions());
        addEvents(stateBuilder.getEvents());
        DefaultState state = stateBuilder.build();
        startState(state);
        return new TransitionBuilder<>(this, this, state);
    }

    private void startState(DefaultState state) {
        states.add(state);
        workflow.withStart(new Start().withStateName(state.getName()));
    }

    public Workflow build() {
        workflow.setStates(states);
        workflow.withFunctions(new Functions(functions));
        workflow.withEvents(new Events(events));
        return workflow;
    }

    public final WorkflowBuilder function(FunctionBuilder functionBuilder) {
        FunctionDefinition function = functionBuilder.build();
        functions.add(function);
        return this;
    }

    public final WorkflowBuilder event(EventDefBuilder eventBuilder) {
        EventDefinition event = eventBuilder.build();
        events.add(event);
        return this;
    }

    public static ObjectNode jsonObject() {
        return mapper.createObjectNode();
    }

    public static ArrayNode jsonArray() {
        return mapper.createArrayNode();
    }

    void addState(DefaultState state) {
        if (!states.contains(state)) {
            states.add(state);
        }
    }

    void addFunctions(Collection<FunctionBuilder> functions) {
        functions.forEach(this::function);
    }

    void addEvents(Collection<EventDefBuilder> events) {
        events.forEach(this::event);
    }
}
