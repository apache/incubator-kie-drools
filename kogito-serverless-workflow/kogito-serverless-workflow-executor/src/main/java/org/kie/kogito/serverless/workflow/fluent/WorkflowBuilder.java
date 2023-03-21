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

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.executor.JavaFunctionExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.start.Start;
import io.serverlessworkflow.api.states.DefaultState;
import io.serverlessworkflow.api.workflow.Constants;
import io.serverlessworkflow.api.workflow.Functions;

public class WorkflowBuilder {

    private static ObjectMapper mapper = ObjectMapperFactory.get();

    public static WorkflowBuilder workflow(String id) {
        return workflow(id, "1_0");
    }

    public static WorkflowBuilder workflow(String id, String version) {
        return new WorkflowBuilder(id, version);
    }

    private Workflow workflow;
    private List<FunctionDefinition> functions = new LinkedList<>();
    private Deque<DefaultState> states = new LinkedList<>();
    private JavaFunctionExtension javaFunctionExtension;

    private WorkflowBuilder(String id, String version) {
        this.workflow = new Workflow().withId(id).withVersion(version);
    }

    public WorkflowBuilder name(String name) {
        workflow.withName(name);
        return this;
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

    public Workflow singleton(StateBuilder<?, ?> stateBuilder) {
        startState(stateBuilder.build(new End()));
        return build();
    }

    public TransitionBuilder<WorkflowBuilder> start(StateBuilder<?, ?> stateBuilder) {
        startState(stateBuilder.build());
        return new TransitionBuilder<>(this, states);
    }

    private void startState(DefaultState state) {
        states.add(state);
        workflow.withStart(new Start().withStateName(state.getName()));
    }

    public Workflow build() {
        workflow.setStates((List) states);
        workflow.withFunctions(new Functions(functions));
        return workflow;
    }

    public final WorkflowBuilder function(FunctionBuilder functionBuilder) {
        FunctionDefinition function = functionBuilder.build();
        functions.add(function);
        if (function instanceof FunctionDefinitionEx) {
            Function<?, ?> javaFunction = ((FunctionDefinitionEx) function).getFunction();
            if (javaFunctionExtension == null) {
                javaFunctionExtension = new JavaFunctionExtension();
                workflow.getExtensions().add(javaFunctionExtension);
            }
            javaFunctionExtension.functions().put(function.getName(), javaFunction);
        }
        return this;
    }

    public static ObjectNode objectNode() {
        return mapper.createObjectNode();
    }

    public static ArrayNode arrayNode() {
        return mapper.createArrayNode();
    }

}
