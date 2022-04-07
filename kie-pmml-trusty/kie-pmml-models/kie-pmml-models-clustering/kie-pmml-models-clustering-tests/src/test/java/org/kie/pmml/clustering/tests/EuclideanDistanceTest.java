package org.kie.pmml.clustering.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.InstanceOfAssertFactories;
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
public class EuclideanDistanceTest extends AbstractPMMLTest {

    private static final double DOUBLE_VALID_PERCENTAGE = 0.99999;

    private static final String FILE_NAME = "euclideanDistance.pmml";
    private static final String MODEL_NAME = "euclidianDistance";
    private static final String CLUSTER_ID_FIELD = "predictedValue";
    private static final String AFFINITY_FIELD = "predictedAffinity";

    protected static PMMLRuntime pmmlRuntime;

    private double dimension1;
    private double dimension2;
    private String classId;
    private double affinity;

    public EuclideanDistanceTest(double dimension1, double dimension2, String classId, double affinity) {
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.classId = classId;
        this.affinity = affinity;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {-1.0, -1.0, "1", 1.4142135623730951},
                {7.0, 8.0, "2", 3.605551275463989},
        });
    }

    @Test
    public void test() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Dimension1", dimension1);
        inputData.put("Dimension2", dimension2);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);
        assertThat(pmml4Result.getResultVariables().get(CLUSTER_ID_FIELD)).isEqualTo(classId);
        assertThat(pmml4Result.getResultVariables().get(AFFINITY_FIELD))
                .asInstanceOf(InstanceOfAssertFactories.DOUBLE)
                .isCloseTo(affinity, Percentage.withPercentage(DOUBLE_VALID_PERCENTAGE));
    }
}
