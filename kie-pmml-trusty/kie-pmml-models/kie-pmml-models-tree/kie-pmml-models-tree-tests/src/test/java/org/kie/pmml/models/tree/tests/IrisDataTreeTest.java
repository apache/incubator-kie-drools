package org.kie.pmml.models.tree.tests;

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
public class IrisDataTreeTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "irisTree.pmml";
    private static final String MODEL_NAME = "IrisTreeModel";
    private static final String TARGET_FIELD = "Species";
    private static PMMLRuntime pmmlRuntime;

    private double sepalLength;
    private double sepalWidth;
    private double petalLength;
    private double petalWidth;
    private String expectedResult;

    public IrisDataTreeTest(double sepalLength, double sepalWidth, double petalLength,
                            double petalWidth, String expectedResult) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.expectedResult = expectedResult;
    }

  @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {6.9, 3.1, 5.1, 2.3, "virginica"},
                {5.8, 2.6, 4.0, 1.2, "versicolor"},
                {5.7, 3.0, 4.2, 1.2, "versicolor"},
                {5.0, 3.3, 1.4, 0.2, "setosa"},
                {5.4, 3.9, 1.3, 0.4, "setosa"}
        });
    }

    @Test
    public void testIrisTree() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Sepal.Length", sepalLength);
        inputData.put("Sepal.Width", sepalWidth);
        inputData.put("Petal.Length", petalLength);
        inputData.put("Petal.Width", petalWidth);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
