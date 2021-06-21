/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.kie.kogito.monitoring.core.common.system.metrics.SystemMetricsCollector;

public class MetricsInterceptor {

    private final SystemMetricsCollector systemMetricsCollector;

    public MetricsInterceptor(SystemMetricsCollector systemMetricsCollector) {
        this.systemMetricsCollector = systemMetricsCollector;
    }

    public void filter(String matchedUrl, int statusCode) {
        String stringStatusCode = String.valueOf(statusCode);
        if (statusCode != 404) {
            String cleanUrl = cleanUrl(matchedUrl);
            systemMetricsCollector.registerStatusCodeRequest(cleanUrl, stringStatusCode);
        } else // Log the number of requests that did not match any Uri -> 404 not found.
        {
            systemMetricsCollector.registerStatusCodeRequest("NOT FOUND", stringStatusCode);
        }
    }

    private String cleanUrl(String matchedUrl) {
        if (matchedUrl != null && matchedUrl.startsWith("/")) {
            return matchedUrl.substring(1);
        }
        return matchedUrl;
    }
}
