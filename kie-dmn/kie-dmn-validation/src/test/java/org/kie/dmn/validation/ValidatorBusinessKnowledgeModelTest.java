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

class ValidatorBusinessKnowledgeModelTest extends AbstractValidatorTest {

    @Test
    void bkm_missing_var_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businessknowledgemodel/BKM_MISSING_VAR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
        }
    }

    @Test
    void bkm_missing_var_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("businessknowledgemodel/BKM_MISSING_VAR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
    }

    @Test
    void bkm_missing_var_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("businessknowledgemodel/BKM_MISSING_VAR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "BKM_MISSING_VAR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
    }

    @Test
    void bkm_mismatch_var_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businessknowledgemodel/BKM_MISMATCH_VAR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
        }
    }

    @Test
    void bkm_mismatch_var_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("businessknowledgemodel/BKM_MISMATCH_VAR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
    }

    @Test
    void bkm_mismatch_var_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("businessknowledgemodel/BKM_MISMATCH_VAR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "BKM_MISSING_VAR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
    }

    @Test
    void bkm_missing_expr_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businessknowledgemodel/BKM_MISSING_EXPR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.get(0).getMessageType()).withFailMessage(validate.get(0).toString()).isEqualTo(DMNMessageType.MISSING_EXPRESSION);
        }
    }

    @Test
    void bkm_missing_expr_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("businessknowledgemodel/BKM_MISSING_EXPR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.get(0).getMessageType()).withFailMessage(validate.get(0).toString()).isEqualTo(DMNMessageType.MISSING_EXPRESSION);
    }

    @Test
    void bkm_missing_expr_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("businessknowledgemodel/BKM_MISSING_EXPR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "BKM_MISSING_EXPR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.get(0).getMessageType()).withFailMessage(validate.get(0).toString()).isEqualTo(DMNMessageType.MISSING_EXPRESSION);
    }
}
