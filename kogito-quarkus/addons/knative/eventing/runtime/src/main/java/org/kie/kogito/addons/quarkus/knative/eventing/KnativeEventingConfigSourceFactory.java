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
package org.kie.kogito.addons.quarkus.knative.eventing;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.TreeMap;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.kie.kogito.event.KogitoEventStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;

import static org.kie.kogito.addons.quarkus.knative.eventing.KnativeEventingConfigSource.K_SINK;
import static org.kie.kogito.addons.quarkus.knative.eventing.KnativeEventingConfigSource.ORDINAL;

public final class KnativeEventingConfigSourceFactory implements ConfigSourceFactory {

    public static final String INCLUDE_PROCESS_EVENTS = "org.kie.kogito.addons.quarkus.knative.eventing.includeProcessEvents";

    public static final String SKIP_DEFAULT_INCOMING_STREAM = "org.kie.kogito.addons.quarkus.knative.eventing.skipDefaultIncomingStream";

    private static final String PROCESS_INSTANCES_EVENTS = "kogito-processinstances-events";

    private static final String PROCESS_DEFINITIONS_EVENTS = "kogito-processdefinitions-events";

    private static final String USER_TASK_INSTANCES_EVENTS = "kogito-usertaskinstances-events";

    private static final String QUARKUS_HTTP_CONNECTOR = "quarkus-http";

    private static final String OUTGOING_CONNECTOR_PREFIX = "mp.messaging.outgoing";

    private static final String INCOMING_CONNECTOR_PREFIX = "mp.messaging.incoming";

    /**
     * Default Knative Sink for local dev environments. Just a default endpoint, nothing in particular.
     * Users can then configure their local sinks to this port.
     */
    private static final String DEFAULT_SINK_URL = "http://localhost:9090";

    private static final String DEFAULT_SINK_URL_EXPRESSION = "${K_SINK:" + DEFAULT_SINK_URL + "}";

    private static final Logger LOGGER = LoggerFactory.getLogger(KnativeEventingConfigSourceFactory.class);

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        Map<String, String> configuration = new TreeMap<>();

        addOutgoingConnector(configuration, KogitoEventStreams.OUTGOING);

        if (!skipDefaultIncomingStream(context)) {
            addIncomingConnector(configuration);
        }

        if (includeProcessEvents(context)) {
            addOutgoingConnector(configuration, PROCESS_INSTANCES_EVENTS);
            addOutgoingConnector(configuration, PROCESS_DEFINITIONS_EVENTS);
            addOutgoingConnector(configuration, USER_TASK_INSTANCES_EVENTS);
        }

        final String sinkUrl = context.getValue(K_SINK).getValue();
        if (sinkUrl == null || sinkUrl.isEmpty()) {
            LOGGER.debug("{} variable is empty or doesn't exist. Please make sure that this service is a Knative Source or has a SinkBinding bound to it.", K_SINK);
        }

        configuration.forEach((key, value) -> LOGGER.debug("Adding connector -> {} =  {}", key, value));

        return List.of(new KnativeEventingConfigSource(configuration));
    }

    @Override
    public OptionalInt getPriority() {
        return OptionalInt.of(ORDINAL);
    }

    private static void addOutgoingConnector(Map<String, String> configuration, String name) {
        configuration.put(buildOutgoingConnector(name), QUARKUS_HTTP_CONNECTOR);
        // add the default configuration to fall back to a placeholder since the underlying connector will fail on
        // bootstrap if either the env var is not defined or the URL is not valid.
        // we handle the missing env var injected by knative via probe
        configuration.put(buildOutgoingConnectorUrl(name), DEFAULT_SINK_URL_EXPRESSION);
    }

    private static void addIncomingConnector(Map<String, String> configuration) {
        configuration.put(INCOMING_CONNECTOR_PREFIX + "." + KogitoEventStreams.INCOMING + ".connector", QUARKUS_HTTP_CONNECTOR);
        configuration.put(INCOMING_CONNECTOR_PREFIX + "." + KogitoEventStreams.INCOMING + ".path", "/");
    }

    private static String buildOutgoingConnector(String name) {
        return OUTGOING_CONNECTOR_PREFIX + "." + name + ".connector";
    }

    private static String buildOutgoingConnectorUrl(String name) {
        return OUTGOING_CONNECTOR_PREFIX + "." + name + ".url";
    }

    private boolean includeProcessEvents(ConfigSourceContext context) {
        return Boolean.parseBoolean(context.getValue(INCLUDE_PROCESS_EVENTS).getValue());
    }

    private boolean skipDefaultIncomingStream(ConfigSourceContext context) {
        return Boolean.parseBoolean(context.getValue(SKIP_DEFAULT_INCOMING_STREAM).getValue());
    }
}
