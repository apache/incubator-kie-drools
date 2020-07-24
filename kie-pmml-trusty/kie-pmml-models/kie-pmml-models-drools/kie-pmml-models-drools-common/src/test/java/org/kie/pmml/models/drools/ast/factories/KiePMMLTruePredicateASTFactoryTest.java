/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.models.drools.ast.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dmg.pmml.True;
import org.junit.Test;
import org.kie.pmml.commons.enums.ResultCode;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getPredicateASTFactoryData;

public class KiePMMLTruePredicateASTFactoryTest {

    @Test
    public void declareRuleFromTruePredicateNotFinalLeaf() {
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        True truePredicate = new True();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(truePredicate, Collections.emptyList(), rules, parentPath, currentRule, Collections.emptyMap());
        KiePMMLTruePredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromTruePredicateWithResult(DONE, false);
        assertEquals(1, rules.size());
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(currentRule, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertNull(retrieved.getAndConstraints());
        assertNull(retrieved.getResultCode());
    }

    @Test
    public void declareRuleFromTruePredicateFinalLeaf() {
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        String statusToSet = DONE;
        True truePredicate = new True();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(truePredicate, Collections.emptyList(), rules, parentPath, currentRule, Collections.emptyMap());
        KiePMMLTruePredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromTruePredicateWithResult(statusToSet, true);
        assertEquals(1, rules.size());
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(statusToSet, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertNull(retrieved.getAndConstraints());
        assertEquals(DONE, retrieved.getResult());
        assertEquals(ResultCode.OK, retrieved.getResultCode());
    }
}