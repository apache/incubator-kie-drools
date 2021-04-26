package org.kie.pmml.clustering.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

@RunWith(Parameterized.class)
public class SingleIrisKMeansTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "SingleIrisKMeans.pmml";
    private static final String MODEL_NAME = "SingleIrisKMeans";
    private static final String TARGET_FIELD = "class";

    private static PMMLRuntime pmmlRuntime;

    private final double sepalLength;
    private final double sepalWidth;
    private final double petalLength;
    private final double petalWidth;
    private final int irisClass;

    public SingleIrisKMeansTest(double sepalLength, double sepalWidth, double petalLength, double petalWidth, int irisClass) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.irisClass = irisClass;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {4.4, 3.0, 1.3, 0.2, 3},
                {5.0, 3.3, 1.4, 0.2, 3},
                {7.0, 3.2, 4.7, 1.4, 2},
                {5.7, 2.8, 4.1, 1.3, 4},
                {6.3, 3.3, 6.0, 2.5, 1},
                {6.7, 3.0, 5.2, 2.3, 1}
        });
    }

    @Test
    public void testLogisticRegressionIrisData() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("sepal_length", sepalLength);
        inputData.put("sepal_width", sepalWidth);
        inputData.put("petal_length", petalLength);
        inputData.put("petal_width", petalWidth);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(irisClass);
    }
}
