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

class ValidatorAuthorityRequirementTest extends AbstractValidatorTest {

    @Test
    void auth_req_missing_dependency_req_auth_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_AUTH.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
             assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void auth_req_missing_dependency_req_auth_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_AUTH.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void auth_req_missing_dependency_req_auth_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_AUTH.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_MISSING_DEPENDENCY_REQ_AUTH"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void auth_req_missing_dependency_req_dec_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_DEC.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
             assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void auth_req_missing_dependency_req_dec_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_DEC.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void auth_req_missing_dependency_req_dec_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_DEC.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_MISSING_DEPENDENCY_REQ_DEC"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void auth_req_missing_dependency_req_input_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_INPUT.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
             assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void auth_req_missing_dependency_req_input_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_INPUT.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void auth_req_missing_dependency_req_input_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_INPUT.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_MISSING_DEPENDENCY_REQ_INPUT"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void authreq_dep_req_auth_not_knowledgesource_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
             assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void authreq_dep_req_auth_not_knowledgesource_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void authreq_dep_req_auth_not_knowledgesource_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void authreq_dep_req_dec_not_decision_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_DEP_REQ_DEC_NOT_DECISION.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
             assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void authreq_dep_req_dec_not_decision_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_DEP_REQ_DEC_NOT_DECISION.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void authreq_dep_req_dec_not_decision_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_DEP_REQ_DEC_NOT_DECISION.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_DEP_REQ_DEC_NOT_DECISION"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void authreq_dep_req_input_not_input_ReaderInput() throws IOException {
        try (final Reader reader = getReader("authorityrequirement/AUTHREQ_DEP_REQ_INPUT_NOT_INPUT.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
             assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void authreq_dep_req_input_not_input_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("authorityrequirement/AUTHREQ_DEP_REQ_INPUT_NOT_INPUT.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void authreq_dep_req_input_not_input_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("authorityrequirement/AUTHREQ_DEP_REQ_INPUT_NOT_INPUT.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "AUTHREQ_DEP_REQ_INPUT_NOT_INPUT"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
         assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }
}
