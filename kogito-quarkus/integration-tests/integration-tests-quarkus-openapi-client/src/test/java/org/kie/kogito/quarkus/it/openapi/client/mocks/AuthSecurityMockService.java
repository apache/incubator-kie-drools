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

package org.kie.kogito.quarkus.it.openapi.client.mocks;

import org.kie.kogito.test.utils.SocketUtils;

/**
 * Mock Service to emulate a secured service for OpenAPI Client calls with tokens, basic auth, and etc.
 */
public class AuthSecurityMockService extends MockServiceConfigurer {

    public static final MockServerConfig SEC_20 =
            new MockServerConfig(SocketUtils.findAvailablePort(), "{}", "/iq9MzY/watsonorchestrate", "authWithApiKeyServer2");

    public static final MockServerConfig SEC_30 =
            new MockServerConfig(SocketUtils.findAvailablePort(), "{}", "/", "authWithApiKeyServer3");

    public static final MockServerConfig SEC_20_NO_AUTH =
            new MockServerConfig(SocketUtils.findAvailablePort(), "{}", "/unprotected", "authWithApiKeyServer2NoAuth");

    public static final MockServerConfig SEC_30_NO_AUTH =
            new MockServerConfig(SocketUtils.findAvailablePort(), "{}", "/unprotected", "authWithApiKeyServer3NoAuth");

    public AuthSecurityMockService() {
        super(SEC_20, SEC_30, SEC_20_NO_AUTH, SEC_30_NO_AUTH);
    }

}
