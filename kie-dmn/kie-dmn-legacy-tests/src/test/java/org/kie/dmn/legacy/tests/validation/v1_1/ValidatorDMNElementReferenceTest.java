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
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.ValidatorUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class ValidatorDMNElementReferenceTest extends AbstractValidatorTest {

    @Test
    public void testELEMREF_NOHASH_ReaderInput() throws IOException {
        try (final Reader reader = getReader("dmnelementref/ELEMREF_NOHASH.dmn")) {
            final List<DMNMessage> validationMessages = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertValiadationResult(validationMessages);
        }
    }

    @Test
    public void testELEMREF_NOHASH_FileInput() {
        final List<DMNMessage> validationMessages = validator.validate(
                getFile("dmnelementref/ELEMREF_NOHASH.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertValiadationResult(validationMessages);
    }

    @Test
    public void testELEMREF_NOHASH_DefinitionsInput() {
        final List<DMNMessage> validationMessages = validator.validate(
                getDefinitions("dmnelementref/ELEMREF_NOHASH.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "ELEMREF_NOHASH"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertValiadationResult(validationMessages);
    }

    private void assertValiadationResult(List<DMNMessage> validationMessages) {
        assertThat(ValidatorUtil.formatMessages(validationMessages), validationMessages.size(), is(3));
        assertTrue(validationMessages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
        assertTrue(validationMessages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_HREF_SYNTAX)));
        assertTrue(validationMessages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

}
