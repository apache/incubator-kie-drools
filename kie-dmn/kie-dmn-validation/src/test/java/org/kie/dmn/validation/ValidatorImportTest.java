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
import org.kie.dmn.validation.DMNValidator.Validation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ValidatorImportTest extends AbstractValidatorTest {

    @Test
    public void testBaseModel_OK__ReaderInput() throws IOException {
        try (final Reader reader0 = getReader("import/Base-model.dmn");
                final Reader reader1 = getReader("import/Import-base-model.dmn");) {
            final List<DMNMessage> messages = ((DMNValidatorImpl) validator).validateUsing( // VALIDATE_SCHEMA, disabled, due to QName use not compliant. 
                                                                                           Validation.VALIDATE_MODEL,
                                                                                           Validation.VALIDATE_COMPILATION)
                                                                            .theseModels(reader0, reader1);
            assertThat( messages.toString(), messages.size(), is( 0 ) );
        }
    }


}
