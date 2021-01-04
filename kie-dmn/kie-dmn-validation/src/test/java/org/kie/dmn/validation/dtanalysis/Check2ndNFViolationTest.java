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

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.Contraction;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class Check2ndNFViolationTest extends AbstractDTAnalysisTest {

    @Test
    public void testCheck2ndNFViolation() {
        List<DMNMessage> validate = validator.validate(getReader("DT2ndNFViolation.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysis = getAnalysis(validate, "_4e358bae-7012-42dd-acea-e88b3aa3c8b2");
        assertThat(analysis.is2ndNFViolation(), is(true));
        assertThat(analysis.getContractionsViolating2ndNF(), hasSize(1));
        Contraction c2NFViolation = analysis.getContractionsViolating2ndNF().iterator().next();
        assertThat(c2NFViolation.rule, is(1));
        assertThat(c2NFViolation.pairedRules, contains(2));
        assertThat(c2NFViolation.adjacentDimension, is(3));
        assertTrue("It should contain at DMNMessage for the 2nd NF Violation",
                   validate.stream().anyMatch(p -> p.getSourceId().equals("_4e358bae-7012-42dd-acea-e88b3aa3c8b2") && p.getMessageType().equals(DMNMessageType.DECISION_TABLE_2NDNFVIOLATION)));

    }

    @Test
    public void testCheck2ndNFViolation3combo() {
        List<DMNMessage> validate = validator.validate(getReader("DT2ndNF3combo.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysis = getAnalysis(validate, "_BA703D04-803A-44AA-8A31-F5EEDD4FD54E");
        assertThat(analysis.is2ndNFViolation(), is(true));
        assertThat(analysis.getContractionsViolating2ndNF(), hasSize(2));
    }

    @Test
    public void testCheck2ndNFViolationWasADash() {
        List<DMNMessage> validate = validator.validate(getReader("DT2ndNFWasADash.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysis = getAnalysis(validate, "_C40525EF-9735-410B-A070-E0336E108268");
        assertThat(analysis.is2ndNFViolation(), is(true));
        assertThat(analysis.getCellsViolating2ndNF(), hasSize(1));
    }

    @Test
    public void testCheck2ndNFViolationWasADash2() {
        List<DMNMessage> validate = validator.validate(getReader("DT2ndNFWasADash2.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysis = getAnalysis(validate, "_D3F1D5B8-642B-446D-9099-DE4CB978CB94");
        assertThat(analysis.is2ndNFViolation(), is(true));
        assertThat(analysis.getCellsViolating2ndNF(), hasSize(1));
    }

}
