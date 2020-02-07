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
import java.util.stream.Collectors;

import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.api.model.regression.KiePMMLRegressionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.library.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.models.regression.factories.KiePMMLNumericPredictorFactory.getKiePMMLNumericPredictors;

public class KiePMMLRegressionTableFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLRegressionTableFactory.class.getName());

    public static List<KiePMMLRegressionTable> getRegressionTables(List<RegressionTable> regressionTables) {
        logger.info("getRegressionTables {}", regressionTables);
        return regressionTables.stream().map(KiePMMLRegressionTableFactory :: getRegressionTable).collect(Collectors.toList());
    }

    public static KiePMMLRegressionTable getRegressionTable(RegressionTable regressionTable) {
        logger.info("getRegressionTable {}", regressionTable);
        return KiePMMLRegressionTable.builder()
                .withIntercept(regressionTable.getIntercept())
                .withCategoricalPredictors(KiePMMLCategoricalPredictorFactory.getKiePMMLCategoricalPredictors(regressionTable.getCategoricalPredictors()))
                .withExtensions(getKiePMMLExtensions(regressionTable.getExtensions()))
                .withNumericPredictors(getKiePMMLNumericPredictors(regressionTable.getNumericPredictors()))
                .withPredictorTerms(KiePMMLPredictorTermFactory.getKiePMMLPredictorTerms(regressionTable.getPredictorTerms()))
                .withTargetCategory(regressionTable.getTargetCategory())
                .build();
    }


}
