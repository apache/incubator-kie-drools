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

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class ValidatorBusinessKnowledgeModelTest extends AbstractValidatorTest {

    @Test
    public void testBKM_MISSING_VAR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businessknowledgemodel/BKM_MISSING_VAR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
        }
    }

    @Test
    public void testBKM_MISSING_VAR_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("businessknowledgemodel/BKM_MISSING_VAR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
    }

    @Test
    public void testBKM_MISSING_VAR_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("businessknowledgemodel/BKM_MISSING_VAR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "BKM_MISSING_VAR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
    }

    @Test
    public void testBKM_MISMATCH_VAR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businessknowledgemodel/BKM_MISMATCH_VAR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
        }
    }

    @Test
    public void testBKM_MISMATCH_VAR_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("businessknowledgemodel/BKM_MISMATCH_VAR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
    }

    @Test
    public void testBKM_MISMATCH_VAR_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("businessknowledgemodel/BKM_MISMATCH_VAR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "BKM_MISSING_VAR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
    }

    @Test
    public void testBKM_MISSING_EXPR_ReaderInput() throws IOException {
        try (final Reader reader = getReader("businessknowledgemodel/BKM_MISSING_EXPR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.MISSING_EXPRESSION);
        }
    }

    @Test
    public void testBKM_MISSING_EXPR_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("businessknowledgemodel/BKM_MISSING_EXPR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.MISSING_EXPRESSION);
    }

    @Test
    public void testBKM_MISSING_EXPR_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("businessknowledgemodel/BKM_MISSING_EXPR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "BKM_MISSING_EXPR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.MISSING_EXPRESSION);
    }
}
