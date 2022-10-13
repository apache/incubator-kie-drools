/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.jobs.service.executor;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.kie.kogito.jobs.api.URIBuilder;
import org.kie.kogito.jobs.service.converters.HttpConverters;
import org.kie.kogito.jobs.service.exception.JobExecutionException;
import org.kie.kogito.jobs.service.model.HTTPRequestCallback;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.job.JobDetails;
import org.kie.kogito.jobs.service.model.job.Recipient.HTTPRecipient;
import org.kie.kogito.jobs.service.stream.JobStreams;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

@ApplicationScoped
public class HttpJobExecutor implements JobExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpJobExecutor.class);

    @Inject
    Vertx vertx;

    private WebClient client;

    @Inject
    HttpConverters httpConverters;

    @Inject
    JobStreams jobStreams;

    @PostConstruct
    void initialize() {
        this.client = WebClient.create(vertx);
    }

    private Uni<HttpResponse<Buffer>> executeCallback(HTTPRequestCallback request) {
        LOGGER.debug("Executing callback {}", request);
        final URI uri = URIBuilder.toURI(request.getUrl());
        final HttpRequest<Buffer> clientRequest = client.request(httpConverters.convertHttpMethod(request.getMethod()),
                uri.getPort(),
                uri.getHost(),
                uri.getPath());
        Optional.ofNullable(request.getQueryParams())
                .ifPresent(params -> clientRequest.queryParams().addAll(params));
        return clientRequest.send();
    }

    private <T extends JobExecutionResponse> Uni<T> handleResponse(T response) {
        LOGGER.debug("handle response {}", response);
        return Uni.createFrom().item(response)
                .onItem().transform(JobExecutionResponse::getCode)
                .onItem().transform(Integer::valueOf)
                .chain(code -> Response.Status.Family.SUCCESSFUL.equals(Response.Status.Family.familyOf(code))
                        ? handleSuccess(response)
                        : handleError(response));
    }

    private <T extends JobExecutionResponse> Uni<T> handleError(T response) {
        LOGGER.info("handle error {}", response);
        return Uni.createFrom().item(response)
                .onItem().invoke(jobStreams::publishJobError)
                .onItem().invoke(r -> LOGGER.debug("Error executing job {}.", r));
    }

    private <T extends JobExecutionResponse> Uni<T> handleSuccess(T response) {
        LOGGER.info("handle success {}", response);
        return Uni.createFrom().item(response)
                .onItem().invoke(jobStreams::publishJobSuccess)
                .onItem().invoke(r -> LOGGER.debug("Success executing job {}.", r));
    }

    @Override
    public CompletionStage<JobDetails> execute(CompletionStage<JobDetails> futureJob) {
        return Uni.createFrom().completionStage(futureJob)
                .chain(job -> {
                    //Using just POST method for now
                    final String callbackEndpoint = getCallbackEndpoint(job);
                    final String limit = getLimit(job);
                    final HTTPRequestCallback callback = buildCallbackRequest(callbackEndpoint, limit);
                    return executeCallback(callback)
                            .onItem().transform(response -> JobExecutionResponse.builder()
                                    .message(response.statusMessage())
                                    .code(String.valueOf(response.statusCode()))
                                    .now()
                                    .jobId(job.getId())
                                    .build())
                            .chain(this::handleResponse)
                            .onItem().transform(response -> job)
                            .onFailure().transform(ex -> new JobExecutionException(job, ex.getMessage()));
                })
                .onFailure(JobExecutionException.class).invoke(ex -> {
                    JobDetails job = ((JobExecutionException) ex).getJob();
                    LOGGER.error("Generic error executing job {}", job, ex);
                    jobStreams.publishJobError(JobExecutionResponse.builder()
                            .message(ex.getMessage())
                            .now()
                            .jobId(job.getId())
                            .build());
                })
                .convert().toCompletionStage();
    }

    private HTTPRequestCallback buildCallbackRequest(String callbackEndpoint, String limit) {
        return HTTPRequestCallback.builder()
                .url(callbackEndpoint)
                .method(HTTPRequestCallback.HTTPMethod.POST)
                //in case of repeatable jobs add the limit parameter
                .addQueryParam("limit", limit)
                .build();
    }

    private String getLimit(JobDetails job) {
        return Optional.ofNullable(job.getTrigger())
                .filter(IntervalTrigger.class::isInstance)
                .map(interval -> getRepeatableJobCountDown(job))
                .map(String::valueOf)
                .orElse(null);
    }

    private String getCallbackEndpoint(JobDetails job) {
        return Optional.ofNullable(job.getRecipient())
                .filter(HTTPRecipient.class::isInstance)
                .map(HTTPRecipient.class::cast)
                .map(HTTPRecipient::getEndpoint)
                .orElseThrow(() -> new IllegalArgumentException("Callback Endpoint is null for job " + job));
    }

    private int getRepeatableJobCountDown(JobDetails job) {
        IntervalTrigger trigger = (IntervalTrigger) job.getTrigger();
        return trigger.getRepeatLimit() - trigger.getRepeatCount();
    }

    WebClient getClient() {
        return client;
    }
}
