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
import org.kie.api.builder.Message.Level;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

class SomeProblemTest extends AbstractDTAnalysisTest {

    @Test
    void test() {
        List<DMNMessage> validate = validator.validate(getReader("SomeProblem.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysis1 = getAnalysis(validate, "_a36e37f8-aae0-4118-8267-cbb37c7955cb");
        assertThat(analysis1.isError()).isFalse();
        assertThat(analysis1.getGaps()).hasSize(0);
        assertThat(analysis1.getOverlaps()).hasSize(0);
        
        DTAnalysis analysis2 = getAnalysis(validate, "_2aea80b4-19fa-4831-8829-4db925a128aa");
        assertThat(analysis2.isError()).isTrue();
    }

    @Test
    void ltGtNumber() {
        List<DMNMessage> validate = validator.validate(getReader("problemLtGtNumber.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate).anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR) && p.getText().contains("Unrecognized unary test: '<> 47'; did you meant to write 'not(47)' instead?"));
        
        DTAnalysis analysis1 = getAnalysis(validate, "_207A079D-3C86-48D9-AE18-40D9485514F3");
        assertThat(analysis1.isError()).isTrue();
    }

    @Test
    void gtLtString() {
        List<DMNMessage> validate = validator.validate(getReader("problemGtLtString.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate).anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR) && p.getText().contains("Unrecognized unary test: '><\"a\"'; did you meant to write 'not(\"a\")' instead?"));
        
        DTAnalysis analysis1 = getAnalysis(validate, "_207A079D-3C86-48D9-AE18-40D9485514F3");
        assertThat(analysis1.isError()).isTrue();
    }

    @Test
    void diseqNumber() {
        List<DMNMessage> validate = validator.validate(getReader("problemDiseqNumber.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate).anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR) && p.getText().contains("Unrecognized unary test: '!= 47'; did you meant to write 'not(47)' instead?"));
        
        DTAnalysis analysis1 = getAnalysis(validate, "_B390752D-2181-40AF-A42C-737B3009DBAB");
        assertThat(analysis1.isError()).isTrue();
    }

    @Test
    void diseqString() {
        List<DMNMessage> validate = validator.validate(getReader("problemDiseqString.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate).anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR) && p.getText().contains("Unrecognized unary test: '!=\"a\"'; did you meant to write 'not(\"a\")' instead?"));
        
        DTAnalysis analysis1 = getAnalysis(validate, "_B390752D-2181-40AF-A42C-737B3009DBAB");
        assertThat(analysis1.isError()).isTrue();
    }

    @Test
    void validDiseqNumber1() {
        List<DMNMessage> validate = validator.validate(getReader("validDiseqNumber1.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate).anyMatch(p -> p.getLevel() == Level.WARNING && p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR) && p.getText().contains("Unmanaged unary test: '? != 47'; you could write 'not(47)' instead."));
        
        DTAnalysis analysis1 = getAnalysis(validate, "_C72B227B-AF0C-4BC4-9E3F-CD44F842C886");
        assertThat(analysis1.isError()).isTrue();
    }

    @Test
    void validDiseqNumber2() {
        List<DMNMessage> validate = validator.validate(getReader("validDiseqNumber2.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate).noneMatch(p -> p.getLevel()== Level.ERROR || p.getLevel() == Level.WARNING);
        
        DTAnalysis analysis1 = getAnalysis(validate, "_C72B227B-AF0C-4BC4-9E3F-CD44F842C886");
        assertThat(analysis1.isError()).isFalse();
        assertThat(analysis1.getGaps()).hasSize(0);
        assertThat(analysis1.getOverlaps()).hasSize(0);
    }
}
