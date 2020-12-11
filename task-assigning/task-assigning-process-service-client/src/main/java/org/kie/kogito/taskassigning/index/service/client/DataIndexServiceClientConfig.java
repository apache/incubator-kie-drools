/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.index.service.client;

import java.net.URL;

import org.kie.kogito.taskassigning.config.ServiceClientConfig;

public class DataIndexServiceClientConfig extends ServiceClientConfig {

    private DataIndexServiceClientConfig() {
    }

    private DataIndexServiceClientConfig(URL serviceUrl, long connectTimeoutMillis, long readTimeoutMillis) {
        super(serviceUrl, connectTimeoutMillis, readTimeoutMillis);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends ServiceClientConfig.Builder<DataIndexServiceClientConfig, Builder> {

        private Builder() {
        }

        @Override
        public DataIndexServiceClientConfig build() {
            return new DataIndexServiceClientConfig(serviceUrl, connectTimeoutMillis, readTimeoutMillis);
        }
    }
}
