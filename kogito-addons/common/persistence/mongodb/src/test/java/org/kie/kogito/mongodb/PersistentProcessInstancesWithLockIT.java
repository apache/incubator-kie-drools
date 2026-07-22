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
package org.kie.kogito.mongodb;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.bpmn2.StaticApplicationAssembler;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertEmpty;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertOne;

class PersistentProcessInstancesWithLockIT extends TestHelper {

    private BpmnProcess createProcess(MongoDBProcessInstancesFactory factory) {
        StaticProcessConfig processConfig = StaticProcessConfig.newStaticProcessConfigBuilder()
                .withWorkItemHandler("Human Task", new DefaultKogitoWorkItemHandler())
                .build();

        Application application =
                StaticApplicationAssembler.instance().newStaticApplication(factory, processConfig, "BPMN2-UserTask.bpmn2");

        org.kie.kogito.process.Processes container = application.get(org.kie.kogito.process.Processes.class);
        String processId = container.processIds().stream().findFirst().get();
        org.kie.kogito.process.Process<? extends Model> process = container.processById(processId);

        BpmnProcess compiledProcess = (BpmnProcess) process;

        abort(process.instances());
        return compiledProcess;
    }

    @Test
    public void testUpdate() {
        MongoDBProcessInstancesFactory factory = new MongoDBProcessInstancesFactory();

        BpmnProcess process = createProcess(factory);
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));
        processInstance.start();

        MongoDBProcessInstances<?> processInstances = new MongoDBProcessInstances<>(getMongoClient(), process, DB_NAME, getDisabledMongoDBTransactionManager(), true);
        assertOne(processInstances);

        BpmnProcessInstance instanceOne = (BpmnProcessInstance) processInstances.findById(processInstance.id()).get();
        BpmnProcessInstance instanceTwo = (BpmnProcessInstance) processInstances.findById(processInstance.id()).get();

        assertThat(instanceOne.version()).isOne();
        assertThat(instanceTwo.version()).isOne();
        instanceOne.updateVariables(BpmnVariables.create(Collections.singletonMap("s", "test")));

        BpmnVariables testvar = BpmnVariables.create(Collections.singletonMap("ss", "test"));
        instanceTwo.updateVariables(testvar);
        assertThat(instanceTwo.version()).isEqualTo(2L);

        instanceOne = (BpmnProcessInstance) processInstances.findById(processInstance.id()).get();
        assertThat(instanceOne.version()).isEqualTo(3L);

        processInstances.remove(processInstance.id());
        assertEmpty(processInstances);
    }

    @Test
    public void testRemove() {
        MongoDBProcessInstancesFactory factory = new MongoDBProcessInstancesFactory();

        BpmnProcess process = createProcess(factory);

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));
        processInstance.start();

        MongoDBProcessInstances<?> processInstances = new MongoDBProcessInstances<>(getMongoClient(), process, DB_NAME, getDisabledMongoDBTransactionManager(), true);
        BpmnProcessInstance instanceOne = (BpmnProcessInstance) processInstances.findById(processInstance.id()).get();
        BpmnProcessInstance instanceTwo = (BpmnProcessInstance) processInstances.findById(processInstance.id()).get();
        assertThat(instanceOne.version()).isOne();
        assertThat(instanceTwo.version()).isOne();

        processInstances.remove(instanceOne.id());
        try {
            String id = instanceTwo.id();
            processInstances.remove(id);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("The document with ID: " + instanceOne.id() + " was deleted by other request.");
        }
    }

    private class MongoDBProcessInstancesFactory extends AbstractProcessInstancesFactory {

        public MongoDBProcessInstancesFactory() {
            super(getMongoClient(), DB_NAME, true, getDisabledMongoDBTransactionManager());
        }

    }
}
