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

public class RandomForestClassifierMiningTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "RandomForestClassifier";
    private static final String MODEL_NAME = "RandomForestClassifier";
    private static final String TARGET_FIELD = "Approved";
    private static PMMLRuntime pmmlRuntime;

    private double age;
    private double debt;
    private double yearsEmployed;
    private double income;
    private int expectedResult;

    public void initRandomForestClassifierMiningTest(double age,
                                            double debt,
                                            double yearsEmployed,
                                            double income,
                                            int expectedResult) {
        this.age = age;
        this.debt = debt;
        this.yearsEmployed = yearsEmployed;
        this.income = income;
        this.expectedResult = expectedResult;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {40.83, 3.5, 0.5, 0, 0},
                {32.25, 1.5, 0.25, 122, 0},
                {28.17, 0.585, 0.04, 1004, 1},
                {29.75, 0.665, 0.25, 0, 0}
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMixedMining(double age, double debt, double yearsEmployed, double income, int expectedResult) {
        initRandomForestClassifierMiningTest(age, debt, yearsEmployed, income, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Age", age);
        inputData.put("Debt", debt);
        inputData.put("YearsEmployed", yearsEmployed);
        inputData.put("Income", income);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
