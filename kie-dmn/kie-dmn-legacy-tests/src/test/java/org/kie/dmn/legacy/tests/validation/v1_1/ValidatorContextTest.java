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
import org.kie.dmn.model.api.Context;
import org.kie.dmn.model.api.ContextEntry;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.ValidatorUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

class ValidatorContextTest extends AbstractValidatorTest {

    @Test
    void context_missing_expr_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_MISSING_EXPR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION))).isTrue(); // this is schema validation
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
        }
    }

    @Test
    void context_missing_expr_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("context/CONTEXT_MISSING_EXPR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION))).isTrue(); // this is schema validation
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
    }

    @Test
    void context_missing_expr_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("context/CONTEXT_MISSING_EXPR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "CONTEXT_MISSING_EXPR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
    }

    @Test
    void context_missing_entries_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_MISSING_ENTRIES.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
        }
    }

    @Test
    void context_missing_entries_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("context/CONTEXT_MISSING_ENTRIES.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
    }

    @Test
    void context_missing_entries_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("context/CONTEXT_MISSING_ENTRIES.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "CONTEXT_MISSING_EXPR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
    }

    @Test
    void context_entry_missing_variable_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_ENTRY_MISSING_VARIABLE.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
            // check that it reports and error for the second context entry, but not for the last one
            final ContextEntry ce = (ContextEntry) validate.get(0).getSourceReference();
            assertThat(((Context) ce.getParent()).getContextEntry().indexOf(ce)).isEqualTo(1);
        }
    }

    @Test
    void context_entry_missing_variable_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("context/CONTEXT_ENTRY_MISSING_VARIABLE.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
        // check that it reports and error for the second context entry, but not for the last one
        final ContextEntry ce = (ContextEntry) validate.get(0).getSourceReference();
        assertThat(((Context) ce.getParent()).getContextEntry().indexOf(ce)).isEqualTo(1);
    }

    @Test
    void context_entry_missing_variable_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("context/CONTEXT_ENTRY_MISSING_VARIABLE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "CONTEXT_MISSING_EXPR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
        // check that it reports and error for the second context entry, but not for the last one
        final ContextEntry ce = (ContextEntry) validate.get(0).getSourceReference();
        assertThat(((Context) ce.getParent()).getContextEntry().indexOf(ce)).isEqualTo(1);
    }

    @Test
    void context_dup_entry_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_DUP_ENTRY.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME))).isTrue();
        }
    }

    @Test
    void context_dup_entry_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("context/CONTEXT_DUP_ENTRY.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME))).isTrue();
    }

    @Test
    void context_dup_entry_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("context/CONTEXT_DUP_ENTRY.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "CONTEXT_DUP_ENTRY"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME))).isTrue();
    }

    @Test
    void context_entry_notyperef_ReaderInput() throws IOException {
        try (final Reader reader = getReader("context/CONTEXT_ENTRY_NOTYPEREF.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF))).isTrue();
        }
    }

    @Test
    void context_entry_notyperef_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("context/CONTEXT_ENTRY_NOTYPEREF.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF))).isTrue();
    }

    @Test
    void context_entry_notyperef_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("context/CONTEXT_ENTRY_NOTYPEREF.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "CONTEXT_ENTRY_NOTYPEREF"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF))).isTrue();
    }
}
