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
package org.kie.kogito.gradle.plugin;

import java.io.File;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.gradle.plugin.GenerateModelTask.GENERATE_MODEL_TASK_NAME;
import static org.kie.kogito.gradle.plugin.KogitoGradlePlugin.PLUGIN_ID;

class KogitoGradlePluginTest {

    private Project project;

    @BeforeEach
    void setup() {
        // Create an in-memory project and apply the plugin
        project = ProjectBuilder.builder().build();
        project.getPlugins().apply("java");
        project.getPlugins().apply(PLUGIN_ID);

    }

    @Test
    void pluginRegistersGenerateModelTask() {
        // Verify the task is registered
        assertThat(project.getTasks().findByName(GENERATE_MODEL_TASK_NAME)).isNotNull();
    }

    @Test
    void extensionHasSensibleDefaults() {
        KogitoGradleExtension ext = project.getExtensions().getByType(KogitoGradleExtension.class);

        // Default generated sources directory file is build/generated/sources/kogito
        File defaultGeneratedSourcesDir = ext.getGeneratedSourcesDir().get();
        assertThat(defaultGeneratedSourcesDir).isEqualTo(project.getLayout().getBuildDirectory().getAsFile().get().toPath()
                .resolve("generated")
                .resolve("sources")
                .resolve("kogito")
                .toFile());
    }
}   