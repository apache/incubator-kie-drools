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

package org.kie.pmml.models.regression.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.models.regression.model.predictors.KiePMMLCategoricalPredictor;
import org.kie.pmml.models.regression.model.predictors.KiePMMLNumericPredictor;
import org.kie.pmml.models.regression.model.predictors.KiePMMLPredictorTerm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KiePMMLRegressionTableTest {

    private static final String MODEL_NAME = "REGRESSION_MODEL";
    private static final Integer INTERCEPT = 23;
    private static final Object TARGET_CATEGORY = "TARGET_CATEGORY";
    private static final List<KiePMMLExtension> EXTENSIONS = new ArrayList<>();
    private static final Set<KiePMMLNumericPredictor> NUMERIC_PREDICTORS = new HashSet<>();
    private static final Set<KiePMMLCategoricalPredictor> CATEGORICAL_PREDICTORS = new HashSet<>();
    private static final Set<KiePMMLPredictorTerm> PREDICTOR_TERMS = new HashSet<>();

    @Test
    public void buildWithAll() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder(MODEL_NAME, Collections.emptyList(), INTERCEPT)
                .withCategoricalPredictors(CATEGORICAL_PREDICTORS)
                .withNumericPredictors(NUMERIC_PREDICTORS)
                .withPredictorTerms(PREDICTOR_TERMS)
                .withTargetCategory(TARGET_CATEGORY)
                .build();
        assertNotNull(retrieved);
        assertEquals(INTERCEPT, retrieved.getIntercept());
        assertTrue(retrieved.getCategoricalPredictors().isPresent());
        assertEquals(CATEGORICAL_PREDICTORS, retrieved.getCategoricalPredictors().get());
        assertEquals(EXTENSIONS, retrieved.getExtensions());
        assertTrue(retrieved.getNumericPredictors().isPresent());
        assertEquals(NUMERIC_PREDICTORS, retrieved.getNumericPredictors().get());
        assertTrue(retrieved.getPredictorTerms().isPresent());
        assertEquals(PREDICTOR_TERMS, retrieved.getPredictorTerms().get());
        assertTrue(retrieved.getTargetCategory().isPresent());
        assertEquals(TARGET_CATEGORY, retrieved.getTargetCategory().get());
    }

    @Test
    public void buildWithIntercept() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder(MODEL_NAME, Collections.emptyList(), INTERCEPT)
                .build();
        commonVerify(INTERCEPT, retrieved);
    }

    @Test
    public void buildWithTargetCategory() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder(MODEL_NAME, Collections.emptyList(), INTERCEPT)
                .withTargetCategory(TARGET_CATEGORY)
                .build();
        commonVerify(TARGET_CATEGORY, retrieved);
    }

    @Test
    public void buildWithNumericPredictors() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder(MODEL_NAME, Collections.emptyList(), INTERCEPT)
                .withNumericPredictors(NUMERIC_PREDICTORS)
                .build();
        commonVerify(NUMERIC_PREDICTORS, retrieved);
    }

    @Test
    public void buildWithCategoricalPredictors() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder(MODEL_NAME, Collections.emptyList(), INTERCEPT)
                .withCategoricalPredictors(CATEGORICAL_PREDICTORS)
                .build();
        commonVerify(CATEGORICAL_PREDICTORS, retrieved);
    }

    @Test
    public void buildWithPredictorTerms() {
        final KiePMMLRegressionTable retrieved = KiePMMLRegressionTable.builder(MODEL_NAME, Collections.emptyList(), INTERCEPT)
                .withPredictorTerms(PREDICTOR_TERMS)
                .build();
        commonVerify(PREDICTOR_TERMS, retrieved);
    }

    private void commonVerify(Object notNull, KiePMMLRegressionTable retrieved) {
        if (INTERCEPT == notNull) {
            assertEquals(notNull, retrieved.getIntercept());
        }
        if (TARGET_CATEGORY == notNull) {
            assertTrue(retrieved.getTargetCategory().isPresent());
            assertEquals(notNull, retrieved.getTargetCategory().get());
        } else {
            assertFalse(retrieved.getTargetCategory().isPresent());
        }
        if (EXTENSIONS == notNull) {
            assertEquals(notNull, retrieved.getExtensions());
        }
        if (NUMERIC_PREDICTORS == notNull) {
            assertTrue(retrieved.getNumericPredictors().isPresent());
            assertEquals(notNull, retrieved.getNumericPredictors().get());
        } else {
            assertFalse(retrieved.getNumericPredictors().isPresent());
        }
        if (CATEGORICAL_PREDICTORS == notNull) {
            assertTrue(retrieved.getCategoricalPredictors().isPresent());
            assertEquals(notNull, retrieved.getCategoricalPredictors().get());
        } else {
            assertFalse(retrieved.getCategoricalPredictors().isPresent());
        }
        if (PREDICTOR_TERMS == notNull) {
            assertTrue(retrieved.getPredictorTerms().isPresent());
            assertEquals(notNull, retrieved.getPredictorTerms().get());
        } else {
            assertFalse(retrieved.getPredictorTerms().isPresent());
        }
    }
}