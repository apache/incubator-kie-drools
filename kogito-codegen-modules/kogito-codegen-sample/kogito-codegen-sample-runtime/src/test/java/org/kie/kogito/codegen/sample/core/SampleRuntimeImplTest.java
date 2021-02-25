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

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Config;
import org.kie.kogito.StaticApplication;
import org.kie.kogito.StaticConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SampleRuntimeImplTest {

    Config config = new StaticConfig(null, new SampleConfigImpl(10));
    Application application = new StaticApplication(config);

    @Test
    void initApplication() {

        SampleRuntimeImpl sampleRuntime = new SampleRuntimeImpl(application);
        assertThat(sampleRuntime.config).isNotNull();

        sampleRuntime = new SampleRuntimeImpl();
        assertThat(sampleRuntime.config).isNull();
        sampleRuntime.initApplication(application);
        assertThat(sampleRuntime.config).isNotNull();
    }

    @Test
    void addModels() {
        SampleRuntimeImpl sampleRuntime = new SampleRuntimeImpl(application);
        assertThat(sampleRuntime.rawContent).isEmpty();
        sampleRuntime.addModels(Collections.singletonMap("name", "content"));
        assertThat(sampleRuntime.rawContent).hasSize(1);
    }

    @Test
    void getModel() {
        SampleRuntimeImpl sampleRuntime = new SampleRuntimeImpl(application);
        sampleRuntime.addModels(Collections.singletonMap("name", "content"));

        assertThat(sampleRuntime.getModel("name"))
                .isNotNull();

        assertThatThrownBy(() -> sampleRuntime.getModel("notExisting"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
