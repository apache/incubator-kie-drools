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
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

class SymbolInDTTest extends AbstractDTAnalysisTest {

    @Test
    void symbolMyThreshold() {
        List<DMNMessage> validate = validator.validate(getReader("SymbolInDT.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate.stream().anyMatch(messageForSymbol("my threshold"))).as("It should contain DMNMessage for symbol not supported in input").isTrue();

        DTAnalysis analysis1 = getAnalysis(validate, "_50D70081-079A-40DA-BA9C-B1173F0D2831");
        assertThat(analysis1.isError()).isTrue();
    }

    private Predicate<? super DMNMessage> messageForSymbol(String symbolName) {
        return p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR) && p.getText().contains("symbol reference: '"+symbolName+"'.");
    }

    @Test
    void symbolLastDateOfWork() {
        List<DMNMessage> validate = validator.validate(getReader("SymbolInDT2.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate.stream().anyMatch(messageForSymbol("Last Date of Work"))).as("It should contain DMNMessage for symbol not supported in input").isTrue();

        DTAnalysis analysis1 = getAnalysis(validate, "_50D70081-079A-40DA-BA9C-B1173F0D2831");
        assertThat(analysis1.isError()).isTrue();
    }
}
