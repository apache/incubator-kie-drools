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

package org.kie.pmml.models.tree.compiler.factories;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import org.dmg.pmml.True;
import org.junit.Test;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_PATTERN;

public class KiePMMLTreeModelTruePredicateASTFactoryTest {

    @Test
    public void declareRuleFromTruePredicateNotFinalLeaf() {
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        String statusToSet = StatusCode.DONE.getName();
        True truePredicate = new True();
        KiePMMLTreeModelTruePredicateASTFactory.factory(truePredicate, Collections.emptyList(), rules).declareRuleFromTruePredicate(parentPath, currentRule, statusToSet, false);
        assertEquals(1, rules.size());
        final KiePMMLDrooledRule retrieved = rules.poll();
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
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        String statusToSet = StatusCode.DONE.getName();
        True truePredicate = new True();
        KiePMMLTreeModelTruePredicateASTFactory.factory(truePredicate, Collections.emptyList(), rules).declareRuleFromTruePredicate(parentPath, currentRule, statusToSet, true);
        assertEquals(1, rules.size());
        final KiePMMLDrooledRule retrieved = rules.poll();
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(statusToSet, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertNull(retrieved.getAndConstraints());
        assertEquals(StatusCode.DONE.getName(), retrieved.getResult());
        assertEquals(StatusCode.OK, retrieved.getResultCode());
    }
}