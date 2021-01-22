/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.monitoring.core.springboot;

import org.junit.jupiter.api.Test;
import org.kie.kogito.monitoring.core.common.system.interceptor.MetricsInterceptor;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class SpringMetricsInterceptorTest {

    @Test
    public void filter() throws Exception {
        commonMetricInterceptorFilter("url1", 200, "url1");
        commonMetricInterceptorFilter("/url2", 200, "/url2");
        commonMetricInterceptorFilter(null, 404, null);
        commonMetricInterceptorFilter("", 200, "");
        commonMetricInterceptorFilter("/url2", 404, "/url2");
    }

    private void commonMetricInterceptorFilter(String requestUri, int statusCode, String expectedMatchedUrl) throws Exception {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpServletResponse responseMock = mock(HttpServletResponse.class);

        when(requestMock.getRequestURI()).thenReturn(requestUri);
        when(responseMock.getStatus()).thenReturn(statusCode);

        try (MockedStatic<MetricsInterceptor> metricsInterceptor = Mockito.mockStatic(MetricsInterceptor.class)) {

            final ArgumentCaptor<String> matchedUrl = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<Integer> statusCodeCaptor = ArgumentCaptor.forClass(int.class);

            SpringbootMetricsInterceptor interceptor = new SpringbootMetricsInterceptor();

            interceptor.postHandle(requestMock, responseMock, null, null);

            metricsInterceptor.verify(times(1), () -> MetricsInterceptor.filter(matchedUrl.capture(), statusCodeCaptor.capture()));

            List<String> endpoints = matchedUrl.getAllValues();
            assertThat(endpoints.isEmpty()).isFalse();
            assertThat(endpoints.size()).isEqualTo(1);
            assertThat(endpoints.get(0)).isEqualTo(expectedMatchedUrl);

            List<Integer> statusCodes = statusCodeCaptor.getAllValues();
            assertThat(statusCodes.isEmpty()).isFalse();
            assertThat(statusCodes.size()).isEqualTo(1);
            assertThat(statusCodes.get(0)).isEqualTo(statusCode);
        }
    }
}