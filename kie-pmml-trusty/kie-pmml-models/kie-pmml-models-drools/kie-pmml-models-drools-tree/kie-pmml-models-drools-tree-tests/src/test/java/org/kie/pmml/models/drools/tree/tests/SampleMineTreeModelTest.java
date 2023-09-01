package org.kie.pmml.models.drools.tree.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

public class SampleMineTreeModelTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "SampleMine";
    private static final String MODEL_NAME = "SampleMineTreeModel";
    private static final String TARGET_FIELD = "decision";
    private static PMMLRuntime pmmlRuntime;

    private double temperature;
    private double humidity;
    private String expectedResult;

    public void initSampleMineTreeModelTest(double temperature, double humidity, String expectedResult) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.expectedResult = expectedResult;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime =  getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {30.0, 10.0, "sunglasses"},
                {5.0, 70.0, "umbrella"},
                {10.0, 15.0, "nothing"}
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testTree(double temperature, double humidity, String expectedResult) {
        initSampleMineTreeModelTest(temperature, humidity, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("temperature", temperature);
        inputData.put("humidity", humidity);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
