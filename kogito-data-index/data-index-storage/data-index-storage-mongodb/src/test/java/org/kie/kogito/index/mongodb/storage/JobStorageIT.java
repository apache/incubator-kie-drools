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

package org.kie.kogito.index.mongodb.storage;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.mongodb.model.JobEntity;
import org.kie.kogito.index.mongodb.model.JobEntityMapper;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.mongodb.client.MongoClientManager;
import org.kie.kogito.persistence.mongodb.storage.MongoStorage;
import org.kie.kogito.testcontainers.quarkus.MongoDBQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.kie.kogito.index.storage.Constants.JOBS_STORAGE;

@QuarkusTest
@QuarkusTestResource(MongoDBQuarkusTestResource.class)
class JobStorageIT extends StorageTestBase<String, Job> {

    @Inject
    MongoClientManager mongoClientManager;

    Storage<String, Job> storage;

    @BeforeEach
    void setUp() {
        this.storage = new MongoStorage<>(mongoClientManager.getCollection(JOBS_STORAGE, JobEntity.class),
                Job.class.getName(), new JobEntityMapper());
    }

    @AfterEach
    void tearDown() {
        storage.clear();
    }

    @Test
    void testCache() {
        String jobId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();

        Job job1 = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "EXPECTED", 0L);
        Job job2 = TestUtils
                .createJob(jobId, processInstanceId, RandomStringUtils.randomAlphabetic(5), UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "SCHEDULED", 1000L);
        testStorage(storage, jobId, job1, job2);
    }
}
