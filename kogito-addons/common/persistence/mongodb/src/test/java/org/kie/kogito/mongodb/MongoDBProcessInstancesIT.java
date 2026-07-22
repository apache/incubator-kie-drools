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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.ActionNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Node;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;
import org.kie.kogito.mongodb.utils.DocumentConstants;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.bpmn2.StaticApplicationAssembler;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.kie.kogito.testcontainers.KogitoMongoDBContainer;
import org.kie.kogito.uow.events.UnitOfWorkEndEvent;
import org.kie.kogito.uow.events.UnitOfWorkStartEvent;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.entry;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_COMPLETED;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ERROR;
import static org.kie.kogito.mongodb.utils.DocumentConstants.DOCUMENT_ID;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abortFirst;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertEmpty;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertOne;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.getFirstReadOnly;

@Testcontainers
class MongoDBProcessInstancesIT {

    private SecurityPolicy securityPolicy = SecurityPolicy.of("john", emptyList());

    @Container
    final static KogitoMongoDBContainer mongoDBContainer = new KogitoMongoDBContainer();
    final static String DB_NAME = "testdb";
    final static String COLLECTION_NAME = "UserTask";
    final static String TEST_ID = "test";
    private static MongoClient mongoClient;

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        mongoDBContainer.start();
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        // Create the collection
        MongoCollection<Document> collection = mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME);
        collection.insertOne(new Document().append(DOCUMENT_ID, TEST_ID));
        collection.deleteOne(new Document().append(DOCUMENT_ID, TEST_ID));
    }

    @AfterAll
    public static void close() {
        mongoDBContainer.stop();
    }

    @Test
    void test() {
        AbstractTransactionManager transactionManager = new AbstractTransactionManager(mongoClient, false) {
        };

        test(transactionManager);
    }

    @Test
    void testWithTransaction() {
        AbstractTransactionManager transactionManager = new AbstractTransactionManager(mongoClient, true) {
        };

        transactionManager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));
        test(transactionManager);
        transactionManager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
    }

    private BpmnProcess createProcess(AbstractTransactionManager transactionManager, String name) {
        StaticProcessConfig processConfig = StaticProcessConfig.newStaticProcessConfigBuilder()
                .withWorkItemHandler("Human Task", new DefaultKogitoWorkItemHandler())
                .build();

        Application application =
                StaticApplicationAssembler.instance().newStaticApplication(new MongoDBProcessInstancesFactory(mongoClient, transactionManager), processConfig, name);

        org.kie.kogito.process.Processes container = application.get(org.kie.kogito.process.Processes.class);
        String processId = container.processIds().stream().findFirst().get();
        org.kie.kogito.process.Process<? extends Model> process = container.processById(processId);

        BpmnProcess compiledProcess = (BpmnProcess) process;

        abort(process.instances());
        return compiledProcess;
    }

    private void test(AbstractTransactionManager transactionManager) {
        BpmnProcess process = createProcess(transactionManager, "BPMN2-UserTask.bpmn2");
        testIndexCreation(process);

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

        assertOne(process.instances());

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
        assertThat(workItem.getParameters()).containsEntry("ActorId", "john");
        processInstance.completeWorkItem(workItem.getId(), null, securityPolicy);
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);
        assertEmpty(process.instances());
    }

    private void testIndexCreation(BpmnProcess process) {
        assertThat(process.instances()).isInstanceOf(MongoDBProcessInstances.class);
        MongoDBProcessInstances mongoDBProcessInstances = (MongoDBProcessInstances) process.instances();
        assertThat(mongoDBProcessInstances.getCollection()).isNotNull();
        assertThat(StreamSupport.stream(mongoDBProcessInstances.getCollection().listIndexes().spliterator(), false)
                .map(Document.class::cast)
                .filter(index -> ((Document) index).get("name").equals(DocumentConstants.PROCESS_INSTANCE_ID_INDEX))
                .findFirst()).isPresent();
        assertThat(StreamSupport.stream(mongoDBProcessInstances.getCollection().listIndexes().spliterator(), false)
                .map(Document.class::cast)
                .filter(index -> ((Document) index).get("name").equals(DocumentConstants.PROCESS_BUSINESS_KEY_INDEX))
                .findFirst()).isPresent();
    }

    @Test
    void testFindByIdAndBusinessKeyReadMode() {
        AbstractTransactionManager transactionManager = new AbstractTransactionManager(mongoClient, false) {
        };

        testFindByIdAndBusinessKeyReadMode(transactionManager);
    }

    @Test
    void testFindByIdAndBusinessKeyReadModeWithTransaction() {
        AbstractTransactionManager transactionManager = new AbstractTransactionManager(mongoClient, true) {
        };

        transactionManager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));
        testFindByIdAndBusinessKeyReadMode(transactionManager);
        transactionManager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
    }

    void testFindByIdAndBusinessKeyReadMode(AbstractTransactionManager transactionManager) {
        BpmnProcess process = createProcess(transactionManager, "BPMN2-UserTask-Script.bpmn2");
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

        ProcessInstance<BpmnVariables> mutablePi = process.createInstance("bk-1", BpmnVariables.create(Collections.singletonMap("var", "value")));
        mutablePi.start();
        assertThat(mutablePi.status()).isEqualTo(STATE_ERROR);
        assertThat(mutablePi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).contains("java.lang.NullPointerException");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(mutablePi.variables().toMap()).containsExactly(entry("var", "value"));

        ProcessInstances<BpmnVariables> instances = process.instances();
        assertOne(instances);
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

        ProcessInstance<BpmnVariables> readOnlyPiByBk = instances.findByBusinessKey(mutablePi.businessKey(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThat(readOnlyPiByBk.status()).isEqualTo(STATE_ERROR);
        assertThat(readOnlyPiByBk.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).contains("java.lang.NullPointerException");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(readOnlyPiByBk.variables().toMap()).containsExactly(entry("var", "value"));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> readOnlyPiByBk.abort());

        instances.findById(mutablePi.id()).get().abort();
        assertEmpty(instances);
    }

    @Test
    void testValuesReadMode() {
        AbstractTransactionManager transactionManager = new AbstractTransactionManager(mongoClient, false) {
        };

        testValuesReadMode(transactionManager);
    }

    @Test
    void testValuesReadModeWithTransaction() {
        AbstractTransactionManager transactionManager = new AbstractTransactionManager(mongoClient, true) {
        };

        transactionManager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));
        testValuesReadMode(transactionManager);
        transactionManager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
    }

    void testValuesReadMode(AbstractTransactionManager transactionManager) {
        BpmnProcess process = createProcess(transactionManager, "BPMN2-UserTask.bpmn2");

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();

        ProcessInstances<BpmnVariables> instances = process.instances();
        ProcessInstance<BpmnVariables> pi = getFirstReadOnly(instances);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> pi.abort());
        abortFirst(instances);
        assertEmpty(instances);
    }

    private class MongoDBProcessInstancesFactory extends AbstractProcessInstancesFactory {

        public MongoDBProcessInstancesFactory(MongoClient mongoClient, AbstractTransactionManager transactionManager) {
            super(mongoClient, DB_NAME, false, transactionManager);
        }

    }
}
