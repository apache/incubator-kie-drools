package org.kie.pmml.mining.tests;

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

public class SegmentationClassificationSelectFirstTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "segmentationClassificationSelectFirst";
    private static final String MODEL_NAME = "SegmentationClassificationSelectFirst";
    private static final String TARGET_FIELD = "result";
    private static PMMLRuntime pmmlRuntime;

    private double input1;
    private double input2;
    private double input3;
    private String result;

    public void initSegmentationClassificationSelectFirstTest(double input1, double input2, double input3, String result) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.result = result;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, -1, 0, "classB1"},
                {2, 20, 90, "classC1"},
                {4, 20, 0, "classA1"},
                {15, -1, 0, "classC2"},
                {17, 20, 90, "classA2"},
                {18, 20, 0, "classB2"},
                {55, -1, 0, "classA3"},
                {57, 20, 90, "classB3"},
                {58, 20, 0, "classC3"},
                {58, 20, -1, "classC3"},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testSegmentationClassificationSelectFirstTest(double input1, double input2, double input3, String result) {
        initSegmentationClassificationSelectFirstTest(input1, input2, input3, result);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("input1", input1);
        inputData.put("input2", input2);
        inputData.put("input3", input3);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(result);
    }
}
