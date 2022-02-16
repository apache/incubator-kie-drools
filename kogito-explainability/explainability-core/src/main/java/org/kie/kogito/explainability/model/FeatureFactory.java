/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.model;

import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.kogito.explainability.model.domain.FeatureDomain;

/**
 * Factory class for {@link Feature}s
 */
public class FeatureFactory {

    private FeatureFactory() {
    }

    public static Feature newTextFeature(String name, String text) {
        return new Feature(name, Type.TEXT, new Value(text));
    }

    public static Feature newTextFeature(String name, String text, FeatureDomain domain) {
        return new Feature(name, Type.TEXT, new Value(text), false, domain);
    }

    public static Feature newFulltextFeature(String name, String text, Function<String, List<String>> tokenizer) {
        List<String> tokens = tokenizer.apply(text);
        List<Feature> tokenFeatures = new ArrayList<>(tokens.size());
        int featurePosition = 1;
        for (String token : tokens) {
            tokenFeatures.add(FeatureFactory.newTextFeature(name + "_" + featurePosition, token));
            featurePosition++;
        }
        return FeatureFactory.newCompositeFeature(name, tokenFeatures);
    }

    public static Feature newFulltextFeature(String name, String text) {
        return FeatureFactory.newFulltextFeature(name, text, s -> Arrays.asList(s.split(" ")));
    }

    public static Feature newCategoricalFeature(String name, String category) {
        return new Feature(name, Type.CATEGORICAL, new Value(category));
    }

    public static Feature newCategoricalFeature(String name, String category, FeatureDomain domain) {
        return new Feature(name, Type.CATEGORICAL, new Value(category), false, domain);
    }

    public static Feature newNumericalFeature(String name, Number number) {
        return new Feature(name, Type.NUMBER, new Value(number));
    }

    public static Feature newNumericalFeature(String name, Number number, FeatureDomain domain) {
        return new Feature(name, Type.NUMBER, new Value(number), false, domain);
    }

    public static Feature newBooleanFeature(String name, Boolean truthValue) {
        return new Feature(name, Type.BOOLEAN, new Value(truthValue));
    }

    public static Feature newBooleanFeature(String name, Boolean truthValue, FeatureDomain domain) {
        return new Feature(name, Type.BOOLEAN, new Value(truthValue), false, domain);
    }

    public static Feature newCurrencyFeature(String name, Currency currency) {
        return new Feature(name, Type.CURRENCY, new Value(currency));
    }

    public static Feature newCurrencyFeature(String name, Currency currency, FeatureDomain domain) {
        return new Feature(name, Type.CURRENCY, new Value(currency), false, domain);
    }

    public static Feature newBinaryFeature(String name, ByteBuffer byteBuffer) {
        return new Feature(name, Type.BINARY, new Value(byteBuffer));
    }

    public static Feature newBinaryFeature(String name, ByteBuffer byteBuffer, FeatureDomain domain) {
        return new Feature(name, Type.BINARY, new Value(byteBuffer), false, domain);
    }

    public static Feature newURIFeature(String name, URI uri) {
        return new Feature(name, Type.URI, new Value(uri));
    }

    public static Feature newURIFeature(String name, URI uri, FeatureDomain domain) {
        return new Feature(name, Type.URI, new Value(uri), false, domain);
    }

    public static Feature newDurationFeature(String name, Duration duration) {
        return new Feature(name, Type.DURATION, new Value(duration));
    }

    public static Feature newDurationFeature(String name, Duration duration, FeatureDomain domain) {
        return new Feature(name, Type.DURATION, new Value(duration), false, domain);
    }

    public static Feature newTimeFeature(String name, LocalTime time) {
        return new Feature(name, Type.TIME, new Value(time));
    }

    public static Feature newTimeFeature(String name, LocalTime time, FeatureDomain domain) {
        return new Feature(name, Type.TIME, new Value(time), false, domain);
    }

    public static Feature newVectorFeature(String name, double... doubles) {
        return new Feature(name, Type.VECTOR, new Value(doubles));
    }

    public static Feature newObjectFeature(String name, Object object) {
        return new Feature(name, Type.UNDEFINED, new Value(object));
    }

    public static Feature newObjectFeature(String name, Object object, FeatureDomain domain) {
        return new Feature(name, Type.UNDEFINED, new Value(object), false, domain);
    }

    public static Feature newCompositeFeature(String name, Map<String, Object> map) {
        List<Feature> features = new LinkedList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            features.add(parseFeatureValue(entry.getKey(), entry.getValue()));
        }
        return newCompositeFeature(name, features);
    }

    public static Feature newCompositeFeature(String name, List<Feature> features) {
        return new Feature(name, Type.COMPOSITE, new Value(features));
    }

    @SuppressWarnings("unchecked")
    public static Feature parseFeatureValue(String featureName, Object value) {
        if (value instanceof Map) {
            return newCompositeFeature(featureName, (Map<String, Object>) value);
        } else if (value instanceof double[]) {
            return newVectorFeature(featureName, (double[]) value);
        } else if (value instanceof LocalTime) {
            return newTimeFeature(featureName, (LocalTime) value);
        } else if (value instanceof Duration) {
            return newDurationFeature(featureName, (Duration) value);
        } else if (value instanceof URI) {
            return newURIFeature(featureName, (URI) value);
        } else if (value instanceof ByteBuffer) {
            return newBinaryFeature(featureName, (ByteBuffer) value);
        } else if (value instanceof Currency) {
            return newCurrencyFeature(featureName, (Currency) value);
        } else if (value instanceof Boolean) {
            return newBooleanFeature(featureName, (Boolean) value);
        } else if (value instanceof Number) {
            return newNumericalFeature(featureName, (Number) value);
        } else if (value instanceof String) {
            return newTextFeature(featureName, (String) value);
        } else if (value instanceof Feature) {
            return (Feature) value;
        } else if (value instanceof List) {
            return parseList(featureName, (List<Object>) value);
        } else {
            return newObjectFeature(featureName, value);
        }
    }

    private static Feature parseList(String featureName, List<Object> value) {
        if (!value.isEmpty()) {
            if (value.get(0) instanceof Feature) {
                @SuppressWarnings("unchecked")
                List<Feature> features = (List<Feature>) (Object) value;
                return newCompositeFeature(featureName, features);
            } else {
                List<Feature> fs = IntStream.range(0, value.size())
                        .mapToObj(i -> parseFeatureValue(featureName + "_" + i, value.get(i)))
                        .collect(Collectors.toList());
                return newCompositeFeature(featureName, fs);
            }
        } else {
            return newCompositeFeature(featureName, Collections.emptyList());
        }
    }

    /**
     * Create a copy of a {@code Feature} but with a different {@code Value}.
     *
     * @param feature the Feature to copy
     * @param value the Value to inject
     * @return a copy of the input Feature but having the given Value
     */
    public static Feature copyOf(Feature feature, Value value) {
        return new Feature(feature.getName(), feature.getType(), value);
    }
}
