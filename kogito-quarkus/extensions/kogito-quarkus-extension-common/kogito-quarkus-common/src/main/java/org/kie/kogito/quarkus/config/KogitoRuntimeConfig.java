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

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "", phase = ConfigPhase.RUN_TIME, prefix = "kogito")
public class KogitoRuntimeConfig {

    /**
     * The service URL needed to connect to the runtime endpoint from outside the service.
     * <p>
     */
    @ConfigItem(name = "service.url")
    public Optional<String> serviceUrl;

    /**
     * Eventing runtime configuration
     */
    @ConfigItem(name = "quarkus.events")
    public KogitoEventingRuntimeConfig eventingConfig;

    /**
     * Maximum number of process instance to be returned by GET api
     */
    @ConfigItem(name = "process.instances.limit", defaultValue = "1000")
    public short processInstanceLimit;
}
