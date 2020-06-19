/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.solver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.solver.io.XStreamConfigReader;

import com.thoughtworks.xstream.XStream;

public class SolverConfigTest {
    private static final String TEST_SOLVER_CONFIG = "testSolverConfig.xml";

    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;

    public SolverConfigTest() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SolverConfig.class);
        unmarshaller = jaxbContext.createUnmarshaller();
        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.toString());
    }

    @Test
    public void solverConfigMarshalling() throws JAXBException {
        SolverConfig jaxbSolverConfig = unmarshallSolverConfig(TEST_SOLVER_CONFIG);

        Writer stringWriter = new StringWriter();
        marshaller.marshal(jaxbSolverConfig, stringWriter);
        String jaxbString = stringWriter.toString();
        Reader stringReader = new StringReader(jaxbString);
        jaxbSolverConfig = (SolverConfig) unmarshaller.unmarshal(stringReader);

        // compare with xstream TODO: replace by a comparison with a configuration object model created via API
        SolverConfig xstreamSolverConfig = (SolverConfig) XStreamConfigReader.buildXStream().fromXML(jaxbString);
        Assertions.assertThat(jaxbSolverConfig).usingRecursiveComparison().isEqualTo(xstreamSolverConfig);
    }

    private SolverConfig unmarshallSolverConfig(String solverConfigResource) {
        try (InputStream testSolverConfigStream = SolverConfigTest.class.getResourceAsStream(TEST_SOLVER_CONFIG)) {
            return (SolverConfig) unmarshaller.unmarshal(testSolverConfigStream);
        } catch (IOException | JAXBException exception) {
            throw new RuntimeException("Failed to read solver configuration resource " + solverConfigResource, exception);
        }
    }

    // TODO: remove this test when switching to JAXB
    @Test
    public void xmlConfigFileRemainsSameAfterReadWrite() throws IOException {
        String solverConfigResource = "org/optaplanner/core/config/solver/testdataSolverConfig.xml";
        String originalXml = IOUtils.toString(
                getClass().getClassLoader().getResourceAsStream(solverConfigResource), StandardCharsets.UTF_8);
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        assertThat(SolverFactory.create(solverConfig).buildSolver()).isNotNull();
        XStream xStream = XStreamConfigReader.buildXStream(getClass().getClassLoader());
        xStream.setMode(XStream.NO_REFERENCES);
        String savedXml = xStream.toXML(solverConfig);
        assertThat(savedXml.trim()).isEqualTo(originalXml.trim());
    }

}
