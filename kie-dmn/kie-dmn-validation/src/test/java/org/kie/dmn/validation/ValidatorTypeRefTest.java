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
package org.kie.dmn.validation;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

class ValidatorTypeRefTest extends AbstractValidatorTest {

    @Test
    void typeref_no_feel_type_ReaderInput() throws IOException {
        try (final Reader reader = getReader("typeref/TYPEREF_NO_FEEL_TYPE.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void typeref_no_feel_type_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("typeref/TYPEREF_NO_FEEL_TYPE.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND))).isTrue();
    }

    @Test
    void typeref_no_feel_type_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("typeref/TYPEREF_NO_FEEL_TYPE.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NO_FEEL_TYPE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND))).isTrue();
    }

    @Test
    void typeref_no_ns_ReaderInput() throws IOException {
        try (final Reader reader = getReader("typeref/TYPEREF_NO_NS.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void typeref_no_ns_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("typeref/TYPEREF_NO_NS.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND))).isTrue();
    }

    @Test
    void typeref_no_ns_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("typeref/TYPEREF_NO_NS.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NO_NS"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND))).isTrue();
    }

    @Test
    void typeref_not_feel_not_def_ReaderInput() throws IOException {
        try (final Reader reader = getReader("typeref/TYPEREF_NOT_FEEL_NOT_DEF.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(3);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME))).isTrue();
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void typeref_not_feel_not_def_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("typeref/TYPEREF_NOT_FEEL_NOT_DEF.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(3);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND))).isTrue();
    }

    @Test
    void typeref_not_feel_not_def_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("typeref/TYPEREF_NOT_FEEL_NOT_DEF.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NOT_FEEL_NOT_DEF"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(3);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND))).isTrue();
    }

    @Test
    void typeref_not_feel_not_defValidResourceInput() throws IOException {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any DMNMessageTypeId.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        try (final Reader reader = getReader("typeref/TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
        }
    }

    @Test
    void typeref_not_feel_not_defValidFileInput() {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any DMNMessageTypeId.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        final List<DMNMessage> validate = validator.validate(
                getFile("typeref/TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void typeref_not_feel_not_defValidDefinitionsInput() {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any DMNMessageTypeId.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("typeref/TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NOT_FEEL_NOT_DEF_valid"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void bkm_with_no_typeref_is_ok_DefinitionsInput() {
        // DROOLS-2631
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("typeref/BKM_WITH_NO_TYPEREF_IS_OK.dmn", "http://www.trisotech.com/dmn/definitions/_7e8d7561-657a-4729-b2a9-5a6279df6d5d", "Drawing 1"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }
}
