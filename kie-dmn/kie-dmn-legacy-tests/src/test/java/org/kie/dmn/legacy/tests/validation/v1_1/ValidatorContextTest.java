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

package org.kie.dmn.legacy.tests.validation.v1_1;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.model.api.Context;
import org.kie.dmn.model.api.ContextEntry;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.ValidatorUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class ValidatorContextTest extends AbstractValidatorTest {

    @Test
    public void testCONTEXT_MISSING_EXPR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_MISSING_EXPR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION))); // this is schema validation
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
        }
    }

    @Test
    public void testCONTEXT_MISSING_EXPR_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("context/CONTEXT_MISSING_EXPR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION))); // this is schema validation
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testCONTEXT_MISSING_EXPR_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("context/CONTEXT_MISSING_EXPR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "CONTEXT_MISSING_EXPR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testCONTEXT_MISSING_ENTRIES_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_MISSING_ENTRIES.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
        }
    }

    @Test
    public void testCONTEXT_MISSING_ENTRIES_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("context/CONTEXT_MISSING_ENTRIES.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testCONTEXT_MISSING_ENTRIES_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("context/CONTEXT_MISSING_ENTRIES.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "CONTEXT_MISSING_EXPR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testCONTEXT_ENTRY_MISSING_VARIABLE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_ENTRY_MISSING_VARIABLE.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
            // check that it reports and error for the second context entry, but not for the last one
            final ContextEntry ce = (ContextEntry) validate.get(0).getSourceReference();
            assertThat(((Context) ce.getParent()).getContextEntry().indexOf(ce), is(1));
        }
    }

    @Test
    public void testCONTEXT_ENTRY_MISSING_VARIABLE_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("context/CONTEXT_ENTRY_MISSING_VARIABLE.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
        // check that it reports and error for the second context entry, but not for the last one
        final ContextEntry ce = (ContextEntry) validate.get(0).getSourceReference();
        assertThat(((Context) ce.getParent()).getContextEntry().indexOf(ce), is(1));
    }

    @Test
    public void testCONTEXT_ENTRY_MISSING_VARIABLE_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("context/CONTEXT_ENTRY_MISSING_VARIABLE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "CONTEXT_MISSING_EXPR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
        // check that it reports and error for the second context entry, but not for the last one
        final ContextEntry ce = (ContextEntry) validate.get(0).getSourceReference();
        assertThat(((Context) ce.getParent()).getContextEntry().indexOf(ce), is(1));
    }

    @Test
    public void testCONTEXT_DUP_ENTRY_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_DUP_ENTRY.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME)));
        }
    }

    @Test
    public void testCONTEXT_DUP_ENTRY_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("context/CONTEXT_DUP_ENTRY.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME)));
    }

    @Test
    public void testCONTEXT_DUP_ENTRY_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("context/CONTEXT_DUP_ENTRY.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "CONTEXT_DUP_ENTRY"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME)));
    }

    @Test
    public void testCONTEXT_ENTRY_NOTYPEREF_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_ENTRY_NOTYPEREF.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF)));
        }
    }

    @Test
    public void testCONTEXT_ENTRY_NOTYPEREF_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("context/CONTEXT_ENTRY_NOTYPEREF.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF)));
    }

    @Test
    public void testCONTEXT_ENTRY_NOTYPEREF_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("context/CONTEXT_ENTRY_NOTYPEREF.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "CONTEXT_ENTRY_NOTYPEREF"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF)));
    }
}
