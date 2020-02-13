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

package org.kie.pmml.models.regression.api.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLCategoricalPredictor;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLNumericPredictor;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLPredictorTerm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class KiePMMLRegressionTableTest {

    private static final Integer INTERCEPT = 23;
    private static final Object TARGET_CATEGORY = "TARGET_CATEGORY";
    private static final List<KiePMMLExtension> EXTENSIONS = new ArrayList<>();
    private static final Set<KiePMMLNumericPredictor> NUMERIC_PREDICTORS = new HashSet<>();
    private static final List<KiePMMLCategoricalPredictor> CATEGORICAL_PREDICTORS = new ArrayList<>();
    private static final Set<KiePMMLPredictorTerm> PREDICTOR_TERMS = new HashSet<>();

    @Test
    public void buildWithAll() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder()
                .withIntercept(INTERCEPT)
                .withCategoricalPredictors(CATEGORICAL_PREDICTORS)
                .withExtensions(EXTENSIONS)
                .withNumericPredictors(NUMERIC_PREDICTORS)
                .withPredictorTerms(PREDICTOR_TERMS)
                .withTargetCategory(TARGET_CATEGORY)
                .build();
        assertNotNull(retrieved);
        assertEquals(INTERCEPT, retrieved.getIntercept());
        assertEquals(CATEGORICAL_PREDICTORS, retrieved.getCategoricalPredictors());
        assertEquals(EXTENSIONS, retrieved.getExtensions());
        assertEquals(NUMERIC_PREDICTORS, retrieved.getNumericPredictors());
        assertEquals(PREDICTOR_TERMS, retrieved.getPredictorTerms());
        assertEquals(TARGET_CATEGORY, retrieved.getTargetCategory());
    }

    @Test
    public void buildWithIntercept() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder()
                .withIntercept(INTERCEPT)
                .build();
        commonVerify(INTERCEPT, retrieved);
    }

    @Test
    public void buildWithTargetCategory() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder()
                .withTargetCategory(TARGET_CATEGORY)
                .build();
        commonVerify(TARGET_CATEGORY, retrieved);
    }

    @Test
    public void buildWithExtensions() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder()
                .withExtensions(EXTENSIONS)
                .build();
        commonVerify(EXTENSIONS, retrieved);
    }

    @Test
    public void buildWithNumericPredictors() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder()
                .withNumericPredictors(NUMERIC_PREDICTORS)
                .build();
        commonVerify(NUMERIC_PREDICTORS, retrieved);
    }

    @Test
    public void buildWithCategoricalPredictors() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder()
                .withCategoricalPredictors(CATEGORICAL_PREDICTORS)
                .build();
        commonVerify(CATEGORICAL_PREDICTORS, retrieved);
    }

    @Test
    public void buildWithPredictorTerms() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder()
                .withPredictorTerms(PREDICTOR_TERMS)
                .build();
        commonVerify(PREDICTOR_TERMS, retrieved);
    }

    private void commonVerify(Object notNull, KiePMMLRegressionTable retrieved) {
        if (INTERCEPT == notNull) {
            assertEquals(notNull, retrieved.getIntercept());
        } else {
            assertNull(retrieved.getIntercept());
        }
        if (TARGET_CATEGORY == notNull) {
            assertEquals(notNull, retrieved.getTargetCategory());
        } else {
            assertNull(retrieved.getTargetCategory());
        }
        if (EXTENSIONS == notNull) {
            assertEquals(notNull, retrieved.getExtensions());
        } else {
            assertNull(retrieved.getExtensions());
        }
        if (NUMERIC_PREDICTORS == notNull) {
            assertEquals(notNull, retrieved.getNumericPredictors());
        } else {
            assertNull(retrieved.getNumericPredictors());
        }
        if (CATEGORICAL_PREDICTORS == notNull) {
            assertEquals(notNull, retrieved.getCategoricalPredictors());
        } else {
            assertNull(retrieved.getCategoricalPredictors());
        }
        if (PREDICTOR_TERMS == notNull) {
            assertEquals(notNull, retrieved.getPredictorTerms());
        } else {
            assertNull(retrieved.getPredictorTerms());
        }
    }
}