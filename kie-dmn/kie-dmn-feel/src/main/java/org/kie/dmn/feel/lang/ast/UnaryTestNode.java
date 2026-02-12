/*
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
package org.kie.dmn.feel.lang.ast;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.dialectHandlers.DefaultDialectHandler;
import org.kie.dmn.feel.lang.ast.dialectHandlers.DialectHandler;
import org.kie.dmn.feel.lang.ast.dialectHandlers.DialectHandlerFactory;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.UnaryTestImpl;
import org.kie.dmn.feel.util.Msg;

public class UnaryTestNode
        extends BaseNode {

    private UnaryOperator operator;
    private BaseNode value;

    public enum UnaryOperator {
        LTE("<="),
        LT("<"),
        GT(">"),
        GTE(">="),
        NE("!="),
        EQ("="),
        NOT("not"),
        IN("in"),
        TEST("test");

        public final String symbol;

        UnaryOperator(String symbol) {
            this.symbol = symbol;
        }

        public static UnaryOperator determineOperator(String symbol) {
            for (UnaryOperator op : UnaryOperator.values()) {
                if (op.symbol.equals(symbol)) {
                    return op;
                }
            }
            throw new IllegalArgumentException("No operator found for symbol '" + symbol + "'");
        }
    }

    public UnaryTestNode(String op, BaseNode value) {
        super();
        setText(op + " " + value.getText());
        this.operator = UnaryOperator.determineOperator(op);
        this.value = value;
    }

    public UnaryTestNode(UnaryOperator op, BaseNode value) {
        super();
        setText(op.symbol + " " + value.getText());
        this.operator = op;
        this.value = value;
    }

    public UnaryTestNode(ParserRuleContext ctx, String op, BaseNode value) {
        super(ctx);
        this.operator = UnaryOperator.determineOperator(op);
        this.value = value;
    }

    public UnaryTestNode(UnaryOperator op, BaseNode value, String text) {
        this.operator = op;
        this.value = value;
        this.setText(text);
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    public void setOperator(UnaryOperator operator) {
        this.operator = operator;
    }

    public BaseNode getValue() {
        return value;
    }

    public void setValue(BaseNode value) {
        this.value = value;
    }

    @Override
    public UnaryTest evaluate(EvaluationContext ctx) {
        UnaryTest toReturn = getUnaryTest();
        if (toReturn == null) {
            ctx.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.NULL_OR_UNKNOWN_OPERATOR)));
        }
        return toReturn;
    }

    public UnaryTest getUnaryTest() {
        return new UnaryTestImpl((context, left) -> {
            DialectHandler handler = DialectHandlerFactory.getHandler(context);

            switch (operator) {
                case EQ:
                    return createIsEqualUnaryTest().apply(context, left);
                case NE:
                    return createIsNotEqualUnaryTest().apply(context, left);
                case IN:
                    return createInUnaryTest().apply(context, left);
                case NOT:
                    return createNotUnaryTest().apply(context, left);
                case TEST:
                    return createBooleanUnaryTest().apply(context, left);
                    
                case LTE:
                case LT:
                case GT:
                case GTE:
                    // Comparison operators share the same right value evaluation
                    Object right = evaluateRightValue(context, left);
                    Object result = switch (operator) {
                        case LTE -> handler.executeLte(left, right, context);
                        case LT -> handler.executeLt(left, right, context);
                        case GT -> handler.executeGt(left, right, context);
                        case GTE -> handler.executeGte(left, right, context);
                        default -> throw new UnsupportedOperationException("Unsupported operator: " + operator);
                    };
                    return (result instanceof Boolean) ? (Boolean) result : Boolean.FALSE;

                default:
                    throw new UnsupportedOperationException("Unsupported operator: " + operator);
            }
        }, value.getText());
    }

    /**
     * For a Unary Test an = (equal) semantic depends on the RIGHT value.
     * If the RIGHT is NOT a list, then standard equals semantic applies
     * If the RIGHT is a LIST, then the semantic is "right contains left"
     * When both are Collections:
     * - Verify that the two objects have the same size
     * - Verify that the element at each position in the left object equals the element at the same position in the right object.
     */
    private Boolean utEqualSemantic(Object left, Object right) {
        if (left instanceof Collection && right instanceof Collection) {
            return areCollectionsEqual((Collection<?>) left, (Collection<?>) right);
        } else if (right instanceof Collection) {
            return isElementInCollection((Collection<?>) right, left);
        } else {
            return areElementsEqual(left, right);
        }
    }

    /**
     * Checks if two collections are equal by comparing elements in order.
     * Both collections must have the same size and each element at position i in left
     * must equal the element at position i in right.
     *
     * @param left the left collection
     * @param right the right collection
     * @return true if collections have same size and elements match in order, false otherwise
     */
    static Boolean areCollectionsEqual(Collection<?> left, Collection<?> right) {
        if (left.size() != right.size()) {
            return false;
        }

        Iterator<?> leftIterator = left.iterator();
        Iterator<?> rightIterator = right.iterator();
        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            if (!areElementsEqual(leftIterator.next(), rightIterator.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a collection contains a specific element.
     * Uses areElementsEqual() to ensure consistent equality semantics
     * with custom null handling via DefaultDialectHandler.isEqual().
     *
     * @param collection the collection to search in
     * @param element the element to search for
     * @return true if collection contains the element, false otherwise
     */
    static Boolean isElementInCollection(Collection<?> collection, Object element) {
        return collection.stream().anyMatch(item -> areElementsEqual(item, element));
    }

    /**
     * Checks if two elements are equal.
     *
     * @param left the left element
     * @param right the right element
     * @return true if elements are equal, false otherwise
     */
    static Boolean areElementsEqual(Object left, Object right) {
        return Boolean.TRUE.equals(
                DefaultDialectHandler.isEqual(left, right,
                        () -> (left == null && right == null),
                        () -> Boolean.FALSE)
        );
    }
    Object evaluateRightValue(EvaluationContext context, Object left) {
        Object right;
        // set the value if the expression contains ('?') question mark
        if (containsQuestionMarkReference(value)) {
            Object existing = context.getValue("?");
            if (Objects.equals(existing, left)) {
                right = value.evaluate(context);
            } else {
                context.enterFrame();
                try {
                    context.setValue("?", left);
                    right = value.evaluate(context);
                } finally {
                    context.exitFrame();
                }
            }
        } else {
            right = value.evaluate(context);
        }
        return right;
    }
    
    /**
     * Checks if the given node is a plain '?'
     */
    private boolean isPlainQuestionMark(BaseNode node) {
        return node instanceof NameRefNode && "?".equals(((NameRefNode) node).getText());
    }
    
    /**
     * Recursively checks if a BaseNode or its children contain a reference to '?'
     */
    private boolean containsQuestionMarkReference(BaseNode node) {
        if (isPlainQuestionMark(node)) {
            return true;
        }
        if (node.getChildrenNode() != null) {
            for (ASTNode child : node.getChildrenNode()) {
                if (child instanceof BaseNode && containsQuestionMarkReference((BaseNode) child)) {
                    return true;
                }
            }
        }
        return false;
    }

    private UnaryTest createIsEqualUnaryTest() {
        return (context, left) -> {
            Object right = evaluateRightValue(context, left);
            return utEqualSemantic(left, right);
        };
    }

    private UnaryTest createIsNotEqualUnaryTest() {
        return (context, left) -> {
            Object right = evaluateRightValue(context, left);
            Boolean result = utEqualSemantic(left, right);
            return result != null ? !result : null;
        };
    }

    private UnaryTest createInUnaryTest() {
        return (context, left) -> {
            if (left == null) {
                return false;
            }
            Object right = evaluateRightValue(context, left);
            if (right instanceof Range) {
                try {
                    return ((Range) right).includes(context, left);
                } catch (Exception e) {
                    context.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.EXPRESSION_IS_RANGE_BUT_VALUE_IS_NOT_COMPARABLE, left, right)));
                    throw e;
                }
            } else if (right instanceof Collection) {
                return ((Collection) right).contains(left);
            } else {
                return false; // make consistent with #createNotUnaryTest()
            }
        };
    }

    private UnaryTest createNotUnaryTest() {
        return (context, left) -> {
            Object right = evaluateRightValue(context, left);
            if (right == null) {
                return null;
            }
            List<Object> tests = (List<Object>) right;
            for (Object test : tests) {
                if (test == null) {
                    if (left == null) {
                        return false;
                    }
                } else if (test instanceof UnaryTest) {
                    if (((UnaryTest) test).apply(context, left)) {
                        return false;
                    }
                } else if (left == null) {
                    if (test == null) {
                        return false;
                    }
                } else if (test instanceof Range) {
                    try {
                        if (((Range) test).includes(context, left)) {
                            return false;
                        }
                    } catch (Exception e) {
                        context.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.EXPRESSION_IS_RANGE_BUT_VALUE_IS_NOT_COMPARABLE, left, test)));
                        throw e;
                    }
                } else if (test instanceof Collection) {
                    return !((Collection) test).contains(left);
                } else {
                    // test is a constant, so return false if it is equal to "left"
                    if (test.equals(left)) {
                        return false;
                    }
                }
            }
            return true;
        };
    }

    private UnaryTest createBooleanUnaryTest() {
        return (context, left) -> {
            Object right = evaluateRightValue(context, left);
            if (right instanceof Boolean) {
                return (Boolean) right;
            } else {
                context.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.EXTENDED_UNARY_TEST_MUST_BE_BOOLEAN, value.getText(), right)));
                return Boolean.FALSE;
            }
        };
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { value };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
