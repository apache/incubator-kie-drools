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
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.ValidatorUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

class ValidatorBusinessContextTest extends AbstractValidatorTest {

    @Test
    void org_unit_decision_made_wrong_type_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businesscontext/ORG_UNIT_DECISION_MADE_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
             assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void org_unit_decision_made_wrong_type_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("businesscontext/ORG_UNIT_DECISION_MADE_WRONG_TYPE.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void org_unit_decision_made_wrong_type_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("businesscontext/ORG_UNIT_DECISION_MADE_WRONG_TYPE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "ORG_UNIT_DECISION_MADE_WRONG_TYPE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void org_unit_decision_owned_wrong_type_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businesscontext/ORG_UNIT_DECISION_OWNED_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
             assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void org_unit_decision_owned_wrong_type_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("businesscontext/ORG_UNIT_DECISION_OWNED_WRONG_TYPE.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void org_unit_decision_owned_wrong_type_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("businesscontext/ORG_UNIT_DECISION_OWNED_WRONG_TYPE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "ORG_UNIT_DECISION_OWNED_WRONG_TYPE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void perf_indicator_imp_decision_wrong_type_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businesscontext/PERF_INDICATOR_IMP_DECISION_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
             assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void perf_indicator_imp_decision_wrong_type_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("businesscontext/PERF_INDICATOR_IMP_DECISION_WRONG_TYPE.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void perf_indicator_imp_decision_wrong_type_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("businesscontext/PERF_INDICATOR_IMP_DECISION_WRONG_TYPE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "PERF_INDICATOR_IMP_DECISION_WRONG_TYPE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }
}
