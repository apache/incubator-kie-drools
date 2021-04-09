/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.kie.api.builder.Message.Level;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class Check1stNFViolationTest extends AbstractDTAnalysisTest {

    @Test
    public void testCheck1stNFViolation() {
        List<DMNMessage> validate = validator.validate(getReader("DT1stNFViolation.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysisDuplicate = getAnalysis(validate, "_053034d5-0e1f-4c4a-8513-ab3c6ba538db");
        assertThat(analysisDuplicate.is1stNFViolation(), is(true));
        assertThat(analysisDuplicate.getDuplicateRulesTuples(), hasSize(1));
        assertThat(analysisDuplicate.getDuplicateRulesTuples(), contains(Collections.singletonList(Arrays.asList(1, 2)).toArray()));
        assertTrue("It should contain at DMNMessage for the 1st NF Violation",
                   validate.stream().anyMatch(p -> p.getSourceId().equals("_053034d5-0e1f-4c4a-8513-ab3c6ba538db") && p.getMessageType().equals(DMNMessageType.DECISION_TABLE_1STNFVIOLATION)));

        DTAnalysis analysisFIRST = getAnalysis(validate, "_1ca6acde-c1d4-4c50-8e21-f3b11e106f3d");
        assertThat(analysisFIRST.is1stNFViolation(), is(true));
        assertTrue("It should contain at DMNMessage for the 1st NF Violation",
                   validate.stream().anyMatch(p -> p.getSourceId().equals("_1ca6acde-c1d4-4c50-8e21-f3b11e106f3d") && p.getMessageType().equals(DMNMessageType.DECISION_TABLE_1STNFVIOLATION)));

        DTAnalysis analysisRULE_ORDER = getAnalysis(validate, "_03522945-b520-4b45-ac5e-ef3cbd7f1eaf");
        assertThat(analysisRULE_ORDER.is1stNFViolation(), is(true));
        assertTrue("It should contain at DMNMessage for the 1st NF Violation",
                   validate.stream().anyMatch(p -> p.getSourceId().equals("_03522945-b520-4b45-ac5e-ef3cbd7f1eaf") && p.getMessageType().equals(DMNMessageType.DECISION_TABLE_1STNFVIOLATION)));
    }

    @Test
    public void testCheck1stNFViolationB() {
        List<DMNMessage> validate = validator.validate(getReader("DT1stNFViolationB.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysisDuplicate = getAnalysis(validate, "_053034d5-0e1f-4c4a-8513-ab3c6ba538db");
        assertThat(analysisDuplicate.is1stNFViolation(), is(true));
        assertThat(analysisDuplicate.getDuplicateRulesTuples(), hasSize(1));
        assertThat(analysisDuplicate.getDuplicateRulesTuples(), contains(Collections.singletonList(Arrays.asList(1, 2)).toArray()));
        assertTrue("It should contain at DMNMessage for the 1st NF Violation",
                   validate.stream().anyMatch(p -> p.getSourceId().equals("_053034d5-0e1f-4c4a-8513-ab3c6ba538db") && p.getMessageType().equals(DMNMessageType.DECISION_TABLE_1STNFVIOLATION)));
    }

    @Test
    public void testCheck1stNFViolationDuplicateNoSubsumption() {
        List<DMNMessage> validate = validator.validate(getReader("DT1stNFViolationDuplicateNoSubsumption.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysis = getAnalysis(validate, "_221BF4A4-F8D4-466C-96E4-311FE3C9867B");
        assertThat(analysis.is1stNFViolation(), is(true));
        assertThat(analysis.getDuplicateRulesTuples(), hasSize(1));
        assertThat(analysis.getDuplicateRulesTuples(), contains(Collections.singletonList(Arrays.asList(1, 2)).toArray()));
        assertTrue("It should contain at DMNMessage for the 1st NF Violation",
                   validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_1STNFVIOLATION)));
        assertThat(analysis.getSubsumptions().isEmpty(), is(false));
        assertTrue("No message about subsumption",
                   validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_SUBSUMPTION_RULE)));
    }

    @Test
    public void testCheck1stNFViolationCollect() {
        List<DMNMessage> validate = validator.validate(getReader("DT1stNFViolationCollect.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysisDuplicate = getAnalysis(validate, "_4237d55b-2589-48a5-8183-f9f4e0e00c07");
        assertThat(analysisDuplicate.is1stNFViolation(), is(true));
        assertThat(analysisDuplicate.getDuplicateRulesTuples(), hasSize(2));
        assertTrue("It should contain DMNMessage(s) for the 1st NF Violation",
                   validate.stream().anyMatch(p -> p.getSourceId().equals("_4237d55b-2589-48a5-8183-f9f4e0e00c07") && p.getMessageType().equals(DMNMessageType.DECISION_TABLE_1STNFVIOLATION)));
        assertTrue("Being a C table, DMNMessage(s) for the 1st NF Violation are of type Warning",
                   validate.stream()
                           .filter(p -> p.getSourceId().equals("_4237d55b-2589-48a5-8183-f9f4e0e00c07") && p.getMessageType().equals(DMNMessageType.DECISION_TABLE_1STNFVIOLATION))
                           .allMatch(p -> p.getLevel() == Level.WARNING));
    }
}
