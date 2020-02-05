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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

public class RecommenderHitPolicyTest extends AbstractDTAnalysisTest {

    @Test
    public void testGaps() {
        Definitions definitions = getDefinitions("RecommenderHitPolicy1.dmn", "http://www.trisotech.com/definitions/_50aea0bb-4482-48f6-acfe-4abc1a1bd0d6", "Drawing 1");
        List<DMNMessage> validate = validator.validate(definitions, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_3aa68aee-6314-482f-a0be-84c2411d65d7");

        debugValidatorMsg(validate);
        assertThat(analysis.getGaps(), hasSize(1));
        assertTrue(validate.stream().noneMatch(m -> m.getMessageType() == DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER));
    }

    @Test
    public void testNoGapsNoOverlaps() {
        List<HitPolicy> wrongHPs = Arrays.asList(HitPolicy.ANY, HitPolicy.PRIORITY, HitPolicy.FIRST);
        for (HitPolicy hp : wrongHPs) {
            List<DMNMessage> validate = getRecommenderHitPolicy2(hp);
            assertTrue(validate.stream().anyMatch(m -> m.getMessageType() == DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER));
        }

        List<DMNMessage> validate = getRecommenderHitPolicy2(HitPolicy.UNIQUE);
        assertTrue(validate.stream().noneMatch(m -> m.getMessageType() == DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER));
    }

    private List<DMNMessage> getRecommenderHitPolicy2(HitPolicy hp) {
        Definitions definitions = getDefinitions("RecommenderHitPolicy2.dmn", "http://www.trisotech.com/definitions/_50aea0bb-4482-48f6-acfe-4abc1a1bd0d6", "Drawing 1");
        // mutates XML file in the Hit Policy, accordingly to this test parameter. 
        ((DecisionTable) ((Decision) definitions.getDrgElement().get(1)).getExpression()).setHitPolicy(hp);
        List<DMNMessage> validate = validator.validate(definitions, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_3aa68aee-6314-482f-a0be-84c2411d65d7");

        debugValidatorMsg(validate);
        assertThat(analysis.getGaps(), hasSize(0));
        assertThat(analysis.getOverlaps(), hasSize(0));
        return validate;
    }

    @Test
    public void testNoGapsOverlapsSameValue() {
        List<HitPolicy> wrongHPs = Arrays.asList(HitPolicy.UNIQUE, HitPolicy.PRIORITY, HitPolicy.FIRST);
        for (HitPolicy hp : wrongHPs) {
            List<DMNMessage> validate = getRecommenderHitPolicy3(hp);
            assertTrue(validate.stream().anyMatch(m -> m.getMessageType() == DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER));
        }

        List<DMNMessage> validate = getRecommenderHitPolicy3(HitPolicy.ANY);
        assertTrue(validate.stream().noneMatch(m -> m.getMessageType() == DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER));
    }

    private List<DMNMessage> getRecommenderHitPolicy3(HitPolicy hp) {
        Definitions definitions = getDefinitions("RecommenderHitPolicy3.dmn", "http://www.trisotech.com/definitions/_50aea0bb-4482-48f6-acfe-4abc1a1bd0d6", "Drawing 1");
        // mutates XML file in the Hit Policy, accordingly to this test parameter. 
        ((DecisionTable) ((Decision) definitions.getDrgElement().get(1)).getExpression()).setHitPolicy(hp);
        List<DMNMessage> validate = validator.validate(definitions, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_3aa68aee-6314-482f-a0be-84c2411d65d7");

        debugValidatorMsg(validate);
        assertThat(analysis.getGaps(), hasSize(0));
        assertThat(analysis.getOverlaps(), hasSize(1));
        return validate;
    }

    @Test
    public void testNoGapsOverlapsDiffValue() {
        List<HitPolicy> wrongHPs = Arrays.asList(HitPolicy.UNIQUE, HitPolicy.ANY, HitPolicy.FIRST);
        for (HitPolicy hp : wrongHPs) {
            List<DMNMessage> validate = getRecommenderHitPolicy4(hp);
            assertTrue(validate.stream().anyMatch(m -> m.getMessageType() == DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER));
        }

        List<DMNMessage> validate = getRecommenderHitPolicy4(HitPolicy.PRIORITY);
        assertTrue(validate.stream().noneMatch(m -> m.getMessageType() == DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER));
    }

    private List<DMNMessage> getRecommenderHitPolicy4(HitPolicy hp) {
        Definitions definitions = getDefinitions("RecommenderHitPolicy4.dmn", "http://www.trisotech.com/definitions/_50aea0bb-4482-48f6-acfe-4abc1a1bd0d6", "Drawing 1");
        // mutates XML file in the Hit Policy, accordingly to this test parameter. 
        ((DecisionTable) ((Decision) definitions.getDrgElement().get(1)).getExpression()).setHitPolicy(hp);
        List<DMNMessage> validate = validator.validate(definitions, VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_3aa68aee-6314-482f-a0be-84c2411d65d7");

        debugValidatorMsg(validate);
        assertThat(analysis.getGaps(), hasSize(0));
        assertThat(analysis.getOverlaps(), hasSize(1));
        return validate;
    }
}
