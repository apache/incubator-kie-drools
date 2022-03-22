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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.DateAndTimeFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.runtime.functions.YearsAndMonthsFunction;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.model.api.GwtIncompatible;

@GwtIncompatible
public class RangeFunction extends BaseFEELFunction {
    
    public static final RangeFunction INSTANCE = new RangeFunction();
    
    private static final EvaluationContext STUBBED = new StubbedEvaluationContext();

    public RangeFunction() {
        super( "range" );
    }

    public FEELFnResult<Range> invoke(@ParameterName("from") String from) {
        if (from == null) {
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
        Object left = leftNode.evaluate(STUBBED);
        Object right = rightNode.evaluate(STUBBED);
        
        return FEELFnResult.ofResult( new RangeImpl(startBoundary, (Comparable)left, (Comparable)right, endBoundary) );
    }
    
    private boolean nodeIsAllowed(BaseNode node) {
        if (node instanceof NullNode) {
            return true;
        } else if (node instanceof NumberNode) {
            return true;
        } else if (node instanceof StringNode) {
            return true;
        } else if (node instanceof BooleanNode) {
            return true;
        } else if (node instanceof AtLiteralNode) {
            return true;
        } else if (node instanceof FunctionInvocationNode) {
            return true;
        } else {
            return false;
        }
    }
    
    private BaseNode parse(String input) {
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
