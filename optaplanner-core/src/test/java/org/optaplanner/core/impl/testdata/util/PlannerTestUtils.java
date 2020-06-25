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

package org.optaplanner.core.impl.testdata.util;

import static java.util.Arrays.stream;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.mockito.AdditionalAnswers;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.DummySimpleScoreEasyScoreCalculator;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirectorFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * @see PlannerAssert
 */
public class PlannerTestUtils {

    public static final int TERMINATION_STEP_COUNT_LIMIT = 10;

    // ************************************************************************
    // SolverFactory methods
    // ************************************************************************

    public static <Solution_> SolverFactory<Solution_> buildSolverFactory(
            Class<Solution_> solutionClass, Class<?>... entityClasses) {
        SolverConfig solverConfig = buildSolverConfig(solutionClass, entityClasses);
        return SolverFactory.create(solverConfig);
    }

    public static <Solution_> SolverConfig buildSolverConfig(
            Class<Solution_> solutionClass, Class<?>... entityClasses) {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setSolutionClass(solutionClass);
        solverConfig.setEntityClassList(Arrays.asList(entityClasses));
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(DummySimpleScoreEasyScoreCalculator.class);
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        List<PhaseConfig> phaseConfigList = new ArrayList<>(2);
        phaseConfigList.add(new ConstructionHeuristicPhaseConfig());
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(TERMINATION_STEP_COUNT_LIMIT));
        phaseConfigList.add(localSearchPhaseConfig);
        solverConfig.setPhaseConfigList(phaseConfigList);
        return solverConfig;
    }

    public static <Solution_> SolverFactory<Solution_> buildSolverFactoryWithDroolsScoreDirector(
            Class<Solution_> solutionClass, Class<?>... entityClasses) {
        SolverConfig solverConfig = buildSolverConfig(solutionClass, entityClasses);
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = solverConfig.getScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(null);
        scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(
                "org/optaplanner/core/impl/score/dummySimpleScoreDroolsConstraints.drl"));
        return SolverFactory.create(solverConfig);
    }

    public static <Solution_> Solution_ solve(SolverConfig solverConfig, Solution_ problem) {
        SolverFactory<Solution_> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver().solve(problem);
    }

    // ************************************************************************
    // Testdata methods
    // ************************************************************************

    public static TestdataSolution generateTestdataSolution(String code) {
        return generateTestdataSolution(code, 2);
    }

    public static TestdataSolution generateTestdataSolution(String code, int entityAndValueCount) {
        TestdataSolution solution = new TestdataSolution(code);
        solution.setValueList(IntStream.range(1, entityAndValueCount + 1)
                .mapToObj(i -> new TestdataValue("v" + i))
                .collect(Collectors.toList()));
        solution.setEntityList(IntStream.range(1, entityAndValueCount + 1)
                .mapToObj(i -> new TestdataEntity("e" + i))
                .collect(Collectors.toList()));
        return solution;
    }

    // ************************************************************************
    // ScoreDirector methods
    // ************************************************************************

    public static <Solution_> InnerScoreDirector<Solution_> mockScoreDirector(
            SolutionDescriptor<Solution_> solutionDescriptor) {
        EasyScoreDirectorFactory<Solution_> scoreDirectorFactory = new EasyScoreDirectorFactory<>(solutionDescriptor,
                (solution_) -> SimpleScore.of(0));
        scoreDirectorFactory.setInitializingScoreTrend(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1));
        return mock(InnerScoreDirector.class,
                AdditionalAnswers.delegatesTo(scoreDirectorFactory.buildScoreDirector(false, false)));
    }

    public static <Solution_> InnerScoreDirector<Solution_> mockRebasingScoreDirector(
            SolutionDescriptor<Solution_> solutionDescriptor, Object[][] lookUpMappings) {
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        when(scoreDirector.lookUpWorkingObject(any())).thenAnswer((invocation) -> {
            Object externalObject = invocation.getArguments()[0];
            if (externalObject == null) {
                return null;
            }
            for (Object[] lookUpMapping : lookUpMappings) {
                if (externalObject == lookUpMapping[0]) {
                    return lookUpMapping[1];
                }
            }
            throw new IllegalStateException("No method mocked for parameter (" + externalObject + ").");
        });
        return scoreDirector;
    }

    // ************************************************************************
    // Serialization methods
    // ************************************************************************

    public static <T> void serializeAndDeserializeWithXStream(T input, Consumer<T> outputAsserter) {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        if (input != null) {
            xStream.processAnnotations(input.getClass());
        }
        XStream.setupDefaultSecurity(xStream);
        xStream.addPermission(new AnyTypePermission());
        String xmlString = xStream.toXML(input);
        outputAsserter.accept((T) xStream.fromXML(xmlString));
    }

    // ************************************************************************
    // Collection helpers
    // ************************************************************************

    @SafeVarargs
    public static <X> Set<X> asSet(X... items) {
        return stream(items).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @SafeVarargs
    public static <X> SortedSet<X> asSortedSet(X... items) {
        return stream(items).collect(Collectors.toCollection(TreeSet::new));
    }

    public static <X, Y> Map<X, Y> asMap(X x1, Y y1) {
        Map<X, Y> result = new LinkedHashMap<>(0);
        result.put(x1, y1);
        return result;
    }

    public static <X, Y> Map<X, Y> asMap(X x1, Y y1, X x2, Y y2) {
        Map<X, Y> result = asMap(x1, y1);
        result.put(x2, y2);
        return result;
    }

    public static <X extends Comparable<X>, Y> SortedMap<X, Y> asSortedMap(X x1, Y y1) {
        SortedMap<X, Y> result = new TreeMap<>();
        result.put(x1, y1);
        return result;
    }

    public static <X extends Comparable<X>, Y> SortedMap<X, Y> asSortedMap(X x1, Y y1, X x2, Y y2) {
        SortedMap<X, Y> result = asSortedMap(x1, y1);
        result.put(x2, y2);
        return result;
    }

    // ************************************************************************
    // Private constructor
    // ************************************************************************

    private PlannerTestUtils() {
    }

}
