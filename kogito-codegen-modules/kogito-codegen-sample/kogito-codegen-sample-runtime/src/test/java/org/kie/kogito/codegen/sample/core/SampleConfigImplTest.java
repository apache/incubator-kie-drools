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
package org.kie.kogito.codegen.sample.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SampleConfigImplTest {

    @Test
    void numberOfCopy() {
        assertThat(new SampleConfigImpl().numberOfCopy()).isEqualTo(1);
        assertThat(new SampleConfigImpl(10).numberOfCopy()).isEqualTo(10);

        SampleConfigImpl sampleConfig = new SampleConfigImpl();
        sampleConfig.setNumberOfCopy(10);
        assertThat(sampleConfig.numberOfCopy()).isEqualTo(10);
    }
}