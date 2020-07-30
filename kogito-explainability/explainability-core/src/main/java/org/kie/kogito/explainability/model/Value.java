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

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;

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
        return "Value{" + underlyingObject + '}';
    }

    public double[] asVector() {
        double[] doubles;
        if (underlyingObject instanceof double[]) {
            doubles = (double[]) underlyingObject;
        } else {
            if (underlyingObject instanceof String) {
                String[] tokens = ((String) underlyingObject).split(",?\\s+");
                int noOfWords = tokens.length;
                doubles = new double[noOfWords];
                // parse string encoded vector
                if (Arrays.stream(tokens).allMatch(s -> s.matches("-?\\d+(\\.\\d+)?"))) {
                    for (int i = 0; i < tokens.length; i++) {
                        doubles[i] = Double.parseDouble(tokens[i]);
                    }
                } else { // or make a vector of 1s
                    Arrays.fill(doubles, 1);
                }
            } else {
                double v = asNumber();
                doubles = new double[1];
                doubles[0] = v;
            }
            // FAI-234 : handle parsing of different underlyingObject types as vectors (e.g. ByteBuffer, etc.)
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
