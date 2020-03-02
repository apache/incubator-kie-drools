/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.remote;

import java.util.Properties;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CommonConfigTest {

    private static final String TEST_PROPERTY = "test-property";
    private static final String TEST_PROPERTY_VALUE = "test-property-value";

    @Test
    public void testGetStaticConfig() {
        final Properties config = CommonConfig.getStatic();
        Assertions.assertThat(config).containsKeys(CommonConfig.KEY_SERIALIZER_KEY,
                                                   CommonConfig.VALUE_SERIALIZER_KEY,
                                                   CommonConfig.KEY_DESERIALIZER_KEY,
                                                   CommonConfig.VALUE_DESERIALIZER_KEY,
                                                   CommonConfig.GROUP_ID_CONFIG);
    }

    @Test
    public void testProducerConfig() {
        final Properties producerConfig = CommonConfig.getProducerConfig();
        Assertions.assertThat(producerConfig).isNotNull();
        Assertions.assertThat(producerConfig.getProperty(TEST_PROPERTY)).isEqualTo(TEST_PROPERTY_VALUE);
    }
}
