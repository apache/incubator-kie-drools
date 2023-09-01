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

import org.junit.Test;
import org.kie.api.builder.Message.Level;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class ValidatorDMNDITest extends AbstractValidatorTest {

    @Test
    public void testAllElements() throws IOException {
        try (final Reader reader = getReader("dmndi/all-elements.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);

            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
        }
    }

    @Test
    public void testMISSING_DMNSHAPE_MISSING_DMNEDGE() throws IOException {
        try (final Reader reader = getReader("dmndi/all-elements-with-dmndi-no-dmnshape.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);

            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(12);
            assertThat(validate.stream().filter(p -> p.getLevel() == Level.WARNING &&
                                                     p.getMessageType().equals(DMNMessageType.DMNDI_MISSING_DIAGRAM)).count()).isEqualTo(12L);
        }
    }

    @Test
    public void testUNKNOWN_REF() throws IOException {
        try (final Reader reader = getReader("dmndi/all-elements-invalid-ref.dmn")) {
            final List<DMNMessage> validate = validator.validate(reader, VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);

            assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(24);
            assertThat(validate.stream().filter(p -> p.getLevel() == Level.WARNING &&
                                                     p.getMessageType().equals(DMNMessageType.DMNDI_MISSING_DIAGRAM)).count()).isEqualTo(12L);
            assertThat(validate.stream().filter(p -> p.getLevel() == Level.ERROR &&
                                                     p.getMessageType().equals(DMNMessageType.DMNDI_UNKNOWN_REF)).count()).isEqualTo(12L);
        }
    }
}
