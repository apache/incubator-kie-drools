package org.kie.pmml.mining.tests;

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
public class SegmentationMajorityVoteMiningTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "segmentationClassificationMajorityVote.pmml";
    private static final String MODEL_NAME = "SegmentationClassificationMajorityVote";
    private static final String TARGET_FIELD = "result";
    private static PMMLRuntime pmmlRuntime;

    private double input1;
    private double input2;
    private double input3;
    private String result;

    public SegmentationMajorityVoteMiningTest(double input1, double input2, double input3, String result) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.result = result;
    }

  @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {-15.5, -51, 12, "classB"},
                {-15.5, -51, 1001, "classB"},
                {705, -51, 11, "classB"},
                {-15.5, -40, 12, "classB"},
                {0, -42, 50, "classA"},
                {-17, -42, 50, "classA"},
                {0, 1000, 50, "classA"},
                {0, -42, -1000, "classA"},
                {90, -5, 210, "classC"},
                {-50, -5, 210, "classC"},
                {90, -2000, 210, "classC"},
                {90, -5, 195, "classC"},
        });
    }

    @Test
    public void testSegmentationMajorityVoteTest() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("input1", input1);
        inputData.put("input2", input2);
        inputData.put("input3", input3);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(result);
    }
}
