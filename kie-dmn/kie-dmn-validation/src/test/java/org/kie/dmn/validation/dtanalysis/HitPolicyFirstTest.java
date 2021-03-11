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
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class HitPolicyFirstTest extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("DTAnalysisFirst.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_38EB6C20-6DF4-4EA0-A421-206B9F31AF22");

        assertThat(analysis.getGaps(), hasSize(0));
        assertThat(analysis.getOverlaps(), hasSize(0));
        assertTrue("It should contain at least 1 DMNMessage for the type " + DMNMessageType.DECISION_TABLE_1STNFVIOLATION,
                   validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_1STNFVIOLATION)));
    }
}
