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

package org.kie.dmn.validation.mcdc;

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.dtanalysis.AbstractDTAnalysisTest;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class ExampleMCDCTest extends AbstractDTAnalysisTest {

    @Test
    public void test1() {
        List<DMNMessage> validate = validator.validate(getReader("example1.dmn"), ANALYZE_DECISION_TABLE, DMNValidator.Validation.COMPUTE_DECISION_TABLE_MCDC);

        DTAnalysis analysis = getAnalysis(validate, "_452a0adf-dd49-47c3-b02d-fe0ad45902c7");
    }

    @Test
    public void test2() {
        List<DMNMessage> validate = validator.validate(getReader("example2.dmn"), ANALYZE_DECISION_TABLE, DMNValidator.Validation.COMPUTE_DECISION_TABLE_MCDC);

        // DTAnalysis analysis = getAnalysis(validate, "_452a0adf-dd49-47c3-b02d-fe0ad45902c7");
    }
}
