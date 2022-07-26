/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.drools.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.model.functions.Function1;
import org.drools.model.functions.Function3;

public interface PrototypeExpression {


    Function1<PrototypeFact, Object> asFunction(Prototype prototype);

    Collection<String> getImpactedFields();

    public static class ExpressionBuilder implements PrototypeExpression {

        private final PrototypeExpression expression;

        public ExpressionBuilder(PrototypeExpression expression) {
            this.expression = expression;
        }

        public static ExpressionBuilder fixedValue(Object value) {
            return new ExpressionBuilder(new FixedValue(value));
        }

        public static ExpressionBuilder prototypeField(String fieldName) {
            return new ExpressionBuilder(new PrototypeFieldValue(fieldName));
        }

        public ExpressionBuilder composeWith(BinaryOperation.Operator op, ExpressionBuilder right) {
            return new ExpressionBuilder(new BinaryOperation(expression, op, right.expression));
        }
        public ExpressionBuilder add(ExpressionBuilder right) {
            return composeWith(BinaryOperation.Operator.ADD, right);
        }
        public ExpressionBuilder sub(ExpressionBuilder right) {
            return composeWith(BinaryOperation.Operator.SUB, right);
        }
        public ExpressionBuilder mul(ExpressionBuilder right) {
            return composeWith(BinaryOperation.Operator.MUL, right);
        }
        public ExpressionBuilder div(ExpressionBuilder right) {
            return composeWith(BinaryOperation.Operator.DIV, right);
        }

        @Override
        public Function1<PrototypeFact, Object> asFunction(Prototype prototype) {
            return expression.asFunction(prototype);
        }

        @Override
        public Collection<String> getImpactedFields() {
            return expression.getImpactedFields();
        }
    }

    static class FixedValue implements PrototypeExpression {

        private final Object value;

        FixedValue(Object value) {
            this.value = value;
        }

        @Override
        public Function1<PrototypeFact, Object> asFunction(Prototype prototype) {
            return x -> value;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "FixedValue{" + value + '}';
        }

        @Override
        public Collection<String> getImpactedFields() {
            return Collections.emptyList();
        }
    }

    static class PrototypeFieldValue implements PrototypeExpression {

        private final String fieldName;

        PrototypeFieldValue(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public Function1<PrototypeFact, Object> asFunction(Prototype prototype) {
            return prototype.getFieldValueExtractor(fieldName)::apply;
        }

        public String getFieldName() {
            return fieldName;
        }

        @Override
        public String toString() {
            return "PrototypeFieldValue{" + fieldName + '}';
        }

        public Collection<String> getImpactedFields() {
            return Collections.singletonList(fieldName);
        }
    }

    static class BinaryOperation implements PrototypeExpression {

        enum Operator {
            ADD("+", "add", Operator::add),
            SUB("-", "sub", Operator::sub),
            MUL("*", "mul", Operator::mul),
            DIV("/", "add", Operator::div);

            private final String symbol;
            private final String keyword;
            private final Function3<Prototype, PrototypeExpression, PrototypeExpression, Function1<PrototypeFact, Object>> operator;

            Operator(String symbol, String keyword, Function3<Prototype, PrototypeExpression, PrototypeExpression, Function1<PrototypeFact, Object>> operator) {
                this.symbol = symbol;
                this.keyword = keyword;
                this.operator = operator;
            }

            private static Function1<PrototypeFact, Object> add(Prototype prototype, PrototypeExpression left, PrototypeExpression right) {
                return (PrototypeFact fact) -> {
                    Object leftValue = left.asFunction(prototype).apply(fact);
                    Object rightValue = right.asFunction(prototype).apply(fact);

                    if (leftValue instanceof String) {
                        return ((String) leftValue) + (rightValue != null ? rightValue : "");
                    }
                    if (leftValue instanceof Integer && rightValue instanceof Integer) {
                        return ((Integer) leftValue).intValue() + ((Integer) rightValue).intValue();
                    }
                    if (leftValue == null) {
                        return rightValue == null ? 0 : rightValue;
                    } else if (rightValue == null) {
                        return leftValue;
                    }
                    return ((Number) leftValue).doubleValue() + ((Number) rightValue).doubleValue();
                };
            }

            private static Function1<PrototypeFact, Object> sub(Prototype prototype, PrototypeExpression left, PrototypeExpression right) {
                return (PrototypeFact fact) -> {
                    Object leftValue = left.asFunction(prototype).apply(fact);
                    Object rightValue = right.asFunction(prototype).apply(fact);

                    if (leftValue instanceof Integer && rightValue instanceof Integer) {
                        return ((Integer) leftValue).intValue() - ((Integer) rightValue).intValue();
                    }
                    if (leftValue == null) {
                        return rightValue == null ? 0 : rightValue;
                    } else if (rightValue == null) {
                        return leftValue;
                    }
                    return ((Number) leftValue).doubleValue() - ((Number) rightValue).doubleValue();
                };
            }

            private static Function1<PrototypeFact, Object> mul(Prototype prototype, PrototypeExpression left, PrototypeExpression right) {
                return (PrototypeFact fact) -> {
                    Object leftValue = left.asFunction(prototype).apply(fact);
                    Object rightValue = right.asFunction(prototype).apply(fact);

                    if (leftValue instanceof Integer && rightValue instanceof Integer) {
                        return ((Integer) leftValue).intValue() * ((Integer) rightValue).intValue();
                    }
                    if (leftValue == null || rightValue == null) {
                        return 0;
                    }
                    return ((Number) leftValue).doubleValue() * ((Number) rightValue).doubleValue();
                };
            }

            private static Function1<PrototypeFact, Object> div(Prototype prototype, PrototypeExpression left, PrototypeExpression right) {
                return (PrototypeFact fact) -> {
                    Object leftValue = left.asFunction(prototype).apply(fact);
                    Object rightValue = right.asFunction(prototype).apply(fact);

                    if (leftValue instanceof Integer && rightValue instanceof Integer) {
                        return ((Integer) leftValue).intValue() / ((Integer) rightValue).intValue();
                    }
                    if (leftValue == null || rightValue == null) {
                        return 0;
                    }
                    return ((Number) leftValue).doubleValue() / ((Number) rightValue).doubleValue();
                };
            }

            @Override
            public String toString() {
                return symbol;
            }

            public String getSymbol() {
                return symbol;
            }

            public String getKeyword() {
                return keyword;
            }

            public static Operator decodeSymbol(String symbol) {
                return Arrays.stream(Operator.class.getEnumConstants())
                        .filter(op -> op.getSymbol().equals(symbol))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("Unrecognized symbol: " + symbol));
            }

            public static Operator decodeKeyword(String keyword) {
                return Arrays.stream(Operator.class.getEnumConstants())
                        .filter(op -> op.getKeyword().equals(keyword))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("Unrecognized keyword: " + keyword));
            }
        }

        private BinaryOperation(PrototypeExpression left, Operator op, PrototypeExpression right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        private final PrototypeExpression left;
        private final Operator op;
        private final PrototypeExpression right;

        @Override
        public Function1<PrototypeFact, Object> asFunction(Prototype prototype) {
            return op.operator.apply(prototype, left, right);
        }

        @Override
        public String toString() {
            return left + " " + op + " " + right;
        }

        public Collection<String> getImpactedFields() {
            Set<String> fields = new HashSet<>();
            fields.addAll(left.getImpactedFields());
            fields.addAll(right.getImpactedFields());
            return fields;
        }
    }
}
