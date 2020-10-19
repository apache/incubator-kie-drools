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

package org.kie.pmml.models.drools.commons.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dmg.pmml.SimplePredicate;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.OUTPUTFIELDS_MAP;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.OUTPUTFIELDS_MAP_IDENTIFIER;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.PMML4_RESULT;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.PMML4_RESULT_IDENTIFIER;

public class KiePMMLDescrFactoryTest {

    private static final String PACKAGE_NAME = "package";
    private static final String RULE_NAME = "NAME";
    private static final String STATUS_TO_SET = "STATUS_TO_SET";
    private static final String PATTERN_TYPE = "TEMPERATURE";

    @Test
    public void getBaseDescr() {
        List<KiePMMLDroolsType> types = new ArrayList<>();
        types.add(KiePMMLDescrTestUtils.getDroolsType());
        types.add(KiePMMLDescrTestUtils.getDottedDroolsType());
        List<KiePMMLFieldOperatorValue> orConstraints = Arrays.asList(new KiePMMLFieldOperatorValue(PATTERN_TYPE, BOOLEAN_OPERATOR.OR, Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 35)), null),
                                                                      new KiePMMLFieldOperatorValue(PATTERN_TYPE, BOOLEAN_OPERATOR.AND, Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 85)), null));
        KiePMMLDroolsRule rule = KiePMMLDroolsRule.builder(RULE_NAME, STATUS_TO_SET, Collections.emptyList())
                .withOrConstraints(orConstraints)
                .build();
        List<KiePMMLDroolsRule> rules = new ArrayList<>();
        rules.add(rule);
        KiePMMLDroolsAST drooledAST = new KiePMMLDroolsAST(types, rules);
        PackageDescr packageDescr = KiePMMLDescrFactory.getBaseDescr(drooledAST, PACKAGE_NAME);
        assertEquals(PACKAGE_NAME, packageDescr.getName());
        checkImports(packageDescr.getImports());
        checkGlobals(packageDescr.getGlobals());
        checkRules(packageDescr.getRules());
    }

    private void checkImports(List<ImportDescr> toCheck) {
        assertEquals(4, toCheck.size());
        List<String> expectedImports = Arrays.asList(KiePMMLStatusHolder.class.getName(),
                                                     SimplePredicate.class.getName(),
                                                     PMML4Result.class.getName());
        for (String expectedImport : expectedImports) {
            assertNotNull(toCheck.stream().filter(importDescr -> expectedImport.equals(importDescr.getTarget())).findFirst().orElse(null));
        }
    }

    private void checkGlobals(List<GlobalDescr> toCheck) {
        assertEquals(2, toCheck.size());
        GlobalDescr retrieved = toCheck.get(0);
        assertEquals(PMML4_RESULT_IDENTIFIER, retrieved.getIdentifier());
        assertEquals(PMML4_RESULT, retrieved.getType());
        retrieved = toCheck.get(1);
        assertEquals(OUTPUTFIELDS_MAP_IDENTIFIER, retrieved.getIdentifier());
        assertEquals(OUTPUTFIELDS_MAP, retrieved.getType());
    }

    private void checkRules(List<RuleDescr> toCheck) {
        assertEquals(1, toCheck.size());
        RuleDescr retrieved = toCheck.get(0);
        assertEquals(RULE_NAME, retrieved.getName());
    }
}