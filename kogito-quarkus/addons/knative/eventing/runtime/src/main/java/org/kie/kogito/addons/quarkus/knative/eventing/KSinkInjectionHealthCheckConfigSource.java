/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * Creates an alias ({@value CONFIG_ALIAS}) for the
 * {@code "io.smallrye.health.check."org.kie.kogito.addons.quarkus.knative.eventing.KSinkInjectionHealthCheck".enabled"} property.
 */
class KSinkInjectionHealthCheckConfigSource implements ConfigSource {

    private static final String CONFIG_ALIAS = org.kie.kogito.addons.quarkus.knative.eventing.KSinkInjectionHealthCheck.CONFIG_ALIAS;

    private final String configValue;

    private final int ordinal;

    private static final Set<String> propertyNames = Set.of(
            "io.smallrye.health.check." + KSinkInjectionHealthCheck.class.getName() + ".enabled",
            CONFIG_ALIAS);

    public KSinkInjectionHealthCheckConfigSource(int ordinal, String configValue) {
        this.ordinal = ordinal;
        this.configValue = configValue;
    }

    @Override
    public Set<String> getPropertyNames() {
        return propertyNames;
    }

    @Override
    public String getValue(String propertyName) {
        return propertyNames.contains(propertyName) ? configValue : null;
    }

    @Override
    public String getName() {
        return KSinkInjectionHealthCheckConfigSource.class.getSimpleName();
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }
}
