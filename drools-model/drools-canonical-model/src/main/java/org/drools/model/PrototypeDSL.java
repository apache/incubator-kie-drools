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
package org.drools.model;

import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.temporal.TemporalPredicate;
import org.drools.model.impl.PrototypeImpl;
import org.drools.model.impl.PrototypeVariableImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.UUID.randomUUID;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.reactOn;
import static org.drools.model.PrototypeExpression.prototypeArrayItem;
import static org.drools.model.PrototypeExpression.prototypeField;

public class PrototypeDSL {

    public static final Prototype DEFAULT_PROTOTYPE = prototype("$DEFAULT_PROTOTYPE$");

    public static Prototype prototype(String name) {
        return new PrototypeImpl(name);
    }

    public static Prototype prototype(String name, String... fields) {
        return new PrototypeImpl(name, fields);
    }

    public static Prototype prototype(String name, Prototype.Field... fields) {
        return new PrototypeImpl(name, fields);
    }

    public static Prototype.Field field(String name) {
        return new PrototypeImpl.FieldImpl(name);
    }

    public static Prototype.Field field(String name, Function<PrototypeFact, Object> extractor) {
        return new PrototypeImpl.FieldImpl(name, extractor);
    }

    public static Prototype.Field field(String name, Class<?> type) {
        return new PrototypeImpl.FieldImpl(name, type);
    }

    public static Prototype.Field field(String name, Class<?> type, Function<PrototypeFact, Object> extractor) {
        return new PrototypeImpl.FieldImpl(name, type, extractor);
    }

    public static PrototypeVariable variable(Prototype prototype) {
        return new PrototypeVariableImpl(prototype);
    }

    public static PrototypeVariable variable(Prototype prototype, String name) {
        return new PrototypeVariableImpl(prototype, name);
    }

    public static PrototypePatternDef protoPattern(PrototypeVariable protoVar) {
        return new PrototypePatternDefImpl(protoVar);
    }

    public interface PrototypePatternDef extends PatternDSL.PatternDef<PrototypeFact> {
        PrototypePatternDef expr(String fieldName, ConstraintOperator operator, Object value);
        PrototypePatternDef expr(PrototypeExpression left, ConstraintOperator operator, PrototypeExpression right);

        PrototypePatternDef expr(String fieldName, ConstraintOperator operator, PrototypeVariable other, String otherFieldName);
        PrototypePatternDef expr(PrototypeExpression left, ConstraintOperator operator, PrototypeVariable other, PrototypeExpression right);

        PrototypePatternDef expr(TemporalPredicate temporalPredicate, PrototypeVariable other);

        PrototypePatternDef and();

        PrototypePatternDef or();
    }

    public static class PrototypePatternDefImpl extends PatternDSL.PatternDefImpl<PrototypeFact> implements PrototypePatternDef {
        public PrototypePatternDefImpl(PrototypeVariable variable) {
            super(variable);
        }

        public PrototypeVariable getPrototypeVariable() {
            return (PrototypeVariable) getFirstVariable();
        }

        public Prototype getPrototype() {
            return getPrototypeVariable().getPrototype();
        }

        @Override
        public PrototypePatternDef and() {
            return new PrototypeSubPatternDefImpl( this, PatternDSL.LogicalCombiner.AND );
        }

        @Override
        public PrototypePatternDef or() {
            return new PrototypeSubPatternDefImpl( this, PatternDSL.LogicalCombiner.OR );
        }

        @Override
        public PrototypePatternDef expr(String fieldName, ConstraintOperator operator, Object value) {
            return expr(fieldName2PrototypeExpression(fieldName), operator, PrototypeExpression.fixedValue(value));
        }

        @Override
        public PrototypePatternDef expr(PrototypeExpression left, ConstraintOperator operator, PrototypeExpression right) {
            if (right.hasPrototypeVariable()) {
                return exprWithProtoDef(left, operator, right);
            }

            Prototype prototype = getPrototype();
            Function1<PrototypeFact, Object> leftExtractor = left.asFunction(prototype);

            Set<String> reactOnFields = new HashSet<>();
            reactOnFields.addAll(left.getImpactedFields());
            reactOnFields.addAll(right.getImpactedFields());

            expr(createExprId(left, operator, right),
                    asPredicate1(leftExtractor, operator, right.asFunction(prototype)),
                    createAlphaIndex(left, operator, right, prototype, leftExtractor),
                    reactOn( reactOnFields.toArray(new String[reactOnFields.size()])) );

            return this;
        }

