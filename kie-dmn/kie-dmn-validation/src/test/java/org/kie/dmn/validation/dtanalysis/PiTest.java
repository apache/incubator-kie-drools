/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import static org.junit.Assert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

public class PiTest extends AbstractDTAnalysisTest {

    @Test
    public void testPi() {
        List<DMNMessage> validate = validator.validate(getReader("Pi.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkAnalysis(validate);
    }

    private void checkAnalysis(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_F32338CF-6F14-4E66-95AE-8BD4276BA75F");

        assertThat(analysis.getGaps(), hasSize(1));
        
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = Arrays.asList(new Hyperrectangle(1,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("3.1415926535897932384626433832794"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("3.1415926535897932384626433832794"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))));
        assertThat(gaps, hasSize(1));

        // Assert GAPS
        assertThat(analysis.getGaps(), contains(gaps.toArray()));

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps(), hasSize(0));
    }
}
