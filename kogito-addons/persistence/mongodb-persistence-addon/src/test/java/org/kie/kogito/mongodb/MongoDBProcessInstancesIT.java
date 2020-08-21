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

package org.kie.kogito.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.drools.core.io.impl.ClassPathResource;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.ActionNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Node;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.persistence.KogitoProcessInstancesFactory;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.services.identity.StaticIdentityProvider;
import org.kie.kogito.testcontainers.KogitoMongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ERROR;

@Testcontainers
class MongoDBProcessInstancesIT {

    private SecurityPolicy securityPolicy = SecurityPolicy.of(new StaticIdentityProvider("john"));

    @Container
    final static KogitoMongoDBContainer mongoDBContainer = new KogitoMongoDBContainer();
    final static String DB_NAME = "testdb";
    private static MongoClient mongoClient;

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        mongoDBContainer.start();
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
    }

    @AfterAll
    public static void close() {
        mongoDBContainer.stop();
    }

    @Test
    void test() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new MongoDBProcessInstancesFactory(mongoClient));
        process.configure();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", "test");
        parameters.put("integerVar", 10);
        parameters.put("booleanVar", true);
        parameters.put("doubleVar", 10.11);
        parameters.put("floatVar", 3.5f);
        parameters.put("address", new Address("main street", "Boston", "10005", "US"));

        PersonWithAddresses pa = new PersonWithAddresses("bob", 16);
        List<Address> list = new ArrayList<>();
        list.add(new Address("main street", "Boston", "10005", "US"));
        list.add(new Address("new Street", "Charlotte", "28200", "US"));

        pa.setAddresses(list);
        parameters.put("pa", pa);
        parameters.put("addresslist", list);

        Map<Object, Object> map = new HashMap<>();
        map.put("addresslist", list);
        map.put(1, "ss");
        map.put("2", "kk");
        Map<Object, Object> testMap = new HashMap<>();
        testMap.put("addresslist", list);
        testMap.put(1, "integer");
        testMap.put("2", "string");
        testMap.put("map", map);
        parameters.put("testMap", testMap);
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(parameters));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        assertThat(processInstance.description()).isEqualTo("User Task");

        Collection<? extends ProcessInstance<BpmnVariables>> values = process.instances().values();
        assertThat(values).hasSize(1);

        String testVar = (String) processInstance.variables().get("test");
        assertThat(testVar).isEqualTo("test");
        Object addr = processInstance.variables().get("address");
        assertThat(addr.getClass().getName()).isEqualTo("org.kie.kogito.mongodb.Address");
        Object flt = processInstance.variables().get("floatVar");
        assertThat(flt.getClass().getName()).isEqualTo("java.lang.Float");
        assertThat(processInstance.description()).isEqualTo("User Task");

        List<WorkItem> workItems = processInstance.workItems(securityPolicy);
        assertThat(workItems).hasSize(1);
        WorkItem workItem = workItems.get(0);
        assertEquals("john", workItem.getParameters().get("ActorId"));
        processInstance.completeWorkItem(workItem.getId(), null, securityPolicy);
        assertEquals(STATE_COMPLETED, processInstance.status());
        assertThat(process.instances().size()).isZero();
    }

    @Test
    void testFindByIdReadMode() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask-Script.bpmn2")).get(0);
        // workaround as BpmnProcess does not compile the scripts but just reads the xml
        for (Node node : ((WorkflowProcess) process.process()).getNodes()) {
            if (node instanceof ActionNode) {
                DroolsAction a = ((ActionNode) node).getAction();
                a.setMetaData("Action", (Action) kcontext -> {
                    System.out.println("The variable value is " + kcontext.getVariable("s") + " about to call toString on it");
                    kcontext.getVariable("s").toString();
                });
            }
        }
        process.setProcessInstancesFactory(new MongoDBProcessInstancesFactory(mongoClient));
        process.configure();

        ProcessInstance<BpmnVariables> mutablePi = process.createInstance(BpmnVariables.create(Collections.singletonMap("var", "value")));

        mutablePi.start();
        assertThat(mutablePi.status()).isEqualTo(STATE_ERROR);
        assertThat(mutablePi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).endsWith("java.lang.NullPointerException - null");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(mutablePi.variables().toMap()).containsExactly(entry("var", "value"));

        ProcessInstances<BpmnVariables> instances = process.instances();
        assertThat(instances.size()).isOne();
        ProcessInstance<BpmnVariables> pi = instances.findById(mutablePi.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> pi.abort());

        ProcessInstance<BpmnVariables> readOnlyPi = instances.findById(mutablePi.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThat(readOnlyPi.status()).isEqualTo(STATE_ERROR);
        assertThat(readOnlyPi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).endsWith("java.lang.NullPointerException - null");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(readOnlyPi.variables().toMap()).containsExactly(entry("var", "value"));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> readOnlyPi.abort());

        instances.findById(mutablePi.id()).get().abort();
        assertThat(instances.size()).isZero();
    }

    @Test
    void testValuesReadMode() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new MongoDBProcessInstancesFactory(mongoClient));
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();

        ProcessInstances<BpmnVariables> instances = process.instances();
        assertThat(instances.size()).isOne();
        ProcessInstance<BpmnVariables> pi = instances.values().stream().findFirst().get();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> pi.abort());
        instances.values(ProcessInstanceReadMode.MUTABLE).stream().findFirst().get().abort();
        assertThat(instances.size()).isZero();
    }

    private class MongoDBProcessInstancesFactory extends KogitoProcessInstancesFactory {

        public MongoDBProcessInstancesFactory(MongoClient mongoClient) {
            super(mongoClient);
        }

        @Override
        public String dbName() {
            return DB_NAME;
        }
    }
}
