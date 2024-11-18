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
package org.kie.kogito.jobs.management;

import java.net.URI;
import java.util.Objects;

import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.api.URIBuilder;
import org.kie.kogito.jobs.service.api.Job;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.jobs.api.JobCallbackResourceDef.buildCallbackPatternJob;
import static org.kie.kogito.jobs.api.JobCallbackResourceDef.buildCallbackURI;

public abstract class RestJobsService implements JobsService {

    @SuppressWarnings("squid:S1075")
    public static final String JOBS_PATH = "/v2/jobs";

    private URI jobsServiceUri;
    private String callbackEndpoint;
    private ObjectMapper objectMapper;

    public RestJobsService(String jobServiceUrl, String callbackEndpoint, ObjectMapper objectMapper) {
        this.jobsServiceUri = Objects.nonNull(jobServiceUrl) ? buildJobsServiceURI(jobServiceUrl) : null;
        this.callbackEndpoint = callbackEndpoint;
        this.objectMapper = objectMapper;
    }

    public String getCallbackEndpoint(JobDescription description) {
        return buildCallbackURI(description, callbackEndpoint);
    }

    private URI buildJobsServiceURI(String jobServiceUrl) {
        return URIBuilder.toURI(jobServiceUrl + JOBS_PATH);
    }

    public URI getJobsServiceUri() {
        return jobsServiceUri;
    }

    public Job buildJob(JobDescription description, String callback) {
        return buildCallbackPatternJob(description, callback, objectMapper);
    }
}
