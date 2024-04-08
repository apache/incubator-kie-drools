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
import java.util.Date;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.drools.io.ClassPathResource;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.jupiter.api.Test;
import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.impl.AbstractProcessInstance;

import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_INSTANCE_ID;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertEmpty;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertOne;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersistentProcessInstancesIT extends TestHelper {

    @Test
    void testMongoDBPersistence() {
        MongoDBProcessInstancesFactory factory = new MongoDBProcessInstancesFactory(getMongoClient(), getDisabledMongoDBTransactionManager());

        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(factory);
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);

        MongoDBProcessInstances<?> mongodbInstance = new MongoDBProcessInstances<>(getMongoClient(), process, DB_NAME, getDisabledMongoDBTransactionManager(), false);

        assertOne(mongodbInstance);
        assertOne(process.instances());

        Optional<?> findById = mongodbInstance.findById(processInstance.id());
        BpmnProcessInstance found = (BpmnProcessInstance) findById.get();
        assertThat(found).as("ProcessInstanceDocument cannot be null").isNotNull();
        assertThat(found.id()).isEqualTo(processInstance.id());
        assertThat(found.description()).isEqualTo("User Task");
        assertThat(found.variables().toMap()).containsExactly(entry("test", "test"));
        assertThat(mongodbInstance.exists(processInstance.id())).isTrue();
        assertOne(mongodbInstance);

        ProcessInstance<?> readOnlyPI = mongodbInstance.findById(processInstance.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThat(readOnlyPI).as("ProcessInstanceDocument cannot be null").isNotNull();
        assertThat(mongodbInstance.stream(ProcessInstanceReadMode.READ_ONLY)).hasSize(1);

        mongodbInstance.remove(processInstance.id());
        assertThat(mongodbInstance.exists(processInstance.id())).isFalse();
        assertEmpty(mongodbInstance);
    }

    @Test
    void testMongoDBPersistenceWithTransaction() {
        AbstractTransactionManager transactionExecutor = mock(AbstractTransactionManager.class);
        ClientSession clientSession = mock(ClientSession.class);
        when(transactionExecutor.getClientSession()).thenReturn(clientSession);

        MongoClient mongoClient = mock(MongoClient.class);
        MongoDatabase mongoDatabase = mock(MongoDatabase.class);
        MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.withCodecRegistry(any())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString(), eq(Document.class))).thenReturn(mongoCollection);
        when(mongoCollection.withCodecRegistry(any())).thenReturn(mongoCollection);

        MongoCursor<Document> cursor = mock(MongoCursor.class);
        when(cursor.hasNext()).thenReturn(false);
        FindIterable<Document> results = mock(FindIterable.class);
        when(results.first()).thenReturn(null);
        when(results.iterator()).thenReturn(cursor);
        when(mongoCollection.find(eq(clientSession), any(Bson.class))).thenReturn(results);
        when(mongoCollection.find(eq(clientSession))).thenReturn(results);
        when(mongoCollection.find(any(Bson.class))).thenReturn(results);
        when(mongoCollection.find()).thenReturn(results);

        String id = "testId";

        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new MongoDBProcessInstancesFactory(getMongoClient(), transactionExecutor));
        process.configure();

        MongoDBProcessInstances<BpmnVariables> mongodbInstance = new MongoDBProcessInstances<>(mongoClient, process, DB_NAME, transactionExecutor, false);

        mongodbInstance.findById(id, ProcessInstanceReadMode.READ_ONLY);
        verify(mongoCollection, times(1)).find(eq(clientSession), eq(Filters.eq(PROCESS_INSTANCE_ID, id)));

        mongodbInstance.exists(id);
        verify(mongoCollection, times(2)).find(eq(clientSession), eq(Filters.eq(PROCESS_INSTANCE_ID, id)));

        mongodbInstance.remove(id);
        verify(mongoCollection, times(1)).deleteOne(eq(clientSession), eq(Filters.eq(PROCESS_INSTANCE_ID, id)));

        WorkflowProcessInstance updatePi = ((AbstractProcessInstance<?>) process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")))).internalGetProcessInstance();
        updatePi.setId(id);
        updatePi.setStartDate(new Date());

        AbstractProcessInstance mockUpdateProcessInstance = mock(AbstractProcessInstance.class);
        when(mockUpdateProcessInstance.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
        when(mockUpdateProcessInstance.internalGetProcessInstance()).thenReturn(updatePi);
        when(mockUpdateProcessInstance.process()).thenReturn(process);

        mongodbInstance.update(id, mockUpdateProcessInstance);
        verify(mongoCollection, times(1)).replaceOne(eq(clientSession), eq(Filters.eq(PROCESS_INSTANCE_ID, id)), any());

        WorkflowProcessInstance createPi = ((AbstractProcessInstance<?>) process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")))).internalGetProcessInstance();
        createPi.setId(id);
        createPi.setStartDate(new Date());

        AbstractProcessInstance mockCreateProcessInstance = mock(AbstractProcessInstance.class);
        when(mockCreateProcessInstance.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
        when(mockCreateProcessInstance.internalGetProcessInstance()).thenReturn(createPi);
        when(mockCreateProcessInstance.process()).thenReturn(process);

        mongodbInstance.create(id, mockCreateProcessInstance);
        verify(mongoCollection, times(1)).insertOne(eq(clientSession), any());
    }

    private class MongoDBProcessInstancesFactory extends AbstractProcessInstancesFactory {

        public MongoDBProcessInstancesFactory(MongoClient mongoClient, AbstractTransactionManager transactionManager) {
            super(mongoClient, DB_NAME, false, transactionManager);
        }

    }

}
