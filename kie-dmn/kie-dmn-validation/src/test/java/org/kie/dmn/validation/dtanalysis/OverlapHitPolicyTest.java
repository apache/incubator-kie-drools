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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.kie.dmn.validation.dtanalysis.model.Overlap;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

@RunWith(Parameterized.class)
public class OverlapHitPolicyTest extends AbstractDTAnalysisTest {

    @Parameterized.Parameter()
    public HitPolicy hp;

    @Parameterized.Parameters(name = "using {0}")
    public static Collection<HitPolicy> data() {
        // Overlaps are not checked for COLLECT hit policy
        return Arrays.asList(HitPolicy.values()).stream().filter(hp-> hp!= HitPolicy.COLLECT).collect(Collectors.toList());
    }

    @Test
    public void testOverlapHitPolicy() {
        Definitions definitions = getDefinitions("OverlapHitPolicy.dmn", "https://github.com/kiegroup/drools/kie-dmn/_3010653A-DD3F-4C88-89DA-3FDD845F6604", "OverlapHitPolicy");

        // mutates XML file in the Hit Policy, accordingly to this test parameter.
        ((DecisionTable) ((Decision) definitions.getDrgElement().get(0)).getExpression()).setHitPolicy(hp);

        List<DMNMessage> validate = validator.validate(definitions, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkAnalysis(validate);

        if (hp == HitPolicy.UNIQUE) {
            assertTrue("It should contain at least 1 DMNMessage for the type",
                       validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_UNIQUE)));
        } else if (hp == HitPolicy.ANY) {
            assertTrue("It should contain at least 1 DMNMessage for the type",
                       validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_ANY)));
        } else {
            LOG.debug("Testing for {} I am expecting there is NOT DMNMessage pertaining to Overlaps", hp);
            assertTrue(validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_UNIQUE)) &&
                       validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_ANY)) &&
                       validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_OVERLAP)));
        }
    }

    private void checkAnalysis(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_C4A1625B-0606-4F2D-9779-49B1A981718E");

        assertThat(analysis.getGaps(), hasSize(0));

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps(), hasSize(1));

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = Arrays.asList(new Overlap(Arrays.asList(2,
                                                                         3),
                                                           new Hyperrectangle(1,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("30"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(new BigDecimal("30"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null))))));
        assertThat(overlaps, hasSize(1));

        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps(), contains(overlaps.toArray()));
    }

}
