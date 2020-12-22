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
import static org.kie.kogito.codegen.AddonsConfig.DEFAULT;
import static org.kie.kogito.codegen.AddonsConfig.builder;

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
    }

    @Test
    public void addonsAreProperlyActivated() {;
        assertThat(AddonsConfig.DEFAULT.useMonitoring()).isFalse();
        assertThat(builder().withMonitoring(true).build().useMonitoring()).isTrue();

        assertThat(AddonsConfig.DEFAULT.usePrometheusMonitoring()).isFalse();
        assertThat(builder().withPrometheusMonitoring(true).build().usePrometheusMonitoring()).isTrue();

        assertThat(AddonsConfig.DEFAULT.useTracing()).isFalse();
        assertThat(builder().withTracing(true).build().useTracing()).isTrue();

        assertThat(AddonsConfig.DEFAULT.usePersistence()).isFalse();
        assertThat(builder().withPersistence(true).build().usePersistence()).isTrue();

        assertThat(AddonsConfig.DEFAULT.useKnativeEventing()).isFalse();
        assertThat(builder().withKnativeEventing(true).build().useKnativeEventing()).isTrue();

        assertThat(AddonsConfig.DEFAULT.useCloudEvents()).isFalse();
        assertThat(builder().withCloudEvents(true).build().useCloudEvents()).isTrue();
    }
}
