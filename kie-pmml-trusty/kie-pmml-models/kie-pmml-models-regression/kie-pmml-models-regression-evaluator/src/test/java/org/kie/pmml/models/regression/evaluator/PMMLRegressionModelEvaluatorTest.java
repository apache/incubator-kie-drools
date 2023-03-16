package org.kie.pmml.models.regression.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.api.enums.PMML_MODEL;

import static org.assertj.core.api.Assertions.assertThat;

public class PMMLRegressionModelEvaluatorTest {

    private PMMLRegressionModelEvaluator executor;

    @Before
    public void setUp() {
        executor = new PMMLRegressionModelEvaluator();
    }

    @Test
    public void getPMMLModelType() {
        assertThat(executor.getPMMLModelType()).isEqualTo(PMML_MODEL.REGRESSION_MODEL);
    }
}
