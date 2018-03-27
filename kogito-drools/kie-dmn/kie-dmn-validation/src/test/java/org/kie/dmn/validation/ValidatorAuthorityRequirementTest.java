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

public class ValidatorAuthorityRequirementTest extends AbstractValidatorTest {

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_AUTH_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_AUTH.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_AUTH_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_AUTH.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_AUTH_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_AUTH.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_MISSING_DEPENDENCY_REQ_AUTH"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_DEC_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_DEC.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_DEC_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_DEC.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_DEC_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_DEC.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_MISSING_DEPENDENCY_REQ_DEC"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_INPUT_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_INPUT.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_INPUT_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_INPUT.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_INPUT_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_INPUT.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_MISSING_DEPENDENCY_REQ_INPUT"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testAUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTHREQ_DEP_REQ_DEC_NOT_DECISION_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_DEP_REQ_DEC_NOT_DECISION.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testAUTHREQ_DEP_REQ_DEC_NOT_DECISION_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_DEP_REQ_DEC_NOT_DECISION.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTHREQ_DEP_REQ_DEC_NOT_DECISION_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_DEP_REQ_DEC_NOT_DECISION.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_DEP_REQ_DEC_NOT_DECISION"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTHREQ_DEP_REQ_INPUT_NOT_INPUT_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_DEP_REQ_INPUT_NOT_INPUT.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
        }
    }

    @Test
    public void testAUTHREQ_DEP_REQ_INPUT_NOT_INPUT_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_DEP_REQ_INPUT_NOT_INPUT.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTHREQ_DEP_REQ_INPUT_NOT_INPUT_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_DEP_REQ_INPUT_NOT_INPUT.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_DEP_REQ_INPUT_NOT_INPUT"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }
}
