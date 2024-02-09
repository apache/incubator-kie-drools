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
import org.kie.dmn.api.core.DMNMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;


public class ValidatorDMN14Test extends AbstractValidatorTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(ValidatorDMN14Test.class);
	
    @Test
    public void testSimple_ReaderInput() throws IOException {
        try (final Reader reader = getReader("dmn14simple.dmn")) {
            final List<DMNMessage> validate = validator.validate(
                    reader,
                    VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(validate).hasSize(0);
        }
    }

    @Test
    public void testSimple_FileInput() {
        final List<DMNMessage> validate = validator.validate(
                getFile("dmn14simple.dmn"),
                VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).hasSize(0);
    }

    @Test
    public void testSimple_DefintionsInput() {
        final List<DMNMessage> validate = validator.validate(
                getDefinitions("dmn14simple.dmn",
                               "http://www.trisotech.com/definitions/_d9232146-7aaa-49a9-8668-261a01844ace",
                               "Drawing 1"),
                VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).hasSize(0);
    }

    @Test
    public void testBoxedExtension_Conditional14() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .theseModels(getReader("dmn14boxed/conditional.dmn"));
        LOG.debug("{}", validate);
        assertThat(validate).hasSize(0);
    }

    @Test
    public void testBoxedExtension_Iterator14() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .theseModels(getReader("dmn14boxed/iterator.dmn"));
        LOG.debug("{}", validate);
        assertThat(validate).hasSize(0);
    }
    @Test
    public void testBoxedExtension_IteratorDataType14() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .theseModels(getReader("dmn14boxed/iterator-datatype.dmn"));
        assertThat(validate).hasSize(0);
    }

    @Test
    public void testBoxedExtension_Filter14() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .theseModels(getReader("dmn14boxed/filter.dmn"));
        assertThat(validate).hasSize(0);
    }

    @Test
    public void testBoxedExtension_FilterDataType14() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .theseModels(getReader("dmn14boxed/filter-datatype.dmn"));
        assertThat(validate).hasSize(0);
    }
}
