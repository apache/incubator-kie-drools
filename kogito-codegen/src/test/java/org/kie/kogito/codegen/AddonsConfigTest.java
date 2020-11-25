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

package org.kie.kogito.codegen;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddonsConfigTest {

    @Test
    public void allAddonsAreDisabledInDefaultConfiguration() {
        AddonsConfig addonsConfig = AddonsConfig.DEFAULT;
        assertThat(addonsConfig.useMonitoring()).isFalse();
        assertThat(addonsConfig.useTracing()).isFalse();
        assertThat(addonsConfig.usePersistence()).isFalse();
    }

    @Test
    public void addonsAreProperlyActivated() {
        AddonsConfig addonsConfig = new AddonsConfig();

        assertThat(addonsConfig.useMonitoring()).isFalse();
        assertThat(addonsConfig.withMonitoring(true).useMonitoring()).isTrue();

        assertThat(addonsConfig.usePrometheusMonitoring()).isFalse();
        assertThat(addonsConfig.withPrometheusMonitoring(true).usePrometheusMonitoring()).isTrue();

        assertThat(addonsConfig.useTracing()).isFalse();
        assertThat(addonsConfig.withTracing(true).useTracing()).isTrue();

        assertThat(addonsConfig.usePersistence()).isFalse();
        assertThat(addonsConfig.withPersistence(true).usePersistence()).isTrue();

        assertThat(addonsConfig.useKnativeEventing()).isFalse();
        assertThat(addonsConfig.withKnativeEventing(true).useKnativeEventing()).isTrue();

        assertThat(addonsConfig.useCloudEvents()).isFalse();
        assertThat(addonsConfig.withCloudEvents(true).useCloudEvents()).isTrue();
    }
}
