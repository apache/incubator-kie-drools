/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.mongodb;

import java.util.Collections;
import java.util.Optional;

import org.drools.core.io.impl.ClassPathResource;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.KogitoProcessInstancesFactory;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnVariables;

import com.mongodb.client.MongoClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;

class PersistentProcessInstancesIT extends TestHelper {

    @Test
    void testMongoDBPersistence() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new MongoDBProcessInstancesFactory(getMongoClient()));
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertEquals(STATE_ACTIVE, processInstance.status());

        MongoDBProcessInstances<?> mongodbInstance = new MongoDBProcessInstances<>(getMongoClient(), process, DB_NAME);

        assertThat(mongodbInstance.size()).isOne();
        assertThat(mongodbInstance.size()).isEqualTo(process.instances().size());

        Optional<?> findById = mongodbInstance.findById(processInstance.id());
        BpmnProcessInstance found = (BpmnProcessInstance) findById.get();
        assertNotNull(found, "ProcessInstanceDocument cannot be null");
        assertThat(found.id()).isEqualTo(processInstance.id());
        assertThat(found.description()).isEqualTo("User Task");
        assertThat(found.variables().toMap()).containsExactly(entry("test", "test"));
        assertThat(mongodbInstance.exists(processInstance.id())).isTrue();
        assertThat(mongodbInstance.values().size()).isOne();

        ProcessInstance<?> readOnlyPI = mongodbInstance.findById(processInstance.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertNotNull(readOnlyPI, "ProcessInstanceDocument cannot be null");
        assertThat(mongodbInstance.values(ProcessInstanceReadMode.READ_ONLY).size()).isOne();

        mongodbInstance.remove(processInstance.id());
        assertThat(mongodbInstance.exists(processInstance.id())).isFalse();
        assertThat(mongodbInstance.values()).isEmpty();
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
