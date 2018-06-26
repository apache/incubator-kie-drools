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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.ForExpressionNode;
import org.kie.dmn.feel.lang.ast.ForExpressionNode.ForIteration;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode.QEIteration;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode.Quantifier;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.ASTEventBase;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.Msg;

public class CompiledFEELSupport {

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
                    ctx.notifyEvt(() -> new ASTEventBase(Severity.ERROR, Msg.createMessage(Msg.INDEX_OUT_OF_BOUND), null));
                    return null;
                }
            } else {
                // TODO this differs behavior from evaluation mode because should actually throw an error as it's doing here.
                // TODO report error.
                return null;
            }
        }
    }

    public static PathBuilder path(EvaluationContext ctx, Object value) {
        return new PathBuilder(ctx, value);
    }

    public static class PathBuilder {

        private EvaluationContext ctx;
        private Object o;

        public PathBuilder(EvaluationContext evaluationContext, Object value) {
            this.ctx = evaluationContext;
            this.o = value;
        }

        public Object with(final String... names) {
            if (o instanceof List) {
                List list = (List) o;
                // list of contexts/elements as defined in the spec, page 114
                List results = new ArrayList();
                for (Object element : list) {
                    Object r = fetchValue(element, names);
                    if (r != null) {
                        results.add(r);
                    }
                }
                return results;
            } else {
                return fetchValue(o, names);
            }
        }

        private Object fetchValue(final Object o, final String... names) {
            Object result = o;
            for (String nr : names) {
                result = EvalHelper.getDefinedValue(result, nr)
                                   .getValueResult()
                                   .cata(err -> {
                                       //TODO report error in ctx.
                                       return null;
                                   }, Function.identity());
            }
            return result;
        }
    }

    public static ForBuilder ffor(EvaluationContext ctx) {
        return new ForBuilder(ctx);
    }

    public static class ForBuilder {

        private EvaluationContext ctx;
        private List<IterationContextCompiled> iterationContexts = new ArrayList<>();

        public ForBuilder(EvaluationContext evaluationContext) {
            this.ctx = evaluationContext;
        }

        public ForBuilder with(Function<EvaluationContext, Object> nameExpression, Function<EvaluationContext, Object> iterationExpression) {
            iterationContexts.add(new IterationContextCompiled(nameExpression, iterationExpression));
            return this;
        }

        public ForBuilder with(Function<EvaluationContext, Object> nameExpression,
                               Function<EvaluationContext, Object> iterationExpression,
                               Function<EvaluationContext, Object> rangeEndExpression) {
            iterationContexts.add(new IterationContextCompiled(nameExpression, iterationExpression, rangeEndExpression));
            return this;
        }

        public Object rreturn(Function<EvaluationContext, Object> expression) {
            try {
                ctx.enterFrame();
                List results = new ArrayList();
                ctx.setValue("partial", results);
                ForIteration[] ictx = initializeContexts(ctx, iterationContexts);

                while (ForExpressionNode.nextIteration(ctx, ictx)) {
                    Object result = expression.apply(ctx);
                    results.add(result);
                }
                return results;
            } catch (EndpointOfRangeNotOfNumberException e) {
                // ast error already reported
                return null;
            } finally {
                ctx.exitFrame();
            }
        }

        private ForIteration[] initializeContexts(EvaluationContext ctx, List<IterationContextCompiled> iterationContexts) {
            ForIteration[] ictx = new ForIteration[iterationContexts.size()];
            int i = 0;
            for (IterationContextCompiled icn : iterationContexts) {
                ictx[i] = createQuantifiedExpressionIterationContext(ctx, icn);
                if (i < iterationContexts.size() - 1 && ictx[i].hasNextValue()) {
                    ForExpressionNode.setValueIntoContext(ctx, ictx[i]);
                }
                i++;
            }
            return ictx;
        }

        private ForIteration createQuantifiedExpressionIterationContext(EvaluationContext ctx, IterationContextCompiled icn) {
            ForIteration fi = null;
            String name = (String) icn.getName().apply(ctx); // TODO revise
            Object result = icn.getExpression().apply(ctx);
            Object rangeEnd = icn.getRangeEndExpr().apply(ctx);
            if (rangeEnd == null) {
                Iterable values = result instanceof Iterable ? (Iterable) result : Collections.singletonList(result);
                fi = new ForIteration(name, values);
            } else {
                valueMustBeANumber(ctx, result);
                BigDecimal start = (BigDecimal) result;
                valueMustBeANumber(ctx, rangeEnd);
                BigDecimal end = (BigDecimal) rangeEnd;
                fi = new ForIteration(name, start, end);
            }
            return fi;
        }

        private void valueMustBeANumber(EvaluationContext ctx, Object value) {
            if (!(value instanceof BigDecimal)) {
                ctx.notifyEvt(() -> new ASTEventBase(Severity.ERROR, Msg.createMessage(Msg.VALUE_X_NOT_A_VALID_ENDPOINT_FOR_RANGE_BECAUSE_NOT_A_NUMBER, value), null));
                throw new EndpointOfRangeNotOfNumberException();
            }
        }

        private static class EndpointOfRangeNotOfNumberException extends RuntimeException {

            private static final long serialVersionUID = 1L;
        }
    }

    public static class IterationContextCompiled {

        private final Function<EvaluationContext, Object> name;
        private final Function<EvaluationContext, Object> expression;
        private final Function<EvaluationContext, Object> rangeEndExpr;

        public IterationContextCompiled(Function<EvaluationContext, Object> name, Function<EvaluationContext, Object> expression) {
            this.name = name;
            this.expression = expression;
            this.rangeEndExpr = (ctx) -> null;
        }

        public IterationContextCompiled(Function<EvaluationContext, Object> name, Function<EvaluationContext, Object> expression, Function<EvaluationContext, Object> rangeEndExpr) {
            this.name = name;
            this.expression = expression;
            this.rangeEndExpr = rangeEndExpr;
        }

        public Function<EvaluationContext, Object> getName() {
            return name;
        }

        public Function<EvaluationContext, Object> getExpression() {
            return expression;
        }

        public Function<EvaluationContext, Object> getRangeEndExpr() {
            return rangeEndExpr;
        }

    }

    public static QuantBuilder quant(Quantifier quantOp, EvaluationContext ctx) {
        return new QuantBuilder(quantOp, ctx);
    }

    public static class QuantBuilder {

        private Quantifier quantOp;
        private EvaluationContext ctx;
        private List<IterationContextCompiled> iterationContexts = new ArrayList<>();

        public QuantBuilder(Quantifier quantOp, EvaluationContext evaluationContext) {
            this.quantOp = quantOp;
            this.ctx = evaluationContext;
        }

        public QuantBuilder with(Function<EvaluationContext, Object> nameExpression, Function<EvaluationContext, Object> iterationExpression) {
            iterationContexts.add(new IterationContextCompiled(nameExpression, iterationExpression));
            return this;
        }

        public Object satisfies(Function<EvaluationContext, Object> expression) {
            if (quantOp == Quantifier.SOME || quantOp == Quantifier.EVERY) {
                return iterateContexts(ctx, iterationContexts, expression, quantOp);
            }
            // TODO can never happen?
            ctx.notifyEvt(() -> new ASTEventBase(Severity.ERROR, Msg.createMessage(Msg.IS_NULL, "Quantifier"), null));
            return null;
        }

        private Boolean iterateContexts(EvaluationContext ctx, List<IterationContextCompiled> iterationContexts, Function<EvaluationContext, Object> expression, Quantifier quantifier) {
            try {
                ctx.enterFrame();
                QEIteration[] ictx = initializeContexts(ctx, iterationContexts);

                while (QuantifiedExpressionNode.nextIteration(ctx, ictx)) {
                    Boolean result = (Boolean) expression.apply(ctx);
                    if (result != null && result.equals(quantifier.positiveTest())) {
                        return quantifier.positiveTest();
                    }
                }
                return quantifier.defaultValue();
            } finally {
                ctx.exitFrame();
            }
        }

        private QEIteration[] initializeContexts(EvaluationContext ctx, List<IterationContextCompiled> iterationContexts) {
            QEIteration[] ictx = new QEIteration[iterationContexts.size()];
            int i = 0;
            for (IterationContextCompiled icn : iterationContexts) {
                ictx[i] = createQuantifiedExpressionIterationContext(ctx, icn);
                if (i < ictx.length - 1) {
                    // initalize all contexts except the very last one, as it will be initialized in the nextIteration() method
                    QuantifiedExpressionNode.setValueIntoContext(ctx, ictx[i]);
                }
                i++;
            }
            return ictx;
        }

        private QEIteration createQuantifiedExpressionIterationContext(EvaluationContext ctx, IterationContextCompiled icn) {
            String name = (String) icn.getName().apply(ctx); // TODO revise
            Object result = icn.getExpression().apply(ctx);
            Iterable values = result instanceof Iterable ? (Iterable) result : Collections.singletonList(result);
            QEIteration qei = new QEIteration(name, values);
            return qei;
        }
    }

    public static Object invoke(EvaluationContext feelExprCtx, Object function, Object params) {
        if (function == null) {
            feelExprCtx.notifyEvt(() -> new ASTEventBase(Severity.ERROR, Msg.createMessage(Msg.FUNCTION_NOT_FOUND, function), null));
            return null;
        }
        if (function instanceof FEELFunction) {
            Object[] invocationParams = toFunctionParams(params);

            FEELFunction f = (FEELFunction) function;

            if (function instanceof CompiledCustomFEELFunction) {
                CompiledCustomFEELFunction ff = (CompiledCustomFEELFunction) function;
                if (ff.isProperClosure()) {
                    return ff.invokeReflectively(ff.getEvaluationContext(), invocationParams);
                }
            }

            return f.invokeReflectively(feelExprCtx, invocationParams);
        } else if (function instanceof UnaryTest) {
            throw new UnsupportedOperationException("TODO"); // TODO
        }
        return null;
    }

    private static Object[] toFunctionParams(Object params) {
        Object[] invocationParams = null;
        if (params instanceof List) {
            invocationParams = ((List) params).toArray(new Object[]{});
        } else if (params instanceof Object[]) {
            invocationParams = (Object[]) params;
        } else {
            invocationParams = new Object[]{params};
        }
        return invocationParams;
    }
}
