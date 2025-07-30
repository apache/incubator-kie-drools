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
package org.kie.kogito.codegen.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.codegen.api.AddonsConfig.DEFAULT;
import static org.kie.kogito.codegen.api.AddonsConfig.builder;

public class AddonsConfigTest {

    @Test
    public void allAddonsAreDisabledInDefaultConfiguration() {
        AddonsConfig addonsConfig = DEFAULT;
        assertThat(addonsConfig.useMonitoring()).isFalse();
        assertThat(addonsConfig.useTracing()).isFalse();
        assertThat(addonsConfig.usePersistence()).isFalse();
        assertThat(addonsConfig.useCloudEvents()).isFalse();
        assertThat(addonsConfig.usePersistence()).isFalse();
        assertThat(addonsConfig.useCloudEvents()).isFalse();
        assertThat(addonsConfig.useProcessSVG()).isFalse();
        assertThat(addonsConfig.useSourceFiles()).isFalse();
    }

    @Test
    public void addonsAreProperlyActivated() {
        ;
        assertThat(DEFAULT.useMonitoring()).isFalse();
        assertThat(builder().withMonitoring(true).build().useMonitoring()).isTrue();

        assertThat(DEFAULT.usePrometheusMonitoring()).isFalse();
        assertThat(builder().withPrometheusMonitoring(true).build().usePrometheusMonitoring()).isTrue();

        assertThat(DEFAULT.useTracing()).isFalse();
        assertThat(builder().withTracing(true).build().useTracing()).isTrue();

        assertThat(DEFAULT.usePersistence()).isFalse();
        assertThat(builder().withPersistence(true).build().usePersistence()).isTrue();

        assertThat(DEFAULT.useCloudEvents()).isFalse();
        assertThat(builder().withCloudEvents(true).build().useCloudEvents()).isTrue();

        assertThat(DEFAULT.useExplainability()).isFalse();
        assertThat(builder().withExplainability(true).build().useExplainability()).isTrue();

        assertThat(DEFAULT.useProcessSVG()).isFalse();
        assertThat(builder().withProcessSVG(true).build().useProcessSVG()).isTrue();

        assertThat(DEFAULT.useSourceFiles()).isFalse();
        assertThat(builder().withSourceFiles(true).build().useSourceFiles()).isTrue();
    }
}
