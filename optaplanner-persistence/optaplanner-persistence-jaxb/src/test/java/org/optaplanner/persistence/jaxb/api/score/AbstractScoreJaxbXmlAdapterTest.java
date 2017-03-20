/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.persistence.jaxb.api.score;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.optaplanner.core.api.score.Score;

import static org.junit.Assert.*;

public abstract class AbstractScoreJaxbXmlAdapterTest {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected <S extends Score, W extends TestScoreWrapper<S>> void assertSerializeAndDeserialize(S expectedScore, W input) {
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
        assertEquals(expectedScore, output.getScore());
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
            fail("Regular expression match failed.\nExpected regular expression: " + regex + "\nActual string: " + xmlString);
        }
    }

    public static abstract class TestScoreWrapper<S extends Score> implements Serializable {

        public abstract S getScore();

    }

}
