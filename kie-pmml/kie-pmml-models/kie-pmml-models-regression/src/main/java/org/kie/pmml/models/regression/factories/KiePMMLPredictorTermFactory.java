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

import org.dmg.pmml.regression.PredictorTerm;
import org.kie.pmml.api.model.regression.KiePMMLPredictorTerm;

import static org.kie.pmml.models.core.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.models.core.factories.KiePMMLFieldRefFactory.getKiePMMLFieldRefs;

public class KiePMMLPredictorTermFactory {

    private KiePMMLPredictorTermFactory() {
    }

    public static Set<KiePMMLPredictorTerm> getKiePMMLPredictorTerms(List<PredictorTerm> predictorTerms) {
        return predictorTerms.stream().map(KiePMMLPredictorTermFactory::getKiePMMLPredictorTerm).collect(Collectors.toSet());
    }

    public static KiePMMLPredictorTerm getKiePMMLPredictorTerm(PredictorTerm predictorTerm) {
        return new KiePMMLPredictorTerm(predictorTerm.getName().getValue(),
                                        getKiePMMLFieldRefs(predictorTerm.getFieldRefs()),
                                        predictorTerm.getCoefficient(),
                                        getKiePMMLExtensions(predictorTerm.getExtensions()));
    }
}
