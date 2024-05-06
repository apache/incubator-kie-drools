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
package org.kie.dmn.trisotech.validation;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.trisotech.TrisotechDMNProfile;
import org.kie.dmn.trisotech.core.compiler.TrisotechDMNEvaluatorCompilerFactory;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class TrisotechValidationTest {

    protected static DMNValidator validator;
    protected static DMNMarshaller marshaller;

    @BeforeAll
    static void init() {
        List<DMNProfile> dmnProfiles = List.of(new TrisotechDMNProfile());
        Properties p = new Properties();
        p.put(DMNAssemblerService.DMN_DECISION_LOGIC_COMPILER, TrisotechDMNEvaluatorCompilerFactory.class.getCanonicalName());
        validator = DMNValidatorFactory.newValidator(dmnProfiles, p);
    }

    @AfterAll
    static void dispose() {
        validator.dispose();
    }

    protected Reader getReader(final String resourceFileName) {
        return getReader(resourceFileName, this.getClass());
    }

    protected Reader getReader(final String resourceFileName, Class<?> clazz) {
        return new InputStreamReader(clazz.getResourceAsStream(resourceFileName));
    }

    @Test
    void boxedExtensionConditional13() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .usingSchema(TrisotechSchema.INSTANCEv1_3)
                                             .theseModels(getReader("boxedcontextextension/conditional.dmn"));
        assertThat(validate).hasSize(0);
    }

    @Test
    void boxedExtensionIterator13() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .usingSchema(TrisotechSchema.INSTANCEv1_3)
                                             .theseModels(getReader("boxedcontextextension/iterator.dmn"));
        assertThat(validate).hasSize(0);
    }

    @Test
    void boxedExtensionIteratorDataType13() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .usingSchema(TrisotechSchema.INSTANCEv1_3)
                                             .theseModels(getReader("boxedcontextextension/iterator-datatype.dmn"));
        assertThat(validate).hasSize(0);
    }

    @Test
    void boxedExtensionFilter13() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .usingSchema(TrisotechSchema.INSTANCEv1_3)
                                             .theseModels(getReader("boxedcontextextension/filter.dmn"));
        assertThat(validate).hasSize(0);
    }

    @Test
    void boxedExtensionFilterDataType13() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .usingSchema(TrisotechSchema.INSTANCEv1_3)
                                             .theseModels(getReader("boxedcontextextension/filter-datatype.dmn"));
        assertThat(validate).hasSize(0);
    }
}
