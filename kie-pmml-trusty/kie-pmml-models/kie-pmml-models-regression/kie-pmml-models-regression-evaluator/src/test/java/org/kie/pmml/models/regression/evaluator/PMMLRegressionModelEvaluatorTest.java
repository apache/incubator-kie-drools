package org.kie.pmml.models.regression.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.PMML_MODEL;

import static org.assertj.core.api.Assertions.assertThat;

public class PMMLRegressionModelEvaluatorTest {

    private PMMLRegressionModelEvaluator executor;

    @BeforeEach
    public void setUp() {
        executor = new PMMLRegressionModelEvaluator();
    }

    @Test
    void getPMMLModelType() {
        assertThat(executor.getPMMLModelType()).isEqualTo(PMML_MODEL.REGRESSION_MODEL);
    }
}
