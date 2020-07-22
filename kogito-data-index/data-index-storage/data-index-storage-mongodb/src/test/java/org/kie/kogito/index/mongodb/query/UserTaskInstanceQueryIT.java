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
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.mongodb.TestUtils;
import org.kie.kogito.index.mongodb.model.UserTaskInstanceEntity;
import org.kie.kogito.index.mongodb.model.UserTaskInstanceEntityMapper;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.SortDirection;
import org.kie.kogito.persistence.mongodb.MongoServerTestResource;
import org.kie.kogito.persistence.mongodb.client.MongoClientManager;
import org.kie.kogito.persistence.mongodb.storage.MongoStorage;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.kie.kogito.index.Constants.USER_TASK_INSTANCES_STORAGE;
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
class UserTaskInstanceQueryIT extends QueryTestBase<String, UserTaskInstance> {

    @Inject
    MongoClientManager mongoClientManager;

    Storage<String, UserTaskInstance> storage;

    @BeforeEach
    void setUp() {
        this.storage = new MongoStorage<>(mongoClientManager.getCollection(USER_TASK_INSTANCES_STORAGE, UserTaskInstanceEntity.class),
                                          mongoClientManager.getReactiveCollection(USER_TASK_INSTANCES_STORAGE, UserTaskInstanceEntity.class),
                                          UserTaskInstance.class.getName(), new UserTaskInstanceEntityMapper());
    }

    @AfterEach
    void tearDown() {
        storage.clear();
    }

    @Test
    void test() {
        String taskId1 = UUID.randomUUID().toString();
        String processInstanceId1 = UUID.randomUUID().toString();
        String taskId2 = UUID.randomUUID().toString();
        String processInstanceId2 = UUID.randomUUID().toString();

        UserTaskInstance userTaskInstance1 = TestUtils.createUserTaskInstance(taskId1, processInstanceId1, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(), RandomStringUtils.randomAlphabetic(10), "InProgress", 0L);
        UserTaskInstance userTaskInstance2 = TestUtils.createUserTaskInstance(taskId2, processInstanceId2, RandomStringUtils.randomAlphabetic(5), null, null, "Completed", 1000L);
        storage.put(taskId1, userTaskInstance1);
        storage.put(taskId2, userTaskInstance2);

        queryAndAssert(assertWithId(), storage, singletonList(in("state", asList("InProgress", "Completed"))), null, null, null, taskId1, taskId2);
        queryAndAssert(assertWithId(), storage, singletonList(equalTo("state", "InProgress")), null, null, null, taskId1);
        queryAndAssert(assertWithId(), storage, singletonList(greaterThan("started", Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli())), null, null, null);
        queryAndAssert(assertWithId(), storage, singletonList(greaterThanEqual("completed", Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli())), null, null, null, taskId1, taskId2);
        queryAndAssert(assertWithId(), storage, singletonList(lessThan("completed", Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli())), null, null, null);
        queryAndAssert(assertWithId(), storage, singletonList(lessThanEqual("started", Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli())), null, null, null, taskId1, taskId2);
        queryAndAssert(assertWithId(), storage, singletonList(between("completed", Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli(), Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli())), null, null, null, taskId1, taskId2);
        queryAndAssert(assertWithId(), storage, singletonList(isNull("rootProcessInstanceId")), null, null, null, taskId2);
        queryAndAssert(assertWithId(), storage, singletonList(notNull("rootProcessInstanceId")), null, null, null, taskId1);
        queryAndAssert(assertWithId(), storage, singletonList(contains("id", taskId1)), null, null, null, taskId1);
        queryAndAssert(assertWithId(), storage, singletonList(containsAny("processInstanceId", asList(processInstanceId1, processInstanceId2))), null, null, null, taskId1, taskId2);
        queryAndAssert(assertWithId(), storage, singletonList(containsAll("processInstanceId", asList(processInstanceId1, processInstanceId2))), null, null, null);
        queryAndAssert(assertWithId(), storage, singletonList(like("state", "*ss")), null, null, null, taskId1);
        queryAndAssert(assertWithId(), storage, singletonList(and(asList(equalTo("id", taskId1), equalTo("processInstanceId", processInstanceId1)))), null, null, null, taskId1);
        queryAndAssert(assertWithId(), storage, singletonList(or(asList(equalTo("id", taskId1), equalTo("id", taskId2)))), null, null, null, taskId1, taskId2);
        queryAndAssert(assertWithId(), storage, asList(equalTo("id", taskId1), equalTo("processInstanceId", processInstanceId2)), null, null, null);

        queryAndAssert(assertWithIdInOrder(), storage, asList(in("id", asList(taskId1, taskId2)), in("processInstanceId", asList(processInstanceId1, processInstanceId2))), singletonList(orderBy("state", SortDirection.ASC)), 1, 1, taskId1);
        queryAndAssert(assertWithIdInOrder(), storage, null, singletonList(orderBy("state", SortDirection.DESC)), null, null, taskId1, taskId2);
        queryAndAssert(assertWithIdInOrder(), storage, null, null, 1, 1, taskId2);
        queryAndAssert(assertWithIdInOrder(), storage, null, asList(orderBy("state", SortDirection.ASC), orderBy("completed", SortDirection.ASC)), 1, 1, taskId1);
    }
}
