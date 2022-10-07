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

package org.kie.kogito.tracing.decision.quarkus.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class KogitoDevServicesBuildTimeConfig {

    /**
     * If Dev Services for Kogito has been explicitly enabled or disabled. Dev Services are generally enabled
     * by default, unless there is an existing configuration present.
     */
    @ConfigItem
    public Optional<Boolean> enabled = Optional.empty();

    /**
     * Optional fixed port the dev service will listen to.
     * <p>
     * If not defined, 8081 will be used.
     */
    @ConfigItem(defaultValue = "8081")
    public Optional<Integer> port;

    /**
     * The TrustyService image to use.
     */
    @ConfigItem
    public String imageName;

    /**
     * Indicates if the TrustyService instance managed by Quarkus DevServices is shared.
     * When shared, Quarkus looks for running containers using label-based service discovery.
     * If a matching container is found, it is used, and so a second one is not started.
     * Otherwise, DevServices starts a new TrustyService instance.
     * <p>
     * The discovery uses the {@code kogito-dev-service-trusty-service} label.
     * The value is configured using the {@code service-name} property.
     * <p>
     * Container sharing is only used in dev mode.
     */
    @ConfigItem(defaultValue = "true")
    public boolean shared;

    /**
     * The value of the {@code kogito-dev-service-trusty-service} label attached to the started container.
     * This property is used when {@code shared} is set to {@code true}.
     * In this case, before starting a container, DevServices looks for a container with the
     * {@code kogito-dev-service-trusty-service} label set to the configured value. If found, it will use this
     * container instead of starting a new one. Otherwise, it starts a new container with the
     * {@code kogito-dev-service-trusty-service} label set to the specified value.
     */
    @ConfigItem(defaultValue = "kogito-trusty-service")
    public String serviceName;

    /**
     * Optional random port the dev service will listen to in tests.
     */
    @ConfigItem(defaultValue = "-1")
    public Integer portUsedByTest;

}
