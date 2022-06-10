package org.kie.pmml.models.tree.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.data.Percentage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class IrisDataTreeTest extends AbstractPMMLTest {

    private static final Percentage TOLERANCE_PERCENTAGE = Percentage.withPercentage(0.000001);
    private static final String FILE_NAME = "irisTree.pmml";
    private static final String MODEL_NAME = "IrisTreeModel";
    private static final String TARGET_FIELD = "Species";
    private static final String PROBABILITY_SETOSA = "Probability_setosa";
    private static final String PROBABILITY_VERSICOLOR = "Probability_versicolor";
    private static final String PROBABILITY_VIRGINICA = "Probability_virginica";
    private static PMMLRuntime pmmlRuntime;

    private double sepalLength;
    private double sepalWidth;
    private double petalLength;
    private double petalWidth;
    private String expectedResult;
    private double probabilitySetosa;
    private double probabilityVersicolor;
    private double probabilityVirginica;

    public IrisDataTreeTest(double sepalLength, double sepalWidth, double petalLength,
                            double petalWidth,
                            String expectedResult,
                            double probabilitySetosa,
                            double probabilityVersicolor,
                            double probabilityVirginica) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.expectedResult = expectedResult;
        this.probabilitySetosa = probabilitySetosa;
        this.probabilityVersicolor = probabilityVersicolor;
        this.probabilityVirginica = probabilityVirginica;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {6.9, 3.1, 5.1, 2.3, "virginica", 0.0, 0.021739130434782608, 0.9782608695652174},
                {5.8, 2.6, 4.0, 1.2, "versicolor", 0.0, 0.9074074074074074, 0.09259259259259259},
                {5.7, 3.0, 4.2, 1.2, "versicolor", 0.0, 0.9074074074074074, 0.09259259259259259},
                {5.0, 3.3, 1.4, 0.2, "setosa", 1.0, 0.0, 0.0},
                {5.4, 3.9, 1.3, 0.4, "setosa", 1.0, 0.0, 0.0},
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

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_SETOSA)).isNotNull();

        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_SETOSA)).isEqualTo(probabilitySetosa);
        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_VERSICOLOR)).isNotNull();
        assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_VERSICOLOR)).isCloseTo(probabilityVersicolor, TOLERANCE_PERCENTAGE);
        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_VIRGINICA)).isNotNull();
        assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_VIRGINICA)).isCloseTo(probabilityVirginica, TOLERANCE_PERCENTAGE);
    }
}
