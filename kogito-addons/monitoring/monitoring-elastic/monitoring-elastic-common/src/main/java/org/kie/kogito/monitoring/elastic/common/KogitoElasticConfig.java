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
package org.kie.kogito.monitoring.elastic.common;

import java.util.HashMap;
import java.util.Map;

import io.micrometer.elastic.ElasticConfig;

public class KogitoElasticConfig {

    public static final String HOST_KEY = "host";
    public static final String INDEX_KEY = "index";
    public static final String STEP_KEY = "step";
    public static final String INDEX_DATE_FORMAT_KEY = "indexDateFormat";
    public static final String TIMESTAMP_FIELD_NAME_KEY = "timestampFieldName";
    public static final String AUTO_CREATE_INDEX_KEY = "autoCreateIndex";
    public static final String USERNAME_KEY = "userName";
    public static final String PASSWORD_KEY = "password";
    public static final String PIPELINE_KEY = "pipeline";
    public static final String INDEX_DATE_SEPARATOR_KEY = "indexDateSeparator";
    public static final String DOCUMENT_TYPE_KEY = "documentType";

    private final String prefix = ElasticConfig.DEFAULT.prefix();
    private final Map<String, String> configMap;

    public KogitoElasticConfig() {
        this.configMap = new HashMap<>();
    }

    public KogitoElasticConfig withProperty(String key, String value) {
        configMap.put(String.join(".", prefix, key), value);
        return this;
    }

    public Map<String, String> getConfigMap() {
        return configMap;
    }
}
