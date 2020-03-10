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
package org.kie.pmml.models.tree.model.predicates;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.models.tree.model.enums.OPERATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimplePredicate>SimplePredicate</a>
 */
public class KiePMMLSimplePredicate extends KiePMMLPredicate {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSimplePredicate.class);

    private final OPERATOR operator;
    private Object value;

    private KiePMMLSimplePredicate(String name, List<KiePMMLExtension> extensions, OPERATOR operator) {
        super(name, extensions);
        this.operator = operator;
    }

    /**
     * Builder to auto-generate the <b>id</b>
     * @return
     */
    public static Builder builder(String name, List<KiePMMLExtension> extensions, OPERATOR operator) {
        return new Builder(name, extensions, operator);
    }

    @Override
    public boolean evaluate(Map<String, Object> values) {
        logger.info("{} evaluate {}", this, values);
        boolean toReturn = false;
        if (values.containsKey(name)) {
            logger.info("found matching parameter, evaluating... ");
            toReturn = evaluation(values.get(name));
        }
        logger.info("return {}", toReturn);
        return toReturn;
    }

    public String getName() {
        return name;
    }

    public OPERATOR getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "KiePMMLSimplePredicate{" +
                "operator=" + operator +
                ", name='" + name + '\'' +
                ", value=" + value +
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
        KiePMMLSimplePredicate that = (KiePMMLSimplePredicate) o;
        return operator == that.operator &&
                Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), operator, name, value);
    }

    private boolean evaluation(Object inputValue) {
        logger.info("evaluation {}", inputValue);
        switch (operator) {
            case EQUAL:
                return value.equals(inputValue);
            case NOT_EQUAL:
                return !value.equals(inputValue);
            case LESS_THAN:
                if (inputValue instanceof Number && value instanceof Number) {
                    return ((Number) inputValue).doubleValue() < ((Number) value).doubleValue();
                } else {
                    return false;
                }
            case LESS_OR_EQUAL:
                if (inputValue instanceof Number && value instanceof Number) {
                    return ((Number) inputValue).doubleValue() <= ((Number) value).doubleValue();
                } else {
                    return false;
                }
            case GREATER_THAN:
                if (inputValue instanceof Number && value instanceof Number) {
                    return ((Number) inputValue).doubleValue() > ((Number) value).doubleValue();
                } else {
                    return false;
                }
            case GREATER_OR_EQUAL:
                if (inputValue instanceof Number && value instanceof Number) {
                    return ((Number) inputValue).doubleValue() >= ((Number) value).doubleValue();
                } else {
                    return false;
                }
            case IS_MISSING:
            case IS_NOT_MISSING:
            default:
                return true;
        }
    }

    public static class Builder extends KiePMMLPredicate.Builder<KiePMMLSimplePredicate> {

        private Builder(String name, List<KiePMMLExtension> extensions, OPERATOR operator) {
            super("SimplePredicate-", () -> new KiePMMLSimplePredicate(name, extensions, operator));
        }

        public KiePMMLSimplePredicate.Builder withValue(Object value) {
            toBuild.value = value;
            return this;
        }
    }
}
