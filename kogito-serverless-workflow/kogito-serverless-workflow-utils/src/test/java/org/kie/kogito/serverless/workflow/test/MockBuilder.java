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
package org.kie.kogito.serverless.workflow.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jbpm.ruleflow.core.Metadata;
import org.kie.api.definition.process.Process;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.workflow.Constants;
import io.serverlessworkflow.api.workflow.Functions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockBuilder {

    private MockBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static WorkflowMockBuilder workflow() {
        return new WorkflowMockBuilder();
    }

    public static KogitoProcessContextMockBuilder kogitoProcessContext() {
        return new KogitoProcessContextMockBuilder();
    }

    public static class WorkflowMockBuilder {
        private Map<String, Object> constants = new HashMap<>();
        private List<Consumer<FunctionDefinition>> functionDefinitionsMockManipulations = new ArrayList<>();

        public WorkflowMockBuilder withConstants(Map<String, Object> constants) {
            this.constants.putAll(constants);
            return this;
        }

        public WorkflowMockBuilder withFunctionDefinitionMock(Consumer<FunctionDefinition> functionDefinitionMockManipulation) {
            this.functionDefinitionsMockManipulations.add(functionDefinitionMockManipulation);
            return this;
        }

        public Workflow build() {
            Workflow workflowMock = mock(Workflow.class);
            Constants constantsMock = mock(Constants.class);
            when(workflowMock.getConstants()).thenReturn(constantsMock);
            if (constants != null && !constants.isEmpty()) {
                when(constantsMock.getConstantsDef()).thenReturn(ObjectMapperFactory.get().valueToTree(constants));
            }
            List<FunctionDefinition> functionDefinitionsMocks = functionDefinitionsMockManipulations.stream().map(def -> {
                FunctionDefinition mock = mock(FunctionDefinition.class);
                def.accept(mock);
                return mock;
            }).collect(Collectors.toList());
            Functions functions = new Functions(functionDefinitionsMocks);
            when(workflowMock.getFunctions()).thenReturn(functions);
            return workflowMock;
        }
    }

    public static class KogitoProcessContextMockBuilder {
        private Consumer<KogitoProcessInstance> processInstanceMockManipulation;
        private Map<String, Object> constants = new HashMap<>();
        private JsonNode input;

        public KogitoProcessContextMockBuilder withProcessInstanceMock(Consumer<KogitoProcessInstance> processInstanceMockManipulation) {
            this.processInstanceMockManipulation = processInstanceMockManipulation;
            return this;
        }

        public KogitoProcessContextMockBuilder withConstants(Map<String, Object> constants) {
            this.constants.putAll(constants);
            return this;
        }

        public KogitoProcessContextMockBuilder withInput(JsonNode input) {
            this.input = input;
            return this;
        }

        public KogitoProcessContext build() {
            KogitoProcessContext context = mock(KogitoProcessContext.class);
            KogitoProcessInstance kogitoProcessInstanceMock = mock(KogitoProcessInstance.class);
            when(context.getProcessInstance()).thenReturn(kogitoProcessInstanceMock);
            Process processMock = mock(Process.class);
            when(kogitoProcessInstanceMock.getProcess()).thenReturn(processMock);
            if (processInstanceMockManipulation != null) {
                processInstanceMockManipulation.accept(kogitoProcessInstanceMock);
            }
            if (constants != null && !constants.isEmpty()) {
                when(processMock.getMetaData()).thenReturn(Collections.singletonMap(Metadata.CONSTANTS, ObjectMapperFactory.get().valueToTree(constants)));
            }
            when(context.getVariable(SWFConstants.INPUT_WORKFLOW_VAR)).thenReturn(input);
            return context;
        }
    }
}
