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
import java.util.Objects;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLCategoricalPredictor extends KiePMMLRegressionTablePredictor {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLCategoricalPredictor.class.getName());
    private Object value;

    public KiePMMLCategoricalPredictor(String name, Object value, Number coefficient, List<KiePMMLExtension> extensions) {
        super(name, coefficient, extensions);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public double evaluate(Object input) {
        double toReturn = Objects.equals(value, input) ? coefficient.doubleValue() : 0.0;
        logger.debug("{} evaluate {} return {}", this, input, toReturn);
        return toReturn;
    }

    @Override
    public String toString() {
        return "KiePMMLCategoricalPredictor{" +
                "value=" + value +
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
        KiePMMLCategoricalPredictor that = (KiePMMLCategoricalPredictor) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
