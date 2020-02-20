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
package org.kie.pmml.models.regression.compiler.factories;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.model.predictors.KiePMMLCategoricalPredictor;
import org.kie.pmml.models.regression.model.predictors.KiePMMLNumericPredictor;
import org.kie.pmml.models.regression.model.predictors.KiePMMLRegressionTablePredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLCategoricalPredictorFactory.getKiePMMLCategoricalPredictors;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLNumericPredictorFactory.getKiePMMLNumericPredictors;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLPredictorTermFactory.getKiePMMLPredictorTerms;

public class KiePMMLRegressionTableFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLRegressionTableFactory.class.getName());

    public static List<KiePMMLRegressionTable> getRegressionTables(List<RegressionTable> regressionTables) {
        logger.debug("getRegressionTables {}", regressionTables);
        return regressionTables.stream().map(KiePMMLRegressionTableFactory::getRegressionTable).collect(Collectors.toList());
    }

    public static KiePMMLRegressionTable getRegressionTable(RegressionTable regressionTable) {
        logger.debug("getRegressionTable {}", regressionTable);
        final Set<KiePMMLCategoricalPredictor> categoricalPredictors = getKiePMMLCategoricalPredictors(regressionTable.getCategoricalPredictors());
        final Set<KiePMMLNumericPredictor> numericPredictors = getKiePMMLNumericPredictors(regressionTable.getNumericPredictors());
        final Set<KiePMMLRegressionTablePredictor> numericCategoricalPredictors = new HashSet<>();
        numericCategoricalPredictors.addAll(categoricalPredictors);
        numericCategoricalPredictors.addAll(numericPredictors);
        return KiePMMLRegressionTable.builder(regressionTable.getIntercept())
                .withExtensions(getKiePMMLExtensions(regressionTable.getExtensions()))
                .withCategoricalPredictors(categoricalPredictors)
                .withNumericPredictors(numericPredictors)
                .withPredictorTerms(getKiePMMLPredictorTerms(regressionTable.getPredictorTerms(), numericCategoricalPredictors))
                .withTargetCategory(regressionTable.getTargetCategory())
                .build();
    }
}
