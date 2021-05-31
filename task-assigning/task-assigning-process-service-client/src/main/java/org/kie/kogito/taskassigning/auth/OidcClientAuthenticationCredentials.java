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

package org.kie.kogito.taskassigning.auth;

public class OidcClientAuthenticationCredentials implements AuthenticationCredentials {

    private final String oidcClient;

    private OidcClientAuthenticationCredentials(String oidcClient) {
        if (oidcClient == null) {
            throw new IllegalArgumentException("The oidcClient can not be null");
        }
        this.oidcClient = oidcClient;
    }

    public String getOidcClient() {
        return oidcClient;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String oidcClient;

        private Builder() {
        }

        public Builder oidcClient(String oidcClient) {
            this.oidcClient = oidcClient;
            return this;
        }

        public OidcClientAuthenticationCredentials build() {
            return new OidcClientAuthenticationCredentials(oidcClient);
        }
    }
}