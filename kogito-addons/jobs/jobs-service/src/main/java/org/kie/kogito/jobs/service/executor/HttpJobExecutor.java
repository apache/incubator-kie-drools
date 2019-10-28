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

package org.kie.kogito.jobs.service.executor;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.axle.core.Vertx;
import io.vertx.axle.ext.web.client.WebClient;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.converters.HttpConverters;
import org.kie.kogito.jobs.service.model.HTTPRequestCallback;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class HttpJobExecutor implements JobExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpJobExecutor.class);

    @Inject
    Vertx vertx;

    private WebClient client;

    @Inject
    HttpConverters httpConverters;

    @Inject
    ReactiveJobRepository jobRepository;

    @PostConstruct
    void initialize() {
        this.client = WebClient.create(vertx);
    }

    private CompletionStage<Boolean> executeCallback(HTTPRequestCallback request) {
        LOGGER.info("Executing callback {}", request);
        final URL url = httpConverters.convertURL(request.getUrl());
        return client.request(httpConverters.convertHttpMethod(request.getMethod()),
                              url.getPort(),
                              url.getHost(),
                              url.getPath())
                .send()
                .thenApplyAsync(response -> Optional
                        .ofNullable(response.statusCode())
                        .filter(new Integer(200)::equals)
                        .map(code -> Boolean.TRUE)
                        .orElse(Boolean.FALSE));
    }

    @Override
    public CompletionStage<Job> execute(Job job) {
        //Using just POST method for now
        final HTTPRequestCallback callback = HTTPRequestCallback.builder()
                .url(job.getCallbackEndpoint())
                .method(HTTPRequestCallback.HTTPMethod.POST)
                .build();

        return executeCallback(callback)
                .thenApply(result -> {
                    LOGGER.info("Response of executed job {} {}", result, job);
                    jobRepository.delete(job.getId());
                    return job;
                })
                //handle error
                .exceptionally(ex -> {
                    LOGGER.error("Error executing job " + job, ex);
                    return job;
                });
    }
}
