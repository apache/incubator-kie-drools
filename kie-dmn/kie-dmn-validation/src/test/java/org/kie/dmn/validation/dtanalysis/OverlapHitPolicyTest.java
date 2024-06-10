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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

public class OverlapHitPolicyTest extends AbstractDTAnalysisTest {
    public HitPolicy hp;

    public static Collection<HitPolicy> data() {
        // Overlaps are not checked for COLLECT hit policy
        return Arrays.asList(HitPolicy.values()).stream().filter(hp-> hp!= HitPolicy.COLLECT).collect(Collectors.toList());
    }

    @MethodSource("data")
    @ParameterizedTest(name = "using {0}")
    public void overlapHitPolicy(HitPolicy hp) {
        initOverlapHitPolicyTest(hp);
        Definitions definitions = getDefinitions("OverlapHitPolicy.dmn", "https://github.com/kiegroup/drools/kie-dmn/_3010653A-DD3F-4C88-89DA-3FDD845F6604", "OverlapHitPolicy");

        // mutates XML file in the Hit Policy, accordingly to this test parameter.
        ((DecisionTable) ((Decision) definitions.getDrgElement().get(0)).getExpression()).setHitPolicy(hp);

        List<DMNMessage> validate = validator.validate(definitions, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkAnalysis(validate);

        if (hp == HitPolicy.UNIQUE) {
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_UNIQUE))).as("It should contain at least 1 DMNMessage for the type").isTrue();
        } else if (hp == HitPolicy.ANY) {
            assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_ANY))).as("It should contain at least 1 DMNMessage for the type").isTrue();
        } else {
            LOG.debug("Testing for {} I am expecting there is NOT DMNMessage pertaining to Overlaps", hp);
            assertThat(validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_UNIQUE)) &&
                    validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_ANY)) &&
                    validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_OVERLAP))).isTrue();
        }
    }

    private void checkAnalysis(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_C4A1625B-0606-4F2D-9779-49B1A981718E");

        assertThat(analysis.getGaps()).hasSize(0);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = List.of(new Overlap(Arrays.asList(2,
                                                                   3),
                                                     new Hyperrectangle(1,
                                                                        List.of(Interval.newFromBounds(new Bound(new BigDecimal("30"),
                                                                                                                 RangeBoundary.CLOSED,
                                                                                                                 null),
                                                                                                       new Bound(new BigDecimal("30"),
                                                                                                                 RangeBoundary.CLOSED,
                                                                                                                 null))))));
        assertThat(overlaps).hasSize(1);

        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);
    }

    public void initOverlapHitPolicyTest(HitPolicy hp) {
        this.hp = hp;
    }

}
