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
package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Arrays;

import org.kie.dmn.feel.lang.EvaluationContext;

// A class used to evaluate a property name against a FEEL evaluation context
public class PropertyEvaluator {

    private final EvaluationContext evaluationContext;
    private final Object[] values;

    public PropertyEvaluator(EvaluationContext evaluationContext, String... propertyNames) {
        this.evaluationContext = evaluationContext;
        this.values = new Object[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            values[i] = evaluationContext.getValue(propertyNames[i]);
        }
    }

    public Object getValue(int i) {
        return values[i];
    }

    public EvaluationContext getEvaluationContext() {
        return evaluationContext;
    }

    @Override
    public String toString() {
        return "PropertyEvaluator{" +
                "values=" + Arrays.toString(values) +
                '}';
    }
}
