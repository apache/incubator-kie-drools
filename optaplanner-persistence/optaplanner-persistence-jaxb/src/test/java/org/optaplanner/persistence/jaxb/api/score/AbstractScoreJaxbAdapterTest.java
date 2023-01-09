package org.optaplanner.persistence.jaxb.api.score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.optaplanner.core.api.score.Score;

public abstract class AbstractScoreJaxbAdapterTest {

    private static final String JAVAX_XML_BIND_CONTEXT_FACTORY_PROPERTY = "javax.xml.bind.context.factory";
    private static final String JAKARTA_XML_BIND_CONTEXT_FACTORY_PROPERTY = "jakarta.xml.bind.JAXBContextFactory";

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected <Score_ extends Score<Score_>, W extends TestScoreWrapper<Score_>> void
            assertSerializeAndDeserialize(Score_ expectedScore, W input) {
        assertSerializeAndDeserializeXML(expectedScore, input);
    }

    protected <Score_ extends Score<Score_>, W extends TestScoreWrapper<Score_>> void
            assertSerializeAndDeserializeXML(Score_ expectedScore, W input) {
        String xmlString;
        W output;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(input.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            StringWriter writer = new StringWriter();
            jaxbMarshaller.marshal(input, writer);
            xmlString = writer.toString();
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xmlString);
            output = (W) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new IllegalStateException("Marshalling or unmarshalling for input (" + input + ") failed.", e);
        }
        assertThat(output.getScore()).isEqualTo(expectedScore);
        String regex;
        if (expectedScore != null) {
            regex = "<\\?[^\\?]*\\?>" // XML header
                    + "<([\\w\\-\\.]+)>\\s*" // Start of element
                    + "<score>"
                    + expectedScore.toString().replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") // Score
                    + "</score>"
                    + "\\s*</\\1>"; // End of element
        } else {
            regex = "<\\?[^\\?]*\\?>" // XML header
                    + "<([\\w\\-\\.]+)/>"; // Start and end of element
        }
        if (!xmlString.matches(regex)) {
            fail(String.format("Regular expression match failed.%nExpected regular expression: %s%n" +
                    "Actual string: %s", regex, xmlString));
        }
    }

    public static abstract class TestScoreWrapper<Score_ extends Score<Score_>> {

        public abstract Score_ getScore();

    }

}
