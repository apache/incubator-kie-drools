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

package org.kie.kogito.taskassigning.process.service.client;

public class ProcessServiceClientConfig {

    private String serviceUrl;

    private long connectTimeoutMillis;

    private long readTimeoutMillis;

    private ProcessServiceClientConfig() {
    }

    private ProcessServiceClientConfig(String serviceUrl, long connectTimeoutMillis, long readTimeoutMillis) {
        this.serviceUrl = serviceUrl;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public long getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public long getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public static class Builder {

        private String serviceUrl;
        private long connectTimeoutMillis;
        private long readTimeoutMillis;

        private Builder() {
        }

        public ProcessServiceClientConfig build() {
            return new ProcessServiceClientConfig(serviceUrl, connectTimeoutMillis, readTimeoutMillis);
        }

        public Builder serviceUrl(String serviceUrl) {
            this.serviceUrl = serviceUrl;
            return this;
        }

        /**
         * Set the connect timeout in milliseconds.
         * <p>
         * Like JAX-RS's <code>javax.ws.rs.client.ClientBuilder</code>'s
         * <code>connectTimeout</code> method, specifying a timeout of 0 represents
         * infinity, and negative values are not allowed.
         */
        public Builder connectTimeoutMillis(long connectTimeoutMillis) {
            if (connectTimeoutMillis < 0) {
                throw new IllegalArgumentException("Cannot set a negative connectTimeoutMillis value");
            }
            this.connectTimeoutMillis = connectTimeoutMillis;
            return this;
        }

        /**
         * Set the read timeout.
         * <p>
         * Like JAX-RS's <code>javax.ws.rs.client.ClientBuilder</code>'s
         * <code>readTimeout</code> method, specifying a timeout of 0 represents
         * infinity, and negative values are not allowed.
         * </p>
         */
        public Builder readTimeoutMillis(long readTimeoutMillis) {
            if (readTimeoutMillis < 0) {
                throw new IllegalArgumentException("Cannot set a negative readTimeoutMillis value");
            }
            this.readTimeoutMillis = readTimeoutMillis;
            return this;
        }
    }
}
