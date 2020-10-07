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
package org.kie.pmml.commons.model.predicates;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;

import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.enums.BOOLEAN_OPERATOR.SURROGATE;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimplePredicate>SimplePredicate</a>
 */
public class KiePMMLCompoundPredicate extends KiePMMLPredicate {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLCompoundPredicate.class);

    private final BOOLEAN_OPERATOR booleanOperator;
    protected BinaryOperator<Boolean> operatorFunction;
    protected List<KiePMMLPredicate> kiePMMLPredicates;

    protected KiePMMLCompoundPredicate(final String name, final List<KiePMMLExtension> extensions, final BOOLEAN_OPERATOR booleanOperator) {
        super(name, extensions);
        this.booleanOperator = booleanOperator;
    }

    /**
     * Builder to auto-generate the <b>id</b>
     * @return
     */
    public static Builder builder(List<KiePMMLExtension> extensions, BOOLEAN_OPERATOR booleanOperator) {
        return new Builder(extensions, booleanOperator);
    }

    @Override
    public boolean evaluate(Map<String, Object> values) {
        Boolean toReturn = null;
        for (KiePMMLPredicate kiePMMLPredicate : kiePMMLPredicates) {
            toReturn = operatorFunction.apply(toReturn, kiePMMLPredicate.evaluate(values));
        }
        return toReturn != null && toReturn;
    }

    @Override
    public String getId() {
        return id;
    }

    public BOOLEAN_OPERATOR getBooleanOperator() {
        return booleanOperator;
    }

    public List<KiePMMLPredicate> getKiePMMLPredicates() {
        return kiePMMLPredicates;
    }

    @Override
    public String toString() {
        return "KiePMMLCompoundPredicate{" +
                "booleanOperator=" + booleanOperator +
                ", operatorFunction=" + operatorFunction +
                ", kiePMMLPredicates=" + kiePMMLPredicates +
                ", extensions=" + extensions +
                ", id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        KiePMMLCompoundPredicate that = (KiePMMLCompoundPredicate) o;
        return booleanOperator == that.booleanOperator &&
                Objects.equals(operatorFunction, that.operatorFunction) &&
                Objects.equals(kiePMMLPredicates, that.kiePMMLPredicates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), booleanOperator, operatorFunction, kiePMMLPredicates);
    }

    public static class Builder extends KiePMMLPredicate.Builder<KiePMMLCompoundPredicate> {

        private Builder(List<KiePMMLExtension> extensions, BOOLEAN_OPERATOR booleanOperator) {
            super("CompoundPredicate-", () -> new KiePMMLCompoundPredicate("CompoundPredicate", extensions, booleanOperator));
            toBuild.operatorFunction = getInnerBinaryOperator(booleanOperator);
        }

        public KiePMMLCompoundPredicate.Builder withKiePMMLPredicates(List<KiePMMLPredicate> kiePMMLPredicates) {
            kiePMMLPredicates.forEach(predicate -> predicate.setParentId(toBuild.id));
            toBuild.kiePMMLPredicates = kiePMMLPredicates;
            return this;
        }

        private BinaryOperator<Boolean> getInnerBinaryOperator(BOOLEAN_OPERATOR booleanOperator) {
            switch (booleanOperator) {
                // logic here is
                // first boolean may be null (initial evaluation) so we start taking the second boolean
                case OR:
                    return (aBoolean, aBoolean2) -> aBoolean != null ? aBoolean || aBoolean2 : aBoolean2;
                case AND:
                    return (aBoolean, aBoolean2) -> aBoolean != null ? aBoolean && aBoolean2 : aBoolean2;
                case XOR:
                    return (aBoolean, aBoolean2) -> aBoolean != null ? aBoolean ^ aBoolean2 : aBoolean2;
                case SURROGATE:
                    // TODO {gcardosi} DROOLS-5594
                    throw new IllegalArgumentException(SURROGATE + " not supported, yet");
                default:
                    throw new KiePMMLException("Unknown BOOLEAN_OPERATOR " + booleanOperator);
            }
        }
    }
}
