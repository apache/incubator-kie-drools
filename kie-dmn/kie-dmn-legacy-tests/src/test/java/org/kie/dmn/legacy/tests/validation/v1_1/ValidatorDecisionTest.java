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

class ValidatorDecisionTest extends AbstractValidatorTest {

    @Test
    void decision_missing_expr_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_MISSING_EXPR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.MISSING_EXPRESSION);
        }
    }

    @Test
    void decision_missing_expr_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("decision/DECISION_MISSING_EXPR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.MISSING_EXPRESSION);
    }

    @Test
    void decision_missing_expr_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_MISSING_EXPR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_MISSING_EXPR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.MISSING_EXPRESSION);
    }

    @Test
    void decision_missing_var_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_MISSING_VAR.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
        }
    }

    @Test
    void decision_missing_var_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("decision/DECISION_MISSING_VAR.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
    }

    @Test
    void decision_missing_var_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_MISSING_VAR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_MISSING_VAR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
    }

    @Test
    void decision_missing_vaRbisReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_MISSING_VARbis.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
        }
    }

    @Test
    void decision_missing_vaRbisFileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("decision/DECISION_MISSING_VARbis.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
    }

    @Test
    void decision_missing_vaRbisDefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_MISSING_VARbis.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_MISSING_VARbis"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_VARIABLE))).isTrue();
    }

    @Test
    void decision_mismatch_var_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_MISMATCH_VAR.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
        }
    }

    @Test
    void decision_mismatch_var_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("decision/DECISION_MISMATCH_VAR.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
    }

    @Test
    void decision_mismatch_var_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_MISMATCH_VAR.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_MISSING_VAR"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
    }

    @Test
    void decision_multiple_expressions_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_MULTIPLE_EXPRESSIONS.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION))).isTrue();
        }
    }

    @Test
    void decision_multiple_expressions_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("decision/DECISION_MULTIPLE_EXPRESSIONS.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION))).isTrue();
    }

    @Test
    void decision_multiple_expressions_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_MULTIPLE_EXPRESSIONS.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_MULTIPLE_EXPRESSIONS"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void decision_perf_indicator_wrong_type_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_PERF_INDICATOR_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void decision_perf_indicator_wrong_type_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("decision/DECISION_PERF_INDICATOR_WRONG_TYPE.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void decision_perf_indicator_wrong_type_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_PERF_INDICATOR_WRONG_TYPE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_PERF_INDICATOR_WRONG_TYPE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void decision_decision_maker_wrong_type_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_DECISION_MAKER_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void decision_decision_maker_wrong_type_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("decision/DECISION_DECISION_MAKER_WRONG_TYPE.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void decision_decision_maker_wrong_type_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_DECISION_MAKER_WRONG_TYPE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_DECISION_MAKER_WRONG_TYPE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void decision_decision_owner_wrong_type_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_DECISION_OWNER_WRONG_TYPE.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void decision_decision_owner_wrong_type_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("decision/DECISION_DECISION_OWNER_WRONG_TYPE.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void decision_decision_owner_wrong_type_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_DECISION_OWNER_WRONG_TYPE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_DECISION_MAKER_WRONG_TYPE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void decision_cyclic_dependency_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_CYCLIC_DEPENDENCY.dmn")) {
            final List<DMNMessage> validate = validator.validate( reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION );
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void decision_cyclic_dependency_FileInput() {
        final List<DMNMessage> validate = validator.validate( getFile("decision/DECISION_CYCLIC_DEPENDENCY.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION );
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void decision_cyclic_dependency_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_CYCLIC_DEPENDENCY.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_CYCLIC_DEPENDENCY"),
                VALIDATE_MODEL, VALIDATE_COMPILATION );
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void decision_cyclic_dependency_self_reference_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE.dmn")) {
            final List<DMNMessage> validate = validator.validate( reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION );
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void decision_cyclic_dependency_self_reference_FileInput() {
        final List<DMNMessage> validate = validator.validate( getFile("decision/DECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION );
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void decision_cyclic_dependency_self_reference_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_CYCLIC_DEPENDENCY_SELF_REFERENCE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION );
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @Test
    void decision_deadly_diamond_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_DEADLY_DIAMOND.dmn")) {
            final List<DMNMessage> validate = validator.validate( reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION );
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
        }
    }

    @Test
    void decision_deadly_diamond_FileInput() {
        final List<DMNMessage> validate = validator.validate( getFile("decision/DECISION_DEADLY_DIAMOND.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION );
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void decision_deadly_diamond_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_DEADLY_DIAMOND.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_DEADLY_DIAMOND"),
                VALIDATE_MODEL, VALIDATE_COMPILATION );
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void decision_deadly_kite_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decision/DECISION_DEADLY_KITE.dmn")) {
            final List<DMNMessage> validate = validator.validate( reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION );
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
        }
    }

    @Test
    void decision_deadly_kite_FileInput() {
        final List<DMNMessage> validate = validator.validate( getFile("decision/DECISION_DEADLY_KITE.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION );
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void decision_deadly_kite_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decision/DECISION_DEADLY_KITE.dmn",
                               "https://github.com/kiegroup/kie-dmn",
                               "DECISION_DEADLY_KITE"),
                VALIDATE_MODEL, VALIDATE_COMPILATION );
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }
}
