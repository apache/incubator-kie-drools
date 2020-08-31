/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.explainability;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.tracing.typedvalue.TypedValue;

public class ConversionUtils {

    private ConversionUtils() {
        // prevent initialization
    }

    protected static Feature toFeature(String name, Object value) {
        if (value instanceof JsonObject) {
            return new Feature(name, Type.COMPOSITE, new Value<>(toFeatureList((JsonObject) value)));
        }
        return toTypeValuePair(value)
                .map(p -> new Feature(name, p.getLeft(), p.getRight()))
                .orElse(null);
    }

    public static Feature toFeature(String name, TypedValue value) {
        // TODO: handle COLLECTION values https://issues.redhat.com/browse/KOGITO-3194
        if (value.isUnit()) {
            return toTypeValuePair(value.toUnit().getValue())
                    .map(p -> new Feature(name, p.getLeft(), p.getRight()))
                    .orElse(null);
        }
        if (value.isStructure()) {
            return new Feature(name, Type.COMPOSITE, new Value<>(toFeatureList(value.toStructure().getValue())));
        }
        return null;
    }

    public static List<Feature> toFeatureList(JsonObject mainObj) {
        return toList(mainObj, ConversionUtils::toFeature);
    }

    public static List<Feature> toFeatureList(Map<String, TypedValue> values) {
        return toList(values, ConversionUtils::toFeature);
    }

    public static <T> List<T> toList(JsonObject mainObj, BiFunction<String, Object, T> unitConverter) {
        if (mainObj == null) {
            return Collections.emptyList();
        }
        return mainObj.stream()
                .map(entry -> unitConverter.apply(entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static <T> List<T> toList(Map<String, TypedValue> values, BiFunction<String, TypedValue, T> unitConverter) {
        if (values == null) {
            return Collections.emptyList();
        }
        return values.entrySet().stream()
                .map(entry -> unitConverter.apply(entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected static Output toOutput(String name, Object value) {
        if (value instanceof JsonObject) {
            return new Output(name, Type.COMPOSITE, new Value<>(toFeatureList((JsonObject) value)), 1d);
        }
        return toTypeValuePair(value)
                .map(p -> new Output(name, p.getLeft(), p.getRight(), 1d))
                .orElse(null);
    }

    public static Output toOutput(String name, TypedValue value) {
        // TODO: handle COLLECTION values https://issues.redhat.com/browse/KOGITO-3194
        if (value.isUnit()) {
            return toTypeValuePair(value.toUnit().getValue())
                    .map(p -> new Output(name, p.getLeft(), p.getRight(), 1d))
                    .orElse(null);
        }
        if (value.isStructure()) {
            return new Output(name, Type.COMPOSITE, new Value<>(toFeatureList(value.toStructure().getValue())), 1d);
        }
        return null;
    }

    public static List<Output> toOutputList(JsonObject mainObj) {
        return toList(mainObj, ConversionUtils::toOutput);
    }

    public static List<Output> toOutputList(Map<String, TypedValue> values) {
        return toList(values, ConversionUtils::toOutput);
    }

    protected static Optional<Pair<Type, Value<Object>>> toTypeValuePair(Object value) {
        if (value instanceof Boolean) {
            return Optional.of(Pair.of(Type.BOOLEAN, new Value<>(value)));
        }
        if (value instanceof Number) {
            return Optional.of(Pair.of(Type.NUMBER, new Value<>(((Number) value).doubleValue())));
        }
        if (value instanceof String) {
            return Optional.of(Pair.of(Type.TEXT, new Value<>(value)));
        }
        return Optional.empty();
    }

    public static Optional<Pair<Type, Value<Object>>> toTypeValuePair(JsonNode jsonValue) {
        if (jsonValue.isBoolean()) {
            return Optional.of(Pair.of(Type.BOOLEAN, new Value<>(jsonValue.asBoolean())));
        }
        if (jsonValue.isNumber()) {
            return Optional.of(Pair.of(Type.NUMBER, new Value<>(jsonValue.asDouble())));
        }
        if (jsonValue.isTextual()) {
            return Optional.of(Pair.of(Type.TEXT, new Value<>(jsonValue.asText())));
        }
        return Optional.empty();
    }
}
