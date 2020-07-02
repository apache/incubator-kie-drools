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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.core.impl.solver.io.XStreamConfigReader;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

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
    public void jaxbXmlConfigFileRemainsSameAfterReadWrite() throws IOException {
        SolverConfig jaxbSolverConfig = unmarshallSolverConfigFromResource(TEST_SOLVER_CONFIG);

        Writer stringWriter = new StringWriter();
        marshall(jaxbSolverConfig, stringWriter);
        String jaxbString = stringWriter.toString();

        String originalXml = IOUtils.toString(
                SolverConfigTest.class.getResourceAsStream(TEST_SOLVER_CONFIG), StandardCharsets.UTF_8);

        assertThat(jaxbString.trim()).isEqualToNormalizingNewlines(originalXml.trim());
    }

    private SolverConfig unmarshallSolverConfigFromResource(String solverConfigResource) {
        try (InputStream testSolverConfigStream = SolverConfigTest.class.getResourceAsStream(solverConfigResource)) {
            return (SolverConfig) unmarshaller.unmarshal(testSolverConfigStream);
        } catch (IOException | JAXBException exception) {
            throw new RuntimeException("Failed to read solver configuration resource " + solverConfigResource, exception);
        }
    }

    private void marshall(SolverConfig solverConfig, Writer writer) {
        DOMResult domResult = new DOMResult();
        try {
            marshaller.marshal(solverConfig, domResult);
        } catch (JAXBException jaxbException) {
            throw new RuntimeException("Unable to marshall PlannerBenchmarkConfig to XML.", jaxbException);
        }

        // see https://stackoverflow.com/questions/46708498/jaxb-marshaller-indentation
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(domResult.getNode()), new StreamResult(writer));
        } catch (TransformerException e) {
            throw new RuntimeException("Unable to format PlannerBenchmarkConfig XML.", e);
        }
    }

    @Test
    public void whiteCharsInClassName() {
        String solutionClassName = "org.optaplanner.core.impl.testdata.domain.TestdataSolution";
        String xmlFragment = String.format("<solver>%n"
                + "  <solutionClass>  %s  %n" // Intentionally included white chars around the class name.
                + "  </solutionClass>%n"
                + "</solver>", solutionClassName);
        SolverConfig solverConfig = unmarshallSolverConfigFromString(xmlFragment);
        assertThat(solverConfig.getSolutionClass().getName()).isEqualTo(solutionClassName);
    }

    private SolverConfig unmarshallSolverConfigFromString(String solverConfigXml) {
        Reader stringReader = new StringReader(solverConfigXml);
        try {
            return (SolverConfig) unmarshaller.unmarshal(stringReader);
        } catch (JAXBException jaxbException) {
            throw new RuntimeException("Error during unmarshalling a solver config.", jaxbException);
        }
    }

    @Test
    public void variableNameAsNestedElementInValueSelector() {
        String xmlFragment = String.format("<solver>\n"
                + "  <constructionHeuristic>\n"
                + "      <changeMoveSelector>\n"
                + "        <valueSelector>\n"
                // Intentionally wrong: variableName should be an attribute of the <valueSelector/>
                + "          <variableName>subValue</variableName>\n"
                + "        </valueSelector>\n"
                + "      </changeMoveSelector>\n"
                + "  </constructionHeuristic>\n"
                + "</solver>");
        SolverConfig solverConfig = unmarshallSolverConfigFromString(xmlFragment);

        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig =
                (ConstructionHeuristicPhaseConfig) solverConfig.getPhaseConfigList().get(0);
        ChangeMoveSelectorConfig changeMoveSelectorConfig =
                (ChangeMoveSelectorConfig) constructionHeuristicPhaseConfig.getMoveSelectorConfigList().get(0);
        ValueSelectorConfig valueSelectorConfig = changeMoveSelectorConfig.getValueSelectorConfig();
        assertThat(valueSelectorConfig.getVariableName()).isNull();
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

    /* Dummy classes below are referenced from the testSolverConfig.xml used in this test case. */

    private static abstract class DummySolutionPartitioner implements SolutionPartitioner<TestdataSolution> {
    }

    private static abstract class DummyEasyScoreCalculator implements EasyScoreCalculator<TestdataSolution> {
    }

    private static abstract class DummyIncrementalScoreCalculator implements IncrementalScoreCalculator<TestdataSolution> {
    }

    private static abstract class DummyConstraintProvider implements ConstraintProvider {
    }

    private abstract class DummyValueFilter implements SelectionFilter<TestdataSolution, TestdataValue> {
    }

    private abstract class DummyEntityFilter implements SelectionFilter<TestdataSolution, TestdataEntity> {
    }

    private abstract class DummyChangeMoveFilter implements SelectionFilter<TestdataSolution, ChangeMove> {
    }

    private abstract class DummyMoveIteratorFactory implements MoveIteratorFactory<TestdataSolution> {
    }

    private abstract class DummyMoveListFactory implements MoveListFactory<TestdataSolution> {
    }
}
