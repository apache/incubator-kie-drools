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

package org.optaplanner.core.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.io.XStreamConfigReader;

public class ConfigMarshallingTest {
    private static final String TEST_SOLVER_CONFIG = "testSolverConfig.xml";

    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;

    public ConfigMarshallingTest() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SolverConfig.class);
        unmarshaller = jaxbContext.createUnmarshaller();
        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    @Test
    public void solverConfigMarshalling() throws JAXBException, IOException {
        SolverConfig jaxbSolverConfig = unmarshallSolverConfig(TEST_SOLVER_CONFIG);

        // serialize and deserialize back
        File tempFile = Files.createTempFile("jaxbSolverConfig", ".xml").toFile();
        marshaller.marshal(jaxbSolverConfig, tempFile);
        jaxbSolverConfig = (SolverConfig) unmarshaller.unmarshal(tempFile);

        // compare with xstream TODO: replace by a comparison with a configuration object model created via API
        SolverConfig xstreamSolverConfig = (SolverConfig) XStreamConfigReader.buildXStream().fromXML(tempFile);
        Assertions.assertThat(jaxbSolverConfig).usingRecursiveComparison().isEqualTo(xstreamSolverConfig);
    }

    private SolverConfig unmarshallSolverConfig(String solverConfigResource) {
        try (InputStream testSolverConfigStream = ConfigMarshallingTest.class.getResourceAsStream(TEST_SOLVER_CONFIG)) {
            return (SolverConfig) unmarshaller.unmarshal(testSolverConfigStream);
        } catch (IOException | JAXBException exception) {
            throw new RuntimeException("Failed to read solver configuration resource " + solverConfigResource, exception);
        }
    }
}
