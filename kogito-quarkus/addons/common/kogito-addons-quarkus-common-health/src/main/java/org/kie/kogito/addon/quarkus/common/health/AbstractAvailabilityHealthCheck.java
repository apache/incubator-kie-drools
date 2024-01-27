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

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

public abstract class AbstractAvailabilityHealthCheck implements HealthCheck {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(500);
    private static final int DEFAULT_PORT = 80;

    protected String serviceName;

    protected String serviceUrl;

    protected String healthPath;

    protected String checkType;

    protected Vertx vertx;

    protected WebClient webClient;

    protected Duration requestTimeout;

    public AbstractAvailabilityHealthCheck() {
        // CDI
    }

    public AbstractAvailabilityHealthCheck(String serviceName, String serviceUrl, String healthPath, String checkType,
            Vertx vertx, Duration requestTimeout) {
        this.serviceName = serviceName;
        this.serviceUrl = serviceUrl;
        this.healthPath = healthPath;
        this.checkType = checkType;
        this.vertx = vertx;
        this.requestTimeout = requestTimeout != null ? requestTimeout : DEFAULT_TIMEOUT;
        this.webClient = createClient();
    }

    /**
     * facilitates tests.
     */
    protected WebClient createClient() {
        return WebClient.create(vertx);
    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named(serviceName + " Availability - " + checkType).up();
        if (serviceUrl == null) {
            builder.down().withData("error", serviceName + " url is not configured");
        } else {
            URL url;
            try {
                url = new URL(serviceUrl);
                HttpResponse<Buffer> response = webClient.request(HttpMethod.GET,
                        url.getPort() > 0 ? url.getPort() : DEFAULT_PORT,
                        url.getHost(),
                        healthPath)
                        .send()
                        .await()
                        .atMost(requestTimeout);
                if (response.statusCode() != 200) {
                    String data = "[" + response.statusCode() + "] - " + response.statusMessage();
                    builder.down().withData("error", data);
                } else {
                    JsonObject jsonResponse = response.bodyAsJsonObject();
                    if (jsonResponse == null || !HealthCheckResponse.Status.UP.name().equals(jsonResponse.getString("status"))) {
                        builder.down();
                    }
                }
            } catch (Exception e) {
                if (e instanceof MalformedURLException) {
                    builder.down().withData("error", serviceName + " url configuration error: " + e.getMessage());
                } else {
                    builder.down().withData("error", "[unknown] - " + e.getMessage());
                }
            }
        }
        return builder.build();
    }
}
