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
package org.kie.pmml.api.model.regression.predictors;

import java.util.Objects;

import org.kie.pmml.api.exceptions.KiePMMLException;

public class KiePMMLNumericPredictor extends KiePMMLRegressionTablePredictor {

    private static final long serialVersionUID = -1694608925806912507L;
    private int exponent;

    public KiePMMLNumericPredictor(String name, int exponent, Number coefficient) {
        super(name, coefficient);
        this.exponent = exponent;
    }

    public int getExponent() {
        return exponent;
    }

    @Override
    public double evaluate(Object input) throws KiePMMLException {
        if (!(input instanceof Number)) {
            throw new KiePMMLException("Expected a Number, received a " + input.getClass().getName());
        }
        double inputDouble = ((Number)input).doubleValue();
        return exponent == 1 ? inputDouble * coefficient.doubleValue() : Math.pow(inputDouble, exponent) * coefficient.doubleValue();
    }

    @Override
    public String toString() {
        return "KiePMMLNumericPredictor{" +
                "exponent=" + exponent +
                ", coefficient=" + coefficient +
                ", name='" + name + '\'' +
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
        KiePMMLNumericPredictor that = (KiePMMLNumericPredictor) o;
        return exponent == that.exponent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), exponent);
    }
}
