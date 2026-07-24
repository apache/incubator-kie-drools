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
package org.kie.kogito.jobs.service.resource.v2;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.jobs.service.resource.CommonBaseJobResourceTest;
import org.kie.kogito.jobs.service.resource.RestApiConstants;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseJobResourceV2Test extends CommonBaseJobResourceTest {

    @Override
    protected String getCreatePath() {
        return RestApiConstants.V2 + RestApiConstants.JOBS_PATH;
    }

    protected String getGetJobQuery(String jobId) {
        return String.format(RestApiConstants.V2 + RestApiConstants.JOBS_PATH + "/%s", jobId);
    }

    public static final String JOB_ID = "JOB_ID";

    public static final OffsetDateTime OVERDUE_START_TIME = OffsetDateTime.parse("2023-01-24T15:20:25.001+01:00");

    @Test
    void createOverdueJob() throws Exception {
        createOverdueJob(JOB_ID);
    }

    @ParameterizedTest
    @MethodSource("createOverdueJobMultipleParams")
    void createOverdueJobMultiple(String jobId) throws Exception {
        createOverdueJob(jobId);
    }

    protected static List<String> createOverdueJobMultipleParams() {
        return Arrays.asList("JOB_ID_R1", "JOB_ID_R2", "JOB_ID_R3", "JOB_ID_R4", "JOB_ID_R5", "JOB_ID_R6", "JOB_ID_R7",
                "JOB_ID_R8", "JOB_ID_R9", "JOB_ID_R10");
    }

    protected void createOverdueJob(String jobId) throws Exception {
        scheduler.setForceExecuteExpiredJobs(true);
        Job job = Job.builder()
                .id(jobId)
                .correlationId(jobId)
                .schedule(TimerSchedule.builder().startTime(OVERDUE_START_TIME).build())
                .recipient(HttpRecipient.builder()
                        .forStringPayload()
                        .url(getCallbackEndpoint())
                        .build())
                .build();

        Job response = createJob(job);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(jobId);
        assertJobHasFinished(jobId, 10);
    }

    protected Job createJob(Job job) throws Exception {
        String response = create(objectMapper.writeValueAsString(job))
                .statusCode(OK)
                .extract()
                .body()
                .asString();
        return objectMapper.readValue(response, Job.class);
    }

    @Test
    public void testCreateGetAndDelete() throws Exception {
        Job job = buildJob(JOB_ID, OffsetDateTime.now().plusMinutes(10));
        Job created = createJob(job);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(job.getId());
        assertThat(created.getState()).isNotNull();
        assertThat(job.getState()).isNull();

        Job getJob = getJob(created.getId(), Job.class);
        assertThat(getJob.getId()).isEqualTo(created.getId());

        deleteJob(created.getId());
        getJob(created.getId(), Job.class, 404);
    }

    private Job buildJob(String jobId, OffsetDateTime time) {
        return Job.builder()
                .id(jobId)
                .correlationId(jobId)
                .schedule(TimerSchedule.builder().startTime(time).build())
                .recipient(HttpRecipient.builder()
                        .forStringPayload()
                        .url(getCallbackEndpoint())
                        .build())
                .build();
    }
}
