/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.backend.expression;

import com.fasterxml.jackson.databind.node.TextNode;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MVEL_ESCAPE_SYMBOL;

public class ExpressionEvaluatorFactoryTest {

    ClassLoader classLoader = ExpressionEvaluatorFactoryTest.class.getClassLoader();

    @Test
    public void create() {
        assertThat(ExpressionEvaluatorFactory.create(classLoader, ScenarioSimulationModel.Type.RULE)).isNotNull();
    }

    @Test
    public void getOrCreate() {
        FactMappingValue simpleFMV = new FactMappingValue(FactIdentifier.INDEX, ExpressionIdentifier.INDEX, "10");
        FactMappingValue objectFMV = new FactMappingValue(FactIdentifier.INDEX, ExpressionIdentifier.INDEX, "10");
        FactMappingValue mvelFMV = new FactMappingValue(FactIdentifier.INDEX, ExpressionIdentifier.INDEX, MVEL_ESCAPE_SYMBOL + " 10");
        FactMappingValue mvelWithSpacesFMV = new FactMappingValue(FactIdentifier.INDEX, ExpressionIdentifier.INDEX, "     " + MVEL_ESCAPE_SYMBOL + " 10");
        FactMappingValue mvelCollectionExpressionFMV = new FactMappingValue(FactIdentifier.INDEX, ExpressionIdentifier.INDEX, new TextNode(MVEL_ESCAPE_SYMBOL + " 10").textValue());
        FactMappingValue mvelCollectionExpressionWithSpacesFMV = new FactMappingValue(FactIdentifier.INDEX, ExpressionIdentifier.INDEX, new TextNode("     " + MVEL_ESCAPE_SYMBOL + " 10").textValue());
        FactMappingValue mvelCollectionExpressionWitoutMVELEscapeSymbolFMV = new FactMappingValue(FactIdentifier.INDEX, ExpressionIdentifier.INDEX, new TextNode(" 10").textValue());

        ExpressionEvaluatorFactory ruleEvaluatorFactory = ExpressionEvaluatorFactory.create(classLoader, ScenarioSimulationModel.Type.RULE);
        ExpressionEvaluatorFactory dmnEvaluatorFactory = ExpressionEvaluatorFactory.create(classLoader, ScenarioSimulationModel.Type.DMN);

        assertThat(ruleEvaluatorFactory.getOrCreate(simpleFMV) instanceof BaseExpressionEvaluator).isTrue();
        assertThat(ruleEvaluatorFactory.getOrCreate(objectFMV) instanceof BaseExpressionEvaluator).isTrue();
        assertThat(ruleEvaluatorFactory.getOrCreate(mvelFMV) instanceof MVELExpressionEvaluator).isTrue();
        assertThat(ruleEvaluatorFactory.getOrCreate(mvelWithSpacesFMV) instanceof MVELExpressionEvaluator).isTrue();
        assertThat(ruleEvaluatorFactory.getOrCreate(mvelCollectionExpressionFMV) instanceof MVELExpressionEvaluator).isTrue();
        assertThat(ruleEvaluatorFactory.getOrCreate(mvelCollectionExpressionWithSpacesFMV) instanceof MVELExpressionEvaluator).isTrue();
        assertThat(ruleEvaluatorFactory.getOrCreate(mvelCollectionExpressionWitoutMVELEscapeSymbolFMV) instanceof BaseExpressionEvaluator).isTrue();

        assertThat(dmnEvaluatorFactory.getOrCreate(simpleFMV) instanceof DMNFeelExpressionEvaluator).isTrue();
        assertThat(dmnEvaluatorFactory.getOrCreate(objectFMV) instanceof DMNFeelExpressionEvaluator).isTrue();
        assertThat(dmnEvaluatorFactory.getOrCreate(mvelFMV) instanceof DMNFeelExpressionEvaluator).isTrue();
        assertThat(dmnEvaluatorFactory.getOrCreate(mvelWithSpacesFMV) instanceof DMNFeelExpressionEvaluator).isTrue();
        assertThat(dmnEvaluatorFactory.getOrCreate(mvelCollectionExpressionFMV) instanceof DMNFeelExpressionEvaluator).isTrue();
        assertThat(dmnEvaluatorFactory.getOrCreate(mvelCollectionExpressionWithSpacesFMV) instanceof DMNFeelExpressionEvaluator).isTrue();
        assertThat(dmnEvaluatorFactory.getOrCreate(mvelCollectionExpressionWitoutMVELEscapeSymbolFMV) instanceof DMNFeelExpressionEvaluator).isTrue();
    }

    @Test
    public void isAnMVELExpression() {
        ExpressionEvaluatorFactory ruleEvaluatorFactory = ExpressionEvaluatorFactory.create(classLoader, ScenarioSimulationModel.Type.RULE);
        assertThat(ruleEvaluatorFactory.isAnMVELExpression("10")).isFalse();
        assertThat(ruleEvaluatorFactory.isAnMVELExpression(MVEL_ESCAPE_SYMBOL + "10")).isTrue();
        assertThat(ruleEvaluatorFactory.isAnMVELExpression("     " + MVEL_ESCAPE_SYMBOL + " 10")).isTrue();
        assertThat(ruleEvaluatorFactory.isAnMVELExpression(new TextNode(MVEL_ESCAPE_SYMBOL + " 10").textValue())).isTrue();
        assertThat(ruleEvaluatorFactory.isAnMVELExpression(new TextNode("     " + MVEL_ESCAPE_SYMBOL + " 10").textValue())).isTrue();
        assertThat(ruleEvaluatorFactory.isAnMVELExpression(new TextNode("10").textValue())).isFalse();
    }
}