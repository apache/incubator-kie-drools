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

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.model.v1_1.Context;
import org.kie.dmn.model.v1_1.ContextEntry;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.*;

public class ValidatorDecisionTableTest
        extends AbstractValidatorTest {

    @Test
    public void testDTABLE_EMPTY_ENTRY() {
        final List<DMNMessage> validate = validator.validate(
                getReader("DTABLE_EMPTY_ENTRY.dmn"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testDTABLE_MULTIPLEOUT_NAME() {
        List<DMNMessage> validate = validator.validate( getReader( "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 5 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_NAME ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_TYPE_REF ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
    }

    @Test
    public void testDTABLE_PRIORITY_MISSING_OUTVALS() {
        List<DMNMessage> validate = validator.validate( getReader( "DTABLE_PRIORITY_MISSING_OUTVALS.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_OUTPUT_VALUES ) ) );
    }

    @Test
    public void testDTABLE_SINGLEOUT_NONAME() {
        List<DMNMessage> validate = validator.validate( getReader( "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ILLEGAL_USE_OF_NAME ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ILLEGAL_USE_OF_TYPEREF ) ) );
    }
}
