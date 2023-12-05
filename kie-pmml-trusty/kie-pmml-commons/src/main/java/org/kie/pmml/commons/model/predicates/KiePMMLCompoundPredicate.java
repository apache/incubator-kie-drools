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
package org.kie.pmml.commons.model.predicates;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLCompoundPredicate extends KiePMMLPredicate {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLCompoundPredicate.class);
    private static final long serialVersionUID = -8106791592643949001L;

    private final BOOLEAN_OPERATOR booleanOperator;
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
            Boolean evaluation = kiePMMLPredicate.evaluate(values);
            switch (booleanOperator) {
                case OR:
                    toReturn = orOperator(toReturn, evaluation);
                    break;
                case AND:
                    toReturn = andOperator(toReturn, evaluation);
                    break;
                case XOR:
                    toReturn = xorOperator(toReturn, evaluation);
                    break;
                case SURROGATE:
                    toReturn = surrogateOperator(toReturn, evaluation);
                    break;
                default:
                    throw new KiePMMLException("Unknown BOOLEAN_OPERATOR " + booleanOperator);
            }
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
                Objects.equals(kiePMMLPredicates, that.kiePMMLPredicates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), booleanOperator, kiePMMLPredicates);
    }

    static Boolean orOperator(Boolean aBoolean, Boolean aBoolean2) {
        logger.trace("orOperator {} {}",  aBoolean,  aBoolean2);
        return aBoolean != null ? aBoolean || aBoolean2 : aBoolean2;
    }

    static Boolean andOperator(Boolean aBoolean, Boolean aBoolean2) {
        logger.trace("andOperator {} {}",  aBoolean,  aBoolean2);
        return aBoolean != null ? aBoolean && aBoolean2 : aBoolean2;
    }

    static Boolean xorOperator(Boolean aBoolean, Boolean aBoolean2) {
        logger.trace("xorOperator {} {}",  aBoolean,  aBoolean2);
        return aBoolean != null ? aBoolean ^ aBoolean2 : aBoolean2;
    }

    static Boolean surrogateOperator(Boolean aBoolean, Boolean aBoolean2) {
        logger.trace("surrogateOperator {} {}",  aBoolean,  aBoolean2);
        return aBoolean != null ? aBoolean : aBoolean2;
    }

    public static class Builder extends KiePMMLPredicate.Builder<KiePMMLCompoundPredicate> {

        private Builder(List<KiePMMLExtension> extensions, BOOLEAN_OPERATOR booleanOperator) {
            super("CompoundPredicate-", () -> new KiePMMLCompoundPredicate("CompoundPredicate", extensions, booleanOperator));
        }

        public KiePMMLCompoundPredicate.Builder withKiePMMLPredicates(List<KiePMMLPredicate> kiePMMLPredicates) {
            kiePMMLPredicates.forEach(predicate -> predicate.setParentId(toBuild.id));
            toBuild.kiePMMLPredicates = kiePMMLPredicates;
            return this;
        }

    }
}
