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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.kie.dmn.validation.dtanalysis.model.MaskedRule;
import org.kie.dmn.validation.dtanalysis.model.Overlap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

class MaskTest extends AbstractDTAnalysisTest {

    @Test
    void mask_basic() {
        List<DMNMessage> validate = validator.validate(getReader("MaskBasic.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_BA703D04-803A-44AA-8A31-F5EEDD4FD54E");
        assertThat(analysis.getGaps()).hasSize(0);
        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(1);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = List.of(new Overlap(Arrays.asList(2,
                                                                   1),
                                                     new Hyperrectangle(1,
                                                                        List.of(Interval.newFromBounds(new Bound(new BigDecimal("18"),
                                                                                                                 RangeBoundary.CLOSED,
                                                                                                                 null),
                                                                                                       new Bound(Interval.POS_INF,
                                                                                                                 RangeBoundary.CLOSED,
                                                                                                                 null))))));
        assertThat(overlaps).hasSize(1);
        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);

        // MaskedRules count.
        assertThat(analysis.getMaskedRules()).hasSize(1);
        List<MaskedRule> maskedRules = List.of(new MaskedRule(1, 2));
        assertThat(maskedRules).hasSize(1);
        assertThat(analysis.getMaskedRules()).containsAll(maskedRules);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MASKED_RULE))).as("It should contain at least 1 DMNMessage for the MaskedRule").isTrue();
        assertThat(validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MISLEADING_RULE))).as("It should not contain DMNMessage for the MisleadingRule").isTrue();
    }

    @Test
    void mask_test() {
        List<DMNMessage> validate = validator.validate(getReader("MaskTest.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_BA703D04-803A-44AA-8A31-F5EEDD4FD54E");
        assertThat(analysis.getGaps()).hasSize(0);
        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(1);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = List.of(new Overlap(Arrays.asList(2,
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
        assertThat(overlaps).hasSize(1);
        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);

        // MaskedRules count.
        assertThat(analysis.getMaskedRules()).hasSize(1);
        List<MaskedRule> maskedRules = List.of(new MaskedRule(1, 2));
        assertThat(maskedRules).hasSize(1);
        assertThat(analysis.getMaskedRules()).containsAll(maskedRules);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MASKED_RULE))).as("It should contain at least 1 DMNMessage for the MaskedRule").isTrue();
        assertThat(validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MISLEADING_RULE))).as("It should not contain DMNMessage for the MisleadingRule").isTrue();
    }

}
