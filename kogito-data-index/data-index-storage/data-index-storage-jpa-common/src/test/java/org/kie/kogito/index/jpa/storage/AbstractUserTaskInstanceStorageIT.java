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
package org.kie.kogito.index.jpa.storage;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.jpa.model.UserTaskInstanceEntityRepository;
import org.kie.kogito.index.test.TestUtils;

import jakarta.inject.Inject;

public abstract class AbstractUserTaskInstanceStorageIT {

    @Inject
    UserTaskInstanceEntityRepository repository;

    @Test
    public void testUserTaskInstanceEntity() {
        String taskId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();
        TestUtils
                .createUserTaskInstance(taskId, processInstanceId, RandomStringUtils.randomAlphabetic(5),
                        UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "InProgress", 0L);
        TestUtils
                .createUserTaskInstance(taskId, processInstanceId, RandomStringUtils.randomAlphabetic(5),
                        UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "Completed", 1000L);
    }

}
