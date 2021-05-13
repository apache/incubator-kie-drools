/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.conf;

import java.util.Optional;

import org.kie.kogito.KogitoGAV;

public class StaticConfigBean implements ConfigBean {

    private String serviceUrl;
    private boolean useCloudEvents = true;
    private KogitoGAV gav;

    public StaticConfigBean() {
    }

    public StaticConfigBean(String serviceUrl, boolean useCloudEvents, KogitoGAV gav) {
        this.serviceUrl = serviceUrl;
        this.useCloudEvents = useCloudEvents;
        this.gav = gav;
    }

    protected void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    protected void setCloudEvents(boolean useCloudEvents) {
        this.useCloudEvents = useCloudEvents;
    }

    public void setGav(KogitoGAV gav) {
        this.gav = gav;
    }

    @Override
    public boolean useCloudEvents() {
        return useCloudEvents;
    }

    @Override
    public String getServiceUrl() {
        return serviceUrl;
    }

    @Override
    public Optional<KogitoGAV> getGav() {
        return Optional.ofNullable(gav);
    }
}
