/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * Adds Prometheus metadata labels
 */
public class PrometheusLabeler implements Labeler {

    static final String PROMETHEUS_LABEL_PREFIX = "prometheus.io";
    static final String LABEL_PATH = PROMETHEUS_LABEL_PREFIX + "/path";
    static final String LABEL_SCHEME = PROMETHEUS_LABEL_PREFIX + "/scheme";
    static final String LABEL_PORT = PROMETHEUS_LABEL_PREFIX + "/port";
    static final String LABEL_SCRAPE = PROMETHEUS_LABEL_PREFIX + "/scrape";
    static final String DEFAULT_PATH = "/metrics";
    static final String DEFAULT_SCHEME = "http";
    static final String DEFAULT_PORT = "8080";
    static final String DEFAULT_SCRAPE = "true";

    private final Map<String, String> labels = new HashMap<>();

    public PrometheusLabeler() {
        labels.put(LABEL_PATH, DEFAULT_PATH);
        labels.put(LABEL_SCHEME, DEFAULT_SCHEME);
        labels.put(LABEL_PORT, DEFAULT_PORT);
        labels.put(LABEL_SCRAPE, DEFAULT_SCRAPE);
    }

    @Override
    public Map<String, String> generateLabels() {
        return labels;
    }

}
