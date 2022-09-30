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
import org.drools.model.impl.PrototypeImpl;
import org.drools.model.impl.PrototypeVariableImpl;

import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.reactOn;

public class PrototypeDSL {

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
    }

    public static class PrototypePatternDefImpl extends PatternDSL.PatternDefImpl<PrototypeFact> implements PrototypePatternDef {
        public PrototypePatternDefImpl(PrototypeVariable variable) {
            super(variable);
        }

        @Override
        public PrototypePatternDef expr(String fieldName, Index.ConstraintType constraintType, Object value) {
            PrototypeVariable protoVar = (PrototypeVariable) getFirstVariable();

            Prototype prototype = protoVar.getPrototype();
            Prototype.Field field = prototype.getField(fieldName);

            Function1<PrototypeFact, Object> extractor = null;
            AlphaIndex alphaIndex = null;
            if (constraintType == Index.ConstraintType.EXISTS_PROTOTYPE_FIELD) {
                extractor = prototypeFact -> prototypeFact.has(fieldName);
                constraintType = Index.ConstraintType.EQUAL;
            } else {
                extractor = getFieldValueExtractor(prototype, fieldName);
                int fieldIndex = field != null ? prototype.getFieldIndex(fieldName) : Math.abs(fieldName.hashCode());

                Class<Object> fieldClass = (Class<Object>) (field != null && field.isTyped() ? field.getType() : value != null ? value.getClass() : null);
                if (fieldClass != null) {
                    alphaIndex = alphaIndexedBy(fieldClass, constraintType, fieldIndex, extractor, value);
                }
            }

            expr("expr:" + fieldName + ":" + constraintType + ":" + value,
                    asPredicate1(extractor, constraintType, value),
                    alphaIndex,
                    reactOn( fieldName ));

            return this;
        }

        @Override
        public PrototypePatternDef expr(PrototypeExpression left, Index.ConstraintType constraintType, PrototypeExpression right) {
            if (left instanceof PrototypeExpression.PrototypeFieldValue && right instanceof PrototypeExpression.FixedValue) {
                return expr(((PrototypeExpression.PrototypeFieldValue) left).getFieldName(), constraintType, ((PrototypeExpression.FixedValue) right).getValue());
            }

            PrototypeVariable protoVar = (PrototypeVariable) getFirstVariable();
            Prototype prototype = protoVar.getPrototype();

            Set<String> reactOnFields = new HashSet<>();
            reactOnFields.addAll(left.getImpactedFields());
            reactOnFields.addAll(right.getImpactedFields());

            expr("expr:" + left + ":" + constraintType + ":" + right,
                    asPredicate1(left.asFunction(prototype), constraintType, right.asFunction(prototype)),
                    reactOn( reactOnFields.toArray(new String[reactOnFields.size()])) );

            return this;
        }

        @Override
        public PrototypePatternDef expr(String fieldName, Index.ConstraintType constraintType, PrototypeVariable other, String otherFieldName) {
            PrototypeVariable protoVar = (PrototypeVariable) getFirstVariable();

            Prototype prototype = protoVar.getPrototype();
            Prototype.Field field = prototype.getField(fieldName);
            Function1<PrototypeFact, Object> extractor = getFieldValueExtractor(prototype, fieldName);

            Prototype otherPrototype = other.getPrototype();
            Function1<PrototypeFact, Object> otherExtractor = getFieldValueExtractor(otherPrototype, otherFieldName);

            expr("expr:" + fieldName + ":" + constraintType + ":" + otherFieldName,
                    other, asPredicate2(extractor, constraintType, otherExtractor),
                    reactOn( fieldName ));

            return this;
        }

        @Override
        public PrototypePatternDef expr(PrototypeExpression left, Index.ConstraintType constraintType, PrototypeVariable other, PrototypeExpression right) {
            if (left instanceof PrototypeExpression.PrototypeFieldValue && right instanceof PrototypeExpression.PrototypeFieldValue) {
                return expr(((PrototypeExpression.PrototypeFieldValue) left).getFieldName(), constraintType, other, ((PrototypeExpression.PrototypeFieldValue) right).getFieldName());
            }

            PrototypeVariable protoVar = (PrototypeVariable) getFirstVariable();
            Prototype prototype = protoVar.getPrototype();
            Prototype otherPrototype = other.getPrototype();

            Set<String> reactOnFields = new HashSet<>();
            reactOnFields.addAll(left.getImpactedFields());
            reactOnFields.addAll(right.getImpactedFields());

            expr("expr:" + left + ":" + constraintType + ":" + right,
                    other, asPredicate2(left.asFunction(prototype), constraintType, right.asFunction(otherPrototype)),
                    reactOn( reactOnFields.toArray(new String[reactOnFields.size()])) );

            return this;
        }

        private Predicate1<PrototypeFact> asPredicate1(Function1<PrototypeFact, Object> extractor, Index.ConstraintType constraintType, Object value) {
            return p -> constraintType.asPredicate().test(extractor.apply(p), value);
        }

        private Predicate1<PrototypeFact> asPredicate1(Function1<PrototypeFact, Object> left, Index.ConstraintType constraintType, Function1<PrototypeFact, Object> right) {
            return p -> constraintType.asPredicate().test(left.apply(p), right.apply(p));
        }

        private Predicate2<PrototypeFact, PrototypeFact> asPredicate2(Function1<PrototypeFact, Object> extractor, Index.ConstraintType constraintType, Function1<PrototypeFact, Object> otherExtractor) {
            return (p1, p2) -> constraintType.asPredicate().and((a,b) -> a != null).test(extractor.apply(p1), otherExtractor.apply(p2));
        }

        private Function1<PrototypeFact, Object> getFieldValueExtractor(Prototype prototype, String fieldName) {
            return prototype.getFieldValueExtractor(fieldName)::apply;
        }
    }
}