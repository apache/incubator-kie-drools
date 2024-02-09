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
package org.kie.pmml.models.drools.ast.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dmg.pmml.True;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getPredicateASTFactoryData;

public class KiePMMLTruePredicateASTFactoryTest {

    @Test
    void declareRuleFromTruePredicateNotFinalLeaf() {
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        True truePredicate = new True();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(truePredicate, Collections.emptyList(), rules, parentPath, currentRule, Collections.emptyMap());
        KiePMMLTruePredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromTruePredicateWithResult(DONE, false);
        assertThat(rules).hasSize(1);
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo(currentRule);
        assertThat(retrieved.getStatusToSet()).isEqualTo(currentRule);
        assertThat(retrieved.getStatusConstraint()).isEqualTo(String.format(STATUS_PATTERN, parentPath));
        assertThat(retrieved.getAndConstraints()).isNull();
        assertThat(retrieved.getResultCode()).isNull();
    }

    @Test
    void declareRuleFromTruePredicateFinalLeaf() {
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        String statusToSet = DONE;
        True truePredicate = new True();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(truePredicate, Collections.emptyList(), rules, parentPath, currentRule, Collections.emptyMap());
        KiePMMLTruePredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromTruePredicateWithResult(statusToSet, true);
        assertThat(rules).hasSize(1);
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo(currentRule);
        assertThat(retrieved.getStatusToSet()).isEqualTo(statusToSet);
        assertThat(retrieved.getStatusConstraint()).isEqualTo(String.format(STATUS_PATTERN, parentPath));
        assertThat(retrieved.getAndConstraints()).isNull();
        assertThat(retrieved.getResult()).isEqualTo(DONE);
        assertThat(retrieved.getResultCode()).isEqualTo(ResultCode.OK);
    }
}