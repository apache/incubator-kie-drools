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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.io.jaxb.JaxbIO;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class SolverConfigTest {
    private static final String TEST_SOLVER_CONFIG = "testSolverConfig.xml";

    private final JaxbIO<SolverConfig> xmlIO = new JaxbIO<>(SolverConfig.class);

    @Test
    public void xmlConfigFileRemainsSameAfterReadWrite() throws IOException {
        SolverConfig jaxbSolverConfig = unmarshallSolverConfigFromResource(TEST_SOLVER_CONFIG);

        Writer stringWriter = new StringWriter();
        xmlIO.write(jaxbSolverConfig, stringWriter);
        String jaxbString = stringWriter.toString();

        String originalXml = IOUtils.toString(
                SolverConfigTest.class.getResourceAsStream(TEST_SOLVER_CONFIG), StandardCharsets.UTF_8);

        assertThat(jaxbString.trim()).isEqualToNormalizingNewlines(originalXml.trim());
    }

    private SolverConfig unmarshallSolverConfigFromResource(String solverConfigResource) {
        try (Reader reader = new InputStreamReader(SolverConfigTest.class.getResourceAsStream(solverConfigResource))) {
            return xmlIO.read(reader);
        } catch (IOException ioException) {
            throw new RuntimeException("Failed to read solver configuration resource " + solverConfigResource, ioException);
        }
    }

    @Test
    public void whiteCharsInClassName() {
        String solutionClassName = "org.optaplanner.core.impl.testdata.domain.TestdataSolution";
        String xmlFragment = String.format("<solver>%n"
                + "  <solutionClass>  %s  %n" // Intentionally included white chars around the class name.
                + "  </solutionClass>%n"
                + "</solver>", solutionClassName);
        SolverConfig solverConfig = xmlIO.read(new StringReader(xmlFragment));
        assertThat(solverConfig.getSolutionClass().getName()).isEqualTo(solutionClassName);
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
        SolverConfig solverConfig = xmlIO.read(new StringReader(xmlFragment));

        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig =
                (ConstructionHeuristicPhaseConfig) solverConfig.getPhaseConfigList().get(0);
        ChangeMoveSelectorConfig changeMoveSelectorConfig =
                (ChangeMoveSelectorConfig) constructionHeuristicPhaseConfig.getMoveSelectorConfigList().get(0);
        ValueSelectorConfig valueSelectorConfig = changeMoveSelectorConfig.getValueSelectorConfig();
        assertThat(valueSelectorConfig.getVariableName()).isNull();
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
