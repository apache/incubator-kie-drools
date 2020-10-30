package org.kie.pmml.models.regression.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.api.enums.PMML_MODEL;

import static org.junit.Assert.assertEquals;

public class PMMLRegressionModelEvaluatorTest {

    private PMMLRegressionModelEvaluator executor;

    @Before
    public void setUp() {
        executor = new PMMLRegressionModelEvaluator();
    }

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.REGRESSION_MODEL, executor.getPMMLModelType());
    }
}
