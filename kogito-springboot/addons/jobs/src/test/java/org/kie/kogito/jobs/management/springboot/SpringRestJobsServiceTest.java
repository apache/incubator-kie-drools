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
package org.kie.kogito.jobs.management.springboot;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.management.RestJobsServiceTest;
import org.kie.kogito.jobs.service.api.Job;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.jobs.service.api.serlialization.SerializationUtils.registerDescriptors;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringRestJobsServiceTest extends RestJobsServiceTest<SpringRestJobsService> {

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    @Override
    public SpringRestJobsService createJobService(String jobServiceUrl, String callbackUrl) {
        this.objectMapper = new Jackson2ObjectMapperBuilder().build();
        registerDescriptors(objectMapper);
        SpringRestJobsService jobsService = new SpringRestJobsService(jobServiceUrl, callbackUrl, restTemplate, objectMapper);
        jobsService.initialize();
        return jobsService;
    }

    @Test
    void scheduleProcessInstanceJob() throws Exception {
        when(restTemplate.postForEntity(any(URI.class), any(HttpEntity.class), eq(String.class))).thenReturn(ResponseEntity.ok().build());
        ProcessInstanceJobDescription processInstanceJobDescription = buildProcessInstanceJobDescription();
        tested.scheduleJob(processInstanceJobDescription);
        ArgumentCaptor<HttpEntity<String>> jobArgumentCaptor = forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(eq(tested.getJobsServiceUri()),
                jobArgumentCaptor.capture(),
                eq(String.class));
        HttpEntity<String> request = jobArgumentCaptor.getValue();
        String json = request.getBody();
        Job job = objectMapper.readValue(json, Job.class);
        assertExpectedJob(job, processInstanceJobDescription.id());
    }

    @Test
    void cancelJob() {
        tested.cancelJob(JOB_ID);
        verify(restTemplate).delete(tested.getJobsServiceUri() + "/{id}", JOB_ID);
    }
}
