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
package org.kie.kogito.addons.quarkus.knative.eventing.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface SinkConfiguration {

    String DEFAULT_SINK_API_VERSION = "eventing.knative.dev/v1";
    String DEFAULT_SINK_NAME = "default";
    String DEFAULT_SINK_KIND = "Broker";

    /**
     * Namespace where the given Knative Sink is deployed.
     * This sink is used to configure the "sink" property in the generated Knative SinkBinding for this Kogito service.
     */
    Optional<String> namespace();

    /**
     * Kubernetes API Version of the given Knative Sink.
     * <p>
     * This sink is used to configure the "sink" property in the generated Knative SinkBinding for this Kogito service.
     */
    @WithDefault(DEFAULT_SINK_API_VERSION)
    String apiVersion();

    /**
     * Name of the given Knative Sink.
     * <p>
     * This sink is used to configure the "sink" property in the generated Knative SinkBinding for this Kogito service.
     */
    @WithDefault(DEFAULT_SINK_NAME)
    String name();

    /**
     * Kubernetes Kind of the given Knative Sink.
     * <p>
     * This sink is used to configure the "sink" property in the generated Knative SinkBinding for this Kogito service.
     */
    @WithDefault(DEFAULT_SINK_KIND)
    String kind();
}
