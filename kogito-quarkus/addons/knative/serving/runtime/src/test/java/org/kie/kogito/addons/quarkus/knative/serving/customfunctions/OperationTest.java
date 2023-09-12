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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.vertx.core.http.HttpMethod;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.Operation.CLOUD_EVENT_PARAMETER_NAME;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.Operation.METHOD_PARAMETER_NAME;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.Operation.PATH_PARAMETER_NAME;

class OperationTest {

    public static final String SERVICE = "service";

    public static Stream<Arguments> parseSource() {
        return Stream.of(
                Arguments.of(SERVICE, Operation.builder().withService(SERVICE).build()),

                Arguments.of("service?", Operation.builder().withService(SERVICE).build()),

                Arguments.of("service?" + PATH_PARAMETER_NAME + "=/my_path", Operation.builder().withService(SERVICE).withPath("/my_path").build()),

                Arguments.of("service?" + CLOUD_EVENT_PARAMETER_NAME + "=true", Operation.builder().withService(SERVICE).withIsCloudEvent(true).build()),

                Arguments.of("service?" + METHOD_PARAMETER_NAME + "=GET", Operation.builder().withService(SERVICE).withMethod(HttpMethod.GET).build()),

                Arguments.of("service?" + METHOD_PARAMETER_NAME + "=get", Operation.builder().withService(SERVICE).withMethod(HttpMethod.GET).build()),

                Arguments.of("service?" + PATH_PARAMETER_NAME + "=/my_path&" + CLOUD_EVENT_PARAMETER_NAME + "=true",
                        Operation.builder().withService(SERVICE).withPath("/my_path").withIsCloudEvent(true).build()),

                Arguments.of("service?" + PATH_PARAMETER_NAME + "=/my_path&" + CLOUD_EVENT_PARAMETER_NAME + "=false&" + METHOD_PARAMETER_NAME + "=GET",
                        Operation.builder().withService(SERVICE).withPath("/my_path").withIsCloudEvent(false).withMethod(HttpMethod.GET).build()));
    }

    public static Stream<Arguments> invalidOperationSource() {
        return Stream.of(
                Arguments.of(Operation.builder().withService(SERVICE).withMethod(HttpMethod.DELETE)),
                Arguments.of(Operation.builder().withService(SERVICE).withIsCloudEvent(true).withMethod(HttpMethod.GET)));
    }

    @ParameterizedTest
    @MethodSource("parseSource")
    void parse(String operationValue, Operation expectedOperation) {
        assertThat(Operation.parse(operationValue)).isEqualTo(expectedOperation);
    }

    @ParameterizedTest
    @MethodSource("invalidOperationSource")
    void invalidOperation(Operation.Builder operationBuilder) {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(operationBuilder::build);
    }
}
