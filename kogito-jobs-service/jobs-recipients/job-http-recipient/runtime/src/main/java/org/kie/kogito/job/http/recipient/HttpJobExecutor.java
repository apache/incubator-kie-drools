/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.job.http.recipient;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.job.http.recipient.converters.HttpConverters;
import org.kie.kogito.jobs.api.URIBuilder;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.exception.JobExecutionException;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.Recipient;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

@ApplicationScoped
public class HttpJobExecutor implements JobExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpJobExecutor.class);

    @ConfigProperty(name = "quarkus.job.recipient.http.timeout-in-millis", defaultValue = "5000")
    long timeout;

    @Inject
    Vertx vertx;
    private WebClient client;

    @Inject
    ObjectMapper objectMapper;

    @PostConstruct
    void initialize() {
        this.client = WebClient.create(vertx);
    }

    private Uni<HttpResponse<Buffer>> executeCallback(HTTPRequestCallback request) {
        LOGGER.debug("Executing callback {}", request);
        final URI uri = URIBuilder.toURI(request.getUrl());
        final HttpRequest<Buffer> clientRequest = client.request(HttpConverters.convertHttpMethod(request.getMethod()),
                uri.getPort(),
                uri.getHost(),
                uri.getPath()).timeout(timeout);
        clientRequest.queryParams().addAll(filterEntries(request.getQueryParams()));
        clientRequest.headers().addAll(filterEntries(request.getHeaders()));
        return request.getBody()
                .map(body -> clientRequest.sendBuffer(buildBuffer(body)))
                .orElse(clientRequest.send());
    }

    private Buffer buildBuffer(Object body) {
        if (body instanceof String) {
            return Buffer.buffer((String) body);
        } else if (body instanceof byte[]) {
            return Buffer.buffer(((byte[]) body));
        } else if (body instanceof JsonNode) {
            try {
                return Buffer.buffer(objectMapper.writeValueAsBytes(body));
            } catch (Exception e) {
                throw new RuntimeException("Failed to encode body as JSON: " + e.getMessage(), e);
            }
        }
        throw new IllegalArgumentException("Unexpected body type: " + body.getClass());
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
        return Uni.createFrom().item(response)
                .onItem().invoke(r -> LOGGER.debug("Error executing job {}.", r))
                .onItem().failWith(() -> new JobExecutionException(response.getJobId(), "Response error when executing HTTP request for " + response));
    }

    private <T extends JobExecutionResponse> Uni<T> handleSuccess(T response) {
        return Uni.createFrom().item(response)
                .onItem().invoke(r -> LOGGER.debug("Success executing job {}.", r));
    }

    @Override
    public Uni<JobExecutionResponse> execute(JobDetails jobDetails) {
        return Uni.createFrom().item(jobDetails)
                .chain(job -> {
                    final HttpRecipient<?> httpRecipient = getHttpRecipient(job);
                    final String limit = getLimit(job);
                    final HTTPRequestCallback callback = buildCallbackRequest(httpRecipient, limit);
                    return executeCallback(callback)
                            .onItem().transform(response -> JobExecutionResponse.builder()
                                    .message(response.bodyAsString())
                                    .code(String.valueOf(response.statusCode()))
                                    .now()
                                    .jobId(job.getId())
                                    .build())
                            .chain(this::handleResponse);
                });
    }

    private HTTPRequestCallback buildCallbackRequest(HttpRecipient recipient, String limit) {
        return HTTPRequestCallback.builder()
                .url(recipient.getUrl())
                .method(recipient.getMethod())
                .headers(recipient.getHeaders())
                .queryParams(recipient.getQueryParams())
                //in case of repeatable jobs add the limit parameter, override if already present.
                .addQueryParam("limit", limit)
                .body(recipient.getPayload() != null ? recipient.getPayload().getData() : null)
                .build();
    }

    private String getLimit(JobDetails job) {
        return Optional.ofNullable(job.getTrigger())
                .filter(IntervalTrigger.class::isInstance)
                .map(limit -> getRepeatableJobCountDown(job))
                .map(String::valueOf)
                .orElse(null);
    }

    private HttpRecipient getHttpRecipient(JobDetails job) {
        return Optional.ofNullable(job.getRecipient())
                .map(Recipient::getRecipient)
                .filter(HttpRecipient.class::isInstance)
                .map(HttpRecipient.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("HttpRecipient expected for job " + job));
    }

    private int getRepeatableJobCountDown(JobDetails job) {
        IntervalTrigger trigger = (IntervalTrigger) job.getTrigger();
        return trigger.getRepeatLimit() - trigger.getRepeatCount() - 1;//since the repeatCount is updated only after this call when persisting the job.
    }

    @Override
    public Class<HttpRecipient> type() {
        return HttpRecipient.class;
    }

    private static Map<String, String> filterEntries(Map<String, String> source) {
        if (source == null) {
            return Collections.emptyMap();
        }
        return source.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
