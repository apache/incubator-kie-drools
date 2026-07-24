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

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class JobServiceLeaderLivenessHealthCheckTest {

    private static final long START_TIME = 1234;

    private JobServiceLeaderLivenessHealthCheck healthCheck;

    @BeforeEach
    void setUp() {
        healthCheck = spy(new JobServiceLeaderLivenessHealthCheck());
        doReturn(START_TIME).when(healthCheck).getCurrentTimeMillis();
        healthCheck.init();
    }

    @Test
    void timeoutNotSet() {
        doReturn(START_TIME + 1000 * 50).when(healthCheck).getCurrentTimeMillis();
        assertThat(healthCheck.call().getStatus())
                .isNotNull()
                .isEqualTo(HealthCheckResponse.Status.UP);
    }

    @Test
    void timeoutSetButNotReached() {
        healthCheck.expirationInSeconds = 60;
        doReturn(START_TIME + 1000 * 10).when(healthCheck).getCurrentTimeMillis();
        assertThat(healthCheck.call().getStatus())
                .isNotNull()
                .isEqualTo(HealthCheckResponse.Status.UP);
    }

    @Test
    void timeoutSetAndReached() {
        healthCheck.expirationInSeconds = 60;
        doReturn(START_TIME + 1000 * 60 + 1).when(healthCheck).getCurrentTimeMillis();
        assertThat(healthCheck.call().getStatus())
                .isNotNull()
                .isEqualTo(HealthCheckResponse.Status.DOWN);
    }

    @Test
    void statusChanged() {
        healthCheck.onMessagingStatusChange(new MessagingChangeEvent(true));
        doReturn(START_TIME + 1000 * 10).when(healthCheck).getCurrentTimeMillis();
        HealthCheckResponse response = healthCheck.call();
        assertThat(response.getStatus())
                .isNotNull()
                .isEqualTo(HealthCheckResponse.Status.UP);
    }
}
