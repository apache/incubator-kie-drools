/**
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
package org.kie.dmn.legacy.tests.validation.v1_1;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.ValidatorUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

class ValidatorDecisionTableTest
        extends AbstractValidatorTest {

    @Test
    void dtable_empty_entry_ReaderInput() throws IOException {
        try (final Reader reader = getReader("DTABLE_EMPTY_ENTRY.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
        }
    }

    @Test
    void dtable_empty_entry_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("DTABLE_EMPTY_ENTRY.dmn"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
    }

    @Test
    void dtable_empty_entry_DefintionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("DTABLE_EMPTY_ENTRY.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DTABLE_PRIORITY_MISSING_OUTVALS"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
    }

    @Test
    void dtable_multipleout_name_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT.dmn" )) {
            List<DMNMessage> validate = validator.validate( reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(6);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_NAME))).isTrue();
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF))).isTrue();
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME))).isTrue();
        }
    }

    @Test
    void dtable_multipleout_name_FileInput() {
        List<DMNMessage> validate = validator.validate( getFile( "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(6);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_NAME))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME))).isTrue();
    }

    @Test
    void dtable_multipleout_name_DefinitionsInput() {
        List<DMNMessage> validate = validator.validate(
                getDefinitions( "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT.dmn",
                                "https://github.com/kiegroup/kie-dmn",
                                "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(6);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_NAME))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME))).isTrue();
    }

    @Test
    void dtable_priority_missing_outvals_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "DTABLE_PRIORITY_MISSING_OUTVALS.dmn" )) {
            List<DMNMessage> validate = validator.validate( reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).isNotEmpty();
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_OUTPUT_VALUES))).isTrue();
        }
    }

    @Test
    void dtable_priority_missing_outvals_FileInput() {
        List<DMNMessage> validate = validator.validate( getFile( "DTABLE_PRIORITY_MISSING_OUTVALS.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).isNotEmpty();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_OUTPUT_VALUES))).isTrue();
    }

    @Test
    void dtable_priority_missing_outvals_DefinitionsInput() {
        List<DMNMessage> validate = validator.validate(
                getDefinitions( "DTABLE_PRIORITY_MISSING_OUTVALS.dmn",
                                "https://github.com/kiegroup/kie-dmn",
                                "DTABLE_PRIORITY_MISSING_OUTVALS"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).isNotEmpty();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_OUTPUT_VALUES))).isTrue();
    }

    @Test
    void dtable_singleout_noname_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT.dmn" )) {
            List<DMNMessage> validate = validator.validate( reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.ILLEGAL_USE_OF_NAME))).isTrue();
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.ILLEGAL_USE_OF_TYPEREF))).isTrue();
        }
    }

    @Test
    void dtable_singleout_noname_FileInput() {
        List<DMNMessage> validate = validator.validate( getFile( "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.ILLEGAL_USE_OF_NAME))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.ILLEGAL_USE_OF_TYPEREF))).isTrue();
    }

    @Test
    void dtable_singleout_noname_DefinitionsInput() {
        List<DMNMessage> validate = validator.validate(
                getDefinitions("DTABLE_SINGLEOUTPUT_WRONG_OUTPUT.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.ILLEGAL_USE_OF_NAME))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.ILLEGAL_USE_OF_TYPEREF))).isTrue();
    }
}
