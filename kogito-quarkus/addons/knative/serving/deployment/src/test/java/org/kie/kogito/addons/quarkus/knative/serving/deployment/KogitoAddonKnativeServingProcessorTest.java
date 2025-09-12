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

package org.kie.kogito.addons.quarkus.knative.serving.deployment;

import org.junit.jupiter.api.Test;
import org.kie.kogito.addons.quarkus.knative.serving.customfunctions.CloudEventKnativeParamsDecorator;
import org.kie.kogito.addons.quarkus.knative.serving.customfunctions.GetParamsDecorator;
import org.kie.kogito.addons.quarkus.knative.serving.customfunctions.PlainJsonKnativeParamsDecorator;

import static org.assertj.core.api.Assertions.assertThat;

class KogitoAddonKnativeServingProcessorTest {

    private final KogitoAddonKnativeServingProcessor processor = new KogitoAddonKnativeServingProcessor();

    @Test
    void feature() {
        assertThat(processor.feature()).isNotNull();
        assertThat(processor.feature().getName()).isEqualTo(KogitoAddonKnativeServingProcessor.FEATURE);
    }

    @Test
    void reflectiveClasses() {
        assertThat(processor.reflectiveClasses()).isNotNull();
        assertThat(processor.reflectiveClasses().getClassNames())
                .containsExactlyInAnyOrder(CloudEventKnativeParamsDecorator.class.getName(),
                        PlainJsonKnativeParamsDecorator.class.getName(), GetParamsDecorator.class.getName());
    }
}
