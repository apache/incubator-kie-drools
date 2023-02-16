/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime.functions.extended;

import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.*;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.*;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.model.api.GwtIncompatible;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

@GwtIncompatible
public class RangeFunction extends BaseFEELFunction {
    
    public static final RangeFunction INSTANCE = new RangeFunction();
    
    private static final EvaluationContext STUBBED = new StubbedEvaluationContext();

    private static final List<Predicate<BaseNode>> ALLOWED_NODES = Arrays.asList(baseNode -> baseNode instanceof NullNode,
            baseNode -> baseNode instanceof NumberNode,
            baseNode -> baseNode instanceof StringNode,
            baseNode -> baseNode instanceof BooleanNode,
            baseNode -> baseNode instanceof AtLiteralNode,
            baseNode -> baseNode instanceof FunctionInvocationNode);


    public RangeFunction() {
        super( "range" );
    }

    public FEELFnResult<Range> invoke(@ParameterName("from") String from) {
        if (from == null || from.isEmpty() || from.isBlank()) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "cannot be null"));
        }
        Range.RangeBoundary startBoundary = null;
        if (from.startsWith("(") || from.startsWith("]")) {
            startBoundary = RangeBoundary.OPEN;
        } else if (from.startsWith("[")) {
            startBoundary = RangeBoundary.CLOSED;
        } else {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "does not start with a valid character"));
        }
        Range.RangeBoundary endBoundary = null;
        if (from.endsWith(")") || from.endsWith("[")) {
            endBoundary = RangeBoundary.OPEN;
        } else if (from.endsWith("]")) {
            endBoundary = RangeBoundary.CLOSED;
        } else {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "does not end with a valid character"));
        }
        
        String[] split = from.split("\\.\\.");
        if (split.length != 2) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "does not include two literals separated by `..` two dots characters"));
        }
        String leftString = split[0].substring(1);
        String rightString = split[1].substring(0, split[1].length()-1);
        BaseNode leftNode = parse(leftString);
        if (!nodeIsAllowed(leftNode)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "left endpoint is not a recognised valid literal"));
        }
        BaseNode rightNode = parse(rightString);
        if (!nodeIsAllowed(rightNode)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "right endpoint is not a recognised valid literal"));
        }

        if (!nodesAreSameType(leftNode, rightNode)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "endpoints must be of equivalent types"));
        }

        Object left = leftNode.evaluate(STUBBED);
        Object right = rightNode.evaluate(STUBBED);
        
        return FEELFnResult.ofResult( new RangeImpl(startBoundary, (Comparable)left, (Comparable)right, endBoundary) );
    }
    
    protected boolean nodeIsAllowed(BaseNode node) {
        return ALLOWED_NODES.stream().anyMatch(baseNodePredicate -> baseNodePredicate.test(node));
    }

    protected boolean nodesAreSameType(BaseNode leftNode, BaseNode rightNode) {
        if (leftNode instanceof NullNode || rightNode instanceof NullNode) {
            return true;
        } else if (leftNode instanceof FunctionInvocationNode && rightNode instanceof FunctionInvocationNode) {
            return nodesAreSameFunction((FunctionInvocationNode) leftNode, (FunctionInvocationNode) rightNode);
        } else {
            return ALLOWED_NODES.stream().anyMatch(baseNodePredicate -> baseNodePredicate.test(leftNode) && baseNodePredicate.test(rightNode));
        }

    }

    // https://www.omg.org/spec/DMN/1.4/Beta1/PDF: Two function types (T1, ..., Tn) →U and (S1, ..., Sm) →V are equivalent iff n = m, Ti ≡ Sj for i = 1, n and U ≡ V
    protected boolean nodesAreSameFunction(FunctionInvocationNode leftNode, FunctionInvocationNode rightNode) {
        Class<?> left = FEEL.newInstance().evaluate(leftNode.getText()).getClass();
        Class<?> right = FEEL.newInstance().evaluate(rightNode.getText()).getClass();
        return left.equals(right) || left.isAssignableFrom(right) || right.isAssignableFrom(left);
    }
    
    protected BaseNode parse(String input) {
        FEEL_1_1Parser parser = FEELParser.parse(null, input, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), null);
        ParseTree tree = parser.expression();
        ASTBuilderVisitor v = new ASTBuilderVisitor(Collections.emptyMap(), null);
        BaseNode expr = v.visit( tree );
        return expr;
    }
    
    private static class StubbedEvaluationContext implements EvaluationContext {

        private final Map<String, Object> functions;

        private StubbedEvaluationContext() {
            Map<String, Object> builtIn = new ConcurrentHashMap<>(  );
            addToBuildin(builtIn, DateFunction.INSTANCE);
            addToBuildin(builtIn, TimeFunction.INSTANCE);
            addToBuildin(builtIn, DateAndTimeFunction.INSTANCE);
            addToBuildin(builtIn, DurationFunction.INSTANCE);
            addToBuildin(builtIn, YearsAndMonthsFunction.INSTANCE);
            functions = Collections.unmodifiableMap( builtIn );
        }

        private void addToBuildin(Map<String, Object> builtIn, FEELFunction f) {
            builtIn.put( EvalHelper.normalizeVariableName( f.getName() ), f );
        }

        public Object getValue(String symbol) {
            symbol = EvalHelper.normalizeVariableName( symbol );
            if ( functions.containsKey( symbol ) ) {
                return functions.get( symbol );
            }
            return null;
        }
        
        @Override
        public void enterFrame() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void exitFrame() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EvaluationContext current() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setValue(String name, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getValue(String[] name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDefined(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDefined(String[] name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<String, Object> getAllValues() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DMNRuntime getDMNRuntime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ClassLoader getRootClassLoader() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void notifyEvt(Supplier<FEELEvent> event) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<FEELEventListener> getListeners() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setRootObject(Object v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getRootObject() {
            throw new UnsupportedOperationException();
        }
        
    }
}
