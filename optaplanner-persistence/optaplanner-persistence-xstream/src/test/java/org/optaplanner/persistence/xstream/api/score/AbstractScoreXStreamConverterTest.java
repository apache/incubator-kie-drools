package org.optaplanner.persistence.xstream.api.score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.optaplanner.core.api.score.Score;

import com.thoughtworks.xstream.XStream;

public abstract class AbstractScoreXStreamConverterTest {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected <Score_ extends Score<Score_>, W extends TestScoreWrapper<Score_>> void
            assertSerializeAndDeserialize(Score_ expectedScore, W input) {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(input.getClass());
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByRegExp(new String[] { "org\\.optaplanner\\.\\w+\\.config\\..*",
                "org\\.optaplanner\\.persistence\\.xstream\\..*\\$Test\\w+ScoreWrapper" });
        String xmlString = xStream.toXML(input);
        W output = (W) xStream.fromXML(xmlString);

        assertThat(output.getScore()).isEqualTo(expectedScore);
        String regex;
        if (expectedScore != null) {
            regex = "<([\\w\\-\\.]+)( id=\"\\d+\")?>" // Start of element
                    + "\\s*<score( id=\"\\d+\")?>"
                    + expectedScore.toString().replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") // Score
                    + "</score>"
                    + "\\s*</\\1>"; // End of element
        } else {
            regex = "<([\\w\\-\\.]+)( id=\"\\d+\")?/>"; // Start and end of element
        }
        if (!xmlString.matches(regex)) {
            fail("Regular expression match failed.\nExpected regular expression: " + regex + "\nActual string: " + xmlString);
        }
    }

    public static abstract class TestScoreWrapper<Score_ extends Score<Score_>> {

        public abstract Score_ getScore();

    }

}
