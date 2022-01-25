/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.DMNInputRuntimeTest;

public class UsingResourceValidatorTest extends AbstractValidatorTest {

    @Test
    public void testInvalidXml_single() {
        List<DMNMessage> validateXML = validator.validate( getResource( "invalidXml.dmn" ), VALIDATE_SCHEMA);
        assertThat( ValidatorUtil.formatMessages( validateXML ), validateXML.size(), is( 1 ) );
        assertThat( validateXML.get( 0 ).toString(), validateXML.get( 0 ).getMessageType(), is( DMNMessageType.FAILED_XML_VALIDATION ) );
        assertThat( validateXML.get(0).getPath(), containsString("invalidXml.dmn") );
    }
    
    @Test
    public void testInvalidXml_builder() {
        List<DMNMessage> validateXML = validator.validateUsing(VALIDATE_SCHEMA).theseModels(getResource( "invalidXml.dmn" ), getResource( "0001-input-data-string.dmn", DMNInputRuntimeTest.class ));
        assertThat( ValidatorUtil.formatMessages( validateXML ), validateXML.size(), is( 1 ) );
        assertThat( validateXML.get( 0 ).toString(), validateXML.get( 0 ).getMessageType(), is( DMNMessageType.FAILED_XML_VALIDATION ) );
        assertThat( validateXML.get(0).getPath(), containsString("invalidXml.dmn") );
    }

}
