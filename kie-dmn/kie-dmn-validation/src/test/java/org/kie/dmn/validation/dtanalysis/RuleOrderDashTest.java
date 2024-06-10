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
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.kie.dmn.validation.dtanalysis.model.Overlap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

class RuleOrderDashTest extends AbstractDTAnalysisTest {

    @Test
    void test() {
        List<DMNMessage> validate = validator.validate(getReader("RuleOrderDash.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_eb02106a-cee1-47f5-a9d9-3160c5ac868b");

        // Assert GAPS count.
        assertThat(analysis.getGaps()).hasSize(0);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(3);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = Arrays.asList(new Overlap(Arrays.asList(3,
                                                                         1),
                                                           new Hyperrectangle(2,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(new BigDecimal("60"),
                                                                                                                             RangeBoundary.OPEN,
                                                                                                                             null)),
                                                                                            Interval.newFromBounds(new Bound(new BigDecimal("30"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null))))),
                                               new Overlap(Arrays.asList(2,
                                                                         3),
                                                           new Hyperrectangle(2,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("60"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null)),
                                                                                            Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(new BigDecimal("30"),
                                                                                                                             RangeBoundary.OPEN,
                                                                                                                             null))))),
                                               new Overlap(Arrays.asList(2,
                                                                         3,
                                                                         1),
                                                           new Hyperrectangle(2,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("60"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null)),
                                                                                            Interval.newFromBounds(new Bound(new BigDecimal("30"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null))))));
        assertThat(overlaps).hasSize(3);

        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);
    }
}
