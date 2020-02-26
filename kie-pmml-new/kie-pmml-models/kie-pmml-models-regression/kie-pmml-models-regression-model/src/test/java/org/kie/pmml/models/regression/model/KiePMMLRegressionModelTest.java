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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.models.regression.model.enums.MODEL_TYPE;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KiePMMLRegressionModelTest {

    private static final String MODEL_NAME = "MODEL_NAME";
    private static final String ALGORITHM_NAME = "multinom";
    private static final MINING_FUNCTION _MINING_FUNCTION = MINING_FUNCTION.REGRESSION;
    private static final MODEL_TYPE _MODEL_TYPE = MODEL_TYPE.LINEAR_REGRESSION;
    private static final REGRESSION_NORMALIZATION_METHOD _REGRESSION_NORMALIZATION_METHOD = REGRESSION_NORMALIZATION_METHOD.SIMPLEMAX;
    private static final List<KiePMMLRegressionTable> REGRESSION_TABLES = new ArrayList<>();
    private static final boolean SCORABLE = true;
    private static final String TARGETFIELD_NAME = "TARGETFIELD_NAME";
    private static final OP_TYPE _OP_TYPE = OP_TYPE.CONTINUOUS;

    @Test
    public void buildWithAll() {
        final KiePMMLRegressionModel retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), _MINING_FUNCTION, REGRESSION_TABLES, _OP_TYPE)
                .withAlgorithmName(ALGORITHM_NAME)
                .withModelType(_MODEL_TYPE)
                .withRegressionNormalizationMethod(_REGRESSION_NORMALIZATION_METHOD)
                .withScorable(SCORABLE)
                .withTargetField(TARGETFIELD_NAME)
                .build();
        assertNotNull(retrieved);
        assertEquals(MODEL_NAME, retrieved.getName());
        assertEquals(_MINING_FUNCTION, retrieved.getMiningFunction());
        assertEquals(_OP_TYPE, retrieved.getTargetOpType());
        assertEquals(_REGRESSION_NORMALIZATION_METHOD, retrieved.getRegressionNormalizationMethod());
        assertTrue(retrieved.getAlgorithmName().isPresent());
        assertEquals(ALGORITHM_NAME, retrieved.getAlgorithmName().get());
        assertTrue(retrieved.getModelType().isPresent());
        assertEquals(_MODEL_TYPE, retrieved.getModelType().get());
        assertTrue(retrieved.getModelType().isPresent());
        assertEquals(SCORABLE, retrieved.isScorable());
        assertEquals(TARGETFIELD_NAME, retrieved.getTargetField());
    }

    @Test
    public void isRegressionTrue() {
        KiePMMLRegressionModel retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION, REGRESSION_TABLES, _OP_TYPE).build();
        assertTrue(retrieved.isRegression());
        retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION, REGRESSION_TABLES, OP_TYPE.CONTINUOUS)
                .withTargetField(TARGETFIELD_NAME)
                .build();
        assertTrue(retrieved.isRegression());
    }

    @Test
    public void isRegressionFalse() {
        KiePMMLRegressionModel retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.CLASSIFICATION, REGRESSION_TABLES, _OP_TYPE).build();
        assertFalse(retrieved.isRegression());
        retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION, REGRESSION_TABLES, OP_TYPE.CATEGORICAL)
                .withTargetField(TARGETFIELD_NAME)
                .build();
        assertFalse(retrieved.isRegression());
        retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION, REGRESSION_TABLES, OP_TYPE.ORDINAL)
                .withTargetField(TARGETFIELD_NAME)
                .build();
        assertFalse(retrieved.isRegression());
    }

    @Test
    public void isBinaryTrue() {
        List<Object> targetValues = Arrays.asList("FIRST", "SECOND");
        KiePMMLRegressionModel retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.CLASSIFICATION, REGRESSION_TABLES, OP_TYPE.CATEGORICAL)
                .withTargetValues(targetValues)
                .build();
        assertTrue(retrieved.isBinary());
    }

    @Test
    public void isBinaryFalse() {
        List<Object> targetValues = Arrays.asList("FIRST", "SECOND");
        KiePMMLRegressionModel retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.CLASSIFICATION, REGRESSION_TABLES, OP_TYPE.ORDINAL)
                .withTargetValues(targetValues)
                .build();
        assertFalse(retrieved.isBinary());
        retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.CLASSIFICATION, REGRESSION_TABLES, OP_TYPE.CONTINUOUS)
                .withTargetValues(targetValues)
                .build();
        assertFalse(retrieved.isBinary());
        retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.CLASSIFICATION, REGRESSION_TABLES, OP_TYPE.CATEGORICAL)
                .build();
        assertFalse(retrieved.isBinary());
        retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.CLASSIFICATION, REGRESSION_TABLES, OP_TYPE.CATEGORICAL)
                .withTargetValues(Collections.emptyList())
                .build();
        assertFalse(retrieved.isBinary());
        retrieved = KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.CLASSIFICATION, REGRESSION_TABLES, OP_TYPE.CATEGORICAL)
                .withTargetValues(Collections.singletonList("FIRST"))
                .build();
        assertFalse(retrieved.isBinary());
    }
}