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
package org.kie.kogito.addons.quarkus.camel.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import org.eclipse.microprofile.config.spi.ConfigSource;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;

/**
 * Configuration Source Factory for default Camel properties when integrating with Kogito Serverless Workflow.
 * The client application can override these properties.
 */
public final class CamelConfigSourceFactory implements ConfigSourceFactory {

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        Map<String, String> configuration = new HashMap<>();

        // default Kogito Quarkus Camel Route configuration
        configuration.put("camel.main.routes-reload-enabled", "true");
        configuration.put("camel.main.routes-reload-directory", "src/main/resources/routes");
        configuration.put("camel.main.routes-reload-pattern", "*.xml,*.yaml,*.yml");
        configuration.put("camel.main.routes-include-pattern", "routes/*.xml,routes/*.yaml,routes/*.yml");

        return List.of(new CamelConfigSource(configuration));
    }

    @Override
    public OptionalInt getPriority() {
        return OptionalInt.of(CamelConfigSource.ORDINAL);
    }
}
