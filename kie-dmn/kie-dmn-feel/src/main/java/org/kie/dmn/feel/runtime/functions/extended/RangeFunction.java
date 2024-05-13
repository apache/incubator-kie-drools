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
package org.kie.dmn.feel.runtime.functions.extended;

import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.ast.*;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import java.time.*;
import java.time.chrono.ChronoPeriod;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class RangeFunction extends BaseFEELFunction {

    public static final RangeFunction INSTANCE = new RangeFunction();
    // Defaulting FEELDialect to FEEL
    private static final EvaluationContext STUBBED = new EvaluationContextImpl(Thread.currentThread().getContextClassLoader(), new FEELEventListenersManager(), 0, FEELDialect.FEEL);

    private static final List<Predicate<BaseNode>> ALLOWED_NODES = Arrays.asList(baseNode -> baseNode instanceof NullNode,
            baseNode -> baseNode instanceof NumberNode,
            baseNode -> baseNode instanceof StringNode,
            baseNode -> baseNode instanceof AtLiteralNode,
            baseNode -> baseNode instanceof FunctionInvocationNode);

    private static final List<Predicate<Object>> ALLOWED_TYPES = Arrays.asList(Objects::isNull,
            object -> object instanceof String,
            object -> object instanceof Number,
            object -> object instanceof Duration,
            object -> object instanceof ChronoPeriod,
            object -> object instanceof ZonedDateTime,
            object -> object instanceof OffsetDateTime,
            object -> object instanceof LocalDateTime,
            object -> object instanceof LocalDate,
            object -> object instanceof OffsetTime,
            object -> object instanceof LocalTime);


    public RangeFunction() {
        super("range");
    }

    public FEELFnResult<Range> invoke(@ParameterName("from") String from) {
        if (from == null || from.isEmpty() || from.isBlank()) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "cannot be null"));
        }
        Range.RangeBoundary startBoundary;
        if (from.startsWith("(") || from.startsWith("]")) {
            startBoundary = RangeBoundary.OPEN;
        } else if (from.startsWith("[")) {
            startBoundary = RangeBoundary.CLOSED;
        } else {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "does not start with a valid character"));
        }
        Range.RangeBoundary endBoundary;
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
        String rightString = split[1].substring(0, split[1].length() - 1);
        if ((leftString.isEmpty() || leftString.isBlank()) && (rightString.isEmpty() || rightString.isBlank())) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "at least one endpoint must not be null"));
        }
        BaseNode leftNode = parse(leftString);
        if (!nodeIsAllowed(leftNode)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "left endpoint is not a recognised valid literal"));
        }
        BaseNode rightNode = parse(rightString);
        if (!nodeIsAllowed(rightNode)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "right endpoint is not a recognised valid literal"));
        }
        Object left = leftNode.evaluate(STUBBED);
        if (!nodeValueIsAllowed(left)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "left endpoint is not a valid value " + left.getClass()));
        }

        Object right = rightNode.evaluate(STUBBED);
        if (!nodeValueIsAllowed(right)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "right endpoint is not a valid value " + right.getClass()));
        }

        if (!nodesReturnsSameType(left, right)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from", "endpoints must be of equivalent types"));
        }

        return FEELFnResult.ofResult(new RangeImpl(startBoundary, (Comparable) left, (Comparable) right, endBoundary));
    }

    protected boolean nodeIsAllowed(BaseNode node) {
        return ALLOWED_NODES.stream().anyMatch(baseNodePredicate -> baseNodePredicate.test(node));
    }

    protected boolean nodeValueIsAllowed(Object value) {
        return ALLOWED_TYPES.stream().anyMatch(objectPredicate -> objectPredicate.test(value));
    }

    /**
     * @param leftObject
     * @param leftObject
     * @return
     */
    protected boolean nodesReturnsSameType(Object leftObject, Object rightObject) {
        if (Objects.equals(leftObject, rightObject)) {
            return true;
        } else if (leftObject == null || rightObject == null) {
            return true;
        } else {
            Class<?> left = leftObject.getClass();
            Class<?> right = rightObject.getClass();
            return left.equals(right) || left.isAssignableFrom(right) || right.isAssignableFrom(left);
        }
    }

    protected BaseNode parse(String input) {
       return input.isEmpty() || input.isBlank() ? getNullNode() : parseNotEmptyInput(input);
    }

    protected BaseNode getNullNode() {
        return parseNotEmptyInput("null");
    }

    protected BaseNode parseNotEmptyInput(String input) {
        FEEL_1_1Parser parser = FEELParser.parse(null, input, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), null);
        ParseTree tree = parser.expression();
        ASTBuilderVisitor v = new ASTBuilderVisitor(Collections.emptyMap(), null);
        BaseNode expr = v.visit(tree);
        return expr;
    }

}
