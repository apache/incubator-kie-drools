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

package org.kie.kogito.addon.quarkus.common.health;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractAvailabilityHealthCheckTest {

    protected static final String HOST = "service-host";
    protected static final int PORT = 8080;
    protected static final String SERVICE_URL = "http://" + HOST + ":" + PORT;
    protected static final String HEALTH_PATH = "/q/health/ready";
    protected AbstractAvailabilityHealthCheck healthCheck;
    @Mock
    protected Vertx vertx;
    @Mock
    protected WebClient webClient;

    protected abstract AbstractAvailabilityHealthCheck createHealthCheck(String serviceUrl, Vertx vertxMock, WebClient webClientMock);

    @Test
    void healthCheckUp() {
        healthCheck = createHealthCheck(SERVICE_URL, vertx, webClient);
        prepareCall(HOST, PORT, HEALTH_PATH, HealthCheckResponse.Status.UP, false);
        HealthCheckResponse response = healthCheck.call();
        assertThat(response.getStatus()).isEqualTo(HealthCheckResponse.Status.UP);
    }

    @Test
    void healthCheckDown() {
        healthCheck = createHealthCheck(SERVICE_URL, vertx, webClient);
        prepareCall(HOST, PORT, HEALTH_PATH, HealthCheckResponse.Status.DOWN, false);
        HealthCheckResponse response = healthCheck.call();
        assertThat(response.getStatus()).isEqualTo(HealthCheckResponse.Status.DOWN);
    }

    @Test
    void healthCheckDownServiceCallError() {
        healthCheck = createHealthCheck(SERVICE_URL, vertx, webClient);
        prepareCall(HOST, PORT, HEALTH_PATH, HealthCheckResponse.Status.UP, true);
        HealthCheckResponse response = healthCheck.call();
        assertThat(response.getStatus()).isEqualTo(HealthCheckResponse.Status.DOWN);
        assertThat(response.getData()).isPresent();
        Object error = response.getData().get().get("error");
        assertThat(error).isNotNull().hasToString("[500] - Internal server error");
    }

    protected void prepareCall(String host, int port, String path, HealthCheckResponse.Status responseStatus, boolean mockError) {
        HttpRequest<Buffer> requestMock = mock(HttpRequest.class);
        doReturn(requestMock).when(webClient).request(HttpMethod.GET, port, host, path);

        HttpResponse<Buffer> httpResponse = mock(HttpResponse.class);
        JsonObject jsonObject = JsonObject.of("status", responseStatus.name());
        lenient().doReturn(jsonObject).when(httpResponse).bodyAsJsonObject();
        int statusCode = 200;
        if (mockError) {
            statusCode = 500;
            doReturn("Internal server error").when(httpResponse).statusMessage();
        }
        doReturn(statusCode).when(httpResponse).statusCode();
        doReturn(Uni.createFrom().item(httpResponse)).when(requestMock).send();
    }
}
