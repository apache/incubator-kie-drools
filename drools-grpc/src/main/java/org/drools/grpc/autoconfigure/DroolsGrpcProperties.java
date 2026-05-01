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
package org.drools.grpc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "drools.grpc")
public class DroolsGrpcProperties {

    private int port = 50051;
    private int sessionPoolSize = 10;
    private boolean reflectionEnabled = true;
    private boolean metricsEnabled = true;
    private final Auth auth = new Auth();
    private final Tls tls = new Tls();

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSessionPoolSize() {
        return sessionPoolSize;
    }

    public void setSessionPoolSize(int sessionPoolSize) {
        this.sessionPoolSize = sessionPoolSize;
    }

    public boolean isReflectionEnabled() {
        return reflectionEnabled;
    }

    public void setReflectionEnabled(boolean reflectionEnabled) {
        this.reflectionEnabled = reflectionEnabled;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public void setMetricsEnabled(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
    }

    public Auth getAuth() {
        return auth;
    }

    public Tls getTls() {
        return tls;
    }

    public static class Auth {

        private boolean enabled = false;
        private String staticToken;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getStaticToken() {
            return staticToken;
        }

        public void setStaticToken(String staticToken) {
            this.staticToken = staticToken;
        }
    }

    public static class Tls {

        private boolean enabled = false;
        private String certChainPath;
        private String privateKeyPath;
        private String trustCertPath;
        private String clientAuth = "NONE";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getCertChainPath() {
            return certChainPath;
        }

        public void setCertChainPath(String certChainPath) {
            this.certChainPath = certChainPath;
        }

        public String getPrivateKeyPath() {
            return privateKeyPath;
        }

        public void setPrivateKeyPath(String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
        }

        public String getTrustCertPath() {
            return trustCertPath;
        }

        public void setTrustCertPath(String trustCertPath) {
            this.trustCertPath = trustCertPath;
        }

        public String getClientAuth() {
            return clientAuth;
        }

        public void setClientAuth(String clientAuth) {
            this.clientAuth = clientAuth;
        }
    }
}
