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
package org.kie.kogito.jobs.knative.eventing.quarkus.deployment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.knative.eventing.quarkus.deployment.KogitoAddOnJobsKnativeEventingProcessor.*;

@ExtendWith(MockitoExtension.class)
class KogitoAddOnJobsKnativeEventingProcessorTest {

    @Test
    void jobsApiReflection() {
        ReflectiveClassBuildItem reflectiveClassBuildItem = new KogitoAddOnJobsKnativeEventingProcessor().jobsApiReflection();
        assertThat(reflectiveClassBuildItem.getClassNames()).hasSize(21);
    }

    @Test
    void featureBuildItem() {
        FeatureBuildItem featureBuildItem = new KogitoAddOnJobsKnativeEventingProcessor().feature();
        assertThat(featureBuildItem).isNotNull();
        assertThat(featureBuildItem.getName()).isEqualTo(FEATURE);
    }

}
