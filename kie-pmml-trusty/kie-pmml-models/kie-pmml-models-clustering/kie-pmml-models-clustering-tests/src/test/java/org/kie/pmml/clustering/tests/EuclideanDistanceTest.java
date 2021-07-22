package org.kie.pmml.clustering.tests;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(Parameterized.class)
public class EuclideanDistanceTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "euclideanDistance.pmml";
    private static final String MODEL_NAME = "euclidianDistance";
    private static final String CLUSTER_ID_FIELD = "predictedValue";
    private static final String AFFINITY_1_FIELD = "clusterAffinity_1";
    private static final String AFFINITY_2_FIELD = "clusterAffinity_2";

    protected static PMMLRuntime pmmlRuntime;

    private double dimension1;
    private double dimension2;
    private String classId;
    private double affinity1;
    private double affinity2;

    public EuclideanDistanceTest(double dimension1, double dimension2, String classId,
                                 double affinity1, double affinity2) {
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.classId = classId;
        this.affinity1 = affinity1;
        this.affinity2 = affinity2;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {-1.0, -1.0, "1", 1.4142135623730951, 8.48528137423857},
                {7.0, 8.0, "2", 10.63014581273465, 3.605551275463989},
        });
    }

    @Test
    public void test() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Dimension1", dimension1);
        inputData.put("Dimension2", dimension2);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);
        Assertions.assertThat(pmml4Result.getResultVariables().get(CLUSTER_ID_FIELD)).isEqualTo(classId);

        /* TODO: Uncomment when DROOLS-6449 is implemented
        Assertions.assertThat(pmml4Result.getResultVariables().get(AFFINITY_1_FIELD)).isEqualTo(affinity1);
        Assertions.assertThat(pmml4Result.getResultVariables().get(AFFINITY_2_FIELD)).isEqualTo(affinity2);
         */
    }
}
