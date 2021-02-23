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

package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class SomeProblemTest extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("SomeProblem.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysis1 = getAnalysis(validate, "_a36e37f8-aae0-4118-8267-cbb37c7955cb");
        assertThat(analysis1.isError(), is(false));
        assertThat(analysis1.getGaps(), hasSize(0));
        assertThat(analysis1.getOverlaps(), hasSize(0));
        
        DTAnalysis analysis2 = getAnalysis(validate, "_2aea80b4-19fa-4831-8829-4db925a128aa");
        assertThat(analysis2.isError(), is(true));
    }
}
