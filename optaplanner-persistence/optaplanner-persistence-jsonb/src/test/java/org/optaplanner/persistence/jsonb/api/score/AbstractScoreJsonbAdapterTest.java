package org.optaplanner.persistence.jsonb.api.score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

import org.optaplanner.core.api.score.Score;

public abstract class AbstractScoreJsonbAdapterTest {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected <Score_ extends Score<Score_>, W extends TestScoreWrapper<Score_>> void
            assertSerializeAndDeserialize(Score_ expectedScore, W input) {
        String jsonString;
        W output;
        try {
            Jsonb jsonb = JsonbBuilder.create();
            jsonString = jsonb.toJson(input);
            output = (W) jsonb.fromJson(jsonString, input.getClass());
        } catch (JsonbException e) {
            throw new IllegalStateException("Marshalling or unmarshalling for input (" + input + ") failed.", e);
        }
        assertThat(output.getScore()).isEqualTo(expectedScore);

        String newLine = System.lineSeparator();
        String regex;
        if (expectedScore != null) {
            regex = "\\{\"score\":\"" // Start of element
                    + expectedScore.toString().replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") // Score
                    + "\"\\}"; // End of element
        } else {
            regex = "\\{\\}";
        }
        if (!jsonString.matches(regex)) {
            fail("Regular expression match failed." + newLine + "Expected regular expression: " + regex + newLine +
                    "Actual string: " + jsonString);
        }
    }

    public static abstract class TestScoreWrapper<Score_ extends Score<Score_>> {

        public abstract Score_ getScore();

        public abstract void setScore(Score_ score);
    }
}
