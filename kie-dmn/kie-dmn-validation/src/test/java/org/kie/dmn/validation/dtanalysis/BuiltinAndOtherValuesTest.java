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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.kie.dmn.validation.dtanalysis.model.Overlap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

class BuiltinAndOtherValuesTest extends AbstractDTAnalysisTest {

    @Test
    void complexDTdates() {
        List<DMNMessage> validate = validator.validate(getReader("complexDTdates.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_a8a4362e-9f2d-4051-9bd6-e7356244f6b7");

        checkComplexDTDates(analysis);
    }

    @Test
    void complexDTdatesV2() {
        List<DMNMessage> validate = validator.validate(getReader("complexDTdatesV2.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_a8a4362e-9f2d-4051-9bd6-e7356244f6b7");

        checkComplexDTDates(analysis);
    }

    private void checkComplexDTDates(DTAnalysis analysis) {
        assertThat(analysis.getGaps()).hasSize(2);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = Arrays.asList(new Hyperrectangle(1,
                                                                     List.of(Interval.newFromBounds(new Bound(LocalDate.parse("2019-03-31"),
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null),
                                                                                                    new Bound(LocalDate.parse("2019-03-31"),
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null)))),
                                                  new Hyperrectangle(1,
                                                                     List.of(Interval.newFromBounds(new Bound(LocalDate.parse("2019-12-31"),
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null),
                                                                                                    new Bound(LocalDate.parse("2019-12-31"),
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null)))));
        assertThat(gaps).hasSize(2);

        // Assert GAPS same values
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(1);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = List.of(new Overlap(Arrays.asList(2,
                                                                   3),
                                                     new Hyperrectangle(1,
                                                                        List.of(Interval.newFromBounds(new Bound(LocalDate.parse("2019-06-30"),
                                                                                                                 RangeBoundary.CLOSED,
                                                                                                                 null),
                                                                                                       new Bound(LocalDate.parse("2019-06-30"),
                                                                                                                 RangeBoundary.CLOSED,
                                                                                                                 null))))));
        assertThat(overlaps).hasSize(1);
        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);
    }

    @Test
    void weirdPosNeg() {
        List<DMNMessage> validate = validator.validate(getReader("weirdPosNeg.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_54ae95be-6866-4dc1-8c10-1c5a4dd15c93");

        assertThat(analysis.getGaps()).hasSize(1);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound(new BigDecimal("0"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS same values
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);
    }

    @Test
    void weirdYMduration() {
        List<DMNMessage> validate = validator.validate(getReader("weirdYMduration.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_fe7d267b-d770-461e-8300-e09981147341");

        assertThat(analysis.getGaps()).hasSize(1);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound(ComparablePeriod.parse("P1M"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound(ComparablePeriod.parse("P1M"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS same values
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);
    }
}
