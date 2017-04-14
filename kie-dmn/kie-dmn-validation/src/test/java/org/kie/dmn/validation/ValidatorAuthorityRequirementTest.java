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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

import java.util.List;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;

public class ValidatorAuthorityRequirementTest extends AbstractValidatorTest {

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_AUTH() {
        final List<DMNMessage> validate = validator.validate(
                getReader("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_AUTH.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_DEC() {
        final List<DMNMessage> validate = validator.validate(
                getReader("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_DEC.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTH_REQ_MISSING_DEPENDENCY_REQ_INPUT() {
        final List<DMNMessage> validate = validator.validate(
                getReader("authorityrequirement/AUTHREQ_MISSING_DEPENDENCY_REQ_INPUT.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE() {
        final List<DMNMessage> validate = validator.validate(
                getReader("authorityrequirement/AUTHREQ_DEP_REQ_AUTH_NOT_KNOWLEDGESOURCE.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTHREQ_DEP_REQ_DEC_NOT_DECISION() {
        final List<DMNMessage> validate = validator.validate(
                getReader("authorityrequirement/AUTHREQ_DEP_REQ_DEC_NOT_DECISION.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }

    @Test
    public void testAUTHREQ_DEP_REQ_INPUT_NOT_INPUT() {
        final List<DMNMessage> validate = validator.validate(
                getReader("authorityrequirement/AUTHREQ_DEP_REQ_INPUT_NOT_INPUT.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(3));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)));
    }
}
