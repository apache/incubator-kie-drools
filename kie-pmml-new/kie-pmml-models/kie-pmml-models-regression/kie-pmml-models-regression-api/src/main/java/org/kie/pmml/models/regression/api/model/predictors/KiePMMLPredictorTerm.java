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
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.interfaces.FunctionalWrapperFactory.throwingConsumerWrapper;

public class KiePMMLPredictorTerm extends KiePMMLRegressionTablePredictor {

    private static final long serialVersionUID = 4077271967051895553L;
    private List<KiePMMLRegressionTablePredictor> predictors;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLPredictorTerm.class.getName());

    public KiePMMLPredictorTerm(String name, List<KiePMMLRegressionTablePredictor> predictors, Number coefficient, List<KiePMMLExtension> extensions) {
        super(name,  coefficient, extensions);
        this.predictors = predictors;
    }

    @Override
    public Number getCoefficient() {
        return coefficient;
    }

    @Override
    public double evaluate(Object input) throws KiePMMLException {
        if (!(input instanceof Map)) {
            throw new KiePMMLException("Expecting a Map<String, Double>, received " + input.getClass().getName());
        }
        Map<String, Double> resultMap;
        try {
            resultMap = (Map<String, Double>) input;
        } catch (ClassCastException e) {
            throw new KiePMMLException("Expecting a Map<String, Double>, received " + input.getClass().getName());
        }
        AtomicReference<Double> result = new AtomicReference<>(1.0);
        predictors.forEach(predictor -> {
            if (resultMap.containsKey(predictor.getName())) {
                result.set(result.get() * resultMap.get(predictor.getName()));
            }

        });
        double toReturn =result.get() * coefficient.doubleValue();
        logger.info("{} evaluate {} return {}", this, input, toReturn);
        return toReturn;
    }

    public List<KiePMMLRegressionTablePredictor> getPredictors() {
        return predictors;
    }
}
