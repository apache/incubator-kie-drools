/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.validation.dtanalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.OverlappingIssue;
import org.drools.verifier.api.reporting.Severity;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.model.v1_3.TDecisionTable;
import org.kie.dmn.validation.dtanalysis.model.DDTATable;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.junit.Assert.assertEquals;

public class AnalysisMsgConverterTest {

    private final TDecisionTable dt = new TDecisionTable();
    private final DDTATable ddtaTable = new DDTATable();
    private final HashSet<Issue> issues = new HashSet<>();

    @Before
    public void setUp() throws Exception {
        dt.setId("testId");
    }

    @Test
    public void uniqueOverlap() {
        issues.add(new OverlappingIssue(Severity.WARNING,
                                        CheckType.OVERLAPPING_ROWS,
                                        new ArrayList<>(), // TODO fill this with something?
                                        true,
                                        new HashMap<>(),
                                        getRowNumbers(1, 2)));

        final Collection<DMNDTAnalysisMessage> convert = new AnalysisMsgConverter(dt,
                                                                                  ddtaTable,
                                                                                  new DTAnalysis(dt,
                                                                                                 ddtaTable)).convert(issues).getIssues();

        assertEquals(1, convert.size());

        final DMNDTAnalysisMessage message = convert.iterator().next();
        assertEquals(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_UNIQUE, message.getMessageType());
        assertEquals(DMNMessage.Severity.ERROR, message.getSeverity());
        assertEquals("testId", message.getSourceId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void needsAtLeastTwoRowNumbers() {
        issues.add(new OverlappingIssue(Severity.WARNING,
                                        CheckType.OVERLAPPING_ROWS,
                                        new ArrayList<>(), // TODO fill this with something?
                                        true,
                                        new HashMap<>(),
                                        Collections.emptySet()));

        new AnalysisMsgConverter(dt,
                                 ddtaTable,
                                 new DTAnalysis(dt,
                                                ddtaTable)).convert(issues);
    }

    @Test
    public void basic() {
        issues.add(new Issue(Severity.NOTE,
                             CheckType.MISSING_RANGE,
                             Collections.emptySet()));

        final Collection<DMNDTAnalysisMessage> convert = new AnalysisMsgConverter(dt,
                                                                                  ddtaTable,
                                                                                  new DTAnalysis(dt,
                                                                                                 ddtaTable)).convert(issues).getIssues();

        assertEquals(1, convert.size());

        final DMNDTAnalysisMessage message = convert.iterator().next();
        assertEquals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR, message.getMessageType());
        assertEquals(DMNMessage.Severity.INFO, message.getSeverity());
        assertEquals("testId", message.getSourceId());
    }

    private Set<Integer> getRowNumbers(final int... rowNumbers) {
        final Set<Integer> result = new HashSet<>();

        for (int rowNumber : rowNumbers) {
            result.add(rowNumber);
        }

        return result;
    }
}