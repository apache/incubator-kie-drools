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
package org.kie.kogito.addons.quarkus.knative.eventing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.kie.kogito.event.KogitoEventStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;

import static org.kie.kogito.addons.quarkus.knative.eventing.KnativeEventingConfigSource.K_SINK;
import static org.kie.kogito.addons.quarkus.knative.eventing.KnativeEventingConfigSource.ORDINAL;

public final class KnativeEventingConfigSourceFactory implements ConfigSourceFactory {

    private static final String URL_CONFIG = "mp.messaging.outgoing." + KogitoEventStreams.OUTGOING + ".url";

    /**
     * Default Knative Sink for local dev environments. Just a default endpoint, nothing in particular.
     * Users can then configure their local sinks to this port.
     */
    private static final String DEFAULT_SINK_URL = "http://localhost:9090";

    private static final Logger LOGGER = LoggerFactory.getLogger(KnativeEventingConfigSourceFactory.class);

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        Map<String, String> configuration = new HashMap<>();

        configuration.put("mp.messaging.outgoing." + KogitoEventStreams.OUTGOING + ".connector", "quarkus-http");

        // add the default configuration to fall back to a placeholder since the underlying connector will fail on
        // bootstrap if either the env var is not defined or the URL is not valid.
        // we handle the missing env var injected by knative via probe
        configuration.put(URL_CONFIG, "${K_SINK:" + DEFAULT_SINK_URL + "}");

        final String sinkUrl = context.getValue(K_SINK).getValue();
        if (sinkUrl == null || "".equals(sinkUrl)) {
            LOGGER.warn("{} variable is empty or doesn't exist. Please make sure that this service is a Knative Source or has a SinkBinding bound to it.", K_SINK);
        }

        return List.of(new KnativeEventingConfigSource(configuration));
    }

    @Override
    public OptionalInt getPriority() {
        return OptionalInt.of(ORDINAL);
    }
}
