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
package org.kie.kogito.addons.quarkus.jobs.service.embedded;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class KogitoAddonsQuarkusJobsServiceEmbeddedConfigSource implements ConfigSource {

    private static final String KAFKA_DEV_SERVICES = "quarkus.kafka.devservices.enabled";
    private static final String DATASOURCE_DEV_SERVICES = "quarkus.datasource.devservices.enabled";

    private static final Map<String, String> DEFAULT_CONFIG = new HashMap<>();

    static {
        DEFAULT_CONFIG.put(KAFKA_DEV_SERVICES, "false");
        DEFAULT_CONFIG.put(DATASOURCE_DEV_SERVICES, "false");
    }

    @Override
    public Set<String> getPropertyNames() {
        return DEFAULT_CONFIG.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return DEFAULT_CONFIG.get(propertyName);
    }

    @Override
    public Map<String, String> getProperties() {
        return DEFAULT_CONFIG;
    }

    @Override
    public String getName() {
        return KogitoAddonsQuarkusJobsServiceEmbeddedConfigSource.class.getSimpleName();
    }

    @Override
    public int getOrdinal() {
        return Integer.MIN_VALUE;
    }
}
