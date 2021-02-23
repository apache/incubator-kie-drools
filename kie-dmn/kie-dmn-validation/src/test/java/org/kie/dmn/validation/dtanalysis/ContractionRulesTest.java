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
import java.util.stream.Collectors;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Contraction;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Interval;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class ContractionRulesTest extends AbstractDTAnalysisTest {

    @Test
    public void testContractionRules() {
        List<DMNMessage> validate = validator.validate(getReader("Contraction.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_01d9abb9-b968-49c0-b6ab-909f3e03d8d3");
        assertThat(analysis.getGaps(), hasSize(0));
        assertThat(analysis.getOverlaps(), hasSize(0));

        // Contraction count.
        assertThat(analysis.getContractions(), hasSize(2));
        List<Contraction> results = Arrays.asList(new Contraction(4,
                                                                  Arrays.asList(5),
                                                                  2,
                                                                  Arrays.asList(new Interval(RangeBoundary.CLOSED, new BigDecimal("0.35"), Interval.POS_INF, RangeBoundary.CLOSED, 0, 0))),
                                                  new Contraction(3, 
                                                                  Arrays.asList(6),
                                                                  1,
                                                                  Arrays.asList(new Interval(RangeBoundary.CLOSED, new BigDecimal("600"), Interval.POS_INF, RangeBoundary.CLOSED, 0, 0))));
        assertThat(results, hasSize(2));
        assertThat(analysis.getContractions(), contains(results.toArray()));
        assertThat("It should contain 2 DMNMessage for the Contraction",
                   validate.stream().filter(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_CONTRACTION_RULE)).collect(Collectors.toList()),
                   hasSize(2));
    }
}
