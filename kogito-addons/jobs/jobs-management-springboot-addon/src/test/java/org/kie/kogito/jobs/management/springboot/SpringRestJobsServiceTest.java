/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.management.springboot;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.jobs.api.Job;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpringRestJobsServiceTest {

    public static final String CALLBACK_URL = "http://localhost";
    public static final String JOB_SERVICE_URL = "http://localhost:8085";

    private SpringRestJobsService tested;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        this.tested = new SpringRestJobsService(JOB_SERVICE_URL, CALLBACK_URL, restTemplate);
        tested.initialize();
    }

    @Test
    void testScheduleProcessJob() {
        ProcessJobDescription processJobDescription = ProcessJobDescription.of(ExactExpirationTime.now(),
                                                                               1,
                                                                               "processId");
        assertThatThrownBy(() -> tested.scheduleProcessJob(processJobDescription))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testScheduleProcessInstanceJob() {
        when(restTemplate.postForEntity(any(URI.class), any(Job.class), eq(String.class))).thenReturn(ResponseEntity.ok().build());
        ProcessInstanceJobDescription processInstanceJobDescription = ProcessInstanceJobDescription.of(123,
                                                                                                       ExactExpirationTime.now(),
                                                                                                       "processInstanceId",
                                                                                                       "processId");
        tested.scheduleProcessInstanceJob(processInstanceJobDescription);
        ArgumentCaptor<Job> jobArgumentCaptor = forClass(Job.class);
        verify(restTemplate).postForEntity(eq(tested.getJobsServiceUri()),
                                           jobArgumentCaptor.capture(),
                                           eq(String.class));
        Job job = jobArgumentCaptor.getValue();
        assertThat(job.getId()).isEqualTo(processInstanceJobDescription.id());
    }

    @Test
    void testCancelJob() {
        tested.cancelJob("123");
        verify(restTemplate).delete(tested.getJobsServiceUri() + "/{id}", "123");
    }
}