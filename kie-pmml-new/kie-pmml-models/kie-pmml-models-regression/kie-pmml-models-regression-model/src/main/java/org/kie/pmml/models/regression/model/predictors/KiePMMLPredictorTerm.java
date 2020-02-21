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
package org.kie.pmml.models.regression.model.predictors;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLPredictorTerm extends KiePMMLRegressionTablePredictor {

    private static final long serialVersionUID = 4077271967051895553L;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLPredictorTerm.class.getName());
    private static final String EXPECTING_A_MAP_STRING_DOUBLE_RECEIVED = "Expecting a Map<String, Double>, received %s";
    private static final String NOT_DOUBLE_RECEIVED = "Expecting a Double, but %s";
    private List<KiePMMLRegressionTablePredictor> predictors;

    public KiePMMLPredictorTerm(String name, List<KiePMMLRegressionTablePredictor> predictors, Number coefficient, List<KiePMMLExtension> extensions) {
        super(name, coefficient, extensions);
        this.predictors = predictors;
    }

    @Override
    public Number getCoefficient() {
        return coefficient;
    }

    @Override
    public double evaluate(Object input) {
        if (!(input instanceof Map)) {
            throw new KiePMMLInternalException(String.format(EXPECTING_A_MAP_STRING_DOUBLE_RECEIVED, input.getClass().getName()));
        }
        try {
            AtomicReference<Double> result = new AtomicReference<>(1.0);
            Map<String, Double> resultMap = (Map<String, Double>) input;
            predictors.forEach(predictor -> {
                if (resultMap.containsKey(predictor.getName())) {
                    result.set(result.get() * resultMap.get(predictor.getName()));
                }
            });
            double toReturn = result.get() * coefficient.doubleValue();
            logger.debug("{} evaluate {} return {}", this, input, toReturn);
            return toReturn;
        } catch (ClassCastException e) {
            throw new KiePMMLInternalException(String.format(NOT_DOUBLE_RECEIVED, e.getMessage()));
        }
    }

    @Override
    public String toString() {
        return "KiePMMLPredictorTerm{" +
                "predictors=" + predictors +
                ", coefficient=" + coefficient +
                ", extensions=" + extensions +
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
        KiePMMLPredictorTerm that = (KiePMMLPredictorTerm) o;
        return Objects.equals(predictors, that.predictors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), predictors);
    }

    public List<KiePMMLRegressionTablePredictor> getPredictors() {
        return predictors;
    }
}
