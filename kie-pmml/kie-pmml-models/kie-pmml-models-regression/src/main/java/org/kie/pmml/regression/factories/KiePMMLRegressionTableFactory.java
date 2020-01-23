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
package org.kie.pmml.regression.factories;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.api.model.regression.KiePMMLRegressionTable;

import static org.kie.pmml.models.core.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.regression.factories.KiePMMLCategoricalPredictorFactory.getKiePMMLCategoricalPredictors;
import static org.kie.pmml.regression.factories.KiePMMLNumericPredictorFactory.getKiePMMLNumericPredictors;
import static org.kie.pmml.regression.factories.KiePMMLPredictorTermFactory.getKiePMMLPredictorTerms;

public class KiePMMLRegressionTableFactory {

    private static final Logger log = Logger.getLogger(KiePMMLRegressionTableFactory.class.getName());

    public static List<KiePMMLRegressionTable> getRegressionTables(List<RegressionTable> regressionTables) {
        log.info("getRegressionTables " + regressionTables);
        return regressionTables.stream().map(KiePMMLRegressionTableFactory :: getRegressionTable).collect(Collectors.toList());
    }

    public static KiePMMLRegressionTable getRegressionTable(RegressionTable regressionTable) {
        log.info("getRegressionTable " + regressionTable);
        return KiePMMLRegressionTable.builder()
                .withIntercept(regressionTable.getIntercept())
                .withCategoricalPredictors(getKiePMMLCategoricalPredictors(regressionTable.getCategoricalPredictors()))
                .withExtensions(getKiePMMLExtensions(regressionTable.getExtensions()))
                .withNumericPredictors(getKiePMMLNumericPredictors(regressionTable.getNumericPredictors()))
                .withPredictorTerms(getKiePMMLPredictorTerms(regressionTable.getPredictorTerms()))
                .withTargetCategory(regressionTable.getTargetCategory())
                .build();
    }


}
