/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.service.health;

import java.time.format.DateTimeFormatter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;
import org.kie.kogito.taskassigning.service.ServiceMessage;
import org.kie.kogito.taskassigning.service.ServiceStatus;
import org.kie.kogito.taskassigning.service.ServiceStatusInfo;
import org.kie.kogito.taskassigning.service.TaskAssigningService;

@ApplicationScoped
public class TaskAssigningServiceHealthCheck {

    public static final String SERVICE_STATUS = "service-status";
    public static final String SERVICE_STATUS_MESSAGE = "service-status-message";
    public static final String LIVENESS_NAME = "Task Assigning Service - liveness check";
    public static final String READINESS_NAME = "Task Assigning Service - readiness check";

    @Inject
    TaskAssigningService service;

    @Produces
    @Liveness
    HealthCheck livenessCheck() {
        return () -> {
            ServiceStatusInfo statusInfo = service.getContext().getStatusInfo();
            HealthCheckResponseBuilder builder = newBuilder(LIVENESS_NAME, statusInfo);
            if (statusInfo.getStatus() == ServiceStatus.ERROR ||
                    statusInfo.getStatus() == ServiceStatus.SHUTDOWN) {
                return builder.down().build();
            } else {
                return builder.up().build();
            }
        };
    }

    @Produces
    @Readiness
    HealthCheck readinessCheck() {
        return () -> {
            ServiceStatusInfo statusInfo = service.getContext().getStatusInfo();
            HealthCheckResponseBuilder builder = newBuilder(READINESS_NAME, statusInfo);
            if (statusInfo.getStatus() == ServiceStatus.READY) {
                return builder.up().build();
            } else {
                return builder.down().build();
            }
        };
    }

    private static HealthCheckResponseBuilder newBuilder(String name, ServiceStatusInfo statusInfo) {
        HealthCheckResponseBuilder builder = HealthCheckResponse.builder()
                .name(name)
                .withData(SERVICE_STATUS, statusInfo.getStatus().name());
        if (statusInfo.getStatusMessage() != null) {
            builder.withData(SERVICE_STATUS_MESSAGE, buildServerMessage(statusInfo.getStatusMessage()));
        }
        return builder;
    }

    private static String buildServerMessage(ServiceMessage message) {
        return String.format(
                "[%s]:[%s]:[%s]",
                message.getTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                message.getType().name(),
                message.getValue());
    }
}
