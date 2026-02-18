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
package org.kie.kogito.app.jobs.jpa.quarkus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.app.jobs.jpa.JPAJobStore;
import org.kie.kogito.app.jobs.jpa.model.JobDetailsEntity;
import org.kie.kogito.app.jobs.spi.JobContext;
import org.kie.kogito.jobs.service.model.JobDetails;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for verifying exception details persistence in Quarkus JPA Job Store.
 * Focuses on testing the newly added exceptionMessage and exceptionDetails fields.
 */
@QuarkusTest
public class QuarkusJPAJobStoreExceptionDetailsTest {

    @Inject
    EntityManager entityManager;

    private JPAJobStore jobStore;

    @BeforeEach
    public void init() {
        jobStore = new JPAJobStore();
    }

    @Test
    @Transactional
    public void testJobStoreDirectlyPersistsExceptionDetails() {
        // Given: A JobDetails with exception details
        JobDetails jobDetails = createJobDetailsWithException("direct-test-1");

        // When: Persisting through JobStore
        JobContext jobContext = new JobContext() {
            @Override
            public <T> T getContext() {
                return (T) entityManager;
            }
        };
        jobStore.persist(jobContext, jobDetails);
        entityManager.flush();
        entityManager.clear();

        // Then: Exception details are persisted
        JobDetailsEntity entity = entityManager.find(JobDetailsEntity.class, "direct-test-1");
        assertThat(entity).isNotNull();
        assertThat(entity.getExceptionMessage()).isEqualTo("java.lang.RuntimeException");
        assertThat(entity.getExceptionDetails()).isEqualTo("Test exception details");
    }

    @Test
    @Transactional
    public void testJobStoreUpdateClearsExceptionDetails() {
        // Given: A job with exception details exists
        JobDetails jobDetailsWithException = createJobDetailsWithException("direct-test-2");
        JobContext jobContext = new JobContext() {
            @Override
            public <T> T getContext() {
                return (T) entityManager;
            }
        };
        jobStore.persist(jobContext, jobDetailsWithException);
        entityManager.flush();
        entityManager.clear();

        // When: Updating with null exception details
        JobDetails jobDetailsWithoutException = createJobDetailsWithoutException("direct-test-2");
        jobStore.update(jobContext, jobDetailsWithoutException);
        entityManager.flush();
        entityManager.clear();

        // Then: Exception details are cleared
        JobDetailsEntity entity = entityManager.find(JobDetailsEntity.class, "direct-test-2");
        assertThat(entity).isNotNull();
        assertThat(entity.getExceptionMessage()).isNull();
        assertThat(entity.getExceptionDetails()).isNull();
    }

    @Test
    @Transactional
    public void testJobStoreFindReturnsExceptionDetails() {
        // Given: A job with exception details exists
        JobDetails jobDetails = createJobDetailsWithException("direct-test-3");
        JobContext jobContext = new JobContext() {
            @Override
            public <T> T getContext() {
                return (T) entityManager;
            }
        };
        jobStore.persist(jobContext, jobDetails);
        entityManager.flush();
        entityManager.clear();

        // When: Finding the job
        JobDetails found = jobStore.find(jobContext, "direct-test-3");

        // Then: Exception details are returned
        assertThat(found).isNotNull();
        assertThat(found.getExceptionDetails()).isNotNull();
        assertThat(found.getExceptionDetails().exceptionMessage()).isEqualTo("java.lang.RuntimeException");
        assertThat(found.getExceptionDetails().exceptionDetails()).isEqualTo("Test exception details");
    }

    @Transactional
    JobDetailsEntity findJobEntity(String jobId) {
        return entityManager.find(JobDetailsEntity.class, jobId);
    }

    private JobDetails createJobDetailsWithException(String jobId) {
        return TestJobDetailsFactory.createJobDetailsWithException(jobId);
    }

    private JobDetails createJobDetailsWithoutException(String jobId) {
        return TestJobDetailsFactory.createJobDetailsWithoutException(jobId);
    }
}
