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
package org.jbpm.flow.serialization.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.jbpm.flow.serialization.MarshallerContextName;
import org.jbpm.flow.serialization.NodeInstanceWriter;
import org.jbpm.flow.serialization.ObjectMarshallerStrategyHelper;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory;
import org.jbpm.util.JbpmClassLoaderUtil;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.instance.node.ForEachNodeInstance;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.process.impl.AbstractProcess;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProtobufProcessInstanceWriterTest {

    private static AbstractProcess<?> mockProcess;
    private static NodeInstanceWriter[] nodeInstanceWriters;
    private ProtobufProcessMarshallerWriteContext writeContext;

    @BeforeAll
    static void setup() {
        ServiceLoader<NodeInstanceWriter> writerLoader = ServiceLoader.load(NodeInstanceWriter.class, JbpmClassLoaderUtil.findClassLoader());
        int items = (int) writerLoader.stream().count();
        nodeInstanceWriters = writerLoader.stream().map(ServiceLoader.Provider::get).map(NodeInstanceWriter.class::cast).toArray(value -> new NodeInstanceWriter[items]);
        mockProcess = getMockedProcess();
    }

    @BeforeEach
    void init() {
        writeContext = new ProtobufProcessMarshallerWriteContext(new ByteArrayOutputStream());
        writeContext.set(MarshallerContextName.OBJECT_MARSHALLING_STRATEGIES, ObjectMarshallerStrategyHelper.defaultStrategies());
        writeContext.set(MarshallerContextName.MARSHALLER_PROCESS, mockProcess);
        writeContext.set(MarshallerContextName.MARSHALLER_NODE_INSTANCE_WRITER, nodeInstanceWriters);
    }

    @SuppressWarnings("unchecked")
    @Test
    void buildWorkflowContext() {
        ProtobufProcessInstanceWriter spiedProtobufProcessInstanceWriter = spy(new ProtobufProcessInstanceWriter(writeContext));
        ForEachNodeInstance nodeInstance = getNodeInstanceContainer();
        try {
            spiedProtobufProcessInstanceWriter.buildWorkflowContext(nodeInstance);
        } catch (Exception e) {
            // expected due to partial instantiation
            assertThat(e).isInstanceOf(NullPointerException.class);
            ArgumentCaptor<List<NodeInstance>> nodeInstancesCapture = ArgumentCaptor.forClass(ArrayList.class);
            ArgumentCaptor<List<ContextInstance>> exclusiveGroupInstancesCapture = ArgumentCaptor.forClass(ArrayList.class);
            ArgumentCaptor<List<Map.Entry<String, Object>>> variablesCapture = ArgumentCaptor.forClass(ArrayList.class);
            ArgumentCaptor<List<Map.Entry<String, Integer>>> iterationlevelsCapture = ArgumentCaptor.forClass(ArrayList.class);
            verify(spiedProtobufProcessInstanceWriter).buildWorkflowContext(nodeInstancesCapture.capture(), exclusiveGroupInstancesCapture.capture(), variablesCapture.capture(),
                    iterationlevelsCapture.capture());
            Collection<NodeInstance> expected = nodeInstance.getSerializableNodeInstances();
            List<NodeInstance> retrieved = nodeInstancesCapture.getValue();
            assertThat(retrieved).isNotNull().hasSize(expected.size()).allMatch(expected::contains);
        }
    }

    private ForEachNodeInstance getNodeInstanceContainer() {
        String id = "NodeInstanceContainer";
        ForEachNodeInstance toReturn = new ForEachNodeInstance();
        toReturn.setId(id);
        toReturn.setLevel(1);
        toReturn.addNodeInstance(getNodeInstanceSerializable(id));
        toReturn.addNodeInstance(getNodeInstanceNotSerializable(id));
        toReturn.setNodeId(getWorkflowElementIdentifier(id));
        toReturn.setContextInstance(VariableScope.VARIABLE_SCOPE, new VariableScopeInstance());
        Collection<NodeInstance> nodeInstances = toReturn.getNodeInstances();
        assertThat(nodeInstances)
                .isNotNull()
                .hasSize(2)
                .anyMatch(HumanTaskNodeInstance.class::isInstance)
                .anyMatch(ForEachNodeInstance.ForEachJoinNodeInstance.class::isInstance);
        Collection<NodeInstance> serializableNodeInstances = toReturn.getSerializableNodeInstances();
        assertThat(serializableNodeInstances)
                .isNotNull()
                .hasSize(1)
                .allMatch(HumanTaskNodeInstance.class::isInstance);
        return toReturn;
    }

    private ForEachNodeInstance.ForEachJoinNodeInstance getNodeInstanceNotSerializable(String parent) {
        String id = String.format("%s-%s", parent, "nestedNodeInstanceNotSerializable");
        ForEachNodeInstance.ForEachJoinNodeInstance toReturn = new ForEachNodeInstance().new ForEachJoinNodeInstance();
        toReturn.setId(id);
        toReturn.setLevel(2);
        return toReturn;
    }

    private HumanTaskNodeInstance getNodeInstanceSerializable(String parent) {
        String id = String.format("%s-%s", parent, "nestedNodeInstanceSerializable");
        HumanTaskNodeInstance toReturn = new HumanTaskNodeInstance();
        toReturn.setId(id);
        toReturn.setNodeId(getWorkflowElementIdentifier(id));
        return toReturn;
    }

    private WorkflowElementIdentifier getWorkflowElementIdentifier(String parent) {
        String id = String.format("%s-%s", parent, "workflowElementIdentifier");
        return WorkflowElementIdentifierFactory.fromExternalFormat(id);
    }

    private static AbstractProcess<?> getMockedProcess() {
        AbstractProcess<?> toReturn = mock(AbstractProcess.class);
        when(toReturn.getProcessRuntime()).thenReturn(mock(KogitoProcessRuntime.class));
        when(toReturn.get()).thenReturn(new WorkflowProcessImpl());
        return toReturn;
    }

}
