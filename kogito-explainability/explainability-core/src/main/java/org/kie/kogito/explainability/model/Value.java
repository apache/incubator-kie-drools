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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Wrapper class for any kind of value part of a prediction input or output.
 *
 * @param <S>
 */
public class Value<S> {

    private final S underlyingObject;

    public Value(S underlyingObject) {
        this.underlyingObject = underlyingObject;
    }

    public String asString() {
        if (underlyingObject instanceof List) {
            try {
                List<Feature> composite = (List<Feature>) underlyingObject;
                return composite.stream().map(f -> f.getValue().asString()).collect(Collectors.joining(" "));
            } catch (ClassCastException ignored) {
            }
        }
        if (underlyingObject instanceof ByteBuffer) {
            ByteBuffer byteBuffer = (ByteBuffer) this.underlyingObject;
            return new String(byteBuffer.array());
        }
        return ArrayUtils.toString(underlyingObject);
    }

    public double asNumber() {
        if (underlyingObject != null) {
            try {
                return underlyingObject instanceof Boolean ? (Boolean) underlyingObject ? 1d : 0d : Double.parseDouble(asString());
            } catch (NumberFormatException nfe) {
                return Double.NaN;
            }
        } else {
            return Double.NaN;
        }
    }

    public S getUnderlyingObject() {
        return underlyingObject;
    }

    @Override
    public String toString() {
        return Objects.toString(underlyingObject);
    }

    public double[] asVector() {
        double[] doubles;
        if (underlyingObject instanceof double[]) {
            doubles = (double[]) underlyingObject;
        } else {
            if (underlyingObject instanceof String) {
                String string = (String) this.underlyingObject;
                doubles = parseVectorString(string);
            } else if (underlyingObject instanceof ByteBuffer) {
                ByteBuffer byteBuffer = (ByteBuffer) underlyingObject;
                String string = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                doubles = parseVectorString(string);
            } else {
                double v = asNumber();
                doubles = new double[1];
                doubles[0] = v;
            }
        }
        return doubles;
    }

    private double[] parseVectorString(String string) {
        double[] doubles;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonParser parser = objectMapper.createParser(string);
            // parse a double[] as a string
            Double[] ar = objectMapper.readValue(parser, Double[].class);
            doubles = Arrays.stream(ar).mapToDouble(Double::doubleValue).toArray();
        } catch (Exception e) {
            try {
                // parse a string of whitespace separated doubles
                JsonParser parser = objectMapper.createParser(string);
                MappingIterator<Double> iterator = objectMapper.readValues(parser, Double.class);
                doubles = iterator.readAll().stream().mapToDouble(Double::doubleValue).toArray();
            } catch (Exception e2) {
                // it was not possible to parse the string as a vector
                doubles = new double[0];
            }
        }
        return doubles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Value<?> value = (Value<?>) o;
        return Objects.equals(underlyingObject, value.underlyingObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(underlyingObject);
    }
}
