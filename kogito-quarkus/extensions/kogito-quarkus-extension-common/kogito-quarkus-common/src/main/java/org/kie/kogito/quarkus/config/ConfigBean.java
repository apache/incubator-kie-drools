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

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.kie.kogito.KogitoGAV;
import org.kie.kogito.config.StaticConfigBean;

@Singleton
public class ConfigBean extends StaticConfigBean {

    @Inject
    Instance<KogitoRuntimeConfig> runtimeConfig;

    @Inject
    Instance<KogitoBuildTimeConfig> buildTimeConfig;

    @Inject
    KogitoGAV gav;

    @Override
    public String getServiceUrl() {
        return runtimeConfig.get().serviceUrl.orElse("");
    }

    @Override
    public short processInstanceLimit() {
        return runtimeConfig.get().processInstanceLimit;
    }

    @Override
    public Optional<KogitoGAV> getGav() {
        return Optional.ofNullable(gav);
    }

    @Override
    public boolean failOnEmptyBean() {
        return buildTimeConfig.get().failOnEmptyBean;
    }

    @Override
    public boolean useCloudEvents() {
        return buildTimeConfig.get().useCloudEvents;
    }
}
