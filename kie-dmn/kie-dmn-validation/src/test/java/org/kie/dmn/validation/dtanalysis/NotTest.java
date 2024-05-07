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

class NotTest extends AbstractDTAnalysisTest {

    @Test
    void notString() {
        List<DMNMessage> validate = validator.validate(getReader("DTusingNOT.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_b53fac34-fb12-4601-8605-c226e68292f9");

        assertThat(analysis.getGaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound("i",
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound("o",
                                                                                                        RangeBoundary.OPEN,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = List.of(new Overlap(Arrays.asList(1,
                                                                   2),
                                                     new Hyperrectangle(1,
                                                                        List.of(Interval.newFromBounds(new Bound("o",
                                                                                                                 RangeBoundary.CLOSED,
                                                                                                                 null),
                                                                                                       new Bound("u",
                                                                                                                 RangeBoundary.OPEN,
                                                                                                                 null))))));
        assertThat(overlaps).hasSize(1);

        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);
    }

    @Test
    void notString2() {
        List<DMNMessage> validate = validator.validate(getReader("DTusingNOT2.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_b53fac34-fb12-4601-8605-c226e68292f9");

        assertThat(analysis.getGaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound("i",
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound("o",
                                                                                                        RangeBoundary.OPEN,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);

    }

    @Test
    void notStringVowel() {
        List<DMNMessage> validate = validator.validate(getReader("NotStringVowel.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_406133D7-96FE-4237-8726-44D839F400D6");

        // assert GAPS count
        assertThat(analysis.getGaps()).hasSize(0);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);
    }

    @Test
    void notStringVowel2() {
        List<DMNMessage> validate = validator.validate(getReader("NotStringVowel2.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_406133D7-96FE-4237-8726-44D839F400D6");

        // assert GAPS count
        assertThat(analysis.getGaps()).hasSize(0);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);

        assertThat(validate).hasSize(1);
        assertThat(validate).anyMatch(m -> m.getText().contains("string values which can be enumerated for the inputs; Gap analysis skipped"));
    }

    @Test
    void nOTnumber() {
        List<DMNMessage> validate = validator.validate(getReader("DTusingNOTnumber.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_d0cbacca-55d4-47dd-acc6-131add2a8a53");

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

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = List.of(new Overlap(Arrays.asList(1,
                                                                   2),
                                                     new Hyperrectangle(1,
                                                                        List.of(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                 RangeBoundary.OPEN,
                                                                                                                 null),
                                                                                                       new Bound(Interval.POS_INF,
                                                                                                                 RangeBoundary.CLOSED,
                                                                                                                 null))))));
        assertThat(overlaps).hasSize(1);

        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);
    }

}
