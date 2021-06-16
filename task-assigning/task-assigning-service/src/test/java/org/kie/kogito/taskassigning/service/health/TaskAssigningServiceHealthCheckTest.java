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

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.taskassigning.service.ServiceMessage;
import org.kie.kogito.taskassigning.service.ServiceStatus;
import org.kie.kogito.taskassigning.service.ServiceStatusInfo;
import org.kie.kogito.taskassigning.service.TaskAssigningService;
import org.kie.kogito.taskassigning.service.TaskAssigningServiceContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.service.TestUtil.parseZonedDateTime;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TaskAssigningServiceHealthCheckTest {

    private static final String MESSAGE_VALUE = "SERVICE_MESSAGE";

    private static final ZonedDateTime MESSAGE_TIME = parseZonedDateTime("2021-05-03T10:00:00.001Z");

    @Mock
    TaskAssigningService service;

    @Mock
    TaskAssigningServiceContext context;

    private TaskAssigningServiceHealthCheck taskAssigningServiceHealthCheck;

    @BeforeEach
    void setUp() {
        doReturn(context).when(service).getContext();
        taskAssigningServiceHealthCheck = new TaskAssigningServiceHealthCheck();
        taskAssigningServiceHealthCheck.service = service;
    }

    @ParameterizedTest
    @MethodSource("livenessCheckParams")
    void livenessCheck(ServiceStatus status, ServiceMessage message, HealthCheckResponse.Status expectedResponseStatus) {
        prepareTest(status, message);
        HealthCheck healthCheck = taskAssigningServiceHealthCheck.livenessCheck();
        executeTest(healthCheck, status, message, expectedResponseStatus);
    }

    @ParameterizedTest
    @MethodSource("readinessCheckParams")
    void readinessCheck(ServiceStatus status, ServiceMessage message, HealthCheckResponse.Status expectedResponseStatus) {
        prepareTest(status, message);
        HealthCheck healthCheck = taskAssigningServiceHealthCheck.readinessCheck();
        executeTest(healthCheck, status, message, expectedResponseStatus);
    }

    private void prepareTest(ServiceStatus status, ServiceMessage message) {
        ServiceStatusInfo info = new ServiceStatusInfo(status, message);
        doReturn(info).when(context).getStatusInfo();
    }

    private void executeTest(HealthCheck healthCheck,
            ServiceStatus status,
            ServiceMessage message,
            HealthCheckResponse.Status expectedResponseStatus) {
        assertThat(healthCheck).isNotNull();
        HealthCheckResponse response = healthCheck.call();
        verifyResponse(response, expectedResponseStatus, status, message);
    }

    private void verifyResponse(HealthCheckResponse response,
            HealthCheckResponse.Status expectedResponseStatus,
            ServiceStatus expectedServiceStatus,
            ServiceMessage expectedServiceMessage) {
        assertThat(response.getStatus()).isEqualTo(expectedResponseStatus);
        if (expectedServiceMessage != null) {
            assertThat(response.getData()).isPresent();
            Map<String, Object> data = response.getData().get();
            String status = data.get(TaskAssigningServiceHealthCheck.SERVICE_STATUS).toString();
            assertThat(status)
                    .isNotNull()
                    .isEqualTo(expectedServiceStatus.name());
            String statusMessage = data.get(TaskAssigningServiceHealthCheck.SERVICE_STATUS_MESSAGE).toString();
            assertThat(statusMessage).isNotNull();
            assertThat(statusMessage).isEqualTo("[%s]:[%s]:[%s]", MESSAGE_TIME, expectedServiceMessage.getType(), MESSAGE_VALUE);
        }
    }

    private static Stream<Arguments> livenessCheckParams() {
        return Stream.of(
                Arguments.of(ServiceStatus.STARTING, mockMessage(ServiceMessage.Type.INFO, MESSAGE_TIME, MESSAGE_VALUE), HealthCheckResponse.Status.UP),
                Arguments.of(ServiceStatus.READY, mockMessage(ServiceMessage.Type.INFO, MESSAGE_TIME, MESSAGE_VALUE), HealthCheckResponse.Status.UP),
                Arguments.of(ServiceStatus.SHUTDOWN, mockMessage(ServiceMessage.Type.INFO, MESSAGE_TIME, MESSAGE_VALUE), HealthCheckResponse.Status.DOWN),
                Arguments.of(ServiceStatus.ERROR, mockMessage(ServiceMessage.Type.ERROR, MESSAGE_TIME, MESSAGE_VALUE), HealthCheckResponse.Status.DOWN));
    }

    private static Stream<Arguments> readinessCheckParams() {
        return Stream.of(
                Arguments.of(ServiceStatus.READY, mockMessage(ServiceMessage.Type.INFO, MESSAGE_TIME, MESSAGE_VALUE), HealthCheckResponse.Status.UP),
                Arguments.of(ServiceStatus.STARTING, mockMessage(ServiceMessage.Type.INFO, MESSAGE_TIME, MESSAGE_VALUE), HealthCheckResponse.Status.DOWN),
                Arguments.of(ServiceStatus.SHUTDOWN, mockMessage(ServiceMessage.Type.INFO, MESSAGE_TIME, MESSAGE_VALUE), HealthCheckResponse.Status.DOWN),
                Arguments.of(ServiceStatus.ERROR, mockMessage(ServiceMessage.Type.ERROR, MESSAGE_TIME, MESSAGE_VALUE), HealthCheckResponse.Status.DOWN));
    }

    private static ServiceMessage mockMessage(ServiceMessage.Type messageType, ZonedDateTime messageTime, String messageValue) {
        ServiceMessage message = mock(ServiceMessage.class);
        doReturn(messageTime).when(message).getTime();
        doReturn(messageValue).when(message).getValue();
        doReturn(messageType).when(message).getType();
        return message;
    }
}
