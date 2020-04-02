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

package org.kie.kogito.monitoring.system.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Summary;

public class SystemMetricsCollector {

    private static final String STATUS_CODE_NAME = "api_http_response_code";

    private static final String STATUS_CODE_HELP = "Request status code.";

    private static final String[] ENDPOINT_LABEL = new String[]{"endpoint"};

    private static final String[] ENDPOINT_INDENTIFIER_LABELS = new String[]{"endpoint", "identifier"};

    private static final String ELAPSED_TIME_NAME = "api_execution_elapsed_nanosecond";

    private static final String ELAPSED_TIME_HELP = "Endpoint execution elapsed nanoseconds, 3 minutes time window.";

    private static final String EXCEPTIONS_NAME = "api_http_stacktrace_exceptions";

    private static final String EXCEPTIONS_HELP = "System exceptions details.";

    private static final Counter REQUEST_STATUS_CODE_COUNTER =
            Counter.build().name(STATUS_CODE_NAME)
                    .help(STATUS_CODE_HELP)
                    .labelNames(ENDPOINT_INDENTIFIER_LABELS).register();

    private static final Counter EXCEPTIONS_COUNTER =
            Counter.build().name(EXCEPTIONS_NAME)
                    .help(EXCEPTIONS_HELP)
                    .labelNames(ENDPOINT_INDENTIFIER_LABELS).register();

    private static final Summary ELAPSED_TIME_SUMMARY =
            Summary.build() // Calculate quantiles over a sliding window of 3 minutes.
                    .quantile(0.1, 0.01)   // Add 10th percentile with 5% tolerated error
                    .quantile(0.25, 0.05)
                    .quantile(0.50, 0.05)
                    .quantile(0.75, 0.05)
                    .quantile(0.9, 0.05)
                    .quantile(0.99, 0.01)
                    .maxAgeSeconds(180)
                    .name(ELAPSED_TIME_NAME)
                    .help(ELAPSED_TIME_HELP)
                    .labelNames(ENDPOINT_LABEL)
                    .register();

    private SystemMetricsCollector() {
    }

    public static void registerStatusCodeRequest(String endpoint, String statusCode) {
        REQUEST_STATUS_CODE_COUNTER.labels(endpoint, statusCode).inc();
    }

    public static void registerElapsedTimeSampleMetrics(String endpoint, double elapsedTime) {
        ELAPSED_TIME_SUMMARY.labels(endpoint).observe(elapsedTime);
    }

    public static void registerException(String endpoint, String stackTrace) {
        EXCEPTIONS_COUNTER.labels(endpoint, stackTrace).inc();
    }
}