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
package org.kie.kogito.jobs.service.repository.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.Recipient;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.stream.JobStreams;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.jobs.service.utils.FunctionsUtil;
import org.kie.kogito.timer.impl.PointInTimeTrigger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@SuppressWarnings("java:S5786")
public abstract class BaseJobRepositoryTest {

    public static final String ID = UUID.randomUUID().toString();

    private JobDetails job;

    @BeforeEach
    public void setUp() throws Exception {
        createAndSaveJob(ID);
    }

    public JobStreams mockJobStreams() {
        final JobStreams mock = mock(JobStreams.class);
        lenient().when(mock.publishJobStatusChange(any(JobDetails.class))).thenAnswer(a -> a.getArgument(0));
        lenient().when(mock.publishJobSuccess(any(JobExecutionResponse.class))).thenAnswer(a -> a.getArgument(0));
        lenient().when(mock.publishJobError(any(JobExecutionResponse.class))).thenAnswer(a -> a.getArgument(0));
        return mock;
    }

    public abstract ReactiveJobRepository tested();

    @Test
    void testSaveAndGet() throws ExecutionException, InterruptedException {
        JobDetails scheduledJob = tested().get(ID).toCompletableFuture().get();
        assertThat(scheduledJob).isEqualTo(job);
        JobDetails notFound = tested().get(UUID.randomUUID().toString()).toCompletableFuture().get();
        assertThat(notFound).isNull();
    }

    private void createAndSaveJob(String id) throws Exception {
        job = JobDetails.builder()
                .id(id)
                .trigger(new PointInTimeTrigger(System.currentTimeMillis(), null, null))
                .priority(1)
                .recipient(new RecipientInstance(HttpRecipient.builder()
                        .forStringPayload().url("url")
                        .payload(HttpRecipientStringPayloadData.from("payload test"))
                        .build()))
                .build();
        tested().save(job).toCompletableFuture().get();
    }

    @Test
    void testExists() throws ExecutionException, InterruptedException {
        Boolean exists = tested().exists(ID).toCompletableFuture().get();
        assertThat(exists).isTrue();
        Boolean notFound = tested().exists(UUID.randomUUID().toString()).toCompletableFuture().get();
        assertThat(notFound).isFalse();
    }

    @Test
    void testDelete() throws ExecutionException, InterruptedException {
        JobDetails scheduledJob = tested().delete(ID).toCompletableFuture().get();
        assertThat(scheduledJob).isEqualTo(job);
        JobDetails notFound = tested().get(ID).toCompletableFuture().get();
        assertThat(notFound).isNull();
    }

    @Test
    void testFindAll() throws ExecutionException, InterruptedException {
        List<JobDetails> jobs = tested().findAll().toList().run().toCompletableFuture().get();
        assertThat(jobs.size()).isEqualTo(1);
        assertThat(jobs.get(0)).isEqualTo(job);
    }

    @Test
    void testFindByStatusBetweenDates() throws ExecutionException, InterruptedException {
        List<JobDetails> jobs = IntStream.rangeClosed(1, 10).boxed()
                .map(id -> JobDetails.builder()
                        .status(JobStatus.SCHEDULED)
                        .id(String.valueOf(id))
                        .priority(id)
                        .trigger(new PointInTimeTrigger(DateUtil.now().plusMinutes(id).toInstant().toEpochMilli(), null, null))
                        .priority(id)
                        .build())
                .peek(j -> FunctionsUtil.unchecked((t) -> tested().save(j).toCompletableFuture().get()).apply(null))
                .collect(Collectors.toList());

        final List<JobDetails> fetched = tested().findByStatusBetweenDatesOrderByPriority(DateUtil.now(),
                DateUtil.now().plusMinutes(5).plusSeconds(1),
                JobStatus.SCHEDULED)
                .toList()
                .run()
                .toCompletableFuture()
                .get();

        assertThat(fetched.size()).isEqualTo(5);

        IntStream.rangeClosed(0, 4).forEach(
                i -> assertThat(fetched.get(i)).isEqualTo(jobs.get(fetched.size() - 1 - i)));

        //not found test
        List<JobDetails> fetchedNotFound = tested().findByStatusBetweenDatesOrderByPriority(DateUtil.now(),
                DateUtil.now().plusMinutes(5).plusSeconds(1),
                JobStatus.CANCELED)
                .toList()
                .run()
                .toCompletableFuture()
                .get();

        assertThat(fetchedNotFound.size()).isZero();

        fetchedNotFound = tested().findByStatusBetweenDatesOrderByPriority(DateUtil.now().plusDays(1),
                DateUtil.now().plusDays(2),
                JobStatus.SCHEDULED)
                .toList()
                .run()
                .toCompletableFuture()
                .get();

        assertThat(fetchedNotFound.size()).isZero();
    }

    @Test
    void testMergeCallbackEndpoint() throws Exception {
        String id = UUID.randomUUID().toString();
        createAndSaveJob(id);
        final String newCallbackEndpoint = "http://localhost/newcallback";
        final Recipient recipient = new RecipientInstance(HttpRecipient.builder().forStringPayload().url(newCallbackEndpoint).build());
        final JobDetails toMerge = JobDetails.builder()
                .id(id)
                .recipient(recipient)
                .build();

        JobDetails merged = tested().merge(id, toMerge).toCompletableFuture().get();
        assertThat(merged.getRecipient()).isEqualTo(recipient);
        assertThat(merged.getId()).isEqualTo(job.getId());
        assertThat(merged.getTrigger().hasNextFireTime()).isEqualTo(job.getTrigger().hasNextFireTime());
    }
}
