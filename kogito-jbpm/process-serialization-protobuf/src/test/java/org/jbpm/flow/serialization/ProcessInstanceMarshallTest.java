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
package org.jbpm.flow.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.flow.serialization.impl.ProtobufMarshallerReaderContext;
import org.jbpm.flow.serialization.impl.ProtobufProcessInstanceReader;
import org.jbpm.flow.serialization.impl.ProtobufProcessInstanceWriter;
import org.jbpm.flow.serialization.impl.ProtobufProcessMarshallerWriteContext;
import org.jbpm.flow.serialization.impl.ProtobufVariableReader;
import org.jbpm.flow.serialization.impl.ProtobufVariableWriter;
import org.jbpm.flow.serialization.protobuf.KogitoTypesProtobuf;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.process.impl.AbstractProcess;

import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess.RULEFLOW_TYPE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProcessInstanceMarshallTest {
    private static final String PROCESS_DESCRIPTION = "The description";
    private static final String PROCESS_INSTANCE_ID = "1";
    private static final String ROOT_PROCESS_ID = "rootProcess";
    private static final String ROOT_PROCESS_INSTANCE_ID = "0";
    private static final String PARENT_PROCESS_ID = "2";

    private static AbstractProcess<?> process;

    private static WorkflowProcessImpl workflow;

    @BeforeAll
    public static void init() {
        workflow = new WorkflowProcessImpl();

        workflow.setId("processId");
        workflow.setVersion("1.0");
        workflow.setType(RULEFLOW_TYPE);

        Node endNode = new EndNode();
        endNode.setId(WorkflowElementIdentifierFactory.fromExternalFormat("one"));
        endNode.setName("end node");

        Node taskNode = new HumanTaskNode();
        taskNode.setId(WorkflowElementIdentifierFactory.fromExternalFormat("two"));
        taskNode.setName("human task");
        new ConnectionImpl(taskNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);

        Node startNode = new StartNode();
        startNode.setId(WorkflowElementIdentifierFactory.fromExternalFormat("three"));
        startNode.setName("start node");
        new ConnectionImpl(startNode, Node.CONNECTION_DEFAULT_TYPE, taskNode, Node.CONNECTION_DEFAULT_TYPE);

        workflow.addNode(startNode);
        workflow.addNode(taskNode);
        workflow.addNode(endNode);

        process = mock(AbstractProcess.class);
        when(process.getProcessRuntime()).thenReturn(mock(KogitoProcessRuntime.class));
        when(process.get()).thenReturn(workflow);
    }

    public static class MarshableObject implements Serializable {

        private static final long serialVersionUID = 1481370154514125687L;

        private String name;

        public MarshableObject() {
        }

        public MarshableObject(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MarshableObject) {
                return this.name.equals(((MarshableObject) obj).name);
            }
            return false;
        }
    }

    private static Stream<Arguments> testProcessInstanceMarshalling() {
        return Stream.of(Arguments.of(buildInstance(false)),
                (Arguments.of(buildInstance(true))));
    }

    private static RuleFlowProcessInstance buildInstance(boolean orphan) {
        RuleFlowProcessInstance instance = new RuleFlowProcessInstance();
        instance.setId(PROCESS_INSTANCE_ID);
        instance.setStartDate(new Date());
        instance.setDescription(PROCESS_DESCRIPTION);

        if (!orphan) {
            instance.setRootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID);
            instance.setRootProcessId(ROOT_PROCESS_ID);
            instance.setParentProcessInstanceId(PARENT_PROCESS_ID);
        }

        instance.setProcess(workflow);
        instance.setContextInstance(VariableScope.VARIABLE_SCOPE, new VariableScopeInstance());

        return instance;
    }

    private static Stream<Arguments> testRoundTrip() throws Exception {
        return Stream.of(
                Arguments.of(1),
                Arguments.of("hello"),
                Arguments.of(Boolean.TRUE),
                Arguments.of(2f),
                Arguments.of(3d),
                Arguments.of(5l),
                Arguments.of(BigDecimal.valueOf(10l)),
                Arguments.of(new MarshableObject("henry")),
                Arguments.of(new ObjectMapper().readTree("{ \"key\" : \"value\" }")),
                Arguments.of(new ObjectMapper().valueToTree(new MarshableObject("henry"))),
                Arguments.of(new Date()),
                Arguments.of(Instant.now()),
                Arguments.of(OffsetDateTime.now()),
                Arguments.of(LocalDateTime.now()),
                Arguments.of(LocalDate.now()),
                Arguments.of(ZonedDateTime.now()),
                Arguments.of(new Timestamp(System.currentTimeMillis())),
                Arguments.of(Duration.ofDays(1))

        );
    }

    @ParameterizedTest
    @MethodSource
    public void testProcessInstanceMarshalling(RuleFlowProcessInstance toMarshall) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtobufProcessMarshallerWriteContext ctxOut = new ProtobufProcessMarshallerWriteContext(out);
        ctxOut.set(MarshallerContextName.OBJECT_MARSHALLING_STRATEGIES, defaultStrategies());
        ctxOut.set(MarshallerContextName.MARSHALLER_PROCESS, process);

        ProtobufProcessInstanceWriter writer = new ProtobufProcessInstanceWriter(ctxOut);

        writer.writeProcessInstance(toMarshall, out);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ProtobufMarshallerReaderContext ctxIn = new ProtobufMarshallerReaderContext(in);
        ctxIn.set(MarshallerContextName.OBJECT_MARSHALLING_STRATEGIES, defaultStrategies());
        ctxIn.set(MarshallerContextName.MARSHALLER_PROCESS, process);
        ProtobufProcessInstanceReader reader = new ProtobufProcessInstanceReader(ctxIn);
        RuleFlowProcessInstance unmarshalled = reader.read(in);

        assertThat(unmarshalled)
                .hasFieldOrPropertyWithValue("id", toMarshall.getId())
                .hasFieldOrPropertyWithValue("state", toMarshall.getState())
                .hasFieldOrPropertyWithValue("startDate", toMarshall.getStartDate())
                .hasFieldOrPropertyWithValue("processId", toMarshall.getProcessId())
                .hasFieldOrPropertyWithValue("processVersion", toMarshall.getProcessVersion())
                .hasFieldOrPropertyWithValue("description", toMarshall.getDescription())
                .hasFieldOrPropertyWithValue("rootProcessInstanceId", toMarshall.getRootProcessInstanceId())
                .hasFieldOrPropertyWithValue("rootProcessId", toMarshall.getRootProcessId())
                .hasFieldOrPropertyWithValue("parentProcessInstanceId", toMarshall.getParentProcessInstanceId())
                .hasFieldOrPropertyWithValue("process", toMarshall.getProcess());
    }

    @ParameterizedTest
    @MethodSource
    @NullSource
    public void testRoundTrip(Object toMarshall) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtobufProcessMarshallerWriteContext ctxOut = new ProtobufProcessMarshallerWriteContext(out);
        ctxOut.set(MarshallerContextName.OBJECT_MARSHALLING_STRATEGIES, defaultStrategies());
        ctxOut.set(MarshallerContextName.MARSHALLER_PROCESS, process);
        ProtobufVariableWriter writer = new ProtobufVariableWriter(ctxOut);
        List<KogitoTypesProtobuf.Variable> variables = writer.buildVariables(singletonMap("var", toMarshall).entrySet().stream().collect(Collectors.toList()));

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ProtobufMarshallerReaderContext ctxIn = new ProtobufMarshallerReaderContext(in);
        ctxIn.set(MarshallerContextName.OBJECT_MARSHALLING_STRATEGIES, defaultStrategies());
        ctxIn.set(MarshallerContextName.MARSHALLER_PROCESS, process);
        ProtobufVariableReader reader = new ProtobufVariableReader(ctxIn);
        List<Variable> unmarshalledVars = reader.buildVariables(variables);
        assertThat(unmarshalledVars).hasSize(1);
        assertThat(unmarshalledVars.get(0).getValue()).isEqualTo(toMarshall);
    }

    private ObjectMarshallerStrategy[] defaultStrategies() {
        List<ObjectMarshallerStrategy> strats = new ArrayList<>();
        ServiceLoader<ObjectMarshallerStrategy> loader = ServiceLoader.load(ObjectMarshallerStrategy.class);

        for (ObjectMarshallerStrategy strategy : loader) {
            strats.add(strategy);
        }
        Collections.sort(strats);
        return strats.stream().toArray(ObjectMarshallerStrategy[]::new);
    }
}
