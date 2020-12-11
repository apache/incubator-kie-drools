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

package org.kie.kogito.taskassigning.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractServiceClientConfigTest<T extends ServiceClientConfig> {

    public static final String SERVICE_URL = "http://localhost:8080";
    public static final long CONNECT_TIMEOUT = 1;
    public static final long READ_TIMOUT = 2;

    private T config;

    @BeforeEach
    void setUp() {
        config = createConfig();
    }

    protected abstract T createConfig();

    @Test
    void getServiceURL() {
        assertThat(config.getServiceUrl())
                .isNotNull()
                .hasToString(SERVICE_URL);
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
