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

import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;

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

    public ExpressionEvaluator getOrCreate(FactMappingValue factMappingValue) {
        if (Type.DMN.equals(type)) {
            return getOrCreateDMNExpressionEvaluator();
        }

        Object rawValue = factMappingValue.getRawValue();

        if (rawValue instanceof String && ((String) rawValue).startsWith("#")) {
            return getOrCreateMVELExpressionEvaluator();
        } else {
            return getOrCreateBaseExpressionEvaluator();
        }
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
