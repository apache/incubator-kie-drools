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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.decisionservices.DMNDecisionServicesTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class ValidatorImportTest extends AbstractValidatorTest {

    public static final Logger LOG = LoggerFactory.getLogger(ValidatorImportTest.class);

    @Disabled
    @Test
    void baseModelOK() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("import/Base-model.dmn", this.getClass(), "import/Import-base-model.dmn");
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b33fa7d9-f501-423b-afa8-15ded7e7f493", "Import base model");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = DMNFactory.newContext();
        context.set("Customer", mapOf(entry("full name", "John Doe"), entry("age", 47)));
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
    }

    @Test
    void baseModelOKFromReaderInput() throws IOException {
        try (final Reader reader0 = getReader("import/Base-model.dmn");
                final Reader reader1 = getReader("import/Import-base-model.dmn");) {
            final List<DMNMessage> messages = validator.validateUsing(// VALIDATE_SCHEMA, disabled, due to QName use not compliant. 
                                                                      Validation.VALIDATE_MODEL,
                                                                      Validation.VALIDATE_COMPILATION)
                                                       .theseModels(reader0, reader1);
            assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(0);
        }
    }

    @Test
    void baseModelOKFromFileInput() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(// VALIDATE_SCHEMA, disabled, due to QName use not compliant. 
                                                                  Validation.VALIDATE_MODEL,
                                                                  Validation.VALIDATE_COMPILATION)
                                                   .theseModels(getFile("import/Import-base-model.dmn"), // switch order for DROOLS-2936 
                                                                getFile("import/Base-model.dmn"));
        assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(0);
    }

    @Test
    void baseModelImportModelNameFromFileInput() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL)
                                                   .theseModels(getFile("import/Import-base-model-modelnameattribute.dmn"), // DROOLS-2938
                                                                getFile("import/Base-model.dmn"));
        assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(0);
    }

    @Test
    void baseModelOKFromDefinitionsInput() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(// VALIDATE_SCHEMA, disabled, due to QName use not compliant. 
                                                                  Validation.VALIDATE_MODEL,
                                                                  Validation.VALIDATE_COMPILATION)
                                                   .theseModels(getDefinitions(Arrays.asList("import/Base-model.dmn", "import/Import-base-model.dmn"),
                                                                               "http://www.trisotech.com/definitions/_70df1ad5-2a33-4ede-b8b2-869988ac1d30",
                                                                               "Base model"),
                                                                getDefinitions(Arrays.asList("import/Base-model.dmn", "import/Import-base-model.dmn"),
                                                                               "http://www.trisotech.com/dmn/definitions/_b33fa7d9-f501-423b-afa8-15ded7e7f493",
                                                                               "Import base model"));
        assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(0);
    }

    @Test
    void wrongImportBaseModelFromReaderInput() throws IOException {
        try (final Reader reader0 = getReader("import/Base-model.dmn");
                final Reader reader1 = getReader("import/Wrong-Import-base-model.dmn");) {
            final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL)
                                                       .theseModels(reader0, reader1);
            assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(1);
            assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND) &&
                    p.getSourceReference() instanceof DMNElementReference &&
                    ((DMNElementReference) p.getSourceReference()).getHref()
                            .equals("http://www.trisotech.com/definitions/_70df1ad5-2a33-4ede-b8b2-869988ac1d30#_1d52934e-aa4e-47c9-a011-fc989d795664"))).isTrue();
        }
    }

    @Test
    void wrongImportBaseModelFromFileInput() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL)
                                                   .theseModels(getFile("import/Base-model.dmn"),
                                                                getFile("import/Wrong-Import-base-model.dmn"));
        assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(1);
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND) &&
                p.getSourceReference() instanceof DMNElementReference &&
                ((DMNElementReference) p.getSourceReference()).getHref()
                        .equals("http://www.trisotech.com/definitions/_70df1ad5-2a33-4ede-b8b2-869988ac1d30#_1d52934e-aa4e-47c9-a011-fc989d795664"))).isTrue();
    }

    @Test
    void wrongImportBaseModelFromDefinitionsInput() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL)
                                                   .theseModels(getDefinitions(Arrays.asList("import/Base-model.dmn", "import/Wrong-Import-base-model.dmn"),
                                                                               "http://www.trisotech.com/definitions/_70df1ad5-2a33-4ede-b8b2-869988ac1d30",
                                                                               "Base model"),
                                                                getDefinitions(Arrays.asList("import/Base-model.dmn", "import/Wrong-Import-base-model.dmn"),
                                                                               "http://www.trisotech.com/dmn/definitions/_719a2325-5cac-47ea-8a99-665c01d570a5",
                                                                               "Wrong Import base model"));
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND) &&
                p.getSourceReference() instanceof DMNElementReference &&
                ((DMNElementReference) p.getSourceReference()).getHref()
                        .equals("http://www.trisotech.com/definitions/_70df1ad5-2a33-4ede-b8b2-869988ac1d30#_1d52934e-aa4e-47c9-a011-fc989d795664"))).isTrue();
    }

    @Test
    void onlyImportBaseModelFromReaderInput() throws IOException {
        try (final Reader reader1 = getReader("import/Only-Import-base-model.dmn");) {
            final List<DMNMessage> messages = validator.validate(reader1, Validation.VALIDATE_MODEL);
            assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.IMPORT_NOT_FOUND))).isTrue();
        }
    }

    @Test
    void onlyImportBaseModelFromFileInput() throws IOException {
        final List<DMNMessage> messages = validator.validate(getFile("import/Only-Import-base-model.dmn"), Validation.VALIDATE_MODEL);
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.IMPORT_NOT_FOUND))).isTrue();
    }

    @Test
    void onlyImportBaseModelFromDefinitionsInput() throws IOException {
        final List<DMNMessage> messages = validator.validate(getDefinitions("import/Only-Import-base-model.dmn",
                                                                            "http://www.trisotech.com/dmn/definitions/_a9bfa4de-cf5c-4b2f-9011-ab576e00b162",
                                                                            "Only Import base model"),
                                                             Validation.VALIDATE_MODEL);
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.IMPORT_NOT_FOUND))).isTrue();
    }

    @Test
    void importNoAddtnAttribute() throws IOException {
        // DROOLS-4187 kie-dmn-validation: Incorrect import detection
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL,
                                                                  Validation.VALIDATE_COMPILATION)
                                                   .theseModels(getFile("import/DROOLS-4187a.dmn"),
                                                                getFile("import/DROOLS-4187b.dmn"));
        assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(0);

        DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("import/DROOLS-4187a.dmn", this.getClass(), "import/DROOLS-4187b.dmn");
        DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_D0CFBAE7-4EBD-4FA5-A15F-DA00581ADA0B", "b");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = DMNFactory.newContext();
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("aaa").getResult()).isEqualTo(new BigDecimal(2));
    }

    @Test
    void importNameOK() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION)
                                                   .theseModels(getReader("myHelloDS.dmn", DMNDecisionServicesTest.class),
                                                                getReader("importingMyHelloDSbkmBoxedInvocation.dmn", DMNDecisionServicesTest.class));
        assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(0);
    }

    @Test
    void importNameNotUniqueWithDRG() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION)
                                                   .theseModels(getReader("myHelloDS.dmn", DMNDecisionServicesTest.class),
                                                                getReader("import/importingMyHelloDSbkmBoxedInvocation_wrongBKMname.dmn"));
        assertThat(messages.stream().anyMatch(p -> p.getText().contains("myHelloDS") && p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME))).isTrue();
    }

    @Test
    void importNameNotUniqueWithItemDef() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION)
                                                   .theseModels(getReader("myHelloDS.dmn", DMNDecisionServicesTest.class),
                                                                getReader("import/importingMyHelloDSbkmBoxedInvocation_wrongItemDefName.dmn"));
        assertThat(messages.stream().anyMatch(p -> p.getText().contains("myHelloDS") && p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME))).isTrue();
    }

    @Test
    void importNameOKWithItemDefComponent() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION)
                                                   .theseModels(getReader("myHelloDS.dmn", DMNDecisionServicesTest.class),
                                                                getReader("import/importingMyHelloDSbkmBoxedInvocation_okItemDefName.dmn"));
        assertThat(messages).as(ValidatorUtil.formatMessages(messages)).hasSize(0);
    }

    @Test
    void importNameNotUniqueWithOtherImport() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION)
                                                   .theseModels(getReader("myHelloDS.dmn", DMNDecisionServicesTest.class),
                                                                getReader("import/importingMyHelloDSbkmBoxedInvocation_wrongDoubleImportName.dmn"));
        assertThat(messages.stream().anyMatch(p -> p.getText().contains("myHelloDS") && p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME))).isTrue();
    }
}
