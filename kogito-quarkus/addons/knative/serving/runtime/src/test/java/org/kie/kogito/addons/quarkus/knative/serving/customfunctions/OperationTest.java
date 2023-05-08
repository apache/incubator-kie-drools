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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.Operation.CLOUD_EVENT_PARAMETER_NAME;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.Operation.NAMESPACE_PARAMETER_NAME;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.Operation.PATH_PARAMETER_NAME;

class OperationTest {

    public static Stream<Arguments> parseSource() {
        return Stream.of(
                Arguments.of("service", Operation.builder().withService("service").build()),

                Arguments.of("service?", Operation.builder().withService("service").build()),

                Arguments.of("service?" + PATH_PARAMETER_NAME + "=/my_path", Operation.builder().withService("service").withPath("/my_path").build()),

                Arguments.of("service?" + CLOUD_EVENT_PARAMETER_NAME + "=true", Operation.builder().withService("service").withIsCloudEvent(true).build()),

                Arguments.of("service?" + NAMESPACE_PARAMETER_NAME + "=my_namespace", Operation.builder()
                        .withService("service").withNamespace("my_namespace").build()),

                Arguments.of("service?" + NAMESPACE_PARAMETER_NAME + "=my_namespace&" + PATH_PARAMETER_NAME + "=/my_path",
                        Operation.builder().withService("service").withNamespace("my_namespace").withPath("/my_path").build()),

                Arguments.of("service?" + NAMESPACE_PARAMETER_NAME + "=my_namespace&" + CLOUD_EVENT_PARAMETER_NAME + "=true",
                        Operation.builder().withService("service").withNamespace("my_namespace").withIsCloudEvent(true).build()),

                Arguments.of("service?" + PATH_PARAMETER_NAME + "=/my_path&" + CLOUD_EVENT_PARAMETER_NAME + "=true",
                        Operation.builder().withService("service").withPath("/my_path").withIsCloudEvent(true).build()),

                Arguments.of("service?" + NAMESPACE_PARAMETER_NAME + "=my_namespace&" + PATH_PARAMETER_NAME + "=/my_path&" + CLOUD_EVENT_PARAMETER_NAME + "=true",
                        Operation.builder().withService("service").withNamespace("my_namespace").withPath("/my_path").withIsCloudEvent(true).build()));
    }

    @ParameterizedTest
    @MethodSource("parseSource")
    void parse(String operationValue, Operation expectedOperation) {
        assertThat(Operation.parse(operationValue)).isEqualTo(expectedOperation);
    }
}