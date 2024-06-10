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

class ValidatorKnowledgeSourceTest extends AbstractValidatorTest {

    @Test
    void know_source_missing_owner_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "knowledgesource/KNOW_SOURCE_MISSING_OWNER.dmn" )) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void know_source_missing_owner_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile( "knowledgesource/KNOW_SOURCE_MISSING_OWNER.dmn" ),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void know_source_missing_owner_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions( "knowledgesource/KNOW_SOURCE_MISSING_OWNER.dmn",
                                "https://github.com/kiegroup/kie-dmn",
                                "KNOW_SOURCE_MISSING_OWNER"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void know_source_owner_not_org_unit_ReaderInput() throws IOException {
        try (final Reader reader = getReader( "knowledgesource/KNOW_SOURCE_OWNER_NOT_ORG_UNIT.dmn" )) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void know_source_owner_not_org_unit_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile( "knowledgesource/KNOW_SOURCE_OWNER_NOT_ORG_UNIT.dmn" ),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void know_source_owner_not_org_unit_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions( "knowledgesource/KNOW_SOURCE_OWNER_NOT_ORG_UNIT.dmn",
                                "https://github.com/kiegroup/kie-dmn",
                                "KNOW_SOURCE_OWNER_NOT_ORG_UNIT"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }
}
