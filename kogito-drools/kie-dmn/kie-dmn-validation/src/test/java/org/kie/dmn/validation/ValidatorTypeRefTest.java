/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class ValidatorTypeRefTest extends AbstractValidatorTest {

    @Test
    public void testTYPEREF_NO_FEEL_TYPE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("typeref/TYPEREF_NO_FEEL_TYPE.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_REF_NOT_FOUND)));
        }
    }

    @Test
    public void testTYPEREF_NO_FEEL_TYPE_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("typeref/TYPEREF_NO_FEEL_TYPE.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_REF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NO_FEEL_TYPE_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("typeref/TYPEREF_NO_FEEL_TYPE.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NO_FEEL_TYPE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_REF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NO_NS_ReaderInput() throws IOException {
        try (final Reader reader = getReader("typeref/TYPEREF_NO_NS.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION)));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
        }
    }

    @Test
    public void testTYPEREF_NO_NS_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("typeref/TYPEREF_NO_NS.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION)));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NO_NS_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("typeref/TYPEREF_NO_NS.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NO_NS"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_ReaderInput() throws IOException {
        try (final Reader reader = getReader("typeref/TYPEREF_NOT_FEEL_NOT_DEF.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME)));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
        }
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("typeref/TYPEREF_NOT_FEEL_NOT_DEF.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME)));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("typeref/TYPEREF_NOT_FEEL_NOT_DEF.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NOT_FEEL_NOT_DEF"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME)));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND)));
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_valid_ResourceInput() throws IOException {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any DMNMessageTypeId.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        try (final Reader reader = getReader("typeref/TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
        }
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_valid_FileInput() {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any DMNMessageTypeId.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        final List<DMNMessage> validate = validator.validate(
                getFile("typeref/TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_valid_DefinitionsInput() {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any DMNMessageTypeId.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("typeref/TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn", "https://github.com/kiegroup/kie-dmn", "TYPEREF_NOT_FEEL_NOT_DEF_valid"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }
}
