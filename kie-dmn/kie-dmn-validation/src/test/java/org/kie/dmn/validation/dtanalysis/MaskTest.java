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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.kie.dmn.validation.dtanalysis.model.MaskedRule;
import org.kie.dmn.validation.dtanalysis.model.Overlap;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

public class MaskTest extends AbstractDTAnalysisTest {

    @Test
    public void test_MaskBasic() {
        List<DMNMessage> validate = validator.validate(getReader("MaskBasic.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_BA703D04-803A-44AA-8A31-F5EEDD4FD54E");
        assertThat(analysis.getGaps(), hasSize(0));
        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps(), hasSize(1));
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = Arrays.asList(new Overlap(Arrays.asList(2,
                                                                         1),
                                                           new Hyperrectangle(1,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("18"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null))))));
        assertThat(overlaps, hasSize(1));
        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps(), contains(overlaps.toArray()));

        // MaskedRules count.
        assertThat(analysis.getMaskedRules(), hasSize(1));
        List<MaskedRule> maskedRules = Arrays.asList(new MaskedRule(1, 2));
        assertThat(maskedRules, hasSize(1));
        assertThat(analysis.getMaskedRules(), contains(maskedRules.toArray()));
        assertTrue("It should contain at least 1 DMNMessage for the MaskedRule",
                   validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MASKED_RULE)));
        assertTrue("It should not contain DMNMessage for the MisleadingRule",
                   validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MISLEADING_RULE)));
    }

    @Test
    public void test_MaskTest() {
        List<DMNMessage> validate = validator.validate(getReader("MaskTest.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_BA703D04-803A-44AA-8A31-F5EEDD4FD54E");
        assertThat(analysis.getGaps(), hasSize(0));
        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps(), hasSize(1));
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = Arrays.asList(new Overlap(Arrays.asList(2,
                                                                         1),
                                                           new Hyperrectangle(3,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("18"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null)),
                                                                                            Interval.newFromBounds(new Bound("L",
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound("M",
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null)),
                                                                                            Interval.newFromBounds(new Bound(true,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(true,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null))))));
        assertThat(overlaps, hasSize(1));
        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps(), contains(overlaps.toArray()));

        // MaskedRules count.
        assertThat(analysis.getMaskedRules(), hasSize(1));
        List<MaskedRule> maskedRules = Arrays.asList(new MaskedRule(1, 2));
        assertThat(maskedRules, hasSize(1));
        assertThat(analysis.getMaskedRules(), contains(maskedRules.toArray()));
        assertTrue("It should contain at least 1 DMNMessage for the MaskedRule",
                   validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MASKED_RULE)));
        assertTrue("It should not contain DMNMessage for the MisleadingRule",
                   validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MISLEADING_RULE)));
    }

}
