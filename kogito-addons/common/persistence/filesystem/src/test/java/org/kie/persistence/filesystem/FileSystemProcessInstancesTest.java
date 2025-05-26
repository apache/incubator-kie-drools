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
package org.kie.persistence.filesystem;

import java.util.Collections;

import org.drools.io.ClassPathResource;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.ActionNode;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Node;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.persistence.filesystem.AbstractProcessInstancesFactory;
import org.kie.kogito.persistence.filesystem.FileSystemProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.kie.kogito.uow.UnitOfWork;
import org.kie.kogito.uow.UnitOfWorkManager;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.entry;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_COMPLETED;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ERROR;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abortFirst;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertEmpty;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.getFirst;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FileSystemProcessInstancesTest {

    private SecurityPolicy securityPolicy = SecurityPolicy.of("john", emptyList());

    private BpmnProcess createProcess(String fileName) {
        StaticProcessConfig config = new StaticProcessConfig();
        ((DefaultWorkItemHandlerConfig) config.workItemHandlers()).register("Human Task", new DefaultKogitoWorkItemHandler());
        BpmnProcess process = BpmnProcess.from(config, new ClassPathResource(fileName)).get(0);
        process.setProcessInstancesFactory(new FileSystemProcessInstancesFactory());
        process.configure();
        abort(process.instances());
        return process;
    }

    @Test
    void testFindByIdReadMode() {
        BpmnProcess process = createProcess("BPMN2-UserTask-Script.bpmn2");
        // workaround as BpmnProcess does not compile the scripts but just reads the xml
        for (Node node : ((WorkflowProcess) process.get()).getNodes()) {
            if (node instanceof ActionNode) {
                DroolsAction a = ((ActionNode) node).getAction();
                a.setMetaData("Action", (Action) kcontext -> {
                    System.out.println("The variable value is " + kcontext.getVariable("s") + " about to call toString on it");
                    kcontext.getVariable("s").toString();
                });
            }
        }

        ProcessInstance<BpmnVariables> mutablePi = process.createInstance(BpmnVariables.create(Collections.singletonMap("var", "value")));

        mutablePi.start();
        assertThat(mutablePi.status()).isEqualTo(STATE_ERROR);
        assertThat(mutablePi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).contains("java.lang.NullPointerException");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(mutablePi.variables().toMap()).containsExactly(entry("var", "value"));

        ProcessInstances<BpmnVariables> instances = process.instances();
        ProcessInstance<BpmnVariables> pi = instances.findById(mutablePi.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> pi.abort());

        ProcessInstance<BpmnVariables> readOnlyPi = instances.findById(mutablePi.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThat(readOnlyPi.status()).isEqualTo(STATE_ERROR);
        assertThat(readOnlyPi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).contains("java.lang.NullPointerException");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(readOnlyPi.variables().toMap()).containsExactly(entry("var", "value"));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> readOnlyPi.abort());

        instances.findById(mutablePi.id()).get().abort();

    }

    @Test
    void testValuesReadMode() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));
        processInstance.start();

        ProcessInstances<BpmnVariables> instances = process.instances();
        ProcessInstance<BpmnVariables> pi = getFirst(instances);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> pi.abort());
        abortFirst(instances);
        assertEmpty(instances);
    }

    @Test
    void testBasicFlow() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        assertThat(processInstance.description()).isEqualTo("User Task");

        FileSystemProcessInstances fileSystemBasedStorage = (FileSystemProcessInstances) process.instances();
        assertThat(fileSystemBasedStorage.exists(processInstance.id())).isTrue();
        verify(fileSystemBasedStorage).create(any(), any());
        verify(fileSystemBasedStorage, times(2)).setMetadata(any(), eq(FileSystemProcessInstances.PI_DESCRIPTION), eq("User Task"));
        verify(fileSystemBasedStorage, times(2)).setMetadata(any(), eq(FileSystemProcessInstances.PI_STATUS), eq("1"));

        String testVar = (String) processInstance.variables().get("test");
        assertThat(testVar).isEqualTo("test");

        assertThat(processInstance.description()).isEqualTo("User Task");
        assertThat(processInstance.workItems(securityPolicy)).hasSize(1);

        WorkItem workItem = processInstance.workItems(securityPolicy).get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters().get("ActorId")).isEqualTo("john");
        processInstance.completeWorkItem(workItem.getId(), null, securityPolicy);
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);

        fileSystemBasedStorage = (FileSystemProcessInstances) process.instances();
        verify(fileSystemBasedStorage, times(1)).remove(processInstance.id());
        assertEmpty(fileSystemBasedStorage);
    }

    @Test
    void testBasicFlowWithStartFrom() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));
        processInstance.startFrom("_2");

        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        assertThat(processInstance.description()).isEqualTo("User Task");

        FileSystemProcessInstances fileSystemBasedStorage = (FileSystemProcessInstances) process.instances();
        verify(fileSystemBasedStorage).create(any(), any());

        String testVar = (String) processInstance.variables().get("test");
        assertThat(testVar).isEqualTo("test");

        assertThat(processInstance.description()).isEqualTo("User Task");

        WorkItem workItem = processInstance.workItems(securityPolicy).get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters().get("ActorId")).isEqualTo("john");
        processInstance.completeWorkItem(workItem.getId(), null, securityPolicy);
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);

        fileSystemBasedStorage = (FileSystemProcessInstances) process.instances();
        verify(fileSystemBasedStorage, times(1)).remove(any());
        assertEmpty(fileSystemBasedStorage);
    }

    @Test
    void testBasicFlowControlledByUnitOfWork() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        process.setProcessInstancesFactory(new FileSystemProcessInstancesFactory());
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        UnitOfWorkManager uowManager = process.getApplication().unitOfWorkManager();
        UnitOfWork uow = uowManager.newUnitOfWork();
        uow.start();

        processInstance.start();

        uow.end();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        assertThat(processInstance.description()).isEqualTo("User Task");

        FileSystemProcessInstances fileSystemBasedStorage = (FileSystemProcessInstances) process.instances();
        assertThat(fileSystemBasedStorage.exists(processInstance.id())).isTrue();
        verify(fileSystemBasedStorage).create(processInstance.id(), processInstance);
        verify(fileSystemBasedStorage, times(2)).setMetadata(any(), eq(FileSystemProcessInstances.PI_DESCRIPTION), eq("User Task"));
        verify(fileSystemBasedStorage, times(2)).setMetadata(any(), eq(FileSystemProcessInstances.PI_STATUS), eq("1"));

        String testVar = (String) processInstance.variables().get("test");
        assertThat(testVar).isEqualTo("test");

        assertThat(processInstance.description()).isEqualTo("User Task");

        WorkItem workItem = processInstance.workItems(securityPolicy).get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters().get("ActorId")).isEqualTo("john");

        uow = uowManager.newUnitOfWork();
        uow.start();
        processInstance.completeWorkItem(workItem.getId(), null, securityPolicy);
        uow.end();

        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);

        fileSystemBasedStorage = (FileSystemProcessInstances) process.instances();
        verify(fileSystemBasedStorage).remove(processInstance.id());
        assertEmpty(fileSystemBasedStorage);
    }

    @Test
    void testResolveSecureRejectsPathTraversalViaFindById() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        FileSystemProcessInstances fsInstances = (FileSystemProcessInstances) process.instances();

        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> fsInstances.findById("../hack"))
                .withMessageContaining("Path traversal attempt detected");
    }

    @Test
    void testResolveSecureRejectsPathTraversalViaCreate() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        FileSystemProcessInstances fsInstances = (FileSystemProcessInstances) process.instances();
        ProcessInstance<BpmnVariables> pi = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "value")));
        pi.start();
        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> fsInstances.create("../malicious", pi))
                .withMessageContaining("Path traversal attempt detected");
    }

    @Test
    void testResolveSecureRejectsPathTraversalViaUpdate() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        FileSystemProcessInstances fsInstances = (FileSystemProcessInstances) process.instances();
        ProcessInstance<BpmnVariables> pi = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "value")));
        pi.start();
        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> fsInstances.update("../../invalid", pi))
                .withMessageContaining("Path traversal attempt detected");
    }

    @Test
    void testResolveSecureRejectsPathTraversalViaRemove() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        FileSystemProcessInstances fsInstances = (FileSystemProcessInstances) process.instances();

        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> fsInstances.remove("../../../etc/passwd"))
                .withMessageContaining("Path traversal attempt detected");
    }

    private static class FileSystemProcessInstancesFactory extends AbstractProcessInstancesFactory {

        public FileSystemProcessInstancesFactory() {
            super("target");
        }

        @Override
        public FileSystemProcessInstances createProcessInstances(Process<?> process) {
            return spy(super.createProcessInstances(process));
        }

    }
}
