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

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;

import static org.assertj.core.api.Assertions.assertThat;

class MultipleModelsTest extends AbstractDTAnalysisTest {

    @Test
    void multipleFromReaderInput() throws IOException {
        try (final Reader reader0 = getReader("MyDecisionTable.dmn");
                final Reader reader1 = getReader("tVowelDefinition.dmn");) {
            final List<DMNMessage> messages = validator.validateUsing(Validation.ANALYZE_DECISION_TABLE)
                                                       .theseModels(reader0, reader1);
            checkAnalysis(messages);
        }
    }

    @Test
    void multipleFromFileInput() {
        final List<DMNMessage> messages = validator.validateUsing(Validation.ANALYZE_DECISION_TABLE)
                                                   .theseModels(getFile("MyDecisionTable.dmn"),
                                                                getFile("tVowelDefinition.dmn"));
        checkAnalysis(messages);
    }

    @Test
    void multipleFromDefinitionsInput() throws IOException {
        final List<DMNMessage> messages = validator.validateUsing(Validation.ANALYZE_DECISION_TABLE)
                                                   .theseModels(getDefinitions("MyDecisionTable.dmn",
                                                                               "http://www.trisotech.com/definitions/_6b77f7ac-d61a-4fb0-9e24-7ebf04444f59",
                                                                               "MyDecisionTable"),
                                                                getDefinitions("tVowelDefinition.dmn",
                                                                               "http://www.trisotech.com/definitions/_d9beb8dd-2578-4f32-8231-cd27c199f098",
                                                                               "tVowelDefinition"));
        checkAnalysis(messages);
    }

    private void checkAnalysis(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_3cde04b9-d5c9-4254-9d27-436889111406");

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
}
