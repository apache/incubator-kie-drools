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

package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class DTinBKMTest extends AbstractDTAnalysisTest {
    @Test
    public void testDTnestedEverywhere() {
        List<DMNMessage> validate = validator.validate(getReader("dtInBKM.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_860A5A56-0C43-4B42-B1DB-7415984E5624");

        assertThat(analysis.getGaps(), hasSize(0));
        assertThat(analysis.getOverlaps(), hasSize(0));
        assertThat(validate, hasSize(1));
        assertThat(validate.get(0).getMessageType(), is(DMNMessageType.DECISION_TABLE_ANALYSIS_EMPTY));
    }
}
