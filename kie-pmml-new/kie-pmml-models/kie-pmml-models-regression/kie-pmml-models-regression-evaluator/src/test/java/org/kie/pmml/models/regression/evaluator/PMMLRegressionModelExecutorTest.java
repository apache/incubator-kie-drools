package org.kie.pmml.models.regression.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class PMMLRegressionModelExecutorTest {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRegressionModelExecutorTest.class);

    private PMMLRegressionModelExecutor executor;

    @Before
    public void setUp() throws Exception {
        executor = new PMMLRegressionModelExecutor();
    }

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.REGRESSION_MODEL, executor.getPMMLModelType());
    }

    @Test
    public void evaluate() {
    }
}