        private static AlphaIndex createAlphaIndex(PrototypeExpression left, ConstraintOperator operator, PrototypeExpression right, Prototype prototype, Function1<PrototypeFact, Object> leftExtractor) {
            if (left.getIndexingKey().isPresent() && right instanceof PrototypeExpression.FixedValue && operator instanceof Index.ConstraintType) {
                String fieldName = left.getIndexingKey().get();
                Index.ConstraintType constraintType = (Index.ConstraintType) operator;
                Prototype.Field field = prototype.getField(fieldName);
                Object value = ((PrototypeExpression.FixedValue) right).getValue();

                Class<Object> fieldClass = (Class<Object>) (field != null && field.isTyped() ? field.getType() : value != null ? value.getClass() : null);
                if (fieldClass != null) {
                    return alphaIndexedBy(fieldClass, constraintType, getFieldIndex(prototype, fieldName, field), leftExtractor, value);
                }
            }
            return null;
        }

        private static int getFieldIndex(Prototype prototype, String fieldName, Prototype.Field field) {
            return field != null ? prototype.getFieldIndex(fieldName) : Math.abs(fieldName.hashCode());
        }

        @Override
        public PrototypePatternDef expr(String fieldName, ConstraintOperator operator, PrototypeVariable other, String otherFieldName) {
            return expr(fieldName2PrototypeExpression(fieldName), operator, other, fieldName2PrototypeExpression(otherFieldName));
        }

        @Override
        public PrototypePatternDef expr(PrototypeExpression left, ConstraintOperator operator, PrototypeVariable other, PrototypeExpression right) {
            Prototype prototype = getPrototype();
            Prototype otherPrototype = other.getPrototype();

            Set<String> reactOnFields = new HashSet<>();
            reactOnFields.addAll(left.getImpactedFields());
            reactOnFields.addAll(right.getImpactedFields());

            expr(createExprId(left, operator, right),
                    other, asPredicate2(left.asFunction(prototype), operator, right.asFunction(otherPrototype)),
                    createBetaIndex(left, operator, right, prototype, otherPrototype),
                    reactOn( reactOnFields.toArray(new String[reactOnFields.size()])) );

            return this;
        }

        private static String createExprId(PrototypeExpression left, ConstraintOperator operator, PrototypeExpression right) {
            Object leftId = left.getIndexingKey().orElse(left.toString());
            Object rightId = right instanceof PrototypeExpression.FixedValue ? ((PrototypeExpression.FixedValue) right).getValue() : right;
            return "expr:" + leftId + ":" + operator + ":" + rightId;
        }

        private BetaIndex createBetaIndex(PrototypeExpression left, ConstraintOperator operator, PrototypeExpression right, Prototype prototype, Prototype otherPrototype) {
            if (left.getIndexingKey().isPresent() && operator instanceof Index.ConstraintType && right.getIndexingKey().isPresent()) {
                String fieldName = left.getIndexingKey().get();
                Index.ConstraintType constraintType = (Index.ConstraintType) operator;
                Prototype.Field field = prototype.getField(fieldName);
                Function1<PrototypeFact, Object> extractor = left.asFunction(prototype);
                Function1<PrototypeFact, Object> otherExtractor = right.asFunction(otherPrototype);

                Class<Object> fieldClass = (Class<Object>) (field != null && field.isTyped() ? field.getType() : Object.class);
                return betaIndexedBy( fieldClass, constraintType, getFieldIndex(prototype, fieldName, field), extractor, otherExtractor );
            }
            return null;
        }

        @Override
        public PrototypePatternDef expr(TemporalPredicate temporalPredicate, PrototypeVariable other) {
            expr( randomUUID().toString(), other, temporalPredicate );
            getPrototype().setAsEvent(true);
            other.getPrototype().setAsEvent(true);
            return this;
        }

        private Predicate1<PrototypeFact> asPredicate1(Function1<PrototypeFact, Object> left, ConstraintOperator operator, Function1<PrototypeFact, Object> right) {
            return p -> {
                Object leftValue = left.apply(p);
                Object rightValue = right.apply(p);
                return evaluateConstraint(leftValue, operator, rightValue);
            };
        }

        private Predicate2<PrototypeFact, PrototypeFact> asPredicate2(Function1<PrototypeFact, Object> extractor, ConstraintOperator operator, Function1<PrototypeFact, Object> otherExtractor) {
            return (p1, p2) -> {
                Object leftValue = extractor.apply(p1);
                Object rightValue = otherExtractor.apply(p2);
                return evaluateConstraint(leftValue, operator, rightValue);
            };
        }

