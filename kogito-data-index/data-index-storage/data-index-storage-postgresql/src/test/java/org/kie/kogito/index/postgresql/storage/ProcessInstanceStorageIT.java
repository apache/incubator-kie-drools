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
package org.kie.kogito.index.postgresql.storage;

import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.postgresql.model.ProcessInstanceEntity;
import org.kie.kogito.index.postgresql.model.ProcessInstanceEntityRepository;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.persistence.api.StorageService;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
public class ProcessInstanceStorageIT extends AbstractStorageIT<ProcessInstanceEntity, ProcessInstance> {

    @Inject
    ProcessInstanceEntityRepository repository;

    @Inject
    StorageService storage;

    public ProcessInstanceStorageIT() {
        super(ProcessInstance.class);
    }

    @Override
    public ProcessInstanceEntityRepository getRepository() {
        return repository;
    }

    @Override
    public StorageService getStorage() {
        return storage;
    }

    @Test
    @Transactional
    public void testProcessInstanceEntity() {
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstance processInstance1 = TestUtils
                .createProcessInstance(processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), ProcessInstanceState.ACTIVE.ordinal(), 0L);
        ProcessInstance processInstance2 = TestUtils
                .createProcessInstance(processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), ProcessInstanceState.COMPLETED.ordinal(), 1000L);
        testStorage(processInstanceId, processInstance1, processInstance2);
    }

}
