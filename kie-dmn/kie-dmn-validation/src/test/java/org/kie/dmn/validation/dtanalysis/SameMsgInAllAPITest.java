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
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;

class SameMsgInAllAPITest extends AbstractDTAnalysisTest {

    @Test
    void singleModelAPI() {
        List<DMNMessage> validate = validator.validate(getReader("sameMsgInAllAPI.dmn"), VALIDATE_COMPILATION, VALIDATE_MODEL, ANALYZE_DECISION_TABLE);
        verify(validate);
    }

    @Test
    void fluentAPI() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_COMPILATION, VALIDATE_MODEL, ANALYZE_DECISION_TABLE).theseModels(getReader("sameMsgInAllAPI.dmn"));
        verify(validate);
    }

    private void verify(List<DMNMessage> validate) {
        assertThat(validate).hasSize(5);
        DTAnalysis analysis = getAnalysis(validate, "_4771db14-e088-4d5a-8942-211c57ad0b42");

        assertThat(analysis.getGaps()).hasSize(3);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = Arrays.asList(new Hyperrectangle(1,
                                                                     List.of(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null),
                                                                                                    new Bound(new BigDecimal("2"),
                                                                                                              RangeBoundary.OPEN,
                                                                                                              null)))),
                                                  new Hyperrectangle(1,
                                                                     List.of(Interval.newFromBounds(new Bound(new BigDecimal("4"),
                                                                                                              RangeBoundary.OPEN,
                                                                                                              null),
                                                                                                    new Bound(new BigDecimal("6"),
                                                                                                              RangeBoundary.OPEN,
                                                                                                              null)))),
                                                  new Hyperrectangle(1,
                                                                     List.of(Interval.newFromBounds(new Bound(new BigDecimal("8"),
                                                                                                              RangeBoundary.OPEN,
                                                                                                              null),
                                                                                                    new Bound(new BigDecimal("10"),
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null)))));
        assertThat(gaps).hasSize(3);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(2);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = Arrays.asList(new Overlap(Arrays.asList(1,
                                                                         2),
                                                           new Hyperrectangle(1,
                                                                              List.of(Interval.newFromBounds(new Bound(new BigDecimal("3"),
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null),
                                                                                                             new Bound(new BigDecimal("4"),
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null))))),
                                               new Overlap(Arrays.asList(3,
                                                                         4),
                                                           new Hyperrectangle(1,
                                                                              List.of(Interval.newFromBounds(new Bound(new BigDecimal("6"),
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null),
                                                                                                             new Bound(new BigDecimal("7"),
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null))))));
        assertThat(overlaps).hasSize(2);

        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);

        // MaskedRules count.
        assertThat(analysis.getMaskedRules()).hasSize(2);
        List<MaskedRule> maskedRules = Arrays.asList(new MaskedRule(2, 1),
                                                     new MaskedRule(4, 3));
        assertThat(maskedRules).hasSize(2);
        assertThat(analysis.getMaskedRules()).containsAll(maskedRules);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_MASKED_RULE))).as("It should contain DMNMessage for the MaskedRule").isTrue();
    }
}
