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

package org.kie.persistence.filesystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.drools.core.io.impl.ClassPathResource;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.persistence.KogitoProcessInstancesFactory;
import org.kie.kogito.persistence.filesystem.FileSystemProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.services.identity.StaticIdentityProvider;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.uow.UnitOfWork;
import org.kie.kogito.uow.UnitOfWorkManager;


public class FileSystemProcessInstancesTest {

    private SecurityPolicy securityPolicy = SecurityPolicy.of(new StaticIdentityProvider("john"));

    @Test
    public void testBasicFlow() {

        BpmnProcess process = (BpmnProcess) BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new FileSystemProcessInstancesFactory());
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        assertThat(processInstance.description()).isEqualTo("User Task");

        assertThat(process.instances().values()).hasSize(1);
        
        FileSystemProcessInstances fileSystemBasedStorage = (FileSystemProcessInstances) process.instances();
        verify(fileSystemBasedStorage, times(1)).create(any(), any());
        verify(fileSystemBasedStorage, times(1)).setMetadata(any(), eq(FileSystemProcessInstances.PI_DESCRIPTION), eq("User Task"));
        verify(fileSystemBasedStorage, times(1)).setMetadata(any(), eq(FileSystemProcessInstances.PI_STATUS), eq("1"));

        String testVar = (String) processInstance.variables().get("test");
        assertThat(testVar).isEqualTo("test");

        assertThat(processInstance.description()).isEqualTo("User Task");

        WorkItem workItem = processInstance.workItems(securityPolicy).get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters().get("ActorId")).isEqualTo("john");
        processInstance.completeWorkItem(workItem.getId(), null, securityPolicy);
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);
        
        fileSystemBasedStorage = (FileSystemProcessInstances) process.instances();
        verify(fileSystemBasedStorage, times(2)).remove(any());
    }
    
    @Test
    public void testBasicFlowControlledByUnitOfWork() {

        UnitOfWorkManager uowManager = new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory());
        ProcessConfig config = new StaticProcessConfig(new DefaultWorkItemHandlerConfig(), new DefaultProcessEventListenerConfig(), uowManager, null);
        BpmnProcess process = (BpmnProcess) BpmnProcess.from(config, new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new FileSystemProcessInstancesFactory());
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        UnitOfWork uow = uowManager.newUnitOfWork();
        uow.start();
        
        processInstance.start();
        
        uow.end();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        assertThat(processInstance.description()).isEqualTo("User Task");

        assertThat(process.instances().values()).hasSize(1);
        
        FileSystemProcessInstances fileSystemBasedStorage = (FileSystemProcessInstances) process.instances();
        verify(fileSystemBasedStorage, times(1)).create(any(), any());
        verify(fileSystemBasedStorage, times(1)).setMetadata(any(), eq(FileSystemProcessInstances.PI_DESCRIPTION), eq("User Task"));
        verify(fileSystemBasedStorage, times(1)).setMetadata(any(), eq(FileSystemProcessInstances.PI_STATUS), eq("1"));

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
        verify(fileSystemBasedStorage, times(1)).remove(any());
    }

    private class FileSystemProcessInstancesFactory extends KogitoProcessInstancesFactory {
                
        @Override
        public FileSystemProcessInstances createProcessInstances(Process<?> process) {
            FileSystemProcessInstances instances = spy(super.createProcessInstances(process));
            return instances;
        }

        @Override
        public String path() {
            return "target";
        }
    }
}
