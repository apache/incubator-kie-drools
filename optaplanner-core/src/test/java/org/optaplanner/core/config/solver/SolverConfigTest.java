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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicForagerConfig;
import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicPickEarlyType;
import org.optaplanner.core.config.constructionheuristic.placer.PooledEntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedEntityPlacerConfig;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchType;
import org.optaplanner.core.config.exhaustivesearch.NodeExplorationType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorType;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingType;
import org.optaplanner.core.config.localsearch.decider.forager.FinalistPodiumType;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchPickEarlyType;
import org.optaplanner.core.config.partitionedsearch.PartitionedSearchPhaseConfig;
import org.optaplanner.core.config.phase.NoChangePhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.termination.TerminationCompositionStyle;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.phase.custom.AbstractCustomPhaseCommand;
import org.optaplanner.core.impl.solver.io.XStreamConfigReader;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataAnnotatedExtendedEntity;

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
        SolverConfig jaxbSolverConfig = unmarshallSolverConfigFromResource(TEST_SOLVER_CONFIG);

        Writer stringWriter = new StringWriter();
        marshaller.marshal(jaxbSolverConfig, stringWriter);
        String jaxbString = stringWriter.toString();
        Reader stringReader = new StringReader(jaxbString);
        jaxbSolverConfig = (SolverConfig) unmarshaller.unmarshal(stringReader);

        Assertions.assertThat(jaxbSolverConfig).usingRecursiveComparison().isEqualTo(createSolverConfigViaApi());
    }

    private SolverConfig unmarshallSolverConfigFromResource(String solverConfigResource) {
        try (InputStream testSolverConfigStream = SolverConfigTest.class.getResourceAsStream(TEST_SOLVER_CONFIG)) {
            return (SolverConfig) unmarshaller.unmarshal(testSolverConfigStream);
        } catch (IOException | JAXBException exception) {
            throw new RuntimeException("Failed to read solver configuration resource " + solverConfigResource, exception);
        }
    }

    /**
     * Creates an equal solver configuration as {@link SolverConfigTest#TEST_SOLVER_CONFIG}.
     */
    private SolverConfig createSolverConfigViaApi() {
        ConstructionHeuristicForagerConfig constructionHeuristicForagerConfig = new ConstructionHeuristicForagerConfig();
        constructionHeuristicForagerConfig.setPickEarlyType(
                ConstructionHeuristicPickEarlyType.FIRST_FEASIBLE_SCORE_OR_NON_DETERIORATING_HARD);

        String placerEntitySelectorId = "placerEntitySelector";
        EntitySelectorConfig placerEntitySelectorConfig = new EntitySelectorConfig();
        placerEntitySelectorConfig.setId(placerEntitySelectorId);
        placerEntitySelectorConfig.setEntityClass(TestdataEntity.class);
        placerEntitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        placerEntitySelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        placerEntitySelectorConfig.setSorterManner(EntitySorterManner.DECREASING_DIFFICULTY);

        EntitySelectorConfig mimicPlacerEntitySelectorConfig = new EntitySelectorConfig();
        mimicPlacerEntitySelectorConfig.setMimicSelectorRef(placerEntitySelectorId);

        ValueSelectorConfig firstValueSelectorConfig = new ValueSelectorConfig();
        firstValueSelectorConfig.setVariableName("subValue");
        firstValueSelectorConfig.setDowncastEntityClass(TestdataAnnotatedExtendedEntity.class);
        firstValueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        firstValueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        firstValueSelectorConfig.setSorterManner(ValueSorterManner.INCREASING_STRENGTH);

        ChangeMoveSelectorConfig firstChangeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        firstChangeMoveSelectorConfig.setEntitySelectorConfig(mimicPlacerEntitySelectorConfig);
        firstChangeMoveSelectorConfig.setValueSelectorConfig(firstValueSelectorConfig);

        ValueSelectorConfig secondValueSelectorConfig = new ValueSelectorConfig();
        secondValueSelectorConfig.setVariableName("value");
        secondValueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        secondValueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        secondValueSelectorConfig.setSorterManner(ValueSorterManner.INCREASING_STRENGTH);

        ChangeMoveSelectorConfig secondChangeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        secondChangeMoveSelectorConfig.setEntitySelectorConfig(mimicPlacerEntitySelectorConfig);
        secondChangeMoveSelectorConfig.setValueSelectorConfig(secondValueSelectorConfig);

        CartesianProductMoveSelectorConfig cartesianProductMoveSelectorConfig = new CartesianProductMoveSelectorConfig();
        List<MoveSelectorConfig> moveSelectorConfigList = new ArrayList<>();
        moveSelectorConfigList.add(firstChangeMoveSelectorConfig);
        moveSelectorConfigList.add(secondChangeMoveSelectorConfig);
        cartesianProductMoveSelectorConfig.setMoveSelectorConfigList(moveSelectorConfigList);

        QueuedEntityPlacerConfig queuedEntityPlacerConfig = new QueuedEntityPlacerConfig();
        queuedEntityPlacerConfig.setEntitySelectorConfig(placerEntitySelectorConfig);
        queuedEntityPlacerConfig.setMoveSelectorConfigList(Collections.singletonList(cartesianProductMoveSelectorConfig));

        List<String> variableNameIncludeList = new ArrayList<>();
        variableNameIncludeList.add("variableA");
        variableNameIncludeList.add("variableB");

        SwapMoveSelectorConfig swapMoveSelectorConfig = new SwapMoveSelectorConfig();
        swapMoveSelectorConfig.setVariableNameIncludeList(variableNameIncludeList);

        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.withConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT_DECREASING)
                .withForagerConfig(constructionHeuristicForagerConfig)
                .withEntityPlacerConfig(queuedEntityPlacerConfig)
                .withMoveSelectorConfigList(Collections.singletonList(swapMoveSelectorConfig));

        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig().withCustomPhaseCommandClassList(
                Collections.singletonList(AbstractCustomPhaseCommand.class));

        ExhaustiveSearchPhaseConfig exhaustiveSearchPhaseConfig = new ExhaustiveSearchPhaseConfig();
        exhaustiveSearchPhaseConfig.setExhaustiveSearchType(ExhaustiveSearchType.BRANCH_AND_BOUND);
        exhaustiveSearchPhaseConfig.setNodeExplorationType(NodeExplorationType.BREADTH_FIRST);
        exhaustiveSearchPhaseConfig.setMoveSelectorConfig(new ChangeMoveSelectorConfig());

        List<MoveSelectorConfig> localSearchMoveSelectorConfigList = new ArrayList<>();
        localSearchMoveSelectorConfigList.add(new PillarChangeMoveSelectorConfig());
        localSearchMoveSelectorConfigList.add(new PillarSwapMoveSelectorConfig());

        UnionMoveSelectorConfig localSearchUnionMoveSelectorConfig = new UnionMoveSelectorConfig();
        localSearchUnionMoveSelectorConfig.setMoveSelectorConfigList(localSearchMoveSelectorConfigList);

        TerminationConfig innerTerminationConfig = new TerminationConfig()
                .withUnimprovedStepCountLimit(1000)
                .withSecondsSpentLimit(20L)
                .withTerminationCompositionStyle(TerminationCompositionStyle.OR);

        TerminationConfig localSearchTerminationConfig = new TerminationConfig()
                .withUnimprovedSecondsSpentLimit(10L)
                .withTerminationCompositionStyle(TerminationCompositionStyle.AND)
                .withTerminationConfigList(Collections.singletonList(innerTerminationConfig));

        List<AcceptorType> acceptorTypeList = new ArrayList<>();
        acceptorTypeList.add(AcceptorType.ENTITY_TABU);
        acceptorTypeList.add(AcceptorType.STEP_COUNTING_HILL_CLIMBING);

        LocalSearchAcceptorConfig localSearchAcceptorConfig = new LocalSearchAcceptorConfig()
                .withAcceptorTypeList(acceptorTypeList)
                .withEntityTabuRatio(2.0)
                .withStepCountingHillClimbingType(StepCountingHillClimbingType.EQUAL_OR_IMPROVING_STEP)
                .withStepCountingHillClimbingSize(10);

        LocalSearchForagerConfig localSearchForagerConfig = new LocalSearchForagerConfig()
                .withAcceptedCountLimit(1000)
                .withFinalistPodiumType(FinalistPodiumType.STRATEGIC_OSCILLATION)
                .withPickEarlyType(LocalSearchPickEarlyType.FIRST_LAST_STEP_SCORE_IMPROVING)
                .withBreakTieRandomly(true);

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig()
                .withLocalSearchType(LocalSearchType.TABU_SEARCH)
                .withAcceptorConfig(localSearchAcceptorConfig)
                .withForagerConfig(localSearchForagerConfig);
        localSearchPhaseConfig.setMoveSelectorConfig(localSearchUnionMoveSelectorConfig);
        localSearchPhaseConfig.setTerminationConfig(localSearchTerminationConfig);

        PooledEntityPlacerConfig pooledEntityPlacerConfig = new PooledEntityPlacerConfig();
        pooledEntityPlacerConfig.setMoveSelectorConfig(new ChangeMoveSelectorConfig());

        ConstructionHeuristicPhaseConfig innerConstructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig()
                .withEntityPlacerConfig(pooledEntityPlacerConfig);
        List<PhaseConfig> partitionedSearchPhaseConfigList = new ArrayList<>();
        partitionedSearchPhaseConfigList.add(innerConstructionHeuristicPhaseConfig);
        partitionedSearchPhaseConfigList.add(new LocalSearchPhaseConfig());

        Map<String, String> solutionPartitionerCustomProperties = new HashMap<>();
        solutionPartitionerCustomProperties.put("partCount", "4");
        solutionPartitionerCustomProperties.put("minimumProcessListSize", "300");

        PartitionedSearchPhaseConfig partitionedSearchPhaseConfig = new PartitionedSearchPhaseConfig();
        partitionedSearchPhaseConfig.setPhaseConfigList(partitionedSearchPhaseConfigList);
        partitionedSearchPhaseConfig.setSolutionPartitionerClass(DummySolutionPartitioner.class);
        partitionedSearchPhaseConfig.setSolutionPartitionerCustomProperties(solutionPartitionerCustomProperties);

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig()
                .withScoreDrls("org/optaplanner/config/first-non-existing-constraints.drl",
                        "org/optaplanner/config/second-non-existing-constraints.drl")
                .withInitializingScoreTrend("ONLY_DOWN");

        SolverConfig solverConfig = new SolverConfig()
                .withPhases(constructionHeuristicPhaseConfig, customPhaseConfig, exhaustiveSearchPhaseConfig,
                        localSearchPhaseConfig, new NoChangePhaseConfig(), partitionedSearchPhaseConfig)
                .withEnvironmentMode(EnvironmentMode.FULL_ASSERT)
                .withSolutionClass(TestdataSolution.class)
                .withEntityClasses(TestdataEntity.class)
                .withScoreDirectorFactory(scoreDirectorFactoryConfig);

        return solverConfig;
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

    private static class DummySolutionPartitioner implements SolutionPartitioner<TestdataSolution> {

        @Override
        public List<TestdataSolution> splitWorkingSolution(ScoreDirector<TestdataSolution> scoreDirector,
                Integer runnablePartThreadLimit) {
            return Collections.emptyList(); // noop
        }
    }
}
