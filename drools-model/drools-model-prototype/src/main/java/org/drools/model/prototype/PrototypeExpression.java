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
package org.drools.model.prototype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.kie.api.prototype.Prototype;
import org.kie.api.prototype.PrototypeFactInstance;

public interface PrototypeExpression {

    interface EvaluableExpression {
        Object evaluate(Map<PrototypeVariable, PrototypeFactInstance> factsMap);
    }

    Function1<PrototypeFactInstance, Object> asFunction(Prototype prototype);

    Collection<String> getImpactedFields();
    
    /**
     * if indexable, return a key for alpha/beta indexing
     */
    default Optional<String> getIndexingKey() {
        return Optional.empty();
    }

    static PrototypeExpression fixedValue(Object value) {
        return new FixedValue(value);
    }

    static PrototypeExpression thisPrototype() {
        return ThisPrototype.INSTANCE;
    }

    static PrototypeExpression prototypeField(String fieldName) {
        return new PrototypeFieldValue(fieldName);
    }

    static PrototypeExpression prototypeField(PrototypeVariable protoVar, String fieldName) {
        return new CompletePrototypeFieldValue(protoVar, fieldName);
    }

    static PrototypeExpression prototypeArrayItem(String fieldName, int pos) {
        return new PrototypeArrayItemValue(fieldName, pos);
    }

    default PrototypeExpression andThen(PrototypeExpression other) {
        return new PrototypeCompositeExpression(this, other);
    }

    default boolean hasPrototypeVariable() {
        return false;
    }

    default Collection<PrototypeVariable> getPrototypeVariables() {
        return Collections.emptyList();
    }

    default PrototypeExpression composeWith(BinaryOperation.Operator op, PrototypeExpression right) {
        return new BinaryOperation(this, op, right);
    }
    default PrototypeExpression add(PrototypeExpression right) {
        return composeWith(BinaryOperation.Operator.ADD, right);
    }
    default PrototypeExpression sub(PrototypeExpression right) {
        return composeWith(BinaryOperation.Operator.SUB, right);
    }
    default PrototypeExpression mul(PrototypeExpression right) {
        return composeWith(BinaryOperation.Operator.MUL, right);
    }
    default PrototypeExpression div(PrototypeExpression right) {
        return composeWith(BinaryOperation.Operator.DIV, right);
    }

    class FixedValue implements PrototypeExpression, EvaluableExpression {

        private final Object value;

        FixedValue(Object value) {
            this.value = value;
        }

        @Override
        public Function1<PrototypeFactInstance, Object> asFunction(Prototype prototype) {
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

        @Override
        public Object evaluate(Map<PrototypeVariable, PrototypeFactInstance> factsMap) {
            return value;
        }
    }

    enum ThisPrototype implements PrototypeExpression {

        INSTANCE;

        @Override
        public Function1<PrototypeFactInstance, Object> asFunction(Prototype prototype) {
            return p -> p;
        }

        @Override
        public String toString() {
            return "ThisPrototypeFieldValue";
        }

        @Override
        public Collection<String> getImpactedFields() {
            return Collections.emptyList();
        }
    }

    class PrototypeFieldValue implements PrototypeExpression {

        private final String fieldName;

        PrototypeFieldValue(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public Function1<PrototypeFactInstance, Object> asFunction(Prototype prototype) {
            return prototype.getFieldValueExtractor(fieldName)::apply;
        }

        @Override
        public Optional<String> getIndexingKey() {
            return Optional.of(fieldName);
        }

        @Override
        public String toString() {
            return "PrototypeFieldValue{" + fieldName + "}";
        }

        @Override
        public Collection<String> getImpactedFields() {
            return Collections.singletonList(fieldName);
        }
    }

    class CompletePrototypeFieldValue extends PrototypeFieldValue implements EvaluableExpression {
        private final PrototypeVariable protoVar;

        CompletePrototypeFieldValue(PrototypeVariable protoVar, String fieldName) {
            super(fieldName);
            this.protoVar = protoVar;
        }

        public PrototypeVariable getProtoVar() {
            return protoVar;
        }

        @Override
        public boolean hasPrototypeVariable() {
            return true;
        }

        @Override
        public Collection<PrototypeVariable> getPrototypeVariables() {
            return Collections.singletonList(protoVar);
        }

        @Override
        public Object evaluate(Map<PrototypeVariable, PrototypeFactInstance> factsMap) {
            return protoVar.getPrototype().getFieldValueExtractor(getIndexingKey().get()).apply(factsMap.get(protoVar));
        }

        @Override
        public String toString() {
            return "PrototypeFieldValue{" + getIndexingKey() + " on " + protoVar + "}";
        }
    }

    class PrototypeArrayItemValue implements PrototypeExpression {

        private final String fieldName;
        private final int pos;

        PrototypeArrayItemValue(String fieldName, int pos) {
            this.fieldName = fieldName;
            this.pos = pos;
        }

        @Override
        public Function1<PrototypeFactInstance, Object> asFunction(Prototype prototype) {
            return fact -> extractArrayItem( prototype.getFieldValueExtractor(fieldName).apply(fact) );
        }

        private Object extractArrayItem(Object value) {
            if ( value instanceof int[] ) {
                int[] array = (int[]) value;
                return array.length > pos ? array[pos] : Prototype.UNDEFINED_VALUE;
            }
            if ( value.getClass().isArray() ) {
                Object[] array = (Object[]) value;
                return array.length > pos ? array[pos] : Prototype.UNDEFINED_VALUE;
            }
            if ( value instanceof List ) {
                List<?> list = (List<?>) value;
                return list.size() > pos ? list.get(pos) : Prototype.UNDEFINED_VALUE;
            }
            return Prototype.UNDEFINED_VALUE;
        }

