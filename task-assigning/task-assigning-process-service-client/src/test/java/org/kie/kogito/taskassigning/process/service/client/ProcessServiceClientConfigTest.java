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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessServiceClientConfigTest {

    private static final String SERVICE_URL = "SERVICE_URL";
    private static final long CONNECT_TIMEOUT = 1;
    private static final long READ_TIMOUT = 2;

    private ProcessServiceClientConfig config;

    @BeforeEach
    void setUp() {
        config = ProcessServiceClientConfig.newBuilder()
                .serviceUrl(SERVICE_URL)
                .connectTimeoutMillis(CONNECT_TIMEOUT)
                .readTimeoutMillis(READ_TIMOUT).build();
    }

    @Test
    void getServiceURL() {
        assertThat(config.getServiceUrl()).isEqualTo(SERVICE_URL);
    }

    @Test
    void getConnectTimoutMillis() {
        assertThat(config.getConnectTimeoutMillis()).isEqualTo(CONNECT_TIMEOUT);
    }

    @Test
    void getReadTimoutMillis() {
        assertThat(config.getReadTimeoutMillis()).isEqualTo(READ_TIMOUT);
    }
}
