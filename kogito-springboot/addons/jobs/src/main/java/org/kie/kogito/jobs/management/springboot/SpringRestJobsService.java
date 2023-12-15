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

import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.jobs.management.RestJobsService;
import org.kie.kogito.jobs.service.api.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
public class SpringRestJobsService extends RestJobsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringRestJobsService.class);

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    @Autowired
    public SpringRestJobsService(
            @Value("${kogito.jobs-service.url}") String jobServiceUrl,
            @Value("${kogito.service.url}") String callbackEndpoint,
            @Autowired(required = false) RestTemplate restTemplate,
            @Autowired ObjectMapper objectMapper) {
        super(jobServiceUrl, callbackEndpoint, objectMapper);
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    SpringRestJobsService() {
        this(null, null, null, null);
    }

    @PostConstruct
    public void initialize() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
            LOGGER.debug("No RestTemplate found, creating a default one");
        }
    }

    @Override
    public String scheduleProcessJob(ProcessJobDescription description) {

        throw new UnsupportedOperationException("Scheduling for process jobs is not yet implemented");
    }

    @Override
    public String scheduleProcessInstanceJob(ProcessInstanceJobDescription description) {
        String callback = getCallbackEndpoint(description);
        LOGGER.debug("Job to be scheduled {} with callback URL {}", description, callback);
        final Job job = buildJob(description, callback);
        final HttpEntity<String> request = buildJobRequest(job);
        ResponseEntity<String> result = restTemplate.postForEntity(getJobsServiceUri(),
                request,
                String.class);
        if (result.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
            LOGGER.debug("Creating of the job {} done with status code {} ", job, result.getStatusCode());
        }
        return job.getId();
    }

    @Override
    public boolean cancelJob(String id) {

        try {
            restTemplate.delete(getJobsServiceUri() + "/{id}", id);

            return true;
        } catch (RestClientException e) {
            LOGGER.debug("Exception thrown during canceling of job {}", id, e);
            return false;
        }
    }

    private HttpEntity<String> buildJobRequest(Job job) {
        String json;
        try {
            json = objectMapper.writeValueAsString(job);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("It was not possible to create the http request for the job: " + job, e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }
}
