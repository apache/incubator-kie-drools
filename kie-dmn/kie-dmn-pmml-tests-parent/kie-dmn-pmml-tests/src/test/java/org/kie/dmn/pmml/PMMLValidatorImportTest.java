/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.pmml;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.ValidatorUtil;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class PMMLValidatorImportTest extends AbstractValidatorTest {

    @Test
    public void testImportPMML() throws IOException {
        // DROOLS-4187 kie-dmn-validation: Incorrect import detection
        final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL)
                .theseModels(getReader("Invoke_Iris.dmn", PMMLValidatorImportTest.class));
        assertThat(ValidatorUtil.formatMessages(messages), messages.size(), is(0));

    }

    @Test
    public void testImportPMML2() throws IOException {
        // DROOLS-4395 [DMN Designer] Validation fails for included PMML model
        try (Reader defsReader = getReader("KiePMMLScoreCard_wInputType.dmn", DMNRuntimePMMLTest.class);) {
            final Definitions defs = getDefinitions(defsReader,
                    "http://www.trisotech.com/definitions/_ca466dbe-20b4-4e88-a43f-4ce3aff26e4f",
                    "KiePMMLScoreCard");
            DMNValidator.ValidatorBuilder.ValidatorImportReaderResolver resolver = (ns, name, i) -> {
                if (ns.equals(defs.getNamespace()) && name.equals(defs.getName()) && i.equals(defs.getImport().get(0).getLocationURI())) {
                    return getReader("test_scorecard.pmml", DMNRuntimePMMLTest.class);
                } else {
                    return null;
                }
            };
            final List<DMNMessage> messages = validator.validateUsing(Validation.VALIDATE_MODEL,
                    Validation.VALIDATE_COMPILATION)
                    .usingImports(resolver)
                    .theseModels(defs);
            assertThat(ValidatorUtil.formatMessages(messages), messages.size(), is(0));
        }
    }
}
