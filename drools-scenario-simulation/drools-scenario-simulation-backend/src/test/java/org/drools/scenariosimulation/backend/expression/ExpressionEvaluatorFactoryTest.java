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

        assertThat(ruleEvaluatorFactory.getOrCreate(simpleFMV)).isInstanceOf(BaseExpressionEvaluator.class);
        assertThat(ruleEvaluatorFactory.getOrCreate(objectFMV)).isInstanceOf(BaseExpressionEvaluator.class);
        assertThat(ruleEvaluatorFactory.getOrCreate(mvelFMV)).isInstanceOf(MVELExpressionEvaluator.class);
        assertThat(ruleEvaluatorFactory.getOrCreate(mvelWithSpacesFMV)).isInstanceOf(MVELExpressionEvaluator.class);
        assertThat(ruleEvaluatorFactory.getOrCreate(mvelCollectionExpressionFMV)).isInstanceOf(MVELExpressionEvaluator.class);
        assertThat(ruleEvaluatorFactory.getOrCreate(mvelCollectionExpressionWithSpacesFMV)).isInstanceOf(MVELExpressionEvaluator.class);
        assertThat(ruleEvaluatorFactory.getOrCreate(mvelCollectionExpressionWitoutMVELEscapeSymbolFMV)).isInstanceOf(BaseExpressionEvaluator.class);

        assertThat(dmnEvaluatorFactory.getOrCreate(simpleFMV)).isInstanceOf(DMNFeelExpressionEvaluator.class);
        assertThat(dmnEvaluatorFactory.getOrCreate(objectFMV)).isInstanceOf(DMNFeelExpressionEvaluator.class);
        assertThat(dmnEvaluatorFactory.getOrCreate(mvelFMV)).isInstanceOf(DMNFeelExpressionEvaluator.class);
        assertThat(dmnEvaluatorFactory.getOrCreate(mvelWithSpacesFMV)).isInstanceOf(DMNFeelExpressionEvaluator.class);
        assertThat(dmnEvaluatorFactory.getOrCreate(mvelCollectionExpressionFMV)).isInstanceOf(DMNFeelExpressionEvaluator.class);
        assertThat(dmnEvaluatorFactory.getOrCreate(mvelCollectionExpressionWithSpacesFMV)).isInstanceOf(DMNFeelExpressionEvaluator.class);
        assertThat(dmnEvaluatorFactory.getOrCreate(mvelCollectionExpressionWitoutMVELEscapeSymbolFMV)).isInstanceOf(DMNFeelExpressionEvaluator.class);
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