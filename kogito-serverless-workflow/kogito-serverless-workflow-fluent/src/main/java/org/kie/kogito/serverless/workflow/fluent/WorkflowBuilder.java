/*
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
package org.kie.kogito.serverless.workflow.fluent;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.start.Start;
import io.serverlessworkflow.api.states.DefaultState;
import io.serverlessworkflow.api.workflow.Constants;
import io.serverlessworkflow.api.workflow.Events;
import io.serverlessworkflow.api.workflow.Functions;

public class WorkflowBuilder {

    private static ObjectMapper mapper = ObjectMapperFactory.get();

    private final Collection<FunctionBuilder> functionDefinitions = new HashSet<>();
    private final Collection<EventDefBuilder> eventDefinitions = new HashSet<>();

    public static WorkflowBuilder workflow(String id) {
        return workflow(id, id, "1_0");
    }

    public static WorkflowBuilder workflow(String id, String name, String version) {
        return new WorkflowBuilder(id, name, version);
    }

    private Workflow workflow;
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
        workflow.withFunctions(new Functions(functionDefinitions.stream().map(FunctionBuilder::build).collect(Collectors.toList())));
        workflow.withEvents(new Events(eventDefinitions.stream().map(EventDefBuilder::build).collect(Collectors.toList())));
        return workflow;
    }

    public final WorkflowBuilder function(FunctionBuilder functionBuilder) {
        functionDefinitions.add(functionBuilder);
        return this;
    }

    public final WorkflowBuilder event(EventDefBuilder eventBuilder) {
        eventDefinitions.add(eventBuilder);
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
