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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    public static FilterBuilder filter(EvaluationContext ctx, Object value) {
        return new FilterBuilder(ctx, value);
    }

    public static class FilterBuilder {

        private EvaluationContext ctx;
        private Object value;

        public FilterBuilder(EvaluationContext evaluationContext, Object value) {
            this.ctx = evaluationContext;
            this.value = value;
        }

        public Object with(Function<EvaluationContext, Object> filterExpression) {
            if (value == null) {
                return null;
            }
            List list = value instanceof List ? (List) value : Arrays.asList(value);

            List results = new ArrayList();
            for (Object v : list) {
                try {
                    ctx.enterFrame();
                    // handle it as a predicate
                    // Have the "item" variable set first, so to respect the DMN spec: The expression in square brackets can reference a list
                    // element using the name item, unless the list element is a context that contains the key "item".
                    ctx.setValue("item", v);

                    // using Root object logic to avoid having to eagerly inspect all attributes.
                    ctx.setRootObject(v);

                    Object r = filterExpression.apply(ctx);
                    if (r instanceof Boolean && ((Boolean) r) == Boolean.TRUE) {
                        results.add(v);
                    }
                } catch (Exception e) {
                    // TODO report error.
                } finally {
                    ctx.exitFrame();
                }                
            }

            return results;
        }

        public Object with(Object filterIndex) {
            if (value == null) {
                return null;
            }
            List list = value instanceof List ? (List) value : Arrays.asList(value);

            if (filterIndex instanceof Number) {
                int i = ((Number) filterIndex).intValue();
                if (i > 0 && i <= list.size()) {
                    return list.get(i - 1);
                } else if (i < 0 && Math.abs(i) <= list.size()) {
                    return list.get(list.size() + i);
                } else {
                    // TODO report error.
                    return null;
                }
            } else {
                // TODO this differs behavior from evaluation mode because should actually throw an error as it's doing here.
                // TODO report error.
                return null;
            }
        }
    }
}
