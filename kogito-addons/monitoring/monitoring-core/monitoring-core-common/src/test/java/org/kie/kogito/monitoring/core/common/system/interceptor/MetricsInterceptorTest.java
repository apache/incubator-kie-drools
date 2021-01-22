/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.monitoring.core.common.system.interceptor;

import org.junit.jupiter.api.Test;
import org.kie.kogito.monitoring.core.common.system.metrics.SystemMetricsCollector;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;

class MetricsInterceptorTest {

    @Test
    void testUrlEncodedStringFromFailure() throws Exception {
        commonMetricInterceptorFilter("myUrl", 404, "NOT FOUND");
        commonMetricInterceptorFilter("myUrl", 200, "myUrl");
        commonMetricInterceptorFilter("/myUrl", 404, "NOT FOUND");
        commonMetricInterceptorFilter("/myUrl", 200, "myUrl");
    }

    private void commonMetricInterceptorFilter(String endpoint, int statusCode, String expectedEndpoint) {
        try (MockedStatic<SystemMetricsCollector> systemMetricsCollector = Mockito.mockStatic(SystemMetricsCollector.class)) {
            MetricsInterceptor.filter(endpoint, statusCode);

            final ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<String> statusCodeCaptor = ArgumentCaptor.forClass(String.class);
            systemMetricsCollector.verify(times(1), () -> SystemMetricsCollector.registerStatusCodeRequest(endpointCaptor.capture(), statusCodeCaptor.capture()));

            List<String> endpoints = endpointCaptor.getAllValues();
            assertThat(endpoints.isEmpty()).isFalse();
            assertThat(endpoints.size()).isEqualTo(1);
            assertThat(endpoints.get(0)).isEqualTo(expectedEndpoint);

            List<String> statusCodes = statusCodeCaptor.getAllValues();
            assertThat(statusCodes.isEmpty()).isFalse();
            assertThat(statusCodes.size()).isEqualTo(1);
            String statusCodeString = String.valueOf(statusCode);
            assertThat(statusCodes.get(0)).isEqualTo(statusCodeString);
        }
    }

}