        public String getFieldName() {
            return fieldName;
        }

        @Override
        public String toString() {
            return "PrototypeArrayItemValue{" + fieldName + "[" + pos + "]}";
        }

        public Collection<String> getImpactedFields() {
            return Collections.singletonList(fieldName);
        }
    }

    class PrototypeCompositeExpression implements PrototypeExpression {
        private final PrototypeExpression first;
        private final PrototypeExpression second;

        public PrototypeCompositeExpression(PrototypeExpression first, PrototypeExpression second) {
            this.first = first;
            this.second = second;
        }
        public Function1<PrototypeFactInstance, Object> asFunction(Prototype prototype) {
            return first.asFunction(prototype).andThen(this::object2PrototypeFactInstance).andThen(second.asFunction(prototype));
        }

        private PrototypeFactInstance object2PrototypeFactInstance(Object object) {
            if (object == Prototype.UNDEFINED_VALUE) {
                return PrototypeDSL.DEFAULT_PROTOTYPE.newInstance();
            }
            if (object instanceof PrototypeFactInstance) {
                return (PrototypeFactInstance) object;
            }
            if (object instanceof Map) {
                PrototypeFactInstance fact = PrototypeDSL.DEFAULT_PROTOTYPE.newInstance();
                ((Map<String, Object>) object).forEach(fact::put);
                return fact;
            }
            throw new UnsupportedOperationException("Cannot convert " + object + " into a Prototype");
        }

        @Override
        public String toString() {
            return "PrototypeCompisiteExpression{" + first + " composed with " + second + "}";
        }

        public Collection<String> getImpactedFields() {
            return first.getImpactedFields();
        }
    }

    class BinaryOperation implements PrototypeExpression, EvaluableExpression {

        public enum Operator {
            ADD("+", "add", Operator::add),
            SUB("-", "sub", Operator::sub),
            MUL("*", "mul", Operator::mul),
            DIV("/", "add", Operator::div);

            private final String symbol;
            private final String keyword;
            private final Function2<Object, Object, Object> operator;

            Operator(String symbol, String keyword, Function2<Object, Object, Object> operator) {
                this.symbol = symbol;
                this.keyword = keyword;
                this.operator = operator;
            }

            private static Object add(Object leftValue, Object rightValue) {
                if (leftValue instanceof String) {
                    return ((String) leftValue) + (rightValue != null ? rightValue : "");
                }
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return ((Integer) leftValue).intValue() + ((Integer) rightValue).intValue();
                }
                if (leftValue == null || leftValue == Prototype.UNDEFINED_VALUE) {
                    return rightValue == null ? 0 : rightValue;
                } else if (rightValue == null || rightValue == Prototype.UNDEFINED_VALUE) {
                    return leftValue;
                }
                return ((Number) leftValue).doubleValue() + ((Number) rightValue).doubleValue();
            }

            private static Object sub(Object leftValue, Object rightValue) {
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return ((Integer) leftValue).intValue() - ((Integer) rightValue).intValue();
                }
                if (leftValue == null || leftValue == Prototype.UNDEFINED_VALUE) {
                    return rightValue == null ? 0 : rightValue;
                } else if (rightValue == null || rightValue == Prototype.UNDEFINED_VALUE) {
                    return leftValue;
                }
                return ((Number) leftValue).doubleValue() - ((Number) rightValue).doubleValue();
            }

            private static Object mul(Object leftValue, Object rightValue) {
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return ((Integer) leftValue).intValue() * ((Integer) rightValue).intValue();
                }
                if (leftValue == null || leftValue == Prototype.UNDEFINED_VALUE || rightValue == null || rightValue == Prototype.UNDEFINED_VALUE) {
                    return 0;
                }
                return ((Number) leftValue).doubleValue() * ((Number) rightValue).doubleValue();
            }

            private static Object div(Object leftValue, Object rightValue) {
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return ((Integer) leftValue).intValue() / ((Integer) rightValue).intValue();
                }
                if (leftValue == null || leftValue == Prototype.UNDEFINED_VALUE || rightValue == null || rightValue == Prototype.UNDEFINED_VALUE) {
                    return 0;
                }
                return ((Number) leftValue).doubleValue() / ((Number) rightValue).doubleValue();
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
        public Function1<PrototypeFactInstance, Object> asFunction(Prototype prototype) {
            return (PrototypeFactInstance fact) -> {
                Object leftValue = left.asFunction(prototype).apply(fact);
                Object rightValue = right.asFunction(prototype).apply(fact);
                return op.operator.apply(leftValue, rightValue);
            };
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

        @Override
        public boolean hasPrototypeVariable() {
            return left.hasPrototypeVariable() || right.hasPrototypeVariable();
        }

        @Override
        public Collection<PrototypeVariable> getPrototypeVariables() {
            Set<PrototypeVariable> protoVars = new HashSet<>();
            protoVars.addAll(left.getPrototypeVariables());
            protoVars.addAll(right.getPrototypeVariables());
            return protoVars;
        }

        @Override
        public Object evaluate(Map<PrototypeVariable, PrototypeFactInstance> factsMap) {
            Object leftValue = ((EvaluableExpression) left).evaluate(factsMap);
            Object rightValue = ((EvaluableExpression) right).evaluate(factsMap);
            return op.operator.apply(leftValue, rightValue);
        }
    }
}
