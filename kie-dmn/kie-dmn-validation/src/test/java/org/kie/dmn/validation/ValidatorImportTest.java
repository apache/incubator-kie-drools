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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class ValidatorImportTest extends AbstractValidatorTest {

    @Test
    public void testBaseModel_OK__ReaderInput() throws IOException {
        try (final Reader reader = getReader("import/Import-base-model.dmn")) {
            final List<DMNMessage> messages = validator.validate(reader,
                                                                 VALIDATE_SCHEMA, VALIDATE_MODEL);// TODO , VALIDATE_COMPILATION);
            assertThat( messages.toString(), messages.size(), is( 0 ) );
        }
    }


}
