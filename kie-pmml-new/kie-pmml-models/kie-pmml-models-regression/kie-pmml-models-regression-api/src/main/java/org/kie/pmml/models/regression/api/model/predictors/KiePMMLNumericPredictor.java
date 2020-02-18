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
package org.kie.pmml.models.regression.api.model.predictors;

import java.util.List;
import java.util.Objects;

import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLNumericPredictor extends KiePMMLRegressionTablePredictor {

    private static final long serialVersionUID = -1694608925806912507L;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLNumericPredictor.class.getName());
    private int exponent;

    public KiePMMLNumericPredictor(String name, int exponent, Number coefficient, List<KiePMMLExtension> extensions) {
        super(name, coefficient, extensions);
        this.exponent = exponent;
    }

    public int getExponent() {
        return exponent;
    }

    @Override
    public double evaluate(Object input) {
        if (!(input instanceof Number)) {
            throw new KiePMMLInternalException("Expected a Number, received a " + input.getClass().getName());
        }
        double inputDouble = ((Number) input).doubleValue();
        double toReturn = exponent == 1 ? inputDouble * coefficient.doubleValue() : Math.pow(inputDouble, exponent) * coefficient.doubleValue();
        logger.debug("{} evaluate {} return {}", this, input, toReturn);
        return toReturn;
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
