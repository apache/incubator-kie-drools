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

package org.optaplanner.core.impl.exhaustivesearch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchType;
import org.optaplanner.core.config.exhaustivesearch.NodeExplorationType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.comparable.TestdataDifficultyComparingEntity;
import org.optaplanner.core.impl.testdata.domain.comparable.TestdataDifficultyComparingSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

/**
 * The test runs through all available configuration combinations related to ExhaustiveSearch and compares the results
 * with manually calculated data. It tries to find the best solution for 3 values and 4 entities. When the same value
 * is held by different entities, the score is reduced by 1.
 * <p>
 * A solution state is represented by a string containing 4 characters representing entity values. Uninitialized
 * entities are marked by '-' character. (e.g. "1-21" means that the first and the fourth entity have value 1,
 * the second entity doesn't have a value and the third entity has 2, the score is -1) The score is reduced for
 * every duplicate present. (meaning a solution of 1111 has the score of -4)
 */
public class BlackBoxExhaustiveSearchPhaseTest {

    /**
     * Initialize combination of input parameters.
     *
     * @return collection of combination of input parameters
     */
    public static Collection<Object[]> params() {
        return Stream.concat(getBranchAndBoundConfigs(), getBruteForceConfigs()).collect(Collectors.toList());
    }

    private static Stream<Object[]> getBranchAndBoundConfigs() {
        return Stream.of(getBranchAndBoundDepthFirstConfigs(),
                getBranchAndBoundBreadthFirstConfigs(),
                getBranchAndBoundScoreFirstConfigs(),
                getBranchAndBoundOptimisticBoundFirstConfigs(),
                getBranchAndBoundOriginalOrderConfigs())
                .flatMap(i -> i);
    }

