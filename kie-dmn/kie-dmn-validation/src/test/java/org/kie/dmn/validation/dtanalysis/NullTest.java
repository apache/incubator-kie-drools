/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;

public class NullTest extends AbstractDTAnalysisTest {

    @Test
    public void testNullBooleanBefore() {
        List<DMNMessage> validate = validator.validate(getReader("NullBooleanBefore.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkNullBoolean(validate);
    }

    @Test
    public void testNullBooleanAfter() {
        List<DMNMessage> validate = validator.validate(getReader("NullBooleanAfter.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkNullBoolean(validate);
    }

    private void checkNullBoolean(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_76FABA99-C6D0-4C83-81BF-92E807DBDEF8");

        assertThat(analysis.getGaps(), hasSize(1));
        
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = Arrays.asList(new Hyperrectangle(1,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(true,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(true,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))));
        assertThat(gaps, hasSize(1));

        // Assert GAPS
        assertThat(analysis.getGaps(), contains(gaps.toArray()));

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps(), hasSize(0));
    }

    @Test
    public void testNullNumberBefore() {
        List<DMNMessage> validate = validator.validate(getReader("NullNumberBefore.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkNullNumber(validate);
    }

    @Test
    public void testNullNumberAfter() {
        List<DMNMessage> validate = validator.validate(getReader("NullNumberAfter.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkNullNumber(validate);
    }

    private void checkNullNumber(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_76FABA99-C6D0-4C83-81BF-92E807DBDEF8");

        assertThat(analysis.getGaps(), hasSize(1));

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = Arrays.asList(new Hyperrectangle(1,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(Interval.POS_INF,
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))));
        assertThat(gaps, hasSize(1));

        // Assert GAPS
        assertThat(analysis.getGaps(), contains(gaps.toArray()));

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps(), hasSize(0));
    }

    @Test
    public void testGapsXYv2WithNull() {
        List<DMNMessage> validate = validator.validate(getReader("GapsXYv2WithNull.dmn"), VALIDATE_MODEL, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        GapsXYTest.checkAnalysis(validate);
    }
}
