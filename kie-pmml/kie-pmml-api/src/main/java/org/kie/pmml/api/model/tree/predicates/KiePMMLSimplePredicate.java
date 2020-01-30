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

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.kie.pmml.api.model.KiePMMLExtension;
import org.kie.pmml.api.model.tree.enums.OPERATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimplePredicate>SimplePredicate</a>
 */
public class KiePMMLSimplePredicate extends KiePMMLPredicate {

    private static final long serialVersionUID = -1996390505352151403L;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSimplePredicate.class);

    private final OPERATOR operator;
    private String name;
    private Object value;

    /**
     * Builder to provide a defined <b>id</b>
     * @param  id
     * @return
     */
    public static Builder builder(String id, String name, List<KiePMMLExtension> extensions, OPERATOR operator) {
        return new Builder(id, name, extensions, operator);
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
        logger.info(String.format("evaluate %s", this.toString()));
        boolean toReturn = false;
       if (values.containsKey(name)) {
           logger.info("found matching parameter, evaluate ");
           toReturn = evaluation(values.get(name));
       }
        logger.info(String.format("return %s", toReturn));
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
                ", id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", extensions=" + extensions +
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

        if (operator != that.operator) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    private boolean evaluation(Object inputValue) {
        logger.info(String.format("evaluation %s", inputValue));
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

    private KiePMMLSimplePredicate(String id, String name, List<KiePMMLExtension> extensions, OPERATOR operator) {
        super(id, extensions);
        this.name = name;
        this.operator = operator;
    }

    public static class Builder {

        private static final AtomicInteger counter = new AtomicInteger(1);
        private KiePMMLSimplePredicate toBuild;

        private Builder(String id, String name, List<KiePMMLExtension> extensions, OPERATOR operator) {
            this.toBuild = new KiePMMLSimplePredicate(id, name, extensions, operator);
        }

        private Builder(String name, List<KiePMMLExtension> extensions, OPERATOR operator) {
            String id = "SimplePredicate-" + counter.getAndAdd(1);
            this.toBuild = new KiePMMLSimplePredicate(id, name, extensions, operator);
        }

        public KiePMMLSimplePredicate build() {
            return toBuild;
        }

        public KiePMMLSimplePredicate.Builder withValue(Object value) {
            toBuild.value = value;
            return this;
        }
    }
}
