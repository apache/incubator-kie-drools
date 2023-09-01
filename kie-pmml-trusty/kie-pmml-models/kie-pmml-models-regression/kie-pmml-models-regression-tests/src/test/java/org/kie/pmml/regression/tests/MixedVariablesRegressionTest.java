package org.kie.pmml.regression.tests;

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

public class MixedVariablesRegressionTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "MixedVariablesRegression";
    private static final String MODEL_NAME = "MixedVariablesRegression";
    private static final String TARGET_FIELD = "result";
    private static PMMLRuntime pmmlRuntime;

    private double x;
    private String y;

    public void initMixedVariablesRegressionTest(double x, String y) {
        this.x = x;
        this.y = y;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, "classA"}, {-1, "classA"}, {0.5, "classA"}, {3, "classA"}, {25, "classB"},
                {-100, "classB"}, {-100.1, "classB"}, {-8, "classC"}, {-1001.1, "classC"}, {-1701, "classC"}
        });
    }

    private static double regressionFunction(double x, String y) {
        final Map<String, Double> categoriesMap = new HashMap<>();
        categoriesMap.put("classA", 0.0);
        categoriesMap.put("classB", 20.0);
        categoriesMap.put("classC", 40.0);

        return 2 * x + categoriesMap.get(y) + 22;
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMixedVariablesRegression(double x, String y) throws Exception {
        initMixedVariablesRegressionTest(x, y);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("x", x);
        inputData.put("y", y);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result).isNotNull();
        assertThat(pmml4Result.getResultVariables()).containsKey(TARGET_FIELD);
        assertThat((Double) pmml4Result.getResultVariables().get(TARGET_FIELD))
                .isEqualTo(regressionFunction(x, y));
    }
}
