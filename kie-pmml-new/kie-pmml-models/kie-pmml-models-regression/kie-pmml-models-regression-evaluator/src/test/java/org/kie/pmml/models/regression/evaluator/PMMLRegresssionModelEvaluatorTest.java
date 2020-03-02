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

package org.kie.pmml.models.regression.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.models.regression.model.predictors.KiePMMLCategoricalPredictor;
import org.kie.pmml.models.regression.model.predictors.KiePMMLNumericPredictor;
import org.kie.pmml.models.regression.model.predictors.KiePMMLPredictorTerm;
import org.kie.pmml.models.regression.model.predictors.KiePMMLRegressionTablePredictor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PMMLRegresssionModelEvaluatorTest {

    private static final String MODEL_NAME = "LinReg";
    private static final String TARGET_FIELD_NAME = "fld4";

//    @Test
//    public void evaluateClassification() {
//        final PMML4Result retrieved = PMMLRegresssionModelEvaluator.evaluateRegression(getModel(), getContext());
//        commonVerifyPMM4Result(retrieved);
//    }

//    @Test
//    public void testEvaluateClassification() {
//        final PMML4Result retrieved = PMMLRegresssionModelEvaluator.evaluateRegression(TARGET_FIELD_NAME, getModel().getRegressionNormalizationMethod(), OP_TYPE.ORDINAL, getTable(), getRequestData());
//        commonVerifyPMM4Result(retrieved);
//    }

    @Test(expected = KiePMMLModelException.class)
    public void updateResultSOFTMAXCATEGORICAL() {
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.SOFTMAX, OP_TYPE.CATEGORICAL, new AtomicReference<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void updateResultLOGITCATEGORICAL() {
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.LOGIT, OP_TYPE.CATEGORICAL, new AtomicReference<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void updateResultEXPCATEGORICAL() {
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.EXP, OP_TYPE.CATEGORICAL, new AtomicReference<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void updateResultPROBITCATEGORICAL() {
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.PROBIT, OP_TYPE.CATEGORICAL, new AtomicReference<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void updateResultCLOGLOGCATEGORICAL() {
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.CLOGLOG, OP_TYPE.CATEGORICAL, new AtomicReference<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void updateResultCAUCHITCATEGORICAL() {
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.CAUCHIT, OP_TYPE.CATEGORICAL, new AtomicReference<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void updateResultNONECATEGORICAL() {
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.NONE, OP_TYPE.CATEGORICAL, new AtomicReference<>());
    }

    @Test
    public void updateSOFTMAX() {
        /*
        predictedValue = 1/(1+exp(-y1))
         */
        AtomicReference<Double> toUpdate = new AtomicReference<>(2.2580000000000005);
        double expected = 0.905338368331313;
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.SOFTMAX, OP_TYPE.CONTINUOUS, toUpdate);
        assertEquals(expected, toUpdate.get(), 0.0);
    }

    @Test
    public void updateLOGIT() {
        /*
        predictedValue = 1/(1+exp(-y1))
         */
        AtomicReference<Double> toUpdate = new AtomicReference<>(2.2580000000000005);
        double expected = 0.905338368331313;
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.LOGIT, OP_TYPE.ORDINAL, toUpdate);
        assertEquals(expected, toUpdate.get(), 0.0);
    }

    @Test
    public void updateEXP() {
        /*
        predictedValue = exp(y1)
         */
        AtomicReference<Double> toUpdate = new AtomicReference<>(2.2580000000000005);
        double expected = 9.56394214183812;
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.EXP, OP_TYPE.ORDINAL, toUpdate);
        assertEquals(expected, toUpdate.get(), 0.0);
    }

    @Test
    public void updatePROBIT() {
        /*
        predictedValue = CDF(y1)
         */
        AtomicReference<Double> toUpdate = new AtomicReference<>(2.2580000000000005);
        double expected = 0.9880271702826351;
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.PROBIT, OP_TYPE.CONTINUOUS, toUpdate);
        assertEquals(expected, toUpdate.get(), 0.0);
    }

    @Test
    public void updateCLOGLOG() {
        /*
        predictedValue = 1 - exp( -exp( y1))
         */
        AtomicReference<Double> toUpdate = new AtomicReference<>(2.2580000000000005);
        double expected = 0.9999297845469218;
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.CLOGLOG, OP_TYPE.CONTINUOUS, toUpdate);
        assertEquals(expected, toUpdate.get(), 0.0);
    }

    @Test
    public void updateCAUCHIT() {
        /*
        predictedValue = 0.5 + (1/π) arctan(y1)
         */
        AtomicReference<Double> toUpdate = new AtomicReference<>(2.2580000000000005);
        double expected = 0.8672938553679819;
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.CAUCHIT, OP_TYPE.CONTINUOUS, toUpdate);
        assertEquals(expected, toUpdate.get(), 0.0);
    }

    @Test
    public void updateNONE() {
        /*
        predictedValue = y1
         */
        AtomicReference<Double> toUpdate = new AtomicReference<>(2.2580000000000005);
        double expected = toUpdate.get();
        PMMLRegresssionModelEvaluator.updateResult(REGRESSION_NORMALIZATION_METHOD.NONE, OP_TYPE.CONTINUOUS, toUpdate);
        assertEquals(expected, toUpdate.get(), 0.0);
    }

    private void commonVerifyPMM4Result(PMML4Result toVerify) {
        assertNotNull(toVerify);
        assertEquals(StatusCode.OK.getName(), toVerify.getResultCode());
        assertEquals(TARGET_FIELD_NAME, toVerify.getResultObjectName());
        assertEquals(0.9999297845469218, toVerify.getResultVariables().get(TARGET_FIELD_NAME));
    }

    private PMMLContext getContext() {
        return new PMMLContextImpl(getRequestData());
    }

    private PMMLRequestData getRequestData() {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("fld1", 0.9);
        inputMap.put("fld2", 0.3);
        inputMap.put("fld3", "x");
        return TestUtils.getPMMLRequestData(MODEL_NAME, inputMap);
    }

//    private KiePMMLRegressionModel getModel() {
//        return KiePMMLRegressionModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION, Collections.singletonList(getTable()), OP_TYPE.CONTINUOUS)
//                .withRegressionNormalizationMethod(REGRESSION_NORMALIZATION_METHOD.CLOGLOG)
//                .withTargetField(TARGET_FIELD_NAME)
//                .build();
//    }

    private KiePMMLRegressionTable getTable() {
        Set<KiePMMLNumericPredictor> numericPredictors = new HashSet<>(Arrays.asList(
                new KiePMMLNumericPredictor("fld1", 2, 5, Collections.emptyList()),
                new KiePMMLNumericPredictor("fld2", 1, 2, Collections.emptyList())
        ));
        Set<KiePMMLCategoricalPredictor> categoricalPredictors = new HashSet<>(Arrays.asList(
                new KiePMMLCategoricalPredictor("fld3", "x", -3, Collections.emptyList()),
                new KiePMMLCategoricalPredictor("fld3", "y", 3, Collections.emptyList())
        ));

        List<KiePMMLRegressionTablePredictor> predictors = new ArrayList<>(numericPredictors);
        KiePMMLPredictorTerm predictorTerm = new KiePMMLPredictorTerm("predTerm", predictors, 0.4, Collections.emptyList());
        return KiePMMLRegressionTable.builder("TABLE", Collections.emptyList(), 0.5)
                .withTargetCategory("clerical")
                .withNumericPredictors(numericPredictors)
                .withCategoricalPredictors(categoricalPredictors)
                .withPredictorTerms(Collections.singleton(predictorTerm))
                .build();
    }
}