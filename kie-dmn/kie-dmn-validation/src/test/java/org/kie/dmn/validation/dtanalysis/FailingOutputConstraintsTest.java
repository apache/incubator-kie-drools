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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

class FailingOutputConstraintsTest extends AbstractDTAnalysisTest {

    @Test
    void failingOutputConstraints() {
        List<DMNMessage> validate = validator.validate(getReader("FailingOutputConstraints.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR))).as("It should contain DMNMessage for output outside of LoV").isTrue();
        debugValidatorMsg(validate);

        DTAnalysis analysis = getAnalysis(validate, "_E72BD036-C550-4992-AA6D-A8AD4666C63A");
        assertThat(analysis.isError()).isFalse();
        assertThat(analysis.getGaps()).hasSize(1);
        assertThat(analysis.getOverlaps()).hasSize(0);
    }

    @Test
    void failingOutputConstraintsWhenOutputIsSymbol() {
        List<DMNMessage> validate = validator.validate(getReader("FailingOutputConstraints2.dmn"), ANALYZE_DECISION_TABLE);
        debugValidatorMsg(validate);
        assertThat(validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR))).as("It should NOT contain DMNMessage for output outside of LoV (using a symbol in output)").isTrue();

        DTAnalysis analysis = getAnalysis(validate, "_E72BD036-C550-4992-AA6D-A8AD4666C63A");
        assertThat(analysis.isError()).isFalse();
        assertThat(analysis.getGaps()).hasSize(1);
        assertThat(analysis.getOverlaps()).hasSize(0);
    }
}
