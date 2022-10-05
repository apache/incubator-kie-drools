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
package org.kie.kogito.monitoring.core.common.system.interceptor;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.monitoring.core.common.system.metrics.SystemMetricsCollector;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MetricsInterceptorTest {

    @Test
    void testUrlEncodedStringFromFailure() throws Exception {
        commonMetricInterceptorFilter("myUrl", 404, "NOT FOUND");
        commonMetricInterceptorFilter("myUrl", 200, "myUrl");
        commonMetricInterceptorFilter("/myUrl", 404, "NOT FOUND");
        commonMetricInterceptorFilter("/myUrl", 200, "myUrl");
    }

    private void commonMetricInterceptorFilter(String endpoint, int statusCode, String expectedEndpoint) {
        SystemMetricsCollector systemMetricsCollector = Mockito.mock(SystemMetricsCollector.class);
        MetricsInterceptor metricsInterceptor = new MetricsInterceptor(systemMetricsCollector);
        metricsInterceptor.filter(endpoint, statusCode);

        final ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> statusCodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(systemMetricsCollector, times(1)).registerStatusCodeRequest(endpointCaptor.capture(), statusCodeCaptor.capture());

        List<String> endpoints = endpointCaptor.getAllValues();
        assertThat(endpoints).isNotEmpty().hasSize(1);
        assertThat(endpoints).containsExactly(expectedEndpoint);

        List<String> statusCodes = statusCodeCaptor.getAllValues();
        assertThat(statusCodes).isNotEmpty().hasSize(1);
        String statusCodeString = String.valueOf(statusCode);
        assertThat(statusCodes).containsExactly(statusCodeString);
    }
}
