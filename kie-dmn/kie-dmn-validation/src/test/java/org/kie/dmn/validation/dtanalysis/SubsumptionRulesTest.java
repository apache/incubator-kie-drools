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
import org.kie.dmn.validation.dtanalysis.model.Overlap;
import org.kie.dmn.validation.dtanalysis.model.Subsumption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

class SubsumptionRulesTest extends AbstractDTAnalysisTest {

    @Test
    void subsumptionRules() {
        List<DMNMessage> validate = validator.validate(getReader("Subsumption.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_82100fc5-8799-4ee2-981f-215ded39e68a");
        assertThat(analysis.getGaps()).hasSize(0);
        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(2);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = Arrays.asList(new Overlap(Arrays.asList(2,
                                                                         3),
                                                           new Hyperrectangle(2,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("21"),
                                                                                                                             RangeBoundary.OPEN,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null)),
                                                                                            Interval.newFromBounds(new Bound(false,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(true,
                                                                                                                             RangeBoundary.OPEN,
                                                                                                                             null))))),
                                               new Overlap(Arrays.asList(2,
                                                                         4),
                                                           new Hyperrectangle(2,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("21"),
                                                                                                                             RangeBoundary.OPEN,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null)),
                                                                                            Interval.newFromBounds(new Bound(true,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(true,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null))))));
        assertThat(overlaps).hasSize(2);
        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);

        // Subsumption count.
        assertThat(analysis.getSubsumptions()).hasSize(1);
        List<Subsumption> results = List.of(new Subsumption(2, 4));
        assertThat(results).hasSize(1);
        assertThat(analysis.getSubsumptions()).containsAll(results);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_SUBSUMPTION_RULE))).as("It should contain at least 1 DMNMessage for the Subsumtption").isTrue();
    }
}
