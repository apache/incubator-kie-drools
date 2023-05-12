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
package org.kie.kogito.event.cloudevents.utils;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.cloudevents.SpecVersion;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

abstract class BaseCloudEventValidatorTest<T extends BaseCloudEventValidator> {

    private final SpecVersion supportedVersion;

    private final T cloudEventValidator;

    BaseCloudEventValidatorTest(T cloudEventValidator, SpecVersion supportedVersion) {
        this.cloudEventValidator = cloudEventValidator;
        this.supportedVersion = supportedVersion;
    }

    protected abstract Map<String, Object> createValidCloudEvent();

    private Map<String, Object> cloudEventMissing(String attribute) {
        Map<String, Object> cloudEvent = createValidCloudEvent();
        cloudEvent.remove(attribute);
        return cloudEvent;
    }

    private Map<String, Object> cloudEventWithEmptyAttribute(String attribute) {
        Map<String, Object> cloudEvent = createValidCloudEvent();
        cloudEvent.put(attribute, "");
        return cloudEvent;
    }

    public static Stream<Arguments> testInvalidRfcXXXXAttributeSource() {
        return Stream.of(
                Arguments.of("invalid"),
                Arguments.of(new Object()));
    }

    @Test
    void testValidCloudEvent() {
        assertThatCode(() -> cloudEventValidator.validateCloudEvent(createValidCloudEvent()))
                .doesNotThrowAnyException();
    }

    @Test
    void testMissingMandatoryAttribute() {
        supportedVersion.getMandatoryAttributes().forEach(mandatoryAttribute -> {
            Map<String, Object> cloudEvent = cloudEventMissing(mandatoryAttribute);

            InvalidCloudEventException ex = Assertions.assertThrows(InvalidCloudEventException.class,
                    () -> cloudEventValidator.validateCloudEvent(cloudEvent));

            assertThat(ex.getErrors())
                    .hasSize(1)
                    .contains("Missing mandatory attribute: " + mandatoryAttribute);
        });
    }

    @ParameterizedTest
    @MethodSource("testInvalidRfcXXXXAttributeSource")
    void testInvalidRfc3339Attribute() {
        Map<String, Object> cloudEvent = createValidCloudEvent();
        cloudEvent.put(cloudEventValidator.getRfc3339Attribute(), "invalid");

        InvalidCloudEventException ex = Assertions.assertThrows(InvalidCloudEventException.class,
                () -> cloudEventValidator.validateCloudEvent(cloudEvent));

        assertThat(ex.getErrors())
                .containsOnly(cloudEventValidator.getRfc3339Attribute() + " MUST adhere to the format specified in RFC 3339 (https://datatracker.ietf.org/doc/html/rfc3339).");
    }

    @Test
    void testInvalidRfc2046Attribute() {
        Map<String, Object> cloudEvent = createValidCloudEvent();
        cloudEvent.put(cloudEventValidator.getRfc2046Attribute(), "invalid");

        InvalidCloudEventException ex = Assertions.assertThrows(InvalidCloudEventException.class,
                () -> cloudEventValidator.validateCloudEvent(cloudEvent));

        assertThat(ex.getErrors())
                .containsOnly(cloudEventValidator.getRfc2046Attribute() + " MUST adhere to the format specified in RFC 2046 (https://datatracker.ietf.org/doc/html/rfc2046).");
    }

    @Test
    void testNonStringRfc2046Attribute() {
        Map<String, Object> cloudEvent = createValidCloudEvent();
        cloudEvent.put(cloudEventValidator.getRfc2046Attribute(), new Object());

        assertThatExceptionOfType(ClassCastException.class)
                .isThrownBy(() -> cloudEventValidator.validateCloudEvent(cloudEvent));
    }

    @Test
    void testNonEmptyAttribute() {
        cloudEventValidator.getNonEmptyAttributes().forEach(nonEmptyAttribute -> {
            Map<String, Object> cloudEvent = cloudEventWithEmptyAttribute(nonEmptyAttribute);

            InvalidCloudEventException ex = Assertions.assertThrows(InvalidCloudEventException.class,
                    () -> cloudEventValidator.validateCloudEvent(cloudEvent));

            assertThat(ex.getErrors())
                    .contains(nonEmptyAttribute + " must be a non-empty String.");
        });
    }
}
