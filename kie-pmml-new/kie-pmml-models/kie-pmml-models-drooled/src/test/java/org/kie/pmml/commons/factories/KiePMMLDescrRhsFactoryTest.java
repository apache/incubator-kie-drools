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

package org.kie.pmml.commons.factories;

import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.commons.factories.KiePMMLDescrRhsFactory;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.factories.KiePMMLDescrRhsFactory.ADD_PMML4_RESULT_VARIABLE;
import static org.kie.pmml.commons.factories.KiePMMLDescrRhsFactory.SET_PMML4_RESULT_CODE;
import static org.kie.pmml.commons.factories.KiePMMLDescrRhsFactory.UPDATE_STATUS_HOLDER;
import static org.kie.pmml.commons.factories.KiePMMLDescrRulesFactory.BREAK_LABEL;

public class KiePMMLDescrRhsFactoryTest {

    private static final String PACKAGE_NAME = "package";
    private static final String CURRENT_RULE = "currentRule";
    private RuleDescrBuilder ruleBuilder;

    @Before
    public void setUp() throws Exception {
        PackageDescrBuilder builder = DescrFactory.newPackage().name(PACKAGE_NAME);
        ruleBuilder = builder.newRule().name(CURRENT_RULE);
        assertEquals(CURRENT_RULE, ruleBuilder.getDescr().getName());
    }

    @Test
    public void declareRhsWithoutIfBreak() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        KiePMMLDrooledRule rule = KiePMMLDrooledRule.builder(name, statusToSet).build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareRhs(rule);
        assertNotNull(ruleBuilder.getDescr().getConsequence());
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER, statusToSet);
        assertEquals(expectedConsequence, ruleBuilder.getDescr().getConsequence());
        assertNotNull(ruleBuilder.getDescr().getNamedConsequences());
        assertTrue(ruleBuilder.getDescr().getNamedConsequences().isEmpty());
    }

    @Test
    public void declareRhsWithIfBreak() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        String ifBreakField = "ifBreakField";
        String ifBreakOperator = "ifBreakOperator";
        Object ifBreakValue = "ifBreakValue";
        KiePMMLDrooledRule rule = KiePMMLDrooledRule.builder(name, statusToSet)
                .withIfBreak(ifBreakField, ifBreakOperator, ifBreakValue)
                .build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareRhs(rule);
        assertNotNull(ruleBuilder.getDescr().getConsequence());
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER, statusToSet);
        assertEquals(expectedConsequence, ruleBuilder.getDescr().getConsequence());
        assertNotNull(ruleBuilder.getDescr().getNamedConsequences());
        assertEquals(1, ruleBuilder.getDescr().getNamedConsequences().size());
        assertNotNull(ruleBuilder.getDescr().getNamedConsequences().get(BREAK_LABEL));
    }

    @Test
    public void declareDefaultThenWithoutResult() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        KiePMMLDrooledRule rule = KiePMMLDrooledRule.builder(name, statusToSet).build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareDefaultThen(rule);
        assertNotNull(ruleBuilder.getDescr().getConsequence());
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER, statusToSet);
        assertEquals(expectedConsequence, ruleBuilder.getDescr().getConsequence());
        assertNotNull(ruleBuilder.getDescr().getNamedConsequences());
        assertTrue(ruleBuilder.getDescr().getNamedConsequences().isEmpty());
    }

    @Test
    public void declareDefaultThenWithResult() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        String result = "RESULT";
        StatusCode resultCode = StatusCode.OK;
        KiePMMLDrooledRule rule = KiePMMLDrooledRule.builder(name, statusToSet)
                .withResultCode(resultCode)
                .withResult(result)
                .build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareDefaultThen(rule);
        assertNotNull(ruleBuilder.getDescr().getConsequence());
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER, statusToSet);
        expectedConsequence += String.format(SET_PMML4_RESULT_CODE, resultCode);
        expectedConsequence += String.format(ADD_PMML4_RESULT_VARIABLE, result);
        assertEquals(expectedConsequence, ruleBuilder.getDescr().getConsequence());
        assertNotNull(ruleBuilder.getDescr().getNamedConsequences());
        assertTrue(ruleBuilder.getDescr().getNamedConsequences().isEmpty());
    }

    @Test
    public void declareIfThenWithoutResult() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        KiePMMLDrooledRule rule = KiePMMLDrooledRule.builder(name, statusToSet).build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareIfThen(rule);
        assertNotNull(ruleBuilder.getDescr().getConsequence());
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER, statusToSet);
        assertEquals(expectedConsequence, ruleBuilder.getDescr().getConsequence());
        assertNotNull(ruleBuilder.getDescr().getNamedConsequences());
        assertEquals(1, ruleBuilder.getDescr().getNamedConsequences().size());
        assertNotNull(ruleBuilder.getDescr().getNamedConsequences().get(BREAK_LABEL));
        expectedConsequence = String.format(UPDATE_STATUS_HOLDER, StatusCode.DONE.getName());
        assertEquals(expectedConsequence, ruleBuilder.getDescr().getNamedConsequences().get(BREAK_LABEL));
    }

    @Test
    public void declareIfThenWithResult() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        String result = "RESULT";
        StatusCode resultCode = StatusCode.OK;
        KiePMMLDrooledRule rule = KiePMMLDrooledRule.builder(name, statusToSet)
                .withResultCode(resultCode)
                .withResult(result)
                .build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareIfThen(rule);
        assertNotNull(ruleBuilder.getDescr().getConsequence());
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER, statusToSet);
        assertEquals(expectedConsequence, ruleBuilder.getDescr().getConsequence());
        assertNotNull(ruleBuilder.getDescr().getNamedConsequences());
        assertEquals(1, ruleBuilder.getDescr().getNamedConsequences().size());
        assertNotNull(ruleBuilder.getDescr().getNamedConsequences().get(BREAK_LABEL));
        expectedConsequence = String.format(UPDATE_STATUS_HOLDER, StatusCode.DONE.getName());
        expectedConsequence += String.format(SET_PMML4_RESULT_CODE, resultCode);
        expectedConsequence += String.format(ADD_PMML4_RESULT_VARIABLE, result);
        assertEquals(expectedConsequence, ruleBuilder.getDescr().getNamedConsequences().get(BREAK_LABEL));
    }
}