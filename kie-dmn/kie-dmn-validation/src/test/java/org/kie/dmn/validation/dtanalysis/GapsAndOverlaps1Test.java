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
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.kie.dmn.validation.dtanalysis.model.Overlap;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

public class GapsAndOverlaps1Test extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("GapsAndOverlaps1.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_cd2e0a28-3cc2-456b-90b6-392d9c3574af");
        
        assertThat(analysis.getGaps(), hasSize(17));

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = Arrays.asList(new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("1"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("1"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("1"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("1"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("5"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("1"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("6"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("3"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("3"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("4"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("5"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("3"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("6"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("3"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("4"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("3"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("4"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("4"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("4"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("5"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("1"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("4"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("5"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("4"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("5"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("6"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("6"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("7"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("6"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("7"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("3"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("7"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))));
        assertThat(gaps, hasSize(17));

        // Assert GAPS
        assertThat(analysis.getGaps(), contains(gaps.toArray()));

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps(), hasSize(1));

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = Arrays.asList(new Overlap(Arrays.asList(1,
                                                                         3),
                                                           new Hyperrectangle(2,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("2"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(new BigDecimal("4"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null)),
                                                                                            Interval.newFromBounds(new Bound(new BigDecimal("1"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(new BigDecimal("2"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null))))));
        assertThat(overlaps, hasSize(1));

        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps(), contains(overlaps.toArray()));
    }
}
