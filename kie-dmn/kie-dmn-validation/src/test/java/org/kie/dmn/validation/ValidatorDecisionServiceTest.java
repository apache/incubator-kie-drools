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
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.Definitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

class ValidatorDecisionServiceTest extends AbstractValidatorTest {

    private static final Logger LOG = LoggerFactory.getLogger(ValidatorDecisionServiceTest.class);

    @Test
    void output_not_found_for_ds_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decisionservice/HelloDS_noOutput.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
            assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        }
    }

    @Test
    void output_not_found_for_ds_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("decisionservice/HelloDS_noOutput.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
    }

    @Test
    void output_not_found_for_ds_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decisionservice/HelloDS_noOutput.dmn",
                               "https://kiegroup.org/dmn/_7C3C7416-2F33-4718-AE35-F3843C5250DB",
                               "HelloDS"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
    }

    @Test
    void okds() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("decisionservice/HelloDS_OK.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_7C3C7416-2F33-4718-AE35-F3843C5250DB",
                                             "HelloDS");
        assertThat(dmnModel).isNotNull();

        Definitions definitions = dmnModel.getDefinitions();
        assertThat(definitions).isNotNull();

        List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(definitions, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(messages).as(messages.toString()).hasSize(0);

        DMNResult evaluateAll = runtime.evaluateAll(dmnModel, runtime.newContext());
        LOG.debug("{}", evaluateAll);

        assertThat(evaluateAll.getDecisionResultByName("Decision-1").getResult()).isEqualTo("Hello World");
    }

    @Test
    void dS1ofEach() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("decisionservice/DS1ofEach_OK.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_40B3D02F-868C-4925-A1F2-5710DFEEF51E",
                                             "DS1ofEach");
        assertThat(dmnModel).isNotNull();

        Definitions definitions = dmnModel.getDefinitions();
        assertThat(definitions).isNotNull();

        List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(definitions, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(messages).as(messages.toString()).hasSize(0);

        DMNContext dmnContext = runtime.newContext();
        dmnContext.set("InputData-1", "id1");
        dmnContext.set("Decision-1", "od1");
        DMNResult evaluateDS1 = runtime.evaluateDecisionService(dmnModel, dmnContext, "DecisionService-1");
        LOG.debug("{}", evaluateDS1);

        assertThat(evaluateDS1.getDecisionResultByName("Decision-2")).isNull();
        assertThat(evaluateDS1.getDecisionResultByName("Decision-3").getResult()).isEqualTo("d3:d2:id1od1");
    }

    @Test
    void encapsulated_not_found_for_ds_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decisionservice/DS1ofEach_missingEncapsulated.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader,
                                                                 VALIDATE_SCHEMA, VALIDATE_MODEL);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2); // DS-1 and Decision-3 are missing their reference now.
            assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
            assertThat(validate.get(1).getMessageType()).as(validate.get(1).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        }
    }

    @Test
    void encapsulated_not_found_for_ds_FileInput() {
        final List<DMNMessage> validate = validator.validate(getFile("decisionservice/DS1ofEach_missingEncapsulated.dmn"),
                                                             VALIDATE_SCHEMA, VALIDATE_MODEL);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        assertThat(validate.get(1).getMessageType()).as(validate.get(1).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
    }

    @Test
    void encapsulated_not_found_for_ds_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(getDefinitions("decisionservice/DS1ofEach_missingEncapsulated.dmn",
                                                                            "https://kiegroup.org/dmn/_40B3D02F-868C-4925-A1F2-5710DFEEF51E",
                                                                            "DS1ofEach"),
                                                             VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSizeGreaterThanOrEqualTo(2);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        assertThat(validate.get(1).getMessageType()).as(validate.get(1).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
    }

    @Test
    void decisioninput_not_found_for_ds_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decisionservice/DS1ofEach_missingDecisionInput.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader,
                                                                 VALIDATE_SCHEMA, VALIDATE_MODEL);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2); // DS-1 and Decision-2 are missing their reference now.
            assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
            assertThat(validate.get(1).getMessageType()).as(validate.get(1).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        }
    }

    @Test
    void decisioninput_not_found_for_ds_FileInput() {
        final List<DMNMessage> validate = validator.validate(getFile("decisionservice/DS1ofEach_missingDecisionInput.dmn"),
                                                             VALIDATE_SCHEMA, VALIDATE_MODEL);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        assertThat(validate.get(1).getMessageType()).as(validate.get(1).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
    }

    @Test
    void decisioninput_not_found_for_ds_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(getDefinitions("decisionservice/DS1ofEach_missingDecisionInput.dmn",
                                                                            "https://kiegroup.org/dmn/_40B3D02F-868C-4925-A1F2-5710DFEEF51E",
                                                                            "DS1ofEach"),
                                                             VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSizeGreaterThanOrEqualTo(2);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        assertThat(validate.get(1).getMessageType()).as(validate.get(1).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
    }

    @Test
    void inputdata_not_found_for_ds_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decisionservice/DS1ofEach_missingInputData.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader,
                                                                 VALIDATE_SCHEMA, VALIDATE_MODEL);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2); // DS-1 and Decision-2 are missing their reference now.
            assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
            assertThat(validate.get(1).getMessageType()).as(validate.get(1).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        }
    }

    @Test
    void inputdata_not_found_for_ds_FileInput() {
        final List<DMNMessage> validate = validator.validate(getFile("decisionservice/DS1ofEach_missingInputData.dmn"),
                                                             VALIDATE_SCHEMA, VALIDATE_MODEL);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        assertThat(validate.get(1).getMessageType()).as(validate.get(1).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
    }

    @Test
    void inputdata_not_found_for_ds_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(getDefinitions("decisionservice/DS1ofEach_missingInputData.dmn",
                                                                            "https://kiegroup.org/dmn/_40B3D02F-868C-4925-A1F2-5710DFEEF51E",
                                                                            "DS1ofEach"),
                                                             VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSizeGreaterThanOrEqualTo(2);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        assertThat(validate.get(1).getMessageType()).as(validate.get(1).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
    }

    @Test
    void outputelement_not_found_for_ds_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decisionservice/DS1ofEach_missingOutput.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader,
                                                                 VALIDATE_SCHEMA, VALIDATE_MODEL);
            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1); // DS-1 missing its reference now.
            assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
        }
    }

    @Test
    void outputelement_not_found_for_ds_FileInput() {
        final List<DMNMessage> validate = validator.validate(getFile("decisionservice/DS1ofEach_missingOutput.dmn"),
                                                             VALIDATE_SCHEMA, VALIDATE_MODEL);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
    }

    @Test
    void outputelement_not_found_for_ds_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(getDefinitions("decisionservice/DS1ofEach_missingOutput.dmn",
                                                                            "https://kiegroup.org/dmn/_40B3D02F-868C-4925-A1F2-5710DFEEF51E",
                                                                            "DS1ofEach"),
                                                             VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSizeGreaterThanOrEqualTo(2);
        assertThat(validate.get(0).getMessageType()).as(validate.get(0).toString()).isEqualTo(DMNMessageType.REQ_NOT_FOUND);
    }
}
