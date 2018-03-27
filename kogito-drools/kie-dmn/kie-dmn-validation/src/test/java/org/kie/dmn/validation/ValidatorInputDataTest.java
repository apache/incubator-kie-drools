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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;

public class ValidatorInputDataTest extends AbstractValidatorTest {

    @Test
    public void testINPUT_MISSING_VAR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("inputdata/INPUTDATA_MISSING_VAR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
        }
    }

    @Test
    public void testINPUT_MISSING_VAR_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("inputdata/INPUTDATA_MISSING_VAR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
    }

    @Test
    public void testINPUT_MISSING_VAR_DefintionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("inputdata/INPUTDATA_MISSING_VAR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "INPUTDATA_MISSING_VAR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE)));
    }

    @Test
    public void testINPUT_MISMATCH_VAR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("inputdata/INPUTDATA_MISMATCH_VAR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH)));
        }
    }

    @Test
    public void testINPUT_MISMATCH_VAR_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("inputdata/INPUTDATA_MISMATCH_VAR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH)));
    }

    @Test
    public void testINPUT_MISMATCH_VAR_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("inputdata/INPUTDATA_MISMATCH_VAR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "INPUTDATA_MISSING_VAR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH)));
    }
}
