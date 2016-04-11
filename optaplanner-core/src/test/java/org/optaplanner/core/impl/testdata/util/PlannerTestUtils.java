/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang3.SerializationUtils;
import org.mockito.AdditionalAnswers;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.definition.ScoreDefinitionType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.DummySimpleScoreEasyScoreCalculator;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirectorFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.mockito.Mockito.*;

/**
 * @see PlannerAssert
 */
public class PlannerTestUtils {

    // ************************************************************************
    // SolverFactory methods
    // ************************************************************************

    public static <Solution_> SolverFactory<Solution_> buildSolverFactory(
            Class<Solution_> solutionClass, Class<?>... entityClasses) {
        SolverFactory<Solution_> solverFactory = SolverFactory.createEmpty();
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        solverConfig.setSolutionClass(solutionClass);
        solverConfig.setEntityClassList(Arrays.asList(entityClasses));
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setScoreDefinitionType(ScoreDefinitionType.SIMPLE);
        scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(DummySimpleScoreEasyScoreCalculator.class);
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        List<PhaseConfig> phaseConfigList = new ArrayList<>(2);
        phaseConfigList.add(new ConstructionHeuristicPhaseConfig());
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setStepCountLimit(10);
        localSearchPhaseConfig.setTerminationConfig(terminationConfig);
        phaseConfigList.add(localSearchPhaseConfig);
        solverConfig.setPhaseConfigList(phaseConfigList);
        return solverFactory;
    }

    public static <Solution_> SolverFactory<Solution_> buildSolverFactoryWithDroolsScoreDirector(
            Class<Solution_> solutionClass, Class<?>... entityClasses) {
        SolverFactory<Solution_> solverFactory = buildSolverFactory(solutionClass, entityClasses);
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = solverConfig.getScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(null);
        scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(
                "org/optaplanner/core/impl/score/dummySimpleScoreDroolsScoreRules.drl"));
        return solverFactory;
    }

    // ************************************************************************
    // ScoreDirector methods
    // ************************************************************************

    public static <Solution_> InnerScoreDirector mockScoreDirector(SolutionDescriptor<Solution_> solutionDescriptor) {
        EasyScoreDirectorFactory<Solution_> scoreDirectorFactory =
                new EasyScoreDirectorFactory<>((EasyScoreCalculator<Solution_>) solution -> SimpleScore.valueOf(0));
        scoreDirectorFactory.setSolutionDescriptor(solutionDescriptor);
        scoreDirectorFactory.setScoreDefinition(new SimpleScoreDefinition());
        scoreDirectorFactory.setInitializingScoreTrend(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1));
        return mock(InnerScoreDirector.class, AdditionalAnswers.delegatesTo(scoreDirectorFactory.buildScoreDirector(false)));
    }

    // ************************************************************************
    // Serialization methods
    // ************************************************************************

    public static <T> void serializeAndDeserializeWithAll(T input, OutputAsserter<T> outputAsserter) {
        outputAsserter.assertOutput(serializeAndDeserializeWithJavaSerialization(input));
        outputAsserter.assertOutput(serializeAndDeserializeWithXStream(input));
    }

    public static <T> T serializeAndDeserializeWithJavaSerialization(T input) {
        byte[] bytes = SerializationUtils.serialize((Serializable) input);
        return (T) SerializationUtils.deserialize(bytes);
    }

    public static <T> T serializeAndDeserializeWithXStream(T input) {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        if (input != null) {
            xStream.processAnnotations(input.getClass());
        }
        String xmlString = xStream.toXML(input);
        return (T) xStream.fromXML(xmlString);
    }

    public static interface OutputAsserter<T> {

        void assertOutput(T output);

    }

    // ************************************************************************
    // Private constructor
    // ************************************************************************

    private PlannerTestUtils() {
    }

}
