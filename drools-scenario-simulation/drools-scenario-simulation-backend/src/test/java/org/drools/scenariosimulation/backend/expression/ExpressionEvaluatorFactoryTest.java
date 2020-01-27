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

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MVEL_ESCAPE_SYMBOL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExpressionEvaluatorFactoryTest {

    ClassLoader classLoader = ExpressionEvaluatorFactoryTest.class.getClassLoader();

    @Test
    public void create() {
        assertNotNull(ExpressionEvaluatorFactory.create(classLoader, ScenarioSimulationModel.Type.RULE));
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

        assertTrue(ruleEvaluatorFactory.getOrCreate(simpleFMV) instanceof BaseExpressionEvaluator);
        assertTrue(ruleEvaluatorFactory.getOrCreate(objectFMV) instanceof BaseExpressionEvaluator);
        assertTrue(ruleEvaluatorFactory.getOrCreate(mvelFMV) instanceof MVELExpressionEvaluator);
        assertTrue(ruleEvaluatorFactory.getOrCreate(mvelWithSpacesFMV) instanceof MVELExpressionEvaluator);
        assertTrue(ruleEvaluatorFactory.getOrCreate(mvelCollectionExpressionFMV) instanceof MVELExpressionEvaluator);
        assertTrue(ruleEvaluatorFactory.getOrCreate(mvelCollectionExpressionWithSpacesFMV) instanceof MVELExpressionEvaluator);
        assertTrue(ruleEvaluatorFactory.getOrCreate(mvelCollectionExpressionWitoutMVELEscapeSymbolFMV) instanceof BaseExpressionEvaluator);

        assertTrue(dmnEvaluatorFactory.getOrCreate(simpleFMV) instanceof DMNFeelExpressionEvaluator);
        assertTrue(dmnEvaluatorFactory.getOrCreate(objectFMV) instanceof DMNFeelExpressionEvaluator);
        assertTrue(dmnEvaluatorFactory.getOrCreate(mvelFMV) instanceof DMNFeelExpressionEvaluator);
        assertTrue(dmnEvaluatorFactory.getOrCreate(mvelWithSpacesFMV) instanceof DMNFeelExpressionEvaluator);
        assertTrue(dmnEvaluatorFactory.getOrCreate(mvelCollectionExpressionFMV) instanceof DMNFeelExpressionEvaluator);
        assertTrue(dmnEvaluatorFactory.getOrCreate(mvelCollectionExpressionWithSpacesFMV) instanceof DMNFeelExpressionEvaluator);
        assertTrue(dmnEvaluatorFactory.getOrCreate(mvelCollectionExpressionWitoutMVELEscapeSymbolFMV) instanceof DMNFeelExpressionEvaluator);
    }

    @Test
    public void isAnMVELExpression() {
        ExpressionEvaluatorFactory ruleEvaluatorFactory = ExpressionEvaluatorFactory.create(classLoader, ScenarioSimulationModel.Type.RULE);
        assertFalse(ruleEvaluatorFactory.isAnMVELExpression("10"));
        assertTrue(ruleEvaluatorFactory.isAnMVELExpression(MVEL_ESCAPE_SYMBOL + "10"));
        assertTrue(ruleEvaluatorFactory.isAnMVELExpression("     " + MVEL_ESCAPE_SYMBOL + " 10"));
        assertTrue(ruleEvaluatorFactory.isAnMVELExpression(new TextNode(MVEL_ESCAPE_SYMBOL + " 10").textValue()));
        assertTrue(ruleEvaluatorFactory.isAnMVELExpression(new TextNode("     " + MVEL_ESCAPE_SYMBOL + " 10").textValue()));
        assertFalse(ruleEvaluatorFactory.isAnMVELExpression(new TextNode("10").textValue()));
    }
}