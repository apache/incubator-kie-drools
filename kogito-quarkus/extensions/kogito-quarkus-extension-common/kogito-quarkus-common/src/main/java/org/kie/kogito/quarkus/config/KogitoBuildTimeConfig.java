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
package org.kie.kogito.quarkus.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED, prefix = "kogito")
public class KogitoBuildTimeConfig {

    /**
     * If this is enabled the service will use Cloud Events
     * <p>
     * If not defined, true will be used.
     */
    @ConfigItem(name = "messaging.as-cloudevents", defaultValue = "true")
    public Boolean useCloudEvents;

    /**
     * If this property is True, Jackson will fail on an empty bean
     * <p>
     * If not defined, false will be used.
     */
    @ConfigItem(name = "jackson.fail-on-empty-bean", defaultValue = "false")
    public Boolean failOnEmptyBean;

    /**
     * Incoming channel default name
     */
    @ConfigItem(name = "addon.messaging.incoming.defaultName", defaultValue = "kogito_incoming_stream")
    public String incomingChannelDefaultName;

    /**
     * Outgoing channel default name
     */
    @ConfigItem(name = "addon.messaging.outgoing.defaultName", defaultValue = "kogito_outgoing_stream")
    public String outgoingChannelDefaultName;

    /**
     * Whether to ignore hidden files when collecting files for code generation
     * <p>
     * If not defined, true will be used.
     */
    @ConfigItem(name = "codegen.ignoreHiddenFiles", defaultValue = "true")
    public Boolean ignoreHiddenFiles;

    /**
     * Whether to fail when there are parsing/validation errors of process assets
     * <p>
     * If not defined, true will be used.
     */
    @ConfigItem(name = "codegen.process.failOnError", defaultValue = "true")
    public Boolean failOnError;
}
