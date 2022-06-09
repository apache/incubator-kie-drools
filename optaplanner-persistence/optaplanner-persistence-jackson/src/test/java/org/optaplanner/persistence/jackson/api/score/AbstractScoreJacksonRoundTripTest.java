package org.optaplanner.persistence.jackson.api.score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.persistence.jackson.api.AbstractJacksonRoundTripTest;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractScoreJacksonRoundTripTest extends AbstractJacksonRoundTripTest {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected <Score_ extends Score<Score_>, W extends TestScoreWrapper<Score_>> void
            assertSerializeAndDeserialize(Score_ expectedScore, W input) {
        String jsonString;
        W output;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonString = objectMapper.writeValueAsString(input);
            output = (W) objectMapper.readValue(jsonString, input.getClass());
        } catch (IOException e) {
            throw new IllegalStateException("Marshalling or unmarshalling for input (" + input + ") failed.", e);
        }
        assertThat(output.getScore()).isEqualTo(expectedScore);
        String regex;
        if (expectedScore != null) {
            regex = "\\{\\s*" // Start of element
                    + "\"score\":\""
                    + expectedScore.toString().replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") // Score
                    + "\""
                    + "\\s*\\}"; // End of element
        } else {
            regex = "\\{\"score\":null\\}"; // Start and end of element
        }
        if (!jsonString.matches(regex)) {
            fail("Regular expression match failed.\nExpected regular expression: " + regex + "\nActual string: " + jsonString);
        }
    }

    public static abstract class TestScoreWrapper<Score_ extends Score<Score_>> {

        public abstract Score_ getScore();

    }

}
