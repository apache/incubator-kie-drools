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

import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.OnOverflow;
import io.vertx.axle.core.Vertx;
import io.vertx.axle.core.buffer.Buffer;
import io.vertx.axle.ext.web.client.HttpResponse;
import io.vertx.axle.ext.web.client.WebClient;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.stream.AvailableStreams;
import org.kie.kogito.jobs.service.converters.HttpConverters;
import org.kie.kogito.jobs.service.model.HTTPRequestCallback;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
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

    /**
     * Publish on Stream of Job Error events
     */
    @Inject
    @Channel(AvailableStreams.JOB_ERROR)
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10000)
    Emitter<JobExecutionResponse> jobErrorEmitter;

    /**
     * Publish on Stream of Job Success events
     */
    @Inject
    @Channel(AvailableStreams.JOB_SUCCESS)
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10000)
    Emitter<JobExecutionResponse> jobSuccessEmitter;

    @PostConstruct
    void initialize() {
        this.client = WebClient.create(vertx);
    }

    private CompletionStage<HttpResponse<Buffer>> executeCallback(HTTPRequestCallback request) {
        LOGGER.info("Executing callback {}", request);
        final URL url = httpConverters.convertURL(request.getUrl());
        return client.request(httpConverters.convertHttpMethod(request.getMethod()),
                              url.getPort(),
                              url.getHost(),
                              url.getPath())
                .send();
    }

    private String getResponseCode(HttpResponse<Buffer> response) {
        return Optional.ofNullable(response.statusCode())
                .map(String::valueOf)
                .orElse(null);
    }

    private PublisherBuilder<JobExecutionResponse> handleResponse(JobExecutionResponse response) {
        LOGGER.info("handle response {}", response);
        return ReactiveStreams.of(response)
                .map(JobExecutionResponse::getCode)
                .flatMap(code -> code.equals("200")
                        ? handleSuccess(response)
                        : handleError(response));
    }

    private PublisherBuilder<JobExecutionResponse> handleError(JobExecutionResponse response) {
        LOGGER.info("handle error {}", response);
        return ReactiveStreams.of(response)
                .peek(jobErrorEmitter::send)
                .peek(r -> LOGGER.info("Error executing job {}.", r));
    }

    private PublisherBuilder<JobExecutionResponse> handleSuccess(JobExecutionResponse response) {
        LOGGER.info("handle success {}", response);
        return ReactiveStreams.of(response)
                .peek(jobSuccessEmitter::send)
                .peek(r -> LOGGER.info("Success executing job {}.", r));
    }

    @Override
    public CompletionStage<Job> execute(Job job) {
        //Using just POST method for now
        final HTTPRequestCallback callback = HTTPRequestCallback.builder()
                .url(job.getCallbackEndpoint())
                .method(HTTPRequestCallback.HTTPMethod.POST)
                .build();

        return ReactiveStreams.fromCompletionStage(executeCallback(callback))
                .map(response -> JobExecutionResponse.builder()
                        .message(response.statusMessage())
                        .code(getResponseCode(response))
                        .now()
                        .jobId(job.getId())
                        .build())
                .flatMap(this::handleResponse)
                .findFirst()
                .run()
                .thenApply(response -> job)
                .exceptionally((ex) -> {
                    LOGGER.error("Generic error executing job {}", job, ex);
                    jobErrorEmitter.send(JobExecutionResponse.builder()
                                                 .message(ex.getMessage())
                                                 .now()
                                                 .jobId(job.getId())
                                                 .build());
                    return job;
                });
    }
}