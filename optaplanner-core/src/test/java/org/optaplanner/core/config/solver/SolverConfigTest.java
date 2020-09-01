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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.io.OptaPlannerXmlSerializationException;
import org.optaplanner.core.impl.io.jaxb.GenericJaxbIO;
import org.optaplanner.core.impl.io.jaxb.SolverConfigIO;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

class SolverConfigTest {

    private static final String TEST_SOLVER_CONFIG_WITH_NAMESPACE = "testSolverConfigWithNamespace.xml";
    private static final String TEST_SOLVER_CONFIG_WITHOUT_NAMESPACE = "testSolverConfigWithoutNamespace.xml";
    private static final String SOLVER_XSD = "/solver.xsd";
    private final SolverConfigIO solverConfigIO = new SolverConfigIO();

    @Test
    void xmlConfigRemainsSameAfterReadWrite() throws IOException {
        SolverConfig jaxbSolverConfig =
                readSolverConfig(TEST_SOLVER_CONFIG_WITHOUT_NAMESPACE, (reader) -> solverConfigIO.read(reader));

        Writer stringWriter = new StringWriter();
        solverConfigIO.write(jaxbSolverConfig, stringWriter);
        String jaxbString = stringWriter.toString();

        String originalXml = IOUtils.toString(
                SolverConfigTest.class.getResourceAsStream(TEST_SOLVER_CONFIG_WITHOUT_NAMESPACE), StandardCharsets.UTF_8);

        assertThat(jaxbString.trim()).isXmlEqualTo(originalXml.trim());
    }

    @Test
    void readXmlConfigWithNamespace() throws IOException {
        SolverConfig solverConfig =
                readSolverConfig(TEST_SOLVER_CONFIG_WITH_NAMESPACE, (reader) -> solverConfigIO.read(reader));

        assertThat(solverConfig).isNotNull();
        assertThat(solverConfig.getPhaseConfigList())
                .hasSize(2)
                .hasOnlyElementsOfTypes(ConstructionHeuristicPhaseConfig.class, LocalSearchPhaseConfig.class);
        assertThat(solverConfig.getEnvironmentMode()).isEqualTo(EnvironmentMode.FULL_ASSERT);
        assertThat(solverConfig.getSolutionClass()).isAssignableFrom(TestdataSolution.class);
        assertThat(solverConfig.getScoreDirectorFactoryConfig().getConstraintProviderClass())
                .isAssignableFrom(DummyConstraintProvider.class);
    }

    private SolverConfig readSolverConfig(String solverConfigResource, Function<Reader, SolverConfig> solverConfigReader)
            throws IOException {
        try (Reader reader = new InputStreamReader(SolverConfigTest.class.getResourceAsStream(solverConfigResource))) {
            return solverConfigReader.apply(reader);
        }
    }

    @Test
    void whiteCharsInClassName() {
        String solutionClassName = "org.optaplanner.core.impl.testdata.domain.TestdataSolution";
        String xmlFragment = String.format("<solver xmlns=\"https://www.optaplanner.org/xsd/solver\">%n"
                + "  <solutionClass>  %s  %n" // Intentionally included white chars around the class name.
                + "  </solutionClass>%n"
                + "</solver>", solutionClassName);
        SolverConfig solverConfig = solverConfigIO.read(new StringReader(xmlFragment));
        assertThat(solverConfig.getSolutionClass().getName()).isEqualTo(solutionClassName);
    }

    @Test
    void readAndValidateInvalidSolverConfig_failsIndicatingTheIssue() {
        String solverConfigXml = "<solver xmlns=\"https://www.optaplanner.org/xsd/solver\">\n"
                + "  <constructionHeuristic>\n"
                + "      <changeMoveSelector>\n"
                + "        <valueSelector>\n"
                // Intentionally wrong: variableName should be an attribute of the <valueSelector/>
                + "          <variableName>subValue</variableName>\n"
                + "        </valueSelector>\n"
                + "      </changeMoveSelector>\n"
                + "  </constructionHeuristic>\n"
                + "</solver>";

        GenericJaxbIO<SolverConfig> genericJaxbIO = new GenericJaxbIO<>(SolverConfig.class);
        StringReader stringReader = new StringReader(solverConfigXml);
        assertThatExceptionOfType(OptaPlannerXmlSerializationException.class)
                .isThrownBy(
                        () -> genericJaxbIO.readAndValidate(stringReader, SOLVER_XSD))
                .withMessageContaining("Invalid content was found")
                .withMessageContaining("variableName");
    }

    @Test
    void readAndValidateSolverConfig() throws IOException {
        GenericJaxbIO<SolverConfig> genericJaxbIO = new GenericJaxbIO<>(SolverConfig.class);
        SolverConfig solverConfig =
                readSolverConfig(TEST_SOLVER_CONFIG_WITH_NAMESPACE,
                        (reader -> genericJaxbIO.readAndValidate(reader, SOLVER_XSD)));
        assertThat(solverConfig).isNotNull();
    }

    @Test
    void inherit() throws IOException {
        SolverConfig originalSolverConfig =
                readSolverConfig(TEST_SOLVER_CONFIG_WITHOUT_NAMESPACE, (reader) -> solverConfigIO.read(reader));
        SolverConfig inheritedSolverConfig = new SolverConfig().inherit(originalSolverConfig);
        assertThat(inheritedSolverConfig).usingRecursiveComparison().isEqualTo(originalSolverConfig);
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
