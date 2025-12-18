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
package org.kie.kogito.quarkus.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
@ConfigMapping(prefix = "kogito")
public interface KogitoBuildTimeConfig {

    /**
     * If this is enabled the service will use Cloud Events
     * <p>
     * If not defined, true will be used.
     */
    @WithName("messaging.as-cloudevents")
    @WithDefault("true")
    Boolean useCloudEvents();

    /**
     * If this property is True, Jackson will fail on an empty bean
     * <p>
     * If not defined, false will be used.
     */
    @WithName("jackson.fail-on-empty-bean")
    @WithDefault("false")
    Boolean failOnEmptyBean();

    /**
     * Incoming channel default name
     */
    @WithName("addon.messaging.incoming.defaultName")
    @WithDefault("kogito_incoming_stream")
    String incomingChannelDefaultName();

    /**
     * Outgoing channel default name
     */
    @WithName("addon.messaging.outgoing.defaultName")
    @WithDefault("kogito_outgoing_stream")
    String outgoingChannelDefaultName();

    /**
     * Whether to ignore hidden files when collecting files for code generation
     * <p>
     * If not defined, true will be used.
     */
    @WithName("codegen.ignoreHiddenFiles")
    @WithDefault("true")
    Boolean ignoreHiddenFiles();

    /**
     * Whether to fail when there are parsing/validation errors of process assets
     * <p>
     * If not defined, true will be used.
     */
    @WithName("codegen.process.failOnError")
    @WithDefault("true")
    Boolean failOnError();
}
