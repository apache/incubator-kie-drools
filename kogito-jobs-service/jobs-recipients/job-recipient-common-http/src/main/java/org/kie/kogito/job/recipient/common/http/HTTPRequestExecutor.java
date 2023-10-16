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
package org.kie.kogito.job.recipient.common.http;

import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.kie.kogito.job.recipient.common.http.converters.HttpConverters;
import org.kie.kogito.jobs.api.URIBuilder;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.exception.JobExecutionException;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;
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

public abstract class HTTPRequestExecutor<R extends Recipient<?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPRequestExecutor.class);

    protected long timeout;

    protected Vertx vertx;

    protected WebClient client;

    protected ObjectMapper objectMapper;

    protected HTTPRequestExecutor() {
    }

    protected HTTPRequestExecutor(long timeout, Vertx vertx, ObjectMapper objectMapper) {
        this.timeout = timeout;
        this.vertx = vertx;
        this.objectMapper = objectMapper;
    }

    protected void initialize() {
        this.client = createClient();
    }

    /**
     * facilitates tests.
     */
    public WebClient createClient() {
        return WebClient.create(vertx);
    }

    public Uni<JobExecutionResponse> execute(JobDetails jobDetails) {
        return Uni.createFrom().item(jobDetails)
                .chain(job -> {
                    final R recipient = getRecipient(job);
                    final String limit = getLimit(job);
                    final HTTPRequest request = buildRequest(recipient, limit);
                    final long requestTimeout = getTimeoutInMillis(job);
                    return executeRequest(request, requestTimeout)
                            .onFailure().transform(unexpected -> new JobExecutionException(job.getId(),
                                    "Unexpected error when executing HTTP request for job: " + jobDetails.getId() + ". " + unexpected.getMessage()))
                            .onItem().transform(response -> JobExecutionResponse.builder()
                                    .message(response.bodyAsString())
                                    .code(String.valueOf(response.statusCode()))
                                    .now()
                                    .jobId(job.getId())
                                    .build())
                            .chain(this::handleResponse);
                });
    }

    protected abstract R getRecipient(JobDetails job);

    protected abstract HTTPRequest buildRequest(R recipient, String limit);

    protected Uni<HttpResponse<Buffer>> executeRequest(HTTPRequest request, long timeout) {
        LOGGER.debug("Executing request {}", request);
        final URI uri = URIBuilder.toURI(request.getUrl());
        final HttpRequest<Buffer> clientRequest = client.request(HttpConverters.convertHttpMethod(request.getMethod()),
                uri.getPort(),
                uri.getHost(),
                uri.getPath()).timeout(timeout);
        clientRequest.queryParams().addAll(filterEntries(request.getQueryParams()));
        clientRequest.headers().addAll(filterEntries(request.getHeaders()));
        if (request.getBody() != null) {
            return clientRequest.sendBuffer(buildBuffer(request.getBody()));
        } else {
            return clientRequest.send();
        }
    }

    protected Buffer buildBuffer(Object body) {
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

    protected <T extends JobExecutionResponse> Uni<T> handleResponse(T response) {
        LOGGER.debug("Handle response {}", response);
        return Uni.createFrom().item(response)
                .onItem().transform(JobExecutionResponse::getCode)
                .onItem().transform(Integer::valueOf)
                .chain(code -> Response.Status.Family.SUCCESSFUL.equals(Response.Status.Family.familyOf(code))
                        ? handleSuccess(response)
                        : handleError(response));
    }

    protected <T extends JobExecutionResponse> Uni<T> handleError(T response) {
        return Uni.createFrom().item(response)
                .onItem().invoke(r -> LOGGER.debug("Error executing job {}.", r))
                .onItem().failWith(() -> new JobExecutionException(response.getJobId(), "Response error when executing HTTP request for " + response));
    }

    protected <T extends JobExecutionResponse> Uni<T> handleSuccess(T response) {
        return Uni.createFrom().item(response)
                .onItem().invoke(r -> LOGGER.debug("Success executing job {}.", r));
    }

    protected String getLimit(JobDetails job) {
        if (job.getTrigger() instanceof SimpleTimerTrigger) {
            return String.valueOf(getRepeatableJobCountDown((SimpleTimerTrigger) job.getTrigger()));
        }
        if (job.getTrigger() instanceof IntervalTrigger) {
            return String.valueOf(getRepeatableJobCountDown((IntervalTrigger) job.getTrigger()));
        }
        return "0";
    }

    protected long getTimeoutInMillis(JobDetails job) {
        if (job.getExecutionTimeout() == null) {
            return timeout;
        }
        ChronoUnit timeoutUnit = job.getExecutionTimeoutUnit() != null ? job.getExecutionTimeoutUnit() : ChronoUnit.MILLIS;
        return timeoutUnit == ChronoUnit.MILLIS ? job.getExecutionTimeout() : timeoutUnit.getDuration().multipliedBy(job.getExecutionTimeout()).toMillis();
    }

    protected int getRepeatableJobCountDown(IntervalTrigger trigger) {
        return trigger.getRepeatLimit() - trigger.getRepeatCount() - 1;//since the repeatCount is updated only after this call when persisting the job.
    }

    protected int getRepeatableJobCountDown(SimpleTimerTrigger trigger) {
        // The SimpleTimerTrigger stops when the (desired repetitions - actual executed repetitions) == 0.
        return trigger.getRepeatCount() - trigger.getCurrentRepeatCount();
    }

    protected static <K, V> Map<K, V> filterEntries(Map<K, V> source) {
        if (source == null) {
            return Collections.emptyMap();
        }
        return source.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
