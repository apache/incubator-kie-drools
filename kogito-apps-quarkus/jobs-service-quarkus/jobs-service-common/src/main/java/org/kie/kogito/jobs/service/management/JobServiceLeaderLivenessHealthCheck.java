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
package org.kie.kogito.jobs.service.management;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@Liveness
@ApplicationScoped
public class JobServiceLeaderLivenessHealthCheck implements HealthCheck {

    private final AtomicBoolean enabled = new AtomicBoolean(false);

    private final AtomicLong startTime = new AtomicLong();

    private static final String EXPIRATION_IN_SECONDS = "kogito.jobs-service.management.leader-check.expiration-in-seconds";

    @ConfigProperty(name = EXPIRATION_IN_SECONDS, defaultValue = "-1")
    long expirationInSeconds;

    @PostConstruct
    void init() {
        startTime.set(getCurrentTimeMillis());
    }

    @Override
    public HealthCheckResponse call() {
        final HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Get Leader Instance Timeout");
        if (hasExpired() && !enabled.get()) {
            return responseBuilder.down().build();
        }
        return responseBuilder.up().build();
    }

    boolean hasExpired() {
        return (expirationInSeconds > 0) && (getCurrentTimeMillis() - startTime.get()) > (expirationInSeconds * 1000);
    }

    protected void onMessagingStatusChange(@Observes MessagingChangeEvent event) {
        this.enabled.set(event.isEnabled());
        startTime.set(getCurrentTimeMillis());
    }

    /**
     * Facilitates testing
     */
    long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
