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
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.lang3.ArrayUtils;
import org.kie.kogito.explainability.utils.DataUtils;

/**
 * Allowed data types.
 */
public enum Type {

    TEXT("text") {
        @Override
        public Value<?> drop(Value<?> value) {
            return new Value<>("");
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            return new Value<>("");
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            return encodeEquals(target, values);
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            return new Value<>(randomString(perturbationContext.getRandom()));
        }
    },

    CATEGORICAL("categorical") {
        @Override
        public Value<?> drop(Value<?> value) {
            return new Value<>("");
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            String category = value.asString();
            if (!"0".equals(category)) {
                category = "0";
            } else {
                category = "1";
            }
            return new Value<>(category);
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            return encodeEquals(target, values);
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            return new Value<>(String.valueOf(perturbationContext.getRandom().nextInt(4)));
        }
    },

    BINARY("binary") {
        @Override
        public Value<?> drop(Value<?> value) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(0);
            return new Value<>(byteBuffer);
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            if (value.getUnderlyingObject() instanceof ByteBuffer) {
                ByteBuffer currentBuffer = (ByteBuffer) value.getUnderlyingObject();
                byte[] copy = new byte[currentBuffer.array().length];
                int maxPerturbationSize = Math.min(copy.length, Math.max((int) (copy.length * 0.5), perturbationContext.getNoOfPerturbations()));
                System.arraycopy(currentBuffer.array(), 0, copy, 0, currentBuffer.array().length);
                int[] indexes = perturbationContext.getRandom().ints(0, copy.length)
                        .limit(maxPerturbationSize).toArray();
                for (int index : indexes) {
                    copy[index] = 0;
                }
                return new Value<>(ByteBuffer.wrap(copy));
            } else {
                ByteBuffer byteBuffer = ByteBuffer.allocate(0);
                return new Value<>(byteBuffer);
            }
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            return encodeEquals(target, values);
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            byte[] bytes = new byte[8];
            perturbationContext.getRandom().nextBytes(bytes);
            return new Value<>(ByteBuffer.wrap(bytes));
        }
    },

    NUMBER("number") {
        private static final double CLUSTER_THRESHOLD = 1e-1;

        @Override
        public Value<?> drop(Value<?> value) {
            if (value.asNumber() == 0) {
                value = new Value<>(Double.NaN);
            } else {
                value = new Value<>(0d);
            }
            return value;
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            double originalFeatureValue = value.asNumber();
            boolean intValue = originalFeatureValue % 1 == 0;

            // sample from a standard normal distribution and center around feature value
            double normalDistributionSample = perturbationContext.getRandom().nextGaussian();
            if (originalFeatureValue != 0d) {
                double stDev = originalFeatureValue * 0.01; // set std dev at 1% of feature value
                normalDistributionSample = normalDistributionSample * originalFeatureValue + stDev;
            }
            if (intValue) {
                normalDistributionSample = (int) normalDistributionSample;
                if (normalDistributionSample == originalFeatureValue) {
                    normalDistributionSample = (int) normalDistributionSample + 1d;
                }
            }
            return new Value<>(normalDistributionSample);
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            // find maximum and minimum values
            double[] doubles = new double[values.length + 1];
            int valueIndex = 0;
            for (Value<?> v : values) {
                doubles[valueIndex] = Double.isNaN(v.asNumber()) ? 0 : v.asNumber();
                valueIndex++;
            }
            double originalValue = Double.isNaN(target.asNumber()) ? 0 : target.asNumber();
            doubles[valueIndex] = originalValue; // include target number in feature scaling
            double min = DoubleStream.of(doubles).min().orElse(Double.MIN_VALUE);
            double max = DoubleStream.of(doubles).max().orElse(Double.MAX_VALUE);

            // feature scaling
            List<Double> scaledValues = DoubleStream.of(doubles).map(d -> (d - min) / (max - min)).boxed().collect(Collectors.toList());
            double scaledOriginalValue = scaledValues.remove(valueIndex); // extract the scaled original value (it must not appear in encoded values)

            // kernel based clustering
            double sigma = params.getNumericTypeClusterGaussianFilterWidth();
            double threshold = DataUtils.gaussianKernel(scaledOriginalValue, scaledOriginalValue, sigma);
            List<Double> clusteredValues = scaledValues.stream()
                    .map(d -> DataUtils.gaussianKernel(d, scaledOriginalValue, sigma)).collect(Collectors.toList());
            List<Double> encodedValues = clusteredValues.stream()
                    .map(d -> (Math.abs(d - threshold) < params.getNumericTypeClusterThreshold()) ? 1d : 0d).collect(Collectors.toList());

            return encodedValues.stream().map(d -> new double[] { d }).collect(Collectors.toList());
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            return new Value<>(perturbationContext.getRandom().nextDouble());
        }
    },

    BOOLEAN("boolean") {
        @Override
        public Value<?> drop(Value<?> value) {
            return new Value<>(null);
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            return new Value<>(!Boolean.parseBoolean(value.asString()));
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            return encodeEquals(target, values);
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            return new Value<>(perturbationContext.getRandom().nextBoolean());
        }
    },

    URI("uri") {
        @Override
        public Value<?> drop(Value<?> value) {
            return new Value<>(java.net.URI.create(""));
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            String uriAsString = value.asString();
            java.net.URI uri = java.net.URI.create(uriAsString);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (perturbationContext.getRandom().nextBoolean()) {
                if ("localhost".equalsIgnoreCase(host)) {
                    host = "0.0.0.0";
                } else {
                    host = "localhost";
                }
            }
            String path = uri.getPath();
            if (perturbationContext.getRandom().nextBoolean()) {
                path = "";
            }
            String fragment = uri.getFragment();
            if (perturbationContext.getRandom().nextBoolean()) {
                if (fragment != null && fragment.length() > 0) {
                    fragment = "";
                } else { // generate a random string
                    fragment = Long.toHexString(Double.doubleToLongBits(perturbationContext.getRandom().nextDouble()));
                }
            }
            java.net.URI newURI;
            try {
                newURI = new URI(scheme, host, path, fragment);
                if (uri.equals(newURI)) { // to avoid "unfortunate" cases where no URI parameter has been perturbed
                    newURI = java.net.URI.create("");
                }
            } catch (URISyntaxException e) {
                newURI = java.net.URI.create("");
            }
            return new Value<>(newURI);
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            return encodeEquals(target, values);
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            String uriString = "http://" + randomString(perturbationContext.getRandom()) + ".com";
            URI uri = java.net.URI.create(uriString);
            return new Value<>(uri);
        }
    },

    TIME("time") {
        @Override
        public Value<?> drop(Value<?> value) {
            return new Value<>(null);
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            LocalTime featureValue;
            try {
                featureValue = LocalTime.parse(value.asString());
            } catch (DateTimeException dateTimeException) {
                featureValue = LocalTime.now();
            }
            return new Value<>(featureValue.minusHours(1L + perturbationContext.getRandom().nextInt(23)));
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            return encodeEquals(target, values);
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            return new Value<>(LocalTime.of(perturbationContext.getRandom().nextInt(23), perturbationContext.getRandom().nextInt(59)));
        }
    },

    DURATION("duration") {
        @Override
        public Value<?> drop(Value<?> value) {
            return new Value<>(null);
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            Duration duration;
            try {
                duration = Duration.parse(value.asString());
            } catch (DateTimeParseException parseException) {
                duration = Duration.of(0, ChronoUnit.HOURS);
            }
            duration = duration.plus(1L + perturbationContext.getRandom().nextInt(23), ChronoUnit.HOURS);
            return new Value<>(duration);
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            return encodeEquals(target, values);
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            return new Value<>(Duration.ofDays(perturbationContext.getRandom().nextInt(30)));
        }
    },

    VECTOR("vector") {
        @Override
        public Value<?> drop(Value<?> value) {
            double[] values = value.asVector();
            if (values.length > 0) {
                Arrays.fill(values, 0);
            }
            return new Value<>(values);
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            // set a number of non zero values to zero (or decrease them by 1)
            double[] vector = value.asVector();
            double[] values = Arrays.copyOf(vector, vector.length);
            if (values.length > 1) {
                int maxPerturbationSize = Math.min(vector.length, Math.max((int) (vector.length * 0.5), perturbationContext.getNoOfPerturbations()));
                int[] indexes = perturbationContext.getRandom().ints(0, vector.length)
                        .limit(maxPerturbationSize).toArray();
                for (int idx : indexes) {
                    if (values[idx] != 0) {
                        values[idx] = 0;
                    } else {
                        values[idx]--;
                    }
                }
            }
            return new Value<>(values);
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            return encodeEquals(target, values);
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            double[] vector = new double[5];
            for (int i = 0; i < vector.length; i++) {
                vector[i] = perturbationContext.getRandom().nextDouble();
            }
            return new Value<>(vector);
        }
    },

    UNDEFINED("undefined") {
        @Override
        public Value<?> drop(Value<?> value) {
            if (value.getUnderlyingObject() instanceof Feature) {
                Feature underlyingObject = (Feature) value.getUnderlyingObject();
                value = new Value<>(FeatureFactory.copyOf(underlyingObject, underlyingObject.getType().drop(underlyingObject.getValue())));
            } else {
                value = new Value<>(null);
            }
            return value;
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            if (value.getUnderlyingObject() instanceof Feature) {
                Feature underlyingObject = (Feature) value.getUnderlyingObject();
                Type type = underlyingObject.getType();
                Value<?> perturbedValue = type.perturb(underlyingObject.getValue(), perturbationContext);
                value = new Value<>(FeatureFactory.copyOf(underlyingObject, perturbedValue));
            } else {
                value = new Value<>(null);
            }
            return value;
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            return encodeEquals(target, values);
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            return new Value<>(new Object());
        }
    },

    COMPOSITE("composite") {
        @Override
        public Value<?> drop(Value<?> value) {
            List<Feature> composite = getFeatures(value);
            List<Feature> newFeatures = new ArrayList<>(composite.size());
            for (Feature f : composite) {
                newFeatures.add(FeatureFactory.copyOf(f, f.getType().drop(f.getValue())));
            }
            return new Value<>(newFeatures);
        }

        private List<Feature> getFeatures(Value<?> value) {
            List<Feature> features;
            try {
                features = (List<Feature>) value.getUnderlyingObject();
            } catch (ClassCastException cce) {
                features = new LinkedList<>();
            }
            return features;
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            List<Feature> composite = getFeatures(value);
            List<Feature> newList = DataUtils.perturbFeatures(composite, perturbationContext);
            return new Value<>(newList);
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            List<Feature> composite = getFeatures(target);
            int i = 0;
            List<List<double[]>> multiColumns = new LinkedList<>();
            for (Feature f : composite) {
                int finalI = i;
                List<double[]> subColumn = f.getType().encode(params, f.getValue(), Arrays.stream(values)
                        .map(v -> (List<Feature>) v.getUnderlyingObject())
                        .map(l -> l.get(finalI).getValue()).toArray(Value<?>[]::new));
                multiColumns.add(subColumn);
                i++;
            }
            List<double[]> result = new LinkedList<>();

            for (int j = 0; j < values.length; j++) {
                List<Double> vector = new LinkedList<>();
                for (List<double[]> multiColumn : multiColumns) {
                    double[] doubles = multiColumn.get(j);
                    vector.addAll(Arrays.asList(ArrayUtils.toObject(doubles)));
                }
                double[] doubles = new double[vector.size()];
                for (int d = 0; d < doubles.length; d++) {
                    doubles[d] = vector.get(d);
                }
                result.add(doubles);
            }
            return result;
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            Type[] types = Type.values();
            List<Object> values = new LinkedList<>();
            Type nestedType = types[perturbationContext.getRandom().nextInt(types.length - 1)];
            for (int i = 0; i < 5; i++) {
                Feature f = new Feature("f_" + i, nestedType, nestedType.randomValue(perturbationContext));
                values.add(f);
            }
            return new Value<>(values);
        }
    },

    CURRENCY("currency") {
        @Override
        public Value<?> drop(Value<?> value) {
            return new Value<>(null);
        }

        @Override
        public Value<?> perturb(Value<?> value, PerturbationContext perturbationContext) {
            List<Currency> availableCurrencies = new ArrayList<>(Currency.getAvailableCurrencies());
            if (value.getUnderlyingObject() instanceof Currency) {
                Currency current = (Currency) value.getUnderlyingObject();
                availableCurrencies.removeIf(current::equals);
            }
            return new Value<>(availableCurrencies.get(perturbationContext.getRandom().nextInt(availableCurrencies.size())));
        }

        @Override
        public List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values) {
            return encodeEquals(target, values);
        }

        @Override
        public Value<?> randomValue(PerturbationContext perturbationContext) {
            ArrayList<Currency> currencies = new ArrayList<>(Currency.getAvailableCurrencies());
            return new Value<>(currencies.get(perturbationContext.getRandom().nextInt(currencies.size() - 1)));
        }
    };

    static List<double[]> encodeEquals(Value<?> target, Value<?>[] values) {
        List<double[]> result = new ArrayList<>(values.length);
        for (Value<?> value : values) {
            double[] data = new double[1];
            if (target.getUnderlyingObject().equals(value.getUnderlyingObject())) {
                data[0] = 1d;
            } else {
                data[0] = 0d;
            }
            result.add(data);
        }
        return result;
    }

    private final String value;

    Type(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Drop a given {@code Value}. Implementations of this method should generate a new {@code Value} whose
     * {@code Value#getUnderlyingObject} should represent a non existent/empty/void/dropped {@code Type}-specific instance.
     *
     * @param value the value to drop
     * @return the dropped value
     */
    public abstract Value<?> drop(Value<?> value);

    /**
     * Perturb a {@code Value}. Implementations of this method should generate a new {@code Value} whose
     * {@code Value#getUnderlyingObject} should represent a perturbed/changed copy of the original value.
     *
     * @param value the value to perturb
     * @param perturbationContext the context holding metadata about how perturbations should be performed
     * @return the perturbed value
     */
    public abstract Value<?> perturb(Value<?> value, PerturbationContext perturbationContext);

    /**
     * Encode some {@code Value}s with respect to a target value. Implementations of this method should generate a list
     * of vectors for each value. The target value represents the "encoding reference" to be used to decide how to encode
     * each value, e.g. values that are equals to the target one might get encoded as {@code double[1]{1d}} whereas
     * different values (wrt to {@code target}) might get encoded as {@code double[1]{0d}}.
     *
     * @param target the target reference value
     * @param values the values to be encoded
     * @return a list of vectors
     */
    public abstract List<double[]> encode(EncodingParams params, Value<?> target, Value<?>... values);

    /**
     * Generate a random {@code Value} (depending on the underlying {@code Type}).
     *
     * @param perturbationContext context object used to randomize values
     * @return a random Value
     */
    public abstract Value<?> randomValue(PerturbationContext perturbationContext);

    private static String randomString(Random random) {
        return Long.toHexString(Double.doubleToLongBits(random.nextDouble()));
    }
}