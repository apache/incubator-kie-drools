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
package org.kie.pmml.models.regression.factories;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dmg.pmml.regression.NumericPredictor;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLNumericPredictor;

public class KiePMMLNumericPredictorFactory {

    private KiePMMLNumericPredictorFactory() {
    }

    public static Set<KiePMMLNumericPredictor> getKiePMMLNumericPredictors(List<NumericPredictor> numericPredictors) {
        return numericPredictors.stream().map(KiePMMLNumericPredictorFactory::getKiePMMLNumericPredictor).collect(Collectors.toSet());
    }

    public static KiePMMLNumericPredictor getKiePMMLNumericPredictor(NumericPredictor numericPredictor) {
        return new KiePMMLNumericPredictor(numericPredictor.getName().getValue(),
                                           numericPredictor.getExponent(),
                                           numericPredictor.getCoefficient());
    }
}
