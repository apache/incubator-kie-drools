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
package org.kie.pmml.models.drools.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import org.dmg.pmml.OutputField;
import org.dmg.pmml.ResultFeature;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.RuleDescrBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrRhsFactory.ADD_PMML4_OUTPUT_FIELD;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrRhsFactory.ADD_PMML4_RESULT_VARIABLE;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrRhsFactory.FOCUS_AGENDA_GROUP;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrRhsFactory.SET_PMML4_RESULT_CODE;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrRhsFactory.UPDATE_STATUS_HOLDER_STATUS;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrRulesFactory.BREAK_LABEL;

public class KiePMMLDescrRhsFactoryTest {

    private static final String CURRENT_RULE = "currentRule";
    private RuleDescrBuilder ruleBuilder;

    @BeforeEach
    public void setUp() throws Exception {
        PackageDescrBuilder builder = DescrFactory.newPackage().name(PACKAGE_NAME);
        ruleBuilder = builder.newRule().name(CURRENT_RULE);
        assertThat(ruleBuilder.getDescr().getName()).isEqualTo(CURRENT_RULE);
    }

    @Test
    void declareRhsWithoutIfBreak() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        KiePMMLDroolsRule rule = KiePMMLDroolsRule.builder(name, statusToSet, Collections.emptyList()).build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareRhs(rule);
        assertThat(ruleBuilder.getDescr().getConsequence()).isNotNull();
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER_STATUS, statusToSet);
        assertThat(ruleBuilder.getDescr().getConsequence().toString()).contains(expectedConsequence);
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).isNotNull();
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).isEmpty();
    }

    @Test
    void declareRhsWithIfBreak() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        String ifBreakField = "ifBreakField";
        String ifBreakOperator = "ifBreakOperator";
        Object ifBreakValue = "ifBreakValue";
        KiePMMLDroolsRule rule = KiePMMLDroolsRule.builder(name, statusToSet, Collections.emptyList())
                .withIfBreak(ifBreakField, ifBreakOperator, ifBreakValue)
                .build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareRhs(rule);
        assertThat(ruleBuilder.getDescr().getConsequence()).isNotNull();
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER_STATUS, statusToSet);
        assertThat(ruleBuilder.getDescr().getConsequence().toString()).contains(expectedConsequence);
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).isNotNull();
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).hasSize(1);
        assertThat(ruleBuilder.getDescr().getNamedConsequences().get(BREAK_LABEL)).isNotNull();
    }

    @Test
    void declareDefaultThenWithoutResult() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        KiePMMLDroolsRule rule = KiePMMLDroolsRule.builder(name, statusToSet, Collections.emptyList()).build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareDefaultThen(rule);
        assertThat(ruleBuilder.getDescr().getConsequence()).isNotNull();
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER_STATUS, statusToSet);
        assertThat(ruleBuilder.getDescr().getConsequence().toString()).contains(expectedConsequence);
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).isNotNull();
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).isEmpty();
    }

    @Test
    void declareDefaultThenWithResult() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        String result = "RESULT";
        ResultCode resultCode = ResultCode.OK;
        KiePMMLDroolsRule rule = KiePMMLDroolsRule.builder(name, statusToSet, Collections.emptyList())
                .withResultCode(resultCode)
                .withResult(result)
                .build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareDefaultThen(rule);
        assertThat(ruleBuilder.getDescr().getConsequence()).isNotNull();
        String retrievedConsequence = ruleBuilder.getDescr().getConsequence().toString();
        assertThat(retrievedConsequence).contains(String.format(UPDATE_STATUS_HOLDER_STATUS, statusToSet));
        assertThat(retrievedConsequence).contains(String.format(SET_PMML4_RESULT_CODE, resultCode));
        assertThat(retrievedConsequence).contains(String.format(ADD_PMML4_RESULT_VARIABLE, result));
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).isNotNull();
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).isEmpty();
    }

    @Test
    void declareIfThenWithoutResult() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        KiePMMLDroolsRule rule = KiePMMLDroolsRule.builder(name, statusToSet, Collections.emptyList()).build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareIfThen(rule);
        assertThat(ruleBuilder.getDescr().getConsequence()).isNotNull();
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER_STATUS, statusToSet);
        assertThat(ruleBuilder.getDescr().getConsequence()).isEqualTo(expectedConsequence);
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).isNotNull();
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).hasSize(1);
        assertThat(ruleBuilder.getDescr().getNamedConsequences().get(BREAK_LABEL)).isNotNull();
        expectedConsequence = String.format(UPDATE_STATUS_HOLDER_STATUS, DONE);
        assertThat(ruleBuilder.getDescr().getNamedConsequences().get(BREAK_LABEL).toString()).as(expectedConsequence).contains(expectedConsequence);
    }

    @Test
    void declareIfThenWithResult() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        String result = "RESULT";
        ResultCode resultCode = ResultCode.OK;
        KiePMMLDroolsRule rule = KiePMMLDroolsRule.builder(name, statusToSet, Collections.emptyList())
                .withResultCode(resultCode)
                .withResult(result)
                .build();
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareIfThen(rule);
        assertThat(ruleBuilder.getDescr().getConsequence()).isNotNull();
        String expectedConsequence = String.format(UPDATE_STATUS_HOLDER_STATUS, statusToSet);
        assertThat(ruleBuilder.getDescr().getConsequence()).isEqualTo(expectedConsequence);
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).isNotNull();
        assertThat(ruleBuilder.getDescr().getNamedConsequences()).hasSize(1);
        assertThat(ruleBuilder.getDescr().getNamedConsequences().get(BREAK_LABEL)).isNotNull();
        String retrievedConsequence = ruleBuilder.getDescr().getNamedConsequences().get(BREAK_LABEL).toString();
        assertThat(retrievedConsequence).contains(String.format(UPDATE_STATUS_HOLDER_STATUS, DONE));
        assertThat(retrievedConsequence).contains(String.format(SET_PMML4_RESULT_CODE, resultCode));
        assertThat(retrievedConsequence).contains(String.format(ADD_PMML4_RESULT_VARIABLE, result));
    }

    @Test
    void commonDeclareThen() {
        String ruleName = "RULENAME";
        String statusToSet = "STATUSTOSET";
        String outputFieldName = "OUTPUTFIELDNAME";
        Object result = "RESULT";
        OutputField outputField = new OutputField();
        outputField.setName(outputFieldName);
        outputField.setResultFeature(ResultFeature.PREDICTED_VALUE);
        List<OutputField> outputFields = Collections.singletonList(outputField);
        KiePMMLDroolsRule.Builder builder = KiePMMLDroolsRule.builder(ruleName, statusToSet, outputFields);
        KiePMMLDroolsRule rule = builder.build();
        StringJoiner joiner = new StringJoiner("");
        KiePMMLDescrRhsFactory.factory(ruleBuilder).commonDeclareThen(rule, joiner);
        String retrieved = joiner.toString();
        assertThat(retrieved).isEmpty();
        //
        ResultCode resultCode = ResultCode.OK;
        builder = builder.withResultCode(resultCode);
        rule = builder.build();
        joiner = new StringJoiner("");
        KiePMMLDescrRhsFactory.factory(ruleBuilder).commonDeclareThen(rule, joiner);
        retrieved = joiner.toString();
        String expected = String.format(SET_PMML4_RESULT_CODE, resultCode);
        assertThat(retrieved).contains(expected);
        //
        builder = builder.withResult(result);
        rule = builder.build();
        joiner = new StringJoiner("");
        KiePMMLDescrRhsFactory.factory(ruleBuilder).commonDeclareThen(rule, joiner);
        retrieved = joiner.toString();
        expected = String.format(ADD_PMML4_RESULT_VARIABLE, result);
        assertThat(retrieved).contains(expected);
        expected = String.format(ADD_PMML4_OUTPUT_FIELD, outputFieldName, result);
        assertThat(retrieved).contains(expected);
        //
        String focusedAgendaGroup = "FOCUSEDAGENDAGROUP";
        builder = builder.withFocusedAgendaGroup(focusedAgendaGroup);
        rule = builder.build();
        joiner = new StringJoiner("");
        KiePMMLDescrRhsFactory.factory(ruleBuilder).commonDeclareThen(rule, joiner);
        retrieved = joiner.toString();
        expected = String.format(FOCUS_AGENDA_GROUP, focusedAgendaGroup);
        assertThat(retrieved).contains(expected);
        //
    }

    @Test
    void commonDeclareOutputFields() {
        String outputFieldName = "OUTPUTFIELDNAME";
        Object result = "RESULT";
        OutputField outputField = new OutputField();
        outputField.setName(outputFieldName);
        outputField.setResultFeature(ResultFeature.PREDICTED_VALUE);
        List<OutputField> outputFields = Collections.singletonList(outputField);
        StringJoiner joiner = new StringJoiner("");
        KiePMMLDescrRhsFactory.factory(ruleBuilder).commonDeclareOutputFields(outputFields, result, joiner);
        String retrieved = joiner.toString();
        String expected = String.format(ADD_PMML4_OUTPUT_FIELD, outputFieldName, result);
        assertThat(retrieved).isEqualTo(expected);
    }
}