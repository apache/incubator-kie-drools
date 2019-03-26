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

public class ValidatorDecisionTableTest
        extends AbstractValidatorTest {

    @Test
    public void testDTABLE_EMPTY_ENTRY_ReaderInput() throws IOException {
        try (final Reader reader = getReader("DTABLE_EMPTY_ENTRY.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
            assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
        }
    }

    @Test
    public void testDTABLE_EMPTY_ENTRY_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("DTABLE_EMPTY_ENTRY.dmn"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testDTABLE_EMPTY_ENTRY_DefintionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("DTABLE_EMPTY_ENTRY.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DTABLE_PRIORITY_MISSING_OUTVALS"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testDTABLE_MULTIPLEOUT_NAME_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT.dmn" )) {
            List<DMNMessage> validate = validator.validate( reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 5 ) );
            assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_NAME ) ) );
            assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_TYPE_REF ) ) );
            assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
        }
    }

    @Test
    public void testDTABLE_MULTIPLEOUT_NAME_FileInput() {
        List<DMNMessage> validate = validator.validate( getFile( "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 5 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_NAME ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_TYPE_REF ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
    }

    @Test
    public void testDTABLE_MULTIPLEOUT_NAME_DefinitionsInput() {
        List<DMNMessage> validate = validator.validate(
                getDefinitions( "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT.dmn",
                                "https://github.com/kiegroup/kie-dmn",
                                "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 5 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_NAME ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_TYPE_REF ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
    }

    @Test
    public void testDTABLE_PRIORITY_MISSING_OUTVALS_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "DTABLE_PRIORITY_MISSING_OUTVALS.dmn" )) {
            List<DMNMessage> validate = validator.validate( reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat( ValidatorUtil.formatMessages( validate ), validate.isEmpty(), is( false ) );
            assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_OUTPUT_VALUES ) ) );
        }
    }

    @Test
    public void testDTABLE_PRIORITY_MISSING_OUTVALS_FileInput() {
        List<DMNMessage> validate = validator.validate( getFile( "DTABLE_PRIORITY_MISSING_OUTVALS.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.isEmpty(), is( false ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_OUTPUT_VALUES ) ) );
    }

    @Test
    public void testDTABLE_PRIORITY_MISSING_OUTVALS_DefinitionsInput() {
        List<DMNMessage> validate = validator.validate(
                getDefinitions( "DTABLE_PRIORITY_MISSING_OUTVALS.dmn",
                                "https://github.com/kiegroup/kie-dmn",
                                "DTABLE_PRIORITY_MISSING_OUTVALS"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.isEmpty(), is( false ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_OUTPUT_VALUES ) ) );
    }

    @Test
    public void testDTABLE_SINGLEOUT_NONAME_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT.dmn" )) {
            List<DMNMessage> validate = validator.validate( reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 2 ) );
            assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ILLEGAL_USE_OF_NAME ) ) );
            assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ILLEGAL_USE_OF_TYPEREF ) ) );
        }
    }

    @Test
    public void testDTABLE_SINGLEOUT_NONAME_FileInput() {
        List<DMNMessage> validate = validator.validate( getFile( "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ILLEGAL_USE_OF_NAME ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ILLEGAL_USE_OF_TYPEREF ) ) );
    }

    @Test
    public void testDTABLE_SINGLEOUT_NONAME_DefinitionsInput() {
        List<DMNMessage> validate = validator.validate(
                getDefinitions("DTABLE_SINGLEOUTPUT_WRONG_OUTPUT.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ILLEGAL_USE_OF_NAME ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ILLEGAL_USE_OF_TYPEREF ) ) );
    }
}
