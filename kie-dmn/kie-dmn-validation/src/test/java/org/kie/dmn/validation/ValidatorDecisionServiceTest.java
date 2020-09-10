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

package org.kie.dmn.validation;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.Definitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class ValidatorDecisionServiceTest extends AbstractValidatorTest {

    private static final Logger LOG = LoggerFactory.getLogger(ValidatorDecisionServiceTest.class);

    @Test
    public void testOUTPUT_NOT_FOUND_FOR_DS_ReaderInput() throws IOException {
        try (final Reader reader = getReader("decisionservice/HelloDS_noOutput.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
            assertThat(validate.get(0).toString(), validate.get(0).getMessageType(), is(DMNMessageType.REQ_NOT_FOUND));
        }
    }

    @Test
    public void testOUTPUT_NOT_FOUND_FOR_DS_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("decisionservice/HelloDS_noOutput.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertThat(validate.get(0).toString(), validate.get(0).getMessageType(), is(DMNMessageType.REQ_NOT_FOUND));
    }

    @Test
    public void testOUTPUT_NOT_FOUND_FOR_DS_DefinitionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("decisionservice/HelloDS_noOutput.dmn",
                               "https://kiegroup.org/dmn/_7C3C7416-2F33-4718-AE35-F3843C5250DB",
                               "HelloDS"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertThat(validate.get(0).toString(), validate.get(0).getMessageType(), is(DMNMessageType.REQ_NOT_FOUND));
    }

    @Test
    public void testOKDS() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("decisionservice/HelloDS_OK.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_7C3C7416-2F33-4718-AE35-F3843C5250DB",
                                             "HelloDS");
        assertThat(dmnModel, notNullValue());

        Definitions definitions = dmnModel.getDefinitions();
        assertThat(definitions, notNullValue());

        List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(definitions, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(messages.toString(), messages.size(), is(0));

        DMNResult evaluateAll = runtime.evaluateAll(dmnModel, runtime.newContext());
        LOG.debug("{}", evaluateAll);

        assertThat(evaluateAll.getDecisionResultByName("Decision-1").getResult(), is("Hello World"));
    }
}
