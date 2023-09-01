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

public class SegmentationMeanMiningTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "segmentationMeanMining";
    private static final String MODEL_NAME = "SegmentationMeanMining";
    private static final String TARGET_FIELD = "result";
    private static PMMLRuntime pmmlRuntime;

    private double x;
    private double y;
    private double result;

    public void initSegmentationMeanMiningTest(double x, double y, double result) {
        this.x = x;
        this.y = y;
        this.result = result;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {-5, -25, -55},
                {0, 0, 2.5},
                {2, 5, 15.5},
                {12, -5, 10.5},
                {-1, 5, 11},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testSegmentationMeanMiningTest(double x, double y, double result) {
        initSegmentationMeanMiningTest(x, y, result);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("x", x);
        inputData.put("y", y);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(result);
    }
}
