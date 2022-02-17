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

import java.util.function.Function;

import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.impl.PrototypeImpl;
import org.drools.model.impl.PrototypeVariableImpl;

import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
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

        PrototypePatternDef expr(String fieldName, Index.ConstraintType constraintType, PrototypeVariable other, String otherFieldName);
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
            Function1<PrototypeFact, Object> extractor = getFieldValueExtractor(prototype, fieldName);
            Class<Object> fieldClass = (Class<Object>) (field != null && field.isTyped() ? field.getType() : value.getClass());
            int fieldIndex = field != null ? prototype.getFieldIndex(fieldName) : Math.abs(fieldName.hashCode());

            expr("expr:" + fieldName + ":" + constraintType + ":" + value,
                    asPredicate(extractor, constraintType, value),
                    alphaIndexedBy( fieldClass, constraintType, fieldIndex, extractor, value ),
                    reactOn( fieldName ));

            return this;
        }

        @Override
        public PrototypePatternDef expr(String fieldName, Index.ConstraintType constraintType, PrototypeVariable other, String otherFieldName) {
            PrototypeVariable protoVar = (PrototypeVariable) getFirstVariable();

            Prototype prototype = protoVar.getPrototype();
            Prototype.Field field = prototype.getField(fieldName);
            Function1<PrototypeFact, Object> extractor = getFieldValueExtractor(prototype, fieldName);
            Class<Object> fieldClass = (Class<Object>) (field != null && field.isTyped() ? field.getType() : Object.class);
            int fieldIndex = field != null ? prototype.getFieldIndex(fieldName) : Math.abs(fieldName.hashCode());

            Prototype otherPrototype = other.getPrototype();
            Function1<PrototypeFact, Object> otherExtractor = getFieldValueExtractor(otherPrototype, otherFieldName);

            expr("expr:" + fieldName + ":" + constraintType + ":" + otherFieldName,
                    other, asPredicate(extractor, constraintType, otherExtractor),
                    betaIndexedBy( fieldClass, constraintType, fieldIndex, extractor, otherExtractor ),
                    reactOn( fieldName ));

            return this;
        }

        private Predicate1<PrototypeFact> asPredicate(Function1<PrototypeFact, Object> extractor, Index.ConstraintType constraintType, Object value) {
            return p -> constraintType.asPredicate().test(extractor.apply(p), value);
        }

        private Predicate2<PrototypeFact, PrototypeFact> asPredicate(Function1<PrototypeFact, Object> extractor, Index.ConstraintType constraintType, Function1<PrototypeFact, Object> otherExtractor) {
            return (p1, p2) -> constraintType.asPredicate().test(extractor.apply(p1), otherExtractor.apply(p2));
        }

        private Function1<PrototypeFact, Object> getFieldValueExtractor(Prototype prototype, String fieldName) {
            return prototype.getFieldValueExtractor(fieldName)::apply;
        }
    }
}