    private static Stream<Object[]> getBranchAndBoundDepthFirstConfigs() {
        return Stream.of(
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.DEPTH_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "13--", "132-", "13-2", "12--", "123-", "12-3", "1-3-", "123-", "1321")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.DEPTH_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "32--", "321-", "32-1", "31--", "312-", "31-2", "3-2-", "312-", "3213")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.DEPTH_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "12--", "123-", "12-3", "13--", "132-", "13-2", "1-2-", "132-", "1231")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.DEPTH_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "1-3-", "123-", "1-32", "1-2-", "132-", "1-23", "13--", "132-", "1231")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.DEPTH_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "3-2-", "312-", "3-21", "3-1-", "321-", "3-12", "32--", "321-", "3123")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.DEPTH_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "1-2-", "132-", "1-23", "1-3-", "123-", "1-32", "12--", "123-", "1321")));
    }

    private static Stream<Object[]> getBranchAndBoundBreadthFirstConfigs() {
        return Stream.of(
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.BREADTH_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "3---", "2---", "-1--", "-3--", "-2--", "--1-", "--3-", "--2-", "----")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.BREADTH_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "2---", "1---", "-3--", "-2--", "-1--", "--3-", "--2-", "--1-", "----")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.BREADTH_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "2---", "3---", "-1--", "-2--", "-3--", "--1-", "--2-", "--3-", "----")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.BREADTH_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "3---", "2---", "--1-", "--3-", "--2-", "-1--", "-3--", "-2--", "----")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.BREADTH_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "2---", "1---", "--3-", "--2-", "--1-", "-3--", "-2--", "-1--", "----")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.BREADTH_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "2---", "3---", "--1-", "--2-", "--3-", "-1--", "-2--", "-3--", "----")));
    }

    private static Stream<Object[]> getBranchAndBoundScoreFirstConfigs() {
        return Stream.of(
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.SCORE_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "13--", "132-", "13-2", "12--", "123-", "12-3", "1-3-", "123-", "1321")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.SCORE_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "32--", "321-", "32-1", "31--", "312-", "31-2", "3-2-", "312-", "3213")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.SCORE_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "12--", "123-", "12-3", "13--", "132-", "13-2", "1-2-", "132-", "1231")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.SCORE_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "1-3-", "123-", "1-32", "1-2-", "132-", "1-23", "13--", "132-", "1231")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.SCORE_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "3-2-", "312-", "3-21", "3-1-", "321-", "3-12", "32--", "321-", "3123")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.SCORE_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "1-2-", "132-", "1-23", "1-3-", "123-", "1-32", "12--", "123-", "1321")));
    }

    private static Stream<Object[]> getBranchAndBoundOptimisticBoundFirstConfigs() {
        return Stream.of(
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.OPTIMISTIC_BOUND_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "13--", "132-", "13-2", "12--", "123-", "12-3", "1-3-", "123-", "1321")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.OPTIMISTIC_BOUND_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "32--", "321-", "32-1", "31--", "312-", "31-2", "3-2-", "312-", "3213")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.OPTIMISTIC_BOUND_FIRST,
                        EntitySorterManner.NONE,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "12--", "123-", "12-3", "13--", "132-", "13-2", "1-2-", "132-", "1231")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.OPTIMISTIC_BOUND_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "1-3-", "123-", "1-32", "1-2-", "132-", "1-23", "13--", "132-", "1231")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.OPTIMISTIC_BOUND_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "3-2-", "312-", "3-21", "3-1-", "321-", "3-12", "32--", "321-", "3123")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.OPTIMISTIC_BOUND_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "1-2-", "132-", "1-23", "1-3-", "123-", "1-32", "12--", "123-", "1321")));
    }

    private static Stream<Object[]> getBranchAndBoundOriginalOrderConfigs() {
        return Stream.of(
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.ORIGINAL_ORDER,
                        EntitySorterManner.NONE,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "11--", "111-", "113-", "13--", "132-", "13-2", "12--", "123-", "1132")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.ORIGINAL_ORDER,
                        EntitySorterManner.NONE,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "33--", "333-", "332-", "32--", "321-", "32-1", "31--", "312-", "3321")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.ORIGINAL_ORDER,
                        EntitySorterManner.NONE,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "11--", "111-", "112-", "12--", "123-", "12-3", "13--", "132-", "1123")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.ORIGINAL_ORDER,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "1-1-", "111-", "131-", "1-3-", "123-", "1-32", "1-2-", "132-", "1312")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.ORIGINAL_ORDER,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "3-3-", "333-", "323-", "3-2-", "312-", "3-21", "3-1-", "321-", "3231")),
                toObjectArray(
                        ExhaustiveSearchType.BRANCH_AND_BOUND,
                        NodeExplorationType.ORIGINAL_ORDER,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "1-1-", "111-", "121-", "1-2-", "132-", "1-23", "1-3-", "123-", "1213")));
    }

    private static Stream<Object[]> getBruteForceConfigs() {
        return Stream.concat(getBruteForceLegalConfigs(), getBruteForceIllegalConfigs());
    }

    private static Stream<Object[]> getBruteForceLegalConfigs() {
        return Stream.of(
                toObjectArray(
                        ExhaustiveSearchType.BRUTE_FORCE,
                        null,
                        EntitySorterManner.NONE,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "11--", "111-", "113-", "112-", "11-1", "11-3", "11-2", "13--", "1132")),
                toObjectArray(
                        ExhaustiveSearchType.BRUTE_FORCE,
                        null,
                        EntitySorterManner.NONE,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "33--", "333-", "332-", "331-", "33-3", "33-2", "33-1", "32--", "3321")),
                toObjectArray(
                        ExhaustiveSearchType.BRUTE_FORCE,
                        null,
                        EntitySorterManner.NONE,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "11--", "111-", "112-", "113-", "11-1", "11-2", "11-3", "12--", "1123")),
                toObjectArray(
                        ExhaustiveSearchType.BRUTE_FORCE,
                        null,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.NONE,
                        Arrays.asList("----", "1---", "1-1-", "111-", "131-", "121-", "1-11", "1-13", "1-12", "1-3-", "1312")),
                toObjectArray(
                        ExhaustiveSearchType.BRUTE_FORCE,
                        null,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.DECREASING_STRENGTH,
                        Arrays.asList("----", "3---", "3-3-", "333-", "323-", "313-", "3-33", "3-32", "3-31", "3-2-", "3231")),
                toObjectArray(
                        ExhaustiveSearchType.BRUTE_FORCE,
                        null,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.INCREASING_STRENGTH,
                        Arrays.asList("----", "1---", "1-1-", "111-", "121-", "131-", "1-11", "1-12", "1-13", "1-2-", "1213")));
    }

    private static Stream<Object[]> getBruteForceIllegalConfigs() {
        return Stream.of(
                toObjectArray(
                        ExhaustiveSearchType.BRUTE_FORCE,
                        NodeExplorationType.DEPTH_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.DECREASING_STRENGTH,
                        null),
                toObjectArray(
                        ExhaustiveSearchType.BRUTE_FORCE,
                        NodeExplorationType.BREADTH_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.DECREASING_STRENGTH,
                        null),
                toObjectArray(
                        ExhaustiveSearchType.BRUTE_FORCE,
                        NodeExplorationType.SCORE_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.DECREASING_STRENGTH,
                        null),
                toObjectArray(
                        ExhaustiveSearchType.BRUTE_FORCE,
                        NodeExplorationType.OPTIMISTIC_BOUND_FIRST,
                        EntitySorterManner.DECREASING_DIFFICULTY,
                        ValueSorterManner.DECREASING_STRENGTH,
                        null));
    }

    private static Object[] toObjectArray(Object... parameters) {
        return parameters;
    }

    private static SolverConfig buildSolverConfig(
            EntitySorterManner entitySorterManner,
            ValueSorterManner valueSorterManner,
            ExhaustiveSearchType exhaustiveSearchType,
            NodeExplorationType nodeExplorationType) {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataDifficultyComparingSolution.class, TestdataDifficultyComparingEntity.class);

        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSorterManner(entitySorterManner);

        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        valueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        valueSelectorConfig.setSorterManner(valueSorterManner);

        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig();
        moveSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);
        moveSelectorConfig.setValueSelectorConfig(valueSelectorConfig);

        ExhaustiveSearchPhaseConfig exhaustiveSearchPhaseConfig = new ExhaustiveSearchPhaseConfig();
        exhaustiveSearchPhaseConfig.setExhaustiveSearchType(exhaustiveSearchType);
        exhaustiveSearchPhaseConfig.setNodeExplorationType(nodeExplorationType);
        exhaustiveSearchPhaseConfig.setMoveSelectorConfig(moveSelectorConfig);
        exhaustiveSearchPhaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(10));

        solverConfig.setPhaseConfigList(Collections.singletonList(exhaustiveSearchPhaseConfig));
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig()
                .withEasyScoreCalculatorClass(TestdataComparableDifferentValuesCalculator.class)
                .withInitializingScoreTrend("ONLY_DOWN"));

        return solverConfig;
    }

    private static TestdataDifficultyComparingSolution buildSolution() {
        TestdataDifficultyComparingSolution solution = new TestdataDifficultyComparingSolution("solution");
        // Intentionally not sorted, the string is used for sorting in cases it applies.
        solution.setEntityList(Arrays.asList(new TestdataDifficultyComparingEntity("entity4"),
                new TestdataDifficultyComparingEntity("entity2"),
                new TestdataDifficultyComparingEntity("entity3"),
                new TestdataDifficultyComparingEntity("entity1")));
        solution.setValueList(Arrays.asList(new TestdataValue("1"),
                new TestdataValue("3"),
                new TestdataValue("2")));
        return solution;
    }

    @ParameterizedTest(name = "{0}, NodeExplorationType-{1}, EntitySorterManner-{2}, ValueSorterManner-{3}")
    @MethodSource("params")
    public void verifyExhaustiveSearchSteps(
            ExhaustiveSearchType exhaustiveSearchType,
            NodeExplorationType nodeExplorationType,
            EntitySorterManner entitySorterManner,
            ValueSorterManner valueSorterManner,
            List<String> steps) {
        SolverConfig solverConfig = buildSolverConfig(
                entitySorterManner,
                valueSorterManner,
                exhaustiveSearchType,
                nodeExplorationType);
        SolverFactory<TestdataDifficultyComparingSolution> solverFactory = SolverFactory.create(solverConfig);

        if (exhaustiveSearchType == ExhaustiveSearchType.BRUTE_FORCE && nodeExplorationType != null) {
            Assertions.assertThatIllegalArgumentException()
                    .isThrownBy(solverFactory::buildSolver)
                    .withMessage("The phaseConfig (ExhaustiveSearchPhaseConfig) has an "
                            + "nodeExplorationType (" + nodeExplorationType.name()
                            + ") which is not compatible with its exhaustiveSearchType (BRUTE_FORCE).");
        } else {
            Solver<TestdataDifficultyComparingSolution> solver = solverFactory.buildSolver();

            TestdataSolutionStateRecorder listener = new TestdataSolutionStateRecorder();
            ((DefaultSolver<TestdataDifficultyComparingSolution>) solver).addPhaseLifecycleListener(listener);

            solver.solve(buildSolution());

            assertThat(listener.getWorkingSolutions()).containsExactlyElementsOf(steps);
        }
    }

    /**
     * This class calculates the score of a solution by penalizing repeated value occurrences held by entities.
     */
    public static class TestdataComparableDifferentValuesCalculator
            implements EasyScoreCalculator<TestdataDifficultyComparingSolution> {

        @Override
        public SimpleScore calculateScore(TestdataDifficultyComparingSolution solution) {
            int score = 0;
            Set<TestdataValue> alreadyUsedValues = new HashSet<>();

            for (TestdataDifficultyComparingEntity entity : solution.getEntityList()) {
                if (entity.getValue() == null) {
                    continue;
                }
                TestdataValue value = entity.getValue();
                if (alreadyUsedValues.contains(value)) {
                    score -= 1;
                } else {
                    alreadyUsedValues.add(value);
                }
            }
            return SimpleScore.of(score);
        }
    }

    public static class TestdataSolutionStateRecorder
            extends PhaseLifecycleListenerAdapter<TestdataDifficultyComparingSolution> {

        private final List<String> workingSolutions = new ArrayList<>();

        @Override
        public void stepEnded(AbstractStepScope<TestdataDifficultyComparingSolution> abstractStepScope) {
            addWorkingSolution(abstractStepScope.getWorkingSolution());
        }

        @Override
        public void solvingEnded(SolverScope<TestdataDifficultyComparingSolution> solverScope) {
            addWorkingSolution(solverScope.getBestSolution());
        }

        private void addWorkingSolution(TestdataDifficultyComparingSolution solution) {
            workingSolutions.add(solution.getEntityList().stream()
                    .map(TestdataDifficultyComparingEntity::getValue)
                    .map(value -> value == null ? "-" : value.getCode())
                    .collect(Collectors.joining()));
        }

        public List<String> getWorkingSolutions() {
            return workingSolutions;
        }
    }
}
