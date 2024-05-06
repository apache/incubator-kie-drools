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
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

class GapsOverlapsBooleanTest extends AbstractDTAnalysisTest {

    @Test
    void test() {
        List<DMNMessage> validate = validator.validate(getReader("GapsOverlapsBoolean.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_EE34FD37-00D1-47A7-B2F6-CC9BCEF30005");

        assertThat(analysis.getGaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(2,
                                                               Arrays.asList(Interval.newFromBounds(new Bound(true,
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null),
                                                                                                    new Bound(true,
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null)),
                                                                             Interval.newFromBounds(new Bound(true,
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null),
                                                                                                    new Bound(true,
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
                                                     new Hyperrectangle(2,
                                                                        Arrays.asList(Interval.newFromBounds(new Bound(false,
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null),
                                                                                                             new Bound(true,
                                                                                                                       RangeBoundary.OPEN,
                                                                                                                       null)),
                                                                                      Interval.newFromBounds(new Bound(false,
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null),
                                                                                                             new Bound(true,
                                                                                                                       RangeBoundary.OPEN,
                                                                                                                       null))))));
        assertThat(overlaps).hasSize(1);

        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);
    }
}
