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
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;

class NullTest extends AbstractDTAnalysisTest {

    @Test
    void nullBooleanBefore() {
        List<DMNMessage> validate = validator.validate(getReader("NullBooleanBefore.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkNullBoolean(validate);
    }

    @Test
    void nullBooleanAfter() {
        List<DMNMessage> validate = validator.validate(getReader("NullBooleanAfter.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkNullBoolean(validate);
    }

    private void checkNullBoolean(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_76FABA99-C6D0-4C83-81BF-92E807DBDEF8");

        assertThat(analysis.getGaps()).hasSize(1);
        
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound(true,
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound(true,
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);
    }

    @Test
    void nullNumberBefore() {
        List<DMNMessage> validate = validator.validate(getReader("NullNumberBefore.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkNullNumber(validate);
    }

    @Test
    void nullNumberAfter() {
        List<DMNMessage> validate = validator.validate(getReader("NullNumberAfter.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkNullNumber(validate);
    }

    private void checkNullNumber(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_76FABA99-C6D0-4C83-81BF-92E807DBDEF8");

        assertThat(analysis.getGaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound(Interval.POS_INF,
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);
    }

    @Test
    void gapsXYv2WithNull() {
        List<DMNMessage> validate = validator.validate(getReader("GapsXYv2WithNull.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        GapsXYTest.checkAnalysis(validate);
    }

    @Test
    void notNullAndEmptyRule() {
        final List<DMNMessage> validate = validator.validate(getReader("notnulltest.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        final DTAnalysis analysis = getAnalysis(validate, "_850C5F03-DA51-4DE7-89E4-61D2C502A03E");
        assertThat(analysis.getOverlaps()).hasSize(1);

        final Hyperrectangle overlap = new Hyperrectangle(1,
                List.of(Interval.newFromBounds(
                        new Bound(Interval.NEG_INF, RangeBoundary.CLOSED, null),
                        new Bound(Interval.POS_INF, RangeBoundary.CLOSED, null))));

        assertThat(analysis.getOverlaps().get(0).getOverlap()).isEqualTo(overlap);
    }

    @Test
    void nullsWithActiveRules() {
        final List<DMNMessage> validate = validator.validate(getReader("nulltestdt.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        final DTAnalysis analysis = getAnalysis(validate, "decisiontablewithnulls");
        assertThat(analysis.getOverlaps()).isEmpty();
        assertThat(analysis.getGaps()).isEmpty();
        assertThat(validate).hasSize(1);
    }
}
