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
public class MultipleClustersSameClassTest extends AbstractPMMLTest {

    private static final double DOUBLE_VALID_PERCENTAGE = 0.99999;

    private static final String FILE_NAME = "multipleClustersSameClass.pmml";
    private static final String MODEL_NAME = "multipleClusterSameClassModel";
    private static final String AFFINITY_FIELD = "predictedAffinity";
    private static final String CLUSTER_AFFINITY_FIELD = "predictedClusterAffinity";
    private static final String CLUSTER_ID_FIELD = "predictedValue";
    private static final String CLUSTER_NAME_FIELD = "predictedDisplayValue";

    protected static PMMLRuntime pmmlRuntime;

    private double dimension1;
    private double dimension2;
    private String classId;
    private String className;
    private double affinity;

    public MultipleClustersSameClassTest(double dimension1, double dimension2, String classId, String className, double affinity) {
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.classId = classId;
        this.className = className;
        this.affinity = affinity;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0.0, 0.0, "1", "classA", 0.0},
                {0.5, 0.0, "1", "classA", 0.25},
                {1.0, 1.0, "2", "classB", 0.0},
                {1.0, 3.0, "2", "classB", 4.0},
                {-1, -1, "3", "classA", 0.0},
                {-1.3, -1.4, "3", "classA", 0.25},
                {5.0, 5.0, "4", "classB", 0.0},
                {8.0, 9.0, "4", "classB", 25.0},
                {-3.0, 5.0, "5", "classC", 0.0},
                {-2.0, 5.0, "5", "classC", 1.0}
        });
    }

    @Test
    public void test() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Dimension1", dimension1);
        inputData.put("Dimension2", dimension2);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(CLUSTER_ID_FIELD)).isEqualTo(classId);
        assertThat(pmml4Result.getResultVariables().get(CLUSTER_NAME_FIELD)).isEqualTo(className);

        assertDoubleVariable(pmml4Result, AFFINITY_FIELD, affinity);
        assertDoubleVariable(pmml4Result, CLUSTER_AFFINITY_FIELD, affinity);
    }

    private static void assertDoubleVariable(PMML4Result pmml4Result, String variableName, double expectedValue) {
        assertThat(pmml4Result.getResultVariables().get(variableName))
                .asInstanceOf(InstanceOfAssertFactories.DOUBLE)
                .isCloseTo(expectedValue, Percentage.withPercentage(DOUBLE_VALID_PERCENTAGE));
    }
}
