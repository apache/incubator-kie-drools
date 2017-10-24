/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.codegen.feel11;

import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.feel.lang.EvaluationContext;

public class CompiledFEELSupport {
    
    public static Object conditionWasNotBoolean(EvaluationContext ctx) {
        // TODO insert here some "exception" raise by notify ctx that the IF condition was not a boolean.
        return null;
    }

    public static ContextBuilder openContext(EvaluationContext ctx) {
        return new ContextBuilder(ctx);
    }

    public static class ContextBuilder {
        private Map<String, Object> resultContext = new HashMap<>();
        private EvaluationContext evaluationContext;

        public ContextBuilder(EvaluationContext evaluationContext) {
            this.evaluationContext = evaluationContext;
            evaluationContext.enterFrame();
        }

        public ContextBuilder setEntry(String key, Object value) {
            resultContext.put(key, value);
            evaluationContext.setValue(key, value);
            return this;
        }

        public Map<String, Object> closeContext() {
            evaluationContext.exitFrame();
            return resultContext;
        }
    }
}
