/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.api.model.tree.predicates;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import org.kie.pmml.api.model.KiePMMLExtension;
import org.kie.pmml.api.model.tree.enums.BOOLEAN_OPERATOR;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimplePredicate>SimplePredicate</a>
 */
public class KiePMMLCompoundPredicate extends KiePMMLPredicate {

    private static final long serialVersionUID = -1996390505352151403L;

    private final BOOLEAN_OPERATOR booleanOperator;
    private final BinaryOperator<Optional<Boolean>> operatorFunction;
    private List<KiePMMLPredicate> kiePMMLPredicates;

    private KiePMMLCompoundPredicate(List<KiePMMLExtension> extensions, BOOLEAN_OPERATOR booleanOperator, BinaryOperator<Optional<Boolean>> operatorFunction) {
        super(extensions);
        this.booleanOperator = booleanOperator;
        this.operatorFunction = operatorFunction;
    }

    public static Builder builder(List<KiePMMLExtension> extensions, BOOLEAN_OPERATOR booleanOperator) {
        return new Builder(extensions, booleanOperator);
    }

    // TODO {gcardosi} re-implement with native drools rules
   /* @Override
    public Optional<Boolean> evaluate(Map<String, Object> values) {
        Optional<Boolean> toReturn = Optional.empty();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            for (KiePMMLPredicate kiePMMLPredicate : kiePMMLPredicates) {
                toReturn = operatorFunction.apply(toReturn, kiePMMLPredicate.evaluate(Collections.singletonMap(entry.getKey(), entry.getValue())));
            }
        }
        return toReturn;
    }*/

    public BOOLEAN_OPERATOR getBooleanOperator() {
        return booleanOperator;
    }

    public List<KiePMMLPredicate> getKiePMMLPredicates() {
        return kiePMMLPredicates;
    }

    public static class Builder {

        private KiePMMLCompoundPredicate toBuild;

        private Builder(List<KiePMMLExtension> extensions, BOOLEAN_OPERATOR booleanOperator) {
            this.toBuild = new KiePMMLCompoundPredicate(extensions, booleanOperator, getOuterBinaryOperator(getInnerBinaryOperator(booleanOperator)));
        }

        public KiePMMLCompoundPredicate build() {
            return toBuild;
        }

        public KiePMMLCompoundPredicate.Builder withKiePMMLPredicates(List<KiePMMLPredicate> kiePMMLPredicates) {
            toBuild.kiePMMLPredicates = kiePMMLPredicates;
            return this;
        }

        private BinaryOperator<Boolean> getInnerBinaryOperator(BOOLEAN_OPERATOR booleanOperator) {
            switch (booleanOperator) {
                // logic here is
                // First boolean
                case OR:
                    return (aBoolean, aBoolean2) -> aBoolean || aBoolean2;
                case AND:
                    return (aBoolean, aBoolean2) -> aBoolean && aBoolean2;
                case XOR:
                    return (aBoolean, aBoolean2) -> aBoolean ^ aBoolean2;
                // TODO {gcardosi} How to manage?
                case SURROGATE:
                default:
                    return (aBoolean, aBoolean2) -> aBoolean;
            }
        }

        private BinaryOperator<Optional<Boolean>> getOuterBinaryOperator(BinaryOperator<Boolean> binaryOperator) {
            return (aBoolean, aBoolean2) -> {
                if (!aBoolean.isPresent()) {
                    return aBoolean2;
                } else
                    return aBoolean2.map(value -> Optional.of(binaryOperator.apply(aBoolean.get(), value))).orElse(aBoolean);
            };
        }
    }
}
