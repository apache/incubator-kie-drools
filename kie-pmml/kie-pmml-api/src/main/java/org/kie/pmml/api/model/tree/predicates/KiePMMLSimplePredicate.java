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
import java.util.Optional;

import org.kie.pmml.api.model.KiePMMLExtension;
import org.kie.pmml.api.model.tree.enums.OPERATOR;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimplePredicate>SimplePredicate</a>
 */
public class KiePMMLSimplePredicate extends KiePMMLPredicate {

    private static final long serialVersionUID = -1996390505352151403L;
    private final OPERATOR operator;
    private String name;
    private Object value;

    private KiePMMLSimplePredicate(String name, List<KiePMMLExtension> extensions, OPERATOR operator) {
        super(extensions);
        this.name = name;
        this.operator = operator;
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions, OPERATOR operator) {
        return new Builder(name, extensions, operator);
    }

    @Override
    public Optional<Boolean> evaluate(Map<String, Object> values) {
        final Map.Entry<String, Object> entry = values.entrySet().iterator().next();
        String inputName = entry.getKey();
        Object inputValue = entry.getValue();
        if (!this.name.equals(inputName)) {
            return Optional.empty();
        }
        boolean toReturn;
        switch (operator) {
            case EQUAL:
                toReturn = value.equals(inputValue);
                break;
            case NOT_EQUAL:
                toReturn = !value.equals(inputValue);
                break;
            case LESS_THAN:
                if (inputValue instanceof Number && value instanceof Number) {
                    toReturn = ((Number) inputValue).doubleValue() < ((Number) value).doubleValue();
                } else {
                    // TODO {gcardosi}
                    toReturn = true;
                }
                break;
            case LESS_OR_EQUAL:
                if (inputValue instanceof Number && value instanceof Number) {
                    toReturn = ((Number) inputValue).doubleValue() <= ((Number) value).doubleValue();
                } else {
                    // TODO {gcardosi}
                    toReturn = true;
                }
                break;
            case GREATER_THAN:
                if (inputValue instanceof Number && value instanceof Number) {
                    toReturn = ((Number) inputValue).doubleValue() > ((Number) value).doubleValue();
                } else {
                    // TODO {gcardosi}
                    toReturn = true;
                }
                break;
            case GREATER_OR_EQUAL:
                if (inputValue instanceof Number && value instanceof Number) {
                    toReturn = ((Number) inputValue).doubleValue() >= ((Number) value).doubleValue();
                } else {
                    // TODO {gcardosi}
                    toReturn = true;
                }
                break;
            case IS_MISSING:
            case IS_NOT_MISSING:
            default:
                toReturn = true;
        }
        return Optional.of(toReturn);
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KiePMMLSimplePredicate that = (KiePMMLSimplePredicate) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (operator != that.operator) {
            return false;
        }
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KiePMMLSimplePredicate{" +
                "operator=" + operator +
                ", value=" + value +
                ", extensions=" + extensions +
                ", name='" + name + '\'' +
                '}';
    }

    public static class Builder {

        private KiePMMLSimplePredicate toBuild;

        private Builder(String name, List<KiePMMLExtension> extensions, OPERATOR operator) {
            this.toBuild = new KiePMMLSimplePredicate(name, extensions, operator);
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
