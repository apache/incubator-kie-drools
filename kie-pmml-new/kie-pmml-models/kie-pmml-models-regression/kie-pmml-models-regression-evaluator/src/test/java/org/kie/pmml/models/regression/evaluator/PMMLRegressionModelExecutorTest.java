package org.kie.pmml.models.regression.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.model.enums.PMML_MODEL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PMMLRegressionModelExecutorTest {

    private static final String REGRESSION_MODEL_NAME = "LinReg";
    private static final String REGRESSION_TARGET_FIELD_NAME = "fld4";
    private static final String CLASSIFICATION_MODEL_NAME = "Sample for logistic regression";
    private static final String CLASSIFICATION_TARGET_FIELD_NAME = "jobcat";
    private static final String RELEASE_ID = "RELEASE_ID";

    private PMMLRegressionModelExecutor executor;

    @Before
    public void setUp() {
        executor = new PMMLRegressionModelExecutor();
    }

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.REGRESSION_MODEL, executor.getPMMLModelType());
    }

//    @Test
//    public void evaluateRegression() {
//        PMML4Result retrieved = executor.evaluate(getRegressionModel(), getRegressionContext(), RELEASE_ID);
//        assertNotNull(retrieved);
//        assertEquals(StatusCode.OK.getName(), retrieved.getResultCode());
//        assertEquals(REGRESSION_TARGET_FIELD_NAME, retrieved.getResultObjectName());
//        assertEquals(0.9999297845469218, retrieved.getResultVariables().get(REGRESSION_TARGET_FIELD_NAME));
//    }
//
//    @Test
//    public void evaluateClassification() {
//        PMML4Result retrieved = executor.evaluate(getClassificationModel(), getClassificationContext(), RELEASE_ID);
//        assertNotNull(retrieved);
//        assertEquals(StatusCode.OK.getName(), retrieved.getResultCode());
//        assertEquals(CLASSIFICATION_TARGET_FIELD_NAME, retrieved.getResultObjectName());
//        assertEquals("clerical", retrieved.getResultVariables().get(CLASSIFICATION_TARGET_FIELD_NAME));
//    }
//
//    private PMMLContext getRegressionContext() {
//        return new PMMLContextImpl(getRegressionRequestData());
//    }
//
//    private PMMLRequestData getRegressionRequestData() {
//        Map<String, Object> inputMap = new HashMap<>();
//        inputMap.put("fld1", 0.9);
//        inputMap.put("fld2", 0.3);
//        inputMap.put("fld3", "x");
//        return TestUtils.getPMMLRequestData(REGRESSION_MODEL_NAME, inputMap);
//    }
//
//    private KiePMMLRegressionModel getRegressionModel() {
//        return KiePMMLRegressionModel.builder(REGRESSION_MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION, Collections.singletonList(getRegressionTable()), OP_TYPE.CONTINUOUS)
//                .withRegressionNormalizationMethod(REGRESSION_NORMALIZATION_METHOD.CLOGLOG)
//                .withTargetField(REGRESSION_TARGET_FIELD_NAME)
//                .build();
//    }
//
//    private KiePMMLRegressionTable getRegressionTable() {
//        Set<KiePMMLNumericPredictor> numericPredictors = new HashSet<>(Arrays.asList(
//                new KiePMMLNumericPredictor("fld1", 2, 5, Collections.emptyList()),
//                new KiePMMLNumericPredictor("fld2", 1, 2, Collections.emptyList())
//        ));
//        Set<KiePMMLCategoricalPredictor> categoricalPredictors = new HashSet<>(Arrays.asList(
//                new KiePMMLCategoricalPredictor("fld3", "x", -3, Collections.emptyList()),
//                new KiePMMLCategoricalPredictor("fld3", "y", 3, Collections.emptyList())
//        ));
//
//        List<KiePMMLRegressionTablePredictor> predictors = new ArrayList<>(numericPredictors);
//        KiePMMLPredictorTerm predictorTerm = new KiePMMLPredictorTerm("predTerm", predictors, 0.4, Collections.emptyList());
//        return KiePMMLRegressionTable.builder("TABLE", Collections.emptyList(), 0.5)
//                .withTargetCategory("clerical")
//                .withNumericPredictors(numericPredictors)
//                .withCategoricalPredictors(categoricalPredictors)
//                .withPredictorTerms(Collections.singleton(predictorTerm))
//                .build();
//    }
//
//    private PMMLContext getClassificationContext() {
//        return new PMMLContextImpl(getClassificationRequestData());
//    }
//
//    private PMMLRequestData getClassificationRequestData() {
//        Map<String, Object> inputMap = new HashMap<>();
//        inputMap.put("age", 27.0);
//        inputMap.put("work", 3.5);
//        inputMap.put("sex", "0");
//        inputMap.put("minority", "0");
//        return TestUtils.getPMMLRequestData(CLASSIFICATION_MODEL_NAME, inputMap);
//    }
//
//    private KiePMMLRegressionModel getClassificationModel() {
//        return KiePMMLRegressionModel.builder(CLASSIFICATION_MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.CLASSIFICATION, getClassificationTables(), OP_TYPE.CATEGORICAL)
//                .withTargetField(CLASSIFICATION_TARGET_FIELD_NAME)
//                .build();
//    }
//
//    private List<KiePMMLRegressionTable> getClassificationTables() {
//        Set<KiePMMLNumericPredictor> firstNumericPredictors = new HashSet<>(Arrays.asList(
//                new KiePMMLNumericPredictor("age", 1, -0.132, Collections.emptyList()),
//                new KiePMMLNumericPredictor("work", 1, 7.867E-02, Collections.emptyList())
//        ));
//
//        KiePMMLRegressionTable firstTable = KiePMMLRegressionTable.builder("FIRST_TABLE", Collections.emptyList(), 46.418)
//                .withTargetCategory("clerical")
//                .withNumericPredictors(firstNumericPredictors)
//                .build();
//        Set<KiePMMLNumericPredictor> secondNumericPredictors = new HashSet<>(Arrays.asList(
//                new KiePMMLNumericPredictor("age", 1, -0.302, Collections.emptyList()),
//                new KiePMMLNumericPredictor("work", 1, 0.155, Collections.emptyList())
//        ));
//        KiePMMLRegressionTable secondTable = KiePMMLRegressionTable.builder("SECOND_TABLE", Collections.emptyList(), 51.169)
//                .withTargetCategory("professional")
//                .withNumericPredictors(secondNumericPredictors)
//                .build();
//        return Arrays.asList(firstTable, secondTable);
//    }
}
