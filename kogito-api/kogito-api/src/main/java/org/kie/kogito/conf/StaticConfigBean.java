/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.conf;

import java.util.Optional;

public class StaticConfigBean implements ConfigBean {

    private String serviceUrl;
    private Optional<Boolean> useCloudEvents = Optional.empty();

    public StaticConfigBean() {
    }

    public StaticConfigBean(String serviceUrl, boolean useCloudEvents) {
        this.serviceUrl = serviceUrl;
        this.useCloudEvents = Optional.of(useCloudEvents);
    }

    protected void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    protected void setCloudEvents(Optional<Boolean> useCloudEvents) {
        this.useCloudEvents = useCloudEvents;
    }

    @Override
    public Optional<Boolean> useCloudEvents() {
        return useCloudEvents;
    }

    @Override
    public String getServiceUrl() {
        return serviceUrl;
    }
}
