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
package org.kie.dmn.validation.dtanalysis;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.kie.dmn.validation.dtanalysis.model.MisleadingRule;
import org.kie.dmn.validation.dtanalysis.model.Overlap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

class MisleadingRulesTest extends AbstractDTAnalysisTest {

    @Test
    void misleadingRules() {
        List<DMNMessage> validate = validator.validate(getReader("MisleadingRules.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_BA703D04-803A-44AA-8A31-F5EEDD4FD54E");
        assertThat(analysis.getGaps()).hasSize(0);
        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(1);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = List.of(new Overlap(Arrays.asList(4,
                                                                   2),
                                                     new Hyperrectangle(2,
                                                                        Arrays.asList(Interval.newFromBounds(new Bound(false,
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null),
                                                                                                             new Bound(true,
                                                                                                                       RangeBoundary.OPEN,
                                                                                                                       null)),
                                                                                      Interval.newFromBounds(new Bound("M",
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null),
                                                                                                             new Bound("M",
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null))))));
        assertThat(overlaps).hasSize(1);
        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);

        // MisleadingRules count.
        assertThat(analysis.getMisleadingRules()).hasSize(1);
        List<MisleadingRule> misleadingRules = List.of(new MisleadingRule(4, 2));
        assertThat(misleadingRules).hasSize(1);
        assertThat(analysis.getMisleadingRules()).containsAll(misleadingRules);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MISLEADING_RULE))).as("It should contain at least 1 DMNMessage for the MisleadingRule").isTrue();
        assertThat(validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MASKED_RULE))).as("This test case is not a Masked rule example").isTrue();
    }

    @Test
    void misleadingRules2() {
        List<DMNMessage> validate = validator.validate(getReader("MisleadingRules2.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_0cffdf05-071b-423b-94b9-182c2cc2435c");

        assertThat(analysis.getGaps()).hasSize(0);

        // no need for assert overlaps.

        // MisleadingRules count.
        assertThat(analysis.getMisleadingRules()).hasSize(0);
    }
}
