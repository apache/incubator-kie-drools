/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.management.quarkus;

import java.net.URI;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.Vertx;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.management.RestJobsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VertxJobsService extends RestJobsService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VertxJobsService.class);

    private Vertx vertx;

    private Instance<WebClient> providedWebClient;

    private WebClient client;


    @Inject
    public VertxJobsService(@ConfigProperty(name = "kogito.jobs-service.url") String jobServiceUrl,
                            @ConfigProperty(name = "kogito.service.url") String callbackEndpoint,
                            Vertx vertx,
                            Instance<WebClient> providedWebClient) {
        super(jobServiceUrl, callbackEndpoint);
        this.vertx = vertx;
        this.providedWebClient = providedWebClient;
    }

    VertxJobsService() {
        this(null, null, null, null);
    }

    @PostConstruct
    void initialize() {
        DatabindCodec.mapper().disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);        
        DatabindCodec.mapper().registerModule(new JavaTimeModule());
        DatabindCodec.mapper().findAndRegisterModules();
        
        DatabindCodec.prettyMapper().registerModule(new JavaTimeModule());
        DatabindCodec.prettyMapper().findAndRegisterModules();
        DatabindCodec.prettyMapper().disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        if (providedWebClient.isResolvable()) {
            this.client = providedWebClient.get();
            LOGGER.debug("Using provided web client instance");
        } else {
            final URI jobServiceURL = getJobsServiceUri();
            this.client = WebClient.create(vertx,
                                           new WebClientOptions()
                                                   .setDefaultHost(jobServiceURL.getHost())
                                                   .setDefaultPort(jobServiceURL.getPort()));
            LOGGER.debug("Creating new instance of web client for host {} and port {}", jobServiceURL.getHost(), jobServiceURL.getPort());
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
        final Job job = JobBuilder.builder()
                .id(description.id())
                .expirationTime(description.expirationTime().get())
                .repeatInterval(description.expirationTime().repeatInterval())
                .repeatLimit(description.expirationTime().repeatLimit())
                .priority(0)
                .callbackEndpoint(callback)
                .processId(description.processId())
                .processInstanceId(description.processInstanceId())
                .rootProcessId(description.rootProcessId())
                .rootProcessInstanceId(description.rootProcessInstanceId())
                .build();

        client.post(JOBS_PATH).sendJson(job, res -> {
            
            if (res.succeeded() && res.result().statusCode() == 200) {
                LOGGER.debug("Creating of the job {} done with status code {} ", job, res.result().statusCode());
            } else {
                LOGGER.error("Scheduling of job {} failed with response code {}", job, res.result().statusCode(), res.cause());
            }
        });
        
        return job.getId();
    }

    @Override
    public boolean cancelJob(String id) {
        client.delete(JOBS_PATH + "/" + id).send(res -> {
            if (res.succeeded() && (res.result().statusCode() == 200 || res.result().statusCode() == 404)) {
                LOGGER.debug("Canceling of the job {} done with status code {} ", id, res.result().statusCode());
            } else {
                LOGGER.error("Canceling of job {} failed with response code {}", id, res.result().statusCode(), res.cause());
            }
        });
        
        return true;
    }
}
