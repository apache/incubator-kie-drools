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

import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.KiePMMLExtensionedTerm;
import org.kie.pmml.commons.model.abstracts.KiePMMLTerm;

public abstract class KiePMMLRegressionTablePredictor extends KiePMMLExtensionedTerm {

    private static final long serialVersionUID = -3937947927611073965L;
    protected final Number coefficient;

    public KiePMMLRegressionTablePredictor(String name, Number coefficient, List<KiePMMLExtension> extensions) {
        super(name, extensions);
        this.coefficient = coefficient;
    }

    @Override
    public Number getCoefficient() {
        return coefficient;
    }

    /**
     * Returns the predictor value of the current instance for the given input
     * @param input
     * @return
     * @throws KiePMMLException
     */
    public abstract double evaluate(Object input) throws KiePMMLException;

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
        KiePMMLRegressionTablePredictor that = (KiePMMLRegressionTablePredictor) o;
        return Objects.equals(coefficient, that.coefficient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), coefficient);
    }
}