        private PrototypePatternDef exprWithProtoDef(PrototypeExpression left, ConstraintOperator operator, PrototypeExpression right) {
            PrototypeVariable leftVar = getPrototypeVariable();
            PrototypeVariable[] protoVars = findRightPrototypeVariables(right, leftVar);

            switch (protoVars.length) {
                case 2:
                    expr( "expr:" + left + ":" + operator + ":" + right,
                            protoVars[0], protoVars[1],
                            asPredicate3(leftVar, left, operator, (PrototypeExpression.EvaluableExpression) right, protoVars)
                        );
                    break;
                default:
                    throw new UnsupportedOperationException();
            }

            return this;
        }

        private Predicate3<PrototypeFact, PrototypeFact, PrototypeFact> asPredicate3(PrototypeVariable leftVar, PrototypeExpression left, ConstraintOperator operator, PrototypeExpression.EvaluableExpression right, PrototypeVariable[] protoVars) {
            return (l, r1, r2) -> {
                Map<PrototypeVariable, PrototypeFact> factsMap = new HashMap<>();
                factsMap.put(leftVar, l);
                factsMap.put(protoVars[0], r1);
                factsMap.put(protoVars[1], r2);

                Object leftValue = left.asFunction(getPrototype()).apply(l);
                Object rightValue = right.evaluate(factsMap);
                return evaluateConstraint(leftValue, operator, rightValue);
            };
        }

        private PrototypeVariable[] findRightPrototypeVariables(PrototypeExpression right, PrototypeVariable leftVar) {
            Collection<PrototypeVariable> rightVars = right.getPrototypeVariables();
            boolean rightVarsContainLeft = rightVars.contains(leftVar);
            PrototypeVariable[] protoVars = new PrototypeVariable[rightVarsContainLeft ? rightVars.size()-1 : rightVars.size()];
            int i = 0;
            for (PrototypeVariable rightVar : rightVars) {
                if (!rightVarsContainLeft || rightVar != leftVar) {
                    protoVars[i++] = rightVar;
                }
            }
            return protoVars;
        }

        private static boolean evaluateConstraint(Object leftValue, ConstraintOperator operator, Object rightValue) {
            return leftValue != Prototype.UNDEFINED_VALUE && rightValue != Prototype.UNDEFINED_VALUE && operator.asPredicate().test(leftValue, rightValue);
        }
    }

    public static class PrototypeSubPatternDefImpl<T> extends PrototypePatternDefImpl {
        private final PrototypePatternDefImpl parent;
        private final PatternDSL.LogicalCombiner combiner;

        public PrototypeSubPatternDefImpl(PrototypePatternDefImpl parent, PatternDSL.LogicalCombiner combiner ) {
            super((PrototypeVariable) parent.variable);
            this.parent = parent;
            this.combiner = combiner;
        }

        @Override
        public PrototypePatternDefImpl endAnd() {
            if (combiner == PatternDSL.LogicalCombiner.OR) {
                throw new UnsupportedOperationException();
            }
            parent.items.add( new PatternDSL.CombinedPatternExprItem<>( combiner, this.getItems() ));
            return parent;
        }

        @Override
        public PrototypePatternDefImpl endOr() {
            if (combiner == PatternDSL.LogicalCombiner.AND) {
                throw new UnsupportedOperationException();
            }
            parent.items.add( new PatternDSL.CombinedPatternExprItem<>( combiner, this.getItems() ));
            return parent;
        }
    }

    public static PrototypeExpression fieldName2PrototypeExpression(String fieldName) {
        int arrayStart = fieldName.indexOf('[');
        if (arrayStart >= 0) {
            int arrayEnd = fieldName.indexOf(']');
            int pos = Integer.parseInt(fieldName.substring(arrayStart+1, arrayEnd));
            PrototypeExpression arrayExpr = prototypeArrayItem(fieldName.substring(0, arrayStart), pos);
            if (arrayEnd+1 < fieldName.length()) {
                if (fieldName.charAt(arrayEnd+1) != '.') {
                    throw new UnsupportedOperationException("Invalid expression: " + fieldName);
                }
                arrayExpr = arrayExpr.andThen(fieldName2PrototypeExpression(fieldName.substring(arrayEnd+2)));
            }
            return arrayExpr;
        }
        return prototypeField(fieldName);
    }
}