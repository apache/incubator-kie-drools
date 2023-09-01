package org.kie.pmml.models.drools.scorecard.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

public class AirconditioningScorecardTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "air-conditioning-weather-scorecard";
    private static final String MODEL_NAME = "Forecast Score";
    private static final String TARGET_FIELD = "forecastScore";
    private static PMMLRuntime pmmlRuntime;

    private String period;
    private String worldContinent;
    private boolean precipitation;
    private double humidity;
    private double score;

    public void initAirconditioningScorecardTest(String period, String worldContinent, boolean precipitation, double humidity,
                                        double score) {
        this.period = period;
        this.worldContinent = worldContinent;
        this.precipitation = precipitation;
        this.humidity = humidity;
        this.score = score;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"SPRING", "EUROPE", true, 25.0, 9.5},
                {"SUMMER", "ASIA", true, 35.0, 10.0},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testAirconditioningScorecard(String period, String worldContinent, boolean precipitation, double humidity, double score) {
        initAirconditioningScorecardTest(period, worldContinent, precipitation, humidity, score);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("period", period);
        inputData.put("worldcontinent", worldContinent);
        inputData.put("precipitation", precipitation);
        inputData.put("humidity", humidity);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat((double) (pmml4Result.getResultVariables().get(TARGET_FIELD)))
                .isCloseTo(score, Percentage.withPercentage(0.1));
    }
}
