/*
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
package org.kie.kogito.explainability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.kogito.explainability.api.CounterfactualDomain;
import org.kie.kogito.explainability.api.CounterfactualDomainCategorical;
import org.kie.kogito.explainability.api.CounterfactualDomainRange;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainValue;
import org.kie.kogito.explainability.api.HasNameValue;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.model.domain.CategoricalFeatureDomain;
import org.kie.kogito.explainability.model.domain.EmptyFeatureDomain;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.kie.kogito.explainability.model.domain.NumericalFeatureDomain;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.vertx.core.json.JsonObject;

public class ConversionUtils {

    ////////////////////////////
    // TO EXPLAINABILITY MODEL
    ////////////////////////////

    /*
     * ---------------------------------------
     * Feature conversion
     * ---------------------------------------
     */

    public static List<Feature> toFeatureList(Collection<? extends HasNameValue<TypedValue>> values,
            Collection<CounterfactualSearchDomain> searchDomains) {
        if (searchDomains.isEmpty()) {
            return toFeatureList(values);
        } else {
            AtomicInteger index = new AtomicInteger();
            final List<FeatureDomain> featureDomains = toFeatureDomainList(searchDomains);
            final List<Boolean> featureConstraints = toFeatureConstraintList(searchDomains);
            return values.stream().map(hnv -> {
                final String name = hnv.getName();
                final TypedValue value = hnv.getValue();
                final int i = index.getAndIncrement();
                return toFeature(name, value, featureDomains.get(i), featureConstraints.get(i));
            }).collect(Collectors.toList());
        }
    }

    public static List<Feature> toFeatureList(Collection<? extends HasNameValue<TypedValue>> values) {
        return toList(values, ConversionUtils::toFeature);
    }

    static Feature toFeature(HasNameValue<TypedValue> hnv) {
        return toFeature(hnv.getName(), hnv.getValue());
    }

    static Feature toFeature(String name, TypedValue value) {
        if (value.isUnit()) {
            return toTypeValuePair(value.toUnit().getValue())
                    .map(p -> new Feature(name, p.getLeft(), p.getRight()))
                    .orElse(null);
        } else if (value.isStructure()) {
            return FeatureFactory.newCompositeFeature(name, toFeatureList(value.toStructure().getValue()));
        } else if (value.isCollection()) {
            return FeatureFactory.newCompositeFeature(name, toFeatureList(name, value.toCollection()));
        } else {
            throw new IllegalArgumentException(String.format("unexpected value kind %s", value.getKind()));
        }
    }

    static Feature toFeature(String name, TypedValue value, FeatureDomain domain, Boolean isContrained) {
        if (value.isUnit()) {
            return toTypeValuePair(value.toUnit().getValue())
                    .map(p -> {
                        if (isContrained) {
                            return new Feature(name, p.getLeft(), p.getRight());
                        } else {
                            return new Feature(name, p.getLeft(), p.getRight(), false, domain);
                        }
                    })
                    .orElse(null);
        } else if (value.isStructure()) {
            return FeatureFactory.newCompositeFeature(name, toFeatureList(value.toStructure().getValue()));
        } else if (value.isCollection()) {
            return FeatureFactory.newCompositeFeature(name, toFeatureList(name, value.toCollection()));
        } else {
            throw new IllegalArgumentException(String.format("unexpected value kind %s", value.getKind()));
        }
    }

    static List<Feature> toFeatureList(Map<String, TypedValue> values) {
        return toList(values, ConversionUtils::toFeature);
    }

    static List<Feature> toFeatureList(String name, CollectionValue collectionValue) {
        Collection<TypedValue> values = collectionValue.getValue();
        List<Feature> list = new ArrayList<>(values.size());
        int index = 0;
        for (TypedValue typedValue : values) {
            list.add(toFeature(name + "_" + index, typedValue));
            index++;
        }
        return list;
    }

    /*
     * ---------------------------------------
     * Output conversion
     * ---------------------------------------
     */
    public static List<Output> toOutputList(Collection<? extends HasNameValue<TypedValue>> values) {
        return toList(values, hnv -> toOutput(hnv.getName(), hnv.getValue()));
    }

    static Output toOutput(String name, TypedValue value) {
        if (value.isUnit()) {
            return toTypeValuePair(value.toUnit().getValue())
                    .map(p -> new Output(name, p.getLeft(), p.getRight(), 1d))
                    .orElse(null);
        } else if (value.isStructure()) {
            return new Output(name, Type.COMPOSITE, new Value(toOutputListForStructure(value.toStructure().getValue())), 1d);
        } else if (value.isCollection()) {
            return new Output(name, Type.COMPOSITE, new Value(toOutputListForCollection(name, value.toCollection())), 1d);
        }
        return null;
    }

    static Optional<Pair<Type, Value>> toTypeValuePair(JsonNode jsonValue) {
        if (jsonValue.isBoolean()) {
            return Optional.of(Pair.of(Type.BOOLEAN, new Value(jsonValue.asBoolean())));
        }
        if (jsonValue.isNumber()) {
            return Optional.of(Pair.of(Type.NUMBER, new Value(jsonValue.asDouble())));
        }
        if (jsonValue.isTextual()) {
            return Optional.of(Pair.of(Type.TEXT, new Value(jsonValue.asText())));
        }
        return Optional.empty();
    }

    static List<Output> toOutputListForStructure(Map<String, TypedValue> values) {
        return toList(values, ConversionUtils::toOutput);
    }

    static List<Output> toOutputListForCollection(String name, CollectionValue collectionValue) {
        Collection<TypedValue> values = collectionValue.getValue();
        List<Output> list = new ArrayList<>(values.size());
        int index = 0;
        for (TypedValue typedValue : values) {
            list.add(toOutput(name + "_" + index, typedValue));
            index++;
        }
        return list;
    }

    /*
     * ---------------------------------------
     * Counterfactual Search Domain conversion
     * ---------------------------------------
     */
    public static List<FeatureDomain> toFeatureDomainList(Collection<CounterfactualSearchDomain> searchDomains) {
        return toList(searchDomains, hnv -> toFeatureDomain(hnv.getValue()));
    }

    public static FeatureDomain toFeatureDomain(CounterfactualSearchDomainValue domain) {
        if (domain.isUnit()) {
            return toCounterfactualSearchDomain(domain.toUnit().getDomain())
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported CounterfactualSearchDomain type %s", domain.getClass().getName())));
        } else {
            throw new IllegalArgumentException(String.format("Unsupported CounterfactualSearchDomain kind %s", domain.getKind()));
        }
    }

    static Optional<FeatureDomain> toCounterfactualSearchDomain(CounterfactualDomain domain) {
        if (Objects.isNull(domain)) {
            return Optional.of(EmptyFeatureDomain.create());
        } else if (domain instanceof CounterfactualDomainRange) {
            CounterfactualDomainRange range = (CounterfactualDomainRange) domain;
            JsonNode lb = range.getLowerBound();
            JsonNode ub = range.getUpperBound();
            if (lb.isNumber() && ub.isNumber()) {
                return Optional.of(NumericalFeatureDomain.create(range.getLowerBound().asDouble(), range.getUpperBound().asDouble()));
            } else {
                throw new IllegalArgumentException(String.format("Unsupported CounterfactualDomainRange [%s, %s]", lb.asText(), ub.asText()));
            }
        } else if (domain instanceof CounterfactualDomainCategorical) {
            CounterfactualDomainCategorical categorical = (CounterfactualDomainCategorical) domain;
            Collection<JsonNode> jsonCategories = categorical.getCategories();
            String[] categories = new String[jsonCategories.size()];
            if (jsonCategories.stream().allMatch(JsonNode::isTextual)) {
                jsonCategories.stream().map(JsonNode::asText).collect(Collectors.toList()).toArray(categories);
                return Optional.of(CategoricalFeatureDomain.create(categories));
            } else {
                throw new IllegalArgumentException(String.format("Unsupported CounterfactualDomainCategorical [%s]", String.join(", ", categories)));
            }
        }

        return Optional.empty();
    }

    public static List<Boolean> toFeatureConstraintList(Collection<CounterfactualSearchDomain> searchDomains) {
        return toList(searchDomains, hnv -> toFeatureConstraint(hnv.getValue()));
    }

    static Boolean toFeatureConstraint(CounterfactualSearchDomainValue domain) {
        if (domain.isUnit()) {
            return domain.toUnit().isFixed();
        } else {
            throw new IllegalArgumentException(String.format("Unsupported CounterfactualSearchDomain kind %s", domain.getKind()));
        }
    }

    /*
     * ---------------------------------------
     * JSON conversion
     * ---------------------------------------
     */
    public static List<Output> toOutputList(JsonObject mainObj) {
        return toList(mainObj, ConversionUtils::toOutput);
    }

    static Output toOutput(String name, Object value) {
        if (value instanceof JsonObject) {
            return new Output(name, Type.COMPOSITE, new Value(toOutputList((JsonObject) value)), 1d);
        }
        return toTypeValuePair(value)
                .map(p -> new Output(name, p.getLeft(), p.getRight(), 1d))
                .orElse(null);
    }

    static Optional<Pair<Type, Value>> toTypeValuePair(Object value) {
        if (value instanceof Boolean) {
            return Optional.of(Pair.of(Type.BOOLEAN, new Value(value)));
        }
        if (value instanceof Number) {
            return Optional.of(Pair.of(Type.NUMBER, new Value(((Number) value).doubleValue())));
        }
        if (value instanceof String) {
            return Optional.of(Pair.of(Type.TEXT, new Value(value)));
        }
        return Optional.empty();
    }

    /*
     * ---------------------------------------
     * List conversion
     * ---------------------------------------
     */
    static <R, T> List<R> toList(Collection<T> values,
            Function<T, R> unitConverter) {
        if (values == null) {
            return Collections.emptyList();
        }
        return values.stream()
                .map(unitConverter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    static <T, V> List<T> toList(Map<String, V> values, BiFunction<String, V, T> unitConverter) {
        if (values == null) {
            return Collections.emptyList();
        }
        return values.entrySet().stream()
                .map(entry -> unitConverter.apply(entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    static <T> List<T> toList(JsonObject mainObj, BiFunction<String, Object, T> unitConverter) {
        if (mainObj == null) {
            return Collections.emptyList();
        }
        return mainObj.stream()
                .map(entry -> unitConverter.apply(entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    ////////////////////////////
    // FROM EXPLAINABILITY MODEL
    ////////////////////////////

    /*
     * ---------------------------------------
     * Feature conversion
     * ---------------------------------------
     */
    public static List<NamedTypedValue> fromFeatureList(List<Feature> features) {
        return toList(features, f -> new NamedTypedValue(f.getName(), toTypedValue(f)));
    }

    static TypedValue toTypedValue(Feature feature) {
        String name = feature.getName();
        Type type = feature.getType();
        Value value = feature.getValue();
        return toTypedValue(name, type, value);
    }

    static TypedValue toTypedValue(String name, Type type, Value value) {
        Object underlyingObject = value.getUnderlyingObject();
        if (type.equals(Type.BOOLEAN)) {
            if (underlyingObject instanceof Boolean) {
                return new UnitValue(Boolean.class.getSimpleName(), BooleanNode.valueOf(((Boolean) underlyingObject)));
            }
        } else if (type.equals(Type.NUMBER)) {
            if (underlyingObject instanceof Double) {
                return new UnitValue(Double.class.getSimpleName(), new DoubleNode((Double) underlyingObject));
            }
        } else if (type.equals(Type.TEXT)) {
            if (underlyingObject instanceof String) {
                return new UnitValue(String.class.getSimpleName(), new TextNode((String) underlyingObject));
            }
        }
        throw new IllegalArgumentException(String.format("Unable to convert '%s' with Type '%s' and Value '%s'", name, type, value));
    }

    /*
     * ---------------------------------------
     * Output conversion
     * ---------------------------------------
     */
    public static List<NamedTypedValue> fromOutputs(List<Output> outputs) {
        return toList(outputs, o -> new NamedTypedValue(o.getName(), toTypedValue(o)));
    }

    static TypedValue toTypedValue(Output output) {
        String name = output.getName();
        Type type = output.getType();
        Value value = output.getValue();
        return toTypedValue(name, type, value);
    }

}
