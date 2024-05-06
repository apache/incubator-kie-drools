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

class ValidatorDMNElementReferenceTest extends AbstractValidatorTest {

    @Test
    void elemref_nohash_ReaderInput() throws IOException {
        try (final Reader reader = getReader("dmnelementref/ELEMREF_NOHASH.dmn")) {
            final List<DMNMessage> validationMessages = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertValiadationResult(validationMessages);
        }
    }

    @Test
    void elemref_nohash_FileInput() {
        final List<DMNMessage> validationMessages = validator.validate(
                getFile("dmnelementref/ELEMREF_NOHASH.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertValiadationResult(validationMessages);
    }

    @Test
    void elemref_nohash_DefinitionsInput() {
        final List<DMNMessage> validationMessages = validator.validate(
                getDefinitions("dmnelementref/ELEMREF_NOHASH.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "ELEMREF_NOHASH"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertValiadationResult(validationMessages);
    }

    private void assertValiadationResult(List<DMNMessage> validationMessages) {
    	assertThat(validationMessages).as(ValidatorUtil.formatMessages(validationMessages)).hasSize(4);
        assertThat(validationMessages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
        assertThat(validationMessages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_HREF_SYNTAX))).isTrue();
        assertThat(validationMessages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

}
