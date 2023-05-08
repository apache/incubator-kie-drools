/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.management.quarkus.deployment;

import org.junit.jupiter.api.Test;

import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

import static org.assertj.core.api.Assertions.assertThat;

class KogitoAddOnJobsManagementProcessorTest {

    private final KogitoAddOnJobsManagementProcessor processor = new KogitoAddOnJobsManagementProcessor();

    @Test
    void feature() {
        assertThat(processor.feature()).isNotNull();
        assertThat(processor.feature().getName()).isEqualTo("kogito-addon-jobs-management-extension");
    }

    @Test
    void jobsApiReflection() {
        ReflectiveClassBuildItem buildItem = processor.jobsApiReflection();
        assertThat(buildItem.isConstructors()).isTrue();
        assertThat(buildItem.isMethods()).isTrue();
        assertThat(buildItem.isFields()).isTrue();
        assertThat(buildItem.getClassNames()).hasSize(21);
    }
}
