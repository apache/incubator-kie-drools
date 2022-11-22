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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.temporal.TemporalPredicate;
import org.drools.model.impl.PrototypeImpl;
import org.drools.model.impl.PrototypeVariableImpl;

import static java.util.UUID.randomUUID;
import static org.drools.model.PatternDSL.alphaIndexedBy;
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
        PrototypePatternDef expr(String fieldName, Index.ConstraintType constraintType, Object value);
        PrototypePatternDef expr(PrototypeExpression left, Index.ConstraintType constraintType, PrototypeExpression right);

        PrototypePatternDef expr(String fieldName, Index.ConstraintType constraintType, PrototypeVariable other, String otherFieldName);
        PrototypePatternDef expr(PrototypeExpression left, Index.ConstraintType constraintType, PrototypeVariable other, PrototypeExpression right);

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
        public PrototypePatternDef expr(String fieldName, Index.ConstraintType constraintType, Object value) {
            return expr(fieldName2PrototypeExpression(fieldName), constraintType, PrototypeExpression.fixedValue(value));
        }

        @Override
        public PrototypePatternDef expr(PrototypeExpression left, Index.ConstraintType constraintType, PrototypeExpression right) {
            Prototype prototype = getPrototype();
            Function1<PrototypeFact, Object> leftExtractor;
            AlphaIndex alphaIndex = null;
            if (left instanceof PrototypeExpression.PrototypeFieldValue && right instanceof PrototypeExpression.FixedValue) {
                String fieldName = ((PrototypeExpression.PrototypeFieldValue) left).getFieldName();
                if (constraintType == Index.ConstraintType.EXISTS_PROTOTYPE_FIELD) {
                    leftExtractor = prototypeFact -> prototypeFact.has(fieldName);
                    constraintType = Index.ConstraintType.EQUAL;
                } else {
                    Prototype.Field field = prototype.getField(fieldName);
                    Object value = ((PrototypeExpression.FixedValue) right).getValue();

                    leftExtractor = getFieldValueExtractor(prototype, fieldName);
                    int fieldIndex = field != null ? prototype.getFieldIndex(fieldName) : Math.abs(fieldName.hashCode());

                    Class<Object> fieldClass = (Class<Object>) (field != null && field.isTyped() ? field.getType() : value != null ? value.getClass() : null);
                    if (fieldClass != null) {
                        alphaIndex = alphaIndexedBy(fieldClass, constraintType, fieldIndex, leftExtractor, value);
                    }
                }
            } else {
                leftExtractor = left.asFunction(prototype);
            }

            Set<String> reactOnFields = new HashSet<>();
            reactOnFields.addAll(left.getImpactedFields());
            reactOnFields.addAll(right.getImpactedFields());

            expr("expr:" + left + ":" + constraintType + ":" + right,
                    asPredicate1(leftExtractor, constraintType, right.asFunction(prototype)),
                    alphaIndex,
                    reactOn( reactOnFields.toArray(new String[reactOnFields.size()])) );

            return this;
        }

        @Override
        public PrototypePatternDef expr(String fieldName, Index.ConstraintType constraintType, PrototypeVariable other, String otherFieldName) {
            return expr(fieldName2PrototypeExpression(fieldName), constraintType, other, fieldName2PrototypeExpression(otherFieldName));
        }

        @Override
        public PrototypePatternDef expr(PrototypeExpression left, Index.ConstraintType constraintType, PrototypeVariable other, PrototypeExpression right) {
            Prototype prototype = getPrototype();
            Prototype otherPrototype = other.getPrototype();

            Set<String> reactOnFields = new HashSet<>();
            reactOnFields.addAll(left.getImpactedFields());
            reactOnFields.addAll(right.getImpactedFields());

            expr("expr:" + left + ":" + constraintType + ":" + right,
                    other, asPredicate2(left.asFunction(prototype), constraintType, right.asFunction(otherPrototype)),
                    reactOn( reactOnFields.toArray(new String[reactOnFields.size()])) );

            return this;
        }

        @Override
        public PrototypePatternDef expr(TemporalPredicate temporalPredicate, PrototypeVariable other) {
            expr( randomUUID().toString(), other, temporalPredicate );
            getPrototype().setAsEvent(true);
            other.getPrototype().setAsEvent(true);
            return this;
        }

        private Predicate1<PrototypeFact> asPredicate1(Function1<PrototypeFact, Object> left, Index.ConstraintType constraintType, Function1<PrototypeFact, Object> right) {
            return p -> {
                Object leftValue = left.apply(p);
                Object rightValue = right.apply(p);
                return leftValue != Prototype.UNDEFINED_VALUE && rightValue != Prototype.UNDEFINED_VALUE && constraintType.asPredicate().test(leftValue, rightValue);
            };
        }

        private Predicate2<PrototypeFact, PrototypeFact> asPredicate2(Function1<PrototypeFact, Object> extractor, Index.ConstraintType constraintType, Function1<PrototypeFact, Object> otherExtractor) {
            return (p1, p2) -> {
                Object leftValue = extractor.apply(p1);
                Object rightValue = otherExtractor.apply(p2);
                return leftValue != Prototype.UNDEFINED_VALUE && rightValue != Prototype.UNDEFINED_VALUE && constraintType.asPredicate().test(leftValue, rightValue);
            };
        }

        private Function1<PrototypeFact, Object> getFieldValueExtractor(Prototype prototype, String fieldName) {
            return prototype.getFieldValueExtractor(fieldName)::apply;
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