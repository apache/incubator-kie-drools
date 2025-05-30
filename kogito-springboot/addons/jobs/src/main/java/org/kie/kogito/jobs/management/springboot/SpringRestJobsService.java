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

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.management.RestJobsService;
import org.kie.kogito.jobs.service.api.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
    public String scheduleJob(JobDescription description) {
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

    @Override
    public String rescheduleJob(JobDescription jobDescription) {
        String callback = getCallbackEndpoint(jobDescription);
        LOGGER.debug("Job to be rescheduled {} with callback URL {}", jobDescription, callback);
        final Job job = buildJob(jobDescription, callback);
        final HttpEntity<String> request = buildJobRequest(job);
        ResponseEntity<String> response = restTemplate.exchange(
                getJobsServiceUri(),
                HttpMethod.PATCH,
                request,
                String.class);
        if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
            LOGGER.debug("Rescheduling of the job {} done with status code {} ", job, response.getStatusCode());
        }
        return "Job Rescheduled";
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

    private HttpEntity<String> buildJobRequest(String id) {
        String json;
        try {
            Map<String, String> job = new HashMap<>();
            job.put("id", id);
            json = objectMapper.writeValueAsString(job);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("It was not possible to create the http request for the job id: " + id, e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }
}
