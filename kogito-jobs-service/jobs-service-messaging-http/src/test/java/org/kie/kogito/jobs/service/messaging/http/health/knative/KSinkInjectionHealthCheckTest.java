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

package org.kie.kogito.jobs.service.messaging.http.health.knative;

import java.util.function.UnaryOperator;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.messaging.http.health.knative.KSinkInjectionHealthCheck.K_SINK;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class KSinkInjectionHealthCheckTest {

    private KSinkInjectionHealthCheck healthCheck;

    @Mock
    private UnaryOperator<String> envReader;

    @BeforeEach
    void setUp() {
        healthCheck = new KSinkInjectionHealthCheck(envReader);
    }

    @Test
    void callSuccessful() {
        doReturn("http://localhost:8080").when(envReader).apply(K_SINK);
        HealthCheckResponse response = healthCheck.call();
        assertThat(response.getStatus()).isEqualTo(HealthCheckResponse.Status.UP);
    }

    @Test
    void callUnsuccessfulMissingEnvVar() {
        HealthCheckResponse response = healthCheck.call();
        assertThat(response.getStatus()).isEqualTo(HealthCheckResponse.Status.DOWN);
    }

    @Test
    void callUnsuccessfulBadURL() {
        doReturn("that's not a url").when(envReader).apply(K_SINK);
        HealthCheckResponse response = healthCheck.call();
        assertThat(response.getStatus()).isEqualTo(HealthCheckResponse.Status.DOWN);
    }

    @Test
    void callUnsuccessfulUnresolvableURL() {
        // according with https://www.rfc-editor.org/rfc/rfc6761#section-6.4, domains with .invalid should never
        // be resolved.
        doReturn("http://something.invalid").when(envReader).apply(K_SINK);
        HealthCheckResponse response = healthCheck.call();
        assertThat(response.getStatus()).isEqualTo(HealthCheckResponse.Status.DOWN);
    }
}
