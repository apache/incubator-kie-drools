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

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;
import org.drools.scenariosimulation.backend.util.JsonUtils;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MVEL_ESCAPE_SYMBOL;

/**
 * Factory to obtain specific expression evaluator based on context. It works like a delegate that hides part of context
 * so it will be possible to distribute the factory and then obtain the specific expression evaluator instance only when
 * all the information are available
 */
public class ExpressionEvaluatorFactory {

    private final ClassLoader classLoader;
    private final Type type;

    private BaseExpressionEvaluator baseExpressionEvaluator;
    private DMNFeelExpressionEvaluator dmnFeelExpressionEvaluator;
    private MVELExpressionEvaluator mvelExpressionEvaluator;

    public static ExpressionEvaluatorFactory create(ClassLoader classLoader, Type type) {
        return new ExpressionEvaluatorFactory(classLoader, type);
    }

    private ExpressionEvaluatorFactory(ClassLoader classLoader, Type type) {
        this.classLoader = classLoader;
        this.type = type;
    }

    /**
     * Based on factMappingValue information it returns an instance of the specific expression evaluator required.
     * @param factMappingValue
     * @return
     */
    public ExpressionEvaluator getOrCreate(FactMappingValue factMappingValue) {
        if (Type.DMN.equals(type)) {
            return getOrCreateDMNExpressionEvaluator();
        }

        String rawValue = (String) factMappingValue.getRawValue();

        if (isAnMVELExpression(rawValue)) {
            return getOrCreateMVELExpressionEvaluator();
        } else {
            return getOrCreateBaseExpressionEvaluator();
        }
    }

    /**
     * A rawValue is an MVEL expression if:
     * - NOT COLLECTIONS CASE: It's a <code>String</code> which starts with MVEL_ESCAPE_SYMBOL ('#')
     * - COLLECTION CASE: It's a JSON String node, which is used only when an expression is set
     *   (in other cases it's a JSON Object (Map) or a JSON Array (List)) and it's value starts with MVEL_ESCAPE_SYMBOL ('#')
     * @param rawValue
     * @return
     */
    protected boolean isAnMVELExpression(String rawValue) {
        /* NOT COLLECTIONS CASE */
        if (rawValue.trim().startsWith(MVEL_ESCAPE_SYMBOL)) {
            return true;
        }
        /* COLLECTION CASE */
        Optional<JsonNode> optionalNode = JsonUtils.convertFromStringToJSONNode(rawValue);
        return optionalNode.filter(
                jsonNode -> jsonNode.isTextual() && jsonNode.asText().trim().startsWith(MVEL_ESCAPE_SYMBOL)).isPresent();
    }

    private ExpressionEvaluator getOrCreateBaseExpressionEvaluator() {
        if (baseExpressionEvaluator == null) {
            baseExpressionEvaluator = new BaseExpressionEvaluator(classLoader);
        }
        return baseExpressionEvaluator;
    }

    private ExpressionEvaluator getOrCreateMVELExpressionEvaluator() {
        if (mvelExpressionEvaluator == null) {
            mvelExpressionEvaluator = new MVELExpressionEvaluator(classLoader);
        }
        return mvelExpressionEvaluator;
    }

    private ExpressionEvaluator getOrCreateDMNExpressionEvaluator() {
        if (dmnFeelExpressionEvaluator == null) {
            dmnFeelExpressionEvaluator = new DMNFeelExpressionEvaluator(classLoader);
        }
        return dmnFeelExpressionEvaluator;
    }
}
