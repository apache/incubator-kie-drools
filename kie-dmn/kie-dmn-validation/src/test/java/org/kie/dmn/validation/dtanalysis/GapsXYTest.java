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

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

public class GapsXYTest extends AbstractDTAnalysisTest {

    @Test
    void gaps_xy() {
        List<DMNMessage> validate = validator.validate(getReader("GapsXY.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkAnalysis(validate);
    }

    @Test
    void gaps_xy_using_resource() {
        List<DMNMessage> validate = validator.validate(getResource("GapsXY.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkAnalysis(validate);
        LOG.debug("{}", validate);
        assertThat(validate).allMatch(m -> m.getPath().endsWith("GapsXY.dmn"));
    }

    @Test
    void gaps_xy_using_builder_with_resource() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE).theseModels(getResource("GapsXY.dmn"));
        checkAnalysis(validate);
        LOG.debug("{}", validate);
        assertThat(validate).allMatch(m -> m.getPath().endsWith("GapsXY.dmn"));
    }

    @Test
    void gaps_xyv2() {
        List<DMNMessage> validate = validator.validate(getReader("GapsXYv2.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkAnalysis(validate);
    }

    public static void checkAnalysis(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_ce297a95-b16c-4631-8da5-e739dac9e3c4");

        assertThat(analysis.getGaps()).hasSize(3);
        
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = Arrays.asList(new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))),
                                                  new Hyperrectangle(2,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))));
        assertThat(gaps).hasSize(3);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);
    }
}
