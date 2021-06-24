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
public class MultipleClustersSameClassTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "multipleClustersSameClass.pmml";
    private static final String MODEL_NAME = "multipleClusterSameClassModel";
    private static final String CLUSTER_ID_FIELD = "predictedValue";
    private static final String CLUSTER_NAME_FIELD = "predictedDisplayValue";
    private static final String AFFINITY_1_FIELD = "clusterAffinity_1";
    private static final String AFFINITY_2_FIELD = "clusterAffinity_2";
    private static final String AFFINITY_3_FIELD = "clusterAffinity_3";
    private static final String AFFINITY_4_FIELD = "clusterAffinity_4";
    private static final String AFFINITY_5_FIELD = "clusterAffinity_5";

    protected static PMMLRuntime pmmlRuntime;

    private double dimension1;
    private double dimension2;
    private String classId;
    private String className;
    private double affinity1;
    private double affinity2;
    private double affinity3;
    private double affinity4;
    private double affinity5;

    public MultipleClustersSameClassTest(double dimension1, double dimension2, String classId, String className,
                                         double affinity1, double affinity2, double affinity3,
                                         double affinity4, double affinity5) {
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.classId = classId;
        this.className = className;
        this.affinity1 = affinity1;
        this.affinity2 = affinity2;
        this.affinity3 = affinity3;
        this.affinity4 = affinity4;
        this.affinity5 = affinity5;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0.0, 0.0, "1", "classA", 0.0, 2.0, 2.0, 50.0, 34.0},
                {1.0, 1.0, "2", "classB", 2.0, 0.0, 8.0, 32.0, 32.0},
                {-1.0, -1.0, "3", "classA", 2.0, 8.0, 0.0, 72.0, 40.0},
                {5.0, 5.0, "4", "classB", 50.0, 32.0, 72.0, 0.0, 64.0},
                {-3.0, 5.0, "5", "classC", 34.0, 32.0, 40.0, 64.0, 0.0},
        });
    }

    @Test
    public void test() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Dimension1", dimension1);
        inputData.put("Dimension2", dimension2);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);
        Assertions.assertThat(pmml4Result.getResultVariables().get(CLUSTER_ID_FIELD)).isEqualTo(classId);

        /* TODO: Uncomment when DROOLS-6448 is implemented
        Assertions.assertThat(pmml4Result.getResultVariables().get(CLUSTER_NAME_FIELD)).isEqualTo(className);
        */

        /* TODO: Uncomment when DROOLS-6449 is implemented
        Assertions.assertThat(pmml4Result.getResultVariables().get(AFFINITY_1_FIELD)).isEqualTo(affinity1);
        Assertions.assertThat(pmml4Result.getResultVariables().get(AFFINITY_2_FIELD)).isEqualTo(affinity2);
        Assertions.assertThat(pmml4Result.getResultVariables().get(AFFINITY_3_FIELD)).isEqualTo(affinity3);
        Assertions.assertThat(pmml4Result.getResultVariables().get(AFFINITY_4_FIELD)).isEqualTo(affinity4);
        Assertions.assertThat(pmml4Result.getResultVariables().get(AFFINITY_5_FIELD)).isEqualTo(affinity5);
         */
    }
}
