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

package org.kie.kogito.index.mongodb.query;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.mongodb.TestUtils;
import org.kie.kogito.index.mongodb.model.ProcessInstanceEntity;
import org.kie.kogito.index.mongodb.model.ProcessInstanceEntityMapper;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.SortDirection;
import org.kie.kogito.persistence.mongodb.MongoServerTestResource;
import org.kie.kogito.persistence.mongodb.client.MongoClientManager;
import org.kie.kogito.persistence.mongodb.storage.MongoStorage;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.kie.kogito.index.Constants.PROCESS_INSTANCES_STORAGE;
import static org.kie.kogito.index.model.ProcessInstanceState.ACTIVE;
import static org.kie.kogito.index.model.ProcessInstanceState.COMPLETED;
import static org.kie.kogito.index.mongodb.query.QueryTestUtils.assertWithId;
import static org.kie.kogito.index.mongodb.query.QueryTestUtils.assertWithIdInOrder;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.and;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.between;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.contains;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAll;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAny;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.greaterThan;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.greaterThanEqual;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.in;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.isNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.lessThan;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.lessThanEqual;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.like;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.notNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.or;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.orderBy;

@QuarkusTest
@QuarkusTestResource(MongoServerTestResource.class)
class ProcessInstanceQueryIT extends QueryTestBase<String, ProcessInstance> {

    @Inject
    MongoClientManager mongoClientManager;

    Storage<String, ProcessInstance> storage;

    @BeforeEach
    void setUp() {
        this.storage = new MongoStorage<>(mongoClientManager.getCollection(PROCESS_INSTANCES_STORAGE, ProcessInstanceEntity.class),
                                          mongoClientManager.getReactiveCollection(PROCESS_INSTANCES_STORAGE, ProcessInstanceEntity.class),
                                          ProcessInstance.class.getName(), new ProcessInstanceEntityMapper());
    }

    @AfterEach
    void tearDown() {
        storage.clear();
    }

    @Test
    void test() {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();
        String subProcessId = processId + "_sub";
        String subProcessInstanceId = UUID.randomUUID().toString();
        ProcessInstance processInstance = TestUtils.createProcessInstance(processInstanceId, processId, null, null, ACTIVE.ordinal(), 0L);
        ProcessInstance subProcessInstance = TestUtils.createProcessInstance(subProcessInstanceId, subProcessId, processInstanceId, processId, COMPLETED.ordinal(), 1000L);
        storage.put(processInstanceId, processInstance);
        storage.put(subProcessInstanceId, subProcessInstance);

        queryAndAssert(assertWithId(), storage, singletonList(in("state", asList(ACTIVE.ordinal(), COMPLETED.ordinal()))), null, null, null, processInstanceId, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(equalTo("state", ACTIVE.ordinal())), null, null, null, processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(greaterThan("state", ACTIVE.ordinal())), null, null, null, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(greaterThanEqual("state", ACTIVE.ordinal())), null, null, null, processInstanceId, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(lessThan("state", COMPLETED.ordinal())), null, null, null, processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(lessThanEqual("state", COMPLETED.ordinal())), null, null, null, processInstanceId, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(between("state", ACTIVE.ordinal(), COMPLETED.ordinal())), null, null, null, processInstanceId, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(isNull("rootProcessInstanceId")), null, null, null, processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(notNull("rootProcessInstanceId")), null, null, null, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(contains("roles", "admin")), null, null, null, processInstanceId, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(containsAny("roles", asList("admin", "kogito"))), null, null, null, processInstanceId, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(containsAll("roles", asList("admin", "kogito"))), null, null, null);
        queryAndAssert(assertWithId(), storage, singletonList(like("processId", "*_sub")), null, null, null, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(and(asList(lessThan("start", Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()), lessThanEqual("start", Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli())))), null, null, null, processInstanceId, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(or(asList(equalTo("rootProcessInstanceId", processInstanceId), equalTo("start", processInstance.getStart().toInstant().toEpochMilli())))), null, null, null, processInstanceId, subProcessInstanceId);
        queryAndAssert(assertWithId(), storage, asList(isNull("roles"), isNull("end"), greaterThan("start", Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()), greaterThanEqual("start", Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli())), null, null, null);
        queryAndAssert(assertWithId(), storage, singletonList(equalTo("nodes.id", processInstance.getNodes().get(0).getId())), null, null, null, processInstanceId);

        queryAndAssert(assertWithIdInOrder(), storage, asList(in("id", asList(processInstanceId, subProcessInstanceId)), in("processId", asList(processId, subProcessId))), singletonList(orderBy("processId", SortDirection.ASC)), 1, 1, subProcessInstanceId);
        queryAndAssert(assertWithIdInOrder(), storage, null, singletonList(orderBy("processId", SortDirection.DESC)), null, null, subProcessInstanceId, processInstanceId);
        queryAndAssert(assertWithIdInOrder(), storage, null, null, 1, 1, subProcessInstanceId);
        queryAndAssert(assertWithIdInOrder(), storage, null, asList(orderBy("processId", SortDirection.ASC), orderBy("state", SortDirection.ASC)), 1, 1, subProcessInstanceId);
    }
}
